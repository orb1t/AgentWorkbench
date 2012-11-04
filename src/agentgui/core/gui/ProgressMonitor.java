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
package agentgui.core.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import agentgui.core.application.Application;
import agentgui.core.application.Language;

/**
 * The Class AgentGuiUpdaterProgress.
 */
public class ProgressMonitor extends JDialog implements ActionListener {

	private static final long serialVersionUID = -5215083407952353606L;

	private final String pathImage = Application.getGlobalInfo().PathImageIntern();
	private final ImageIcon iconAgentGUI = new ImageIcon( this.getClass().getResource( pathImage + "AgentGUI.png") );
	private final Image imageAgentGUI = iconAgentGUI.getImage();
	
	private JPanel jContentPane = null;
	private JLabel jLabelHeader = null;
	private JPanel jPanelDummy = null;
	
	private JLabel jLabelProgress = null;
	private JProgressBar jProgressBarDownload = null;
	private JButton jButtonCancel = null;

	private Frame owner=null;
	
	private String windowTitle = "Agent.GUI - Progress";  //  @jve:decl-index=0:
	private String headerText = "Agent.GUI - Progress";
	private String progressText = "Download";  //  @jve:decl-index=0:
	
	private boolean allow2Cancel = true;
	private boolean canceled = false;
	
	
	/**
	 * Instantiates a new progress monitor.
	 *
	 * @param owner the owner
	 * @param windowTitle the window title
	 * @param headerText the header text
	 * @param progressText the progress text
	 */
	public ProgressMonitor(Frame owner, String windowTitle, String headerText, String progressText) {
		super(owner);
		this.owner = owner;
		this.windowTitle = windowTitle;
		this.headerText = headerText;
		this.progressText = progressText;
		initialize();
	}
	/**
	 * Instantiates a new progress monitor.
	 *
	 * @param windowTitle the window title
	 * @param headerText the header text
	 * @param progressText the progress text
	 */
	public ProgressMonitor(String windowTitle, String headerText, String progressText) {
		super();
		this.windowTitle = windowTitle;
		this.headerText = headerText;
		this.progressText = progressText;
		initialize();
	}
	
	/**
	 * This method initializes this.
	 * @return void
	 */
	private void initialize() {
		
		this.setSize(571, 188);
		this.setResizable(false);
		
		this.setTitle(this.windowTitle);
		this.setIconImage(imageAgentGUI);	
		this.setContentPane(getJContentPane());
		
		if (this.owner==null) {
			this.setLookAndFeel();	
		}
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
		
		// --- Centre download dialog -----------
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
		int top = (screenSize.height - this.getHeight()) / 2; 
	    int left = (screenSize.width - this.getWidth()) / 2; 
	    this.setLocation(left, top);
	    
		// --- Translations ---------------------
		jButtonCancel.setText(Language.translate(jButtonCancel.getText()));
		
	}

	/**
	 * Sets the window title.
	 * @param windowTitle the new window title
	 */
	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
		this.setTitle(this.windowTitle);
	}
	/**
	 * Sets the header text.
	 * @param headerText the new header text
	 */
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
		this.jLabelHeader.setText(headerText);
	}
	/**
	 * Sets the progress text.
	 * @param progressText the new progress text
	 */
	public void setProgressText(String progressText) {
		this.progressText = progressText;
		this.jLabelProgress.setText(progressText);
	}
	
	/**
	 * Can be used to set the progress in percent.
	 * @param progress the new progress in percent
	 */
	public void setProgress(int progress) {
		this.getJProgressBarDownload().setValue(progress);
		this.setTitle(this.windowTitle + " (" + progress + "%)");
	}
	
	/**
	 * Sets the permission to cancel an action via the progress dialog.
	 */
	public void setAllow2Cancel(boolean allow2Cancel) {
		this.allow2Cancel = allow2Cancel;
		if (this.allow2Cancel==true) {
			this.getJButtonCancel().setEnabled(true);
		} else {
			this.getJButtonCancel().setEnabled(false);
		}
	}
	/**
	 * Checks if this dialog was canceled.
	 * @return true, if is canceled
	 */
	public boolean isCanceled() {
		return this.canceled;
	}
	
	/**
	 * Sets the look and feel of the dialog similar to the core application window
	 */
	private void setLookAndFeel() {
		
		String lnfClassname = Application.getGlobalInfo().getAppLnF();
		try {
			if (lnfClassname == null) {
				lnfClassname = UIManager.getCrossPlatformLookAndFeelClassName();
			}	
			UIManager.setLookAndFeel(lnfClassname);
			SwingUtilities.updateComponentTreeUI(this);				
		
		} catch (Exception e) {
				System.err.println("Cannot install " + lnfClassname + " on this platform:" + e.getMessage());
		}		
	}
	
	/**
	 * This method initializes jContentPane.
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.gridy = 4;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.insets = new Insets(20, 0, 15, 0);
			gridBagConstraints3.gridy = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.insets = new Insets(0, 20, 5, 20);
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.insets = new Insets(0, 20, 0, 20);
			gridBagConstraints1.weighty = 0.0;
			gridBagConstraints1.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.insets = new Insets(15, 20, 10, 20);
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			
			jLabelHeader = new JLabel();
			jLabelHeader.setText(this.headerText);
			jLabelHeader.setFont(new Font("Dialog", Font.BOLD, 14));
			
			jLabelProgress = new JLabel();
			jLabelProgress.setText(this.progressText);
			jLabelProgress.setFont(new Font("Dialog", Font.BOLD, 12));
			
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(jLabelHeader, gridBagConstraints);
			jContentPane.add(getJProgressBarDownload(), gridBagConstraints1);
			jContentPane.add(jLabelProgress, gridBagConstraints2);
			jContentPane.add(getJButtonCancel(), gridBagConstraints3);
			jContentPane.add(getJPanelDummy(), gridBagConstraints11);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jProgressBarDownload.
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBarDownload() {
		if (jProgressBarDownload == null) {
			jProgressBarDownload = new JProgressBar();
			jProgressBarDownload.setPreferredSize(new Dimension(148, 26));
			jProgressBarDownload.setStringPainted(true);
			jProgressBarDownload.setMinimum(0);
			jProgressBarDownload.setMaximum(100);
		}
		return jProgressBarDownload;
	}

	/**
	 * This method initializes jButtonCancel.
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Abbruch");
			jButtonCancel.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonCancel.setPreferredSize(new Dimension(100, 26));
			jButtonCancel.addActionListener(this);
		}
		return jButtonCancel;
	}
	
	/**
	 * This method initializes jPanelDummy	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelDummy() {
		if (jPanelDummy == null) {
			jPanelDummy = new JPanel();
			jPanelDummy.setLayout(new GridBagLayout());
		}
		return jPanelDummy;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		if (ae.getSource()==this.getJButtonCancel()) {
			this.canceled=true;
			this.setVisible(false);
		}
		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
