package org.hl7.fhir.r4.utils.transform;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.conformance.ProfileUtilities;
import org.hl7.fhir.r4.hapi.ctx.DefaultProfileValidationSupport;
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext;
import org.hl7.fhir.r4.hapi.ctx.PrePopulatedValidationSupport;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.utils.StructureMapUtilities;
import org.hl7.fhir.r4.utils.transform.BatchContext;
import org.hl7.fhir.r4.utils.transform.FhirTransformationEngine;
import org.hl7.fhir.r4.utils.transform.MapHandler;
import org.hl7.fhir.r4.utils.transform.MappingIO;
import org.hl7.fhir.r4.utils.transform.deserializer.UrlProcessor;
import org.hl7.fhir.utilities.TextFile;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ANTLRStructureMapTests {
	@Test
	public void FilePathing(){
		File file = new File("");
		System.out.println(file.getAbsolutePath());
	}


	@Test
	public void testParse() throws Exception {
		MapHandler parse = new MapHandler();

		//Read the Map into a string
		MappingIO mapping = new MappingIO();
		mapping.setMappingFile(new File("colorectal3.map"));
		StructureMap map = mapping.getStructureMap();

		System.out.println(map);

	}

	@Test
	public void testUrlPrse() throws Exception{
		UrlProcessor processor = new UrlProcessor();
		processor.parseUrl("\"http://fhir.hl7.org.au/fhir/rcpa/StructureMap/ColorectalMap\"");
	}

	@Test
	public void testTransform() throws Exception {
		final boolean _ANALYSE = true;
		FhirTransformationEngine scu = null;
		StructureDefinition sd1 = null;
		BatchContext bc = new BatchContext();
		PrePopulatedValidationSupport validation = new PrePopulatedValidationSupport();
		Map<String, StructureMap> maps = new HashMap<String, StructureMap>();

		FhirContext context = FhirContext.forR4();
		context.setValidationSupport(validation);
		sd1 = this.createTestStructure();

		//sd1 = context.newXmlParser().parseResource(StructureDefinition.class, new FileReader(new File("C:\\JCimiProject\\hapi-fhir-resource-profile-generator\\target\\classes\\mapping\\logical\\structuredefinition-colorectal.xml")));
		if (sd1.getId().contains("/"))
			sd1.setId(sd1.getId().split("/")[sd1.getId().split("/").length - 1]);
		validation.addStructureDefinition(sd1);
		bc.addStructureDefinition(sd1);
		for (StructureDefinition sd : new DefaultProfileValidationSupport().fetchAllStructureDefinitions(FhirContext.forR4())){
			bc.addStructureDefinition(sd);
			validation.addStructureDefinition(sd);
		}
		StructureMap map = null;
		HapiWorkerContext hapiContext = new HapiWorkerContext(context, validation);
		scu = new FhirTransformationEngine(hapiContext);
		MappingIO mapping = new MappingIO("simpleMapTest.map");
		//mapping.setMappingFile(new File("colorectal3.map"));
		//mapping.setMappingFile(new File("simpleMapTest.map"));
		//String mapText = mapping.readFile();

		map = mapping.getStructureMap();
		if (_ANALYSE) {
			List<StructureDefinition> result = scu.analyse(bc, null, map).getProfiles();

			ProfileUtilities profileUtilities = new ProfileUtilities(hapiContext, null, null);

			StructureDefinition newCode = result.get(0);
			//profileUtilities.generateSnapshot(validation.fetchStructureDefinition(context, "http://hl7.org/fhir/StructureDefinition/Coding"), newCode, "http://foo.com/StructureDefinition/MyMap-Coding", "MyMap-Coding");

			for (StructureDefinition sd : result) {
				System.out.println(sd.toString());
				System.out.println(context.newXmlParser().setPrettyPrint(true).encodeResourceToString(sd));
			}
		}
		else if (!_ANALYSE) {
			System.out.println(map);
		}
	}

	@Test
	public void legacyTestTransform() throws Exception {
		StructureMapUtilities scu = null;
		StructureDefinition sd1 = null;
		PrePopulatedValidationSupport validation = new PrePopulatedValidationSupport();
		Map<String, StructureMap> maps = new HashMap<String, StructureMap>();

		FhirContext context = FhirContext.forR4();
		context.setValidationSupport(validation);
		//sd1 = this.createTestStructure();

		sd1 = context.newXmlParser().parseResource(StructureDefinition.class, new FileReader(new File("C:\\JCimiProject\\hapi-fhir-resource-profile-generator\\target\\classes\\mapping\\logical\\structuredefinition-colorectal.xml")));
		if (sd1.getId().contains("/"))
			sd1.setId(sd1.getId().split("/")[sd1.getId().split("/").length - 1]);
		validation.addStructureDefinition(sd1);
		for (StructureDefinition sd : new DefaultProfileValidationSupport().fetchAllStructureDefinitions(FhirContext.forR4())){
			sd.setId(sd.getId().split("/")[sd.getId().split("/").length - 1]);
			validation.addStructureDefinition(sd);
		}
		StructureMap map = null;
		HapiWorkerContext hapiContext = new HapiWorkerContext(context, validation);
		scu = new StructureMapUtilities(hapiContext);
		map = scu.parse(TextFile.fileToString("colorectal3.map"));

		MappingIO mapping = new MappingIO();
		//mapping.setMappingFile(new File("colorectal3.map"));
		//mapping.setMappingFile(new File("simpleMapTestLegacy.map"));

		List<StructureDefinition> result = scu.analyse( null, map).getProfiles();

		ProfileUtilities profileUtilities = new ProfileUtilities(hapiContext, null, null);

		StructureDefinition newCode = result.get(0);
		//profileUtilities.generateSnapshot(validation.fetchStructureDefinition(context, "http://hl7.org/fhir/StructureDefinition/Coding"), newCode, "http://foo.com/StructureDefinition/MyMap-Coding", "MyMap-Coding");

		for (StructureDefinition sd : result) {
			System.out.println(sd.toString());
			System.out.println(context.newXmlParser().setPrettyPrint(true).encodeResourceToString(sd));
		}
	}


	public StructureDefinition createTestStructure(){
		StructureDefinition sd = new StructureDefinition();
		sd.setId("TestStructure");
		sd.setUrl("http://opencimi.org/structuredefinition/TestStructure");
		sd.setStatus(Enumerations.PublicationStatus.DRAFT);
		sd.setName("TestStructure");
		sd.setType("TestStructure");
		sd.setSnapshot(this.createTestSnapshot());
		sd.setDifferential(this.createTestDiff());
		sd.setKind(StructureDefinition.StructureDefinitionKind.LOGICAL);

		return sd;
	}
	public StructureDefinition.StructureDefinitionSnapshotComponent createTestSnapshot(){
		StructureDefinition.StructureDefinitionSnapshotComponent retVal = new StructureDefinition.StructureDefinitionSnapshotComponent();
		List<ElementDefinition> eList = new ArrayList<>();
		ElementDefinition ed0 = new ElementDefinition();
		//ElementDefinition.ElementDefinitionBaseComponent base = new ElementDefinition.ElementDefinitionBaseComponent();
		//base.setId("http://hl7.org/fhir/StructureDefinition/Element");
		ed0.setId("TestStructure");
		ed0.setSliceName("TestStructure");
		ed0.setPath("TestStructure");
		// ed0.setBase(base);
		ed0.setMin(1);
		ed0.setMax("1");
		eList.add(ed0);


		ElementDefinition ed = new ElementDefinition();
		//ElementDefinition.ElementDefinitionBaseComponent base = new ElementDefinition.ElementDefinitionBaseComponent();
		//base.setId("http://hl7.org/fhir/StructureDefinition/Element");
		ed.setId("system");
		ed.setSliceName("system");
		ed.setPath("TestStructure.system");
		//ed.setBase(base);
		ed.setFixed(new UriType().setValue("HTTP://opencimi.org/structuredefinition/TestStructure.html#Debugging"));
		//ed.setType(this.createTypeRefList());
		ed.setMin(1);
		ed.setMax("1");
		eList.add(ed);

		ed = new ElementDefinition();
		//ElementDefinition.ElementDefinitionBaseComponent base = new ElementDefinition.ElementDefinitionBaseComponent();
		//base.setId("http://hl7.org/fhir/StructureDefinition/Element");
		ed.setId("someValue");
		ed.setSliceName("someValue");
		ed.setPath("TestStructure.someValue");
		//ed.setBase(base);
		ed.setFixed(new StringType().setValue("my value"));
		//ed.setType(this.createTypeRefList());
		ed.setMin(1);
		ed.setMax("0");
		eList.add(ed);


		retVal.setElement(eList);
		return retVal;


	}

	public StructureDefinition.StructureDefinitionDifferentialComponent createTestDiff(){
		StructureDefinition.StructureDefinitionDifferentialComponent retVal = new StructureDefinition.StructureDefinitionDifferentialComponent();
		List<ElementDefinition> eList = new ArrayList<>();
		ElementDefinition ed0 = new ElementDefinition();
		//ElementDefinition.ElementDefinitionBaseComponent base = new ElementDefinition.ElementDefinitionBaseComponent();
		//base.setId("http://hl7.org/fhir/StructureDefinition/Element");
		ed0.setId("TestStructure");
		ed0.setSliceName("TestStructure");
		ed0.setPath("TestStructure");
		// ed0.setBase(base);
		ed0.setMin(1);
		ed0.setMax("1");
		eList.add(ed0);


		ElementDefinition ed = new ElementDefinition();
		//ElementDefinition.ElementDefinitionBaseComponent base = new ElementDefinition.ElementDefinitionBaseComponent();
		//base.setId("http://hl7.org/fhir/StructureDefinition/Element");
//		ed.setId("system");
//		ed.setSliceName("system");
//		ed.setPath("TestStructure.system");
//		//ed.setBase(base);
//		ed.setFixed(new UriType().setValue("HTTP://opencimi.org/structuredefinition/TestStructure.html#Debugging"));
//		//ed.setType(this.createTypeRefList());
//		eList.add(ed);

		ed = new ElementDefinition();
		//ElementDefinition.ElementDefinitionBaseComponent base = new ElementDefinition.ElementDefinitionBaseComponent();
		//base.setId("http://hl7.org/fhir/StructureDefinition/Element");
		ed.setId("someValue");
		ed.setSliceName("someValue");
		ed.setPath("TestStructure.someValue");
		//ed.setBase(base);
		ed.setFixed(new StringType().setValue("my value"));
		//ed.setType(this.createTypeRefList());
		eList.add(ed);

		retVal.setElement(eList);
		return retVal;
	}
}