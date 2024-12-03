/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlrpc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.xmlrpc.DefaultXmlRpcTransport;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransportFactory;
import org.apache.xmlrpc.util.HttpUtil;

public class DefaultXmlRpcTransportFactory
implements XmlRpcTransportFactory {
    protected URL url;
    protected String auth;
    protected static XmlRpcTransportFactory httpsTransportFactory;
    public static final String DEFAULT_HTTPS_PROVIDER = "comnetsun";
    private static Hashtable transports;
    static /* synthetic */ Class class$org$apache$xmlrpc$XmlRpcTransportFactory;

    public static void setHTTPSTransport(String transport, Properties properties) throws XmlRpcClientException {
        httpsTransportFactory = DefaultXmlRpcTransportFactory.createTransportFactory(transport, properties);
    }

    public static XmlRpcTransportFactory createTransportFactory(String transport, Properties properties) throws XmlRpcClientException {
        String transportFactoryClassName = null;
        try {
            Class<?> transportFactoryClass;
            Constructor<?> transportFactoryConstructor;
            Object transportFactoryInstance;
            transportFactoryClassName = (String)transports.get(transport);
            if (transportFactoryClassName == null) {
                transportFactoryClassName = transport;
            }
            if ((transportFactoryInstance = (transportFactoryConstructor = (transportFactoryClass = Class.forName(transportFactoryClassName)).getConstructor(XmlRpcTransportFactory.CONSTRUCTOR_SIGNATURE)).newInstance(properties)) instanceof XmlRpcTransportFactory) {
                return (XmlRpcTransportFactory)transportFactoryInstance;
            }
            throw new XmlRpcClientException("Class '" + transportFactoryClass.getName() + "' does not implement '" + (class$org$apache$xmlrpc$XmlRpcTransportFactory == null ? (class$org$apache$xmlrpc$XmlRpcTransportFactory = DefaultXmlRpcTransportFactory.class$("org.apache.xmlrpc.XmlRpcTransportFactory")) : class$org$apache$xmlrpc$XmlRpcTransportFactory).getName() + "'", null);
        }
        catch (ClassNotFoundException cnfe) {
            throw new XmlRpcClientException("Transport Factory not found: " + transportFactoryClassName, cnfe);
        }
        catch (NoSuchMethodException nsme) {
            throw new XmlRpcClientException("Transport Factory constructor not found: " + transportFactoryClassName + "(java.util.Properties properties)", nsme);
        }
        catch (IllegalAccessException iae) {
            throw new XmlRpcClientException("Unable to access Transport Factory constructor: " + transportFactoryClassName, iae);
        }
        catch (InstantiationException ie) {
            throw new XmlRpcClientException("Unable to instantiate Transport Factory: " + transportFactoryClassName, ie);
        }
        catch (InvocationTargetException ite) {
            throw new XmlRpcClientException("Error calling Transport Factory constructor: ", ite.getTargetException());
        }
    }

    public DefaultXmlRpcTransportFactory(URL url) {
        this.url = url;
    }

    public DefaultXmlRpcTransportFactory(URL url, String auth) {
        this(url);
        this.auth = auth;
    }

    public XmlRpcTransport createTransport() throws XmlRpcClientException {
        if ("https".equals(this.url.getProtocol())) {
            if (httpsTransportFactory == null) {
                Properties properties = new Properties();
                ((Hashtable)properties).put("url", this.url);
                ((Hashtable)properties).put("auth", this.auth);
                DefaultXmlRpcTransportFactory.setHTTPSTransport(DEFAULT_HTTPS_PROVIDER, properties);
            }
            return httpsTransportFactory.createTransport();
        }
        return new DefaultXmlRpcTransport(this.url);
    }

    public void setBasicAuthentication(String user, String password) {
        this.setProperty("auth", HttpUtil.encodeBasicAuthentication(user, password));
    }

    public void setProperty(String propertyName, Object value) {
        if (httpsTransportFactory != null) {
            httpsTransportFactory.setProperty(propertyName, value);
        }
        if ("auth".equals(propertyName)) {
            this.auth = (String)value;
        } else if ("url".equals(propertyName)) {
            this.url = (URL)value;
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        transports = new Hashtable(1);
        transports.put(DEFAULT_HTTPS_PROVIDER, "org.apache.xmlrpc.secure.sunssl.SunSSLTransportFactory");
    }
}

