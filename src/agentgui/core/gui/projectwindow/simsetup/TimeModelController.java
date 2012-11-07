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
package agentgui.core.gui.projectwindow.simsetup;

import java.util.Observable;
import java.util.Observer;

import agentgui.core.application.Language;
import agentgui.core.gui.projectwindow.ProjectWindowTab;
import agentgui.core.project.Project;
import agentgui.core.sim.setup.SimulationSetups;
import agentgui.core.sim.setup.SimulationSetupsChangeNotification;
import agentgui.simulationService.time.DisplayJPanel4Configuration;
import agentgui.simulationService.time.TimeModel;

/**
 * The Class TimeModelController is used within Project 
 * and manages the display of TimeModel's
 * 
 * @see Project
 * @see TimeModel
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class TimeModelController implements Observer {

	private Project currProject = null;
	
	private String currTimeModelClass = null;
	private ProjectWindowTab pwt = null;
	private DisplayJPanel4Configuration display4TimeModel = null;
	
	
	/**
	 * Instantiates a new time model controller.
	 */
	public TimeModelController(Project project) {
		this.currProject = project;
		this.currProject.addObserver(this);
		this.addTimeModelDisplayToProjectWindow();
	}
	
	/**
	 * Sets the display for the selected TimeModel.
	 * @param display4TimeModel the new DisplayJPanel4Configuration
	 */
	public void setDisplay4TimeModel(DisplayJPanel4Configuration display4TimeModel) {
		this.display4TimeModel = display4TimeModel;
	}
	/**
	 * Returns the configuration display for the TimeModel.
	 * @return the DisplayJPanel4Configuration
	 */
	public DisplayJPanel4Configuration getDisplayJPanel4Configuration() {
		if (display4TimeModel==null) {
			
			this.currTimeModelClass = this.currProject.getTimeModelClass();
			if (this.currTimeModelClass!=null) {

				try {
					@SuppressWarnings("unchecked")
					Class<? extends TimeModel> timeModelClass = (Class<? extends TimeModel>) Class.forName(this.currTimeModelClass);
					TimeModel timeModel = (TimeModel) timeModelClass.newInstance();
					this.display4TimeModel = timeModel.getJPanel4Configuration();
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		return display4TimeModel;
	}

	/**
	 * Sets the time model display to project the window.
	 */
	private void addTimeModelDisplayToProjectWindow() {
		
		if (this.pwt!=null) {
			this.pwt.remove();
			this.pwt = null;
			this.display4TimeModel = null;
		}
		
		DisplayJPanel4Configuration configPanel = this.getDisplayJPanel4Configuration();
		if (configPanel!=null) {

			this.pwt = new ProjectWindowTab(this.currProject, ProjectWindowTab.DISPLAY_4_END_USER, 
					   Language.translate("Zeit-Konfiguration"), null, null, 
					   configPanel, Language.translate("Simulations-Setup"));
			this.pwt.add(0);
			
			configPanel.setTimeModel(null);
		}
		
	}
	
	/**
	 * Removes the time model display to project window.
	 */
	private void removeTimeModelDisplayToProjectWindow() {
		if (this.pwt!=null) {
			this.pwt.remove();
			this.pwt = null;
			this.display4TimeModel = null;
			this.currTimeModelClass = null;
		}
	}
	
	private void setupLoad() {
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object updateObject) {
		
		if (updateObject.equals(Project.CHANGED_TimeModelClass)) {
			// --- Changes in the TimeModel class of the project ----   
			if (this.currProject.getTimeModelClass()==null) {
				// --- Remove the Displaying parts, if there any ----
				this.removeTimeModelDisplayToProjectWindow();
				
			} else if (this.currProject.getTimeModelClass().equals(this.currTimeModelClass)==false) {
				// --- Display the new TimeModel display ------------
				this.addTimeModelDisplayToProjectWindow();
			}
		
		} else if (updateObject instanceof SimulationSetupsChangeNotification) {
			// --- Change inside the simulation setup ---------------
			SimulationSetupsChangeNotification scn = (SimulationSetupsChangeNotification) updateObject;
			switch (scn.getUpdateReason()) {
			case SimulationSetups.SIMULATION_SETUP_SAVED:
				break;
				
			case SimulationSetups.SIMULATION_SETUP_ADD_NEW:
				this.setupLoad();
				break;
				
			default:
				this.setupLoad();	
				break;
			}
		}
	}

	
}