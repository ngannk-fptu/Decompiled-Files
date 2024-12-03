/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.i18n.Localizable
 *  org.apache.batik.i18n.LocalizableSupport
 *  org.apache.batik.util.CleanerThread$SoftReferenceCleared
 *  org.apache.batik.util.SoftDoublyIndexedTable
 *  org.apache.batik.w3c.dom.events.MutationNameEvent
 */
package org.apache.batik.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.WeakHashMap;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.batik.dom.AbstractAttr;
import org.apache.batik.dom.AbstractAttrNS;
import org.apache.batik.dom.AbstractDOMImplementation;
import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.AbstractParentNode;
import org.apache.batik.dom.AbstractText;
import org.apache.batik.dom.ExtendedNode;
import org.apache.batik.dom.GenericDocumentType;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.traversal.TraversalSupport;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.xbl.GenericXBLManager;
import org.apache.batik.dom.xbl.XBLManager;
import org.apache.batik.i18n.Localizable;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.util.CleanerThread;
import org.apache.batik.util.SoftDoublyIndexedTable;
import org.apache.batik.w3c.dom.events.MutationNameEvent;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMLocator;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;
import org.w3c.dom.xpath.XPathEvaluator;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;
import org.w3c.dom.xpath.XPathNSResolver;
import org.w3c.dom.xpath.XPathResult;

