/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.MTOMFeature
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.ResourceLoader;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class DeploymentDescriptorParser<A> {
    private final ClassLoader classLoader;
    private final ResourceLoader loader;
    private final AdapterFactory<A> adapterFactory;
    private static final XMLInputFactory xif = XmlUtil.newXMLInputFactory(true);
    private final Set<String> names = new HashSet<String>();
    private final List<URL> docs = new ArrayList<URL>();
    public static final String NS_RUNTIME = "http://java.sun.com/xml/ns/jax-ws/ri/runtime";
    public static final String JAXWS_WSDL_DD_DIR = "WEB-INF/wsdl";
    public static final QName QNAME_ENDPOINTS = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoints");
    public static final QName QNAME_ENDPOINT = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoint");
    public static final String ATTR_VERSION = "version";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_IMPLEMENTATION = "implementation";
    public static final String ATTR_WSDL = "wsdl";
    public static final String ATTR_SERVICE = "service";
    public static final String ATTR_PORT = "port";
    public static final String ATTR_URL_PATTERN = "url-pattern";
    public static final String ATTR_ENABLE_MTOM = "enable-mtom";
    public static final String ATTR_MTOM_THRESHOLD_VALUE = "mtom-threshold-value";
    public static final String ATTR_BINDING = "binding";
    public static final String ATTRVALUE_VERSION_1_0 = "2.0";
    private static final Logger logger = Logger.getLogger(DeploymentDescriptorParser.class.getName());

    public DeploymentDescriptorParser(ClassLoader cl, ResourceLoader loader, AdapterFactory<A> adapterFactory) throws IOException {
        this.classLoader = cl;
        this.loader = loader;
        this.adapterFactory = adapterFactory;
        this.collectDocs("/WEB-INF/wsdl/");
        logger.log(Level.FINE, "war metadata={0}", this.docs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<A> parse(String systemId, InputStream is) {
        XMLStreamReader reader = null;
        try {
            Object object = xif;
            synchronized (object) {
                reader = xif.createXMLStreamReader(systemId, is);
            }
            DeploymentDescriptorParser.nextElementContent(reader);
            object = this.parseAdapters(reader);
            return object;
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
        catch (XMLStreamException xe) {
            throw new WebServiceException((Throwable)xe);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (XMLStreamException xMLStreamException) {}
            }
            try {
                is.close();
            }
            catch (IOException iOException) {}
        }
    }

    private static int nextElementContent(XMLStreamReader reader) throws XMLStreamException {
        int state;
        while ((state = reader.next()) != 1 && state != 2 && state != 8) {
        }
        return state;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<A> parse(File f) throws IOException {
        try (FileInputStream in = new FileInputStream(f);){
            List<A> list = this.parse(f.getPath(), in);
            return list;
        }
    }

    private void collectDocs(String dirPath) throws IOException {
        Set<String> paths = this.loader.getResourcePaths(dirPath);
        if (paths != null) {
            for (String path : paths) {
                if (path.endsWith("/")) {
                    if (path.endsWith("/CVS/") || path.endsWith("/.svn/")) continue;
                    this.collectDocs(path);
                    continue;
                }
                URL res = this.loader.getResource(path);
                this.docs.add(res);
            }
        }
    }

    private List<A> parseAdapters(XMLStreamReader reader) throws IOException, XMLStreamException {
        if (!reader.getName().equals(QNAME_ENDPOINTS)) {
            DeploymentDescriptorParser.failWithFullName("runtime.parser.invalidElement", reader);
        }
        ArrayList<A> adapters = new ArrayList<A>();
        String version = this.getMandatoryNonEmptyAttribute(reader, ATTR_VERSION);
        if (!version.equals(ATTRVALUE_VERSION_1_0)) {
            DeploymentDescriptorParser.failWithLocalName("sun-jaxws.xml's version attribut runtime.parser.invalidVersionNumber", reader, version);
        }
        while (DeploymentDescriptorParser.nextElementContent(reader) != 2) {
            if (reader.getName().equals(QNAME_ENDPOINT)) {
                String bindingId;
                String name = this.getMandatoryNonEmptyAttribute(reader, ATTR_NAME);
                if (!this.names.add(name)) {
                    logger.log(Level.WARNING, "sun-jaxws.xml contains duplicate endpoint names. The first duplicate name is = {0}", name);
                }
                String implementationName = this.getMandatoryNonEmptyAttribute(reader, ATTR_IMPLEMENTATION);
                Class implementorClass = this.getImplementorClass(implementationName, reader);
                QName serviceName = this.getQNameAttribute(reader, ATTR_SERVICE);
                QName portName = this.getQNameAttribute(reader, ATTR_PORT);
                ArrayList<MTOMFeature> features = new ArrayList<MTOMFeature>();
                String enable_mtom = this.getAttribute(reader, ATTR_ENABLE_MTOM);
                String mtomThreshold = this.getAttribute(reader, ATTR_MTOM_THRESHOLD_VALUE);
                if (Boolean.valueOf(enable_mtom).booleanValue()) {
                    if (mtomThreshold != null) {
                        features.add(new MTOMFeature(true, Integer.parseInt(mtomThreshold)));
                    } else {
                        features.add(new MTOMFeature(true));
                    }
                }
                if ((bindingId = this.getAttribute(reader, ATTR_BINDING)) != null) {
                    bindingId = DeploymentDescriptorParser.getBindingIdForToken(bindingId);
                }
                String urlPattern = this.getMandatoryNonEmptyAttribute(reader, ATTR_URL_PATTERN);
                DeploymentDescriptorParser.nextElementContent(reader);
                DeploymentDescriptorParser.ensureNoContent(reader);
                ArrayList<Source> metadata = new ArrayList<Source>();
                for (URL url : this.docs) {
                    StreamSource source = new StreamSource(url.openStream(), url.toExternalForm());
                    metadata.add(source);
                }
                adapters.add(this.adapterFactory.createAdapter(name, urlPattern, implementorClass, serviceName, portName, bindingId, metadata, features.toArray(new WebServiceFeature[features.size()])));
                continue;
            }
            DeploymentDescriptorParser.failWithLocalName("runtime.parser.invalidElement", reader);
        }
        return adapters;
    }

    private static String getBindingIdForToken(String lexical) {
        if (lexical.equals("##SOAP11_HTTP")) {
            return "http://schemas.xmlsoap.org/wsdl/soap/http";
        }
        if (lexical.equals("##SOAP11_HTTP_MTOM")) {
            return "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
        }
        if (lexical.equals("##SOAP12_HTTP")) {
            return "http://www.w3.org/2003/05/soap/bindings/HTTP/";
        }
        if (lexical.equals("##SOAP12_HTTP_MTOM")) {
            return "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";
        }
        if (lexical.equals("##XML_HTTP")) {
            return "http://www.w3.org/2004/08/wsdl/http";
        }
        return lexical;
    }

    protected String getAttribute(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value != null) {
            value = value.trim();
        }
        return value;
    }

    protected QName getQNameAttribute(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value == null || value.equals("")) {
            return null;
        }
        return QName.valueOf(value);
    }

    protected String getNonEmptyAttribute(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value != null && value.equals("")) {
            DeploymentDescriptorParser.failWithLocalName("runtime.parser.invalidAttributeValue", reader, name);
        }
        return value;
    }

    protected String getMandatoryAttribute(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value == null) {
            DeploymentDescriptorParser.failWithLocalName("runtime.parser.missing.attribute", reader, name);
        }
        return value;
    }

    protected String getMandatoryNonEmptyAttribute(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value == null) {
            DeploymentDescriptorParser.failWithLocalName("Missing attribute", reader, name);
        } else if (value.equals("")) {
            DeploymentDescriptorParser.failWithLocalName("Invalid attribute value", reader, name);
        }
        return value;
    }

    protected static void ensureNoContent(XMLStreamReader reader) {
        if (reader.getEventType() != 2) {
            DeploymentDescriptorParser.fail("While parsing sun-jaxws.xml, found unexpected content at line=", reader);
        }
    }

    protected static void fail(String key, XMLStreamReader reader) {
        String msg = key + reader.getLocation().getLineNumber();
        logger.log(Level.SEVERE, msg);
        throw new WebServiceException(msg);
    }

    protected static void failWithFullName(String key, XMLStreamReader reader) {
        String msg = key + reader.getLocation().getLineNumber() + reader.getName();
        throw new WebServiceException(msg);
    }

    protected static void failWithLocalName(String key, XMLStreamReader reader) {
        String msg = key + reader.getLocation().getLineNumber() + reader.getLocalName();
        throw new WebServiceException(msg);
    }

    protected static void failWithLocalName(String key, XMLStreamReader reader, String arg) {
        String msg = key + reader.getLocation().getLineNumber() + reader.getLocalName() + arg;
        throw new WebServiceException(msg);
    }

    protected Class loadClass(String name) {
        try {
            return Class.forName(name, true, this.classLoader);
        }
        catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new WebServiceException((Throwable)e);
        }
    }

    private Class getImplementorClass(String name, XMLStreamReader xsr) {
        try {
            return Class.forName(name, true, this.classLoader);
        }
        catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new WebServiceException("Class at " + xsr.getLocation().getLineNumber() + " is not found", (Throwable)e);
        }
    }

    public static interface AdapterFactory<A> {
        public A createAdapter(String var1, String var2, Class var3, QName var4, QName var5, String var6, List<Source> var7, WebServiceFeature ... var8);
    }
}

