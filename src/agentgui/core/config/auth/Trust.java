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
package agentgui.core.config.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * This class helps managing the trust to a specific key/certificate for a given connection.
 */
public class Trust {
	
	/** The Constant OIDC_TRUST_STORE. */
	public static final String OIDC_TRUST_STORE = "oidcTrustStore.jks";
	
	/** The Constant TRUSTSTORE_PASSWORD. */
	private static final String TRUSTSTORE_PASSWORD = "HoYp8FfLLVJJFX1APMQA";

	/**
	 * Trust a specific certificate (or set of) for a specific connection.
	 *
	 * @param connection the connection
	 * @param trustStoreFile the trust store file
	 */
	public static void trustSpecific(HttpsURLConnection connection, File trustStoreFile) {
		connection.setSSLSocketFactory(getSocketFactory(trustStoreFile));
	}

	/**
	 * Gets the socket factory.
	 *
	 * @param trustStoreFile the trust store file
	 * @return the socket factory
	 */
	public static SSLSocketFactory getSocketFactory(File trustStoreFile) {
		SSLContext trustableSSLContext = getTrustableSSLContext(trustStoreFile);
		return trustableSSLContext.getSocketFactory();
	}

	/**
	 * Gets a SSLContext to be trusted.
	 *
	 * @param trustStoreFile the trust store file
	 * @return the trustable SSL context
	 */
	private static SSLContext getTrustableSSLContext(File trustStoreFile) {
		SSLContext sslContext = null;

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(new FileInputStream(trustStoreFile), TRUSTSTORE_PASSWORD.toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustStore);
			TrustManager[] tms = tmf.getTrustManagers();

			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, tms, null);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return sslContext;
	}
}
