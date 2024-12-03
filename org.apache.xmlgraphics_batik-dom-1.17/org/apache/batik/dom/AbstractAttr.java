/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.AbstractParentNode;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

public abstract class AbstractAttr
extends AbstractParentNode
implements Attr {
    protected String nodeName;
    protected boolean unspecified;
    protected boolean isIdAttr;
    protected AbstractElement ownerElement;
    protected TypeInfo typeInfo;

    protected AbstractAttr() {
    }

    protected AbstractAttr(String name, AbstractDocument owner) throws DOMException {
        this.ownerDocument = owner;
        if (owner.getStrictErrorChecking() && !DOMUtilities.isValidName(name)) {
            throw this.createDOMException((short)5, "xml.name", new Object[]{name});
        }
    }

    @Override
    public void setNodeName(String v) {
        this.nodeName = v;
        this.isIdAttr = this.ownerDocument.isId(this);
    }

    @Override
    public String getNodeName() {
        return this.nodeName;
    }

    @Override
    public short getNodeType() {
        return 2;
    }

    @Override
    public String getNodeValue() throws DOMException {
        Node first = this.getFirstChild();
        if (first == null) {
            return "";
        }
        Node n = first.getNextSibling();
        if (n == null) {
            return first.getNodeValue();
        }
        StringBuffer result = new StringBuffer(first.getNodeValue());
        do {
            result.append(n.getNodeValue());
        } while ((n = n.getNextSibling()) != null);
        return result.toString();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        Node n;
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        String s = this.getNodeValue();
        while ((n = this.getFirstChild()) != null) {
            this.removeChild(n);
        }
        String val = nodeValue == null ? "" : nodeValue;
        n = this.getOwnerDocument().createTextNode(val);
        this.appendChild(n);
        if (this.ownerElement != null) {
            this.ownerElement.fireDOMAttrModifiedEvent(this.nodeName, this, s, val, (short)1);
        }
    }

    @Override
    public String getName() {
        return this.getNodeName();
    }

    @Override
    public boolean getSpecified() {
        return !this.unspecified;
    }

    @Override
    public void setSpecified(boolean v) {
        this.unspecified = !v;
    }

    @Override
    public String getValue() {
        return this.getNodeValue();
    }

    @Override
    public void setValue(String value) throws DOMException {
        this.setNodeValue(value);
    }

    public void setOwnerElement(AbstractElement v) {
        this.ownerElement = v;
    }

    @Override
    public Element getOwnerElement() {
        return this.ownerElement;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        if (this.typeInfo == null) {
            this.typeInfo = new AttrTypeInfo();
        }
        return this.typeInfo;
    }

    @Override
    public boolean isId() {
        return this.isIdAttr;
    }

    public void setIsId(boolean isId) {
        this.isIdAttr = isId;
    }

    @Override
    protected void nodeAdded(Node n) {
        this.setSpecified(true);
    }

    @Override
    protected void nodeToBeRemoved(Node n) {
        this.setSpecified(true);
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        super.export(n, d);
        AbstractAttr aa = (AbstractAttr)n;
        aa.nodeName = this.nodeName;
        aa.unspecified = false;
        aa.isIdAttr = d.isId(aa);
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        AbstractAttr aa = (AbstractAttr)n;
        aa.nodeName = this.nodeName;
        aa.unspecified = false;
        aa.isIdAttr = d.isId(aa);
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        super.copyInto(n);
        AbstractAttr aa = (AbstractAttr)n;
        aa.nodeName = this.nodeName;
        aa.unspecified = this.unspecified;
        aa.isIdAttr = this.isIdAttr;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        AbstractAttr aa = (AbstractAttr)n;
        aa.nodeName = this.nodeName;
        aa.unspecified = this.unspecified;
        aa.isIdAttr = this.isIdAttr;
        return n;
    }

    @Override
    protected void checkChildType(Node n, boolean replace) {
        switch (n.getNodeType()) {
            case 3: 
            case 5: 
            case 11: {
                break;
            }
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[]{(int)this.getNodeType(), this.getNodeName(), (int)n.getNodeType(), n.getNodeName()});
            }
        }
    }

    @Override
    protected void fireDOMSubtreeModifiedEvent() {
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            super.fireDOMSubtreeModifiedEvent();
            if (this.getOwnerElement() != null) {
                ((AbstractElement)this.getOwnerElement()).fireDOMSubtreeModifiedEvent();
            }
        }
    }

    public static class AttrTypeInfo
    implements TypeInfo {
        @Override
        public String getTypeNamespace() {
            return null;
        }

        @Override
        public String getTypeName() {
            return null;
        }

        @Override
        public boolean isDerivedFrom(String ns, String name, int method) {
            return false;
        }
    }
}

