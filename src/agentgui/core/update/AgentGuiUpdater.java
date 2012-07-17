/**
 * ***************************************************************
 * Agent.GUI is a framework to develop Multi-agent based simulation 
 * applications based on the JADE - Framework in compliance with the 
 * FIPA specifications. 
 * Copyright (C) 2010 Christian Derksen and DAWIS
 * http://www.dawis.wiwi.uni-due.de
 * http://sourceforge.net/projects/agentgui/
 * http://www.agentgui.org 
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package agentgui.core.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;

import agentgui.core.application.Application;
import agentgui.simulationService.distribution.Download;
import agentgui.simulationService.distribution.DownloadThread;

/**
 * The Class AgentGuiUpdater.
 */
public class AgentGuiUpdater extends Thread {

	private String updateSite = null;
	private final String updateSiteAddition = "?key=xml";

	private Integer updateAutoConfiguration = null;
	
	private String downloadPath = null;
	private String latestRevisionFile = "latestVersion.xml";

	private UpdateInformation updateInformation = null;
	private DownloadThread downloadThread4Update = null;
	
	private AgentGuiUpdaterProgress progressMonitor = null;
	
	/**
	 * Instantiates a new Agent.GUI updater process.
	 */
	public AgentGuiUpdater() {
	
		this.setName("Agent.GUI-Updater");
		
		Application.getGlobalInfo();
		updateSite = Application.getGlobalInfo().getUpdateSite();
		updateAutoConfiguration = Application.getGlobalInfo().getUpdateAutoConfiguration();
		
		downloadPath = Application.getGlobalInfo().PathDownloads(true);
		
	}
	
	@Override
	public void run() {
		super.run();
		
		// --- Download Info about Latest version --------- 
		String srcFileURL = this.updateSite + this.updateSiteAddition;
		String destinFile = this.downloadPath + this.latestRevisionFile; 
		Download download = new Download(srcFileURL, destinFile);
		download.startDownload();
		boolean loadUpdateInformation = download.isFinished() && download.wasSuccessful();
		download = null;
		
		if (loadUpdateInformation==true) {
			// --------------------------------------------
			// --- Load the UpdateInformation -------------
			this.updateInformation = this.loadUpdateInformation(destinFile);
			if (this.updateInformation!=null) {
				// ----------------------------------------
				// --- Download => Successful download ? --
				boolean readyToInstall = this.downloadUpdateFile();
				if (readyToInstall==true) {
					// ------------------------------------
					// --- Ready to install ---------------
					System.out.println("Ready to install !");
					
				} else {
					// ------------------------------------
					// --- Download unsuccessful ----------
					System.out.println("Unsuccessful download !");	
					
				}
				
			}
		}
		
	}
	
	/**
	 * Download the update file in an extra thread.
	 * @return true, if successful
	 */
	private boolean downloadUpdateFile() {
		
		String sourceFileURL = this.updateInformation.getDownloadLink();
		String destinFileLocal = this.downloadPath + this.updateInformation.getDownloadFile(); 
		
		this.progressMonitor = new AgentGuiUpdaterProgress();
		this.progressMonitor.setVisible(true);
		this.progressMonitor.setProgress(0);
		this.downloadThread4Update = new DownloadThread(sourceFileURL, destinFileLocal);
		this.downloadThread4Update.start();
		while(this.downloadThread4Update.isFinished()==false) {
			this.progressMonitor.setProgress(this.downloadThread4Update.getDownloadProgress());
			if (this.progressMonitor.isCanceled()) {
				this.downloadThread4Update.doCancel();
			}
			try {
				sleep(200);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		} // end while
		this.progressMonitor.setProgress(100);
		this.progressMonitor.setVisible(false);
		this.progressMonitor.dispose();
		this.progressMonitor = null;
		
		// --- Successful download ? --------------
		boolean readyToInstall = this.downloadThread4Update.wasSuccessful();
		this.downloadThread4Update = null;
		return readyToInstall;
		
	}
	
	/**
	 * Creates an example file.
	 * @param destinFile the destination file
	 */
	@SuppressWarnings("unused")
	private void createExampleFile(String destinFile) {
		
		UpdateInformation ui = new UpdateInformation();
		ui.setMajorRevision(0);
		ui.setMinorRevision(98);
		ui.setBuild(174);
		ui.setDownloadLink("http://update.agentgui.org/updates/Agent.GUI_0.98_171.zip");
		ui.setDownloadFile("Agent.GUI_0.98_171.zip");
		ui.setDownloadSize(72456123);
		this.saveUpdateInformation(ui, destinFile);
	}
	
	/**
	 * Save update information.
	 *
	 * @param updateInformation the update information
	 * @param updateFile the update file
	 */
	private void saveUpdateInformation(UpdateInformation updateInformation, String updateFile) {
	    
		// --- Encode downloadLink and downloadFile -------
		String downloadLink64 = null;
		String downloadFile64 = null;
		try {
			downloadLink64 = new String(Base64.encodeBase64(updateInformation.getDownloadLink().getBytes("UTF8")));
			downloadFile64 = new String(Base64.encodeBase64(updateInformation.getDownloadFile().getBytes("UTF8")));
			
			updateInformation.setDownloadLink(downloadLink64);
			updateInformation.setDownloadFile(downloadFile64);
			
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		
		// --- Save File ----------------------------------
	    JAXBContext context;
		try {
			FileWriter componentFileWriter = new FileWriter(updateFile);
			
			context = JAXBContext.newInstance(UpdateInformation.class);
		    Marshaller marsh = context.createMarshaller();
		    marsh.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		    marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		    marsh.marshal(updateInformation, componentFileWriter);

		    componentFileWriter.close();

		} catch (JAXBException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Load update information.
	 *
	 * @param updateFile the update file
	 * @return the update information
	 */
	private UpdateInformation loadUpdateInformation(String updateFile) {
	    
		File componentFile = null;
		// --- Load file ----------------------------------
		UpdateInformation updateInformation = null;
		try {
		    componentFile = new File(updateFile);
		    FileReader componentReader = new FileReader(componentFile);
	
		    JAXBContext context = JAXBContext.newInstance(UpdateInformation.class);
		    Unmarshaller unmarsh = context.createUnmarshaller();
		    updateInformation = (UpdateInformation) unmarsh.unmarshal(componentReader);
		    
		    componentReader.close();
		    componentFile.delete();
		    
		} catch (JAXBException ex) {
		    ex.printStackTrace();
		} catch (FileNotFoundException ex) {
		    ex.printStackTrace();
		} catch (IOException ex) {
		    ex.printStackTrace();
		}

		// --- Decode downloadLink and downloadFile -------
		String downloadLink = null;
		String downloadFile = null;
		try {
			downloadLink = new String(Base64.decodeBase64(updateInformation.getDownloadLink().getBytes("UTF8")));
			downloadFile = new String(Base64.decodeBase64(updateInformation.getDownloadFile().getBytes("UTF8")));
			
			updateInformation.setDownloadLink(downloadLink);
			updateInformation.setDownloadFile(downloadFile);
			
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		
		return updateInformation;
	}

}
