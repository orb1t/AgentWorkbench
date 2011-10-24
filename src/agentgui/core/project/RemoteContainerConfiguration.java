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
package agentgui.core.project;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import agentgui.simulationService.ontology.RemoteContainerConfig;

/**
 * This class manages the configuration for remote containers, which is 
 * stored in a project. 
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class RemoteContainerConfiguration implements Serializable {

	private static final long serialVersionUID = 7745495134485079177L;
	
	@XmlTransient
	private RemoteContainerConfig ontologyRemoteConfainerConfig = null;
	
	
	/**
	 * Instantiates a new remote container configuration.
	 */
	public RemoteContainerConfiguration() {
		
	}
	/**
	 * Instantiates a new remote container configuration.
	 *
	 * @param remoteContainerConfig an Ontology RemoteContainerConfig
	 * @see RemoteContainerConfig
	 */
	public RemoteContainerConfiguration(RemoteContainerConfig remoteContainerConfig) {
		this.ontologyRemoteConfainerConfig = remoteContainerConfig;
	}
	
	
	/**
	 * @return the ontologyRemoteConfainerConfig
	 */
	@XmlTransient
	public RemoteContainerConfig getOntologyRemoteConfainerConfig() {
		return ontologyRemoteConfainerConfig;
	}
	/**
	 * @param ontologyRemoteConfainerConfig the ontologyRemoteConfainerConfig to set
	 * @see RemoteContainerConfig
	 */
	public void setOntologyRemoteConfainerConfig(RemoteContainerConfig ontologyRemoteConfainerConfig) {
		this.ontologyRemoteConfainerConfig = ontologyRemoteConfainerConfig;
	}
	
	

}