public abstract class AbstractDocument
extends AbstractParentNode
implements Document,
DocumentEvent,
DocumentTraversal,
Localizable,
XPathEvaluator {
    protected static final String RESOURCES = "org.apache.batik.dom.resources.Messages";
    protected transient LocalizableSupport localizableSupport = new LocalizableSupport("org.apache.batik.dom.resources.Messages", this.getClass().getClassLoader());
    protected transient DOMImplementation implementation;
    protected transient TraversalSupport traversalSupport;
    protected transient DocumentEventSupport documentEventSupport;
    protected transient boolean eventsEnabled;
    protected transient WeakHashMap elementsByTagNames;
    protected transient WeakHashMap elementsByTagNamesNS;
    protected String inputEncoding;
    protected String xmlEncoding;
    protected String xmlVersion = "1.0";
    protected boolean xmlStandalone;
    protected String documentURI;
    protected boolean strictErrorChecking = true;
    protected DocumentConfiguration domConfig;
    protected transient XBLManager xblManager = new GenericXBLManager();
    protected transient Map elementsById;

    protected AbstractDocument() {
    }

    public AbstractDocument(DocumentType dt, DOMImplementation impl) {
        this.implementation = impl;
        if (dt != null) {
            GenericDocumentType gdt;
            if (dt instanceof GenericDocumentType && (gdt = (GenericDocumentType)dt).getOwnerDocument() == null) {
                gdt.setOwnerDocument(this);
            }
            this.appendChild(dt);
        }
    }

    public void setDocumentInputEncoding(String ie) {
        this.inputEncoding = ie;
    }

    public void setDocumentXmlEncoding(String xe) {
        this.xmlEncoding = xe;
    }

    public void setLocale(Locale l) {
        this.localizableSupport.setLocale(l);
    }

    public Locale getLocale() {
        return this.localizableSupport.getLocale();
    }

    public String formatMessage(String key, Object[] args) throws MissingResourceException {
        return this.localizableSupport.formatMessage(key, args);
    }

    public boolean getEventsEnabled() {
        return this.eventsEnabled;
    }

    public void setEventsEnabled(boolean b) {
        this.eventsEnabled = b;
    }

    @Override
    public String getNodeName() {
        return "#document";
    }

    @Override
    public short getNodeType() {
        return 9;
    }

    @Override
    public DocumentType getDoctype() {
        for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 10) continue;
            return (DocumentType)n;
        }
        return null;
    }

    public void setDoctype(DocumentType dt) {
        if (dt != null) {
            this.appendChild(dt);
            ((ExtendedNode)((Object)dt)).setReadonly(true);
        }
    }

    @Override
    public DOMImplementation getImplementation() {
        return this.implementation;
    }

    @Override
    public Element getDocumentElement() {
        for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            return (Element)n;
        }
        return null;
    }

    @Override
    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        return this.importNode(importedNode, deep, false);
    }

    public Node importNode(Node importedNode, boolean deep, boolean trimId) {
        Node result;
        switch (importedNode.getNodeType()) {
            case 1: {
                Element e = this.createElementNS(importedNode.getNamespaceURI(), importedNode.getNodeName());
                result = e;
                if (!importedNode.hasAttributes()) break;
                NamedNodeMap attr = importedNode.getAttributes();
                int len = attr.getLength();
                for (int i = 0; i < len; ++i) {
                    Attr a = (Attr)attr.item(i);
                    if (!a.getSpecified()) continue;
                    AbstractAttr aa = (AbstractAttr)this.importNode(a, true);
                    if (trimId && aa.isId()) {
                        aa.setIsId(false);
                    }
                    e.setAttributeNodeNS(aa);
                }
                break;
            }
            case 2: {
                result = this.createAttributeNS(importedNode.getNamespaceURI(), importedNode.getNodeName());
                break;
            }
            case 3: {
                result = this.createTextNode(importedNode.getNodeValue());
                deep = false;
                break;
            }
            case 4: {
                result = this.createCDATASection(importedNode.getNodeValue());
                deep = false;
                break;
            }
            case 5: {
                result = this.createEntityReference(importedNode.getNodeName());
                break;
            }
            case 7: {
                result = this.createProcessingInstruction(importedNode.getNodeName(), importedNode.getNodeValue());
                deep = false;
                break;
            }
            case 8: {
                result = this.createComment(importedNode.getNodeValue());
                deep = false;
                break;
            }
            case 11: {
                result = this.createDocumentFragment();
                break;
            }
            case 10: {
                DocumentType docType = (DocumentType)importedNode;
                GenericDocumentType copy = new GenericDocumentType(docType.getName(), docType.getPublicId(), docType.getSystemId());
                copy.ownerDocument = this;
                result = copy;
                break;
            }
            default: {
                throw this.createDOMException((short)9, "import.node", new Object[0]);
            }
        }
        if (importedNode instanceof AbstractNode) {
            this.fireUserDataHandlers((short)2, importedNode, result);
        }
        if (deep) {
            for (Node n = importedNode.getFirstChild(); n != null; n = n.getNextSibling()) {
                result.appendChild(this.importNode(n, true));
            }
        }
        return result;
    }

    @Override
    public Node cloneNode(boolean deep) {
        Document n = (Document)this.newNode();
        this.copyInto(n);
        this.fireUserDataHandlers((short)1, this, n);
        if (deep) {
            for (Node c = this.getFirstChild(); c != null; c = c.getNextSibling()) {
                n.appendChild(n.importNode(c, deep));
            }
        }
        return n;
    }

    public abstract boolean isId(Attr var1);

    @Override
    public Element getElementById(String id) {
        return this.getChildElementById(this.getDocumentElement(), id);
    }

    public Element getChildElementById(Node requestor, String id) {
        if (id == null || id.length() == 0) {
            return null;
        }
        if (this.elementsById == null) {
            return null;
        }
        Node root = this.getRoot(requestor);
        Object o = this.elementsById.get(id);
        if (o == null) {
            return null;
        }
        if (o instanceof IdSoftRef) {
            if ((o = ((IdSoftRef)((Object)o)).get()) == null) {
                this.elementsById.remove(id);
                return null;
            }
            Element e = (Element)o;
            if (this.getRoot(e) == root) {
                return e;
            }
            return null;
        }
        List l = (List)o;
        Iterator li = l.iterator();
        while (li.hasNext()) {
            IdSoftRef sr = (IdSoftRef)((Object)li.next());
            o = sr.get();
            if (o == null) {
                li.remove();
                continue;
            }
            Element e = (Element)o;
            if (this.getRoot(e) != root) continue;
            return e;
        }
        return null;
    }

    protected Node getRoot(Node n) {
        Node r = n;
        while (n != null) {
            r = n;
            n = n.getParentNode();
        }
        return r;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeIdEntry(Element e, String id) {
        if (id == null) {
            return;
        }
        if (this.elementsById == null) {
            return;
        }
        Map map = this.elementsById;
        synchronized (map) {
            Object o = this.elementsById.get(id);
            if (o == null) {
                return;
            }
            if (o instanceof IdSoftRef) {
                this.elementsById.remove(id);
                return;
            }
            List l = (List)o;
            Iterator li = l.iterator();
            while (li.hasNext()) {
                IdSoftRef ip = (IdSoftRef)((Object)li.next());
                o = ip.get();
                if (o == null) {
                    li.remove();
                    continue;
                }
                if (e != o) continue;
                li.remove();
                break;
            }
            if (l.size() == 0) {
                this.elementsById.remove(id);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addIdEntry(Element e, String id) {
        if (id == null) {
            return;
        }
        if (this.elementsById == null) {
            HashMap<String, IdSoftRef> tmp = new HashMap<String, IdSoftRef>();
            tmp.put(id, new IdSoftRef(e, id));
            this.elementsById = tmp;
            return;
        }
        Map map = this.elementsById;
        synchronized (map) {
            Object o = this.elementsById.get(id);
            if (o == null) {
                this.elementsById.put(id, new IdSoftRef(e, id));
                return;
            }
            if (o instanceof IdSoftRef) {
                IdSoftRef ip = (IdSoftRef)((Object)o);
                Object r = ip.get();
                if (r == null) {
                    this.elementsById.put(id, new IdSoftRef(e, id));
                    return;
                }
                ArrayList<IdSoftRef> l = new ArrayList<IdSoftRef>(4);
                ip.setList(l);
                l.add(ip);
                l.add(new IdSoftRef(e, id, l));
                this.elementsById.put(id, l);
                return;
            }
            List l = (List)o;
            l.add(new IdSoftRef(e, id, l));
        }
    }

    public void updateIdEntry(Element e, String oldId, String newId) {
        if (oldId == newId || oldId != null && oldId.equals(newId)) {
            return;
        }
        this.removeIdEntry(e, oldId);
        this.addIdEntry(e, newId);
    }

    public AbstractParentNode.ElementsByTagName getElementsByTagName(Node n, String ln) {
        if (this.elementsByTagNames == null) {
            return null;
        }
        SoftDoublyIndexedTable t = (SoftDoublyIndexedTable)this.elementsByTagNames.get(n);
        if (t == null) {
            return null;
        }
        return (AbstractParentNode.ElementsByTagName)t.get(null, (Object)ln);
    }

    public void putElementsByTagName(Node n, String ln, AbstractParentNode.ElementsByTagName l) {
        SoftDoublyIndexedTable t;
        if (this.elementsByTagNames == null) {
            this.elementsByTagNames = new WeakHashMap(11);
        }
        if ((t = (SoftDoublyIndexedTable)this.elementsByTagNames.get(n)) == null) {
            t = new SoftDoublyIndexedTable();
            this.elementsByTagNames.put(n, t);
        }
        t.put(null, (Object)ln, (Object)l);
    }

    public AbstractParentNode.ElementsByTagNameNS getElementsByTagNameNS(Node n, String ns, String ln) {
        if (this.elementsByTagNamesNS == null) {
            return null;
        }
        SoftDoublyIndexedTable t = (SoftDoublyIndexedTable)this.elementsByTagNamesNS.get(n);
        if (t == null) {
            return null;
        }
        return (AbstractParentNode.ElementsByTagNameNS)t.get((Object)ns, (Object)ln);
    }

    public void putElementsByTagNameNS(Node n, String ns, String ln, AbstractParentNode.ElementsByTagNameNS l) {
        SoftDoublyIndexedTable t;
        if (this.elementsByTagNamesNS == null) {
            this.elementsByTagNamesNS = new WeakHashMap(11);
        }
        if ((t = (SoftDoublyIndexedTable)this.elementsByTagNamesNS.get(n)) == null) {
            t = new SoftDoublyIndexedTable();
            this.elementsByTagNamesNS.put(n, t);
        }
        t.put((Object)ns, (Object)ln, (Object)l);
    }

    @Override
    public Event createEvent(String eventType) throws DOMException {
        if (this.documentEventSupport == null) {
            this.documentEventSupport = ((AbstractDOMImplementation)this.implementation).createDocumentEventSupport();
        }
        return this.documentEventSupport.createEvent(eventType);
    }

    public boolean canDispatch(String ns, String eventType) {
        if (eventType == null) {
            return false;
        }
        if (ns != null && ns.length() == 0) {
            ns = null;
        }
        if (ns == null || ns.equals("http://www.w3.org/2001/xml-events")) {
            return eventType.equals("Event") || eventType.equals("MutationEvent") || eventType.equals("MutationNameEvent") || eventType.equals("UIEvent") || eventType.equals("MouseEvent") || eventType.equals("KeyEvent") || eventType.equals("KeyboardEvent") || eventType.equals("TextEvent") || eventType.equals("CustomEvent");
        }
        return false;
    }

    @Override
    public NodeIterator createNodeIterator(Node root, int whatToShow, NodeFilter filter, boolean entityReferenceExpansion) throws DOMException {
        if (this.traversalSupport == null) {
            this.traversalSupport = new TraversalSupport();
        }
        return this.traversalSupport.createNodeIterator(this, root, whatToShow, filter, entityReferenceExpansion);
    }

    @Override
    public TreeWalker createTreeWalker(Node root, int whatToShow, NodeFilter filter, boolean entityReferenceExpansion) throws DOMException {
        return TraversalSupport.createTreeWalker(this, root, whatToShow, filter, entityReferenceExpansion);
    }

    public void detachNodeIterator(NodeIterator it) {
        this.traversalSupport.detachNodeIterator(it);
    }

    @Override
    public void nodeToBeRemoved(Node node) {
        if (this.traversalSupport != null) {
            this.traversalSupport.nodeToBeRemoved(node);
        }
    }

    @Override
    protected AbstractDocument getCurrentDocument() {
        return this;
    }

    protected Node export(Node n, Document d) {
        throw this.createDOMException((short)9, "import.document", new Object[0]);
    }

    protected Node deepExport(Node n, Document d) {
        throw this.createDOMException((short)9, "import.document", new Object[0]);
    }

    @Override
    protected Node copyInto(Node n) {
        super.copyInto(n);
        AbstractDocument ad = (AbstractDocument)n;
        ad.implementation = this.implementation;
        ad.localizableSupport = new LocalizableSupport(RESOURCES, this.getClass().getClassLoader());
        ad.inputEncoding = this.inputEncoding;
        ad.xmlEncoding = this.xmlEncoding;
        ad.xmlVersion = this.xmlVersion;
        ad.xmlStandalone = this.xmlStandalone;
        ad.documentURI = this.documentURI;
        ad.strictErrorChecking = this.strictErrorChecking;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        AbstractDocument ad = (AbstractDocument)n;
        ad.implementation = this.implementation;
        ad.localizableSupport = new LocalizableSupport(RESOURCES, this.getClass().getClassLoader());
        return n;
    }

    @Override
    protected void checkChildType(Node n, boolean replace) {
        short t = n.getNodeType();
        switch (t) {
            case 1: 
            case 7: 
            case 8: 
            case 10: 
            case 11: {
                break;
            }
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[]{(int)this.getNodeType(), this.getNodeName(), (int)t, n.getNodeName()});
            }
        }
        if (!replace && t == 1 && this.getDocumentElement() != null || t == 10 && this.getDoctype() != null) {
            throw this.createDOMException((short)9, "document.child.already.exists", new Object[]{(int)t, n.getNodeName()});
        }
    }

    @Override
    public String getInputEncoding() {
        return this.inputEncoding;
    }

    @Override
    public String getXmlEncoding() {
        return this.xmlEncoding;
    }

    @Override
    public boolean getXmlStandalone() {
        return this.xmlStandalone;
    }

    @Override
    public void setXmlStandalone(boolean b) throws DOMException {
        this.xmlStandalone = b;
    }

    @Override
    public String getXmlVersion() {
        return this.xmlVersion;
    }

    @Override
    public void setXmlVersion(String v) throws DOMException {
        if (v == null || !v.equals("1.0") && !v.equals("1.1")) {
            throw this.createDOMException((short)9, "xml.version", new Object[]{v});
        }
        this.xmlVersion = v;
    }

    @Override
    public boolean getStrictErrorChecking() {
        return this.strictErrorChecking;
    }

    @Override
    public void setStrictErrorChecking(boolean b) {
        this.strictErrorChecking = b;
    }

    @Override
    public String getDocumentURI() {
        return this.documentURI;
    }

    @Override
    public void setDocumentURI(String uri) {
        this.documentURI = uri;
    }

    @Override
    public DOMConfiguration getDomConfig() {
        if (this.domConfig == null) {
            this.domConfig = new DocumentConfiguration();
        }
        return this.domConfig;
    }

    @Override
    public Node adoptNode(Node n) throws DOMException {
        if (!(n instanceof AbstractNode)) {
            return null;
        }
        switch (n.getNodeType()) {
            case 9: {
                throw this.createDOMException((short)9, "adopt.document", new Object[0]);
            }
            case 10: {
                throw this.createDOMException((short)9, "adopt.document.type", new Object[0]);
            }
            case 6: 
            case 12: {
                return null;
            }
        }
        AbstractNode an = (AbstractNode)n;
        if (an.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)an.getNodeType(), an.getNodeName()});
        }
        Node parent = n.getParentNode();
        if (parent != null) {
            parent.removeChild(n);
        }
        this.adoptNode1((AbstractNode)n);
        return n;
    }

    protected void adoptNode1(AbstractNode n) {
        n.ownerDocument = this;
        switch (n.getNodeType()) {
            case 2: {
                AbstractAttr attr = (AbstractAttr)n;
                attr.ownerElement = null;
                attr.unspecified = false;
                break;
            }
            case 1: {
                AbstractAttr attr;
                NamedNodeMap nnm = n.getAttributes();
                int len = nnm.getLength();
                for (int i = 0; i < len; ++i) {
                    attr = (AbstractAttr)nnm.item(i);
                    if (!attr.getSpecified()) continue;
                    this.adoptNode1(attr);
                }
                break;
            }
            case 5: {
                while (n.getFirstChild() != null) {
                    n.removeChild(n.getFirstChild());
                }
                break;
            }
        }
        this.fireUserDataHandlers((short)5, n, null);
        for (Node m = n.getFirstChild(); m != null; m = m.getNextSibling()) {
            switch (m.getNodeType()) {
                case 6: 
                case 10: 
                case 12: {
                    return;
                }
            }
            this.adoptNode1((AbstractNode)m);
        }
    }

    @Override
    public Node renameNode(Node n, String ns, String qn) {
        AbstractNode an = (AbstractNode)n;
        if (an == this.getDocumentElement()) {
            throw this.createDOMException((short)9, "rename.document.element", new Object[0]);
        }
        short nt = n.getNodeType();
        if (nt != 1 && nt != 2) {
            throw this.createDOMException((short)9, "rename.node", new Object[]{(int)nt, n.getNodeName()});
        }
        if (this.xmlVersion.equals("1.1") && !DOMUtilities.isValidName11(qn) || !DOMUtilities.isValidName(qn)) {
            throw this.createDOMException((short)9, "wf.invalid.name", new Object[]{qn});
        }
        if (n.getOwnerDocument() != this) {
            throw this.createDOMException((short)9, "node.from.wrong.document", new Object[]{(int)nt, n.getNodeName()});
        }
        int i = qn.indexOf(58);
        if (i == 0 || i == qn.length() - 1) {
            throw this.createDOMException((short)14, "qname", new Object[]{(int)nt, n.getNodeName(), qn});
        }
        String prefix = DOMUtilities.getPrefix(qn);
        if (ns != null && ns.length() == 0) {
            ns = null;
        }
        if (prefix != null && ns == null) {
            throw this.createDOMException((short)14, "prefix", new Object[]{(int)nt, n.getNodeName(), prefix});
        }
        if (this.strictErrorChecking && ("xml".equals(prefix) && !"http://www.w3.org/XML/1998/namespace".equals(ns) || "xmlns".equals(prefix) && !"http://www.w3.org/2000/xmlns/".equals(ns))) {
            throw this.createDOMException((short)14, "namespace", new Object[]{(int)nt, n.getNodeName(), ns});
        }
        String prevNamespaceURI = n.getNamespaceURI();
        String prevNodeName = n.getNodeName();
        if (nt == 1) {
            Node parent = n.getParentNode();
            AbstractElement e = (AbstractElement)this.createElementNS(ns, qn);
            EventSupport es1 = an.getEventSupport();
            if (es1 != null) {
                EventSupport es2 = e.getEventSupport();
                if (es2 == null) {
                    AbstractDOMImplementation di = (AbstractDOMImplementation)this.implementation;
                    es2 = di.createEventSupport(e);
                    this.setEventsEnabled(true);
                    e.eventSupport = es2;
                }
                es1.moveEventListeners(e.getEventSupport());
            }
            e.userData = e.userData == null ? null : (HashMap)an.userData.clone();
            e.userDataHandlers = e.userDataHandlers == null ? null : (HashMap)an.userDataHandlers.clone();
            Node next = null;
            if (parent != null) {
                n.getNextSibling();
                parent.removeChild(n);
            }
            while (n.getFirstChild() != null) {
                e.appendChild(n.getFirstChild());
            }
            NamedNodeMap nnm = n.getAttributes();
            for (int j = 0; j < nnm.getLength(); ++j) {
                Attr a = (Attr)nnm.item(j);
                e.setAttributeNodeNS(a);
            }
            if (parent != null) {
                if (next == null) {
                    parent.appendChild(e);
                } else {
                    parent.insertBefore(next, e);
                }
            }
            this.fireUserDataHandlers((short)4, n, e);
            if (this.getEventsEnabled()) {
                MutationNameEvent ev = (MutationNameEvent)this.createEvent("MutationNameEvent");
                ev.initMutationNameEventNS("http://www.w3.org/2001/xml-events", "DOMElementNameChanged", true, false, null, prevNamespaceURI, prevNodeName);
                this.dispatchEvent((Event)ev);
            }
            return e;
        }
        if (n instanceof AbstractAttrNS) {
            AbstractAttrNS a = (AbstractAttrNS)n;
            Element e = a.getOwnerElement();
            if (e != null) {
                e.removeAttributeNode(a);
            }
            a.namespaceURI = ns;
            a.nodeName = qn;
            if (e != null) {
                e.setAttributeNodeNS(a);
            }
            this.fireUserDataHandlers((short)4, a, null);
            if (this.getEventsEnabled()) {
                MutationNameEvent ev = (MutationNameEvent)this.createEvent("MutationNameEvent");
                ev.initMutationNameEventNS("http://www.w3.org/2001/xml-events", "DOMAttrNameChanged", true, false, (Node)a, prevNamespaceURI, prevNodeName);
                this.dispatchEvent((Event)ev);
            }
            return a;
        }
        AbstractAttr a = (AbstractAttr)n;
        Element e = a.getOwnerElement();
        if (e != null) {
            e.removeAttributeNode(a);
        }
        AbstractAttr a2 = (AbstractAttr)this.createAttributeNS(ns, qn);
        a2.setNodeValue(a.getNodeValue());
        a2.userData = a.userData == null ? null : (HashMap)a.userData.clone();
        HashMap hashMap = a2.userDataHandlers = a.userDataHandlers == null ? null : (HashMap)a.userDataHandlers.clone();
        if (e != null) {
            e.setAttributeNodeNS(a2);
        }
        this.fireUserDataHandlers((short)4, a, a2);
        if (this.getEventsEnabled()) {
            MutationNameEvent ev = (MutationNameEvent)this.createEvent("MutationNameEvent");
            ev.initMutationNameEventNS("http://www.w3.org/2001/xml-events", "DOMAttrNameChanged", true, false, (Node)a2, prevNamespaceURI, prevNodeName);
            this.dispatchEvent((Event)ev);
        }
        return a2;
    }

    @Override
    public void normalizeDocument() {
        if (this.domConfig == null) {
            this.domConfig = new DocumentConfiguration();
        }
        boolean cdataSections = this.domConfig.getBooleanParameter("cdata-sections");
        boolean comments = this.domConfig.getBooleanParameter("comments");
        boolean elementContentWhitespace = this.domConfig.getBooleanParameter("element-content-whitespace");
        boolean namespaceDeclarations = this.domConfig.getBooleanParameter("namespace-declarations");
        boolean namespaces = this.domConfig.getBooleanParameter("namespaces");
        boolean splitCdataSections = this.domConfig.getBooleanParameter("split-cdata-sections");
        DOMErrorHandler errorHandler = (DOMErrorHandler)this.domConfig.getParameter("error-handler");
        this.normalizeDocument(this.getDocumentElement(), cdataSections, comments, elementContentWhitespace, namespaceDeclarations, namespaces, splitCdataSections, errorHandler);
    }

    /*
     * WARNING - void declaration
     */
    protected boolean normalizeDocument(Element e, boolean cdataSections, boolean comments, boolean elementContentWhitepace, boolean namespaceDeclarations, boolean namespaces, boolean splitCdataSections, DOMErrorHandler errorHandler) {
        AbstractElement ae = (AbstractElement)e;
        Node n = e.getFirstChild();
        while (n != null) {
            short nt = n.getNodeType();
            if (nt == 3 || !cdataSections && nt == 4) {
                AbstractText abstractText;
                Node t = n;
                StringBuffer sb = new StringBuffer();
                sb.append(t.getNodeValue());
                n = n.getNextSibling();
                while (n != null && (n.getNodeType() == 3 || !cdataSections && n.getNodeType() == 4)) {
                    sb.append(n.getNodeValue());
                    Node next = n.getNextSibling();
                    e.removeChild(n);
                    n = next;
                }
                String s = sb.toString();
                if (s.length() == 0) {
                    Node node = n.getNextSibling();
                    e.removeChild(n);
                    n = node;
                    continue;
                }
                if (!s.equals(t.getNodeValue())) {
                    if (!cdataSections && nt == 3) {
                        n = this.createTextNode(s);
                        e.replaceChild(n, t);
                    } else {
                        n = t;
                        t.setNodeValue(s);
                    }
                } else {
                    n = t;
                }
                if (!elementContentWhitepace && (nt = n.getNodeType()) == 3 && (abstractText = (AbstractText)n).isElementContentWhitespace()) {
                    Node next = n.getNextSibling();
                    e.removeChild(n);
                    n = next;
                    continue;
                }
                if (nt == 4 && splitCdataSections && !this.splitCdata(e, n, errorHandler)) {
                    return false;
                }
            } else if (nt == 4 && splitCdataSections) {
                if (!this.splitCdata(e, n, errorHandler)) {
                    return false;
                }
            } else if (nt == 8 && !comments) {
                Node next = n.getPreviousSibling();
                if (next == null) {
                    next = n.getNextSibling();
                }
                e.removeChild(n);
                n = next;
                continue;
            }
            n = n.getNextSibling();
        }
        NamedNodeMap nnm = e.getAttributes();
        LinkedList<Attr> toRemove = new LinkedList<Attr>();
        HashMap<String, String> names = new HashMap<String, String>();
        for (int i = 0; i < nnm.getLength(); ++i) {
            Attr attr = (Attr)nnm.item(i);
            String prefix = attr.getPrefix();
            if ((attr == null || !"xmlns".equals(prefix)) && !attr.getNodeName().equals("xmlns")) continue;
            if (!namespaceDeclarations) {
                toRemove.add(attr);
                continue;
            }
            String ns = attr.getNodeValue();
            if (attr.getNodeValue().equals("http://www.w3.org/2000/xmlns/") || !ns.equals("http://www.w3.org/2000/xmlns/")) continue;
            names.put(prefix, ns);
        }
        if (!namespaceDeclarations) {
            for (Object e2 : toRemove) {
                e.removeAttributeNode((Attr)e2);
            }
        } else if (namespaces) {
            void var15_32;
            String ens = e.getNamespaceURI();
            if (ens != null) {
                String string = e.getPrefix();
                if (!this.compareStrings(ae.lookupNamespaceURI(string), ens)) {
                    e.setAttributeNS("http://www.w3.org/2000/xmlns/", string == null ? "xmlns" : "xmlns:" + string, ens);
                }
            } else if (e.getLocalName() != null && ae.lookupNamespaceURI(null) == null) {
                e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "");
            }
            nnm = e.getAttributes();
            boolean bl = false;
            while (var15_32 < nnm.getLength()) {
                Attr a = (Attr)nnm.item((int)var15_32);
                String ans = a.getNamespaceURI();
                if (ans != null) {
                    String apre = a.getPrefix();
                    if (!(apre != null && (apre.equals("xml") || apre.equals("xmlns")) || ans.equals("http://www.w3.org/2000/xmlns/"))) {
                        String aprens;
                        String string = aprens = apre == null ? null : ae.lookupNamespaceURI(apre);
                        if (apre == null || aprens == null || !aprens.equals(ans)) {
                            String newpre = ae.lookupPrefix(ans);
                            if (newpre != null) {
                                a.setPrefix(newpre);
                            } else if (apre != null && ae.lookupNamespaceURI(apre) == null) {
                                e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + apre, ans);
                            } else {
                                int index = 1;
                                while (ae.lookupPrefix(newpre = "NS" + index) != null) {
                                }
                                e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + newpre, ans);
                                a.setPrefix(newpre);
                            }
                        }
                    }
                } else if (a.getLocalName() == null) {
                    // empty if block
                }
                ++var15_32;
            }
        }
        nnm = e.getAttributes();
        for (int i = 0; i < nnm.getLength(); ++i) {
            Attr attr = (Attr)nnm.item(i);
            if (!this.checkName(attr.getNodeName()) && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character-in-node-name", (short)2, "wf.invalid.name", new Object[]{attr.getNodeName()}, attr, null))) {
                return false;
            }
            if (this.checkChars(attr.getNodeValue()) || errorHandler == null || errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[]{2, attr.getNodeName(), attr.getNodeValue()}, attr, null))) continue;
            return false;
        }
        block14: for (Node m = e.getFirstChild(); m != null; m = m.getNextSibling()) {
            short s = m.getNodeType();
            switch (s) {
                case 3: {
                    String s2 = m.getNodeValue();
                    if (this.checkChars(s2) || errorHandler == null || errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[]{(int)m.getNodeType(), m.getNodeName(), s2}, m, null))) continue block14;
                    return false;
                }
                case 8: {
                    String s2 = m.getNodeValue();
                    if (this.checkChars(s2) && s2.indexOf("--") == -1 && s2.charAt(s2.length() - 1) != '-' || errorHandler == null || errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[]{(int)m.getNodeType(), m.getNodeName(), s2}, m, null))) continue block14;
                    return false;
                }
                case 4: {
                    String s2 = m.getNodeValue();
                    if (this.checkChars(s2) && s2.indexOf("]]>") == -1 || errorHandler == null || errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[]{(int)m.getNodeType(), m.getNodeName(), s2}, m, null))) continue block14;
                    return false;
                }
                case 7: {
                    if (m.getNodeName().equalsIgnoreCase("xml") && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character-in-node-name", (short)2, "wf.invalid.name", new Object[]{m.getNodeName()}, m, null))) {
                        return false;
                    }
                    String s2 = m.getNodeValue();
                    if (this.checkChars(s2) && s2.indexOf("?>") == -1 || errorHandler == null || errorHandler.handleError(this.createDOMError("wf-invalid-character", (short)2, "wf.invalid.character", new Object[]{(int)m.getNodeType(), m.getNodeName(), s2}, m, null))) continue block14;
                    return false;
                }
                case 1: {
                    if (!this.checkName(m.getNodeName()) && errorHandler != null && !errorHandler.handleError(this.createDOMError("wf-invalid-character-in-node-name", (short)2, "wf.invalid.name", new Object[]{m.getNodeName()}, m, null))) {
                        return false;
                    }
                    if (this.normalizeDocument((Element)m, cdataSections, comments, elementContentWhitepace, namespaceDeclarations, namespaces, splitCdataSections, errorHandler)) continue block14;
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean splitCdata(Element e, Node n, DOMErrorHandler errorHandler) {
        String s2 = n.getNodeValue();
        int index = s2.indexOf("]]>");
        if (index != -1) {
            String before = s2.substring(0, index + 2);
            String after = s2.substring(index + 2);
            n.setNodeValue(before);
            Node next = n.getNextSibling();
            if (next == null) {
                e.appendChild(this.createCDATASection(after));
            } else {
                e.insertBefore(this.createCDATASection(after), next);
            }
            if (errorHandler != null && !errorHandler.handleError(this.createDOMError("cdata-sections-splitted", (short)1, "cdata.section.split", new Object[0], n, null))) {
                return false;
            }
        }
        return true;
    }

    protected boolean checkChars(String s) {
        int len = s.length();
        if (this.xmlVersion.equals("1.1")) {
            for (int i = 0; i < len; ++i) {
                if (DOMUtilities.isXML11Character((int)s.charAt(i))) continue;
                return false;
            }
        } else {
            for (int i = 0; i < len; ++i) {
                if (DOMUtilities.isXMLCharacter((int)s.charAt(i))) continue;
                return false;
            }
        }
        return true;
    }

    protected boolean checkName(String s) {
        if (this.xmlVersion.equals("1.1")) {
            return DOMUtilities.isValidName11(s);
        }
        return DOMUtilities.isValidName(s);
    }

    protected DOMError createDOMError(String type, short severity, String key, Object[] args, Node related, Exception e) {
        try {
            return new DocumentError(type, severity, this.getCurrentDocument().formatMessage(key, args), related, e);
        }
        catch (Exception ex) {
            return new DocumentError(type, severity, key, related, e);
        }
    }

    @Override
    public void setTextContent(String s) throws DOMException {
    }

    public void setXBLManager(XBLManager m) {
        boolean wasProcessing = this.xblManager.isProcessing();
        this.xblManager.stopProcessing();
        if (m == null) {
            m = new GenericXBLManager();
        }
        this.xblManager = m;
        if (wasProcessing) {
            this.xblManager.startProcessing();
        }
    }

    public XBLManager getXBLManager() {
        return this.xblManager;
    }

    @Override
    public XPathExpression createExpression(String expression, XPathNSResolver resolver) throws DOMException, XPathException {
        return new XPathExpr(expression, resolver);
    }

    @Override
    public XPathNSResolver createNSResolver(Node n) {
        return new XPathNodeNSResolver(n);
    }

    @Override
    public Object evaluate(String expression, Node contextNode, XPathNSResolver resolver, short type, Object result) throws XPathException, DOMException {
        XPathExpression xpath = this.createExpression(expression, resolver);
        return xpath.evaluate(contextNode, type, result);
    }

    public XPathException createXPathException(short type, String key, Object[] args) {
        try {
            return new XPathException(type, this.formatMessage(key, args));
        }
        catch (Exception e) {
            return new XPathException(type, key);
        }
    }

    @Override
    public Node getXblParentNode() {
        return this.xblManager.getXblParentNode(this);
    }

    @Override
    public NodeList getXblChildNodes() {
        return this.xblManager.getXblChildNodes(this);
    }

    @Override
    public NodeList getXblScopedChildNodes() {
        return this.xblManager.getXblScopedChildNodes(this);
    }

    @Override
    public Node getXblFirstChild() {
        return this.xblManager.getXblFirstChild(this);
    }

    @Override
    public Node getXblLastChild() {
        return this.xblManager.getXblLastChild(this);
    }

    @Override
    public Node getXblPreviousSibling() {
        return this.xblManager.getXblPreviousSibling(this);
    }

    @Override
    public Node getXblNextSibling() {
        return this.xblManager.getXblNextSibling(this);
    }

    @Override
    public Element getXblFirstElementChild() {
        return this.xblManager.getXblFirstElementChild(this);
    }

    @Override
    public Element getXblLastElementChild() {
        return this.xblManager.getXblLastElementChild(this);
    }

    @Override
    public Element getXblPreviousElementSibling() {
        return this.xblManager.getXblPreviousElementSibling(this);
    }

    @Override
    public Element getXblNextElementSibling() {
        return this.xblManager.getXblNextElementSibling(this);
    }

    @Override
    public Element getXblBoundElement() {
        return this.xblManager.getXblBoundElement(this);
    }

    @Override
    public Element getXblShadowTree() {
        return this.xblManager.getXblShadowTree(this);
    }

    @Override
    public NodeList getXblDefinitions() {
        return this.xblManager.getXblDefinitions(this);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(this.implementation.getClass().getName());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.localizableSupport = new LocalizableSupport(RESOURCES, this.getClass().getClassLoader());
        Class<?> c = Class.forName((String)s.readObject());
        try {
            Method m = c.getMethod("getDOMImplementation", null);
            this.implementation = (DOMImplementation)m.invoke(null, (Object[])null);
        }
        catch (Exception e) {
            if (DOMImplementation.class.isAssignableFrom(c)) {
                try {
                    this.implementation = (DOMImplementation)c.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (Exception exception) {}
            }
            throw new SecurityException("Trying to create object that is not a DOMImplementation.");
        }
    }

    protected static class XPathNodeNSResolver
    implements XPathNSResolver {
        protected Node contextNode;

        public XPathNodeNSResolver(Node n) {
            this.contextNode = n;
        }

        @Override
        public String lookupNamespaceURI(String prefix) {
            return this.contextNode.lookupNamespaceURI(prefix);
        }
    }

    protected class XPathExpr
    implements XPathExpression {
        protected javax.xml.xpath.XPathExpression xpath;
        protected XPathNSResolver resolver;
        protected NSPrefixResolver prefixResolver;

        public XPathExpr(String expr, XPathNSResolver res) throws DOMException, XPathException {
            this.resolver = res;
            this.prefixResolver = new NSPrefixResolver();
            try {
                XPath xPathAPI = XPathFactory.newInstance().newXPath();
                xPathAPI.setNamespaceContext(this.prefixResolver);
                this.xpath = xPathAPI.compile(expr);
            }
            catch (XPathExpressionException te) {
                throw AbstractDocument.this.createXPathException((short)51, "xpath.invalid.expression", new Object[]{expr, te.getMessage()});
            }
        }

        @Override
        public Object evaluate(Node contextNode, short type, Object res) throws XPathException, DOMException {
            if (contextNode.getNodeType() != 9 && contextNode.getOwnerDocument() != AbstractDocument.this || contextNode.getNodeType() == 9 && contextNode != AbstractDocument.this) {
                throw AbstractDocument.this.createDOMException((short)4, "node.from.wrong.document", new Object[]{(int)contextNode.getNodeType(), contextNode.getNodeName()});
            }
            if (type < 0 || type > 9) {
                throw AbstractDocument.this.createDOMException((short)9, "xpath.invalid.result.type", new Object[]{(int)type});
            }
            switch (contextNode.getNodeType()) {
                case 5: 
                case 6: 
                case 10: 
                case 11: 
                case 12: {
                    throw AbstractDocument.this.createDOMException((short)9, "xpath.invalid.context.node", new Object[]{(int)contextNode.getNodeType(), contextNode.getNodeName()});
                }
            }
            try {
                switch (type) {
                    case 8: 
                    case 9: {
                        return new Result((Node)this.xpath.evaluate(contextNode, XPathConstants.NODE), type);
                    }
                    case 3: {
                        return new Result((Boolean)this.xpath.evaluate(contextNode, XPathConstants.BOOLEAN));
                    }
                    case 1: {
                        return new Result((Double)this.xpath.evaluate(contextNode, XPathConstants.NUMBER));
                    }
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: {
                        return new Result((Node)this.xpath.evaluate(contextNode, XPathConstants.NODE), type);
                    }
                    case 2: {
                        return new Result((String)this.xpath.evaluate(contextNode, XPathConstants.STRING));
                    }
                }
            }
            catch (TransformerException | XPathExpressionException te) {
                throw AbstractDocument.this.createXPathException((short)52, "xpath.cannot.convert.result", new Object[]{(int)type, te.getMessage()});
            }
            return null;
        }

        protected class NSPrefixResolver
        implements NamespaceContext {
            protected NSPrefixResolver() {
            }

            @Override
            public String getNamespaceURI(String prefix) {
                if (XPathExpr.this.resolver == null) {
                    return null;
                }
                return XPathExpr.this.resolver.lookupNamespaceURI(prefix);
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        }

        public class Result
        implements XPathResult {
            protected short resultType;
            protected double numberValue;
            protected String stringValue;
            protected boolean booleanValue;
            protected Node singleNodeValue;
            protected NodeList iterator;
            protected int iteratorPosition;

            public Result(Node n, short type) {
                this.resultType = type;
                this.singleNodeValue = n;
            }

            public Result(boolean b) throws TransformerException {
                this.resultType = (short)3;
                this.booleanValue = b;
            }

            public Result(double d) throws TransformerException {
                this.resultType = 1;
                this.numberValue = d;
            }

            public Result(String s) {
                this.resultType = (short)2;
                this.stringValue = s;
            }

            public Result(NodeList nl, short type) {
                this.resultType = type;
                this.iterator = nl;
            }

            @Override
            public short getResultType() {
                return this.resultType;
            }

            @Override
            public boolean getBooleanValue() {
                if (this.resultType != 3) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[]{(int)this.resultType});
                }
                return this.booleanValue;
            }

            @Override
            public double getNumberValue() {
                if (this.resultType != 1) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[]{(int)this.resultType});
                }
                return this.numberValue;
            }

            @Override
            public String getStringValue() {
                if (this.resultType != 2) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[]{(int)this.resultType});
                }
                return this.stringValue;
            }

            @Override
            public Node getSingleNodeValue() {
                if (this.resultType != 8 && this.resultType != 9) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[]{(int)this.resultType});
                }
                return this.singleNodeValue;
            }

            @Override
            public boolean getInvalidIteratorState() {
                return false;
            }

            @Override
            public int getSnapshotLength() {
                if (this.resultType != 6 && this.resultType != 7) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[]{(int)this.resultType});
                }
                return this.iterator.getLength();
            }

            @Override
            public Node iterateNext() {
                if (this.resultType != 4 && this.resultType != 5) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[]{(int)this.resultType});
                }
                return this.iterator.item(this.iteratorPosition++);
            }

            @Override
            public Node snapshotItem(int i) {
                if (this.resultType != 6 && this.resultType != 7) {
                    throw AbstractDocument.this.createXPathException((short)52, "xpath.invalid.result.type", new Object[]{(int)this.resultType});
                }
                return this.iterator.item(i);
            }
        }
    }

    protected class DocumentConfiguration
    implements DOMConfiguration {
        protected String[] booleanParamNames = new String[]{"canonical-form", "cdata-sections", "check-character-normalization", "comments", "datatype-normalization", "element-content-whitespace", "entities", "infoset", "namespaces", "namespace-declarations", "normalize-characters", "split-cdata-sections", "validate", "validate-if-schema", "well-formed"};
        protected boolean[] booleanParamValues = new boolean[]{false, true, false, true, false, false, true, false, true, true, false, true, false, false, true};
        protected boolean[] booleanParamReadOnly = new boolean[]{true, false, true, false, true, false, false, false, false, false, true, false, true, true, false};
        protected Map booleanParamIndexes = new HashMap();
        protected Object errorHandler;
        protected ParameterNameList paramNameList;

        protected DocumentConfiguration() {
            for (int i = 0; i < this.booleanParamNames.length; ++i) {
                this.booleanParamIndexes.put(this.booleanParamNames[i], i);
            }
        }

        @Override
        public void setParameter(String name, Object value) {
            if ("error-handler".equals(name)) {
                if (value != null && !(value instanceof DOMErrorHandler)) {
                    throw AbstractDocument.this.createDOMException((short)17, "domconfig.param.type", new Object[]{name});
                }
                this.errorHandler = value;
                return;
            }
            Integer i = (Integer)this.booleanParamIndexes.get(name);
            if (i == null) {
                throw AbstractDocument.this.createDOMException((short)8, "domconfig.param.not.found", new Object[]{name});
            }
            if (value == null) {
                throw AbstractDocument.this.createDOMException((short)9, "domconfig.param.value", new Object[]{name});
            }
            if (!(value instanceof Boolean)) {
                throw AbstractDocument.this.createDOMException((short)17, "domconfig.param.type", new Object[]{name});
            }
            int index = i;
            boolean val = (Boolean)value;
            if (this.booleanParamReadOnly[index] && this.booleanParamValues[index] != val) {
                throw AbstractDocument.this.createDOMException((short)9, "domconfig.param.value", new Object[]{name});
            }
            this.booleanParamValues[index] = val;
            if (name.equals("infoset")) {
                this.setParameter("validate-if-schema", Boolean.FALSE);
                this.setParameter("entities", Boolean.FALSE);
                this.setParameter("datatype-normalization", Boolean.FALSE);
                this.setParameter("cdata-sections", Boolean.FALSE);
                this.setParameter("well-formed", Boolean.TRUE);
                this.setParameter("element-content-whitespace", Boolean.TRUE);
                this.setParameter("comments", Boolean.TRUE);
                this.setParameter("namespaces", Boolean.TRUE);
            }
        }

        @Override
        public Object getParameter(String name) {
            if ("error-handler".equals(name)) {
                return this.errorHandler;
            }
            Integer index = (Integer)this.booleanParamIndexes.get(name);
            if (index == null) {
                throw AbstractDocument.this.createDOMException((short)8, "domconfig.param.not.found", new Object[]{name});
            }
            return this.booleanParamValues[index] ? Boolean.TRUE : Boolean.FALSE;
        }

        public boolean getBooleanParameter(String name) {
            Boolean b = (Boolean)this.getParameter(name);
            return b;
        }

        @Override
        public boolean canSetParameter(String name, Object value) {
            if (name.equals("error-handler")) {
                return value == null || value instanceof DOMErrorHandler;
            }
            Integer i = (Integer)this.booleanParamIndexes.get(name);
            if (i == null || value == null || !(value instanceof Boolean)) {
                return false;
            }
            int index = i;
            boolean val = (Boolean)value;
            return !this.booleanParamReadOnly[index] || this.booleanParamValues[index] == val;
        }

        @Override
        public DOMStringList getParameterNames() {
            if (this.paramNameList == null) {
                this.paramNameList = new ParameterNameList();
            }
            return this.paramNameList;
        }

        protected class ParameterNameList
        implements DOMStringList {
            protected ParameterNameList() {
            }

            @Override
            public String item(int index) {
                if (index < 0) {
                    return null;
                }
                if (index < DocumentConfiguration.this.booleanParamNames.length) {
                    return DocumentConfiguration.this.booleanParamNames[index];
                }
                if (index == DocumentConfiguration.this.booleanParamNames.length) {
                    return "error-handler";
                }
                return null;
            }

            @Override
            public int getLength() {
                return DocumentConfiguration.this.booleanParamNames.length + 1;
            }

            @Override
            public boolean contains(String s) {
                if ("error-handler".equals(s)) {
                    return true;
                }
                for (String booleanParamName : DocumentConfiguration.this.booleanParamNames) {
                    if (!booleanParamName.equals(s)) continue;
                    return true;
                }
                return false;
            }
        }
    }

    protected static class DocumentError
    implements DOMError {
        protected String type;
        protected short severity;
        protected String message;
        protected Node relatedNode;
        protected Object relatedException;
        protected DOMLocator domLocator;

        public DocumentError(String type, short severity, String message, Node relatedNode, Exception relatedException) {
            this.type = type;
            this.severity = severity;
            this.message = message;
            this.relatedNode = relatedNode;
            this.relatedException = relatedException;
        }

        @Override
        public String getType() {
            return this.type;
        }

        @Override
        public short getSeverity() {
            return this.severity;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public Object getRelatedData() {
            return this.relatedNode;
        }

        @Override
        public Object getRelatedException() {
            return this.relatedException;
        }

        @Override
        public DOMLocator getLocation() {
            if (this.domLocator == null) {
                this.domLocator = new ErrorLocation(this.relatedNode);
            }
            return this.domLocator;
        }

        protected static class ErrorLocation
        implements DOMLocator {
            protected Node node;

            public ErrorLocation(Node n) {
                this.node = n;
            }

            @Override
            public int getLineNumber() {
                return -1;
            }

            @Override
            public int getColumnNumber() {
                return -1;
            }

            @Override
            public int getByteOffset() {
                return -1;
            }

            @Override
            public int getUtf16Offset() {
                return -1;
            }

            @Override
            public Node getRelatedNode() {
                return this.node;
            }

            @Override
            public String getUri() {
                AbstractDocument doc = (AbstractDocument)this.node.getOwnerDocument();
                return doc.getDocumentURI();
            }
        }
    }

    protected class IdSoftRef
    extends CleanerThread.SoftReferenceCleared {
        String id;
        List list;

        IdSoftRef(Object o, String id) {
            super(o);
            this.id = id;
        }

        IdSoftRef(Object o, String id, List list) {
            super(o);
            this.id = id;
            this.list = list;
        }

        public void setList(List list) {
            this.list = list;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void cleared() {
            if (AbstractDocument.this.elementsById == null) {
                return;
            }
            Map map = AbstractDocument.this.elementsById;
            synchronized (map) {
                if (this.list != null) {
                    this.list.remove((Object)this);
                } else {
                    Object o = AbstractDocument.this.elementsById.remove(this.id);
                    if (o != this) {
                        AbstractDocument.this.elementsById.put(this.id, o);
                    }
                }
            }
        }
    }
}

