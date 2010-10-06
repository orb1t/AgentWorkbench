package gui.projectwindow.simsetup;

import javax.swing.JSplitPane;

import javax.swing.DropMode;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JTabbedPane;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import application.Language;
import application.Project;

import mas.display.BasicSVGGUI;
import mas.display.SvgTypes;
import mas.environment.EnvironmentController;
import mas.environment.ontology.ActiveObject;
import mas.environment.ontology.PassiveObject;
import mas.environment.ontology.Physical2DObject;
import mas.environment.ontology.PlaygroundObject;
import mas.environment.ontology.StaticObject;
import mas.environment.utils.SVGHelper;

import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

public class EnvironmentSetup extends JSplitPane implements ActionListener, Observer{

	private static final long serialVersionUID = 1L;
	private JSplitPane spControlls = null;
	private JTree treeEnvironment = null;
	private JTabbedPane tpSettings = null;
	private EnvironmentSetupObjectSettings objectSettings = null;
	private EnvironmentSetupEnvironmentSettings environmentSettings = null;
	private JFileChooser loadSVGDialog = null;
	
	Project project = null;
	
	private BasicSVGGUI svgGUI = null;
	/**
	 * The currently selected SVG element
	 */
	private Element selectedElement = null;
	
	/**
	 * Style properties used for highlighting the selected element
	 */
	private String selectionStyle = "stroke:orange;stroke-width:5px";
	/**
	 * Storing an elements original style attribute while changed for highlighting
	 */
	private String originalStyle = null;
	
	EnvironmentController controller = null;
	/**
	 * This is the default constructor
	 */
	public EnvironmentSetup(Project project) {
		super();
		this.project = project;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 400);
		this.setDividerLocation(200);

		this.setLeftComponent(getSpControlls());
		this.setRightComponent(getSvgGUI());
		
