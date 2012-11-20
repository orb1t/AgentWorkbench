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
package agentgui.simulationService.time;

import jade.core.Agent;
import jade.core.ServiceException;

import java.util.HashMap;

import agentgui.core.project.Project;
import agentgui.simulationService.SimulationService;
import agentgui.simulationService.SimulationServiceHelper;


/**
 * The Class TimeModelContinuous.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class TimeModelContinuous extends TimeModel {

	private static final long serialVersionUID = -6787156387409895035L;

	public final static String PROP_TimeStart = "TimeStart";
	public final static String PROP_TimeStop = "TimeStop";
	public final static String PROP_AccelerationFactor = "AccelerationFactor";
	
	private transient SimulationServiceHelper simHelper = null;
	
	private long startTime = 0;
	private long stopTime = 0;
	private double accelerationFactor = 1.F;
	
	private boolean executed = false;
	private long pauseTime = 0;
	private long timeDiff = 0;
	
	
	/**
	 * Instantiates a new continuous TimeModel .
	 */
	public TimeModelContinuous() {
	}
	/**
	 * Instantiates a new continuous TimeModel ..
	 * @param startTime the start time for the time model
	 */
	public TimeModelContinuous(Long startTime, Long stopTime) {
		this.setStartTime(startTime);
		this.setStopTime(stopTime);
	}
		
	/* (non-Javadoc)
	 * @see agentgui.simulationService.time.TimeModel#getCopy()
	 */
	@Override
	public TimeModel getCopy() {
		TimeModelContinuous tmc = new TimeModelContinuous();
		// ------------------------------------------------
		// --- Do this first to avoid side effects --------
		tmc.setExecutedLocal(this.executed);
		tmc.setPauseTime(this.pauseTime);
		tmc.setTimeDiff(this.timeDiff);
		// --- Do this first to avoid side effects --------
		// ------------------------------------------------		
		tmc.setStartTime(this.startTime);
		tmc.setStopTime(this.stopTime);
		tmc.setAccelerationFactor(this.accelerationFactor);
		return tmc;
	}
	
	/**
	 * Sets the start time of the time model.
	 * @param startTime the new start time
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
		this.setPauseTime(startTime);
	}
	/**
	 * Returns the start time of the time model.
	 * @return the start time
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Returns the stop time for the time model.
	 * @return the stopTime
	 */
	public long getStopTime() {
		return stopTime;
	}
	/**
	 * Sets the stop time for the time model.
	 * @param stopTime the stopTime to set
	 */
	public void setStopTime(long stopTime) {
		this.stopTime = stopTime;
	}
	
	/**
	 * Gets the acceleration factor.
	 * @return the accelerationFactor
	 */
	public double getAccelerationFactor() {
		return accelerationFactor;
	}
	/**
	 * Sets the acceleration factor.
	 * @param accelerationFactor the accelerationFactor to set
	 */
	public void setAccelerationFactor(double accelerationFactor) {
		this.accelerationFactor = accelerationFactor;
	}

	/**
	 * Sets the time, where the time model was paused.
	 * @param pauseTime the new pause time
	 */
	private void setPauseTime(long pauseTime) {
		this.pauseTime = pauseTime;
	}
	/**
	 * Returns the time, where the time model was paused.
	 * @return the pause time
	 */
	private long getPauseTime() {
		if (this.pauseTime==0) {
			this.pauseTime=this.getStartTime();
		}
		return pauseTime;
	}
	
	/**
	 * Sets the time model to be executed in order to forward the time in the  model.
	 * In this method the local system time will be used to set the timeDiff.
	 * @param executed the new executed
	 */
	public void setExecutedLocal(boolean executeTimeModel) {
		if (executeTimeModel==true) {
			this.setTimeDiff(this.getSystemTime() - this.getStartTime());	
		} else {
			this.setPauseTime(this.getTimeLocal());
		}
		this.executed=executeTimeModel;
	}
	
	/**
	 * Sets the time model to be executed in order to forward the time in the  model.
	 * In this method the platform time (provided by the SimulationService) will be used 
	 * to set the timeDiff.
	 * @param executeTimeModel the executed
	 * @param currentAgent the current agent
	 */
	public void setExecutedPlatform(boolean executeTimeModel, Agent currentAgent) {
		if (executeTimeModel==true) {
			this.setTimeDiff(this.getSystemTimeSynchronized(currentAgent) - this.getStartTime());
		} else {
			this.setPauseTime(this.getTimePlatform(currentAgent));
		}
		this.executed=executeTimeModel;
	}
	
	/**
	 * Checks if this time model is executed and so, if time is moving forward.
	 * @return true, if is executed
	 */
	public boolean isExecuted() {
		return executed;
	}
	
	/**
	 * Sets the time difference.
	 * @param timeDiff the timeDiff to set
	 */
	private void setTimeDiff(long timeDiff) {
		this.timeDiff = timeDiff;
	}
	/**
	 * Returns the time difference.
	 * @return the timeDiff
	 */
	public long getTimeDiff() {
		return timeDiff;
	}
	
	/**
	 * Returns the local time as long including the currently set timeDiff.
	 * @return the time
	 */
	public long getTimeLocal() {
		long currTime;
		if (isExecuted()==true) {
			currTime = this.getSystemTime() - getTimeDiff();
		} else {
			currTime = this.getPauseTime();
		}
		return currTime;
	}
	
	/**
	 * Returns the synchronized platform time, if available, or the local time including the timeDiff. 
	 * Platform time requires that JADE is running and that the SimulationService is available.
	 *
	 * @see SimulationServiceHelper#getSynchTimeMillis()
	 * 
	 * @param currentAgent the current agent who is asking for the platform time
	 * @return the time 
	 */
	public long getTimePlatform(Agent currentAgent) {
		long currTime;
		if (isExecuted()==true) {
			currTime = this.getSystemTimeSynchronized(currentAgent) - getTimeDiff();
		} else {
			currTime = this.getPauseTime();
		}
		return currTime;
	}
	

	/**
	 * Returns the local system time. 
	 * @return the system time
	 */
	private long getSystemTime() {
		return System.currentTimeMillis();
	}
	
	/**
	 * Returns the system time, which is either the synchronized time of the SimulationService 
	 * or, in case of an inactive JADE platform, the time of the local machine. 
	 *
	 * @see SimulationServiceHelper#getSynchTimeMillis()
	 * 
	 * @param agent the agent
	 * @return the synchronized system time
	 */
	private long getSystemTimeSynchronized(Agent currentAgent) {
		long sysTime;
		if (currentAgent==null) {
			// --- Just take the local time -------------------------
			sysTime = System.currentTimeMillis();
			System.err.println("No agent was set. Took local time!");
			
		} else {
			// --- Try to get the synchronized platform time --------
			try {
				sysTime = this.getSimulationServiceHelper(currentAgent).getSynchTimeMillis();
				
			} catch (ServiceException se) {
				System.err.println("Could not get synchronized platform time. Took local time!");
				sysTime = System.currentTimeMillis();
			}
		}
		return sysTime;
	}
	
	/**
	 * Returns the SimulationServiceHelper that allows to connect to the SimulationService.
	 * @see SimulationService
	 * @see SimulationServiceHelper
	 * @return the SimulationServiceHelper
	 */
	private SimulationServiceHelper getSimulationServiceHelper(Agent currentAgent) {
		if (this.simHelper==null) {
			try {
				this.simHelper = (SimulationServiceHelper) currentAgent.getHelper(SimulationService.NAME);
			} catch (ServiceException se) {
				System.err.println("Could not connect to SimulationService!");
			}
		}
		return this.simHelper;
	}
	
	
	/* (non-Javadoc)
	 * @see agentgui.simulationService.time.TimeModel#step()
	 */
	@Override
	public void step() {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see agentgui.simulationService.time.TimeModel#stepBack()
	 */
	@Override
	public void stepBack() {
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see agentgui.simulationService.time.TimeModel#getJPanel4Configuration()
	 */
	@Override
	public JPanel4TimeModelConfiguration getJPanel4Configuration(Project project) {
		return new TimeModelContinuousConfiguration(project);
	}
	/* (non-Javadoc)
	 * @see agentgui.simulationService.time.TimeModel#getJToolBar4Execution()
	 */
	@Override
	public JToolBarElements4TimeModelExecution getJToolBarElements4TimeModelExecution() {
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see agentgui.simulationService.time.TimeModel#setSetupConfiguration(java.util.HashMap)
	 */
	@Override
	public void setTimeModelSettings(HashMap<String, String> timeModelSettings) {
		
		try {
			
			if (timeModelSettings.size()==0) {
				// --- Use Default values -----------------
				this.startTime = System.currentTimeMillis();
				this.stopTime = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
				this.accelerationFactor = 1;
				return;
			}
			
			String stringStartTime = timeModelSettings.get(PROP_TimeStart);
			String stringStopTime = timeModelSettings.get(PROP_TimeStop);
			String stringAccelerationFactor = timeModelSettings.get(PROP_AccelerationFactor);

			if (stringStartTime!=null) {
				this.startTime = Long.parseLong(stringStartTime);	
			}
			if (stringStopTime!=null) {
				this.stopTime = Long.parseLong(stringStopTime);	
			}
			if (stringAccelerationFactor!=null) {
				this.accelerationFactor = Float.parseFloat(stringAccelerationFactor);	
			} else {
				this.accelerationFactor = 1;
			}
	
		} catch (Exception ex) {
			System.err.println("Error while converting TimeModel settings from setup");
		}
		
	}
	/* (non-Javadoc)
	 * @see agentgui.simulationService.time.TimeModel#getSetupConfiguration()
	 */
	@Override
	public HashMap<String, String> getTimeModelSetting() {
		HashMap<String, String> hash = new HashMap<String, String>();
		hash.put(PROP_TimeStart, ((Long) this.startTime).toString());
		hash.put(PROP_TimeStop, ((Long) this.stopTime).toString());
		hash.put(PROP_AccelerationFactor, ((Double) this.accelerationFactor).toString());
		return hash;
	}
	
}
