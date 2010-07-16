package sim.setup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import common.FileCopier;

import application.Application;
import application.Language;
import application.Project;

public class SimulationSetups extends Hashtable<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9078535303459653695L;
	public final String XML_FilePostfix = ".xml";
	private Project currProject = Application.ProjectCurr;
	
	private String currSimSetupName = null;
	private String currSimXMLFile = null;
	private SimulationSetup currSimSetup = null;
	
	/**
	 * Constructor of this class
	 * @param project
	 */
	public SimulationSetups(Project project, String simSetupCurrent) {
		currProject = project;
		currSimSetupName = simSetupCurrent;
	}

	/**
	 * This Method creates the 'default' - Simulation-Setup 
	 */
	public void setupCreateDefault() {
		
		String xmlFile = null;
		String XMLPathName = null;
						
		this.currSimSetupName = "default";
		xmlFile = currProject.simSetups.getSuggestSetupFile(currSimSetupName) + this.XML_FilePostfix;
		
		this.put(currSimSetupName, xmlFile);
		currProject.simSetupCurrent = currSimSetupName;
		
		currSimSetup = new SimulationSetup(currProject); 
		
		XMLPathName = currProject.getSubFolderSetups(true) + xmlFile;		
		currSimXMLFile = XMLPathName;

		this.setupSave();
		
	}
	
	/**
	 * Adds a new Setup to this Hashtable
	 * @param name
	 * @param newFileName
	 */
	public void setupAddNew(String name, String newFileName) {
		
		// --- Aktuelles Setup speichern ------------------
		this.setupSave();
		// --- Name und Dateiname hinzuf�gen --------------
		this.put(name, newFileName);
		// --- Fokus auf das aktuelle Setup ---------------
		this.setupLoadAndFocus(name, true);
		// --- Projekt speichern --------------------------
		currProject.save();
	}

	/**
	 * Removes a Setup form this Hashtable
	 * @param name
	 */
	public void setupRemove(String name) {
		
		if (this.containsKey(name)==false) return;

		// --- Setup rausschmeissen------------------------
		this.remove(name);
		new File(currSimXMLFile).delete();
		// --- Groesse des Rests ber�cksichtigen ----------
		if (this.size() == 0) {
			// --- add default - Setup --------------------
			currSimSetupName = "default";
			// --- Noch keine Setups gespeichert ----------
			String newFileName = getSuggestSetupFile(currSimSetupName) + XML_FilePostfix;
			this.setupAddNew(currSimSetupName, newFileName);

		} else {
			// --- Select first Setup ---------------------
			this.setupLoadAndFocus(this.getFirstSetup(), false);
		}
		// --- Projekt speichern --------------------------
		currProject.save();
	}
	
	/**
	 * Renames a Setup and the associated file
	 * @param nameOld
	 * @param nameNew
	 * @param fileNameNew
	 */
	public void setupRename(String nameOld, String nameNew, String fileNameNew) {

		if (this.containsKey(nameOld)==false) return;

		// --- Verzeichnis-Info zusammenbauen -------------
		String pathSimXML  = currProject.getSubFolderSetups(true);
		String fileNameXMLNew = pathSimXML + fileNameNew; 
		// --- Datei umbenennen ---------------------------		
		File fileOld = new File(currSimXMLFile);
		File fileNew = new File(fileNameXMLNew);
		fileOld.renameTo(fileNew);
		// --- alten Eintrag raus, neuen rein -------------
		this.remove(nameOld);
		this.setupAddNew(nameNew, fileNameNew);
		// --- Projekt speichern --------------------------
		currProject.save();
	}

	/**
	 * Copies a Setup and the associated file
	 * @param nameOld
	 * @param nameNew
	 * @param fileNameNew
	 */
	public void setupCopy(String nameOld, String nameNew, String fileNameNew) {

		if (this.containsKey(nameOld)==false) return;
		
		// --- Verzeichnis-Info zusammenbauen -------------
		String pathSimXML  = currProject.getSubFolderSetups(true);
		String fileNameXMLNew = pathSimXML + fileNameNew; 
		// --- Datei kopieren -----------------------------
		FileCopier fc = new FileCopier();
		fc.copyFile(currSimXMLFile, fileNameXMLNew);
		// --- Name und Dateiname hinzuf�gen --------------
		this.put(nameNew, fileNameNew);
		// --- Fokus auf das aktuelle Setup ---------------
		this.setupLoadAndFocus(nameNew, false);
		// --- Projekt speichern --------------------------
		currProject.save();
	}
	
	/**
	 * Set the current Setup-File to the one given by name
	 * @param name
	 */
	public void setupLoadAndFocus(String name, boolean isAddedNew) {
		
		if (this.containsKey(name)==false) return;
		
		// --- Aktuelles Setup auf Input 'name' -----------
		currSimSetupName = name;
		currProject.simSetupCurrent = name;
		currSimXMLFile = currProject.getSubFolderSetups(true) + this.get(currSimSetupName);
		
		// --- 'SimulationSetup'-Objekt neu instanziieren -
		currSimSetup = new SimulationSetup(currProject);
				
		// --- Datei lesen und currSimSetup setzen -------- 
		if (isAddedNew==false) {
			this.setupOpen();	
		}		
		
		// --- Interessenten informieren ------------------
		currProject.setChangedAndNotify("SimSetups");
		
	}
	
	/**
	 * Finds and returns the first Setup name using an alphabetic order  
	 * @return
	 */
	public String getFirstSetup() {

		if (this.size()==0) return null;
			
		Vector<String> v = new Vector<String>(this.keySet());
		Collections.sort(v, String.CASE_INSENSITIVE_ORDER);
		Iterator<String> it = v.iterator();
		while (it.hasNext()) {
			currSimSetupName = it.next();
			break;
		}
		return currSimSetupName;
	}
	
	/**
	 * This method returns a Suggestion for the Name of a Setup-File
	 * @param inputText
	 * @return
	 */
	public String getSuggestSetupFile(String inputText) {
		
		String RegExp = "[a-z;_;0-9]";
		String suggest = inputText;
		String suggestNew = "";
		
		// --- Vorarbeiten ------------------------------
		suggest = suggest.toLowerCase();
		suggest = suggest.replaceAll("  ", " ");
		suggest = suggest.replace(" ", "_");
		suggest = suggest.replace("-", "_");
		suggest = suggest.replace("�", "ae");
		suggest = suggest.replace("�", "oe");
		suggest = suggest.replace("�", "ue");
		
		// --- Alle Buchstaben untersuchen --------------
		for (int i = 0; i < suggest.length(); i++) {
			String SngChar = "" + suggest.charAt(i);
			if ( SngChar.matches( RegExp ) == true ) {
				suggestNew = suggestNew + SngChar;	
			}						
	    }
		suggest = suggestNew;
		suggest = suggest.replaceAll("__", "_");
		return suggest;
	}
	
	/**
	 * This Method saves the current
	 * @return Simulation-Setup
	 */
	public void setupSave() {
		if (currSimSetup!=null) {
			currSimSetup.save();	
		}
		this.setupCleanUpSubFolder();
	}
	
	/**
	 * This Method loads the current Simulation-Setup to the local
	 * variable 'currSimSetup' which can be get and set by using
	 * 'getCurrSimSetup' or 'setCurrSimSetup'
	 */
	private void setupOpen() {
		
		String head=null, msg =null;;
		Integer answer = 0;
		JAXBContext pc = null;
		Unmarshaller um = null;
		
		try {
			pc = JAXBContext.newInstance( currSimSetup.getClass() );
			um = pc.createUnmarshaller();
			// --- 'SimulationSetup'-Objekt neu "instanziieren" -
			currSimSetup = (SimulationSetup) um.unmarshal( new FileReader( currSimXMLFile ) );
			currSimSetup.setCurrProject(currProject);

		} catch (FileNotFoundException e) {

			// --- Die Datei wurde nicht gefunden ---------
			head = Language.translate("Setup-Datei nicht gefunden!");
			msg  = Language.translate("Die Datei") + " '" + this.get(currSimSetupName) + "' " + Language.translate("f�r das Setup") + " '" + currSimSetupName + "' " + Language.translate("wurde nicht gefunden.");
			msg += Language.translate("<br>Kann der Name aus der Liste der Setups entfernt werden?");
			msg += Language.translate("<br>Falls nicht, wird eine neue Setup-Datei erzeugt.");
			answer = JOptionPane.showConfirmDialog(Application.MainWindow, msg, head, JOptionPane.YES_NO_OPTION);
			if ( answer == JOptionPane.YES_OPTION  ) {
				this.setupRemove(currSimSetupName);
			}			
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * This Method scans the Folder of the Simulation-Setups and
	 * deletes all file, which are not used in this project
	 */
	public void setupCleanUpSubFolder() {
		
		String pathSimXML  = currProject.getSubFolderSetups(true);
		File[] files = new File(pathSimXML).listFiles();
		if (files != null) {
			// --- Auflistung der Dateien durchlaufen -----
			for (int i = 0; i < files.length; i++) {
				// --- Nur xml-Dateien beachten -----------
				if (files[i].getName().endsWith(XML_FilePostfix)) {
					if (this.containsValue(files[i].getName())==false) {
						files[i].delete();
					}
				}
			}
			// --------------------------------------------
		}
	}
	
	/**
	 * @return the currSimSetup
	 */
	public SimulationSetup getCurrSimSetup() {
		return currSimSetup;
	}
	/**
	 * @param currSimSetup the currSimSetup to set
	 */
	public void setCurrSimSetup(SimulationSetup currSimSetup) {
		this.currSimSetup = currSimSetup;
	}

	/**
	 * @param currSimXMLFile the currSimXMLFile to set
	 */
	public void setCurrSimXMLFile(String currSimXMLFile) {
		this.currSimXMLFile = currSimXMLFile;
	}
	/**
	 * @return the currSimXMLFile
	 */
	public String getCurrSimXMLFile() {
		return currSimXMLFile;
	}
	
}
