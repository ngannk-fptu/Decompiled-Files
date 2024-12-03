/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.w3c.dom.ElementTraversal
 */
package org.apache.batik.dom;

import java.io.Serializable;
import org.apache.batik.dom.AbstractAttr;
import org.apache.batik.dom.AbstractAttrNS;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractParentChildNode;
import org.apache.batik.dom.AbstractParentNode;
import org.apache.batik.dom.events.DOMMutationEvent;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.w3c.dom.ElementTraversal;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

public abstract class AbstractElement
extends AbstractParentChildNode
implements Element,
ElementTraversal {
    protected NamedNodeMap attributes;
    protected TypeInfo typeInfo;

    protected AbstractElement() {
    }

    protected AbstractElement(String name, AbstractDocument owner) {
        this.ownerDocument = owner;
        if (owner.getStrictErrorChecking() && !DOMUtilities.isValidName(name)) {
            throw this.createDOMException((short)5, "xml.name", new Object[]{name});
        }
    }

    @Override
    public short getNodeType() {
        return 1;
    }

    @Override
    public boolean hasAttributes() {
        return this.attributes != null && this.attributes.getLength() != 0;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return this.attributes == null ? (this.attributes = this.createAttributes()) : this.attributes;
    }

    @Override
    public String getTagName() {
        return this.getNodeName();
    }

    @Override
    public boolean hasAttribute(String name) {
        return this.attributes != null && this.attributes.getNamedItem(name) != null;
    }

    @Override
    public String getAttribute(String name) {
        if (this.attributes == null) {
            return "";
        }
        Attr attr = (Attr)this.attributes.getNamedItem(name);
        return attr == null ? "" : attr.getValue();
    }

    @Override
    public void setAttribute(String name, String value) throws DOMException {
        Attr attr;
        if (this.attributes == null) {
            this.attributes = this.createAttributes();
        }
        if ((attr = this.getAttributeNode(name)) == null) {
            attr = this.getOwnerDocument().createAttribute(name);
            attr.setValue(value);
            this.attributes.setNamedItem(attr);
        } else {
            attr.setValue(value);
        }
    }

    @Override
    public void removeAttribute(String name) throws DOMException {
        if (!this.hasAttribute(name)) {
            return;
        }
        this.attributes.removeNamedItem(name);
    }

    @Override
    public Attr getAttributeNode(String name) {
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItem(name);
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        if (newAttr == null) {
            return null;
        }
        if (this.attributes == null) {
            this.attributes = this.createAttributes();
        }
        return (Attr)this.attributes.setNamedItemNS(newAttr);
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        String nsURI;
        if (oldAttr == null) {
            return null;
        }
        if (this.attributes == null) {
            throw this.createDOMException((short)8, "attribute.missing", new Object[]{oldAttr.getName()});
        }
        return (Attr)this.attributes.removeNamedItemNS(nsURI, (nsURI = oldAttr.getNamespaceURI()) == null ? oldAttr.getNodeName() : oldAttr.getLocalName());
    }

    @Override
    public void normalize() {
        super.normalize();
        if (this.attributes != null) {
            NamedNodeMap map = this.getAttributes();
            for (int i = map.getLength() - 1; i >= 0; --i) {
                map.item(i).normalize();
            }
        }
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        return this.attributes != null && this.attributes.getNamedItemNS(namespaceURI, localName) != null;
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) {
        Attr attr;
        if (this.attributes == null) {
            return "";
        }
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        return (attr = (Attr)this.attributes.getNamedItemNS(namespaceURI, localName)) == null ? "" : attr.getValue();
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        Attr attr;
        if (this.attributes == null) {
            this.attributes = this.createAttributes();
        }
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if ((attr = this.getAttributeNodeNS(namespaceURI, qualifiedName)) == null) {
            attr = this.getOwnerDocument().createAttributeNS(namespaceURI, qualifiedName);
            attr.setValue(value);
            this.attributes.setNamedItemNS(attr);
        } else {
            attr.setValue(value);
        }
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (!this.hasAttributeNS(namespaceURI, localName)) {
            return;
        }
        this.attributes.removeNamedItemNS(namespaceURI, localName);
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItemNS(namespaceURI, localName);
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        if (newAttr == null) {
            return null;
        }
        if (this.attributes == null) {
            this.attributes = this.createAttributes();
        }
        return (Attr)this.attributes.setNamedItemNS(newAttr);
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        if (this.typeInfo == null) {
            this.typeInfo = new ElementTypeInfo();
        }
        return this.typeInfo;
    }

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        AbstractAttr a = (AbstractAttr)this.getAttributeNode(name);
        if (a == null) {
            throw this.createDOMException((short)8, "attribute.missing", new Object[]{name});
        }
        if (a.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{name});
        }
        this.updateIdEntry(a, isId);
        a.isIdAttr = isId;
    }

    @Override
    public void setIdAttributeNS(String ns, String ln, boolean isId) throws DOMException {
        AbstractAttr a;
        if (ns != null && ns.length() == 0) {
            ns = null;
        }
        if ((a = (AbstractAttr)this.getAttributeNodeNS(ns, ln)) == null) {
            throw this.createDOMException((short)8, "attribute.missing", new Object[]{ns, ln});
        }
        if (a.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{a.getNodeName()});
        }
        this.updateIdEntry(a, isId);
        a.isIdAttr = isId;
    }

    @Override
    public void setIdAttributeNode(Attr attr, boolean isId) throws DOMException {
        AbstractAttr a = (AbstractAttr)attr;
        if (a.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{a.getNodeName()});
        }
        this.updateIdEntry(a, isId);
        a.isIdAttr = isId;
    }

    private void updateIdEntry(AbstractAttr a, boolean isId) {
        if (a.isIdAttr) {
            if (!isId) {
                this.ownerDocument.removeIdEntry(this, a.getValue());
            }
        } else if (isId) {
            this.ownerDocument.addIdEntry(this, a.getValue());
        }
    }

    protected Attr getIdAttribute() {
        NamedNodeMap nnm = this.getAttributes();
        if (nnm == null) {
            return null;
        }
        int len = nnm.getLength();
        for (int i = 0; i < len; ++i) {
            AbstractAttr a = (AbstractAttr)nnm.item(i);
            if (!a.isId()) continue;
            return a;
        }
        return null;
    }

    protected String getId() {
        String id;
        Attr a = this.getIdAttribute();
        if (a != null && (id = a.getNodeValue()).length() > 0) {
            return id;
        }
        return null;
    }

    @Override
    protected void nodeAdded(Node node) {
        this.invalidateElementsByTagName(node);
    }

    @Override
    protected void nodeToBeRemoved(Node node) {
        this.invalidateElementsByTagName(node);
    }

    private void invalidateElementsByTagName(Node node) {
        if (node.getNodeType() != 1) {
            return;
        }
        AbstractDocument ad = this.getCurrentDocument();
        String ns = node.getNamespaceURI();
        String nm = node.getNodeName();
        String ln = ns == null ? node.getNodeName() : node.getLocalName();
        block3: for (Node n = this; n != null; n = n.getParentNode()) {
            switch (n.getNodeType()) {
                case 1: 
                case 9: {
                    AbstractParentNode.ElementsByTagNameNS lns;
                    AbstractParentNode.ElementsByTagName l = ad.getElementsByTagName(n, nm);
                    if (l != null) {
                        l.invalidate();
                    }
                    if ((l = ad.getElementsByTagName(n, "*")) != null) {
                        l.invalidate();
                    }
                    if ((lns = ad.getElementsByTagNameNS(n, ns, ln)) != null) {
                        lns.invalidate();
                    }
                    if ((lns = ad.getElementsByTagNameNS(n, "*", ln)) != null) {
                        lns.invalidate();
                    }
                    if ((lns = ad.getElementsByTagNameNS(n, ns, "*")) != null) {
                        lns.invalidate();
                    }
                    if ((lns = ad.getElementsByTagNameNS(n, "*", "*")) == null) continue block3;
                    lns.invalidate();
                }
            }
        }
        for (Node c = node.getFirstChild(); c != null; c = c.getNextSibling()) {
            this.invalidateElementsByTagName(c);
        }
    }

    protected NamedNodeMap createAttributes() {
        return new NamedNodeHashMap();
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        super.export(n, d);
        AbstractElement ae = (AbstractElement)n;
        if (this.attributes != null) {
            NamedNodeMap map = this.attributes;
            for (int i = map.getLength() - 1; i >= 0; --i) {
                AbstractAttr aa = (AbstractAttr)map.item(i);
                if (!aa.getSpecified()) continue;
                Attr attr = (Attr)aa.deepExport(aa.cloneNode(false), d);
                if (aa instanceof AbstractAttrNS) {
                    ae.setAttributeNodeNS(attr);
                    continue;
                }
                ae.setAttributeNode(attr);
            }
        }
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        AbstractElement ae = (AbstractElement)n;
        if (this.attributes != null) {
            NamedNodeMap map = this.attributes;
            for (int i = map.getLength() - 1; i >= 0; --i) {
                AbstractAttr aa = (AbstractAttr)map.item(i);
                if (!aa.getSpecified()) continue;
                Attr attr = (Attr)aa.deepExport(aa.cloneNode(false), d);
                if (aa instanceof AbstractAttrNS) {
                    ae.setAttributeNodeNS(attr);
                    continue;
                }
                ae.setAttributeNode(attr);
            }
        }
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        super.copyInto(n);
        AbstractElement ae = (AbstractElement)n;
        if (this.attributes != null) {
            NamedNodeMap map = this.attributes;
            for (int i = map.getLength() - 1; i >= 0; --i) {
                AbstractAttr aa = (AbstractAttr)map.item(i).cloneNode(true);
                if (aa instanceof AbstractAttrNS) {
                    ae.setAttributeNodeNS(aa);
                    continue;
                }
                ae.setAttributeNode(aa);
            }
        }
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        AbstractElement ae = (AbstractElement)n;
        if (this.attributes != null) {
            NamedNodeMap map = this.attributes;
            for (int i = map.getLength() - 1; i >= 0; --i) {
                AbstractAttr aa = (AbstractAttr)map.item(i).cloneNode(true);
                if (aa instanceof AbstractAttrNS) {
                    ae.setAttributeNodeNS(aa);
                    continue;
                }
                ae.setAttributeNode(aa);
            }
        }
        return n;
    }

    @Override
    protected void checkChildType(Node n, boolean replace) {
        switch (n.getNodeType()) {
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 7: 
            case 8: 
            case 11: {
                break;
            }
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[]{(int)this.getNodeType(), this.getNodeName(), (int)n.getNodeType(), n.getNodeName()});
            }
        }
    }

    public void fireDOMAttrModifiedEvent(String name, Attr node, String oldv, String newv, short change) {
        switch (change) {
            case 2: {
                if (node.isId()) {
                    this.ownerDocument.addIdEntry(this, newv);
                }
                this.attrAdded(node, newv);
                break;
            }
            case 1: {
                if (node.isId()) {
                    this.ownerDocument.updateIdEntry(this, oldv, newv);
                }
                this.attrModified(node, oldv, newv);
                break;
            }
            default: {
                if (node.isId()) {
                    this.ownerDocument.removeIdEntry(this, oldv);
                }
                this.attrRemoved(node, oldv);
            }
        }
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled() && !oldv.equals(newv)) {
            DOMMutationEvent ev = (DOMMutationEvent)doc.createEvent("MutationEvents");
            ev.initMutationEventNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", true, false, node, oldv, newv, name, change);
            this.dispatchEvent(ev);
        }
    }

    protected void attrAdded(Attr node, String newv) {
    }

    protected void attrModified(Attr node, String oldv, String newv) {
    }

    protected void attrRemoved(Attr node, String oldv) {
    }

    public Element getFirstElementChild() {
        for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            return (Element)n;
        }
        return null;
    }

    public Element getLastElementChild() {
        for (Node n = this.getLastChild(); n != null; n = n.getPreviousSibling()) {
            if (n.getNodeType() != 1) continue;
            return (Element)n;
        }
        return null;
    }

    public Element getNextElementSibling() {
        for (Node n = this.getNextSibling(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            return (Element)n;
        }
        return null;
    }

    public Element getPreviousElementSibling() {
        Node n;
        for (n = this.getPreviousSibling(); n != null; n = n.getPreviousSibling()) {
            if (n.getNodeType() != 1) continue;
            return (Element)n;
        }
        return (Element)n;
    }

    public int getChildElementCount() {
        this.getChildNodes();
        return this.childNodes.elementChildren;
    }

    public static class ElementTypeInfo
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

    protected static class Entry
    implements Serializable {
        public int hash;
        public String namespaceURI;
        public String name;
        public Node value;
        public Entry next;

        public Entry(int hash, String ns, String nm, Node value, Entry next) {
            this.hash = hash;
            this.namespaceURI = ns;
            this.name = nm;
            this.value = value;
            this.next = next;
        }

        public boolean match(String ns, String nm) {
            if (this.namespaceURI != null ? !this.namespaceURI.equals(ns) : ns != null) {
                return false;
            }
            return this.name.equals(nm);
        }
    }

    public class NamedNodeHashMap
    implements NamedNodeMap,
    Serializable {
        protected static final int INITIAL_CAPACITY = 3;
        protected Entry[] table = new Entry[3];
        protected int count;

        @Override
        public Node getNamedItem(String name) {
            if (name == null) {
                return null;
            }
            return this.get(null, name);
        }

        @Override
        public Node setNamedItem(Node arg) throws DOMException {
            if (arg == null) {
                return null;
            }
            this.checkNode(arg);
            return this.setNamedItem(null, arg.getNodeName(), arg);
        }

        @Override
        public Node removeNamedItem(String name) throws DOMException {
            return this.removeNamedItemNS(null, name);
        }

        @Override
        public Node item(int index) {
            if (index < 0 || index >= this.count) {
                return null;
            }
            int j = 0;
            for (Entry aTable : this.table) {
                Entry e = aTable;
                if (e == null) continue;
                do {
                    if (j++ != index) continue;
                    return e.value;
                } while ((e = e.next) != null);
            }
            return null;
        }

        @Override
        public int getLength() {
            return this.count;
        }

        @Override
        public Node getNamedItemNS(String namespaceURI, String localName) {
            if (namespaceURI != null && namespaceURI.length() == 0) {
                namespaceURI = null;
            }
            return this.get(namespaceURI, localName);
        }

        @Override
        public Node setNamedItemNS(Node arg) throws DOMException {
            String nsURI;
            if (arg == null) {
                return null;
            }
            return this.setNamedItem(nsURI, (nsURI = arg.getNamespaceURI()) == null ? arg.getNodeName() : arg.getLocalName(), arg);
        }

        @Override
        public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
            AbstractAttr n;
            if (AbstractElement.this.isReadonly()) {
                throw AbstractElement.this.createDOMException((short)7, "readonly.node.map", new Object[0]);
            }
            if (localName == null) {
                throw AbstractElement.this.createDOMException((short)8, "attribute.missing", new Object[]{""});
            }
            if (namespaceURI != null && namespaceURI.length() == 0) {
                namespaceURI = null;
            }
            if ((n = (AbstractAttr)this.remove(namespaceURI, localName)) == null) {
                throw AbstractElement.this.createDOMException((short)8, "attribute.missing", new Object[]{localName});
            }
            n.setOwnerElement(null);
            AbstractElement.this.fireDOMAttrModifiedEvent(n.getNodeName(), n, n.getNodeValue(), "", (short)3);
            return n;
        }

        public Node setNamedItem(String ns, String name, Node arg) throws DOMException {
            if (ns != null && ns.length() == 0) {
                ns = null;
            }
            ((AbstractAttr)arg).setOwnerElement(AbstractElement.this);
            AbstractAttr result = (AbstractAttr)this.put(ns, name, arg);
            if (result != null) {
                result.setOwnerElement(null);
                AbstractElement.this.fireDOMAttrModifiedEvent(name, result, result.getNodeValue(), "", (short)3);
            }
            AbstractElement.this.fireDOMAttrModifiedEvent(name, (Attr)arg, "", arg.getNodeValue(), (short)2);
            return result;
        }

        protected void checkNode(Node arg) {
            if (AbstractElement.this.isReadonly()) {
                throw AbstractElement.this.createDOMException((short)7, "readonly.node.map", new Object[0]);
            }
            if (AbstractElement.this.getOwnerDocument() != arg.getOwnerDocument()) {
                throw AbstractElement.this.createDOMException((short)4, "node.from.wrong.document", new Object[]{(int)arg.getNodeType(), arg.getNodeName()});
            }
            if (arg.getNodeType() == 2 && ((Attr)arg).getOwnerElement() != null) {
                throw AbstractElement.this.createDOMException((short)4, "inuse.attribute", new Object[]{arg.getNodeName()});
            }
        }

        protected Node get(String ns, String nm) {
            int hash = this.hashCode(ns, nm) & Integer.MAX_VALUE;
            int index = hash % this.table.length;
            Entry e = this.table[index];
            while (e != null) {
                if (e.hash == hash && e.match(ns, nm)) {
                    return e.value;
                }
                e = e.next;
            }
            return null;
        }

        protected Node put(String ns, String nm, Node value) {
            Entry e;
            int hash = this.hashCode(ns, nm) & Integer.MAX_VALUE;
            int index = hash % this.table.length;
            Entry e2 = this.table[index];
            while (e2 != null) {
                if (e2.hash == hash && e2.match(ns, nm)) {
                    Node old = e2.value;
                    e2.value = value;
                    return old;
                }
                e2 = e2.next;
            }
            int len = this.table.length;
            if (this.count++ >= len - (len >> 2)) {
                this.rehash();
                index = hash % this.table.length;
            }
            this.table[index] = e = new Entry(hash, ns, nm, value, this.table[index]);
            return null;
        }

        protected Node remove(String ns, String nm) {
            int hash = this.hashCode(ns, nm) & Integer.MAX_VALUE;
            int index = hash % this.table.length;
            Entry p = null;
            Entry e = this.table[index];
            while (e != null) {
                if (e.hash == hash && e.match(ns, nm)) {
                    Node result = e.value;
                    if (p == null) {
                        this.table[index] = e.next;
                    } else {
                        p.next = e.next;
                    }
                    --this.count;
                    return result;
                }
                p = e;
                e = e.next;
            }
            return null;
        }

        protected void rehash() {
            Entry[] oldTable = this.table;
            this.table = new Entry[oldTable.length * 2 + 1];
            for (int i = oldTable.length - 1; i >= 0; --i) {
                Entry old = oldTable[i];
                while (old != null) {
                    Entry e = old;
                    old = old.next;
                    int index = e.hash % this.table.length;
                    e.next = this.table[index];
                    this.table[index] = e;
                }
            }
        }

        protected int hashCode(String ns, String nm) {
            int result = ns == null ? 0 : ns.hashCode();
            return result ^ nm.hashCode();
        }
    }
}

