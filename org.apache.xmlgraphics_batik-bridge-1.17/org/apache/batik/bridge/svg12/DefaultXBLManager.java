/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.BindableElement
 *  org.apache.batik.anim.dom.XBLEventSupport
 *  org.apache.batik.anim.dom.XBLOMContentElement
 *  org.apache.batik.anim.dom.XBLOMDefinitionElement
 *  org.apache.batik.anim.dom.XBLOMImportElement
 *  org.apache.batik.anim.dom.XBLOMShadowTreeElement
 *  org.apache.batik.anim.dom.XBLOMTemplateElement
 *  org.apache.batik.dom.AbstractAttrNS
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.xbl.NodeXBL
 *  org.apache.batik.dom.xbl.ShadowTreeEvent
 *  org.apache.batik.dom.xbl.XBLManager
 *  org.apache.batik.dom.xbl.XBLManagerData
 *  org.apache.batik.dom.xbl.XBLShadowTreeElement
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.apache.batik.util.XBLConstants
 */
package org.apache.batik.bridge.svg12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.event.EventListenerList;
import org.apache.batik.anim.dom.BindableElement;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.anim.dom.XBLOMContentElement;
import org.apache.batik.anim.dom.XBLOMDefinitionElement;
import org.apache.batik.anim.dom.XBLOMImportElement;
import org.apache.batik.anim.dom.XBLOMShadowTreeElement;
import org.apache.batik.anim.dom.XBLOMTemplateElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.svg12.BindingListener;
import org.apache.batik.bridge.svg12.ContentManager;
import org.apache.batik.bridge.svg12.ContentSelectionChangedListener;
import org.apache.batik.dom.AbstractAttrNS;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.xbl.NodeXBL;
import org.apache.batik.dom.xbl.ShadowTreeEvent;
import org.apache.batik.dom.xbl.XBLManager;
import org.apache.batik.dom.xbl.XBLManagerData;
import org.apache.batik.dom.xbl.XBLShadowTreeElement;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.util.XBLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

