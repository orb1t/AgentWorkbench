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
package agentgui.envModel.graph.visualisation.notifications;

import java.util.Vector;

import agentgui.core.charts.DataModel;
import agentgui.core.charts.NoSuchSeriesException;
import agentgui.core.charts.timeseriesChart.gui.TimeSeriesChartEditorJPanel;
import agentgui.core.charts.timeseriesChart.gui.TimeSeriesWidget;
import agentgui.core.ontologies.gui.OntologyClassEditorJPanel;
import agentgui.core.ontologies.gui.OntologyInstanceViewer;
import agentgui.envModel.graph.networkModel.GraphNode;
import agentgui.envModel.graph.networkModel.NetworkComponent;
import agentgui.envModel.graph.networkModel.NetworkModel;
import agentgui.ontology.TimeSeries;
import agentgui.ontology.TimeSeriesChart;

/**
 * The Class UpdateTimeSeries.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class UpdateTimeSeries extends UpdateDataSeries {

	private static final long serialVersionUID = 8563478170121892593L;

	private TimeSeries specifiedTimeSeries = null;
	
	/**
	 * Instantiates a new update time series.
	 *
	 * @param networkComponent the network component
	 * @param targetDataModelIndex the target data model index
	 */
	public UpdateTimeSeries(NetworkComponent networkComponent, int targetDataModelIndex) {
		super(networkComponent, targetDataModelIndex);
	}
	
	/**
	 * Instantiates a new update time series.
	 *
	 * @param graphNode the graph node
	 * @param targetDataModelIndex the target data model index
	 */
	public UpdateTimeSeries(GraphNode graphNode, int targetDataModelIndex) {
		super(graphNode, targetDataModelIndex);
	}
	
	/**
	 * Adds a time series.
	 * @param newTimeSeries the new time series
	 */
	public void addTimeSeries(TimeSeries newTimeSeries) {
		this.setTargetAction(UPDATE_ACTION.AddDataSeries);
		this.setSpecifiedTimeSeries(newTimeSeries);
	}
	/**
	 * Adds or exchanges a Time Series at a specified data series index.
	 * @param newTimeSeries the new time series
	 * @param dataSeriesIndex the data series index
	 */
	public void addOrExchangeTimeSeries(TimeSeries newTimeSeries, int dataSeriesIndex) {
		this.setTargetAction(UPDATE_ACTION.AddOrExchangeDataSeries);
		this.setSpecifiedTimeSeries(newTimeSeries);
		this.setTargetDataSeriesIndex(dataSeriesIndex);
	}
	/**
	 * Exchange exchanges a Time Series at a specified data series index.
	 * If the specified index can not be found, nothing will be done.
	 * @param newTimeSeries the new time series
	 * @param dataSeriesIndex the data series index
	 */
	public void exchangeTimeSeries(TimeSeries newTimeSeries, int dataSeriesIndex) {
		this.setTargetAction(UPDATE_ACTION.ExchangeDataSeries);
		this.setSpecifiedTimeSeries(newTimeSeries);
		this.setTargetDataSeriesIndex(dataSeriesIndex);
	}
	/**
	 * Removes the time series.
	 * @param dataSeriesIndex the data series index
	 */
	public void removeTimeSeries(int dataSeriesIndex) {
		this.setTargetAction(UPDATE_ACTION.RemoveDataSeries);
		this.setTargetDataSeriesIndex(dataSeriesIndex);
	}

	/**
	 * Sets the specified time series.
	 * @param specifiedTimeSeries the new specified time series
	 */
	public void setSpecifiedTimeSeries(TimeSeries specifiedTimeSeries) {
		this.specifiedTimeSeries = specifiedTimeSeries;
	}
	/**
	 * Gets the specified time series.
	 * @return the specified time series
	 */
	public TimeSeries getSpecifiedTimeSeries() {
		return specifiedTimeSeries;
	}
	
	
	/**
	 * Edits the time series: adds new data to a time series.
	 *
	 * @param dataSeriesIndex the data series index
	 * @param timeSeriesData2Add the time series data2 add
	 */
	public void editTimeSeriesAddTimeSeriesData(int dataSeriesIndex, TimeSeries timeSeriesData2Add) {
		this.setTargetAction(UPDATE_ACTION.EditDataSeriesAddData);
		this.setTargetDataSeriesIndex(dataSeriesIndex);
		this.setSpecifiedTimeSeries(timeSeriesData2Add);
	}
	
	/**
	 * Edits the time series: adds or exchanges time series data.
	 *
	 * @param dataSeriesIndex the data series index
	 * @param timeSeriesData2AddOrExchange the time series data2 add or exchange
	 */
	public void editTimeSeriesAddOrExchangeTimeSeriesData(int dataSeriesIndex, TimeSeries timeSeriesData2AddOrExchange) {
		this.setTargetAction(UPDATE_ACTION.EditDataSeriesAddOrExchangeData);
		this.setTargetDataSeriesIndex(dataSeriesIndex);
		this.setSpecifiedTimeSeries(timeSeriesData2AddOrExchange);
	}
	/**
	 * Edits the time series: exchanges time series data. If the specified timestamps 
	 * can not be found, nothing will be done.
	 *
	 * @param dataSeriesIndex the data series index
	 * @param timeSeriesData2AddOrExchange the time series data2 add or exchange
	 */
	public void editTimeSeriesExchangeTimeSeriesData(int dataSeriesIndex, TimeSeries timeSeriesData2AddOrExchange) {
		this.setTargetAction(UPDATE_ACTION.EditDataSeriesExchangeData);
		this.setTargetDataSeriesIndex(dataSeriesIndex);
		this.setSpecifiedTimeSeries(timeSeriesData2AddOrExchange);
	}
	
	/**
	 * Edits the time series: removes time series data specified by the timestamps.
	 *
	 * @param dataSeriesIndex the data series index
	 * @param timeSeriesData2Remove the time series data2 remove
	 */
	public void editTimeSeriesRemoveTimeSeriesData(int dataSeriesIndex, TimeSeries timeSeriesData2Remove) {
		this.setTargetAction(UPDATE_ACTION.EditDataSeriesRemoveData);
		this.setTargetDataSeriesIndex(dataSeriesIndex);
		this.setSpecifiedTimeSeries(timeSeriesData2Remove);
	}

	

	/* (non-Javadoc)
	 * @see agentgui.envModel.graph.visualisation.notifications.UpdateDataSeries#applyToNetworkModelOnly(agentgui.envModel.graph.networkModel.NetworkModel)
	 */
	@Override
	public void applyToNetworkModelOnly(NetworkModel networkModel) throws UpdateDataSeriesException {

		String compID = this.getComponentID();
		String compType = null;
		Object dataModel = null;
		switch (this.getComponentType()) {
		case GraphNode:
			compType = "GraphNode";
			GraphNode node =(GraphNode)networkModel.getGraphElement(compID);
			if (node==null) {
				throw new UpdateDataSeriesException("GraphNode '" + compID + "' could not be found in the NetworkModel!");
			}
			dataModel = node.getDataModel();
			break;
		case NetworkComponent:
			compType = "NetworkComponent";
			NetworkComponent networkComponent = networkModel.getNetworkComponent(compID);
			if (networkComponent==null) {
				throw new UpdateDataSeriesException("NetworkComponent '" + compID + "' could not be found in the NetworkModel!");
			}
			dataModel = networkComponent.getDataModel();
			break;
		} 

		if (dataModel==null) {
			throw new UpdateDataSeriesException("NullPointerException: The data model of " + compType + " '" + compID + "' is null!");
		}
		
		// --- Get the right data model part -------------------- 
		try {
			Object[] objectArr = (Object[]) dataModel; 
			TimeSeriesChart tsc = (TimeSeriesChart) objectArr[this.getTargetDataModelIndex()];
			this.applyUpdate(tsc);
			
		} catch (Exception ex) {
			throw new UpdateDataSeriesException(this.getTargetDataModelIndex(), compType, compID, ex);
		}
		
	}
	
	/**
	 * Apply the update configured in this class.
	 * @param timeSeriesChart the actual time series chart
	 */
	private void applyUpdate(TimeSeriesChart timeSeriesChart) {
		
		switch (this.getTargetAction()) {
		case AddDataSeries:
			timeSeriesChart.getTimeSeriesChartData().add(this.getSpecifiedTimeSeries());
			break;
			
		case AddOrExchangeDataSeries:
			if (this.getTargetDataSeriesIndex()<=timeSeriesChart.getTimeSeriesChartData().size()-1) {
				timeSeriesChart.getTimeSeriesChartData().remove(this.getTargetDataSeriesIndex());	
				timeSeriesChart.getTimeSeriesChartData().add(this.getTargetDataSeriesIndex(), this.getSpecifiedTimeSeries());
			} else {
				timeSeriesChart.getTimeSeriesChartData().add(this.getSpecifiedTimeSeries());
			}
			break;

		case ExchangeDataSeries:
			if (this.getTargetDataSeriesIndex()<=timeSeriesChart.getTimeSeriesChartData().size()-1) {
				timeSeriesChart.getTimeSeriesChartData().remove(this.getTargetDataSeriesIndex());
				timeSeriesChart.getTimeSeriesChartData().add(this.getTargetDataSeriesIndex(), this.getSpecifiedTimeSeries());
			} 
			break;
			
		case RemoveDataSeries:
			if (this.getTargetDataSeriesIndex()<=timeSeriesChart.getTimeSeriesChartData().size()-1) {
				timeSeriesChart.getTimeSeriesChartData().remove(this.getTargetDataSeriesIndex());	
			}
			break;

		// ------------------------------------------------	
		// --- From here: Edits of single TimeSeries ------
		// ------------------------------------------------
		case EditDataSeriesAddData:
			
			break;

		case EditDataSeriesAddOrExchangeData:
			
			break;
			
		case EditDataSeriesRemoveData:
	
			break;
		}

	}

	/* (non-Javadoc)
	 * @see agentgui.envModel.graph.visualisation.notifications.UpdateDataSeries#applyToOntologyInstanceViewer(agentgui.core.ontologies.gui.OntologyInstanceViewer)
	 */
	@Override
	public void applyToOntologyInstanceViewer(OntologyInstanceViewer ontologyInstanceViewer) throws UpdateDataSeriesException {
		
		Vector<OntologyClassEditorJPanel> ontoVisPanel = ontologyInstanceViewer.getOntologyClassEditorJPanel(this.getTargetDataModelIndex());
		if (ontoVisPanel==null || ontoVisPanel.size()==0) {
			// ----------------------------------------------------------------
			// --- Apply just to the data model -------------------------------
			// ----------------------------------------------------------------
			Object[] objectArr = ontologyInstanceViewer.getConfigurationInstances();
			TimeSeriesChart tsc = (TimeSeriesChart) objectArr[this.getTargetDataModelIndex()];
			this.applyUpdate(tsc);
			
		} else {
			// ----------------------------------------------------------------
			// --- Apply to the visualisation ---------------------------------
			// ----------------------------------------------------------------
			for (int i = 0; i < ontoVisPanel.size(); i++) {
				
				DataModel dataModelTimeSeries = null;
				OntologyClassEditorJPanel ontoVisPanelSingle = ontoVisPanel.get(i);
				// --- Get the data model of the chart --------
				if (ontoVisPanelSingle instanceof TimeSeriesChartEditorJPanel) {
					TimeSeriesChartEditorJPanel tscep = (TimeSeriesChartEditorJPanel) ontoVisPanelSingle;
					dataModelTimeSeries = tscep.getModel();
					
				} else if (ontoVisPanelSingle instanceof TimeSeriesWidget) {
					TimeSeriesWidget tsw = (TimeSeriesWidget) ontoVisPanelSingle;
					TimeSeriesChartEditorJPanel tscep = (TimeSeriesChartEditorJPanel) tsw.getTimeSeriesChartEditorJDialog().getContentPane();
					dataModelTimeSeries = tscep.getModel();
				}
				
				// --- Apply Changes --------------------------
				switch (this.getTargetAction()) {
				case AddDataSeries:
					dataModelTimeSeries.addSeries(this.getSpecifiedTimeSeries());
					break;
					
				case AddOrExchangeDataSeries:
					//TODO
					//dataModelTimeSeries.
					break;

				case ExchangeDataSeries:
					//TODO					
					break;

				case RemoveDataSeries:
					try {
						if (this.getTargetDataSeriesIndex()<=dataModelTimeSeries.getSeriesCount()-1) {
							dataModelTimeSeries.removeSeries(this.getTargetDataSeriesIndex());	
						}
						
					} catch (NoSuchSeriesException nsse) {
						nsse.printStackTrace();
					}
					break;
					
				// ------------------------------------------------	
				// --- From here: Edits of single TimeSeries ------
				// ------------------------------------------------
				case EditDataSeriesAddData:
					
					break;

				case EditDataSeriesAddOrExchangeData:
					
					break;
					
				case EditDataSeriesRemoveData:
			
					break;
					
				} // end switch
			} // end apply to visualisation
		}
		
	} // end method
	
	
}
