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
package agentgui.core.database;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JOptionPane;

import agentgui.core.application.Application;
import agentgui.core.application.Language;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSet;
import com.mysql.jdbc.Statement;

/**
 * This class is used within the execution mode as server.master for the
 * background system of Agent.GUI.<br>
 * In this mode the server.master agent will manage all server.slaves
 * and server.clients in a database table. 
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class DBConnection {
	
	private final String newLine = Application.getGlobalInfo().getNewLineSeparator();

	private String dbHost = null;
	private String dbName = null;
	private String dbUser = null;
	private String dbPswd = null;

	private Connection connection = null;
	private String sql = null;
	
	/** The Error object of this class */
	public Error dbError = new Error();
	/** Flag that shows if errors have occurred */
	public boolean hasErrors = false;
	
	
	/**
	 * Constructor of this class. Try's to connect to or build the database for the server.master agent
	 */
	public DBConnection() {
		
		// --- Can the connection be established? ---------
		if (this.connect()==false) {
			hasErrors=true;
			return;
		}
		// --- Is the database available? -----------------
		if (this.dbIsAvailable()==true) {
			this.setConnection2Database(this.dbName);
			// --- DB is there, check table 'platforms' ---
			if (isPlatformTableCorrect()==false) {
				// --- if not correct, recreate -----------
				if (this.createTablePlatforms(true)==false) {
					this.hasErrors = true;
					return;
				}
			}
			
		} else {
			// --- try to create to the database ----------
			if (this.createDB()==true) {
				if (this.createTablePlatforms()==false) {
					this.hasErrors = true;
					return;
				}
			} else {
				this.hasErrors = true;
				return;
			}
		}
	}

	/**
	 * Creates the required Database for the server.master agent
	 * @return True, if the database creation was successful
	 */
	private boolean createDB(){
		
		String msg = ""; 
		msg += "Die Datenbank f�r den Server-Master ist nicht vorhanden." + newLine;
		msg += "Soll versucht werden diese nun anzulegen?"; 
		this.dbError.setText( msg );
		this.dbError.setHead( "Datenbank nicht vorhanden!" );
		this.dbError.setErrNumber( -1 );
		this.dbError.setErr(true);
		if ( this.dbError.showQuestion() == JOptionPane.NO_OPTION ) return false;	
		
		
		// ---------------------------------------------------------------------------------
		// --- Hier steht die Standard-Konfiguration der Datenbank f�r den Server-Master ---
		// ---------------------------------------------------------------------------------
		System.out.println( Language.translate("Erzeuge die Datenbank f�r den Server-Master ... ") );

		// --- Create DB --------------------------
		sql = "CREATE DATABASE " + dbName + " DEFAULT CHARACTER SET utf8";
		if ( this.getSqlExecuteUpdate(sql) == false ) return false;
		
		// --- Set Connection to Database ---------
		this.setConnection2Database(dbName);
		return true;
		
	}
	
	/**
	 * Creates the table platforms.
	 * @return true, if successful
	 */
	private boolean createTablePlatforms() {
		return createTablePlatforms(false);
	}
	/**
	 * Creates the table 'platforms'.
	 * @param reCreate the re create
	 * @return true, if successful
	 */
	private boolean createTablePlatforms(boolean reCreate) {
		
		if (reCreate==true && this.isPlatformTable()==true ) {
			sql = "Drop Table platforms";
			this.getSqlExecuteUpdate(sql);
		}
		
		// --- Create TB 'server' -----------------
		sql  = "CREATE TABLE platforms (" +
				
				"id_platform int(11) NOT NULL AUTO_INCREMENT," +
				"contact_agent varchar(255) CHARACTER SET utf8 NOT NULL," +
				"platform_name varchar(255) CHARACTER SET utf8 NOT NULL," +
				"is_server tinyint(4)," +
				"ip varchar(50) CHARACTER SET utf8," +
				"url varchar(255) CHARACTER SET utf8," +
				"jade_port int(11)," +
				"http4mtp varchar(255) CHARACTER SET utf8," +

				"vers_major int(11)," +
				"vers_minor int(11)," +
				"vers_build int(11)," +
				
				"os_name varchar(50)," +
				"os_version varchar(50)," +
				"os_arch varchar(50)," +
				
				"cpu_vendor varchar(255)," +
				"cpu_model varchar(255)," +
				"cpu_n int(3)," +
				"cpu_speed_mhz int(11)," +
				
				"memory_total_mb int(11)," +
				
				"benchmark_value float default 0," +
				
				"online_since DATETIME," +
				"last_contact_at DATETIME," +
				"local_online_since DATETIME," +
				"local_last_contact_at DATETIME," +
				
				"currently_available tinyint(4)," +
				"current_load_cpu float," +
				"current_load_memory_system float," +
				"current_load_memory_jvm float," +
				"current_load_no_threads int," +
				"current_load_threshold_exceeded tinyint(4)," +
				
			   "PRIMARY KEY (id_platform)," +
			   "UNIQUE KEY contact_agent (contact_agent) " +
			   ") ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='Agent.GUI available platforms'";
		
		if ( this.getSqlExecuteUpdate(sql) == false ) return false;
		
		// ---------------------------------------------------------------------------------
		System.out.println( Language.translate("Erzeuge die Tabelle 'platforms' f�r den Server-Master ... ") );
		return true;
		
	}
	
	/**
	 * Checks if is platform table correct.
	 * @return true, if is platform table correct
	 */
	private boolean isPlatformTableCorrect() {
		
		sql  = "SELECT " +
				"id_platform, contact_agent, platform_name, is_server, ip, url, jade_port, http4mtp, " +
				"vers_major, vers_minor, vers_build, " +
				"os_name, os_version, os_arch, " +
				"cpu_vendor, cpu_model, cpu_n, cpu_speed_mhz, memory_total_mb, " +
				"benchmark_value, " +
				"online_since, last_contact_at, local_online_since, local_last_contact_at, " +
				"currently_available, current_load_cpu, current_load_memory_system, " +
				"current_load_memory_jvm, current_load_no_threads, " +
				"current_load_threshold_exceeded " +
				"FROM platforms";
		
		ResultSet res = getSqlResult4ExecuteQuery(sql, false);
		if (res==null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if is platform table.
	 * @return true, if is platform table
	 */
	private boolean isPlatformTable() {
		
		sql  = "SELECT * FROM platforms";
		ResultSet res = getSqlResult4ExecuteQuery(sql);
		if (res==null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the required database is available
	 * @return True, if the database is available
	 */
	private boolean dbIsAvailable() {

		String sql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbName + "'";
		ResultSet res = getSqlResult4ExecuteQuery(sql);
		if (res==null) {
			return false;
		} else {
			try {
				res.last();
				if ( res.getRow()==0 ) {
					return false;
				} else {
					return true;
				}				
			} catch (SQLException e) {
				e.printStackTrace();
				this.dbError.setErrNumber( e.getErrorCode() );
				this.dbError.setHead( "Fehler beim Auslesen der Datens�tzen!" );
				this.dbError.setText( e.getLocalizedMessage() );
				this.dbError.setErr(true);
				this.dbError.show();
				return false;
			}
		}
	}
	
	/**
	 * Does the setCatalog-method for the current connection
	 * @param database on which we would like to connect
	 */
	private void setConnection2Database(String database) {
		
		try {
			connection.setCatalog(database);
		} catch (SQLException e) {
			e.printStackTrace();
			this.dbError.setText(e.getLocalizedMessage());
			this.dbError.setErrNumber( e.getErrorCode() );
			this.dbError.setHead( "Fehler bei der Ausf�hrung von 'executeUpdate'!" );
			this.dbError.setErr(true);
			this.dbError.show();
		}
		
	}

	/**
	 * This Method executes a SQL-Statement (Create, Insert, Update) in
	 * the database and returns true if this was successful 
	 * @param sqlStatement the SQL-Statement to execute
	 * @return True, if the execution was successful
	 */
	public boolean getSqlExecuteUpdate(String sqlStatement) {
		
		Statement state = getNewStatement();
		if (state!=null) {
			try {
				state.executeUpdate(sqlStatement);
				return true;
			} catch (SQLException e) {
				//e.printStackTrace();
				String msg = e.getLocalizedMessage() + newLine;
				msg += sqlStatement;
				this.dbError.setText(msg);
				this.dbError.put2Clipboard(sqlStatement);
				this.dbError.setErrNumber( e.getErrorCode() );
				this.dbError.setHead( "Fehler bei der Ausf�hrung von 'executeUpdate'!" );
				this.dbError.setErr(true);
				this.dbError.show();
			}
		}
		return false;
	}
	/**
	 * This method returns the number of rows from a ResultSet-Object
	 * @param rs
	 * @return Number of rows from a result set, after SQL execution 
	 */
	public int getRowCount(ResultSet rs){
        int numResults = 0;
        try{
            rs.last();
            numResults = rs.getRow();
            rs.beforeFirst();
        } catch(SQLException e){
            e.printStackTrace();
        }
        return numResults;
    }

	/**
	 * Returns a ResultSet - Object for a SQL-Statement
	 * @param sqlStmt the SQL statement
	 * @return com.mysql.jdbc.ResultSet
	 */
	public ResultSet getSqlResult4ExecuteQuery(String sqlStmt) {
		return this.getSqlResult4ExecuteQuery(sqlStmt, true);
	}
	/**
	 * Returns a ResultSet - Object for a SQL-Statement.
	 * @param sqlStmt the SQL statement
	 * @param showErrorDialog the show error dialog
	 * @return com.mysql.jdbc.ResultSet
	 */
	public ResultSet getSqlResult4ExecuteQuery(String sqlStmt, boolean showErrorDialog) {
		
		ResultSet res = null;
		Statement state = this.getNewStatement();
		if (state!=null) {
			try {
				res = (ResultSet) state.executeQuery(sqlStmt);
			} catch (SQLException e) {
				//e.printStackTrace();
				String msg = e.getLocalizedMessage() + newLine;
				msg += sqlStmt;
				this.dbError.setText(msg);
				this.dbError.put2Clipboard(sqlStmt);
				this.dbError.setErrNumber( e.getErrorCode() );
				this.dbError.setHead( "Fehler bei der Ausf�hrung von 'executeQuery'!" );
				this.dbError.setErr(true);
				if (showErrorDialog==true) {
					this.dbError.show();
				}
			}
		}		
		return res;
		
	}
	
	/**
	 * This method returns a new Statement-Object for a further
	 * handling of SQL-Interaction (e. g. for an executeUpdate)
	 * @return com.mysql.jdbc.Statement
	 */
	public Statement getNewStatement() {
		try {
			return (Statement) connection.createStatement();
		} catch (SQLException e) {
			//e.printStackTrace();
			System.out.println( "Code: " + e.getErrorCode() );
			this.dbError.setErrNumber( e.getErrorCode() );
			this.dbError.setHead( "Fehler beim Erzeugen einer neuen Statement-Instanz!" );
			this.dbError.setText( e.getLocalizedMessage() );
			this.dbError.setErr(true);
			this.dbError.show();
			return null;
		}
	}
	
	/**
	 * This method initialises the connection to the Database-Server
	 * defined in the Application-Options
	 * @return True, if the connection was established successfully 
	 */
	private boolean connect() {
		
		String configuredHost = Application.getGlobalInfo().getServerMasterDBHost();
		if (configuredHost==null) {
			
			String msg = ""; 
			msg += "Fehlende Angaben �ber den Datenbank-Host." + newLine;
			msg += "Bitte �berpr�fen Sie die Datenbank-Einstellungen!";
			this.dbError.setText( msg );			
			this.dbError.setHead( "Fehler in der Datenbank-Konfiguration!" );
			this.dbError.setErrNumber( -1 );
			this.dbError.setErr(true);
			this.dbError.show();			
			return false;
		}

		dbName = Application.getGlobalInfo().getServerMasterDBName();
		dbUser = Application.getGlobalInfo().getServerMasterDBUser();
		dbPswd = Application.getGlobalInfo().getServerMasterDBPswd(); 
		if ( configuredHost.contains(":") ) {
			// --- Port wurde explizit angegeben --------------------
			dbHost = "jdbc:mysql://" + configuredHost + "/";
		} else {
			// --- Port wurde nicht angegeben, Standard verwenden ---
			dbHost = "jdbc:mysql://" + configuredHost + ":3306/";
		}
		
		try {
			
			Properties props = new Properties();
			if (dbUser!=null) {
				props.setProperty("user", dbUser);
			} else {
				props.setProperty("user", "");
			}
			if (dbPswd!=null) {
				props.setProperty("password", dbPswd);
			} else {
				props.setProperty("password", "");
			}
			props.setProperty("useUnicode","true");
			props.setProperty("characterEncoding","UTF-8");
			props.setProperty("connectionCollation","utf8_general_ci");
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = (Connection) DriverManager.getConnection(dbHost, props);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			this.dbError.setText( e.getLocalizedMessage() );
			this.dbError.setErr(true);
		} catch (InstantiationException e) {
			e.printStackTrace();
			this.dbError.setText( e.getLocalizedMessage() );
			this.dbError.setErr(true);
		} catch (IllegalAccessException e) {
			//e.printStackTrace();
			this.dbError.setText( e.getLocalizedMessage() );
			this.dbError.setErr(true);
		} catch (SQLException e) {
			//e.printStackTrace();
			this.dbError.setErrNumber( e.getErrorCode() );
			this.dbError.setText( e.getLocalizedMessage() );
			this.dbError.setErr(true);
		}
		
		// --- Show Error if there ----------------------------------
		if (this.dbError.isErr()) {
			this.dbError.setHead( "Fehler beim Verbindungsaufbau mit Datenbank-Server!" );
			this.dbError.show();
			return false;
		}
		return true;		
	}
	
	/**
	 * Gets the connection instance to the database.
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}
	/**
	 * Sets the connection instance to the database.
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	// ------------------------------
	// --- Start Sub-Class ----------
	// ------------------------------
	/**
	 * This inner class handles the Errors which can occur during the SQL-Interactions
	 * 
	 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
	 */
	public class Error {
		
		private Integer errNumber = 0;
		private String errHead = null;
		private String errText = null;
		private boolean err = false;
		
		private String msg = "";
		
		/**
		 * Constructor of this class 
		 */
		public Error () {
			//------
		}
		
		/**
		 * Sets the Message which will be shown in an Error-Dialog
		 */
		private void setMessage() {
			
			if (errNumber.equals(0) ) {
				// --- NICHT-SQL-Fehler [System] ----------
				msg  = errText + newLine;
			} else  if (errNumber.equals(-1) ) {
				// --- NICHT-SQL-Fehler [own] -------------
				msg  = Language.translate(errText) + newLine;
			} else {
				// --- SQL-Fehler -------------------------
				msg  = Language.translate("MySQL-Fehler ") + errNumber + ":" + newLine;
				msg += errText;				
				msg = formatText(msg);				
			}		
			errHead = Language.translate(errHead);
		}
		/**
		 * Shows the current Error-Message on an OptionPane-MessageDialog.
		 * Afterwards, the error will be reseted
		 */
		public void show() {
			this.setMessage();
			JOptionPane.showMessageDialog(null, msg, errHead, JOptionPane.OK_OPTION);
			this.resetError();
		}
		/**
		 * Shows the current message, will ask the user and returns the User-Answer
		 * @return Answer of the user, coming from a JOptionPane. 
		 */
		public int showQuestion() {
			this.setMessage();
			int answer = JOptionPane.showConfirmDialog(null, msg, errHead, JOptionPane.YES_NO_OPTION); 
			this.resetError();
			return answer;			
		}	
		/**
		 * This Method resets the current Err-Object to a non-Error State
		 */
		private void resetError(){
			errNumber = 0;
			errHead = null;
			errText = null;
			err = false;
			msg = "";
		}
		/**
		 * Here a error-number (for SQL) can be set
		 * @param newErrNumber
		 */
		public void setErrNumber(Integer newErrNumber) {
			errNumber = newErrNumber;
		}
		/**
		 * Here the Dialog-Title of the JOptionPane can be set
		 * @param newErrHead
		 */
		public void setHead(String newErrHead) {
			errHead = newErrHead;
		}
		/**
		 * Here, the Text inside of the JOptionPane can be set 
		 * @param newErrText
		 */
		public void setText(String newErrText) {
			errText = newErrText;
		}
		/**
		 * Here, an indicator for an error can be set 
		 * @param err
		 */
		public void setErr(boolean err) {
			this.err = err;
		}
		/**
		 * This method returns, if there is an error or not
		 * @return boolean
		 */
		public boolean isErr() {
			return err;
		}
		/**
		 * This method puts the value of 'toClipboard' to the clipboard
		 * In case of an SQL-Error, the SQL-Statement will be placed in
		 * such a way and can be used in an external application as well.
		 * @param toClipboard String which will be placed in the clipboard
		 */
		public void put2Clipboard(String toClipboard) {
			StringSelection data = new StringSelection(toClipboard);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(data, data);
		}
		/**
		 * In case of an SQL-Error the Text can be very long.
		 * This Method checks a Text and will reduce the width
		 * of the Text (shown in the JOptionpane) to a maximum
		 * width of 100 characters by adding line breaks. 
		 * @param currText
		 * @return formated String with line breaks
		 */
		private String formatText(String currText) {
			
			String newTextArr[] = currText.split(" ");
			String newText = "";
			String line = "";		
			
			for (int i = 0; i < newTextArr.length; i++) {
				line += newTextArr[i] + " ";
				if ( line.length() >= 100  ) {
					newText += line + newLine;
					line = "";
				}
			}
			newText += line;
			return newText;
			
		}
	}
	// ------------------------------
	// --- End Sub-Class ------------
	// ------------------------------
	
	
	/**
	 * This method converts an DB-Integer to a Java-boolean 
	 * @param intValue
	 * @return False, if the integer value is equal 0 - otherwise true
	 */
	public boolean dbInteger2Bool(Integer intValue) {
		if (intValue.equals(0)) {
			return false;
		} else {
			return true;
		}
	}
	/**
	 * This method converts an DB-boolean to an Java-Integer
	 * @param booleanValue
	 * @return 0, if the boolean is true - otherwise -1
	 */
	public Integer dbBool2Integer(boolean booleanValue) {
		if (booleanValue==true) {
			return -1;
		} else {
			return 0;
		}
	}
	
	
}
