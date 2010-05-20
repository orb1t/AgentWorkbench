/**
 * @author Christian Derksen - DAWIS - ICB - University Duisburg-Essen
 * Copyright 2010 Christian Derksen
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package monitormas.ontology;

import jade.content.onto.Introspector;
import jade.content.onto.Ontology;
import jade.content.onto.ReflectiveIntrospector;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class AgentGUIProjectOntology extends Ontology {

	private static final long serialVersionUID = 9203567406733944302L;
	
	private static AgentGUIProjectOntology instance = null;
	public static final String ONTOLOGY_NAME = "AgentGUI-ProjectOntology";
	
	private static boolean useHardCodedOntologyDefinition = false;
	
	private static final String srcPackage = getSrcPackage();
	private static final String srcFileURL = srcPackage + "/agentgui.xml";
	private static final String xmlRootNode = "project";
	private static final String xmlNode4SubOntologies = "subOntologies";
	
	/**
	 * private Constructor of this class
	 * @param ontology
	 * @param introspector
	 */
	private AgentGUIProjectOntology(Ontology[] ontology, Introspector introspector) {
		super(ONTOLOGY_NAME, ontology, introspector);
	}
	
	/**
	 * This method returns the instance of the project-ontology
	 * @return
	 */
	public static AgentGUIProjectOntology getInstance() {
		if (instance == null) {
			instance = createProjectOntology();
		}
		return instance;
	}
	
	/**
	 * This Method actually builds the Project-Ontology and returns it 
	 * @return
	 */
	private static AgentGUIProjectOntology createProjectOntology() {

		AgentGUIProjectOntology proOntology = null;
		Introspector introspector = null;
		Ontology[] ontologyList = null;
		
		// --------------------------------------------------------------------
		// --- Switch between AgentGUI-Config. or hard coded Ontology ---------
		// --------------------------------------------------------------------		
		if ( useHardCodedOntologyDefinition==false ) {
			// --- In case that you want to use the AgentGUI-Configuration ----
			ArrayList<String> ontologies = getSubOntologies();
			if (ontologies==null) {
				return null;
			}
			
			ontologyList = new Ontology[ontologies.size()];
			int nOnto = -1;
			String subOnto = null;
			Class<?> currOntoClass = null;
			Method method = null;
			
			Iterator<String> it = ontologies.iterator();
			while (it.hasNext()) {

				nOnto++;
				subOnto = it.next();
				 
				// --- Try to get an instance of the current Ontology ---------
				try {
					currOntoClass = Class.forName(subOnto);
					method = currOntoClass.getMethod("getInstance", new Class[0]);
					ontologyList[nOnto] = (Ontology) method.invoke(currOntoClass, new Object[0]);
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}

		} else {
			// --- Here you can hard define the list of Ontologies to use ----- 
			ontologyList = new Ontology[2];
			ontologyList[0] = contmas.ontology.ContainerTerminalOntology.getInstance(); 
			ontologyList[1] = sma.ontology.DisplayOntology.getInstance();
		}
		
		// --- Add the Ontology-Array to this Project-Ontology ----------------
		try {
			introspector = new ReflectiveIntrospector();
			proOntology = new AgentGUIProjectOntology(ontologyList, introspector);
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
		return proOntology;
	}
	
	/**
	 * This Method retuns all defined Sud-Ontologies, which 
	 * are defined in the XMl-File 'agentgui.xml'
	 * @return ArrayList<String>
	 */
	private static ArrayList<String> getSubOntologies() {
		
		String nodeName = null;
		String nodeValue= null;
		Document doc = getAgentGUIxmlFile();
		ArrayList<String> ontologies = new ArrayList<String>();
		
		if (doc==null) return null;;
		
		// --- Get the Root-Node of the XML-Document ----------------
		Node packageNode = doc.getElementsByTagName( xmlRootNode ).item(0);
		NodeList packageElements = packageNode.getChildNodes();
		for(int i = 0; i< packageElements.getLength(); i++) {	 
			// --- Running through sub.nodes ------------------------
			nodeName = packageElements.item(i).getNodeName();
			nodeValue = packageElements.item(i).getTextContent().trim();
			// ------------------------------------------------------
			if ( nodeName.equalsIgnoreCase(xmlNode4SubOntologies)) {
				// --- Found interesting node -----------------------
				//System.out.println(nodeName + ": " + nodeValue);
				NodeList subOntologies = packageElements.item(i).getChildNodes();
				for (int j = 0; j < subOntologies.getLength(); j++) {
					nodeName = subOntologies.item(j).getNodeName();
					nodeValue = subOntologies.item(j).getTextContent().trim();
					if ( nodeValue.length() != 0 ) {
						// --- Add defined ontologies to the list ---
						//System.out.println(nodeName + ": " + nodeValue);
						ontologies.add(nodeValue);
					}
				}
			}
			// ------------------------------------------------------
		}
		return ontologies;
	}
	
	/**
	 * This Method retuns the (XML-) Document of the AgentGUI-Configuration
	 * @return Document
	 */
	private static Document getAgentGUIxmlFile() {
		
		DocumentBuilderFactory docBuiFac = null;
		DocumentBuilder docBui = null;
		Document doc = null;
		
		// --- Start acting on the document -------------------------
		docBuiFac = DocumentBuilderFactory.newInstance();
		try {
			docBui = docBuiFac.newDocumentBuilder();
			doc = parse(docBui);			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;	
	}
	
	/**
	 * This method parses the File to a local Document-Object
	 */
	private static Document parse( DocumentBuilder docBui ) {
		
		Document doc = null;
		InputStream srcFileInputStream = AgentGUIProjectOntology.class.getClassLoader().getResourceAsStream(srcFileURL);
		if (srcFileInputStream!=null) {
			try {
				doc = docBui.parse(srcFileInputStream);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println(AgentGUIProjectOntology.class.getName() +  ": Could not find file '" + srcFileURL + "'");
		}
		return doc;
	}

	/**
	 * This Method returns the Root-Reference of this class. 
	 * E.g. in case that this class is located in package 
	 * 'sma.ontology' it returns 'sma'
	 * @return
	 */
	private static String getSrcPackage() {
		String packageSource = AgentGUIProjectOntology.class.getPackage().getName();
		packageSource = packageSource.substring(0, packageSource.indexOf("."));
		return packageSource;
	}
	
}