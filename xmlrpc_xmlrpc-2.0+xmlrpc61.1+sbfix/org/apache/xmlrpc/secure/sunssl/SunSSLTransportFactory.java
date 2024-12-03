/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.net.ssl.HostnameVerifier
 *  com.sun.net.ssl.HttpsURLConnection
 *  com.sun.net.ssl.SSLContext
 *  com.sun.net.ssl.TrustManager
 *  com.sun.net.ssl.X509TrustManager
 *  com.sun.net.ssl.internal.ssl.Provider
 */
package org.apache.xmlrpc.secure.sunssl;

import com.sun.net.ssl.HostnameVerifier;
import com.sun.net.ssl.HttpsURLConnection;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.X509TrustManager;
import com.sun.net.ssl.internal.ssl.Provider;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Properties;
import javax.net.ssl.SSLSocketFactory;
import org.apache.xmlrpc.DefaultXmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransportFactory;
import org.apache.xmlrpc.secure.SecurityTool;

public class SunSSLTransportFactory
implements XmlRpcTransportFactory {
    protected URL url;
    protected String auth;
    public static final String TRANSPORT_TRUSTMANAGER = "hostnameverifier";
    public static final String TRANSPORT_HOSTNAMEVERIFIER = "trustmanager";
    private static X509TrustManager openTrustManager = new X509TrustManager(){

        public boolean isClientTrusted(X509Certificate[] chain) {
            return true;
        }

        public boolean isServerTrusted(X509Certificate[] chain) {
            return true;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };
    private static HostnameVerifier openHostnameVerifier = new HostnameVerifier(){

        public boolean verify(String hostname, String session) {
            return true;
        }
    };

    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("url", "(java.net.URL) - URL to connect to");
        properties.setProperty("auth", "(java.lang.String) - HTTP Basic Authentication string (encoded).");
        properties.setProperty(TRANSPORT_TRUSTMANAGER, "(com.sun.net.ssl.X509TrustManager) - X.509 Trust Manager to use");
        properties.setProperty(TRANSPORT_HOSTNAMEVERIFIER, "(com.sun.net.ssl.HostnameVerifier) - Hostname verifier to use");
        return properties;
    }

    public SunSSLTransportFactory(Properties properties) throws GeneralSecurityException {
        HostnameVerifier hostnameVerifier;
        Security.addProvider((java.security.Provider)new Provider());
        this.url = (URL)((Hashtable)properties).get("url");
        this.auth = properties.getProperty("auth");
        X509TrustManager trustManager = (X509TrustManager)((Hashtable)properties).get(TRANSPORT_TRUSTMANAGER);
        if (trustManager == null) {
            trustManager = openTrustManager;
        }
        if ((hostnameVerifier = (HostnameVerifier)((Hashtable)properties).get(TRANSPORT_HOSTNAMEVERIFIER)) == null) {
            hostnameVerifier = openHostnameVerifier;
        }
        SSLContext sslContext = SSLContext.getInstance((String)SecurityTool.getSecurityProtocol());
        X509TrustManager[] tmArray = new X509TrustManager[]{trustManager};
        sslContext.init(null, (TrustManager[])tmArray, new SecureRandom());
        if (sslContext != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory((SSLSocketFactory)sslContext.getSocketFactory());
        }
        HttpsURLConnection.setDefaultHostnameVerifier((HostnameVerifier)hostnameVerifier);
    }

    public XmlRpcTransport createTransport() {
        return new DefaultXmlRpcTransport(this.url, this.auth);
    }

    public void setProperty(String propertyName, Object value) {
        if ("auth".equals(propertyName)) {
            this.auth = (String)value;
        } else if ("url".equals(propertyName)) {
            this.url = (URL)value;
        }
    }
}

