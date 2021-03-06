package eu.trentorise.smartcampus.openservices.managers;

import it.smartcommunitylab.openservices.usdl.ExtWeliveCore;
import it.smartcommunitylab.openservices.usdl.Foaf;
import it.smartcommunitylab.openservices.usdl.Tags;
import it.smartcommunitylab.openservices.usdl.WeliveCore;
import it.smartcommunitylab.openservices.usdl.WeliveSecurity;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

import eu.trentorise.smartcampus.openservices.AuthProtocol;
import eu.trentorise.smartcampus.openservices.ServiceState;
import eu.trentorise.smartcampus.openservices.entities.Organization;
import eu.trentorise.smartcampus.openservices.entities.Service;
import eu.trentorise.smartcampus.openservices.entities.ServiceHistory;
import eu.trentorise.smartcampus.openservices.entities.Tag;

@Component
public class USDLGenerator {
	private static final Logger logger = LoggerFactory
			.getLogger(WADLGenerator.class);
	@Autowired
	private ServiceManager serviceManager;
	@Autowired
	private OrganizationManager orgManager;

	@Autowired
	Environment env;

	private static final String OS_NS = "http://platform.smartcommunitylab.it/openservice/services#";
	private static final String SCHEMA_NS = "http://schema.org/";

	private static final Map<String, String> welivePrefixes = new HashMap<String, String>();

	// extra tags

	private static final Property schemaUrlProp = ModelFactory
			.createDefaultModel().createProperty(SCHEMA_NS + "url");
	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

	@PostConstruct
	@SuppressWarnings("unused")
	private void init() {
		welivePrefixes.put("welive-core", WeliveCore.NS);
		welivePrefixes.put("dc", DCTerms.NS);
		welivePrefixes.put("foaf", Foaf.NS);
		welivePrefixes.put("welive-sec", WeliveSecurity.NS);
		welivePrefixes.put("tags", Tags.NS);
		welivePrefixes.put("schema", SCHEMA_NS);
	}

	public String generate(int serviceId) {
		// create missing resources (described in rdf schema as Description tag)
		Service s = serviceManager.getServiceById(serviceId);
		List<ServiceHistory> history = serviceManager.getServiceHistoryByServiceId(serviceId);
		Date creation = new Date();
		if (history != null) {
			String op = ServiceState.PUBLISH.toString();
			for (ServiceHistory h : history) {
				if (op.equalsIgnoreCase(h.getOperation())) {
					creation = h.getDate();
					break;
				}
			}
		}
		
		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefixes(welivePrefixes);
		Resource buildingBlock = m.createResource(OS_NS
				+ s.getName().replaceAll(" ", "-"), WeliveCore.BuildingBlock);
		buildingBlock.addProperty(DCTerms.title, s.getName());
		buildingBlock.addProperty(DCTerms.description, s.getDescription());
		buildingBlock.addProperty(DCTerms.abstract_, s.getDescription());
		buildingBlock.addProperty(WeliveCore.pilot, "Trento");
		buildingBlock.addProperty(DCTerms.created, DATE_FORMATTER.format(creation));
//		buildingBlock.addProperty(WeliveCore.type, ExtWeliveCore.WebService);
		buildingBlock.addProperty(DCTerms.type, ExtWeliveCore.WebService);
		
		// res.addProperty(DCTerms.created)
		buildingBlock.addProperty(Foaf.page, env.getProperty("application.url")
				+ "services/" + s.getId());

		// tags
		for (Tag t : s.getTags()) {
			buildingBlock.addProperty(Tags.tag, t.getName());
		}

		// owner
		String[] owners = s.getOwner() != null ? s.getOwner().split(",")
				: new String[0];
		String[] ownerUrls = s.getOwnerUrl() != null ? s.getOwnerUrl().split(",") : new String[0];
		for (int i = 0; i < owners.length; i++) {
			String o = owners[i];
			if (!StringUtils.isBlank(o)) {
				Resource r = m.createResource(
						buildingBlock.getURI() + o.trim().replaceAll(" ", "-"),
						WeliveCore.Entity).addProperty(DCTerms.title, o.trim());
				if (i < ownerUrls.length) {
					r.addProperty(Foaf.page, ownerUrls[i]);
				}
				buildingBlock.addProperty(WeliveCore.hasBusinessRole, r);
			}
		}

		// publisher
		Organization org = orgManager.getOrganizationById(s.getOrganizationId());
		if (org != null) {
			Resource r = m.createResource(
					buildingBlock.getURI() + org.getName().trim().replaceAll(" ", "-"),
					WeliveCore.Entity).addProperty(DCTerms.title, org.getName().trim());
			r.addProperty(WeliveCore.businessRole, ExtWeliveCore.Provider);
			if (org.getContacts() != null && org.getContacts().containsKey("web")) {
				r.addProperty(Foaf.page, org.getContacts().get("web"));
			}
			buildingBlock.addProperty(WeliveCore.hasBusinessRole, r);
		}

		// license
		if (!StringUtils.isBlank(s.getLicense())) {
			Resource r = m.createResource(buildingBlock.getURI() + "license",
					WeliveCore.StandardLicense).addProperty(DCTerms.title,
					s.getLicense());
			buildingBlock.addProperty(WeliveCore.hasLegalCondition, r);
		}

		// endpoint
		if (s.getAccessInformation() != null) {
			if (s.getAccessInformation().getProtocols() != null) {
				boolean isRest = s.getAccessInformation().supportRESTProtocol();

				Resource protocol = isRest ? WeliveCore.RESTWebServiceIP
						: WeliveCore.SOAPWebServiceIP;
				Resource r = m.createResource(
						buildingBlock.getURI() + "endpoint", protocol)
						.addProperty(schemaUrlProp,
								s.getAccessInformation().getEndpoint());
				if (isRest) {
					r.addProperty(WeliveCore.wadl,
							env.getProperty("application.url")
									+ "service/public/" + s.getId()
									+ "/spec/xwadl");
					r.addProperty(DCTerms.title, s.getName());
				}
				buildingBlock.addProperty(WeliveCore.hasInteractionPoint, r);
			}

			// authentication protocol
			if (s.getAccessInformation().getAuthentication() != null
					&& !s.getAccessInformation().getAuthentication()
							.getAccessProtocol()
							.equalsIgnoreCase(AuthProtocol.Public.toString())) {
				Resource r = m.createResource(buildingBlock.getURI()
						+ "Communication", WeliveSecurity.CommunicationMeasure);
				r.addProperty(WeliveSecurity.protocol, s.getAccessInformation()
						.getAuthentication().getAccessProtocol());
				buildingBlock.addProperty(WeliveSecurity.hasSecurityMeasure, r);
			}
		}

		StringWriter sw = new StringWriter();
		m.write(sw);
		return sw.toString();
	}
}
