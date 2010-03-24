/**
 * @author Hanno - Felix Wagner Copyright 2010 Hanno - Felix Wagner This file is
 *         part of ContMAS. ContMAS is free software: you can redistribute it
 *         and/or modify it under the terms of the GNU Lesser General Public
 *         License as published by the Free Software Foundation, either version
 *         3 of the License, or (at your option) any later version. ContMAS is
 *         distributed in the hope that it will be useful, but WITHOUT ANY
 *         WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *         FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 *         License for more details. You should have received a copy of the GNU
 *         Lesser General Public License along with ContMAS. If not, see
 *         <http://www.gnu.org/licenses/>.
 */

package contmas.main;

import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ActionMap;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.plaf.ActionMapUIResource;

public class ContMASContainer extends JFrame{

	private static final long serialVersionUID=1L;
	private JPanel jContentPane=null;
	private JDesktopPane jDesktopPane=null;

	/**
	 * This is the default constructor
	 */
	public ContMASContainer(){
		super();
		this.initialize();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane(){
		if(this.jContentPane == null){
			this.jContentPane=new JPanel();
			this.jContentPane.setLayout(null);
			this.jContentPane.add(this.getJDesktopPane(),null);
		}
		return this.jContentPane;

	}

	/**
	 * This method initializes jDesktopPane
	 * 
	 * @return javax.swing.JDesktopPane
	 */
	public JDesktopPane getJDesktopPane(){
		if(this.jDesktopPane == null){
			ActionMap actionMap=new ActionMap();
			actionMap.setParent(new ActionMapUIResource());
			this.jDesktopPane=new JDesktopPane();
			this.jDesktopPane.setBounds(new Rectangle(0,0,307,294));
			this.jDesktopPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			this.jDesktopPane.setActionMap(actionMap);
			this.jDesktopPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		}
		return this.jDesktopPane;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(){
		this.setSize(315,322);
		this.setFont(new Font("Dialog",Font.PLAIN,12));
		this.setContentPane(this.getJContentPane());
		this.setTitle("ContMAS");
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				ContMASContainer.this.shutDown();
			}
		});

	}

	void shutDown(){
		// -----------------  Control the closing of this gui

		System.exit(0);
	}

} //  @jve:decl-index=0:visual-constraint="30,15"