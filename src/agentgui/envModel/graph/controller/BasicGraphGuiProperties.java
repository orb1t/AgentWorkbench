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
package agentgui.envModel.graph.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import agentgui.core.application.Language;
import agentgui.core.ontologies.gui.OntologyInstanceViewer;
import agentgui.envModel.graph.GraphGlobals;
import agentgui.envModel.graph.networkModel.GraphEdge;
import agentgui.envModel.graph.networkModel.GraphNode;
import agentgui.envModel.graph.networkModel.NetworkComponent;
import agentgui.envModel.graph.networkModel.NetworkComponentAdapter;
import agentgui.envModel.graph.networkModel.NetworkComponentAdapter4DataModel;
import agentgui.envModel.graph.networkModel.NetworkModelNotification;
import agentgui.envModel.graph.visualisation.notifications.DataModelNotification;
import agentgui.envModel.graph.visualisation.notifications.DisplayAgentNotificationGraph;
import agentgui.envModel.graph.visualisation.notifications.DisplayAgentNotificationGraphMultiple;

/**
 * The Class BasicGraphGuiProperties is used as dialog in order to configure
 * properties of {@link NetworkComponent}'s or {@link GraphNode}'s. Therefore, this 
 * Dialog searches first for the corresponding {@link NetworkComponentAdapter}.
 * As a second step it reads the data model instance from the GraphNode or 
 * NetworkComponent and passes this instance to the component for visualising.
 * In the end the changed instance will be transfered back to the specific
 * GraphNode or NetworkComponent.
 * 
 * @see NetworkComponent
 * @see NetworkComponent#getDataModel()
 * @see GraphNode
 * @see GraphNode#getDataModel()
 * @see NetworkComponentAdapter
 * @see NetworkComponentAdapter4DataModel
 * @see NetworkComponentAdapter4DataModel#getVisualisationComponent()
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class BasicGraphGuiProperties extends BasicGraphGuiJInternalFrame implements Observer, ActionListener {

	private static final long serialVersionUID = -868257113588339559L;

	private final String pathImage = GraphGlobals.getPathImages();
	
	private int defaultWidth = 300;
	private int defaultHeight= 450;
	
	private Object graphObject = null;  //  @jve:decl-index=0:
	private GraphNode graphNode = null;
	private NetworkComponent networkComponent = null;
	private NetworkComponentAdapter networkComponentAdapter = null;
	private NetworkComponentAdapter4DataModel adapter4DataModel = null;
	
	private JPanel jContentPane = null;  
	private JToolBar jJToolBarBarNorth = null;
	private JToolBarButton jToolBarButtonSave = null;
	private JToolBarButton jToolBarButtonSaveAndExit = null;
	private JComponent jComponentContent = null;

	private Vector<Integer> dataModelBase64InitialHashCodes = null;
	
	/**
	 * Instantiates a new properties dialog for GraphNodes or NetworkComponents.
	 * @param graphController the graph controller
	 */
	public BasicGraphGuiProperties(GraphEnvironmentController graphController, BasicGraphGuiJDesktopPane desktop, Object graphObject) {
		super(graphController);
		this.graphController.addObserver(this);
		this.graphObject = graphObject;
		this.initialize();
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		
		this.setAutoscrolls(true);
		this.setMaximizable(true);
		this.setResizable(true);
		this.setIconifiable(true);
		
		this.setClosable(true);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		final JInternalFrame thisFrame = this;
		this.addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameClosing(InternalFrameEvent ife) {
				
				if (hasChanged()==true) {
					// --- Data model has changed ! --------
					String diaTitle = Language.translate("Close Properties", Language.EN);
					String diaQuestion = Language.translate("Save Changes to network model?", Language.EN);
					int diaAnswer = JOptionPane.showInternalConfirmDialog(thisFrame, diaQuestion, diaTitle, JOptionPane.YES_NO_CANCEL_OPTION);
					if (diaAnswer==JOptionPane.YES_OPTION) {
						save();
						thisFrame.setVisible(false);
						thisFrame.dispose();
						
					} else if (diaAnswer==JOptionPane.NO_OPTION){
						thisFrame.setVisible(false);
						thisFrame.dispose();
						
					} else if (diaAnswer==JOptionPane.CANCEL_OPTION){
						// --- Do nothing ---- 
					}
					
				} else {
					// --- Data model has NOT changed ! ---
					thisFrame.setVisible(false);
					thisFrame.dispose();
				
				}
				
			}
		});

		
		this.setTitle("Component");
		this.setSize(this.defaultWidth, this.defaultHeight);
		this.setInitialSize();
		
		BasicInternalFrameUI ui = (BasicInternalFrameUI)this.getUI();
		ui.getNorthPane().remove(0);
		
		this.configureForGraphObject();
		this.setContentPane(getJContentPane());
		
		// --- Call to the super-class ----------
		this.registerAtDesktopAndSetVisible();
		
	}
	
	/**
	 * Sets the initial size, if the frame opens.
	 */
	private void setInitialSize() {

		// --- Configure the size of the frame ------------
		if (this.graphDesktop!=null) {
			if (this.graphDesktop.getLastOpenedEditor()==null) {
				Dimension desktopSize = this.graphDesktop.getSize();
				Dimension newSize = new Dimension(this.defaultWidth, (int) (desktopSize.getHeight()*(2.0/3.0)));
				this.setSize(newSize);
			} else {
				this.setSize(this.graphDesktop.getLastOpenedEditor().getSize());
			}
		} else {
			this.setSize(new Dimension(this.defaultWidth, this.defaultHeight));
		}
		// --- Set also the preferred size ----------------
		this.setPreferredSize(this.getSize());
	}
	
	/**
	 * Returns the graph object.
	 * @return the graphObject
	 */
	public Object getGraphObject() {
		return graphObject;
	}
	/**
	 * Sets the graph object.
	 * @param graphObject the graphObject to set
	 */
	public void setGraphObject(Object graphObject) {
		this.graphObject = graphObject;
	}
	
	/**
	 * Configure for graph object.
	 */
	private void configureForGraphObject() {

		String title2Set = null;
		if (this.getGraphObject()==null) {
			title2Set = "No valid selection!";
			
		} else if (this.getGraphObject() instanceof GraphNode) {
			// --- Set the local variable ---------------------------
			this.graphNode = (GraphNode) this.getGraphObject();
			// --- Get the corresponding NetworkComponentAdapter ----			
			this.networkComponentAdapter = this.graphController.getNetworkModel().getNetworkComponentAdapter(this.graphController, (GraphNode) this.getGraphObject());
			title2Set = "Vertex: " + this.graphNode.getId();
			
		} else if (this.getGraphObject() instanceof GraphEdge) {
			// --- Just get the corresponding NetworkComponent ------ 
			this.networkComponent = this.graphController.getNetworkModel().getNetworkComponent((GraphEdge) this.getGraphObject());
			// --- Get the corresponding NetworkComponentAdapter ---- 
			this.networkComponentAdapter = this.graphController.getNetworkModel().getNetworkComponentAdapter(this.graphController, this.networkComponent);
			title2Set = "Comp.: " + this.networkComponent.getId() + " (" +  this.networkComponent.getType() + ")";
			
		} else if (this.getGraphObject() instanceof NetworkComponent) {
			// --- Cast to NetworkComponent -------------------------
			this.networkComponent = this.graphController.getNetworkModel().getNetworkComponent(((NetworkComponent) this.getGraphObject()).getId());
			// --- Get the corresponding NetworkComponentAdapter ---- 
			this.networkComponentAdapter = this.graphController.getNetworkModel().getNetworkComponentAdapter(this.graphController, this.networkComponent);
			title2Set = "Comp.: " + this.networkComponent.getId() + " (" +  this.networkComponent.getType() + ")";
		}

		if (this.networkComponent!=null) {
			// --- Mark / Select NetworkComponent for user --------------
			NetworkModelNotification nmn = new NetworkModelNotification(NetworkModelNotification.NETWORK_MODEL_Component_Select);
			nmn.setInfoObject(this.networkComponent);
			this.graphController.notifyObservers(nmn);
		}

		// --- Some layout stuff ----------------------------------- 
		if (title2Set!=null) {
			this.setTitle(title2Set);	
		}
		
	}
	
	/**
	 * This method initializes jPanelContent	
	 * @return javax.swing.JPanel	
	 */
	private JComponent getJPanelContent() {
		if (this.jComponentContent==null) {
			
			if (this.networkComponentAdapter==null) {
				this.getJToolBarButtonSave().setEnabled(false);
				this.getJToolBarButtonSaveAndExit().setEnabled(false);

				String displayText = null;
				if (this.networkComponent!=null) {
					displayText = "<html><center>" + Language.translate("No NetworkComponentAdapter\nwas defined for the\n NetworkComponent", Language.EN) + " " + this.networkComponent.getId() + " (" +  this.networkComponent.getType() + ")!<br><br> </center></html>";
				} else if (this.graphNode!=null) {
					String domain = this.graphController.getNetworkModel().getDomain(this.graphNode); 
					displayText = "<html><center>" + Language.translate("No NetworkComponentAdapter\nwas defined for\n the GraphNodes of the Domain", Language.EN) + " '" + domain + "'!<br><br> </center></html>";
				}
				
				JLabel jLabelNoAdapter = new JLabel();
				jLabelNoAdapter.setText(displayText);
				jLabelNoAdapter.setFont(new Font("Dialog", Font.BOLD, 12));
				jLabelNoAdapter.setHorizontalAlignment(JLabel.CENTER);
				jLabelNoAdapter.setSize(new Dimension(200, 260));
				
				this.jComponentContent = jLabelNoAdapter;
				
			} else {
				this.adapter4DataModel = this.networkComponentAdapter.getNewDataModelAdapter();
				if (this.adapter4DataModel == null) {
					if (this.graphNode!=null) {
						this.graphNode.setDataModel(null);
						this.graphNode.setDataModelBase64(null);
					} else {
						this.networkComponent.setDataModel(null);
						this.networkComponent.setDataModelBase64(null);	
					}
					// --- Disable save-actions ---------------------
					this.getJToolBarButtonSave().setEnabled(false);
					this.getJToolBarButtonSaveAndExit().setEnabled(false);
					
				} else {
					Object dataModel = null;
					Vector<String> dataModelBase64 = null;
					// --- Get the Base64 encoded Vector<String> ---- 
					if (this.graphNode!=null) {
						dataModel = this.graphNode.getDataModel();
						dataModelBase64 = this.graphNode.getDataModelBase64();
					} else {
						dataModel = this.networkComponent.getDataModel();
						dataModelBase64 = this.networkComponent.getDataModelBase64();	
					}
					
					if (dataModel==null && dataModelBase64!=null) {
    					// --- Convert Base64 decoded Object --------
    					dataModel = this.adapter4DataModel.getDataModelBase64Decoded(dataModelBase64);
    					if (this.graphNode!=null) {
    						this.graphNode.setDataModel(dataModel);
    					} else {
    						this.networkComponent.setDataModel(dataModel);
    					}
	    			}
					if (this.graphNode!=null) {
						this.adapter4DataModel.setDataModel(dataModel);
					} else {
						this.adapter4DataModel.setDataModel(dataModel);
					}
					// --- Remind the initial HashCodes of the Base64 data model vector ------
					if (dataModelBase64!=null) {
						this.dataModelBase64InitialHashCodes = new Vector<Integer>();
						for (int i=0; i < dataModelBase64.size(); i++) {
							String singleDataModel = dataModelBase64.get(i);
							int singleDataModelHashCode = singleDataModel.hashCode();
							this.dataModelBase64InitialHashCodes.add(singleDataModelHashCode);	
						}
					}
					// --- Get the visualization component -----------------------------------
					JComponent visualisation = this.adapter4DataModel.getVisualisationComponent();
					if (visualisation instanceof OntologyInstanceViewer) {
						((OntologyInstanceViewer)visualisation).setJToolBar4UserFunctions(this.getJJToolBarBarNorth());
					}
					visualisation.validate();
					this.jComponentContent = visualisation;
				}
			}
		}
		return this.jComponentContent;
	}
	
	/**
	 * This method initializes jContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(this.getJJToolBarBarNorth(), BorderLayout.NORTH);
			JComponent dataContent = this.getJPanelContent();
			if (dataContent!=null) {
				jContentPane.add(dataContent, BorderLayout.CENTER);	
			}
		}
		return jContentPane;
	}
	
	/**
	 * This method initializes jJToolBarBarNorth	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJJToolBarBarNorth() {
		if (jJToolBarBarNorth == null) {
			jJToolBarBarNorth = new JToolBar("Properties Bar");
			jJToolBarBarNorth.setFloatable(false);
			jJToolBarBarNorth.setRollover(true);
			jJToolBarBarNorth.add(this.getJToolBarButtonSave());
			jJToolBarBarNorth.add(this.getJToolBarButtonSaveAndExit());
		}
		return jJToolBarBarNorth;
	}
	
	/**
	 * Returns the JToolBarButton for the save action.
	 * @return the JToolBarButton for the save action
	 */
	private JToolBarButton getJToolBarButtonSave() {
		if (this.jToolBarButtonSave==null) {
			this.jToolBarButtonSave=new JToolBarButton("Save", Language.translate("Save", Language.EN), null, "MBsave.png", this);
		}
		return this.jToolBarButtonSave;
	}
	
	/**
	 * Returns the JToolBarButton for the save and exit action.
	 * @return the JToolBarButton for the save and exit action
	 */
	private JToolBarButton getJToolBarButtonSaveAndExit() {
		if (this.jToolBarButtonSaveAndExit==null) {
			this.jToolBarButtonSaveAndExit=new JToolBarButton("SaveAndExit", Language.translate("Save and Exit", Language.EN), null, "MBsaveAndExit.png", this);
		}
		return this.jToolBarButtonSaveAndExit;
	}
	
	/**
	 * Checks if the current settings have changed.
	 * @return true, if the data model was changed
	 */
	private boolean hasChanged() {
		
		boolean changed = false;

		if (this.adapter4DataModel==null) return false;
		
		this.adapter4DataModel.save();
		Object dataModel = this.adapter4DataModel.getDataModel();
		Vector<String> dataModelBase64 = this.adapter4DataModel.getDataModelBase64Encoded(dataModel);
		
		if (dataModelBase64==null && this.dataModelBase64InitialHashCodes==null) {
			changed = false;
			
		} else if (dataModelBase64==null && this.dataModelBase64InitialHashCodes!=null) {
			changed = true;
			
		} else {
			for (int i = 0; i < dataModelBase64.size(); i++) {
				String singleDataModel = dataModelBase64.get(i);
				int singleDataModelHashCode = singleDataModel.hashCode();
				if (this.dataModelBase64InitialHashCodes!=null && this.dataModelBase64InitialHashCodes.size()==dataModelBase64.size()) {
					if (singleDataModelHashCode!=this.dataModelBase64InitialHashCodes.get(i)) {
						changed=true;
						break;
					}
				} else {
					changed = true;
					break;
				}
			}
			
		}
		return changed;
	}
	
	/**
	 * Saves the current settings.
	 */
	private void save() {
		
		this.adapter4DataModel.save();
		
		Object dataModel = this.adapter4DataModel.getDataModel();
		Vector<String> dataModelBase64 = this.adapter4DataModel.getDataModelBase64Encoded(dataModel);

		if (this.graphNode!=null) {
			this.graphNode.setDataModel(dataModel);
			this.graphNode.setDataModelBase64(dataModelBase64);

			GraphNode modelGraphNode = (GraphNode) this.graphController.getNetworkModel().getGraphElement(this.graphNode.getId());
			modelGraphNode.setDataModel(dataModel);
			modelGraphNode.setDataModelBase64(dataModelBase64);
			
		} else {
			this.networkComponent.setDataModel(dataModel);
			this.networkComponent.setDataModelBase64(dataModelBase64);

			NetworkComponent modelNetworkComponent = this.graphController.getNetworkModel().getNetworkComponent(this.networkComponent.getId()); 
			modelNetworkComponent.setDataModel(dataModel);
			modelNetworkComponent.setDataModelBase64(dataModelBase64);

		}
		this.graphController.setProjectUnsaved();
		
	}
	
	/**
	 * The Class JToolBarButton.
	 */
	public class JToolBarButton extends JButton {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
 
		/**
		 * Instantiates a new j tool bar button.
		 *
		 * @param actionCommand the action command
		 * @param toolTipText the tool tip text
		 * @param altText the alt text
		 * @param imgName the image name
		 * @param actionListener the ActionListener
		 */
		private JToolBarButton(String actionCommand, String toolTipText, String altText, String imgName, ActionListener actionListener) {
				
			this.setText(altText);
			this.setToolTipText(toolTipText);
			this.setSize(36, 36);
			
			if (imgName!=null) {
				this.setPreferredSize(new Dimension(26,26));
			} else {
				this.setPreferredSize(null);	
			}

			if (imgName!=null) {
				try {
					ImageIcon imageIcon = new ImageIcon( this.getClass().getResource(pathImage + imgName), altText);
					this.setIcon(imageIcon);
					
				} catch (Exception err) {
					System.err.println(Language.translate("Fehler beim Laden des Bildes: ") + err.getMessage());
				}				
			}
			this.addActionListener(actionListener);	
			this.setActionCommand(actionCommand);
		}
		
	}
	
	/**
	 * Sets the display agent notification graph.
	 */
	private void setDisplayAgentNotificationGraph(DisplayAgentNotificationGraph displayAgentNotificationGraph) {
		
		if (displayAgentNotificationGraph instanceof DataModelNotification) {
			// --- DataModelNotification: Is that mine? -------------
			DataModelNotification dmn = (DataModelNotification) displayAgentNotificationGraph;
			if (this.graphNode!=null && dmn.isGraphNodeConfiguration()==true) {
				// -- Update the model of the current GraphNode ? -------------
				if (dmn.getGraphNode().getId().equals(this.graphNode.getId())) {
					if (dmn.isUseDataModelBase64Encoded()==true) {
						this.adapter4DataModel.getDataModelBase64Decoded(dmn.getGraphNode().getDataModelBase64());
					} else {
						this.adapter4DataModel.setDataModel(dmn.getGraphNode());	
					}
				}// end current GraphNode
			}// end GraphNode
			
			if (this.networkComponent!=null && dmn.isNetworkComponentConfiguration()==true) {
				// -- Update the model of the current NetworkComponent ? ------
				if (dmn.getNetworkComponent().getId().equals(this.networkComponent.getId())) {
					if (dmn.isUseDataModelBase64Encoded()==true) {
						this.adapter4DataModel.getDataModelBase64Decoded(dmn.getNetworkComponent().getDataModelBase64());
					} else {
						this.adapter4DataModel.setDataModel(dmn.getNetworkComponent());
					}
				} // end current NetworkComponent  
			}// end NetworkComponent
			
		}// end DisplayAgentNotificationGraph
		
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object object) {
		
		if (object instanceof NetworkModelNotification) {
			NetworkModelNotification nmn = (NetworkModelNotification) object;
			if (nmn.getReason()==NetworkModelNotification.NETWORK_MODEL_Repaint) {
				// --- Repaint graph: also a data model update? -----
				if (nmn.getInfoObject()==null) {
					// --- Nothing to do here -----------------------
					
				} else if (nmn.getInfoObject() instanceof DisplayAgentNotificationGraph) {
					// --- Got a DisplayAgentNotificationGraph ------
					DisplayAgentNotificationGraph dang = (DisplayAgentNotificationGraph) nmn.getInfoObject();
					// --- Single or multiple notification ? --------
					if (dang instanceof DisplayAgentNotificationGraphMultiple) {
						DisplayAgentNotificationGraphMultiple dangMultiple = (DisplayAgentNotificationGraphMultiple) dang;
						for (int i = 0; i < dangMultiple.getDisplayNotifications().size(); i++) {
							// --- Work on a single notification ----
							this.setDisplayAgentNotificationGraph(dangMultiple.getDisplayNotifications().get(i));
						}
						
					} else {
						// --- Work on a single notification --------
						this.setDisplayAgentNotificationGraph(dang);
					}

				} // end InfoObject
				
			}// end NetworkModelNotification.NETWORK_MODEL_Repaint
		} // end NetworkModelNotification
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		String actionCommand = ae.getActionCommand();
		if (actionCommand.equals("Save") || actionCommand.equals("SaveAndExit")) {
			this.save();
		}
		if (actionCommand.equals("SaveAndExit")) {
			this.setVisible(false);
			this.dispose();
		}
		
	}
	
}
