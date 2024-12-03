/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.net.ssl.KeyManagerFactory
 *  com.sun.net.ssl.SSLContext
 *  com.sun.net.ssl.TrustManager
 *  com.sun.net.ssl.TrustManagerFactory
 *  com.sun.net.ssl.internal.ssl.Provider
 */
package org.apache.axis.components.net;

import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.TrustManagerFactory;
import com.sun.net.ssl.internal.ssl.Provider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Hashtable;
import org.apache.axis.components.net.JSSESocketFactory;
import org.apache.axis.components.net.SecureSocketFactory;
import sun.security.provider.Sun;

public class SunJSSESocketFactory
extends JSSESocketFactory
implements SecureSocketFactory {
    private String keystoreType;
    static String defaultKeystoreType = "JKS";
    static String defaultProtocol = "TLS";
    static String defaultAlgorithm = "SunX509";
    static boolean defaultClientAuth = false;
    private boolean clientAuth = false;
    static String defaultKeystoreFile = System.getProperty("user.home") + "/.keystore";
    static String defaultKeyPass = "changeit";

    public SunJSSESocketFactory(Hashtable attributes) {
        super(attributes);
    }

    protected void initFactory() throws IOException {
        try {
            Security.addProvider(new Sun());
            Security.addProvider((java.security.Provider)new Provider());
            SSLContext context = this.getContext();
            this.sslFactory = context.getSocketFactory();
        }
        catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            throw new IOException(e.getMessage());
        }
    }

    protected SSLContext getContext() throws Exception {
        String algorithm;
        String protocol;
        String keystorePass;
        if (this.attributes == null) {
            SSLContext context = SSLContext.getInstance((String)"SSL");
            context.init(null, null, null);
            return context;
        }
        String keystoreFile = (String)this.attributes.get("keystore");
        if (keystoreFile == null) {
            keystoreFile = defaultKeystoreFile;
        }
        this.keystoreType = (String)this.attributes.get("keystoreType");
        if (this.keystoreType == null) {
            this.keystoreType = defaultKeystoreType;
        }
        this.clientAuth = null != (String)this.attributes.get("clientauth");
        String keyPass = (String)this.attributes.get("keypass");
        if (keyPass == null) {
            keyPass = defaultKeyPass;
        }
        if ((keystorePass = (String)this.attributes.get("keystorePass")) == null) {
            keystorePass = keyPass;
        }
        if ((protocol = (String)this.attributes.get("protocol")) == null) {
            protocol = defaultProtocol;
        }
        if ((algorithm = (String)this.attributes.get("algorithm")) == null) {
            algorithm = defaultAlgorithm;
        }
        KeyStore kstore = this.initKeyStore(keystoreFile, keystorePass);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance((String)algorithm);
        kmf.init(kstore, keyPass.toCharArray());
        TrustManager[] tm = null;
        if (this.clientAuth) {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance((String)"SunX509");
            tmf.init(kstore);
            tm = tmf.getTrustManagers();
        }
        SSLContext context = SSLContext.getInstance((String)protocol);
        context.init(kmf.getKeyManagers(), tm, new SecureRandom());
        return context;
    }

    private KeyStore initKeyStore(String keystoreFile, String keyPass) throws IOException {
        try {
            KeyStore kstore = KeyStore.getInstance(this.keystoreType);
            FileInputStream istream = new FileInputStream(keystoreFile);
            kstore.load(istream, keyPass.toCharArray());
            return kstore;
        }
        catch (FileNotFoundException fnfe) {
            throw fnfe;
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("Exception trying to load keystore " + keystoreFile + ": " + ex.getMessage());
        }
    }
}

