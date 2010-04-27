package mas.display;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JButton;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.w3c.dom.Document;

import java.awt.BorderLayout;

/**
 * Basic GUI for displaying SVG graphics
 * JSVGCanvas on Scrollpane + Zoom buttons   
 * @author Nils
 *
 */
public class BasicSVGGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel pnlZoom = null;
	private JButton btnZoomIn = null;
	private JButton btnZoomOut = null;
	private JButton btnZoomReset = null;
	private JSVGCanvas canvas = null;
	private JSVGScrollPane scrollpane;

	/**
	 * This is the default constructor
	 */
	public BasicSVGGUI() {
		super();
		this.canvas = new JSVGCanvas();
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getPnlZoom(), BorderLayout.NORTH);
		this.add(getScrollpane(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes pnlZoom	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPnlZoom() {
		if (pnlZoom == null) {
			pnlZoom = new JPanel();
			pnlZoom.setLayout(new FlowLayout());
			pnlZoom.add(getBtnZoomIn(), null);
			pnlZoom.add(getBtnZoomOut(), null);
			pnlZoom.add(getBtnZoomReset(), null);
		}
		return pnlZoom;
	}

	/**
	 * This method initializes btnZoomIn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnZoomIn() {
		if (btnZoomIn == null) {
			btnZoomIn = new JButton();
			btnZoomIn.setText("+");
			btnZoomIn.addActionListener(canvas.new ZoomAction(1.2));
		}
		return btnZoomIn;
	}

	/**
	 * This method initializes btnZoomOut	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnZoomOut() {
		if (btnZoomOut == null) {
			btnZoomOut = new JButton();
			btnZoomOut.setText("-");
			btnZoomOut.addActionListener(canvas.new ZoomAction(0.8));
		}
		return btnZoomOut;
	}

	/**
	 * This method initializes btnZoomReset	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnZoomReset() {
		if (btnZoomReset == null) {
			btnZoomReset = new JButton();
			btnZoomReset.setText("100%");
			btnZoomReset.addActionListener(canvas.new ResetTransformAction());
		}
		return btnZoomReset;
	}
	
	private JSVGScrollPane getScrollpane(){
		if(scrollpane == null){
			scrollpane = new JSVGScrollPane(canvas);
		}
		return scrollpane;
	}
	
	public JSVGCanvas getCanvas(){
		return canvas;
	}
	
	public void setSVGDoc(Document doc){
		this.canvas.setDocument(doc);
	}
}