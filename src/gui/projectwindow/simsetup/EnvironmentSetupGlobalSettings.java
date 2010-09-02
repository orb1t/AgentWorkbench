package gui.projectwindow.simsetup;

import java.awt.Point;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Dimension;
import javax.swing.JLabel;

import mas.display.ontology.Scale;


import application.Language;

public class EnvironmentSetupGlobalSettings extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private EnvironmentSetup parent = null;

	private JLabel lblScale = null;
	private JTextField tfRwu = null;
	private JTextField tfPx = null;
	private JComboBox cbUnit = null;
	private JLabel lblPx = null;
	private JButton btnSetScale = null;
	private JButton btnLoadSVG = null;

	/**
	 * This is the default constructor
	 */
	public EnvironmentSetupGlobalSettings(EnvironmentSetup parent) {
		super();
		this.parent = parent;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		lblPx = new JLabel();
		lblPx.setLocation(new Point(70,80));
		lblPx.setText("Pixel");
		lblPx.setSize(lblPx.getPreferredSize());		
		lblScale = new JLabel();
		lblScale.setText(Language.translate("Ma�stab"));
		lblScale.setSize(lblScale.getPreferredSize());
		lblScale.setLocation(new Point(10, 10));
		setLayout(null);
		add(lblScale, null);
		add(getTfRwu(), null);
		add(getTfPx(), null);
		add(getCbUnit(), null);
		add(lblPx, null);
		add(getBtnSetScale(), null);
		add(getBtnLoadSVG(), null);
	}
	
	/**
	 * This method initializes tfRwu	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	JTextField getTfRwu() {
		if (tfRwu == null) {
			tfRwu = new JTextField();
			tfRwu.setLocation(new Point(10, 40));
			tfRwu.setSize(new Dimension(50, 25));			
		}
		return tfRwu;
	}

	/**
	 * This method initializes cbUnit	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	JComboBox getCbUnit() {
		if (cbUnit == null) {
			cbUnit = new JComboBox();
			cbUnit.setSize(90, 30);
			cbUnit.setLocation(new Point(70,40));
			String[] units = {"m", "cm", "mm", "inch", "feet"};
			cbUnit.setModel(new DefaultComboBoxModel(units));			
		}
		return cbUnit;
	}

	/**
	 * This method initializes tfPx	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	JTextField getTfPx() {
		if (tfPx == null) {
			tfPx = new JTextField();
			tfPx.setLocation(new Point(10, 75));
			tfPx.setSize(new Dimension(50, 25));			
		}
		return tfPx;
	}

	JButton getBtnSetScale(){
		if(btnSetScale == null){
			btnSetScale = new JButton();
			btnSetScale.setText(Language.translate("Ma�stab festlegen"));
			btnSetScale.setSize(new Dimension(150, 26));
			btnSetScale.setLocation(new Point(10,115));
			btnSetScale.addActionListener(parent);
		}
		return btnSetScale;
	}
	
	/**
	 * This method initializes btnLoadSVG	
	 * 	
	 * @return javax.swing.JButton	
	 */
	JButton getBtnLoadSVG() {
		if (btnLoadSVG == null) {
			btnLoadSVG = new JButton();
			btnLoadSVG.setText(Language.translate("SVG zuweisen"));
			btnLoadSVG.setSize(new Dimension(150, 26));
			btnLoadSVG.setLocation(new Point(10, 150));
			btnLoadSVG.addActionListener(parent);
		}
		return btnLoadSVG;
	}
	
	// Sets the scale inputs after the scale has been changed from
	public void setScale(Scale scale){
		getTfRwu().setText(""+scale.getValue());
		getCbUnit().setSelectedItem(scale.getUnit());
		getTfPx().setText(""+scale.getPixel());
	}

}