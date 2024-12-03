/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.builder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.builder.BuilderAwareReader;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class StAXBuilder
implements OMXMLParserWrapper {
    private static final Log log = LogFactory.getLog(StAXBuilder.class);
    protected XMLStreamReader parser;
    protected OMFactoryEx omfactory;
    protected OMContainerEx target;
    protected boolean done = false;
    protected boolean cache = true;
    protected boolean parserAccessed = false;
    protected OMDocument document;
    protected String charEncoding = null;
    protected boolean _isClosed = false;
    protected boolean _releaseParserOnClose = false;
    protected CustomBuilder customBuilderForPayload = null;
    protected Map customBuilders = null;
    protected int maxDepthForCustomBuilders = -1;
    protected DataHandlerReader dataHandlerReader;
    protected int elementLevel = 0;
    protected Exception parserException;
    private final Map discardTracker = log.isDebugEnabled() ? new LinkedHashMap() : null;

    protected StAXBuilder(OMFactory ombuilderFactory, XMLStreamReader parser) {
        this.omfactory = (OMFactoryEx)ombuilderFactory;
        this.charEncoding = parser.getEncoding();
        this.initParser(parser);
    }

    protected StAXBuilder(OMFactory ombuilderFactory, XMLStreamReader parser, String characterEncoding) {
        this.omfactory = (OMFactoryEx)ombuilderFactory;
        this.charEncoding = characterEncoding;
        this.initParser(parser);
    }

    private void initParser(XMLStreamReader parser) {
        if (parser instanceof BuilderAwareReader) {
            ((BuilderAwareReader)((Object)parser)).setBuilder(this);
        }
        this.dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(parser);
        this.parser = parser;
    }

    protected StAXBuilder(XMLStreamReader parser) {
        this(OMAbstractFactory.getOMFactory(), parser);
    }

    protected StAXBuilder() {
    }

    public void init(InputStream inputStream, String charSetEncoding, String url, String contentType) throws OMException {
        try {
            this.parser = StAXUtils.createXMLStreamReader(inputStream);
        }
        catch (XMLStreamException e1) {
            throw new OMException(e1);
        }
        this.omfactory = (OMFactoryEx)OMAbstractFactory.getOMFactory();
    }

    public void setOMBuilderFactory(OMFactory ombuilderFactory) {
        this.omfactory = (OMFactoryEx)ombuilderFactory;
    }

    protected abstract void processNamespaceData(OMElement var1);

    protected void processAttributes(OMElement node) {
        int attribCount = this.parser.getAttributeCount();
        for (int i = 0; i < attribCount; ++i) {
            String uri = this.parser.getAttributeNamespace(i);
            String prefix = this.parser.getAttributePrefix(i);
            OMNamespace namespace = null;
            if (uri != null && uri.length() > 0 && (namespace = node.findNamespace(uri, prefix)) == null) {
                if (prefix == null || "".equals(prefix)) {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
                namespace = node.declareNamespace(uri, prefix);
            }
            OMAttribute attr = node.addAttribute(this.parser.getAttributeLocalName(i), this.parser.getAttributeValue(i), namespace);
            attr.setAttributeType(this.parser.getAttributeType(i));
        }
    }

    protected OMNode createOMText(int textType) {
        String text;
        if (this.dataHandlerReader != null && this.dataHandlerReader.isBinary()) {
            DataHandlerProvider dataHandlerObject;
            if (this.dataHandlerReader.isDeferred()) {
                dataHandlerObject = this.dataHandlerReader.getDataHandlerProvider();
            } else {
                try {
                    dataHandlerObject = this.dataHandlerReader.getDataHandler();
                }
                catch (XMLStreamException ex) {
                    throw new OMException(ex);
                }
            }
            OMText text2 = this.omfactory.createOMText((OMContainer)this.target, dataHandlerObject, this.dataHandlerReader.isOptimized(), true);
            String contentID = this.dataHandlerReader.getContentID();
            if (contentID != null) {
                text2.setContentID(contentID);
            }
            return text2;
        }
        try {
            text = this.parser.getText();
        }
        catch (RuntimeException ex) {
            this.parserException = ex;
            throw ex;
        }
        return this.omfactory.createOMText((OMContainer)this.target, text, textType, true);
    }

    private void discarded(OMContainerEx container) {
        container.discarded();
        if (this.discardTracker != null) {
            this.discardTracker.put(container, new Throwable());
        }
    }

    public void debugDiscarded(Object container) {
        Throwable t;
        if (log.isDebugEnabled() && this.discardTracker != null && (t = (Throwable)this.discardTracker.get(container)) != null) {
            log.debug((Object)"About to throw NodeUnavailableException. Location of the code that caused the node to be discarded/consumed:", t);
        }
    }

    public void discard(OMElement element) throws OMException {
        this.discard((OMContainer)element);
        element.discard();
    }

    public void discard(OMContainer container) throws OMException {
        try {
            if (container instanceof OMDocument) {
                if (container != this.document) {
                    throw new OMException("Called discard for a document that is not being built by this builder");
                }
                while (this.parserNext() != 8) {
                }
            } else {
                int targetDepth = this.elementLevel - 1;
                OMContainerEx current = this.target;
                while (current != container) {
                    if (current instanceof OMElement) {
                        --targetDepth;
                        current = (OMContainerEx)((OMElement)((Object)current)).getParent();
                        continue;
                    }
                    throw new OMException("Called discard for an element that is not being built by this builder");
                }
                while (this.elementLevel > targetDepth) {
                    this.parserNext();
                }
            }
            OMContainerEx current = this.target;
            while (true) {
                this.discarded(current);
                if (current == container) break;
                current = (OMContainerEx)((OMElement)((Object)current)).getParent();
            }
            if (container instanceof OMDocument) {
                this.target = null;
                this.done = true;
            } else {
                this.target = (OMContainerEx)((OMElement)container).getParent();
            }
        }
        catch (XMLStreamException e) {
            throw new OMException(e);
        }
    }

    public String getText() throws OMException {
        return this.parser.getText();
    }

    public String getNamespace() throws OMException {
        return this.parser.getNamespaceURI();
    }

    public int getNamespaceCount() throws OMException {
        try {
            return this.parser.getNamespaceCount();
        }
        catch (Exception e) {
            throw new OMException(e);
        }
    }

    public String getNamespacePrefix(int index) throws OMException {
        try {
            return this.parser.getNamespacePrefix(index);
        }
        catch (Exception e) {
            throw new OMException(e);
        }
    }

    public String getNamespaceUri(int index) throws OMException {
        try {
            return this.parser.getNamespaceURI(index);
        }
        catch (Exception e) {
            throw new OMException(e);
        }
    }

    public void setCache(boolean b) {
        if (this.parserAccessed && b) {
            throw new UnsupportedOperationException("parser accessed. cannot set cache");
        }
        this.cache = b;
    }

    public boolean isCache() {
        return this.cache;
    }

    public String getName() throws OMException {
        return this.parser.getLocalName();
    }

    public String getPrefix() throws OMException {
        return this.parser.getPrefix();
    }

    public int getAttributeCount() throws OMException {
        return this.parser.getAttributeCount();
    }

    public String getAttributeNamespace(int arg) throws OMException {
        return this.parser.getAttributeNamespace(arg);
    }

    public String getAttributeName(int arg) throws OMException {
        return this.parser.getAttributeNamespace(arg);
    }

    public String getAttributePrefix(int arg) throws OMException {
        return this.parser.getAttributeNamespace(arg);
    }

    public Object getParser() {
        if (this.parserAccessed) {
            throw new IllegalStateException("Parser already accessed!");
        }
        if (!this.cache) {
            this.parserAccessed = true;
            OMContainerEx current = this.target;
            while (current != null) {
                this.discarded(current);
                if (current instanceof OMElement) {
                    current = (OMContainerEx)((OMElement)((Object)current)).getParent();
                    continue;
                }
                current = null;
            }
            return this.parser;
        }
        throw new IllegalStateException("cache must be switched off to access the parser");
    }

    public boolean isCompleted() {
        return this.done;
    }

    protected abstract OMNode createOMElement() throws OMException;

    abstract int parserNext() throws XMLStreamException;

    public abstract int next() throws OMException;

    public CustomBuilder registerCustomBuilder(QName qName, int maxDepth, CustomBuilder customBuilder) {
        CustomBuilder old = null;
        if (this.customBuilders == null) {
            this.customBuilders = new HashMap();
        } else {
            old = (CustomBuilder)this.customBuilders.get(qName);
        }
        this.maxDepthForCustomBuilders = this.maxDepthForCustomBuilders > maxDepth ? this.maxDepthForCustomBuilders : maxDepth;
        this.customBuilders.put(qName, customBuilder);
        return old;
    }

    public CustomBuilder registerCustomBuilderForPayload(CustomBuilder customBuilder) {
        CustomBuilder old = null;
        this.customBuilderForPayload = customBuilder;
        return old;
    }

    protected CustomBuilder getCustomBuilder(String namespace, String localPart) {
        if (this.customBuilders == null) {
            return null;
        }
        QName qName = new QName(namespace, localPart);
        return (CustomBuilder)this.customBuilders.get(qName);
    }

    public short getBuilderType() {
        return 1;
    }

    public void registerExternalContentHandler(Object obj) {
        throw new UnsupportedOperationException();
    }

    public Object getRegisteredContentHandler() {
        throw new UnsupportedOperationException();
    }

    protected abstract OMDocument createDocument();

    protected void createDocumentIfNecessary() {
        if (this.document == null && this.parser.getEventType() == 7) {
            this.document = this.createDocument();
            if (this.charEncoding != null) {
                this.document.setCharsetEncoding(this.charEncoding);
            }
            this.document.setXMLVersion(this.parser.getVersion());
            this.document.setXMLEncoding(this.parser.getCharacterEncodingScheme());
            this.document.setStandalone(this.parser.isStandalone() ? "yes" : "no");
            this.target = (OMContainerEx)((Object)this.document);
        }
    }

    public OMDocument getDocument() {
        this.createDocumentIfNecessary();
        if (this.document == null) {
            throw new UnsupportedOperationException("There is no document linked to this builder");
        }
        return this.document;
    }

    public String getCharsetEncoding() {
        return this.document.getCharsetEncoding();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        try {
            if (!this.isClosed()) {
                this.parser.close();
            }
        }
        catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Exception occurred during parser close.  Processing continues. " + e));
            }
        }
        finally {
            this._isClosed = true;
            this.done = true;
            if (this._releaseParserOnClose) {
                this.parser = null;
            }
        }
    }

    public Object getReaderProperty(String name) throws IllegalArgumentException {
        if (!this.isClosed()) {
            return this.parser.getProperty(name);
        }
        return null;
    }

    public String getCharacterEncoding() {
        if (this.charEncoding == null) {
            return "UTF-8";
        }
        return this.charEncoding;
    }

    public boolean isClosed() {
        return this._isClosed;
    }

    public void releaseParserOnClose(boolean value) {
        if (this.isClosed() && value) {
            this.parser = null;
        }
        this._releaseParserOnClose = value;
    }
}

