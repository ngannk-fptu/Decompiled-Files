/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.OutputPropertiesFactory
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc.trax;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.DOMCache;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.dom.DOMWSFilter;
import org.apache.xalan.xsltc.dom.SAXImpl;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.apache.xalan.xsltc.runtime.output.TransletOutputHandlerFactory;
import org.apache.xalan.xsltc.trax.DOM2TO;
import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;
import org.apache.xalan.xsltc.trax.XSLTCSource;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.XMLReaderManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public final class TransformerImpl
extends Transformer
implements DOMCache,
ErrorListener {
    private static final String EMPTY_STRING = "";
    private static final String NO_STRING = "no";
    private static final String YES_STRING = "yes";
    private static final String XML_STRING = "xml";
    private static final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
    private AbstractTranslet _translet = null;
    private String _method = null;
    private String _encoding = null;
    private String _sourceSystemId = null;
    private ErrorListener _errorListener = this;
    private URIResolver _uriResolver = null;
    private Properties _properties;
    private Properties _propertiesClone;
    private TransletOutputHandlerFactory _tohFactory = null;
    private DOM _dom = null;
    private int _indentNumber;
    private TransformerFactoryImpl _tfactory = null;
    private OutputStream _ostream = null;
    private XSLTCDTMManager _dtmManager = null;
    private XMLReaderManager _readerManager = XMLReaderManager.getInstance();
    private boolean _isIdentity = false;
    private boolean _isSecureProcessing = false;
    private Hashtable _parameters = null;

    protected TransformerImpl(Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory) {
        this(null, outputProperties, indentNumber, tfactory);
        this._isIdentity = true;
    }

    protected TransformerImpl(Translet translet, Properties outputProperties, int indentNumber, TransformerFactoryImpl tfactory) {
        this._translet = (AbstractTranslet)translet;
        this._properties = this.createOutputProperties(outputProperties);
        this._propertiesClone = (Properties)this._properties.clone();
        this._indentNumber = indentNumber;
        this._tfactory = tfactory;
    }

    public boolean isSecureProcessing() {
        return this._isSecureProcessing;
    }

    public void setSecureProcessing(boolean flag) {
        this._isSecureProcessing = flag;
    }

    protected AbstractTranslet getTranslet() {
        return this._translet;
    }

    public boolean isIdentity() {
        return this._isIdentity;
    }

    @Override
    public void transform(Source source, Result result) throws TransformerException {
        SerializationHandler toHandler;
        if (!this._isIdentity) {
            if (this._translet == null) {
                ErrorMsg err = new ErrorMsg("JAXP_NO_TRANSLET_ERR");
                throw new TransformerException(err.toString());
            }
            this.transferOutputProperties(this._translet);
        }
        if ((toHandler = this.getOutputHandler(result)) == null) {
            ErrorMsg err = new ErrorMsg("JAXP_NO_HANDLER_ERR");
            throw new TransformerException(err.toString());
        }
        if (this._uriResolver != null && !this._isIdentity) {
            this._translet.setDOMCache(this);
        }
        if (this._isIdentity) {
            this.transferOutputProperties(toHandler);
        }
        this.transform(source, toHandler, this._encoding);
        if (result instanceof DOMResult) {
            ((DOMResult)result).setNode(this._tohFactory.getNode());
        }
    }

    public SerializationHandler getOutputHandler(Result result) throws TransformerException {
        this._method = (String)this._properties.get("method");
        this._encoding = this._properties.getProperty("encoding");
        this._tohFactory = TransletOutputHandlerFactory.newInstance();
        this._tohFactory.setEncoding(this._encoding);
        if (this._method != null) {
            this._tohFactory.setOutputMethod(this._method);
        }
        if (this._indentNumber >= 0) {
            this._tohFactory.setIndentNumber(this._indentNumber);
        }
        try {
            if (result instanceof SAXResult) {
                SAXResult target = (SAXResult)result;
                ContentHandler handler = target.getHandler();
                this._tohFactory.setHandler(handler);
                LexicalHandler lexicalHandler = target.getLexicalHandler();
                if (lexicalHandler != null) {
                    this._tohFactory.setLexicalHandler(lexicalHandler);
                }
                this._tohFactory.setOutputType(1);
                return this._tohFactory.getSerializationHandler();
            }
            if (result instanceof DOMResult) {
                this._tohFactory.setNode(((DOMResult)result).getNode());
                this._tohFactory.setNextSibling(((DOMResult)result).getNextSibling());
                this._tohFactory.setOutputType(2);
                return this._tohFactory.getSerializationHandler();
            }
            if (result instanceof StreamResult) {
                StreamResult target = (StreamResult)result;
                this._tohFactory.setOutputType(0);
                Writer writer = target.getWriter();
                if (writer != null) {
                    this._tohFactory.setWriter(writer);
                    return this._tohFactory.getSerializationHandler();
                }
                OutputStream ostream = target.getOutputStream();
                if (ostream != null) {
                    this._tohFactory.setOutputStream(ostream);
                    return this._tohFactory.getSerializationHandler();
                }
                String systemId = result.getSystemId();
                if (systemId == null) {
                    ErrorMsg err = new ErrorMsg("JAXP_NO_RESULT_ERR");
                    throw new TransformerException(err.toString());
                }
                URL url = null;
                if (systemId.startsWith("file:")) {
                    url = new URL(systemId);
                    this._ostream = new FileOutputStream(url.getFile());
                    this._tohFactory.setOutputStream(this._ostream);
                    return this._tohFactory.getSerializationHandler();
                }
                if (systemId.startsWith("http:")) {
                    url = new URL(systemId);
                    URLConnection connection = url.openConnection();
                    this._ostream = connection.getOutputStream();
                    this._tohFactory.setOutputStream(this._ostream);
                    return this._tohFactory.getSerializationHandler();
                }
                url = new File(systemId).toURL();
                this._ostream = new FileOutputStream(url.getFile());
                this._tohFactory.setOutputStream(this._ostream);
                return this._tohFactory.getSerializationHandler();
            }
        }
        catch (UnknownServiceException e) {
            throw new TransformerException(e);
        }
        catch (ParserConfigurationException e) {
            throw new TransformerException(e);
        }
        catch (IOException e) {
            throw new TransformerException(e);
        }
        return null;
    }

    protected void setDOM(DOM dom) {
        this._dom = dom;
    }

    private DOM getDOM(Source source) throws TransformerException {
        try {
            DOM dom = null;
            if (source != null) {
                boolean hasIdCall;
                DOMWSFilter wsfilter = this._translet != null && this._translet instanceof StripFilter ? new DOMWSFilter(this._translet) : null;
                boolean bl = hasIdCall = this._translet != null ? this._translet.hasIdCall() : false;
                if (this._dtmManager == null) {
                    this._dtmManager = (XSLTCDTMManager)this._tfactory.getDTMManagerClass().newInstance();
                }
                dom = (DOM)((Object)this._dtmManager.getDTM(source, false, wsfilter, true, false, false, 0, hasIdCall));
            } else if (this._dom != null) {
                dom = this._dom;
                this._dom = null;
            } else {
                return null;
            }
            if (!this._isIdentity) {
                this._translet.prepassDocument(dom);
            }
            return dom;
        }
        catch (Exception e) {
            if (this._errorListener != null) {
                this.postErrorToListener(e.getMessage());
            }
            throw new TransformerException(e);
        }
    }

    protected TransformerFactoryImpl getTransformerFactory() {
        return this._tfactory;
    }

    protected TransletOutputHandlerFactory getTransletOutputHandlerFactory() {
        return this._tohFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void transformIdentity(Source source, SerializationHandler handler) throws Exception {
        if (source != null) {
            this._sourceSystemId = source.getSystemId();
        }
        if (source instanceof StreamSource) {
            StreamSource stream = (StreamSource)source;
            InputStream streamInput = stream.getInputStream();
            Reader streamReader = stream.getReader();
            XMLReader reader = this._readerManager.getXMLReader();
            try {
                InputSource input;
                try {
                    reader.setProperty(LEXICAL_HANDLER_PROPERTY, handler);
                }
                catch (SAXException sAXException) {
                    // empty catch block
                }
                reader.setContentHandler((ContentHandler)handler);
                if (streamInput != null) {
                    input = new InputSource(streamInput);
                    input.setSystemId(this._sourceSystemId);
                } else if (streamReader != null) {
                    input = new InputSource(streamReader);
                    input.setSystemId(this._sourceSystemId);
                } else if (this._sourceSystemId != null) {
                    input = new InputSource(this._sourceSystemId);
                } else {
                    ErrorMsg err = new ErrorMsg("JAXP_NO_SOURCE_ERR");
                    throw new TransformerException(err.toString());
                }
                reader.parse(input);
            }
            finally {
                this._readerManager.releaseXMLReader(reader);
            }
        }
        if (source instanceof SAXSource) {
            SAXSource sax = (SAXSource)source;
            XMLReader reader = sax.getXMLReader();
            InputSource input = sax.getInputSource();
            boolean userReader = true;
            try {
                if (reader == null) {
                    reader = this._readerManager.getXMLReader();
                    userReader = false;
                }
                try {
                    reader.setProperty(LEXICAL_HANDLER_PROPERTY, handler);
                }
                catch (SAXException sAXException) {
                    // empty catch block
                }
                reader.setContentHandler((ContentHandler)handler);
                reader.parse(input);
            }
            finally {
                if (!userReader) {
                    this._readerManager.releaseXMLReader(reader);
                }
            }
        }
        if (source instanceof DOMSource) {
            DOMSource domsrc = (DOMSource)source;
            new DOM2TO(domsrc.getNode(), handler).parse();
        } else if (source instanceof XSLTCSource) {
            DOM dom = ((XSLTCSource)source).getDOM(null, this._translet);
            ((SAXImpl)dom).copy(handler);
        } else {
            ErrorMsg err = new ErrorMsg("JAXP_NO_SOURCE_ERR");
            throw new TransformerException(err.toString());
        }
    }

    private void transform(Source source, SerializationHandler handler, String encoding) throws TransformerException {
        try {
            if (source instanceof StreamSource && source.getSystemId() == null && ((StreamSource)source).getInputStream() == null && ((StreamSource)source).getReader() == null || source instanceof SAXSource && ((SAXSource)source).getInputSource() == null && ((SAXSource)source).getXMLReader() == null || source instanceof DOMSource && ((DOMSource)source).getNode() == null) {
                DocumentBuilderFactory builderF = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderF.newDocumentBuilder();
                String systemID = source.getSystemId();
                source = new DOMSource(builder.newDocument());
                if (systemID != null) {
                    source.setSystemId(systemID);
                }
            }
            if (this._isIdentity) {
                this.transformIdentity(source, handler);
            } else {
                this._translet.transform(this.getDOM(source), handler);
            }
        }
        catch (TransletException e) {
            if (this._errorListener != null) {
                this.postErrorToListener(e.getMessage());
            }
            throw new TransformerException(e);
        }
        catch (RuntimeException e) {
            if (this._errorListener != null) {
                this.postErrorToListener(e.getMessage());
            }
            throw new TransformerException(e);
        }
        catch (Exception e) {
            if (this._errorListener != null) {
                this.postErrorToListener(e.getMessage());
            }
            throw new TransformerException(e);
        }
        finally {
            this._dtmManager = null;
        }
        if (this._ostream != null) {
            try {
                this._ostream.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this._ostream = null;
        }
    }

    @Override
    public ErrorListener getErrorListener() {
        return this._errorListener;
    }

    @Override
    public void setErrorListener(ErrorListener listener) throws IllegalArgumentException {
        if (listener == null) {
            ErrorMsg err = new ErrorMsg("ERROR_LISTENER_NULL_ERR", "Transformer");
            throw new IllegalArgumentException(err.toString());
        }
        this._errorListener = listener;
        if (this._translet != null) {
            this._translet.setMessageHandler(new MessageHandler(this._errorListener));
        }
    }

    private void postErrorToListener(String message) {
        try {
            this._errorListener.error(new TransformerException(message));
        }
        catch (TransformerException transformerException) {
            // empty catch block
        }
    }

    private void postWarningToListener(String message) {
        try {
            this._errorListener.warning(new TransformerException(message));
        }
        catch (TransformerException transformerException) {
            // empty catch block
        }
    }

    private String makeCDATAString(Hashtable cdata) {
        if (cdata == null) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        Enumeration elements = cdata.keys();
        if (elements.hasMoreElements()) {
            result.append((String)elements.nextElement());
            while (elements.hasMoreElements()) {
                String element = (String)elements.nextElement();
                result.append(' ');
                result.append(element);
            }
        }
        return result.toString();
    }

    @Override
    public Properties getOutputProperties() {
        return (Properties)this._properties.clone();
    }

    @Override
    public String getOutputProperty(String name) throws IllegalArgumentException {
        if (!this.validOutputProperty(name)) {
            ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", name);
            throw new IllegalArgumentException(err.toString());
        }
        return this._properties.getProperty(name);
    }

    @Override
    public void setOutputProperties(Properties properties) throws IllegalArgumentException {
        if (properties != null) {
            Enumeration<?> names = properties.propertyNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                if (this.isDefaultProperty(name, properties)) continue;
                if (this.validOutputProperty(name)) {
                    this._properties.setProperty(name, properties.getProperty(name));
                    continue;
                }
                ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", name);
                throw new IllegalArgumentException(err.toString());
            }
        } else {
            this._properties = this._propertiesClone;
        }
    }

    @Override
    public void setOutputProperty(String name, String value) throws IllegalArgumentException {
        if (!this.validOutputProperty(name)) {
            ErrorMsg err = new ErrorMsg("JAXP_UNKNOWN_PROP_ERR", name);
            throw new IllegalArgumentException(err.toString());
        }
        this._properties.setProperty(name, value);
    }

    private void transferOutputProperties(AbstractTranslet translet) {
        if (this._properties == null) {
            return;
        }
        Enumeration<?> names = this._properties.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            String value = (String)this._properties.get(name);
            if (value == null) continue;
            if (name.equals("encoding")) {
                translet._encoding = value;
                continue;
            }
            if (name.equals("method")) {
                translet._method = value;
                continue;
            }
            if (name.equals("doctype-public")) {
                translet._doctypePublic = value;
                continue;
            }
            if (name.equals("doctype-system")) {
                translet._doctypeSystem = value;
                continue;
            }
            if (name.equals("media-type")) {
                translet._mediaType = value;
                continue;
            }
            if (name.equals("standalone")) {
                translet._standalone = value;
                continue;
            }
            if (name.equals("version")) {
                translet._version = value;
                continue;
            }
            if (name.equals("omit-xml-declaration")) {
                translet._omitHeader = value != null && value.toLowerCase().equals(YES_STRING);
                continue;
            }
            if (name.equals("indent")) {
                translet._indent = value != null && value.toLowerCase().equals(YES_STRING);
                continue;
            }
            if (!name.equals("cdata-section-elements") || value == null) continue;
            translet._cdata = null;
            StringTokenizer e = new StringTokenizer(value);
            while (e.hasMoreTokens()) {
                translet.addCdataElement(e.nextToken());
            }
        }
    }

    public void transferOutputProperties(SerializationHandler handler) {
        if (this._properties == null) {
            return;
        }
        String doctypePublic = null;
        String doctypeSystem = null;
        Enumeration<?> names = this._properties.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            String value = (String)this._properties.get(name);
            if (value == null) continue;
            if (name.equals("doctype-public")) {
                doctypePublic = value;
                continue;
            }
            if (name.equals("doctype-system")) {
                doctypeSystem = value;
                continue;
            }
            if (name.equals("media-type")) {
                handler.setMediaType(value);
                continue;
            }
            if (name.equals("standalone")) {
                handler.setStandalone(value);
                continue;
            }
            if (name.equals("version")) {
                handler.setVersion(value);
                continue;
            }
            if (name.equals("omit-xml-declaration")) {
                handler.setOmitXMLDeclaration(value != null && value.toLowerCase().equals(YES_STRING));
                continue;
            }
            if (name.equals("indent")) {
                handler.setIndent(value != null && value.toLowerCase().equals(YES_STRING));
                continue;
            }
            if (!name.equals("cdata-section-elements") || value == null) continue;
            StringTokenizer e = new StringTokenizer(value);
            Vector<String> uriAndLocalNames = null;
            while (e.hasMoreTokens()) {
                String localName;
                String uri;
                String token = e.nextToken();
                int lastcolon = token.lastIndexOf(58);
                if (lastcolon > 0) {
                    uri = token.substring(0, lastcolon);
                    localName = token.substring(lastcolon + 1);
                } else {
                    uri = null;
                    localName = token;
                }
                if (uriAndLocalNames == null) {
                    uriAndLocalNames = new Vector<String>();
                }
                uriAndLocalNames.addElement(uri);
                uriAndLocalNames.addElement(localName);
            }
            handler.setCdataSectionElements(uriAndLocalNames);
        }
        if (doctypePublic != null || doctypeSystem != null) {
            handler.setDoctype(doctypeSystem, doctypePublic);
        }
    }

    private Properties createOutputProperties(Properties outputProperties) {
        String method;
        Properties defaults = new Properties();
        this.setDefaults(defaults, XML_STRING);
        Properties base = new Properties(defaults);
        if (outputProperties != null) {
            Enumeration<?> names = outputProperties.propertyNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                base.setProperty(name, outputProperties.getProperty(name));
            }
        } else {
            base.setProperty("encoding", this._translet._encoding);
            if (this._translet._method != null) {
                base.setProperty("method", this._translet._method);
            }
        }
        if ((method = base.getProperty("method")) != null) {
            if (method.equals("html")) {
                this.setDefaults(defaults, "html");
            } else if (method.equals("text")) {
                this.setDefaults(defaults, "text");
            }
        }
        return base;
    }

    private void setDefaults(Properties props, String method) {
        Properties method_props = OutputPropertiesFactory.getDefaultMethodProperties((String)method);
        Enumeration<?> names = method_props.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            props.setProperty(name, method_props.getProperty(name));
        }
    }

    private boolean validOutputProperty(String name) {
        return name.equals("encoding") || name.equals("method") || name.equals("indent") || name.equals("doctype-public") || name.equals("doctype-system") || name.equals("cdata-section-elements") || name.equals("media-type") || name.equals("omit-xml-declaration") || name.equals("standalone") || name.equals("version") || name.charAt(0) == '{';
    }

    private boolean isDefaultProperty(String name, Properties properties) {
        return properties.get(name) == null;
    }

    @Override
    public void setParameter(String name, Object value) {
        if (value == null) {
            ErrorMsg err = new ErrorMsg("JAXP_INVALID_SET_PARAM_VALUE", name);
            throw new IllegalArgumentException(err.toString());
        }
        if (this._isIdentity) {
            if (this._parameters == null) {
                this._parameters = new Hashtable();
            }
            this._parameters.put(name, value);
        } else {
            this._translet.addParameter(name, value);
        }
    }

    @Override
    public void clearParameters() {
        if (this._isIdentity && this._parameters != null) {
            this._parameters.clear();
        } else {
            this._translet.clearParameters();
        }
    }

    @Override
    public final Object getParameter(String name) {
        if (this._isIdentity) {
            return this._parameters != null ? this._parameters.get(name) : null;
        }
        return this._translet.getParameter(name);
    }

    @Override
    public URIResolver getURIResolver() {
        return this._uriResolver;
    }

    @Override
    public void setURIResolver(URIResolver resolver) {
        this._uriResolver = resolver;
    }

    @Override
    public DOM retrieveDocument(String baseURI, String href, Translet translet) {
        try {
            Source resolvedSource;
            if (href.length() == 0) {
                href = baseURI;
            }
            if ((resolvedSource = this._uriResolver.resolve(href, baseURI)) == null) {
                StreamSource streamSource = new StreamSource(SystemIDResolver.getAbsoluteURI(href, baseURI));
                return this.getDOM(streamSource);
            }
            return this.getDOM(resolvedSource);
        }
        catch (TransformerException e) {
            if (this._errorListener != null) {
                this.postErrorToListener("File not found: " + e.getMessage());
            }
            return null;
        }
    }

    @Override
    public void error(TransformerException e) throws TransformerException {
        Throwable wrapped = e.getException();
        if (wrapped != null) {
            System.err.println(new ErrorMsg("ERROR_PLUS_WRAPPED_MSG", (Object)e.getMessageAndLocation(), (Object)wrapped.getMessage()));
        } else {
            System.err.println(new ErrorMsg("ERROR_MSG", e.getMessageAndLocation()));
        }
        throw e;
    }

    @Override
    public void fatalError(TransformerException e) throws TransformerException {
        Throwable wrapped = e.getException();
        if (wrapped != null) {
            System.err.println(new ErrorMsg("FATAL_ERR_PLUS_WRAPPED_MSG", (Object)e.getMessageAndLocation(), (Object)wrapped.getMessage()));
        } else {
            System.err.println(new ErrorMsg("FATAL_ERR_MSG", e.getMessageAndLocation()));
        }
        throw e;
    }

    @Override
    public void warning(TransformerException e) throws TransformerException {
        Throwable wrapped = e.getException();
        if (wrapped != null) {
            System.err.println(new ErrorMsg("WARNING_PLUS_WRAPPED_MSG", (Object)e.getMessageAndLocation(), (Object)wrapped.getMessage()));
        } else {
            System.err.println(new ErrorMsg("WARNING_MSG", e.getMessageAndLocation()));
        }
    }

    @Override
    public void reset() {
        this._method = null;
        this._encoding = null;
        this._sourceSystemId = null;
        this._errorListener = this;
        this._uriResolver = null;
        this._dom = null;
        this._parameters = null;
        this._indentNumber = 0;
        this.setOutputProperties(null);
    }

    static class MessageHandler
    extends org.apache.xalan.xsltc.runtime.MessageHandler {
        private ErrorListener _errorListener;

        public MessageHandler(ErrorListener errorListener) {
            this._errorListener = errorListener;
        }

        @Override
        public void displayMessage(String msg) {
            if (this._errorListener == null) {
                System.err.println(msg);
            } else {
                try {
                    this._errorListener.warning(new TransformerException(msg));
                }
                catch (TransformerException transformerException) {
                    // empty catch block
                }
            }
        }
    }
}

