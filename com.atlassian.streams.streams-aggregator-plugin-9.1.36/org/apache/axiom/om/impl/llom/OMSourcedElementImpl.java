/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.llom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.QNameAwareOMDataSource;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMNamespaceImpl;
import org.apache.axiom.om.impl.llom.PushOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OMSourcedElementImpl
extends OMElementImpl
implements OMSourcedElement {
    private OMDataSource dataSource;
    private OMNamespace definedNamespace = null;
    private boolean definedNamespaceSet;
    private boolean isExpanded;
    private static final Log log = LogFactory.getLog(OMSourcedElementImpl.class);
    private static final Log forceExpandLog = LogFactory.getLog((String)(OMSourcedElementImpl.class.getName() + ".forceExpand"));
    private XMLStreamReader readerFromDS = null;

    private static OMNamespace getOMNamespace(QName qName) {
        return qName.getNamespaceURI().length() == 0 ? null : new OMNamespaceImpl(qName.getNamespaceURI(), qName.getPrefix());
    }

    public OMSourcedElementImpl(OMFactory factory, OMDataSource source) {
        super(factory);
        this.dataSource = source;
        this.isExpanded = false;
    }

    public OMSourcedElementImpl(String localName, OMNamespace ns, OMFactory factory, OMDataSource source) {
        super(null, localName, null, null, factory, false);
        if (source == null) {
            throw new IllegalArgumentException("OMDataSource can't be null");
        }
        this.dataSource = source;
        this.isExpanded = false;
        if (ns != null && ns.getNamespaceURI().length() == 0) {
            ns = null;
        }
        if (ns == null || !this.isLossyPrefix(this.dataSource) && ns.getPrefix() != null) {
            this.definedNamespace = ns;
        } else {
            String uri = ns.getNamespaceURI();
            this.definedNamespace = new DeferredNamespace(uri);
        }
        this.definedNamespaceSet = true;
    }

    public OMSourcedElementImpl(QName qName, OMFactory factory, OMDataSource source) {
        super(null, qName.getLocalPart(), null, null, factory, false);
        String uri;
        if (source == null) {
            throw new IllegalArgumentException("OMDataSource can't be null");
        }
        this.dataSource = source;
        this.isExpanded = false;
        this.definedNamespace = !this.isLossyPrefix(this.dataSource) ? OMSourcedElementImpl.getOMNamespace(qName) : ((uri = qName.getNamespaceURI()).length() == 0 ? null : new DeferredNamespace(uri));
        this.definedNamespaceSet = true;
    }

    public OMSourcedElementImpl(String localName, OMNamespace ns, OMContainer parent, OMFactory factory) {
        super(parent, localName, null, null, factory, false);
        this.dataSource = null;
        this.definedNamespace = ns;
        this.isExpanded = true;
        if (ns != null) {
            this.setNamespace(ns);
        }
    }

    public OMSourcedElementImpl(OMContainer parent, String localName, OMNamespace ns, OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(parent, localName, ns, builder, factory, generateNSDecl);
        this.definedNamespace = ns;
        this.isExpanded = true;
    }

    private boolean isLossyPrefix(OMDataSource source) {
        Object lossyPrefix = null;
        if (source instanceof OMDataSourceExt) {
            lossyPrefix = ((OMDataSourceExt)source).getProperty("lossyPrefix");
        }
        return lossyPrefix == Boolean.TRUE;
    }

    private String getPrintableName() {
        if (this.isExpanded || this.definedNamespaceSet && this.localName != null) {
            String uri = null;
            if (this.getNamespace() != null) {
                uri = this.getNamespace().getNamespaceURI();
            }
            if (uri == null || uri.length() == 0) {
                return this.getLocalName();
            }
            return "{" + uri + '}' + this.getLocalName();
        }
        return "<unknown>";
    }

    private void forceExpand() {
        if (!this.isExpanded && this.dataSource != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("forceExpand: expanding element " + this.getPrintableName()));
                if (forceExpandLog.isDebugEnabled()) {
                    Exception e = new Exception("Debug Stack Trace");
                    forceExpandLog.debug((Object)"forceExpand stack", (Throwable)e);
                }
            }
            if (this.isPushDataSource()) {
                this.isExpanded = true;
                try {
                    this.dataSource.serialize(new PushOMBuilder(this));
                }
                catch (XMLStreamException ex) {
                    throw new OMException("Failed to expand data source", ex);
                }
            }
            try {
                this.readerFromDS = this.dataSource.getReader();
            }
            catch (XMLStreamException ex) {
                throw new OMException("Error obtaining parser from data source for element " + this.getPrintableName(), ex);
            }
            String characterEncoding = this.readerFromDS.getCharacterEncodingScheme();
            if (characterEncoding != null) {
                characterEncoding = this.readerFromDS.getEncoding();
            }
            try {
                if (this.readerFromDS.getEventType() != 1) {
                    while (this.readerFromDS.next() != 1) {
                    }
                }
            }
            catch (XMLStreamException ex) {
                throw new OMException("Error parsing data source document for element " + this.getLocalName(), ex);
            }
            this.validateName(this.readerFromDS.getPrefix(), this.readerFromDS.getLocalName(), this.readerFromDS.getNamespaceURI());
            this.isExpanded = true;
            super.setBuilder(new StAXOMBuilder(this.getOMFactory(), this.readerFromDS, this, characterEncoding));
            this.setComplete(false);
        }
    }

    private boolean isPushDataSource() {
        return this.dataSource instanceof AbstractPushOMDataSource;
    }

    void validateName(String staxPrefix, String staxLocalName, String staxNamespaceURI) {
        if (this.localName == null) {
            this.localName = staxLocalName;
        } else if (!staxLocalName.equals(this.localName)) {
            throw new OMException("Element name from data source is " + staxLocalName + ", not the expected " + this.localName);
        }
        if (this.definedNamespaceSet) {
            String namespaceURI;
            if (staxNamespaceURI == null) {
                staxNamespaceURI = "";
            }
            String string = namespaceURI = this.definedNamespace == null ? "" : this.definedNamespace.getNamespaceURI();
            if (!staxNamespaceURI.equals(namespaceURI)) {
                throw new OMException("Element namespace from data source is " + staxNamespaceURI + ", not the expected " + namespaceURI);
            }
            if (!(this.definedNamespace instanceof DeferredNamespace)) {
                String prefix;
                if (staxPrefix == null) {
                    staxPrefix = "";
                }
                String string2 = prefix = this.definedNamespace == null ? "" : this.definedNamespace.getPrefix();
                if (!staxPrefix.equals(prefix)) {
                    throw new OMException("Element prefix from data source is '" + staxPrefix + "', not the expected '" + prefix + "'");
                }
            }
        }
    }

    public boolean isExpanded() {
        return this.isExpanded;
    }

    public Iterator getChildElements() {
        this.forceExpand();
        return super.getChildElements();
    }

    public OMNamespace declareNamespace(String uri, String prefix) {
        this.forceExpand();
        return super.declareNamespace(uri, prefix);
    }

    public OMNamespace declareDefaultNamespace(String uri) {
        this.forceExpand();
        return super.declareDefaultNamespace(uri);
    }

    public OMNamespace getDefaultNamespace() {
        this.forceExpand();
        return super.getDefaultNamespace();
    }

    public OMNamespace declareNamespace(OMNamespace namespace) {
        this.forceExpand();
        return super.declareNamespace(namespace);
    }

    public OMNamespace addNamespaceDeclaration(String uri, String prefix) {
        return super.addNamespaceDeclaration(uri, prefix);
    }

    void addNamespaceDeclaration(OMNamespace ns) {
        super.addNamespaceDeclaration(ns);
    }

    public void undeclarePrefix(String prefix) {
        this.forceExpand();
        super.undeclarePrefix(prefix);
    }

    public OMNamespace findNamespace(String uri, String prefix) {
        this.forceExpand();
        return super.findNamespace(uri, prefix);
    }

    public OMNamespace findNamespaceURI(String prefix) {
        this.forceExpand();
        return super.findNamespaceURI(prefix);
    }

    public Iterator getAllDeclaredNamespaces() throws OMException {
        this.forceExpand();
        return super.getAllDeclaredNamespaces();
    }

    public Iterator getNamespacesInScope() throws OMException {
        this.forceExpand();
        return super.getNamespacesInScope();
    }

    public NamespaceContext getNamespaceContext(boolean detached) {
        this.forceExpand();
        return super.getNamespaceContext(detached);
    }

    public Iterator getAllAttributes() {
        this.forceExpand();
        return super.getAllAttributes();
    }

    public OMAttribute getAttribute(QName qname) {
        this.forceExpand();
        return super.getAttribute(qname);
    }

    public String getAttributeValue(QName qname) {
        this.forceExpand();
        return super.getAttributeValue(qname);
    }

    public OMAttribute addAttribute(OMAttribute attr) {
        this.forceExpand();
        return super.addAttribute(attr);
    }

    public OMAttribute addAttribute(String attributeName, String value, OMNamespace namespace) {
        this.forceExpand();
        return super.addAttribute(attributeName, value, namespace);
    }

    void appendAttribute(OMAttribute attr) {
        super.appendAttribute(attr);
    }

    public void removeAttribute(OMAttribute attr) {
        this.forceExpand();
        super.removeAttribute(attr);
    }

    public void setBuilder(OMXMLParserWrapper wrapper) {
        throw new UnsupportedOperationException("Builder cannot be set for element backed by data source");
    }

    public OMXMLParserWrapper getBuilder() {
        this.forceExpand();
        return super.getBuilder();
    }

    public void setFirstChild(OMNode node) {
        this.forceExpand();
        super.setFirstChild(node);
    }

    public void setLastChild(OMNode omNode) {
        this.forceExpand();
        super.setLastChild(omNode);
    }

    public OMElement getFirstElement() {
        this.forceExpand();
        return super.getFirstElement();
    }

    public XMLStreamReader getXMLStreamReader(boolean cache) {
        return this.getXMLStreamReader(cache, new OMXMLStreamReaderConfiguration());
    }

    public XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("getting XMLStreamReader for " + this.getPrintableName() + " with cache=" + cache));
        }
        if (this.isExpanded) {
            return super.getXMLStreamReader(cache, configuration);
        }
        if (cache && this.isDestructiveRead() || this.isPushDataSource()) {
            this.forceExpand();
            return super.getXMLStreamReader(true, configuration);
        }
        try {
            return this.dataSource.getReader();
        }
        catch (XMLStreamException ex) {
            throw new OMException("Error obtaining parser from data source for element " + this.getPrintableName(), ex);
        }
    }

    public XMLStreamReader getXMLStreamReader() {
        return this.getXMLStreamReader(true);
    }

    public XMLStreamReader getXMLStreamReaderWithoutCaching() {
        return this.getXMLStreamReader(false);
    }

    public void setText(String text) {
        this.forceExpand();
        super.setText(text);
    }

    public void setText(QName text) {
        this.forceExpand();
        super.setText(text);
    }

    public String getText() {
        this.forceExpand();
        return super.getText();
    }

    public Reader getTextAsStream(boolean cache) {
        return super.getTextAsStream(cache);
    }

    public QName getTextAsQName() {
        this.forceExpand();
        return super.getTextAsQName();
    }

    public void writeTextTo(Writer out, boolean cache) throws IOException {
        super.writeTextTo(out, cache);
    }

    private void ensureLocalNameSet() {
        if (this.localName == null) {
            if (this.dataSource instanceof QNameAwareOMDataSource) {
                this.localName = ((QNameAwareOMDataSource)this.dataSource).getLocalName();
            }
            if (this.localName == null) {
                this.forceExpand();
            }
        }
    }

    public String getLocalName() {
        this.ensureLocalNameSet();
        return super.getLocalName();
    }

    public void setLocalName(String localName) {
        this.forceExpand();
        super.setLocalName(localName);
    }

    public OMNamespace getNamespace() throws OMException {
        String namespaceURI;
        if (this.isExpanded()) {
            return super.getNamespace();
        }
        if (this.definedNamespaceSet) {
            return this.definedNamespace;
        }
        if (this.dataSource instanceof QNameAwareOMDataSource && (namespaceURI = ((QNameAwareOMDataSource)this.dataSource).getNamespaceURI()) != null) {
            if (namespaceURI.length() == 0) {
                this.definedNamespaceSet = true;
            } else {
                String prefix = ((QNameAwareOMDataSource)this.dataSource).getPrefix();
                this.definedNamespace = prefix == null ? new DeferredNamespace(namespaceURI) : new OMNamespaceImpl(namespaceURI, prefix);
                this.definedNamespaceSet = true;
            }
        }
        if (this.definedNamespaceSet) {
            return this.definedNamespace;
        }
        this.forceExpand();
        return super.getNamespace();
    }

    public String getPrefix() {
        return super.getPrefix();
    }

    public String getNamespaceURI() {
        return super.getNamespaceURI();
    }

    public void setNamespace(OMNamespace namespace) {
        this.forceExpand();
        super.setNamespace(namespace);
    }

    public void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace) {
        this.forceExpand();
        super.setNamespaceWithNoFindInCurrentScope(namespace);
    }

    public QName getQName() {
        if (this.isExpanded()) {
            return super.getQName();
        }
        if (this.getNamespace() != null) {
            return new QName(this.getNamespace().getNamespaceURI(), this.getLocalName());
        }
        return new QName(this.getLocalName());
    }

    public String toStringWithConsume() throws XMLStreamException {
        if (this.isExpanded()) {
            return super.toStringWithConsume();
        }
        StringWriter writer = new StringWriter();
        XMLStreamWriter writer2 = StAXUtils.createXMLStreamWriter(writer);
        this.dataSource.serialize(writer2);
        writer2.flush();
        return writer.toString();
    }

    private boolean isDestructiveWrite() {
        if (this.dataSource instanceof OMDataSourceExt) {
            return ((OMDataSourceExt)this.dataSource).isDestructiveWrite();
        }
        return true;
    }

    private boolean isDestructiveRead() {
        if (this.dataSource instanceof OMDataSourceExt) {
            return ((OMDataSourceExt)this.dataSource).isDestructiveRead();
        }
        return false;
    }

    public QName resolveQName(String qname) {
        this.forceExpand();
        return super.resolveQName(qname);
    }

    public OMElement cloneOMElement() {
        return super.cloneOMElement();
    }

    public OMInformationItem clone(OMCloneOptions options) {
        return super.clone(options);
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        OMDataSource ds = this.getDataSource();
        if (!options.isCopyOMDataSources() || ds == null || this.isExpanded() || !(ds instanceof OMDataSourceExt)) {
            return super.clone(options, targetParent);
        }
        OMDataSourceExt sourceDS = (OMDataSourceExt)ds;
        if (sourceDS.isDestructiveRead() || sourceDS.isDestructiveWrite()) {
            return super.clone(options, targetParent);
        }
        OMDataSourceExt targetDS = ((OMDataSourceExt)ds).copy();
        if (targetDS == null) {
            return super.clone(options, targetParent);
        }
        OMSourcedElementImpl targetOMSE = options.isPreserveModel() ? (OMSourcedElementImpl)this.createClone(options, targetDS) : (OMSourcedElementImpl)this.factory.createOMElement(targetDS);
        targetOMSE.localName = this.localName;
        targetOMSE.definedNamespaceSet = this.definedNamespaceSet;
        if (this.definedNamespace instanceof DeferredNamespace) {
            OMSourcedElementImpl oMSourcedElementImpl = targetOMSE;
            oMSourcedElementImpl.getClass();
            targetOMSE.definedNamespace = oMSourcedElementImpl.new DeferredNamespace(this.definedNamespace.getNamespaceURI());
        } else {
            targetOMSE.definedNamespace = this.definedNamespace;
        }
        if (targetParent != null) {
            targetParent.addChild(targetOMSE);
        }
        return targetOMSE;
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return super.createClone(options, targetParent);
    }

    protected OMSourcedElement createClone(OMCloneOptions options, OMDataSource ds) {
        return this.factory.createOMElement(ds);
    }

    public void setLineNumber(int lineNumber) {
        super.setLineNumber(lineNumber);
    }

    public int getLineNumber() {
        return super.getLineNumber();
    }

    public void discard() throws OMException {
        this.setComplete(true);
        super.detach();
    }

    public int getType() {
        return super.getType();
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        if (this.isExpanded()) {
            super.internalSerialize(writer, cache);
        } else if (cache) {
            if (this.isDestructiveWrite()) {
                this.forceExpand();
                super.internalSerialize(writer, true);
            } else {
                this.dataSource.serialize(writer);
            }
        } else {
            this.dataSource.serialize(writer);
        }
    }

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        this.internalSerialize(xmlWriter, true);
    }

    public void serialize(OutputStream output) throws XMLStreamException {
        OMOutputFormat format = new OMOutputFormat();
        this.serialize(output, format);
    }

    public void serialize(Writer writer) throws XMLStreamException {
        OMOutputFormat format = new OMOutputFormat();
        this.serialize(writer, format);
    }

    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        if (this.isExpanded) {
            super.serialize(output, format);
        } else if (this.isDestructiveWrite()) {
            this.forceExpand();
            super.serialize(output, format);
        } else {
            this.dataSource.serialize(output, format);
        }
    }

    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        if (this.isExpanded) {
            super.serialize(writer, format);
        } else if (this.isDestructiveWrite()) {
            this.forceExpand();
            super.serialize(writer, format);
        } else {
            this.dataSource.serialize(writer, format);
        }
    }

    public void serializeAndConsume(XMLStreamWriter xmlWriter) throws XMLStreamException {
        this.internalSerialize(xmlWriter, false);
    }

    public void serializeAndConsume(OutputStream output) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("serialize " + this.getPrintableName() + " to output stream"));
        }
        OMOutputFormat format = new OMOutputFormat();
        if (this.isExpanded()) {
            super.serializeAndConsume(output, format);
        } else {
            this.dataSource.serialize(output, format);
        }
    }

    public void serializeAndConsume(Writer writer) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("serialize " + this.getPrintableName() + " to writer"));
        }
        if (this.isExpanded()) {
            super.serializeAndConsume(writer);
        } else {
            OMOutputFormat format = new OMOutputFormat();
            this.dataSource.serialize(writer, format);
        }
    }

    public void serializeAndConsume(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("serialize formatted " + this.getPrintableName() + " to output stream"));
        }
        if (this.isExpanded()) {
            super.serializeAndConsume(output, format);
        } else {
            this.dataSource.serialize(output, format);
        }
    }

    public void serializeAndConsume(Writer writer, OMOutputFormat format) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("serialize formatted " + this.getPrintableName() + " to writer"));
        }
        if (this.isExpanded()) {
            super.serializeAndConsume(writer, format);
        } else {
            this.dataSource.serialize(writer, format);
        }
    }

    public void addChild(OMNode omNode) {
        this.forceExpand();
        super.addChild(omNode);
    }

    public void addChild(OMNode omNode, boolean fromBuilder) {
        this.forceExpand();
        super.addChild(omNode, fromBuilder);
    }

    public Iterator getChildrenWithName(QName elementQName) {
        this.forceExpand();
        return super.getChildrenWithName(elementQName);
    }

    public Iterator getChildrenWithLocalName(String localName) {
        this.forceExpand();
        return super.getChildrenWithLocalName(localName);
    }

    public Iterator getChildrenWithNamespaceURI(String uri) {
        this.forceExpand();
        return super.getChildrenWithNamespaceURI(uri);
    }

    public OMElement getFirstChildWithName(QName elementQName) throws OMException {
        this.forceExpand();
        return super.getFirstChildWithName(elementQName);
    }

    public Iterator getChildren() {
        this.forceExpand();
        return super.getChildren();
    }

    public Iterator getDescendants(boolean includeSelf) {
        this.forceExpand();
        return super.getDescendants(includeSelf);
    }

    public OMNode getFirstOMChild() {
        this.forceExpand();
        return super.getFirstOMChild();
    }

    public OMNode getFirstOMChildIfAvailable() {
        return super.getFirstOMChildIfAvailable();
    }

    public OMNode getLastKnownOMChild() {
        return super.getLastKnownOMChild();
    }

    public OMNode detach() throws OMException {
        boolean complete = this.isComplete();
        this.setComplete(true);
        OMNode result = super.detach();
        this.setComplete(complete);
        return result;
    }

    public OMNode getNextOMSibling() throws OMException {
        return super.getNextOMSibling();
    }

    public OMNode getNextOMSiblingIfAvailable() {
        return super.getNextOMSiblingIfAvailable();
    }

    OMNamespace handleNamespace(QName qname) {
        this.forceExpand();
        return super.handleNamespace(qname);
    }

    public int getState() {
        if (this.isExpanded) {
            return super.getState();
        }
        return 1;
    }

    public boolean isComplete() {
        if (this.isExpanded) {
            return super.isComplete();
        }
        return true;
    }

    public String toString() {
        if (this.isExpanded) {
            return super.toString();
        }
        if (this.isDestructiveWrite()) {
            this.forceExpand();
            return super.toString();
        }
        try {
            StringWriter writer = new StringWriter();
            OMOutputFormat format = new OMOutputFormat();
            this.dataSource.serialize(writer, format);
            String text = writer.toString();
            writer.close();
            return text;
        }
        catch (XMLStreamException e) {
            throw new RuntimeException("Cannot serialize OM Element " + this.getLocalName(), e);
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot serialize OM Element " + this.getLocalName(), e);
        }
    }

    public void buildWithAttachments() {
        if (this.state == 0) {
            this.build();
        }
        if (this.isExpanded()) {
            Iterator iterator = this.getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode)iterator.next();
                node.buildWithAttachments();
            }
        }
    }

    public void build() throws OMException {
        super.build();
    }

    void notifyChildComplete() {
        super.notifyChildComplete();
    }

    OMNamespace handleNamespace(String namespaceURI, String prefix) {
        return super.handleNamespace(namespaceURI, prefix);
    }

    public OMDataSource getDataSource() {
        return this.dataSource;
    }

    public OMDataSource setDataSource(OMDataSource dataSource) {
        if (!this.isExpanded()) {
            OMDataSource oldDS = this.dataSource;
            this.dataSource = dataSource;
            return oldDS;
        }
        OMDataSource oldDS = this.dataSource;
        Iterator it = this.getChildren();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
        this.dataSource = dataSource;
        this.setComplete(false);
        this.isExpanded = false;
        super.setBuilder(null);
        if (this.isLossyPrefix(dataSource)) {
            this.definedNamespace = new DeferredNamespace(this.definedNamespace.getNamespaceURI());
        }
        return oldDS;
    }

    public void setComplete(boolean value) {
        int n = this.state = value ? 1 : 0;
        if (value) {
            if (this.readerFromDS != null) {
                try {
                    this.readerFromDS.close();
                }
                catch (XMLStreamException e) {
                    // empty catch block
                }
                this.readerFromDS = null;
            }
            if (this.dataSource != null) {
                if (this.dataSource instanceof OMDataSourceExt) {
                    ((OMDataSourceExt)this.dataSource).close();
                }
                this.dataSource = null;
            }
        }
        if (value && this.readerFromDS != null) {
            try {
                this.readerFromDS.close();
            }
            catch (XMLStreamException xMLStreamException) {
                // empty catch block
            }
            this.readerFromDS = null;
        }
    }

    public void discarded() {
        super.discarded();
    }

    public SAXSource getSAXSource(boolean cache) {
        return super.getSAXSource(cache);
    }

    public Object getObject(Class dataSourceClass) {
        if (this.dataSource == null || this.isExpanded || !dataSourceClass.isInstance(this.dataSource)) {
            return null;
        }
        return ((OMDataSourceExt)this.dataSource).getObject();
    }

    public void removeChildren() {
        this.forceExpand();
        super.removeChildren();
    }

    class DeferredNamespace
    implements OMNamespace {
        final String uri;

        DeferredNamespace(String ns) {
            this.uri = ns;
        }

        public boolean equals(String uri, String prefix) {
            String thisPrefix = this.getPrefix();
            return this.uri.equals(uri) && (thisPrefix == null ? prefix == null : thisPrefix.equals(prefix));
        }

        public String getName() {
            return this.uri;
        }

        public String getNamespaceURI() {
            return this.uri;
        }

        public String getPrefix() {
            OMNamespace actualNS;
            if (!OMSourcedElementImpl.this.isExpanded()) {
                OMSourcedElementImpl.this.forceExpand();
            }
            return (actualNS = OMSourcedElementImpl.this.getNamespace()) == null ? "" : actualNS.getPrefix();
        }

        public int hashCode() {
            String thisPrefix = this.getPrefix();
            return this.uri.hashCode() ^ (thisPrefix != null ? thisPrefix.hashCode() : 0);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof OMNamespace)) {
                return false;
            }
            OMNamespace other = (OMNamespace)obj;
            String otherPrefix = other.getPrefix();
            String thisPrefix = this.getPrefix();
            return this.uri.equals(other.getNamespaceURI()) && (thisPrefix == null ? otherPrefix == null : thisPrefix.equals(otherPrefix));
        }
    }
}

