/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.MTOMFeature
 *  javax.xml.ws.soap.SOAPBinding
 */
package com.sun.xml.ws.transport.http;

import com.oracle.webservices.api.databinding.DatabindingModeFeature;
import com.oracle.webservices.api.databinding.ExternalMetadataFeature;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.handler.HandlerChainsModel;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.server.EndpointFactory;
import com.sun.xml.ws.server.ServerRtException;
import com.sun.xml.ws.streaming.Attributes;
import com.sun.xml.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.ws.transport.http.ResourceLoader;
import com.sun.xml.ws.util.HandlerAnnotationInfo;
import com.sun.xml.ws.util.exception.LocatableWebServiceException;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.soap.SOAPBinding;
import org.xml.sax.EntityResolver;

public class DeploymentDescriptorParser<A> {
    public static final String NS_RUNTIME = "http://java.sun.com/xml/ns/jax-ws/ri/runtime";
    public static final String JAXWS_WSDL_DD_DIR = "WEB-INF/wsdl";
    public static final QName QNAME_ENDPOINTS = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoints");
    public static final QName QNAME_ENDPOINT = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "endpoint");
    public static final QName QNAME_EXT_METADA = new QName("http://java.sun.com/xml/ns/jax-ws/ri/runtime", "external-metadata");
    public static final String ATTR_FILE = "file";
    public static final String ATTR_RESOURCE = "resource";
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
    public static final String ATTR_DATABINDING = "databinding";
    public static final List<String> ATTRVALUE_SUPPORTED_VERSIONS = Arrays.asList("2.0", "2.1");
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.server.http");
    private final Container container;
    private final ClassLoader classLoader;
    private final ResourceLoader loader;
    private final AdapterFactory<A> adapterFactory;
    private final Set<String> names = new HashSet<String>();
    private final Map<String, SDDocumentSource> docs = new HashMap<String, SDDocumentSource>();

    public DeploymentDescriptorParser(ClassLoader cl, ResourceLoader loader, Container container, AdapterFactory<A> adapterFactory) throws MalformedURLException {
        this.classLoader = cl;
        this.loader = loader;
        this.container = container;
        this.adapterFactory = adapterFactory;
        this.collectDocs("/WEB-INF/wsdl/");
        logger.log(Level.FINE, "war metadata={0}", this.docs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public List<A> parse(String systemId, InputStream is) {
        List<A> list;
        block11: {
            XMLStreamReader reader = null;
            try {
                reader = new TidyXMLStreamReader(XMLStreamReaderFactory.create(systemId, is, true), is);
                XMLStreamReaderUtil.nextElementContent(reader);
                list = this.parseAdapters(reader);
                if (reader == null) break block11;
            }
            catch (Throwable throwable) {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (XMLStreamException e) {
                        throw new ServerRtException("runtime.parser.xmlReader", e);
                    }
                }
                try {
                    is.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                throw throwable;
            }
            try {
                reader.close();
            }
            catch (XMLStreamException e) {
                throw new ServerRtException("runtime.parser.xmlReader", e);
            }
        }
        try {
            is.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return list;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public List<A> parse(File f) throws IOException {
        try (FileInputStream in = new FileInputStream(f);){
            List<A> list = this.parse(f.getPath(), in);
            return list;
        }
    }

    private void collectDocs(String dirPath) throws MalformedURLException {
        Set<String> paths = this.loader.getResourcePaths(dirPath);
        if (paths != null) {
            for (String path : paths) {
                if (path.endsWith("/")) {
                    if (path.endsWith("/CVS/") || path.endsWith("/.svn/")) continue;
                    this.collectDocs(path);
                    continue;
                }
                URL res = this.loader.getResource(path);
                this.docs.put(res.toString(), SDDocumentSource.create(res));
            }
        }
    }

    private List<A> parseAdapters(XMLStreamReader reader) {
        if (!reader.getName().equals(QNAME_ENDPOINTS)) {
            DeploymentDescriptorParser.failWithFullName("runtime.parser.invalidElement", reader);
        }
        ArrayList<A> adapters = new ArrayList<A>();
        Attributes attrs = XMLStreamReaderUtil.getAttributes(reader);
        String version = this.getMandatoryNonEmptyAttribute(reader, attrs, ATTR_VERSION);
        if (!ATTRVALUE_SUPPORTED_VERSIONS.contains(version)) {
            DeploymentDescriptorParser.failWithLocalName("runtime.parser.invalidVersionNumber", reader, version);
        }
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            if (reader.getName().equals(QNAME_ENDPOINT)) {
                QName portName;
                QName serviceName;
                attrs = XMLStreamReaderUtil.getAttributes(reader);
                String name = this.getMandatoryNonEmptyAttribute(reader, attrs, ATTR_NAME);
                if (!this.names.add(name)) {
                    logger.warning(WsservletMessages.SERVLET_WARNING_DUPLICATE_ENDPOINT_NAME());
                }
                String implementationName = this.getMandatoryNonEmptyAttribute(reader, attrs, ATTR_IMPLEMENTATION);
                Class implementorClass = this.getImplementorClass(implementationName, reader);
                MetadataReader metadataReader = null;
                ExternalMetadataFeature externalMetadataFeature = null;
                XMLStreamReaderUtil.nextElementContent(reader);
                if (reader.getEventType() != 2 && (externalMetadataFeature = this.configureExternalMetadataReader(reader)) != null) {
                    metadataReader = externalMetadataFeature.getMetadataReader(implementorClass.getClassLoader(), false);
                }
                if ((serviceName = this.getQNameAttribute(attrs, ATTR_SERVICE)) == null) {
                    serviceName = EndpointFactory.getDefaultServiceName(implementorClass, metadataReader);
                }
                if ((portName = this.getQNameAttribute(attrs, ATTR_PORT)) == null) {
                    portName = EndpointFactory.getDefaultPortName(serviceName, implementorClass, metadataReader);
                }
                String enable_mtom = this.getAttribute(attrs, ATTR_ENABLE_MTOM);
                String mtomThreshold = this.getAttribute(attrs, ATTR_MTOM_THRESHOLD_VALUE);
                String dbMode = this.getAttribute(attrs, ATTR_DATABINDING);
                String bindingId = this.getAttribute(attrs, ATTR_BINDING);
                if (bindingId != null) {
                    bindingId = DeploymentDescriptorParser.getBindingIdForToken(bindingId);
                }
                WSBinding binding = DeploymentDescriptorParser.createBinding(bindingId, implementorClass, enable_mtom, mtomThreshold, dbMode);
                if (externalMetadataFeature != null) {
                    binding.getFeatures().mergeFeatures(new WebServiceFeature[]{externalMetadataFeature}, true);
                }
                String urlPattern = this.getMandatoryNonEmptyAttribute(reader, attrs, ATTR_URL_PATTERN);
                boolean handlersSetInDD = this.setHandlersAndRoles(binding, reader, serviceName, portName);
                EndpointFactory.verifyImplementorClass(implementorClass, metadataReader);
                SDDocumentSource primaryWSDL = this.getPrimaryWSDL(reader, attrs, implementorClass, metadataReader);
                WSEndpoint endpoint = WSEndpoint.create(implementorClass, !handlersSetInDD, null, serviceName, portName, this.container, binding, primaryWSDL, this.docs.values(), this.createEntityResolver(), false);
                adapters.add(this.adapterFactory.createAdapter(name, urlPattern, endpoint));
                continue;
            }
            DeploymentDescriptorParser.failWithLocalName("runtime.parser.invalidElement", reader);
        }
        return adapters;
    }

    private static WSBinding createBinding(String ddBindingId, Class implClass, String mtomEnabled, String mtomThreshold, String dataBindingMode) {
        WebServiceFeatureList features;
        BindingID bindingID;
        MTOMFeature mtomfeature = null;
        if (mtomEnabled != null) {
            mtomfeature = mtomThreshold != null ? new MTOMFeature(Boolean.valueOf(mtomEnabled).booleanValue(), Integer.parseInt(mtomThreshold)) : new MTOMFeature(Boolean.valueOf(mtomEnabled).booleanValue());
        }
        if (ddBindingId != null) {
            bindingID = BindingID.parse(ddBindingId);
            features = bindingID.createBuiltinFeatureList();
            if (DeploymentDescriptorParser.checkMtomConflict(features.get(MTOMFeature.class), mtomfeature)) {
                throw new ServerRtException(ServerMessages.DD_MTOM_CONFLICT(ddBindingId, mtomEnabled), new Object[0]);
            }
        } else {
            bindingID = BindingID.parse(implClass);
            features = new WebServiceFeatureList();
            if (mtomfeature != null) {
                features.add((WebServiceFeature)mtomfeature);
            }
            features.addAll(bindingID.createBuiltinFeatureList());
        }
        if (dataBindingMode != null) {
            features.add(new DatabindingModeFeature(dataBindingMode));
        }
        return bindingID.createBinding(features.toArray());
    }

    private static boolean checkMtomConflict(MTOMFeature lhs, MTOMFeature rhs) {
        if (lhs == null || rhs == null) {
            return false;
        }
        return lhs.isEnabled() ^ rhs.isEnabled();
    }

    @NotNull
    public static String getBindingIdForToken(@NotNull String lexical) {
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

    private SDDocumentSource getPrimaryWSDL(XMLStreamReader xsr, Attributes attrs, Class<?> implementorClass, MetadataReader metadataReader) {
        String wsdlFile = this.getAttribute(attrs, ATTR_WSDL);
        if (wsdlFile == null) {
            wsdlFile = EndpointFactory.getWsdlLocation(implementorClass, metadataReader);
        }
        if (wsdlFile != null) {
            URL wsdl;
            if (!wsdlFile.startsWith(JAXWS_WSDL_DD_DIR)) {
                logger.log(Level.WARNING, "Ignoring wrong wsdl={0}. It should start with {1}. Going to generate and publish a new WSDL.", new Object[]{wsdlFile, JAXWS_WSDL_DD_DIR});
                return null;
            }
            try {
                wsdl = this.loader.getResource('/' + wsdlFile);
            }
            catch (MalformedURLException e) {
                throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(wsdlFile), (Throwable)e, xsr);
            }
            if (wsdl == null) {
                throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_WSDL_NOT_FOUND(wsdlFile), xsr);
            }
            SDDocumentSource docInfo = this.docs.get(wsdl.toExternalForm());
            assert (docInfo != null);
            return docInfo;
        }
        return null;
    }

    private EntityResolver createEntityResolver() {
        try {
            return XmlUtil.createEntityResolver(this.loader.getCatalogFile());
        }
        catch (MalformedURLException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    protected String getAttribute(Attributes attrs, String name) {
        String value = attrs.getValue(name);
        if (value != null) {
            value = value.trim();
        }
        return value;
    }

    protected QName getQNameAttribute(Attributes attrs, String name) {
        String value = this.getAttribute(attrs, name);
        if (value == null || value.equals("")) {
            return null;
        }
        return QName.valueOf(value);
    }

    protected String getNonEmptyAttribute(XMLStreamReader reader, Attributes attrs, String name) {
        String value = this.getAttribute(attrs, name);
        if (value != null && value.equals("")) {
            DeploymentDescriptorParser.failWithLocalName("runtime.parser.invalidAttributeValue", reader, name);
        }
        return value;
    }

    protected String getMandatoryAttribute(XMLStreamReader reader, Attributes attrs, String name) {
        String value = this.getAttribute(attrs, name);
        if (value == null) {
            DeploymentDescriptorParser.failWithLocalName("runtime.parser.missing.attribute", reader, name);
        }
        return value;
    }

    protected String getMandatoryNonEmptyAttribute(XMLStreamReader reader, Attributes attributes, String name) {
        String value = this.getAttribute(attributes, name);
        if (value == null) {
            DeploymentDescriptorParser.failWithLocalName("runtime.parser.missing.attribute", reader, name);
        } else if (value.equals("")) {
            DeploymentDescriptorParser.failWithLocalName("runtime.parser.invalidAttributeValue", reader, name);
        }
        return value;
    }

    protected boolean setHandlersAndRoles(WSBinding binding, XMLStreamReader reader, QName serviceName, QName portName) {
        if (reader.getEventType() == 2 || !reader.getName().equals(HandlerChainsModel.QNAME_HANDLER_CHAINS)) {
            return false;
        }
        HandlerAnnotationInfo handlerInfo = HandlerChainsModel.parseHandlerFile(reader, this.classLoader, serviceName, portName, binding);
        binding.setHandlerChain(handlerInfo.getHandlers());
        if (binding instanceof SOAPBinding) {
            ((SOAPBinding)binding).setRoles(handlerInfo.getRoles());
        }
        XMLStreamReaderUtil.nextContent(reader);
        return true;
    }

    protected ExternalMetadataFeature configureExternalMetadataReader(XMLStreamReader reader) {
        ExternalMetadataFeature.Builder featureBuilder = null;
        while (QNAME_EXT_METADA.equals(reader.getName())) {
            if (reader.getEventType() == 1) {
                String res;
                Attributes attrs = XMLStreamReaderUtil.getAttributes(reader);
                String file = this.getAttribute(attrs, ATTR_FILE);
                if (file != null) {
                    if (featureBuilder == null) {
                        featureBuilder = ExternalMetadataFeature.builder();
                    }
                    featureBuilder.addFiles(new File(file));
                }
                if ((res = this.getAttribute(attrs, ATTR_RESOURCE)) != null) {
                    if (featureBuilder == null) {
                        featureBuilder = ExternalMetadataFeature.builder();
                    }
                    featureBuilder.addResources(res);
                }
            }
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        return this.buildFeature(featureBuilder);
    }

    private ExternalMetadataFeature buildFeature(ExternalMetadataFeature.Builder builder) {
        return builder != null ? builder.build() : null;
    }

    protected static void fail(String key, XMLStreamReader reader) {
        logger.log(Level.SEVERE, "{0}{1}", new Object[]{key, reader.getLocation().getLineNumber()});
        throw new ServerRtException(key, Integer.toString(reader.getLocation().getLineNumber()));
    }

    protected static void failWithFullName(String key, XMLStreamReader reader) {
        throw new ServerRtException(key, reader.getLocation().getLineNumber(), reader.getName());
    }

    protected static void failWithLocalName(String key, XMLStreamReader reader) {
        throw new ServerRtException(key, reader.getLocation().getLineNumber(), reader.getLocalName());
    }

    protected static void failWithLocalName(String key, XMLStreamReader reader, String arg) {
        throw new ServerRtException(key, reader.getLocation().getLineNumber(), reader.getLocalName(), arg);
    }

    protected Class loadClass(String name) {
        try {
            return Class.forName(name, true, this.classLoader);
        }
        catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new ServerRtException("runtime.parser.classNotFound", name);
        }
    }

    private Class getImplementorClass(String name, XMLStreamReader xsr) {
        try {
            return Class.forName(name, true, this.classLoader);
        }
        catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new LocatableWebServiceException(ServerMessages.RUNTIME_PARSER_CLASS_NOT_FOUND(name), (Throwable)e, xsr);
        }
    }

    public static interface AdapterFactory<A> {
        public A createAdapter(String var1, String var2, WSEndpoint<?> var3);
    }
}

