/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.i18n.Messages;
import org.apache.axis.message.NamedNodeMapImpl;
import org.apache.axis.message.NodeListImpl;
import org.apache.axis.message.NullAttributes;
import org.apache.axis.message.Text;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class NodeImpl
implements org.w3c.dom.Node,
Node,
Serializable,
Cloneable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$message$NodeImpl == null ? (class$org$apache$axis$message$NodeImpl = NodeImpl.class$("org.apache.axis.message.NodeImpl")) : class$org$apache$axis$message$NodeImpl).getName());
    protected String name;
    protected String prefix;
    protected String namespaceURI;
    protected transient Attributes attributes = NullAttributes.singleton;
    protected Document document = null;
    protected NodeImpl parent = null;
    protected ArrayList children = null;
    protected CharacterData textRep = null;
    protected boolean _isDirty = false;
    private static final String NULL_URI_NAME = "intentionalNullURI";
    static /* synthetic */ Class class$org$apache$axis$message$NodeImpl;

    public NodeImpl() {
    }

    public NodeImpl(CharacterData text) {
        this.textRep = text;
        this.namespaceURI = text.getNamespaceURI();
        this.name = text.getLocalName();
    }

    public short getNodeType() {
        if (this.textRep != null) {
            if (this.textRep instanceof Comment) {
                return 8;
            }
            if (this.textRep instanceof CDATASection) {
                return 4;
            }
            return 3;
        }
        return 1;
    }

    public void normalize() {
    }

    public boolean hasAttributes() {
        return this.attributes.getLength() > 0;
    }

    public boolean hasChildNodes() {
        return this.children != null && !this.children.isEmpty();
    }

    public String getLocalName() {
        return this.name;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String getNodeName() {
        return this.prefix != null && this.prefix.length() > 0 ? this.prefix + ":" + this.name : this.name;
    }

    public String getNodeValue() throws DOMException {
        if (this.textRep == null) {
            return null;
        }
        return this.textRep.getData();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        throw new DOMException(6, "Cannot use TextNode.set in " + this);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setOwnerDocument(Document doc) {
        this.document = doc;
    }

    public Document getOwnerDocument() {
        NodeImpl node;
        if (this.document == null && (node = this.getParent()) != null) {
            return node.getOwnerDocument();
        }
        return this.document;
    }

    public NamedNodeMap getAttributes() {
        this.makeAttributesEditable();
        return this.convertAttrSAXtoDOM(this.attributes);
    }

    public org.w3c.dom.Node getFirstChild() {
        if (this.children != null && !this.children.isEmpty()) {
            return (org.w3c.dom.Node)this.children.get(0);
        }
        return null;
    }

    public org.w3c.dom.Node getLastChild() {
        if (this.children != null && !this.children.isEmpty()) {
            return (org.w3c.dom.Node)this.children.get(this.children.size() - 1);
        }
        return null;
    }

    public org.w3c.dom.Node getNextSibling() {
        SOAPElement parent = this.getParentElement();
        if (parent == null) {
            return null;
        }
        Iterator iter = parent.getChildElements();
        org.w3c.dom.Node nextSibling = null;
        while (iter.hasNext()) {
            if (iter.next() != this) continue;
            if (iter.hasNext()) {
                return (org.w3c.dom.Node)iter.next();
            }
            return null;
        }
        return nextSibling;
    }

    public org.w3c.dom.Node getParentNode() {
        return this.getParent();
    }

    public org.w3c.dom.Node getPreviousSibling() {
        SOAPElement parent = this.getParentElement();
        if (parent == null) {
            return null;
        }
        NodeList nl = parent.getChildNodes();
        int len = nl.getLength();
        org.w3c.dom.Node previousSibling = null;
        for (int i = 0; i < len; ++i) {
            if (nl.item(i) == this) {
                return previousSibling;
            }
            previousSibling = nl.item(i);
        }
        return previousSibling;
    }

    public org.w3c.dom.Node cloneNode(boolean deep) {
        return new NodeImpl(this.textRep);
    }

    public NodeList getChildNodes() {
        if (this.children == null) {
            return NodeListImpl.EMPTY_NODELIST;
        }
        return new NodeListImpl(this.children);
    }

    public boolean isSupported(String feature, String version) {
        return false;
    }

    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
        if (newChild == null) {
            throw new DOMException(3, "Can't append a null node.");
        }
        this.initializeChildren();
        ((NodeImpl)newChild).detachNode();
        this.children.add(newChild);
        ((NodeImpl)newChild).parent = this;
        this.setDirty(true);
        return newChild;
    }

    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
        if (this.removeNodeFromChildList((NodeImpl)oldChild)) {
            this.setDirty(true);
            return oldChild;
        }
        throw new DOMException(8, "NodeImpl Not found");
    }

    private boolean removeNodeFromChildList(NodeImpl n) {
        boolean removed = false;
        this.initializeChildren();
        Iterator itr = this.children.iterator();
        while (itr.hasNext()) {
            NodeImpl node = (NodeImpl)itr.next();
            if (node != n) continue;
            removed = true;
            itr.remove();
        }
        return removed;
    }

    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
        this.initializeChildren();
        int position = this.children.indexOf(refChild);
        if (position < 0) {
            position = 0;
        }
        this.children.add(position, newChild);
        this.setDirty(true);
        return newChild;
    }

    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
        this.initializeChildren();
        int position = this.children.indexOf(oldChild);
        if (position < 0) {
            throw new DOMException(8, "NodeImpl Not found");
        }
        this.children.remove(position);
        this.children.add(position, newChild);
        this.setDirty(true);
        return oldChild;
    }

    public String getValue() {
        return this.textRep.getNodeValue();
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        if (parent == null) {
            throw new IllegalArgumentException(Messages.getMessage("nullParent00"));
        }
        try {
            this.setParent((NodeImpl)((Object)parent));
        }
        catch (Throwable t) {
            throw new SOAPException(t);
        }
    }

    public SOAPElement getParentElement() {
        return (SOAPElement)((Object)this.getParent());
    }

    public void detachNode() {
        if (this.parent != null) {
            this.parent.removeChild(this);
            this.parent = null;
        }
    }

    public void recycleNode() {
    }

    public void setValue(String value) {
        if (this instanceof Text) {
            this.setNodeValue(value);
        } else if (this.children != null) {
            if (this.children.size() != 1) {
                throw new IllegalStateException("setValue() may not be called on a non-Text node with more than one child.");
            }
            Node child = (Node)this.children.get(0);
            if (!(child instanceof Text)) {
                throw new IllegalStateException("setValue() may not be called on a non-Text node with a non-Text child.");
            }
            ((javax.xml.soap.Text)child).setNodeValue(value);
        } else {
            this.appendChild(new Text(value));
        }
    }

    protected AttributesImpl makeAttributesEditable() {
        if (this.attributes == null || this.attributes instanceof NullAttributes) {
            this.attributes = new AttributesImpl();
        } else if (!(this.attributes instanceof AttributesImpl)) {
            this.attributes = new AttributesImpl(this.attributes);
        }
        return (AttributesImpl)this.attributes;
    }

    protected NamedNodeMap convertAttrSAXtoDOM(Attributes saxAttr) {
        try {
            Document doc = XMLUtils.newDocument();
            AttributesImpl saxAttrs = (AttributesImpl)saxAttr;
            NamedNodeMapImpl domAttributes = new NamedNodeMapImpl();
            for (int i = 0; i < saxAttrs.getLength(); ++i) {
                Attr attr;
                String uri = saxAttrs.getURI(i);
                String qname = saxAttrs.getQName(i);
                String value = saxAttrs.getValue(i);
                if (uri != null && uri.trim().length() > 0) {
                    if (NULL_URI_NAME.equals(uri)) {
                        uri = null;
                    }
                    attr = doc.createAttributeNS(uri, qname);
                    attr.setValue(value);
                    domAttributes.setNamedItemNS(attr);
                    continue;
                }
                attr = doc.createAttribute(qname);
                attr.setValue(value);
                domAttributes.setNamedItem(attr);
            }
            return domAttributes;
        }
        catch (Exception ex) {
            log.error((Object)Messages.getMessage("saxToDomFailed00"), (Throwable)ex);
            return null;
        }
    }

    protected void initializeChildren() {
        if (this.children == null) {
            this.children = new ArrayList();
        }
    }

    protected NodeImpl getParent() {
        return this.parent;
    }

    protected void setParent(NodeImpl parent) throws SOAPException {
        if (this.parent == parent) {
            return;
        }
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        if (parent != null) {
            parent.appendChild(this);
        }
        this.parent = parent;
    }

    public void output(SerializationContext context) throws Exception {
        if (this.textRep == null) {
            return;
        }
        boolean oldPretty = context.getPretty();
        context.setPretty(false);
        if (this.textRep instanceof CDATASection) {
            context.writeString("<![CDATA[");
            context.writeString(((org.w3c.dom.Text)this.textRep).getData());
            context.writeString("]]>");
        } else if (this.textRep instanceof Comment) {
            context.writeString("<!--");
            context.writeString(this.textRep.getData());
            context.writeString("-->");
        } else if (this.textRep instanceof org.w3c.dom.Text) {
            context.writeSafeString(((org.w3c.dom.Text)this.textRep).getData());
        }
        context.setPretty(oldPretty);
    }

    public boolean isDirty() {
        return this._isDirty;
    }

    public void setDirty(boolean dirty) {
        this._isDirty = dirty;
        if (this._isDirty && this.parent != null) {
            this.parent.setDirty(true);
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
}

