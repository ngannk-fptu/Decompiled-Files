/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.llom;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.jaxp.OMSource;
import org.apache.axiom.om.impl.llom.IContainer;
import org.apache.axiom.om.impl.llom.IElement;
import org.apache.axiom.om.impl.llom.NamespaceIterator;
import org.apache.axiom.om.impl.llom.OMAttributeImpl;
import org.apache.axiom.om.impl.llom.OMChildElementIterator;
import org.apache.axiom.om.impl.llom.OMChildrenLegacyQNameIterator;
import org.apache.axiom.om.impl.llom.OMChildrenLocalNameIterator;
import org.apache.axiom.om.impl.llom.OMChildrenNamespaceIterator;
import org.apache.axiom.om.impl.llom.OMChildrenQNameIterator;
import org.apache.axiom.om.impl.llom.OMContainerHelper;
import org.apache.axiom.om.impl.llom.OMDescendantsIterator;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;
import org.apache.axiom.om.impl.llom.OMElementImplUtil;
import org.apache.axiom.om.impl.llom.OMNamespaceImpl;
import org.apache.axiom.om.impl.llom.OMNodeImpl;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;
import org.apache.axiom.om.impl.util.EmptyIterator;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OMElementImpl
extends OMNodeImpl
implements IElement,
OMConstants {
    private static final Log log = LogFactory.getLog(OMElementImpl.class);
    protected OMXMLParserWrapper builder;
    protected int state;
    protected OMNamespace ns;
    protected String localName;
    protected QName qName;
    protected OMNode firstChild;
    protected HashMap namespaces = null;
    protected HashMap attributes = null;
    protected OMNode lastChild;
    private int lineNumber;
    private static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();
    static final OMNamespaceImpl xmlns = new OMNamespaceImpl("http://www.w3.org/XML/1998/namespace", "xml");

    public OMElementImpl(OMContainer parent, String localName, OMNamespace ns, OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(factory);
        if (localName == null || localName.trim().length() == 0) {
            throw new OMException("localname can not be null or empty");
        }
        this.localName = localName;
        this.builder = builder;
        int n = this.state = builder == null ? 1 : 0;
        if (parent != null) {
            ((IContainer)parent).addChild(this, builder != null);
        }
        this.ns = generateNSDecl ? this.handleNamespace(ns) : ns;
    }

    public OMElementImpl(QName qname, OMContainer parent, OMFactory factory) throws OMException {
        super(factory);
        this.state = 1;
        if (parent != null) {
            parent.addChild(this);
        }
        this.localName = qname.getLocalPart();
        this.ns = this.handleNamespace(qname);
    }

    OMElementImpl(OMFactory factory) {
        super(factory);
        this.state = 1;
    }

    OMNamespace handleNamespace(QName qname) {
        OMNamespace ns = null;
        String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI.length() > 0) {
            String prefix = qname.getPrefix();
            ns = this.findNamespace(namespaceURI, prefix);
            if (ns == null) {
                if ("".equals(prefix)) {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
                ns = this.declareNamespace(namespaceURI, prefix);
            }
        } else if (qname.getPrefix().length() > 0) {
            throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
        }
        return ns;
    }

    private OMNamespace handleNamespace(OMNamespace ns) {
        String prefix;
        String namespaceURI = ns == null ? "" : ns.getNamespaceURI();
        String string = prefix = ns == null ? "" : ns.getPrefix();
        if (namespaceURI.length() == 0 && prefix != null && prefix.length() > 0) {
            throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
        }
        if (namespaceURI.length() == 0) {
            if (this.getDefaultNamespace() != null) {
                this.declareDefaultNamespace("");
            }
            return null;
        }
        OMNamespace namespace = this.findNamespace(namespaceURI, prefix);
        if (namespace == null || prefix != null && !namespace.getPrefix().equals(prefix)) {
            namespace = this.declareNamespace(ns);
        }
        return namespace;
    }

    OMNamespace handleNamespace(String namespaceURI, String prefix) {
        if (prefix.length() == 0 && namespaceURI.length() == 0) {
            OMNamespace namespace = this.getDefaultNamespace();
            if (namespace != null) {
                this.declareDefaultNamespace("");
            }
            return null;
        }
        OMNamespace namespace = this.findNamespace(namespaceURI, prefix);
        if (namespace == null) {
            namespace = this.declareNamespace(namespaceURI, prefix.length() > 0 ? prefix : null);
        }
        return namespace;
    }

    public void addChild(OMNode omNode) {
        this.addChild(omNode, false);
    }

    public void addChild(OMNode omNode, boolean fromBuilder) {
        OMContainerHelper.addChild(this, omNode, fromBuilder);
    }

    public Iterator getChildrenWithName(QName elementQName) {
        OMNode firstChild = this.getFirstOMChild();
        OMChildrenQNameIterator it = new OMChildrenQNameIterator(firstChild, elementQName);
        if (elementQName.getNamespaceURI().length() == 0 && firstChild != null && !it.hasNext()) {
            if (log.isTraceEnabled()) {
                log.trace((Object)("There are no child elements that match the unqualifed name: " + elementQName));
                log.trace((Object)"Now looking for child elements that have the same local name.");
            }
            it = new OMChildrenLegacyQNameIterator(this.getFirstOMChild(), elementQName);
        }
        return it;
    }

    public Iterator getChildrenWithLocalName(String localName) {
        return new OMChildrenLocalNameIterator(this.getFirstOMChild(), localName);
    }

    public Iterator getChildrenWithNamespaceURI(String uri) {
        return new OMChildrenNamespaceIterator(this.getFirstOMChild(), uri);
    }

    public OMElement getFirstChildWithName(QName elementQName) throws OMException {
        OMChildrenQNameIterator omChildrenQNameIterator = new OMChildrenQNameIterator(this.getFirstOMChild(), elementQName);
        OMNode omNode = null;
        if (omChildrenQNameIterator.hasNext()) {
            omNode = (OMNode)omChildrenQNameIterator.next();
        }
        return omNode != null && 1 == omNode.getType() ? (OMElement)omNode : null;
    }

    public Iterator getChildren() {
        return new OMChildrenIterator(this.getFirstOMChild());
    }

    public Iterator getDescendants(boolean includeSelf) {
        return new OMDescendantsIterator(this, includeSelf);
    }

    public Iterator getChildElements() {
        return new OMChildElementIterator(this.getFirstElement());
    }

    public OMNamespace declareNamespace(String uri, String prefix) {
        if ("".equals(prefix)) {
            log.warn((Object)"Deprecated usage of OMElement#declareNamespace(String,String) with empty prefix");
            prefix = OMSerializerUtil.getNextNSPrefix();
        }
        OMNamespaceImpl ns = new OMNamespaceImpl(uri, prefix);
        return this.declareNamespace(ns);
    }

    public OMNamespace declareDefaultNamespace(String uri) {
        if (this.ns == null && uri.length() > 0 || this.ns != null && this.ns.getPrefix().length() == 0 && !this.ns.getNamespaceURI().equals(uri)) {
            throw new OMException("Attempt to add a namespace declaration that conflicts with the namespace information of the element");
        }
        OMNamespaceImpl namespace = new OMNamespaceImpl(uri == null ? "" : uri, "");
        if (this.namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        this.namespaces.put("", namespace);
        return namespace;
    }

    public OMNamespace getDefaultNamespace() {
        OMNamespace defaultNS;
        if (this.namespaces != null && (defaultNS = (OMNamespace)this.namespaces.get("")) != null) {
            return defaultNS.getNamespaceURI().length() == 0 ? null : defaultNS;
        }
        if (this.parent instanceof OMElementImpl) {
            return ((OMElementImpl)this.parent).getDefaultNamespace();
        }
        return null;
    }

    public OMNamespace addNamespaceDeclaration(String uri, String prefix) {
        OMNamespaceImpl ns = new OMNamespaceImpl(uri, prefix);
        this.addNamespaceDeclaration(ns);
        return ns;
    }

    void addNamespaceDeclaration(OMNamespace ns) {
        if (this.namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        this.namespaces.put(ns.getPrefix(), ns);
    }

    public OMNamespace declareNamespace(OMNamespace namespace) {
        String prefix;
        if (this.namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        if ((prefix = namespace.getPrefix()) == null) {
            prefix = OMSerializerUtil.getNextNSPrefix();
            namespace = new OMNamespaceImpl(namespace.getNamespaceURI(), prefix);
        }
        if (prefix.length() > 0 && namespace.getNamespaceURI().length() == 0) {
            throw new IllegalArgumentException("Cannot bind a prefix to the empty namespace name");
        }
        this.namespaces.put(prefix, namespace);
        return namespace;
    }

    public void undeclarePrefix(String prefix) {
        if (this.namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        this.namespaces.put(prefix, new OMNamespaceImpl("", prefix));
    }

    public OMNamespace findNamespace(String uri, String prefix) {
        OMNamespace namespace = this.findDeclaredNamespace(uri, prefix);
        if (namespace != null) {
            return namespace;
        }
        if (this.parent != null && this.parent instanceof OMElement) {
            namespace = ((OMElementImpl)this.parent).findNamespace(uri, prefix);
            if (prefix == null && namespace != null && this.findDeclaredNamespace(null, namespace.getPrefix()) != null) {
                namespace = null;
            }
        }
        return namespace;
    }

    public OMNamespace findNamespaceURI(String prefix) {
        OMNamespace ns;
        OMNamespace oMNamespace = ns = this.namespaces == null ? null : (OMNamespace)this.namespaces.get(prefix);
        if (ns == null) {
            if (this.parent instanceof OMElement) {
                return ((OMElement)((Object)this.parent)).findNamespaceURI(prefix);
            }
            return null;
        }
        if (prefix != null && prefix.length() > 0 && ns.getNamespaceURI().length() == 0) {
            return null;
        }
        return ns;
    }

    private OMNamespace findDeclaredNamespace(String uri, String prefix) {
        if (uri == null) {
            return this.namespaces == null ? null : (OMNamespace)this.namespaces.get(prefix);
        }
        if (prefix != null && prefix.equals("xml") && uri.equals("http://www.w3.org/XML/1998/namespace")) {
            return xmlns;
        }
        if (this.namespaces == null) {
            return null;
        }
        if (prefix == null || "".equals(prefix)) {
            OMNamespace defaultNamespace = this.getDefaultNamespace();
            if (defaultNamespace != null && uri.equals(defaultNamespace.getNamespaceURI())) {
                return defaultNamespace;
            }
            for (OMNamespace omNamespace : this.namespaces.values()) {
                String nsUri = omNamespace.getNamespaceURI();
                if (nsUri == null || !nsUri.equals(uri)) continue;
                return omNamespace;
            }
        } else {
            OMNamespace namespace = (OMNamespace)this.namespaces.get(prefix);
            if (namespace != null && uri.equals(namespace.getNamespaceURI())) {
                return namespace;
            }
        }
        return null;
    }

    public Iterator getAllDeclaredNamespaces() {
        if (this.namespaces == null) {
            return EMPTY_ITERATOR;
        }
        return this.namespaces.values().iterator();
    }

    public Iterator getNamespacesInScope() {
        return new NamespaceIterator(this);
    }

    public NamespaceContext getNamespaceContext(boolean detached) {
        return OMElementImplUtil.getNamespaceContext(this, detached);
    }

    public Iterator getAllAttributes() {
        if (this.attributes == null) {
            return EMPTY_ITERATOR;
        }
        return this.attributes.values().iterator();
    }

    public OMAttribute getAttribute(QName qname) {
        return this.attributes == null ? null : (OMAttribute)this.attributes.get(qname);
    }

    public String getAttributeValue(QName qname) {
        OMAttribute attr = this.getAttribute(qname);
        return attr == null ? null : attr.getAttributeValue();
    }

    public OMAttribute addAttribute(OMAttribute attr) {
        String prefix;
        OMNamespace ns2;
        String uri;
        OMNamespace namespace;
        OMElement owner = attr.getOwner();
        if (owner != null) {
            if (owner == this) {
                return attr;
            }
            attr = new OMAttributeImpl(attr.getLocalName(), attr.getNamespace(), attr.getAttributeValue(), attr.getOMFactory());
        }
        if (!((namespace = attr.getNamespace()) == null || (uri = namespace.getNamespaceURI()).length() <= 0 || (ns2 = this.findNamespaceURI(prefix = namespace.getPrefix())) != null && uri.equals(ns2.getNamespaceURI()))) {
            this.declareNamespace(uri, prefix);
        }
        this.appendAttribute(attr);
        return attr;
    }

    void appendAttribute(OMAttribute attr) {
        if (this.attributes == null) {
            this.attributes = new LinkedHashMap(5);
        }
        ((OMAttributeImpl)attr).owner = this;
        OMAttributeImpl oldAttr = (OMAttributeImpl)this.attributes.put(attr.getQName(), attr);
        if (oldAttr != null) {
            oldAttr.owner = null;
        }
    }

    public void removeAttribute(OMAttribute attr) {
        if (attr.getOwner() != this) {
            throw new OMException("The attribute is not owned by this element");
        }
        ((OMAttributeImpl)attr).owner = null;
        this.attributes.remove(attr.getQName());
    }

    public OMAttribute addAttribute(String attributeName, String value, OMNamespace ns) {
        String prefix;
        String namespaceURI;
        OMNamespace namespace = null;
        if (ns != null && (namespace = this.findNamespace(namespaceURI = ns.getNamespaceURI(), prefix = ns.getPrefix())) == null) {
            namespace = new OMNamespaceImpl(namespaceURI, prefix);
        }
        return this.addAttribute(new OMAttributeImpl(attributeName, namespace, value, this.factory));
    }

    public void setBuilder(OMXMLParserWrapper wrapper) {
        this.builder = wrapper;
    }

    public OMXMLParserWrapper getBuilder() {
        return this.builder;
    }

    public OMNode getFirstOMChild() {
        return OMContainerHelper.getFirstOMChild(this);
    }

    public OMNode getFirstOMChildIfAvailable() {
        return this.firstChild;
    }

    public OMNode getLastKnownOMChild() {
        return this.lastChild;
    }

    public void setFirstChild(OMNode firstChild) {
        if (firstChild != null) {
            ((OMNodeEx)firstChild).setParent(this);
        }
        this.firstChild = firstChild;
    }

    public void setLastChild(OMNode omNode) {
        this.lastChild = omNode;
    }

    public OMNode detach() throws OMException {
        if (this.state == 0) {
            this.build();
        }
        super.detach();
        return this;
    }

    public int getType() {
        return 1;
    }

    public void build() throws OMException {
        if (this.builder == null && this.state == 0) {
            Iterator childrenIterator = this.getChildren();
            while (childrenIterator.hasNext()) {
                OMNode omNode = (OMNode)childrenIterator.next();
                omNode.build();
            }
        } else {
            OMContainerHelper.build(this);
        }
    }

    public int getState() {
        return this.state;
    }

    public boolean isComplete() {
        return this.state == 1;
    }

    public void setComplete(boolean complete) {
        int n = this.state = complete ? 1 : 0;
        if (this.parent != null) {
            if (!complete) {
                this.parent.setComplete(false);
            } else if (this.parent instanceof OMElementImpl) {
                ((OMElementImpl)this.parent).notifyChildComplete();
            } else if (this.parent instanceof OMDocumentImpl) {
                ((OMDocumentImpl)this.parent).notifyChildComplete();
            }
        }
    }

    public void discarded() {
        this.state = 2;
    }

    public XMLStreamReader getXMLStreamReader() {
        return this.getXMLStreamReader(true);
    }

    public XMLStreamReader getXMLStreamReaderWithoutCaching() {
        return this.getXMLStreamReader(false);
    }

    public XMLStreamReader getXMLStreamReader(boolean cache) {
        return OMContainerHelper.getXMLStreamReader(this, cache);
    }

    public XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        return OMContainerHelper.getXMLStreamReader(this, cache, configuration);
    }

    public void setText(String text) {
        OMNode child;
        while ((child = this.getFirstOMChild()) != null) {
            child.detach();
        }
        if (text != null && text.length() > 0) {
            this.getOMFactory().createOMText((OMContainer)this, text);
        }
    }

    public void setText(QName qname) {
        OMNode child;
        while ((child = this.getFirstOMChild()) != null) {
            child.detach();
        }
        if (qname != null) {
            this.getOMFactory().createOMText((OMContainer)this, qname);
        }
    }

    public String getText() {
        return OMElementImplUtil.getText(this);
    }

    public Reader getTextAsStream(boolean cache) {
        return OMElementImplUtil.getTextAsStream(this, cache);
    }

    public QName getTextAsQName() {
        String childText = this.getText().trim();
        return childText.length() == 0 ? null : this.resolveQName(childText);
    }

    public void writeTextTo(Writer out, boolean cache) throws IOException {
        OMElementImplUtil.writeTextTo(this, out, cache);
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        if (cache || this.state == 1 || this.builder == null) {
            OMSerializerUtil.serializeStartpart(this, writer);
            OMSerializerUtil.serializeChildren(this, writer, cache);
            OMSerializerUtil.serializeEndpart(writer);
        } else {
            OMSerializerUtil.serializeByPullStream(this, writer, cache);
        }
    }

    public OMElement getFirstElement() {
        for (OMNode node = this.getFirstOMChild(); node != null; node = node.getNextOMSibling()) {
            if (node.getType() != 1) continue;
            return (OMElement)node;
        }
        return null;
    }

    public String getLocalName() {
        return this.localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
        this.qName = null;
    }

    public OMNamespace getNamespace() {
        return this.ns;
    }

    public String getPrefix() {
        OMNamespace ns = this.getNamespace();
        if (ns == null) {
            return null;
        }
        String prefix = ns.getPrefix();
        return prefix.length() == 0 ? null : prefix;
    }

    public String getNamespaceURI() {
        OMNamespace ns = this.getNamespace();
        if (ns == null) {
            return null;
        }
        String namespaceURI = ns.getNamespaceURI();
        return namespaceURI.length() == 0 ? null : namespaceURI;
    }

    public void setNamespace(OMNamespace namespace) {
        this.ns = this.handleNamespace(namespace);
        this.qName = null;
    }

    public void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace) {
        this.ns = namespace;
        this.qName = null;
    }

    public QName getQName() {
        if (this.qName != null) {
            return this.qName;
        }
        this.qName = this.ns != null ? new QName(this.ns.getNamespaceURI(), this.localName, this.ns.getPrefix()) : new QName(this.localName);
        return this.qName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toStringWithConsume() throws XMLStreamException {
        StringWriter writer = new StringWriter();
        XMLStreamWriter writer2 = StAXUtils.createXMLStreamWriter(writer);
        try {
            this.serializeAndConsume(writer2);
            writer2.flush();
        }
        finally {
            writer2.close();
        }
        return writer.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            XMLStreamWriter writer2 = StAXUtils.createXMLStreamWriter(writer);
            try {
                this.serialize(writer2);
                writer2.flush();
            }
            finally {
                writer2.close();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException("Can not serialize OM Element " + this.getLocalName(), e);
        }
        return writer.toString();
    }

    public void discard() throws OMException {
        OMElementImplUtil.discard(this);
    }

    public QName resolveQName(String qname) {
        int idx = qname.indexOf(58);
        if (idx == -1) {
            OMNamespace ns = this.getDefaultNamespace();
            return ns == null ? new QName(qname) : new QName(ns.getNamespaceURI(), qname, "");
        }
        String prefix = qname.substring(0, idx);
        OMNamespace ns = this.findNamespace(null, prefix);
        return ns == null ? null : new QName(ns.getNamespaceURI(), qname.substring(idx + 1), prefix);
    }

    public OMElement cloneOMElement() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"cloneOMElement start");
            log.debug((Object)("  element string =" + this.getLocalName()));
            log.debug((Object)(" isComplete = " + this.isComplete()));
            log.debug((Object)("  builder = " + this.builder));
        }
        return (OMElement)this.clone(new OMCloneOptions());
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        OMElement targetElement = options.isPreserveModel() ? this.createClone(options, targetParent) : this.factory.createOMElement(this.getLocalName(), this.getNamespace(), targetParent);
        Iterator it = this.getAllDeclaredNamespaces();
        while (it.hasNext()) {
            OMNamespace ns = (OMNamespace)it.next();
            targetElement.declareNamespace(ns);
        }
        it = this.getAllAttributes();
        while (it.hasNext()) {
            OMAttribute attr = (OMAttribute)it.next();
            targetElement.addAttribute(attr);
        }
        it = this.getChildren();
        while (it.hasNext()) {
            ((OMNodeImpl)it.next()).clone(options, targetElement);
        }
        return targetElement;
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return this.factory.createOMElement(this.getLocalName(), this.getNamespace(), targetParent);
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void buildWithAttachments() {
        if (this.state == 0) {
            this.build();
        }
        Iterator iterator = this.getChildren();
        while (iterator.hasNext()) {
            OMNode node = (OMNode)iterator.next();
            node.buildWithAttachments();
        }
    }

    void notifyChildComplete() {
        if (this.state == 0 && this.builder == null) {
            Iterator iterator = this.getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode)iterator.next();
                if (node.isComplete()) continue;
                return;
            }
            this.setComplete(true);
        }
    }

    public SAXSource getSAXSource(boolean cache) {
        return new OMSource(this);
    }

    public void removeChildren() {
        OMContainerHelper.removeChildren(this);
    }
}

