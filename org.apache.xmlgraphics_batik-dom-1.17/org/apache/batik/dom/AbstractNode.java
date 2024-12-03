/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.dom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.batik.dom.AbstractDOMImplementation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.ExtendedNode;
import org.apache.batik.dom.events.DOMMutationEvent;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.xbl.NodeXBL;
import org.apache.batik.dom.xbl.XBLManagerData;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;

public abstract class AbstractNode
implements ExtendedNode,
NodeXBL,
XBLManagerData,
Serializable {
    public static final NodeList EMPTY_NODE_LIST = new NodeList(){

        @Override
        public Node item(int i) {
            return null;
        }

        @Override
        public int getLength() {
            return 0;
        }
    };
    protected AbstractDocument ownerDocument;
    protected transient EventSupport eventSupport;
    protected HashMap userData;
    protected HashMap userDataHandlers;
    protected Object managerData;
    public static final short DOCUMENT_POSITION_DISCONNECTED = 1;
    public static final short DOCUMENT_POSITION_PRECEDING = 2;
    public static final short DOCUMENT_POSITION_FOLLOWING = 4;
    public static final short DOCUMENT_POSITION_CONTAINS = 8;
    public static final short DOCUMENT_POSITION_CONTAINED_BY = 16;
    public static final short DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 32;

    @Override
    public void setNodeName(String v) {
    }

    @Override
    public void setOwnerDocument(Document doc) {
        this.ownerDocument = (AbstractDocument)doc;
    }

    @Override
    public void setSpecified(boolean v) {
        throw this.createDOMException((short)11, "node.type", new Object[]{(int)this.getNodeType(), this.getNodeName()});
    }

    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
    }

    @Override
    public Node getParentNode() {
        return null;
    }

    @Override
    public void setParentNode(Node v) {
        throw this.createDOMException((short)3, "parent.not.allowed", new Object[]{(int)this.getNodeType(), this.getNodeName()});
    }

    @Override
    public NodeList getChildNodes() {
        return EMPTY_NODE_LIST;
    }

    @Override
    public Node getFirstChild() {
        return null;
    }

    @Override
    public Node getLastChild() {
        return null;
    }

    @Override
    public void setPreviousSibling(Node n) {
        throw this.createDOMException((short)3, "sibling.not.allowed", new Object[]{(int)this.getNodeType(), this.getNodeName()});
    }

    @Override
    public Node getPreviousSibling() {
        return null;
    }

    @Override
    public void setNextSibling(Node n) {
        throw this.createDOMException((short)3, "sibling.not.allowed", new Object[]{(int)this.getNodeType(), this.getNodeName()});
    }

    @Override
    public Node getNextSibling() {
        return null;
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public Document getOwnerDocument() {
        return this.ownerDocument;
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw this.createDOMException((short)3, "children.not.allowed", new Object[]{(int)this.getNodeType(), this.getNodeName()});
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw this.createDOMException((short)3, "children.not.allowed", new Object[]{(int)this.getNodeType(), this.getNodeName()});
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        throw this.createDOMException((short)3, "children.not.allowed", new Object[]{(int)this.getNodeType(), this.getNodeName()});
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        throw this.createDOMException((short)3, "children.not.allowed", new Object[]{(int)this.getNodeType(), this.getNodeName()});
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    public Node cloneNode(boolean deep) {
        Node n = deep ? this.deepCopyInto(this.newNode()) : this.copyInto(this.newNode());
        this.fireUserDataHandlers((short)1, this, n);
        return n;
    }

    @Override
    public void normalize() {
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return this.getCurrentDocument().getImplementation().hasFeature(feature, version);
    }

    @Override
    public String getPrefix() {
        return this.getNamespaceURI() == null ? null : DOMUtilities.getPrefix(this.getNodeName());
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        String uri = this.getNamespaceURI();
        if (uri == null) {
            throw this.createDOMException((short)14, "namespace", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        String name = this.getLocalName();
        if (prefix == null) {
            this.setNodeName(name);
            return;
        }
        if (!prefix.equals("") && !DOMUtilities.isValidName(prefix)) {
            throw this.createDOMException((short)5, "prefix", new Object[]{(int)this.getNodeType(), this.getNodeName(), prefix});
        }
        if (!DOMUtilities.isValidPrefix(prefix)) {
            throw this.createDOMException((short)14, "prefix", new Object[]{(int)this.getNodeType(), this.getNodeName(), prefix});
        }
        if (prefix.equals("xml") && !"http://www.w3.org/XML/1998/namespace".equals(uri) || prefix.equals("xmlns") && !"http://www.w3.org/2000/xmlns/".equals(uri)) {
            throw this.createDOMException((short)14, "namespace.uri", new Object[]{(int)this.getNodeType(), this.getNodeName(), uri});
        }
        this.setNodeName(prefix + ':' + name);
    }

    @Override
    public String getLocalName() {
        return this.getNamespaceURI() == null ? null : DOMUtilities.getLocalName(this.getNodeName());
    }

    public DOMException createDOMException(short type, String key, Object[] args) {
        try {
            return new DOMException(type, this.getCurrentDocument().formatMessage(key, args));
        }
        catch (Exception e) {
            return new DOMException(type, key);
        }
    }

    protected String getCascadedXMLBase(Node node) {
        String base = null;
        for (Node n = node.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() != 1) continue;
            base = this.getCascadedXMLBase(n);
            break;
        }
        if (base == null) {
            AbstractDocument doc = node.getNodeType() == 9 ? (AbstractDocument)node : (AbstractDocument)node.getOwnerDocument();
            base = doc.getDocumentURI();
        }
        while (node != null && node.getNodeType() != 1) {
            node = node.getParentNode();
        }
        if (node == null) {
            return base;
        }
        Element e = (Element)node;
        Attr attr = e.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "base");
        if (attr != null) {
            base = base == null ? attr.getNodeValue() : new ParsedURL(base, attr.getNodeValue()).toString();
        }
        return base;
    }

    @Override
    public String getBaseURI() {
        return this.getCascadedXMLBase(this);
    }

    public static String getBaseURI(Node n) {
        return n.getBaseURI();
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        Node n;
        if (this == other) {
            return 0;
        }
        ArrayList<AbstractNode> a1 = new ArrayList<AbstractNode>(10);
        ArrayList<Node> a2 = new ArrayList<Node>(10);
        int c1 = 0;
        int c2 = 0;
        if (this.getNodeType() == 2) {
            Attr otherAttr;
            a1.add(this);
            ++c1;
            n = ((Attr)((Object)this)).getOwnerElement();
            if (other.getNodeType() == 2 && n == (otherAttr = (Attr)other).getOwnerElement()) {
                if (this.hashCode() < other.hashCode()) {
                    return 34;
                }
                return 36;
            }
        } else {
            n = this;
        }
        while (n != null) {
            if (n == other) {
                return 20;
            }
            a1.add((AbstractNode)n);
            ++c1;
            n = n.getParentNode();
        }
        if (other.getNodeType() == 2) {
            a2.add(other);
            ++c2;
            n = ((Attr)other).getOwnerElement();
        } else {
            n = other;
        }
        while (n != null) {
            if (n == this) {
                return 10;
            }
            a2.add(n);
            ++c2;
            n = n.getParentNode();
        }
        int i1 = c1 - 1;
        int i2 = c2 - 1;
        if (a1.get(i1) != a2.get(i2)) {
            if (this.hashCode() < other.hashCode()) {
                return 35;
            }
            return 37;
        }
        Object n1 = a1.get(i1);
        Object n2 = a2.get(i2);
        while (n1 == n2) {
            n = (Node)n1;
            n1 = a1.get(--i1);
            n2 = a2.get(--i2);
        }
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n == n1) {
                return 2;
            }
            if (n != n2) continue;
            return 4;
        }
        return 1;
    }

    @Override
    public String getTextContent() {
        return null;
    }

    @Override
    public void setTextContent(String s) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        if (this.getNodeType() != 10) {
            while (this.getFirstChild() != null) {
                this.removeChild(this.getFirstChild());
            }
            this.appendChild(this.getOwnerDocument().createTextNode(s));
        }
    }

    @Override
    public boolean isSameNode(Node other) {
        return this == other;
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        if (namespaceURI == null || namespaceURI.length() == 0) {
            return null;
        }
        short type = this.getNodeType();
        switch (type) {
            case 1: {
                return this.lookupNamespacePrefix(namespaceURI, (Element)((Object)this));
            }
            case 9: {
                AbstractNode de = (AbstractNode)((Object)((Document)((Object)this)).getDocumentElement());
                return de.lookupPrefix(namespaceURI);
            }
            case 6: 
            case 10: 
            case 11: 
            case 12: {
                return null;
            }
            case 2: {
                AbstractNode ownerElement = (AbstractNode)((Object)((Attr)((Object)this)).getOwnerElement());
                if (ownerElement != null) {
                    return ownerElement.lookupPrefix(namespaceURI);
                }
                return null;
            }
        }
        for (Node n = this.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() != 1) continue;
            return n.lookupPrefix(namespaceURI);
        }
        return null;
    }

    protected String lookupNamespacePrefix(String namespaceURI, Element originalElement) {
        String pns;
        String ns = originalElement.getNamespaceURI();
        String prefix = originalElement.getPrefix();
        if (ns != null && ns.equals(namespaceURI) && prefix != null && (pns = originalElement.lookupNamespaceURI(prefix)) != null && pns.equals(namespaceURI)) {
            return prefix;
        }
        NamedNodeMap nnm = originalElement.getAttributes();
        if (nnm != null) {
            for (int i = 0; i < nnm.getLength(); ++i) {
                String ln;
                AbstractNode oe;
                String pns2;
                Node attr = nnm.item(i);
                if (!"xmlns".equals(attr.getPrefix()) || !attr.getNodeValue().equals(namespaceURI) || (pns2 = (oe = (AbstractNode)((Object)originalElement)).lookupNamespaceURI(ln = attr.getLocalName())) == null || !pns2.equals(namespaceURI)) continue;
                return ln;
            }
        }
        for (Node n = this.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() != 1) continue;
            return ((AbstractNode)n).lookupNamespacePrefix(namespaceURI, originalElement);
        }
        return null;
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        switch (this.getNodeType()) {
            case 9: {
                AbstractNode de = (AbstractNode)((Object)((Document)((Object)this)).getDocumentElement());
                return de.isDefaultNamespace(namespaceURI);
            }
            case 6: 
            case 10: 
            case 11: 
            case 12: {
                return false;
            }
            case 2: {
                AbstractNode owner = (AbstractNode)((Object)((Attr)((Object)this)).getOwnerElement());
                if (owner != null) {
                    return owner.isDefaultNamespace(namespaceURI);
                }
                return false;
            }
            case 1: {
                if (this.getPrefix() == null) {
                    String ns = this.getNamespaceURI();
                    return ns == null && namespaceURI == null || ns != null && ns.equals(namespaceURI);
                }
                NamedNodeMap nnm = this.getAttributes();
                if (nnm == null) break;
                for (int i = 0; i < nnm.getLength(); ++i) {
                    Node attr = nnm.item(i);
                    if (!"xmlns".equals(attr.getLocalName())) continue;
                    return attr.getNodeValue().equals(namespaceURI);
                }
                break;
            }
        }
        for (Node n = this; n != null; n = n.getParentNode()) {
            if (n.getNodeType() != 1) continue;
            AbstractNode an = n;
            return an.isDefaultNamespace(namespaceURI);
        }
        return false;
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        switch (this.getNodeType()) {
            case 9: {
                AbstractNode de = (AbstractNode)((Object)((Document)((Object)this)).getDocumentElement());
                return de.lookupNamespaceURI(prefix);
            }
            case 6: 
            case 10: 
            case 11: 
            case 12: {
                return null;
            }
            case 2: {
                AbstractNode owner = (AbstractNode)((Object)((Attr)((Object)this)).getOwnerElement());
                if (owner != null) {
                    return owner.lookupNamespaceURI(prefix);
                }
                return null;
            }
            case 1: {
                NamedNodeMap nnm = this.getAttributes();
                if (nnm == null) break;
                for (int i = 0; i < nnm.getLength(); ++i) {
                    Node attr = nnm.item(i);
                    String attrPrefix = attr.getPrefix();
                    String localName = attr.getLocalName();
                    if (localName == null) {
                        localName = attr.getNodeName();
                    }
                    if ((!"xmlns".equals(attrPrefix) || !this.compareStrings(localName, prefix)) && (!"xmlns".equals(localName) || prefix != null)) continue;
                    String value = attr.getNodeValue();
                    if (value.length() > 0) {
                        return value;
                    }
                    return null;
                }
                break;
            }
        }
        for (Node n = this.getParentNode(); n != null; n = n.getParentNode()) {
            if (n.getNodeType() != 1) continue;
            AbstractNode an = (AbstractNode)n;
            return an.lookupNamespaceURI(prefix);
        }
        return null;
    }

    @Override
    public boolean isEqualNode(Node other) {
        if (other == null) {
            return false;
        }
        short nt = other.getNodeType();
        if (!(nt == this.getNodeType() && this.compareStrings(this.getNodeName(), other.getNodeName()) && this.compareStrings(this.getLocalName(), other.getLocalName()) && this.compareStrings(this.getPrefix(), other.getPrefix()) && this.compareStrings(this.getNodeValue(), other.getNodeValue()) && this.compareStrings(this.getNodeValue(), other.getNodeValue()) && this.compareNamedNodeMaps(this.getAttributes(), other.getAttributes()))) {
            return false;
        }
        if (nt == 10) {
            DocumentType dt1 = (DocumentType)((Object)this);
            DocumentType dt2 = (DocumentType)other;
            if (!(this.compareStrings(dt1.getPublicId(), dt2.getPublicId()) && this.compareStrings(dt1.getSystemId(), dt2.getSystemId()) && this.compareStrings(dt1.getInternalSubset(), dt2.getInternalSubset()) && this.compareNamedNodeMaps(dt1.getEntities(), dt2.getEntities()) && this.compareNamedNodeMaps(dt1.getNotations(), dt2.getNotations()))) {
                return false;
            }
        }
        Node n = this.getFirstChild();
        Node m = other.getFirstChild();
        if (n != null && m != null && !n.isEqualNode(m)) {
            return false;
        }
        return n == m;
    }

    protected boolean compareStrings(String s1, String s2) {
        return s1 != null && s1.equals(s2) || s1 == null && s2 == null;
    }

    protected boolean compareNamedNodeMaps(NamedNodeMap nnm1, NamedNodeMap nnm2) {
        if (nnm1 == null && nnm2 != null || nnm1 != null && nnm2 == null) {
            return false;
        }
        if (nnm1 != null) {
            int len = nnm1.getLength();
            if (len != nnm2.getLength()) {
                return false;
            }
            for (int i = 0; i < len; ++i) {
                Node n1;
                String n1ln = (n1 = nnm1.item(i)).getLocalName();
                Node n2 = n1ln != null ? nnm2.getNamedItemNS(n1.getNamespaceURI(), n1ln) : nnm2.getNamedItem(n1.getNodeName());
                if (n1.isEqualNode(n2)) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public Object getFeature(String feature, String version) {
        return null;
    }

    @Override
    public Object getUserData(String key) {
        if (this.userData == null) {
            return null;
        }
        return this.userData.get(key);
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        if (this.userData == null) {
            this.userData = new HashMap();
            this.userDataHandlers = new HashMap();
        }
        if (data == null) {
            this.userData.remove(key);
            return this.userDataHandlers.remove(key);
        }
        this.userDataHandlers.put(key, handler);
        return this.userData.put(key, data);
    }

    protected void fireUserDataHandlers(short type, Node oldNode, Node newNode) {
        AbstractNode an = (AbstractNode)oldNode;
        if (an.userData != null) {
            for (Map.Entry o : an.userData.entrySet()) {
                Map.Entry e = o;
                UserDataHandler h = (UserDataHandler)an.userDataHandlers.get(e.getKey());
                if (h == null) continue;
                h.handle(type, (String)e.getKey(), e.getValue(), oldNode, newNode);
            }
        }
    }

    @Override
    public void addEventListener(String type, EventListener listener, boolean useCapture) {
        if (this.eventSupport == null) {
            this.initializeEventSupport();
        }
        this.eventSupport.addEventListener(type, listener, useCapture);
    }

    @Override
    public void addEventListenerNS(String namespaceURI, String type, EventListener listener, boolean useCapture, Object evtGroup) {
        if (this.eventSupport == null) {
            this.initializeEventSupport();
        }
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        this.eventSupport.addEventListenerNS(namespaceURI, type, listener, useCapture, evtGroup);
    }

    @Override
    public void removeEventListener(String type, EventListener listener, boolean useCapture) {
        if (this.eventSupport != null) {
            this.eventSupport.removeEventListener(type, listener, useCapture);
        }
    }

    @Override
    public void removeEventListenerNS(String namespaceURI, String type, EventListener listener, boolean useCapture) {
        if (this.eventSupport != null) {
            if (namespaceURI != null && namespaceURI.length() == 0) {
                namespaceURI = null;
            }
            this.eventSupport.removeEventListenerNS(namespaceURI, type, listener, useCapture);
        }
    }

    @Override
    public NodeEventTarget getParentNodeEventTarget() {
        return (NodeEventTarget)((Object)this.getXblParentNode());
    }

    @Override
    public boolean dispatchEvent(Event evt) throws EventException {
        if (this.eventSupport == null) {
            this.initializeEventSupport();
        }
        return this.eventSupport.dispatchEvent(this, evt);
    }

    public boolean willTriggerNS(String namespaceURI, String type) {
        return true;
    }

    public boolean hasEventListenerNS(String namespaceURI, String type) {
        if (this.eventSupport == null) {
            return false;
        }
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        return this.eventSupport.hasEventListenerNS(namespaceURI, type);
    }

    @Override
    public EventSupport getEventSupport() {
        return this.eventSupport;
    }

    public EventSupport initializeEventSupport() {
        if (this.eventSupport == null) {
            AbstractDocument doc = this.getCurrentDocument();
            AbstractDOMImplementation di = (AbstractDOMImplementation)doc.getImplementation();
            this.eventSupport = di.createEventSupport(this);
            doc.setEventsEnabled(true);
        }
        return this.eventSupport;
    }

    public void fireDOMNodeInsertedIntoDocumentEvent() {
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            DOMMutationEvent ev = (DOMMutationEvent)doc.createEvent("MutationEvents");
            ev.initMutationEventNS("http://www.w3.org/2001/xml-events", "DOMNodeInsertedIntoDocument", true, false, null, null, null, null, (short)2);
            this.dispatchEvent(ev);
        }
    }

    public void fireDOMNodeRemovedFromDocumentEvent() {
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            DOMMutationEvent ev = (DOMMutationEvent)doc.createEvent("MutationEvents");
            ev.initMutationEventNS("http://www.w3.org/2001/xml-events", "DOMNodeRemovedFromDocument", true, false, null, null, null, null, (short)3);
            this.dispatchEvent(ev);
        }
    }

    protected void fireDOMCharacterDataModifiedEvent(String oldv, String newv) {
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            DOMMutationEvent ev = (DOMMutationEvent)doc.createEvent("MutationEvents");
            ev.initMutationEventNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", true, false, null, oldv, newv, null, (short)1);
            this.dispatchEvent(ev);
        }
    }

    protected AbstractDocument getCurrentDocument() {
        return this.ownerDocument;
    }

    protected abstract Node newNode();

    protected Node export(Node n, AbstractDocument d) {
        AbstractNode p = (AbstractNode)n;
        p.ownerDocument = d;
        p.setReadonly(false);
        return n;
    }

    protected Node deepExport(Node n, AbstractDocument d) {
        AbstractNode p = (AbstractNode)n;
        p.ownerDocument = d;
        p.setReadonly(false);
        return n;
    }

    protected Node copyInto(Node n) {
        AbstractNode an = (AbstractNode)n;
        an.ownerDocument = this.ownerDocument;
        return n;
    }

    protected Node deepCopyInto(Node n) {
        AbstractNode an = (AbstractNode)n;
        an.ownerDocument = this.ownerDocument;
        return n;
    }

    protected void checkChildType(Node n, boolean replace) {
        throw this.createDOMException((short)3, "children.not.allowed", new Object[]{(int)this.getNodeType(), this.getNodeName()});
    }

    @Override
    public Node getXblParentNode() {
        return this.ownerDocument.getXBLManager().getXblParentNode(this);
    }

    @Override
    public NodeList getXblChildNodes() {
        return this.ownerDocument.getXBLManager().getXblChildNodes(this);
    }

    @Override
    public NodeList getXblScopedChildNodes() {
        return this.ownerDocument.getXBLManager().getXblScopedChildNodes(this);
    }

    @Override
    public Node getXblFirstChild() {
        return this.ownerDocument.getXBLManager().getXblFirstChild(this);
    }

    @Override
    public Node getXblLastChild() {
        return this.ownerDocument.getXBLManager().getXblLastChild(this);
    }

    @Override
    public Node getXblPreviousSibling() {
        return this.ownerDocument.getXBLManager().getXblPreviousSibling(this);
    }

    @Override
    public Node getXblNextSibling() {
        return this.ownerDocument.getXBLManager().getXblNextSibling(this);
    }

    @Override
    public Element getXblFirstElementChild() {
        return this.ownerDocument.getXBLManager().getXblFirstElementChild(this);
    }

    @Override
    public Element getXblLastElementChild() {
        return this.ownerDocument.getXBLManager().getXblLastElementChild(this);
    }

    @Override
    public Element getXblPreviousElementSibling() {
        return this.ownerDocument.getXBLManager().getXblPreviousElementSibling(this);
    }

    @Override
    public Element getXblNextElementSibling() {
        return this.ownerDocument.getXBLManager().getXblNextElementSibling(this);
    }

    @Override
    public Element getXblBoundElement() {
        return this.ownerDocument.getXBLManager().getXblBoundElement(this);
    }

    @Override
    public Element getXblShadowTree() {
        return this.ownerDocument.getXBLManager().getXblShadowTree(this);
    }

    @Override
    public NodeList getXblDefinitions() {
        return this.ownerDocument.getXBLManager().getXblDefinitions(this);
    }

    @Override
    public Object getManagerData() {
        return this.managerData;
    }

    @Override
    public void setManagerData(Object data) {
        this.managerData = data;
    }
}