		this.controller = new EnvironmentController(project);
		controller.addObserver(this);
		if(controller.getSvgDoc() != null){
			setSVGDocument(controller.getSvgDoc());
		}
		if(controller.getEnvironment()!= null){
			rebuildTree();
			environmentSettings.setScale(controller.getEnvironment().getScale());
		}
	}

	/**
	 * This method initializes spControlls	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getSpControlls() {
		if (spControlls == null) {
			spControlls = new JSplitPane();
			spControlls.setOrientation(JSplitPane.VERTICAL_SPLIT);
			JScrollPane scpTree = new JScrollPane();
			scpTree.setViewportView(getTreeEnvironment());
			spControlls.setTopComponent(scpTree);
			spControlls.setBottomComponent(getTpSettings());
			spControlls.setDividerLocation(200);
		}
		return spControlls;
	}

	/**
	 * This method initializes treeEnvironment	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private JTree getTreeEnvironment() {
		if (treeEnvironment == null) {
			treeEnvironment = new JTree();
			DefaultMutableTreeNode dummyNode = new DefaultMutableTreeNode(Language.translate("Keine Umgebung definiert"));
			treeEnvironment.setModel(new DefaultTreeModel(dummyNode));
			treeEnvironment.addTreeSelectionListener(new TreeSelectionListener() {
				
				@Override
				public void valueChanged(TreeSelectionEvent e) {
					if(treeEnvironment.getLastSelectedPathComponent() != null){
						String selection = treeEnvironment.getLastSelectedPathComponent().toString();
						setSelectedElement(svgGUI.getCanvas().getSVGDocument().getElementById(selection));
					}
				}
			});
			
			treeEnvironment.setDragEnabled(true);
			treeEnvironment.setDropMode(DropMode.USE_SELECTION);
			treeEnvironment.setDropTarget(new DropTarget(treeEnvironment, TransferHandler.MOVE, new DropTargetAdapter(){

				@Override
				public void drop(DropTargetDropEvent dtde) {
					
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treeEnvironment.getSelectionPath().getLastPathComponent();
					String selectedObjectID = selectedNode.toString();
					Physical2DObject selectedObject = controller.getEnvWrap().getObjectById(selectedObjectID);
					if(selectedObject != null){
						// Get target TreePath 
						Point dropLocation = dtde.getLocation();
						TreePath targetPath = treeEnvironment.getClosestPathForLocation(dropLocation.x, dropLocation.y);
						DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();
						
						// Find the playground the target node belongs to
						boolean targetPgFound = false;
						Physical2DObject targetObject = null;
						while(targetNode != null && !targetPgFound){
							targetObject = controller.getEnvWrap().getObjectById(targetNode.toString());
							if(targetObject != null && targetObject instanceof PlaygroundObject){
								targetPgFound = true;
							}else{
								targetNode = (DefaultMutableTreeNode) targetNode.getParent();
							}
						}
						
						if(targetPgFound){
							dtde.dropComplete(controller.moveObjectToPlayground(selectedObjectID, targetObject.getId()));
							setSelectedElement(null);
						}else{
							dtde.dropComplete(false);
						}
					}else{
						dtde.dropComplete(false);
					}
					
					
				}
					
			}));
		}
		return treeEnvironment;
	}
	
	/**
	 * Rebuilding treeEnvironment's tree model
	 */
	private void rebuildTree(){
		if(controller.getEnvironment() != null){
			DefaultMutableTreeNode rootNode = getPlayground(controller.getEnvironment().getRootPlayground());
			getTreeEnvironment().setModel(new DefaultTreeModel(rootNode));
		}else{
			DefaultMutableTreeNode dummyNode = new DefaultMutableTreeNode(Language.translate("Keine Umgebung definiert"));
			getTreeEnvironment().setModel(new DefaultTreeModel(dummyNode));
		}
	}
	/**
	 * Creating a DefaultMutableTreeNoode representing a PlaygroundObject and it's child objects
	 * @param pg
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private DefaultMutableTreeNode getPlayground(PlaygroundObject pg){
		DefaultMutableTreeNode pgNode = null;
		DefaultMutableTreeNode agents = null;
		DefaultMutableTreeNode payloads = null;
		DefaultMutableTreeNode obstacles = null;
		DefaultMutableTreeNode playgrounds = null;
		
		if(pg != null){
			pgNode = new DefaultMutableTreeNode(pg.getId());
			Iterator<Physical2DObject> children = pg.getAllChildObjects();
			while(children.hasNext()){
				Physical2DObject child = children.next();
				if(child instanceof ActiveObject){
					if(agents == null){
						agents = new DefaultMutableTreeNode(Language.translate("Agenten"));
					}
					agents.add(new DefaultMutableTreeNode(child.getId()));
				}else if(child instanceof PassiveObject){
					if(payloads == null){
						payloads = new DefaultMutableTreeNode(Language.translate("Nutzlasten"));
					}
					payloads.add(new DefaultMutableTreeNode(child.getId()));
				}else if(child instanceof StaticObject){
					if(obstacles == null){
						obstacles = new DefaultMutableTreeNode(Language.translate("Hindernisse"));
					}
					obstacles.add(new DefaultMutableTreeNode(child.getId()));
				}else if(child instanceof PlaygroundObject){
					if(playgrounds == null){
						playgrounds = new DefaultMutableTreeNode(Language.translate("Spielfelder"));
					}
					playgrounds.add(getPlayground((PlaygroundObject) child));
				}
			}
			pgNode = new DefaultMutableTreeNode(pg.getId());
			if(agents != null){
				pgNode.add(agents);
			}
			if(payloads != null){
				pgNode.add(payloads);
			}
			if(obstacles != null){
				pgNode.add(obstacles);
			}
			if(playgrounds != null){
				pgNode.add(playgrounds);
			}
		}
		return pgNode;
	}

	/**
	 * This method initializes tpSettings	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getTpSettings() {
		if (tpSettings == null) {
			tpSettings = new JTabbedPane();
			this.environmentSettings = new EnvironmentSetupEnvironmentSettings(this);
			this.objectSettings = new EnvironmentSetupObjectSettings(this);
			tpSettings.addTab(Language.translate("Umgebung"), environmentSettings);
			tpSettings.addTab(Language.translate("Objekt"), objectSettings);
		}
		return tpSettings;
	}
	
	private BasicSVGGUI getSvgGUI(){
		if(svgGUI == null){
			svgGUI = new BasicSVGGUI();
			svgGUI.getCanvas().setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);
		}
		return svgGUI;
	}
	
	private JFileChooser getLoadSVGDialog(){
		if(loadSVGDialog == null){
			loadSVGDialog = new JFileChooser();
			loadSVGDialog.setFileFilter(new FileNameExtensionFilter(Language.translate("SVG-Dateien"), "svg"));
		}
		return loadSVGDialog;
	}
	
	private void setSVGDocument(Document doc){
		if(doc != null){
			Element svgRoot = doc.getDocumentElement();
			addEventListeners(svgRoot);
		}
		svgGUI.setSVGDoc(doc);
	}
	
	/**
	 * This method adds OnClick-Listeners to a SVG-element and it's child elements 
	 * @param root The element to add a Listener to
	 */
	private void addEventListeners(Node root){
		if(root instanceof Element
				&& (SvgTypes.getType((Element) root) != null)
				&& (((Element)root).getAttributeNS(null, "id") != "border")
		){
			((EventTarget)root).addEventListener("click", new EventListener(){

				@Override
				public void handleEvent(Event arg0) {
					setSelectedElement((Element) arg0.getTarget());					
				}
				
			}, true);
		}
		if(root.hasChildNodes()){
			NodeList children = root.getChildNodes();
			for(int i=0; i<children.getLength(); i++){
				addEventListeners(children.item(i));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == this.environmentSettings.getBtnLoadSVG()){
			if(getLoadSVGDialog().showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				controller.setSVGFile(loadSVGDialog.getSelectedFile());
			}
		}else if(arg0.getSource() == objectSettings.getBtnApply()){
			controller.createOrChange(objectSettings.getObjectProperties());
			changePosAndSize(selectedElement, 
					objectSettings.getTfXPos().getText(), 
					objectSettings.getTfYPos().getText(), 
					objectSettings.getTfWidth().getText(), 
					objectSettings.getTfHeight().getText()
			);
			setSelectedElement(null);
		}else if(arg0.getSource() == objectSettings.getBtnRemove()){
			controller.removeObject();
			setSelectedElement(null);
		}
	}
	/**
	 * @return the selectedElement
	 */
	Element getSelectedElement() {
		return selectedElement;
	}

	/**
	 * This method invokes an instance of ElementSelector, which changes the selected Element using the JSVGCanvas' update manager 
	 * @param elem
	 */
	private void setSelectedElement(Element elem){
		tpSettings.setSelectedIndex(1);
		UpdateManager um = this.svgGUI.getCanvas().getUpdateManager();
		
		um.getUpdateRunnableQueue().invokeLater(new ElementSelector(elem));
	}
	
	private void changePosAndSize(Element elem, String xPos, String yPos, String width, String height){
		UpdateManager um = this.svgGUI.getCanvas().getUpdateManager();
		um.getUpdateRunnableQueue().invokeLater(new SizeAndPosChanger(selectedElement, xPos, yPos, width, height));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof EnvironmentController){
			int eventCode = ((Integer)arg1).intValue();
			
			if(eventCode == EnvironmentController.ENVIRONMENT_CHANGED){
				System.out.println("Testausgabe: Ereignis ENVIRONMENT_CHANGED empfangen");
				
				rebuildTree();
				if(controller.getEnvironment() != null){
					environmentSettings.setScale(controller.getEnvironment().getScale());
				}
			}
			if(eventCode == EnvironmentController.SCALE_CHANGED){
				System.out.println("Testausgabe: Ereignis SCALE_CHANGED empfangen");
				environmentSettings.setScale(controller.getEnvironment().getScale());
			}
			if(eventCode == EnvironmentController.OBJECTS_CHANGED){
				System.out.println("Testausgabe: Ereignis OBJECTS_CHANGED empfangen");
				rebuildTree();
			}
			if(eventCode == EnvironmentController.SVG_CHANGED){
				System.out.println("Testausgabe: Ereignis SVG_CHANGED empfangen");
				setSVGDocument(controller.getSvgDoc());
			}
			if(eventCode < 0 || eventCode > 3){
				System.err.println(Language.translate("Unbekanntes Ereignis")+" "+eventCode);
			}
		}
	}
	/**
	 * Runnable for changing the selected element's style via the JSVGCanvas' update manager 
	 * @author Nils
	 */
	private class ElementSelector implements Runnable{
		
		Element element = null;
		
		protected ElementSelector(Element elem){
			element = elem;
		}

		@Override
		public void run() {
			if(selectedElement!=null){
				selectedElement.setAttributeNS(null, "style", originalStyle);
			}
			
			
			if(element!=null){
				String fill = "";
				originalStyle = element.getAttributeNS(null, "style");
				
				// Keep fill color if defined via style attribute
				if(originalStyle != null){
					String[] parts = originalStyle.split(";");
					for(int i=0; i<parts.length; i++){
						if(parts[i].contains("fill:")){
							fill = parts[i];
						}
					}
				}
				
				// Highlight selected element
				String newStyle=fill+";"+selectionStyle;
				element.setAttributeNS(null, "style", newStyle);
				objectSettings.enableControlls(true);
				controller.setSelectedObject(element.getAttributeNS(null, "id"));
				if(controller.getSelectedObject() != null){
					objectSettings.getBtnRemove().setEnabled(true);
				}else{
					objectSettings.getBtnRemove().setEnabled(false);
				}
			}else{
				objectSettings.enableControlls(false);
				objectSettings.getBtnRemove().setEnabled(false);
			}
			
			// Set selected element and input values
			selectedElement = element;
			objectSettings.setInputValues(element);
		}
	}
	/**
	 * Changing the selected elements position and size according to the current input values
	 * @author Nils
	 *
	 */
	class SizeAndPosChanger implements Runnable{
		
		private Element elem;
		private String xPos;
		private String yPos;
		private String width;
		private String height;
		
		public SizeAndPosChanger(Element elem, String xPos, String yPos, String width, String height){
			this.elem = elem;
			this.xPos = xPos;
			this.yPos = yPos;
			this.width = width;
			this.height = height;
		}

		@Override
		public void run() {
			SVGHelper.setSizeFromStrings(elem, controller.getEnvironment().getScale(), width, height);
			SVGHelper.setPosFromStrings(elem, controller.getEnvironment().getScale(), xPos, yPos, width, height);
		}
		
	}

}