public class DefaultXBLManager
implements XBLManager,
XBLConstants {
    protected boolean isProcessing;
    protected Document document;
    protected BridgeContext ctx;
    protected DoublyIndexedTable definitionLists = new DoublyIndexedTable();
    protected DoublyIndexedTable definitions = new DoublyIndexedTable();
    protected Map contentManagers = new HashMap();
    protected Map imports = new HashMap();
    protected DocInsertedListener docInsertedListener = new DocInsertedListener();
    protected DocRemovedListener docRemovedListener = new DocRemovedListener();
    protected DocSubtreeListener docSubtreeListener = new DocSubtreeListener();
    protected ImportAttrListener importAttrListener = new ImportAttrListener();
    protected RefAttrListener refAttrListener = new RefAttrListener();
    protected EventListenerList bindingListenerList = new EventListenerList();
    protected EventListenerList contentSelectionChangedListenerList = new EventListenerList();

    public DefaultXBLManager(Document doc, BridgeContext ctx) {
        this.document = doc;
        this.ctx = ctx;
        ImportRecord ir = new ImportRecord(null, null);
        this.imports.put(null, ir);
    }

    public void startProcessing() {
        if (this.isProcessing) {
            return;
        }
        NodeList nl = this.document.getElementsByTagNameNS("http://www.w3.org/2004/xbl", "definition");
        XBLOMDefinitionElement[] defs = new XBLOMDefinitionElement[nl.getLength()];
        for (int i = 0; i < defs.length; ++i) {
            defs[i] = (XBLOMDefinitionElement)nl.item(i);
        }
        nl = this.document.getElementsByTagNameNS("http://www.w3.org/2004/xbl", "import");
        Element[] imports = new Element[nl.getLength()];
        for (int i = 0; i < imports.length; ++i) {
            imports[i] = (Element)nl.item(i);
        }
        AbstractDocument doc = (AbstractDocument)this.document;
        XBLEventSupport es = (XBLEventSupport)doc.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)this.docRemovedListener, true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)this.docInsertedListener, true);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)this.docSubtreeListener, true);
        for (XBLOMDefinitionElement xBLOMDefinitionElement : defs) {
            if (xBLOMDefinitionElement.getAttributeNS(null, "ref").length() != 0) {
                this.addDefinitionRef((Element)xBLOMDefinitionElement);
                continue;
            }
            String ns = xBLOMDefinitionElement.getElementNamespaceURI();
            String ln = xBLOMDefinitionElement.getElementLocalName();
            this.addDefinition(ns, ln, xBLOMDefinitionElement, null);
        }
        for (Element element : imports) {
            this.addImport(element);
        }
        this.isProcessing = true;
        this.bind(this.document.getDocumentElement());
    }

    public void stopProcessing() {
        if (!this.isProcessing) {
            return;
        }
        this.isProcessing = false;
        AbstractDocument doc = (AbstractDocument)this.document;
        XBLEventSupport es = (XBLEventSupport)doc.initializeEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)this.docRemovedListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)this.docInsertedListener, true);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)this.docSubtreeListener, true);
        int nSlots = this.imports.values().size();
        ImportRecord[] irs = new ImportRecord[nSlots];
        this.imports.values().toArray(irs);
        for (ImportRecord ir : irs) {
            if (ir.importElement.getLocalName().equals("definition")) {
                this.removeDefinitionRef(ir.importElement);
                continue;
            }
            this.removeImport(ir.importElement);
        }
        Object[] defRecs = this.definitions.getValuesArray();
        this.definitions.clear();
        for (Object defRec1 : defRecs) {
            DefinitionRecord defRec = (DefinitionRecord)defRec1;
            TreeSet defs = (TreeSet)this.definitionLists.get((Object)defRec.namespaceURI, (Object)defRec.localName);
            if (defs == null) continue;
            while (!defs.isEmpty()) {
                defRec = (DefinitionRecord)defs.first();
                defs.remove(defRec);
                this.removeDefinition(defRec);
            }
            this.definitionLists.put((Object)defRec.namespaceURI, (Object)defRec.localName, null);
        }
        this.definitionLists = new DoublyIndexedTable();
        this.contentManagers.clear();
    }

    public boolean isProcessing() {
        return this.isProcessing;
    }

    protected void addDefinitionRef(Element defRef) {
        String ref = defRef.getAttributeNS(null, "ref");
        Element e = this.ctx.getReferencedElement(defRef, ref);
        if (!"http://www.w3.org/2004/xbl".equals(e.getNamespaceURI()) || !"definition".equals(e.getLocalName())) {
            throw new BridgeException(this.ctx, defRef, "uri.badTarget", new Object[]{ref});
        }
        ImportRecord ir = new ImportRecord(defRef, e);
        this.imports.put(defRef, ir);
        NodeEventTarget et = (NodeEventTarget)defRef;
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)this.refAttrListener, false, null);
        XBLOMDefinitionElement d = (XBLOMDefinitionElement)defRef;
        String ns = d.getElementNamespaceURI();
        String ln = d.getElementLocalName();
        this.addDefinition(ns, ln, (XBLOMDefinitionElement)e, defRef);
    }

    protected void removeDefinitionRef(Element defRef) {
        ImportRecord ir = (ImportRecord)this.imports.get(defRef);
        NodeEventTarget et = (NodeEventTarget)defRef;
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)this.refAttrListener, false);
        DefinitionRecord defRec = (DefinitionRecord)this.definitions.get((Object)ir.node, (Object)defRef);
        this.removeDefinition(defRec);
        this.imports.remove(defRef);
    }

    protected void addImport(Element imp) {
        String bindings = imp.getAttributeNS(null, "bindings");
        Node n = this.ctx.getReferencedNode(imp, bindings);
        if (!(n.getNodeType() != 1 || "http://www.w3.org/2004/xbl".equals(n.getNamespaceURI()) && "xbl".equals(n.getLocalName()))) {
            throw new BridgeException(this.ctx, imp, "uri.badTarget", new Object[]{n});
        }
        ImportRecord ir = new ImportRecord(imp, n);
        this.imports.put(imp, ir);
        NodeEventTarget et = (NodeEventTarget)imp;
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)this.importAttrListener, false, null);
        et = (NodeEventTarget)n;
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)ir.importInsertedListener, false, null);
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)ir.importRemovedListener, false, null);
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)ir.importSubtreeListener, false, null);
        this.addImportedDefinitions(imp, n);
    }

    protected void addImportedDefinitions(Element imp, Node n) {
        if (n instanceof XBLOMDefinitionElement) {
            XBLOMDefinitionElement def = (XBLOMDefinitionElement)n;
            String ns = def.getElementNamespaceURI();
            String ln = def.getElementLocalName();
            this.addDefinition(ns, ln, def, imp);
        } else {
            for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                this.addImportedDefinitions(imp, n);
            }
        }
    }

    protected void removeImport(Element imp) {
        Object[] defRecs;
        ImportRecord ir = (ImportRecord)this.imports.get(imp);
        NodeEventTarget et = (NodeEventTarget)ir.node;
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)ir.importInsertedListener, false);
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)ir.importRemovedListener, false);
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)ir.importSubtreeListener, false);
        et = (NodeEventTarget)imp;
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)this.importAttrListener, false);
        for (Object defRec1 : defRecs = this.definitions.getValuesArray()) {
            DefinitionRecord defRec = (DefinitionRecord)defRec1;
            if (defRec.importElement != imp) continue;
            this.removeDefinition(defRec);
        }
        this.imports.remove(imp);
    }

    protected void addDefinition(String namespaceURI, String localName, XBLOMDefinitionElement def, Element imp) {
        ImportRecord ir = (ImportRecord)this.imports.get(imp);
        DefinitionRecord oldDefRec = null;
        TreeSet<DefinitionRecord> defs = (TreeSet<DefinitionRecord>)this.definitionLists.get((Object)namespaceURI, (Object)localName);
        if (defs == null) {
            defs = new TreeSet<DefinitionRecord>();
            this.definitionLists.put((Object)namespaceURI, (Object)localName, defs);
        } else if (defs.size() > 0) {
            oldDefRec = (DefinitionRecord)defs.first();
        }
        XBLOMTemplateElement template = null;
        for (Node n = def.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (!(n instanceof XBLOMTemplateElement)) continue;
            template = (XBLOMTemplateElement)n;
            break;
        }
        DefinitionRecord defRec = new DefinitionRecord(namespaceURI, localName, def, template, imp);
        defs.add(defRec);
        this.definitions.put((Object)def, (Object)imp, (Object)defRec);
        this.addDefinitionElementListeners(def, ir);
        if (defs.first() != defRec) {
            return;
        }
        if (oldDefRec != null) {
            XBLOMDefinitionElement oldDef = oldDefRec.definition;
            XBLOMTemplateElement oldTemplate = oldDefRec.template;
            if (oldTemplate != null) {
                this.removeTemplateElementListeners(oldTemplate, ir);
            }
            this.removeDefinitionElementListeners(oldDef, ir);
        }
        if (template != null) {
            this.addTemplateElementListeners(template, ir);
        }
        if (this.isProcessing) {
            this.rebind(namespaceURI, localName, this.document.getDocumentElement());
        }
    }

    protected void addDefinitionElementListeners(XBLOMDefinitionElement def, ImportRecord ir) {
        XBLEventSupport es = (XBLEventSupport)def.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)ir.defAttrListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)ir.defNodeInsertedListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)ir.defNodeRemovedListener, false);
    }

    protected void addTemplateElementListeners(XBLOMTemplateElement template, ImportRecord ir) {
        XBLEventSupport es = (XBLEventSupport)template.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)ir.templateMutationListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)ir.templateMutationListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)ir.templateMutationListener, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", (EventListener)ir.templateMutationListener, false);
    }

    protected void removeDefinition(DefinitionRecord defRec) {
        TreeSet defs = (TreeSet)this.definitionLists.get((Object)defRec.namespaceURI, (Object)defRec.localName);
        if (defs == null) {
            return;
        }
        Element imp = defRec.importElement;
        ImportRecord ir = (ImportRecord)this.imports.get(imp);
        DefinitionRecord activeDefRec = (DefinitionRecord)defs.first();
        defs.remove(defRec);
        this.definitions.remove((Object)defRec.definition, (Object)imp);
        this.removeDefinitionElementListeners(defRec.definition, ir);
        if (defRec != activeDefRec) {
            return;
        }
        if (defRec.template != null) {
            this.removeTemplateElementListeners(defRec.template, ir);
        }
        this.rebind(defRec.namespaceURI, defRec.localName, this.document.getDocumentElement());
    }

    protected void removeDefinitionElementListeners(XBLOMDefinitionElement def, ImportRecord ir) {
        XBLEventSupport es = (XBLEventSupport)def.initializeEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)ir.defAttrListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)ir.defNodeInsertedListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)ir.defNodeRemovedListener, false);
    }

    protected void removeTemplateElementListeners(XBLOMTemplateElement template, ImportRecord ir) {
        XBLEventSupport es = (XBLEventSupport)template.initializeEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", (EventListener)ir.templateMutationListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", (EventListener)ir.templateMutationListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)ir.templateMutationListener, false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", (EventListener)ir.templateMutationListener, false);
    }

    protected DefinitionRecord getActiveDefinition(String namespaceURI, String localName) {
        TreeSet defs = (TreeSet)this.definitionLists.get((Object)namespaceURI, (Object)localName);
        if (defs == null || defs.size() == 0) {
            return null;
        }
        return (DefinitionRecord)defs.first();
    }

    protected void unbind(Element e) {
        if (e instanceof BindableElement) {
            this.setActiveDefinition((BindableElement)e, null);
        } else {
            NodeList nl = this.getXblScopedChildNodes(e);
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                if (n.getNodeType() != 1) continue;
                this.unbind((Element)n);
            }
        }
    }

    protected void bind(Element e) {
        XBLManager xm;
        AbstractDocument doc = (AbstractDocument)e.getOwnerDocument();
        if (doc != this.document && (xm = doc.getXBLManager()) instanceof DefaultXBLManager) {
            ((DefaultXBLManager)xm).bind(e);
            return;
        }
        if (e instanceof BindableElement) {
            DefinitionRecord defRec = this.getActiveDefinition(e.getNamespaceURI(), e.getLocalName());
            this.setActiveDefinition((BindableElement)e, defRec);
        } else {
            NodeList nl = this.getXblScopedChildNodes(e);
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                if (n.getNodeType() != 1) continue;
                this.bind((Element)n);
            }
        }
    }

    protected void rebind(String namespaceURI, String localName, Element e) {
        XBLManager xm;
        AbstractDocument doc = (AbstractDocument)e.getOwnerDocument();
        if (doc != this.document && (xm = doc.getXBLManager()) instanceof DefaultXBLManager) {
            ((DefaultXBLManager)xm).rebind(namespaceURI, localName, e);
            return;
        }
        if (e instanceof BindableElement && namespaceURI.equals(e.getNamespaceURI()) && localName.equals(e.getLocalName())) {
            DefinitionRecord defRec = this.getActiveDefinition(e.getNamespaceURI(), e.getLocalName());
            this.setActiveDefinition((BindableElement)e, defRec);
        } else {
            NodeList nl = this.getXblScopedChildNodes(e);
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                if (n.getNodeType() != 1) continue;
                this.rebind(namespaceURI, localName, (Element)n);
            }
        }
    }

    protected void setActiveDefinition(BindableElement elt, DefinitionRecord defRec) {
        XBLRecord rec = this.getRecord((Node)elt);
        XBLOMDefinitionElement xBLOMDefinitionElement = rec.definitionElement = defRec == null ? null : defRec.definition;
        if (defRec != null && defRec.definition != null && defRec.template != null) {
            this.setXblShadowTree(elt, this.cloneTemplate(defRec.template));
        } else {
            this.setXblShadowTree(elt, null);
        }
    }

    protected void setXblShadowTree(BindableElement elt, XBLOMShadowTreeElement newShadow) {
        XBLOMShadowTreeElement oldShadow = (XBLOMShadowTreeElement)this.getXblShadowTree((Node)elt);
        if (oldShadow != null) {
            this.fireShadowTreeEvent(elt, "unbinding", (XBLShadowTreeElement)oldShadow);
            ContentManager cm = this.getContentManager((Node)oldShadow);
            if (cm != null) {
                cm.dispose();
            }
            elt.setShadowTree(null);
            XBLRecord rec = this.getRecord((Node)oldShadow);
            rec.boundElement = null;
            oldShadow.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)this.docSubtreeListener, false);
        }
        if (newShadow != null) {
            newShadow.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)this.docSubtreeListener, false, null);
            this.fireShadowTreeEvent(elt, "prebind", (XBLShadowTreeElement)newShadow);
            elt.setShadowTree(newShadow);
            XBLRecord rec = this.getRecord((Node)newShadow);
            rec.boundElement = elt;
            AbstractDocument doc = (AbstractDocument)elt.getOwnerDocument();
            XBLManager xm = doc.getXBLManager();
            ContentManager cm = new ContentManager(newShadow, xm);
            this.setContentManager((Element)newShadow, cm);
        }
        this.invalidateChildNodes((Node)elt);
        if (newShadow != null) {
            NodeList nl = this.getXblScopedChildNodes((Node)elt);
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                if (n.getNodeType() != 1) continue;
                this.bind((Element)n);
            }
            this.dispatchBindingChangedEvent((Element)elt, (Element)newShadow);
            this.fireShadowTreeEvent(elt, "bound", (XBLShadowTreeElement)newShadow);
        } else {
            this.dispatchBindingChangedEvent((Element)elt, (Element)newShadow);
        }
    }

    protected void fireShadowTreeEvent(BindableElement elt, String type, XBLShadowTreeElement e) {
        DocumentEvent de = (DocumentEvent)((Object)elt.getOwnerDocument());
        ShadowTreeEvent evt = (ShadowTreeEvent)de.createEvent("ShadowTreeEvent");
        evt.initShadowTreeEventNS("http://www.w3.org/2004/xbl", type, true, false, e);
        elt.dispatchEvent((Event)evt);
    }

    protected XBLOMShadowTreeElement cloneTemplate(XBLOMTemplateElement template) {
        XBLOMShadowTreeElement clone = (XBLOMShadowTreeElement)template.getOwnerDocument().createElementNS("http://www.w3.org/2004/xbl", "shadowTree");
        NamedNodeMap attrs = template.getAttributes();
        for (int i = 0; i < attrs.getLength(); ++i) {
            Attr attr = (Attr)attrs.item(i);
            if (attr instanceof AbstractAttrNS) {
                clone.setAttributeNodeNS(attr);
                continue;
            }
            clone.setAttributeNode(attr);
        }
        for (Node n = template.getFirstChild(); n != null; n = n.getNextSibling()) {
            clone.appendChild(n.cloneNode(true));
        }
        return clone;
    }

    public Node getXblParentNode(Node n) {
        Node parent;
        XBLOMContentElement contentElement = this.getXblContentElement(n);
        Node node = parent = contentElement == null ? n.getParentNode() : contentElement.getParentNode();
        if (parent instanceof XBLOMContentElement) {
            parent = parent.getParentNode();
        }
        if (parent instanceof XBLOMShadowTreeElement) {
            parent = this.getXblBoundElement(parent);
        }
        return parent;
    }

    public NodeList getXblChildNodes(Node n) {
        XBLRecord rec = this.getRecord(n);
        if (rec.childNodes == null) {
            rec.childNodes = new XblChildNodes(rec);
        }
        return rec.childNodes;
    }

    public NodeList getXblScopedChildNodes(Node n) {
        XBLRecord rec = this.getRecord(n);
        if (rec.scopedChildNodes == null) {
            rec.scopedChildNodes = new XblScopedChildNodes(rec);
        }
        return rec.scopedChildNodes;
    }

    public Node getXblFirstChild(Node n) {
        NodeList nl = this.getXblChildNodes(n);
        return nl.item(0);
    }

    public Node getXblLastChild(Node n) {
        NodeList nl = this.getXblChildNodes(n);
        return nl.item(nl.getLength() - 1);
    }

    public Node getXblPreviousSibling(Node n) {
        Node p = this.getXblParentNode(n);
        if (p == null || this.getRecord((Node)p).childNodes == null) {
            return n.getPreviousSibling();
        }
        XBLRecord rec = this.getRecord(n);
        if (!rec.linksValid) {
            this.updateLinks(n);
        }
        return rec.previousSibling;
    }

    public Node getXblNextSibling(Node n) {
        Node p = this.getXblParentNode(n);
        if (p == null || this.getRecord((Node)p).childNodes == null) {
            return n.getNextSibling();
        }
        XBLRecord rec = this.getRecord(n);
        if (!rec.linksValid) {
            this.updateLinks(n);
        }
        return rec.nextSibling;
    }

    public Element getXblFirstElementChild(Node n) {
        n = this.getXblFirstChild(n);
        while (n != null && n.getNodeType() != 1) {
            n = this.getXblNextSibling(n);
        }
        return (Element)n;
    }

    public Element getXblLastElementChild(Node n) {
        n = this.getXblLastChild(n);
        while (n != null && n.getNodeType() != 1) {
            n = this.getXblPreviousSibling(n);
        }
        return (Element)n;
    }

    public Element getXblPreviousElementSibling(Node n) {
        while ((n = this.getXblPreviousSibling(n)) != null && n.getNodeType() != 1) {
        }
        return (Element)n;
    }

    public Element getXblNextElementSibling(Node n) {
        while ((n = this.getXblNextSibling(n)) != null && n.getNodeType() != 1) {
        }
        return (Element)n;
    }

    public Element getXblBoundElement(Node n) {
        while (n != null && !(n instanceof XBLShadowTreeElement)) {
            XBLOMContentElement content = this.getXblContentElement(n);
            if (content != null) {
                n = content;
            }
            n = n.getParentNode();
        }
        if (n == null) {
            return null;
        }
        return this.getRecord((Node)n).boundElement;
    }

    public Element getXblShadowTree(Node n) {
        if (n instanceof BindableElement) {
            BindableElement elt = (BindableElement)n;
            return elt.getShadowTree();
        }
        return null;
    }

    public NodeList getXblDefinitions(Node n) {
        final String namespaceURI = n.getNamespaceURI();
        final String localName = n.getLocalName();
        return new NodeList(){

            @Override
            public Node item(int i) {
                TreeSet defs = (TreeSet)DefaultXBLManager.this.definitionLists.get((Object)namespaceURI, (Object)localName);
                if (defs != null && defs.size() != 0 && i == 0) {
                    DefinitionRecord defRec = (DefinitionRecord)defs.first();
                    return defRec.definition;
                }
                return null;
            }

            @Override
            public int getLength() {
                TreeSet defs = (TreeSet)DefaultXBLManager.this.definitionLists.get((Object)namespaceURI, (Object)localName);
                return defs != null && defs.size() != 0 ? 1 : 0;
            }
        };
    }

    protected XBLRecord getRecord(Node n) {
        XBLManagerData xmd = (XBLManagerData)n;
        XBLRecord rec = (XBLRecord)xmd.getManagerData();
        if (rec == null) {
            rec = new XBLRecord();
            rec.node = n;
            xmd.setManagerData((Object)rec);
        }
        return rec;
    }

    protected void updateLinks(Node n) {
        NodeList xcn;
        XBLRecord rec = this.getRecord(n);
        rec.previousSibling = null;
        rec.nextSibling = null;
        rec.linksValid = true;
        Node p = this.getXblParentNode(n);
        if (p != null && (xcn = this.getXblChildNodes(p)) instanceof XblChildNodes) {
            ((XblChildNodes)xcn).update();
        }
    }

    public XBLOMContentElement getXblContentElement(Node n) {
        return this.getRecord((Node)n).contentElement;
    }

    public static int computeBubbleLimit(Node from, Node to) {
        ArrayList<Node> fromList = new ArrayList<Node>(10);
        ArrayList<Node> toList = new ArrayList<Node>(10);
        while (from != null) {
            fromList.add(from);
            from = ((NodeXBL)from).getXblParentNode();
        }
        while (to != null) {
            toList.add(to);
            to = ((NodeXBL)to).getXblParentNode();
        }
        int fromSize = fromList.size();
        int toSize = toList.size();
        for (int i = 0; i < fromSize && i < toSize; ++i) {
            Node n2;
            Node n1 = (Node)fromList.get(fromSize - i - 1);
            if (n1 == (n2 = (Node)toList.get(toSize - i - 1))) continue;
            Element prevBoundElement = ((NodeXBL)n1).getXblBoundElement();
            while (i > 0 && prevBoundElement != fromList.get(fromSize - i - 1)) {
                --i;
            }
            return fromSize - i - 1;
        }
        return 1;
    }

    public ContentManager getContentManager(Node n) {
        Element s;
        Element b = this.getXblBoundElement(n);
        if (b != null && (s = this.getXblShadowTree(b)) != null) {
            ContentManager cm;
            Document doc = b.getOwnerDocument();
            if (doc != this.document) {
                DefaultXBLManager xm = (DefaultXBLManager)((AbstractDocument)doc).getXBLManager();
                cm = (ContentManager)xm.contentManagers.get(s);
            } else {
                cm = (ContentManager)this.contentManagers.get(s);
            }
            return cm;
        }
        return null;
    }

    void setContentManager(Element shadow, ContentManager cm) {
        if (cm == null) {
            this.contentManagers.remove(shadow);
        } else {
            this.contentManagers.put(shadow, cm);
        }
    }

    public void invalidateChildNodes(Node n) {
        XBLRecord rec = this.getRecord(n);
        if (rec.childNodes != null) {
            rec.childNodes.invalidate();
        }
        if (rec.scopedChildNodes != null) {
            rec.scopedChildNodes.invalidate();
        }
    }

    public void addContentSelectionChangedListener(ContentSelectionChangedListener l) {
        this.contentSelectionChangedListenerList.add(ContentSelectionChangedListener.class, l);
    }

    public void removeContentSelectionChangedListener(ContentSelectionChangedListener l) {
        this.contentSelectionChangedListenerList.remove(ContentSelectionChangedListener.class, l);
    }

    protected Object[] getContentSelectionChangedListeners() {
        return this.contentSelectionChangedListenerList.getListenerList();
    }

    void shadowTreeSelectedContentChanged(Set deselected, Set selected) {
        for (Node n : deselected) {
            if (n.getNodeType() != 1) continue;
            this.unbind((Element)n);
        }
        for (Node n : selected) {
            if (n.getNodeType() != 1) continue;
            this.bind((Element)n);
        }
    }

    public void addBindingListener(BindingListener l) {
        this.bindingListenerList.add(BindingListener.class, l);
    }

    public void removeBindingListener(BindingListener l) {
        this.bindingListenerList.remove(BindingListener.class, l);
    }

    protected void dispatchBindingChangedEvent(Element bindableElement, Element shadowTree) {
        Object[] ls = this.bindingListenerList.getListenerList();
        for (int i = ls.length - 2; i >= 0; i -= 2) {
            BindingListener l = (BindingListener)ls[i + 1];
            l.bindingChanged(bindableElement, shadowTree);
        }
    }

    protected boolean isActiveDefinition(XBLOMDefinitionElement def, Element imp) {
        DefinitionRecord defRec = (DefinitionRecord)this.definitions.get((Object)def, (Object)imp);
        if (defRec == null) {
            return false;
        }
        return defRec == this.getActiveDefinition(defRec.namespaceURI, defRec.localName);
    }

    protected class XblScopedChildNodes
    extends XblChildNodes {
        public XblScopedChildNodes(XBLRecord rec) {
            super(rec);
        }

        @Override
        protected void update() {
            Node n;
            this.size = 0;
            Element shadowTree = DefaultXBLManager.this.getXblShadowTree(this.record.node);
            Node node = n = shadowTree == null ? this.record.node.getFirstChild() : shadowTree.getFirstChild();
            while (n != null) {
                this.collectXblScopedChildNodes(n);
                n = n.getNextSibling();
            }
        }

        protected void collectXblScopedChildNodes(Node n) {
            boolean isChild = false;
            if (n.getNodeType() == 1) {
                ContentManager cm;
                if (!n.getNamespaceURI().equals("http://www.w3.org/2004/xbl")) {
                    isChild = true;
                } else if (n instanceof XBLOMContentElement && (cm = DefaultXBLManager.this.getContentManager(n)) != null) {
                    NodeList selected = cm.getSelectedContent((XBLOMContentElement)n);
                    for (int i = 0; i < selected.getLength(); ++i) {
                        this.collectXblScopedChildNodes(selected.item(i));
                    }
                }
            } else {
                isChild = true;
            }
            if (isChild) {
                this.nodes.add(n);
                ++this.size;
            }
        }
    }

    protected class XblChildNodes
    implements NodeList {
        protected XBLRecord record;
        protected List nodes;
        protected int size;

        public XblChildNodes(XBLRecord rec) {
            this.record = rec;
            this.nodes = new ArrayList();
            this.size = -1;
        }

        protected void update() {
            Node m;
            this.size = 0;
            Element shadowTree = DefaultXBLManager.this.getXblShadowTree(this.record.node);
            Node last = null;
            Node node = m = shadowTree == null ? this.record.node.getFirstChild() : shadowTree.getFirstChild();
            while (m != null) {
                last = this.collectXblChildNodes(m, last);
                m = m.getNextSibling();
            }
            if (last != null) {
                XBLRecord rec = DefaultXBLManager.this.getRecord(last);
                rec.nextSibling = null;
                rec.linksValid = true;
            }
        }

        protected Node collectXblChildNodes(Node n, Node prev) {
            boolean isChild = false;
            if (n.getNodeType() == 1) {
                ContentManager cm;
                if (!"http://www.w3.org/2004/xbl".equals(n.getNamespaceURI())) {
                    isChild = true;
                } else if (n instanceof XBLOMContentElement && (cm = DefaultXBLManager.this.getContentManager(n)) != null) {
                    NodeList selected = cm.getSelectedContent((XBLOMContentElement)n);
                    for (int i = 0; i < selected.getLength(); ++i) {
                        prev = this.collectXblChildNodes(selected.item(i), prev);
                    }
                }
            } else {
                isChild = true;
            }
            if (isChild) {
                XBLRecord rec;
                this.nodes.add(n);
                ++this.size;
                if (prev != null) {
                    rec = DefaultXBLManager.this.getRecord(prev);
                    rec.nextSibling = n;
                    rec.linksValid = true;
                }
                rec = DefaultXBLManager.this.getRecord(n);
                rec.previousSibling = prev;
                rec.linksValid = true;
                prev = n;
            }
            return prev;
        }

        public void invalidate() {
            for (int i = 0; i < this.size; ++i) {
                XBLRecord rec = DefaultXBLManager.this.getRecord((Node)this.nodes.get(i));
                rec.previousSibling = null;
                rec.nextSibling = null;
                rec.linksValid = false;
            }
            this.nodes.clear();
            this.size = -1;
        }

        public Node getFirstNode() {
            if (this.size == -1) {
                this.update();
            }
            return this.size == 0 ? null : (Node)this.nodes.get(0);
        }

        public Node getLastNode() {
            if (this.size == -1) {
                this.update();
            }
            return this.size == 0 ? null : (Node)this.nodes.get(this.nodes.size() - 1);
        }

        @Override
        public Node item(int index) {
            if (this.size == -1) {
                this.update();
            }
            if (index < 0 || index >= this.size) {
                return null;
            }
            return (Node)this.nodes.get(index);
        }

        @Override
        public int getLength() {
            if (this.size == -1) {
                this.update();
            }
            return this.size;
        }
    }

    protected class XBLRecord {
        public Node node;
        public XblChildNodes childNodes;
        public XblScopedChildNodes scopedChildNodes;
        public XBLOMContentElement contentElement;
        public XBLOMDefinitionElement definitionElement;
        public BindableElement boundElement;
        public boolean linksValid;
        public Node nextSibling;
        public Node previousSibling;

        protected XBLRecord() {
        }
    }

    protected class RefAttrListener
    implements EventListener {
        protected RefAttrListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            EventTarget target = evt.getTarget();
            if (target != evt.getCurrentTarget()) {
                return;
            }
            MutationEvent mevt = (MutationEvent)evt;
            if (mevt.getAttrName().equals("ref")) {
                Element defRef = (Element)((Object)target);
                DefaultXBLManager.this.removeDefinitionRef(defRef);
                if (mevt.getNewValue().length() == 0) {
                    XBLOMDefinitionElement def = (XBLOMDefinitionElement)defRef;
                    String ns = def.getElementNamespaceURI();
                    String ln = def.getElementLocalName();
                    DefaultXBLManager.this.addDefinition(ns, ln, (XBLOMDefinitionElement)defRef, null);
                } else {
                    DefaultXBLManager.this.addDefinitionRef(defRef);
                }
            }
        }
    }

    protected class ImportAttrListener
    implements EventListener {
        protected ImportAttrListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            EventTarget target = evt.getTarget();
            if (target != evt.getCurrentTarget()) {
                return;
            }
            MutationEvent mevt = (MutationEvent)evt;
            if (mevt.getAttrName().equals("bindings")) {
                Element imp = (Element)((Object)target);
                DefaultXBLManager.this.removeImport(imp);
                DefaultXBLManager.this.addImport(imp);
            }
        }
    }

    protected class DefNodeRemovedListener
    implements EventListener {
        protected Element importElement;

        public DefNodeRemovedListener(Element imp) {
            this.importElement = imp;
        }

        @Override
        public void handleEvent(Event evt) {
            MutationEvent mevt = (MutationEvent)evt;
            Node parent = mevt.getRelatedNode();
            if (!(parent instanceof XBLOMDefinitionElement)) {
                return;
            }
            EventTarget target = evt.getTarget();
            if (!(target instanceof XBLOMTemplateElement)) {
                return;
            }
            XBLOMTemplateElement template = (XBLOMTemplateElement)target;
            DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get((Object)parent, (Object)this.importElement);
            if (defRec == null || defRec.template != template) {
                return;
            }
            ImportRecord ir = (ImportRecord)DefaultXBLManager.this.imports.get(this.importElement);
            DefaultXBLManager.this.removeTemplateElementListeners(template, ir);
            defRec.template = null;
            for (Node n = template.getNextSibling(); n != null; n = n.getNextSibling()) {
                if (!(n instanceof XBLOMTemplateElement)) continue;
                defRec.template = (XBLOMTemplateElement)n;
                break;
            }
            DefaultXBLManager.this.addTemplateElementListeners(defRec.template, ir);
            DefaultXBLManager.this.rebind(defRec.namespaceURI, defRec.localName, DefaultXBLManager.this.document.getDocumentElement());
        }
    }

    protected class DefNodeInsertedListener
    implements EventListener {
        protected Element importElement;

        public DefNodeInsertedListener(Element imp) {
            this.importElement = imp;
        }

        @Override
        public void handleEvent(Event evt) {
            MutationEvent mevt = (MutationEvent)evt;
            Node parent = mevt.getRelatedNode();
            if (!(parent instanceof XBLOMDefinitionElement)) {
                return;
            }
            EventTarget target = evt.getTarget();
            if (!(target instanceof XBLOMTemplateElement)) {
                return;
            }
            XBLOMTemplateElement template = (XBLOMTemplateElement)target;
            DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get((Object)parent, (Object)this.importElement);
            if (defRec == null) {
                return;
            }
            ImportRecord ir = (ImportRecord)DefaultXBLManager.this.imports.get(this.importElement);
            if (defRec.template != null) {
                for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (n == template) {
                        DefaultXBLManager.this.removeTemplateElementListeners(defRec.template, ir);
                        defRec.template = template;
                        break;
                    }
                    if (n != defRec.template) continue;
                    return;
                }
            } else {
                defRec.template = template;
            }
            DefaultXBLManager.this.addTemplateElementListeners(template, ir);
            DefaultXBLManager.this.rebind(defRec.namespaceURI, defRec.localName, DefaultXBLManager.this.document.getDocumentElement());
        }
    }

    protected class DefAttrListener
    implements EventListener {
        protected Element importElement;

        public DefAttrListener(Element imp) {
            this.importElement = imp;
        }

        @Override
        public void handleEvent(Event evt) {
            EventTarget target = evt.getTarget();
            if (!(target instanceof XBLOMDefinitionElement)) {
                return;
            }
            XBLOMDefinitionElement def = (XBLOMDefinitionElement)target;
            if (!DefaultXBLManager.this.isActiveDefinition(def, this.importElement)) {
                return;
            }
            MutationEvent mevt = (MutationEvent)evt;
            String attrName = mevt.getAttrName();
            if (attrName.equals("element")) {
                DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get((Object)def, (Object)this.importElement);
                DefaultXBLManager.this.removeDefinition(defRec);
                DefaultXBLManager.this.addDefinition(def.getElementNamespaceURI(), def.getElementLocalName(), def, this.importElement);
            } else if (attrName.equals("ref") && mevt.getNewValue().length() != 0) {
                DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get((Object)def, (Object)this.importElement);
                DefaultXBLManager.this.removeDefinition(defRec);
                DefaultXBLManager.this.addDefinitionRef((Element)def);
            }
        }
    }

    protected class TemplateMutationListener
    implements EventListener {
        protected Element importElement;

        public TemplateMutationListener(Element imp) {
            this.importElement = imp;
        }

        @Override
        public void handleEvent(Event evt) {
            Node n;
            for (n = (Node)((Object)evt.getTarget()); n != null && !(n instanceof XBLOMDefinitionElement); n = n.getParentNode()) {
            }
            DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get((Object)n, (Object)this.importElement);
            if (defRec == null) {
                return;
            }
            DefaultXBLManager.this.rebind(defRec.namespaceURI, defRec.localName, DefaultXBLManager.this.document.getDocumentElement());
        }
    }

    protected class DocSubtreeListener
    implements EventListener {
        protected DocSubtreeListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            Object[] defs = DefaultXBLManager.this.docRemovedListener.defsToBeRemoved.toArray();
            DefaultXBLManager.this.docRemovedListener.defsToBeRemoved.clear();
            for (Object def1 : defs) {
                XBLOMDefinitionElement def = (XBLOMDefinitionElement)def1;
                if (def.getAttributeNS(null, "ref").length() == 0) {
                    DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get((Object)def, null);
                    DefaultXBLManager.this.removeDefinition(defRec);
                    continue;
                }
                DefaultXBLManager.this.removeDefinitionRef((Element)def);
            }
            Object[] imps = DefaultXBLManager.this.docRemovedListener.importsToBeRemoved.toArray();
            DefaultXBLManager.this.docRemovedListener.importsToBeRemoved.clear();
            for (Object imp : imps) {
                DefaultXBLManager.this.removeImport((Element)imp);
            }
            Object[] nodes = DefaultXBLManager.this.docRemovedListener.nodesToBeInvalidated.toArray();
            DefaultXBLManager.this.docRemovedListener.nodesToBeInvalidated.clear();
            for (Object node : nodes) {
                DefaultXBLManager.this.invalidateChildNodes((Node)node);
            }
        }
    }

    protected class DocRemovedListener
    implements EventListener {
        protected LinkedList defsToBeRemoved = new LinkedList();
        protected LinkedList importsToBeRemoved = new LinkedList();
        protected LinkedList nodesToBeInvalidated = new LinkedList();

        protected DocRemovedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            Node parent;
            EventTarget target = evt.getTarget();
            if (target instanceof XBLOMDefinitionElement) {
                if (DefaultXBLManager.this.getXblBoundElement((Node)((Object)target)) == null) {
                    this.defsToBeRemoved.add(target);
                }
            } else if (target instanceof XBLOMImportElement && DefaultXBLManager.this.getXblBoundElement((Node)((Object)target)) == null) {
                this.importsToBeRemoved.add(target);
            }
            if ((parent = DefaultXBLManager.this.getXblParentNode((Node)((Object)target))) != null) {
                this.nodesToBeInvalidated.add(parent);
            }
        }
    }

    protected class DocInsertedListener
    implements EventListener {
        protected DocInsertedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            EventTarget target = evt.getTarget();
            if (target instanceof XBLOMDefinitionElement) {
                if (DefaultXBLManager.this.getXblBoundElement((Node)((Object)target)) == null) {
                    XBLOMDefinitionElement def = (XBLOMDefinitionElement)target;
                    if (def.getAttributeNS(null, "ref").length() == 0) {
                        DefaultXBLManager.this.addDefinition(def.getElementNamespaceURI(), def.getElementLocalName(), def, null);
                    } else {
                        DefaultXBLManager.this.addDefinitionRef((Element)def);
                    }
                }
            } else if (target instanceof XBLOMImportElement) {
                if (DefaultXBLManager.this.getXblBoundElement((Node)((Object)target)) == null) {
                    DefaultXBLManager.this.addImport((Element)((Object)target));
                }
            } else {
                target = (evt = XBLEventSupport.getUltimateOriginalEvent((Event)evt)).getTarget();
                Node parent = DefaultXBLManager.this.getXblParentNode((Node)((Object)target));
                if (parent != null) {
                    DefaultXBLManager.this.invalidateChildNodes(parent);
                }
                if (target instanceof BindableElement) {
                    for (Node n = ((Node)((Object)target)).getParentNode(); n != null; n = n.getParentNode()) {
                        if (!(n instanceof BindableElement) || DefaultXBLManager.this.getRecord((Node)n).definitionElement == null) continue;
                        return;
                    }
                    DefaultXBLManager.this.bind((Element)((Object)target));
                }
            }
        }
    }

    protected class ImportSubtreeListener
    implements EventListener {
        protected Element importElement;
        protected ImportRemovedListener importRemovedListener;

        public ImportSubtreeListener(Element imp, ImportRemovedListener irl) {
            this.importElement = imp;
            this.importRemovedListener = irl;
        }

        @Override
        public void handleEvent(Event evt) {
            Object[] defs = this.importRemovedListener.toBeRemoved.toArray();
            this.importRemovedListener.toBeRemoved.clear();
            for (Object def1 : defs) {
                XBLOMDefinitionElement def = (XBLOMDefinitionElement)def1;
                DefinitionRecord defRec = (DefinitionRecord)DefaultXBLManager.this.definitions.get((Object)def, (Object)this.importElement);
                DefaultXBLManager.this.removeDefinition(defRec);
            }
        }
    }

    protected static class ImportRemovedListener
    implements EventListener {
        protected LinkedList toBeRemoved = new LinkedList();

        protected ImportRemovedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            this.toBeRemoved.add(evt.getTarget());
        }
    }

    protected class ImportInsertedListener
    implements EventListener {
        protected Element importElement;

        public ImportInsertedListener(Element importElement) {
            this.importElement = importElement;
        }

        @Override
        public void handleEvent(Event evt) {
            EventTarget target = evt.getTarget();
            if (target instanceof XBLOMDefinitionElement) {
                XBLOMDefinitionElement def = (XBLOMDefinitionElement)target;
                DefaultXBLManager.this.addDefinition(def.getElementNamespaceURI(), def.getElementLocalName(), def, this.importElement);
            }
        }
    }

    protected class ImportRecord {
        public Element importElement;
        public Node node;
        public DefNodeInsertedListener defNodeInsertedListener;
        public DefNodeRemovedListener defNodeRemovedListener;
        public DefAttrListener defAttrListener;
        public ImportInsertedListener importInsertedListener;
        public ImportRemovedListener importRemovedListener;
        public ImportSubtreeListener importSubtreeListener;
        public TemplateMutationListener templateMutationListener;

        public ImportRecord(Element imp, Node n) {
            this.importElement = imp;
            this.node = n;
            this.defNodeInsertedListener = new DefNodeInsertedListener(imp);
            this.defNodeRemovedListener = new DefNodeRemovedListener(imp);
            this.defAttrListener = new DefAttrListener(imp);
            this.importInsertedListener = new ImportInsertedListener(imp);
            this.importRemovedListener = new ImportRemovedListener();
            this.importSubtreeListener = new ImportSubtreeListener(imp, this.importRemovedListener);
            this.templateMutationListener = new TemplateMutationListener(imp);
        }
    }

    protected static class DefinitionRecord
    implements Comparable {
        public String namespaceURI;
        public String localName;
        public XBLOMDefinitionElement definition;
        public XBLOMTemplateElement template;
        public Element importElement;

        public DefinitionRecord(String ns, String ln, XBLOMDefinitionElement def, XBLOMTemplateElement t, Element imp) {
            this.namespaceURI = ns;
            this.localName = ln;
            this.definition = def;
            this.template = t;
            this.importElement = imp;
        }

        public boolean equals(Object other) {
            return this.compareTo(other) == 0;
        }

        public int compareTo(Object other) {
            Object n2;
            XBLOMDefinitionElement n1;
            DefinitionRecord rec = (DefinitionRecord)other;
            if (this.importElement == null) {
                n1 = this.definition;
                n2 = rec.importElement == null ? rec.definition : (AbstractNode)rec.importElement;
            } else if (rec.importElement == null) {
                n1 = (AbstractNode)this.importElement;
                n2 = rec.definition;
            } else if (this.definition.getOwnerDocument() == rec.definition.getOwnerDocument()) {
                n1 = this.definition;
                n2 = rec.definition;
            } else {
                n1 = (AbstractNode)this.importElement;
                n2 = (AbstractNode)rec.importElement;
            }
            short comp = n1.compareDocumentPosition((Node)n2);
            if ((comp & 2) != 0) {
                return -1;
            }
            if ((comp & 4) != 0) {
                return 1;
            }
            return 0;
        }
    }
}

