/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.Serializer
 *  org.apache.xml.serializer.SerializerFactory
 *  org.apache.xml.serializer.TreeWalker
 */
package org.apache.xalan.transformer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
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
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xalan.transformer.SerializerSwitcher;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.apache.xml.serializer.TreeWalker;
import org.apache.xml.utils.DOMBuilder;
import org.apache.xml.utils.DefaultErrorHandler;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLReaderManager;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class TransformerIdentityImpl
extends Transformer
implements TransformerHandler,
DeclHandler {
    boolean m_flushedStartDoc = false;
    private FileOutputStream m_outputStream = null;
    private ContentHandler m_resultContentHandler;
    private LexicalHandler m_resultLexicalHandler;
    private DTDHandler m_resultDTDHandler;
    private DeclHandler m_resultDeclHandler;
    private Serializer m_serializer;
    private Result m_result;
    private String m_systemID;
    private Hashtable m_params;
    private ErrorListener m_errorListener = new DefaultErrorHandler(false);
    URIResolver m_URIResolver;
    private OutputProperties m_outputFormat = new OutputProperties("xml");
    boolean m_foundFirstElement;
    private boolean m_isSecureProcessing = false;

    public TransformerIdentityImpl(boolean isSecureProcessing) {
        this.m_isSecureProcessing = isSecureProcessing;
    }

    public TransformerIdentityImpl() {
        this(false);
    }

    @Override
    public void setResult(Result result) throws IllegalArgumentException {
        if (null == result) {
            throw new IllegalArgumentException(XSLMessages.createMessage("ER_RESULT_NULL", null));
        }
        this.m_result = result;
    }

    @Override
    public void setSystemId(String systemID) {
        this.m_systemID = systemID;
    }

    @Override
    public String getSystemId() {
        return this.m_systemID;
    }

    @Override
    public Transformer getTransformer() {
        return this;
    }

    @Override
    public void reset() {
        this.m_flushedStartDoc = false;
        this.m_foundFirstElement = false;
        this.m_outputStream = null;
        this.clearParameters();
        this.m_result = null;
        this.m_resultContentHandler = null;
        this.m_resultDeclHandler = null;
        this.m_resultDTDHandler = null;
        this.m_resultLexicalHandler = null;
        this.m_serializer = null;
        this.m_systemID = null;
        this.m_URIResolver = null;
        this.m_outputFormat = new OutputProperties("xml");
    }

    private void createResultContentHandler(Result outputTarget) throws TransformerException {
        if (outputTarget instanceof SAXResult) {
            SAXResult saxResult = (SAXResult)outputTarget;
            this.m_resultContentHandler = saxResult.getHandler();
            this.m_resultLexicalHandler = saxResult.getLexicalHandler();
            if (this.m_resultContentHandler instanceof Serializer) {
                this.m_serializer = (Serializer)this.m_resultContentHandler;
            }
        } else if (outputTarget instanceof DOMResult) {
            DOMBuilder domBuilder;
            Document doc;
            short type;
            DOMResult domResult = (DOMResult)outputTarget;
            Node outputNode = domResult.getNode();
            Node nextSibling = domResult.getNextSibling();
            if (null != outputNode) {
                type = outputNode.getNodeType();
                doc = 9 == type ? (Document)outputNode : outputNode.getOwnerDocument();
            } else {
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    dbf.setNamespaceAware(true);
                    if (this.m_isSecureProcessing) {
                        try {
                            dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                        }
                        catch (ParserConfigurationException parserConfigurationException) {
                            // empty catch block
                        }
                    }
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    doc = db.newDocument();
                }
                catch (ParserConfigurationException pce) {
                    throw new TransformerException(pce);
                }
                outputNode = doc;
                type = outputNode.getNodeType();
                ((DOMResult)outputTarget).setNode(outputNode);
            }
            DOMBuilder dOMBuilder = domBuilder = 11 == type ? new DOMBuilder(doc, (DocumentFragment)outputNode) : new DOMBuilder(doc, outputNode);
            if (nextSibling != null) {
                domBuilder.setNextSibling(nextSibling);
            }
            this.m_resultContentHandler = domBuilder;
            this.m_resultLexicalHandler = domBuilder;
        } else if (outputTarget instanceof StreamResult) {
            StreamResult sresult = (StreamResult)outputTarget;
            try {
                Serializer serializer;
                this.m_serializer = serializer = SerializerFactory.getSerializer((Properties)this.m_outputFormat.getProperties());
                if (null != sresult.getWriter()) {
                    serializer.setWriter(sresult.getWriter());
                } else if (null != sresult.getOutputStream()) {
                    serializer.setOutputStream(sresult.getOutputStream());
                } else if (null != sresult.getSystemId()) {
                    String fileURL = sresult.getSystemId();
                    if (fileURL.startsWith("file:///")) {
                        fileURL = fileURL.substring(8).indexOf(":") > 0 ? fileURL.substring(8) : fileURL.substring(7);
                    } else if (fileURL.startsWith("file:/")) {
                        fileURL = fileURL.substring(6).indexOf(":") > 0 ? fileURL.substring(6) : fileURL.substring(5);
                    }
                    this.m_outputStream = new FileOutputStream(fileURL);
                    serializer.setOutputStream((OutputStream)this.m_outputStream);
                } else {
                    throw new TransformerException(XSLMessages.createMessage("ER_NO_OUTPUT_SPECIFIED", null));
                }
                this.m_resultContentHandler = serializer.asContentHandler();
            }
            catch (IOException ioe) {
                throw new TransformerException(ioe);
            }
        } else {
            throw new TransformerException(XSLMessages.createMessage("ER_CANNOT_TRANSFORM_TO_RESULT_TYPE", new Object[]{outputTarget.getClass().getName()}));
        }
        if (this.m_resultContentHandler instanceof DTDHandler) {
            this.m_resultDTDHandler = (DTDHandler)((Object)this.m_resultContentHandler);
        }
        if (this.m_resultContentHandler instanceof DeclHandler) {
            this.m_resultDeclHandler = (DeclHandler)((Object)this.m_resultContentHandler);
        }
        if (this.m_resultContentHandler instanceof LexicalHandler) {
            this.m_resultLexicalHandler = (LexicalHandler)((Object)this.m_resultContentHandler);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * WARNING - void declaration
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void transform(Source source, Result outputTarget) throws TransformerException {
        this.createResultContentHandler(outputTarget);
        if (source instanceof StreamSource && source.getSystemId() == null && ((StreamSource)source).getInputStream() == null && ((StreamSource)source).getReader() == null || source instanceof SAXSource && ((SAXSource)source).getInputSource() == null && ((SAXSource)source).getXMLReader() == null || source instanceof DOMSource && ((DOMSource)source).getNode() == null) {
            try {
                DocumentBuilderFactory builderF = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderF.newDocumentBuilder();
                String systemID = source.getSystemId();
                source = new DOMSource(builder.newDocument());
                if (systemID != null) {
                    source.setSystemId(systemID);
                }
            }
            catch (ParserConfigurationException e) {
                throw new TransformerException(e.getMessage());
            }
        }
        try {
            Object inputHandler;
            if (source instanceof DOMSource) {
                DOMSource dsource = (DOMSource)source;
                this.m_systemID = dsource.getSystemId();
                Node dNode = dsource.getNode();
                if (null == dNode) {
                    String messageStr = XSLMessages.createMessage("ER_ILLEGAL_DOMSOURCE_INPUT", null);
                    throw new IllegalArgumentException(messageStr);
                }
                try {
                    if (dNode.getNodeType() == 2) {
                        this.startDocument();
                    }
                    try {
                        if (dNode.getNodeType() == 2) {
                            String data = dNode.getNodeValue();
                            char[] chars = data.toCharArray();
                            this.characters(chars, 0, chars.length);
                            return;
                        }
                        TreeWalker walker = new TreeWalker((ContentHandler)this, this.m_systemID);
                        walker.traverse(dNode);
                        return;
                    }
                    finally {
                        if (dNode.getNodeType() == 2) {
                            this.endDocument();
                        }
                    }
                }
                catch (SAXException se) {
                    throw new TransformerException(se);
                }
            }
            InputSource xmlSource = SAXSource.sourceToInputSource(source);
            if (null == xmlSource) {
                throw new TransformerException(XSLMessages.createMessage("ER_CANNOT_TRANSFORM_SOURCE_TYPE", new Object[]{source.getClass().getName()}));
            }
            if (null != xmlSource.getSystemId()) {
                this.m_systemID = xmlSource.getSystemId();
            }
            XMLReader reader = null;
            boolean managedReader = false;
            ContentHandler oldContentHandler = null;
            DTDHandler oldDtdHandler = null;
            boolean isDtdHandlerSet = false;
            HashMap<String, Object> oldProperties = new HashMap<String, Object>();
            try {
                if (source instanceof SAXSource) {
                    reader = ((SAXSource)source).getXMLReader();
                }
                if (null == reader) {
                    try {
                        reader = XMLReaderManager.getInstance().getXMLReader();
                        managedReader = true;
                    }
                    catch (SAXException se) {
                        throw new TransformerException(se);
                    }
                }
                try {
                    reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                }
                catch (SAXException se) {
                    // empty catch block
                }
                inputHandler = this;
                oldContentHandler = reader.getContentHandler();
                reader.setContentHandler((ContentHandler)inputHandler);
                if (inputHandler instanceof DTDHandler) {
                    isDtdHandlerSet = true;
                    oldDtdHandler = reader.getDTDHandler();
                    reader.setDTDHandler((DTDHandler)inputHandler);
                }
                try {
                    if (inputHandler instanceof LexicalHandler) {
                        oldProperties.put("http://xml.org/sax/properties/lexical-handler", reader.getProperty("http://xml.org/sax/properties/lexical-handler"));
                        reader.setProperty("http://xml.org/sax/properties/lexical-handler", inputHandler);
                    }
                    if (inputHandler instanceof DeclHandler) {
                        oldProperties.put("http://xml.org/sax/properties/declaration-handler", reader.getProperty("http://xml.org/sax/properties/declaration-handler"));
                        reader.setProperty("http://xml.org/sax/properties/declaration-handler", inputHandler);
                    }
                }
                catch (SAXException sAXException) {
                    // empty catch block
                }
                try {
                    if (inputHandler instanceof LexicalHandler) {
                        oldProperties.put("http://xml.org/sax/handlers/LexicalHandler", reader.getProperty("http://xml.org/sax/handlers/LexicalHandler"));
                        reader.setProperty("http://xml.org/sax/handlers/LexicalHandler", inputHandler);
                    }
                    if (inputHandler instanceof DeclHandler) {
                        oldProperties.put("http://xml.org/sax/handlers/DeclHandler", reader.getProperty("http://xml.org/sax/handlers/DeclHandler"));
                        reader.setProperty("http://xml.org/sax/handlers/DeclHandler", inputHandler);
                    }
                }
                catch (SAXNotRecognizedException sAXNotRecognizedException) {
                    // empty catch block
                }
                reader.parse(xmlSource);
            }
            catch (WrappedRuntimeException wre) {
                try {
                    void var11_29;
                    Exception exception = wre.getException();
                    while (var11_29 instanceof WrappedRuntimeException) {
                        Exception exception2 = ((WrappedRuntimeException)var11_29).getException();
                    }
                    throw new TransformerException(wre.getException());
                    catch (SAXException se) {
                        throw new TransformerException(se);
                    }
                    catch (IOException ioe) {
                        throw new TransformerException(ioe);
                    }
                }
                catch (Throwable throwable) {
                    reader.setContentHandler(oldContentHandler);
                    if (isDtdHandlerSet) {
                        reader.setDTDHandler(oldDtdHandler);
                    }
                    for (Map.Entry oldProperty : oldProperties.entrySet()) {
                        try {
                            reader.setProperty((String)oldProperty.getKey(), oldProperty.getValue());
                        }
                        catch (SAXNotRecognizedException sAXNotRecognizedException) {
                        }
                        catch (SAXNotSupportedException sAXNotSupportedException) {}
                    }
                    if (!managedReader) throw throwable;
                    XMLReaderManager.getInstance().releaseXMLReader(reader);
                    throw throwable;
                }
            }
            reader.setContentHandler(oldContentHandler);
            if (isDtdHandlerSet) {
                reader.setDTDHandler(oldDtdHandler);
            }
            inputHandler = oldProperties.entrySet().iterator();
            while (true) {
                if (!inputHandler.hasNext()) {
                    if (!managedReader) return;
                    XMLReaderManager.getInstance().releaseXMLReader(reader);
                    return;
                }
                Map.Entry entry = (Map.Entry)inputHandler.next();
                try {
                    reader.setProperty((String)entry.getKey(), entry.getValue());
                }
                catch (SAXNotRecognizedException sAXNotRecognizedException) {
                }
                catch (SAXNotSupportedException sAXNotSupportedException) {}
            }
        }
        finally {
            if (null != this.m_outputStream) {
                try {
                    this.m_outputStream.close();
                }
                catch (IOException se) {}
                this.m_outputStream = null;
            }
        }
    }

    @Override
    public void setParameter(String name, Object value) {
        if (value == null) {
            throw new IllegalArgumentException(XSLMessages.createMessage("ER_INVALID_SET_PARAM_VALUE", new Object[]{name}));
        }
        if (null == this.m_params) {
            this.m_params = new Hashtable();
        }
        this.m_params.put(name, value);
    }

    @Override
    public Object getParameter(String name) {
        if (null == this.m_params) {
            return null;
        }
        return this.m_params.get(name);
    }

    @Override
    public void clearParameters() {
        if (null == this.m_params) {
            return;
        }
        this.m_params.clear();
    }

    @Override
    public void setURIResolver(URIResolver resolver) {
        this.m_URIResolver = resolver;
    }

    @Override
    public URIResolver getURIResolver() {
        return this.m_URIResolver;
    }

    @Override
    public void setOutputProperties(Properties oformat) throws IllegalArgumentException {
        if (null != oformat) {
            String method = (String)oformat.get("method");
            this.m_outputFormat = null != method ? new OutputProperties(method) : new OutputProperties();
            this.m_outputFormat.copyFrom(oformat);
        } else {
            this.m_outputFormat = null;
        }
    }

    @Override
    public Properties getOutputProperties() {
        return (Properties)this.m_outputFormat.getProperties().clone();
    }

    @Override
    public void setOutputProperty(String name, String value) throws IllegalArgumentException {
        if (!OutputProperties.isLegalPropertyKey(name)) {
            throw new IllegalArgumentException(XSLMessages.createMessage("ER_OUTPUT_PROPERTY_NOT_RECOGNIZED", new Object[]{name}));
        }
        this.m_outputFormat.setProperty(name, value);
    }

    @Override
    public String getOutputProperty(String name) throws IllegalArgumentException {
        String value = null;
        OutputProperties props = this.m_outputFormat;
        value = props.getProperty(name);
        if (null == value && !OutputProperties.isLegalPropertyKey(name)) {
            throw new IllegalArgumentException(XSLMessages.createMessage("ER_OUTPUT_PROPERTY_NOT_RECOGNIZED", new Object[]{name}));
        }
        return value;
    }

    @Override
    public void setErrorListener(ErrorListener listener) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(XSLMessages.createMessage("ER_NULL_ERROR_HANDLER", null));
        }
        this.m_errorListener = listener;
    }

    @Override
    public ErrorListener getErrorListener() {
        return this.m_errorListener;
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        if (null != this.m_resultDTDHandler) {
            this.m_resultDTDHandler.notationDecl(name, publicId, systemId);
        }
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        if (null != this.m_resultDTDHandler) {
            this.m_resultDTDHandler.unparsedEntityDecl(name, publicId, systemId, notationName);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        try {
            if (null == this.m_resultContentHandler) {
                this.createResultContentHandler(this.m_result);
            }
        }
        catch (TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
        this.m_resultContentHandler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            if (null == this.m_resultContentHandler) {
                this.createResultContentHandler(this.m_result);
            }
        }
        catch (TransformerException te) {
            throw new SAXException(te.getMessage(), te);
        }
        this.m_flushedStartDoc = false;
        this.m_foundFirstElement = false;
    }

    protected final void flushStartDoc() throws SAXException {
        if (!this.m_flushedStartDoc) {
            if (this.m_resultContentHandler == null) {
                try {
                    this.createResultContentHandler(this.m_result);
                }
                catch (TransformerException te) {
                    throw new SAXException(te);
                }
            }
            this.m_resultContentHandler.startDocument();
            this.m_flushedStartDoc = true;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        this.flushStartDoc();
        this.m_resultContentHandler.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.flushStartDoc();
        this.m_resultContentHandler.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        this.flushStartDoc();
        this.m_resultContentHandler.endPrefixMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!this.m_foundFirstElement && null != this.m_serializer) {
            Serializer newSerializer;
            this.m_foundFirstElement = true;
            try {
                newSerializer = SerializerSwitcher.switchSerializerIfHTML(uri, localName, this.m_outputFormat.getProperties(), this.m_serializer);
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
            if (newSerializer != this.m_serializer) {
                try {
                    this.m_resultContentHandler = newSerializer.asContentHandler();
                }
                catch (IOException ioe) {
                    throw new SAXException(ioe);
                }
                if (this.m_resultContentHandler instanceof DTDHandler) {
                    this.m_resultDTDHandler = (DTDHandler)((Object)this.m_resultContentHandler);
                }
                if (this.m_resultContentHandler instanceof LexicalHandler) {
                    this.m_resultLexicalHandler = (LexicalHandler)((Object)this.m_resultContentHandler);
                }
                this.m_serializer = newSerializer;
            }
        }
        this.flushStartDoc();
        this.m_resultContentHandler.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.m_resultContentHandler.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.flushStartDoc();
        this.m_resultContentHandler.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.m_resultContentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.flushStartDoc();
        this.m_resultContentHandler.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        this.flushStartDoc();
        this.m_resultContentHandler.skippedEntity(name);
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.flushStartDoc();
        if (null != this.m_resultLexicalHandler) {
            this.m_resultLexicalHandler.startDTD(name, publicId, systemId);
        }
    }

    @Override
    public void endDTD() throws SAXException {
        if (null != this.m_resultLexicalHandler) {
            this.m_resultLexicalHandler.endDTD();
        }
    }

    @Override
    public void startEntity(String name) throws SAXException {
        if (null != this.m_resultLexicalHandler) {
            this.m_resultLexicalHandler.startEntity(name);
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        if (null != this.m_resultLexicalHandler) {
            this.m_resultLexicalHandler.endEntity(name);
        }
    }

    @Override
    public void startCDATA() throws SAXException {
        if (null != this.m_resultLexicalHandler) {
            this.m_resultLexicalHandler.startCDATA();
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        if (null != this.m_resultLexicalHandler) {
            this.m_resultLexicalHandler.endCDATA();
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        this.flushStartDoc();
        if (null != this.m_resultLexicalHandler) {
            this.m_resultLexicalHandler.comment(ch, start, length);
        }
    }

    @Override
    public void elementDecl(String name, String model) throws SAXException {
        if (null != this.m_resultDeclHandler) {
            this.m_resultDeclHandler.elementDecl(name, model);
        }
    }

    @Override
    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
        if (null != this.m_resultDeclHandler) {
            this.m_resultDeclHandler.attributeDecl(eName, aName, type, valueDefault, value);
        }
    }

    @Override
    public void internalEntityDecl(String name, String value) throws SAXException {
        if (null != this.m_resultDeclHandler) {
            this.m_resultDeclHandler.internalEntityDecl(name, value);
        }
    }

    @Override
    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
        if (null != this.m_resultDeclHandler) {
            this.m_resultDeclHandler.externalEntityDecl(name, publicId, systemId);
        }
    }
}

