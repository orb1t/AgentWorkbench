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
package agentgui.simulationService.load.threading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Protocol class for storing Thread-Times
 * 
 * @author Hanno Monschan - DAWIS - ICB - University of Duisburg-Essen
 */
@XmlRootElement
public class ThreadProtocol implements Serializable {
	
	private static final long serialVersionUID = 7906666593362238059L;
	
	private String simulationSetup;
	private String containerName;
	private String machineName;
	private long timestamp;
	private Vector<ThreadTime> threadTimes;
	
	
	/**
	 * Instantiates a new thread protocol.
	 */
	public ThreadProtocol() {
	}
	
	/**
	 * Instantiates a new thread protocol.
	 *
	 * @param simulationSetup the simulation setup
	 * @param timestamp the time stamp
	 */
	public ThreadProtocol(String simulationSetup, long timestamp) {
		this.setSimulationSetup(simulationSetup);
		this.setTimestamp(timestamp);
	}
	
	/**
	 * Instantiates a new thread protocol.
	 *
	 * @param simulationSetup the simulation setup
	 * @param containerName the container name
	 * @param machineName the machine name
	 * @param timestamp the time stamp
	 * @param threadTimes the thread times
	 */
	public ThreadProtocol(String simulationSetup, String containerName, String machineName, long timestamp, Vector<ThreadTime> threadTimes) {
		this.simulationSetup = simulationSetup;
		this.containerName = containerName;
		this.machineName = machineName;
		this.timestamp = timestamp;
		this.threadTimes = threadTimes;
	}

	
	/**
	 * Gets the simulation setup.
	 * @return the simulation setup
	 */
	public String getSimulationSetup() {
		return simulationSetup;
	}
	/**
	 * Sets the simulation setup.
	 * @param simulationSetup the new simulation setup
	 */
	public void setSimulationSetup(String simulationSetup) {
		this.simulationSetup = simulationSetup;
	}

	/**
	 * Gets the timestamp.
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
	/**
	 * Sets the timestamp.
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Gets the thread times.
	 * @return the thread times
	 */
	public Vector<ThreadTime> getThreadTimes() {
		if (threadTimes==null) {
			threadTimes = new Vector<ThreadTime>();
		}
		return threadTimes;
	}
	/**
	 * Sets the thread times.
	 * @param threadTimes the new thread times
	 */
	public void setThreadTimes(Vector<ThreadTime> threadTimes) {
		this.threadTimes = threadTimes;
	}
	
	/**
	 * Gets the container name.
	 * @return the container name
	 */
	public String getContainerName() {
		return containerName;
	}
	/**
	 * Sets the container name.
	 * @param containerName the new container name
	 */
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	
	/**
	 * Gets the machine name.
	 * @return the machine name
	 */
	public String getMachineName() {
		return machineName;
	}
	/**
	 * Sets the machine name.
	 * @param machineName the new machine name
	 */
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	
	

	/**
	 * Save.
	 *
	 * @param file2Save the file2 save
	 * @return true, if successful
	 */
	public boolean save(File file2Save) {
		
		boolean saved = true;
		try {			
			JAXBContext pc = JAXBContext.newInstance(this.getClass()); 
			Marshaller pm = pc.createMarshaller(); 
			pm.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );
			pm.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE ); 

			Writer pw = new FileWriter(file2Save);
			pm.marshal(this, pw);
			pw.close();

		} catch (Exception e) {
			System.out.println("XML-Error while saving Setup-File!");
			e.printStackTrace();
			saved = false;
		}		
		return saved;		
	}
	

	/**
	 * Load.
	 *
	 * @param file2Read the file2 read
	 * @return true, if successful
	 */
	public boolean load(File file2Read) {
		
		boolean done = true;
		ThreadProtocol tp = null;
		try {
			JAXBContext pc = JAXBContext.newInstance(this.getClass());
			Unmarshaller um = pc.createUnmarshaller();
			FileReader fr = new FileReader(file2Read);
			tp = (ThreadProtocol) um.unmarshal(fr);
			fr.close();
			
		} catch (FileNotFoundException fne) {
			fne.printStackTrace();
			return false;
		} catch (JAXBException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		if (tp!=null) {
			this.setSimulationSetup(tp.getSimulationSetup());
			this.setContainerName(tp.getContainerName());
			this.setMachineName(tp.getMachineName());
			this.setTimestamp(tp.getTimestamp());
			this.setThreadTimes(tp.getThreadTimes());
		}
		return done;
	}
	
}