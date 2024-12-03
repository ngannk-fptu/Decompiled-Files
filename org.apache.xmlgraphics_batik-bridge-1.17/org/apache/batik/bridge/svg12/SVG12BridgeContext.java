/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.anim.dom.XBLEventSupport
 *  org.apache.batik.anim.dom.XBLOMShadowTreeElement
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.events.EventSupport
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.xbl.NodeXBL
 *  org.apache.batik.dom.xbl.XBLManager
 *  org.apache.batik.script.Interpreter
 *  org.apache.batik.script.InterpreterPool
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.bridge.svg12;

import java.util.Set;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.anim.dom.XBLOMShadowTreeElement;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.ScriptingEnvironment;
import org.apache.batik.bridge.URIResolver;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.svg12.BindingListener;
import org.apache.batik.bridge.svg12.ContentSelectionChangedEvent;
import org.apache.batik.bridge.svg12.ContentSelectionChangedListener;
import org.apache.batik.bridge.svg12.DefaultXBLManager;
import org.apache.batik.bridge.svg12.SVG12BridgeEventSupport;
import org.apache.batik.bridge.svg12.SVG12BridgeUpdateHandler;
import org.apache.batik.bridge.svg12.SVG12FocusManager;
import org.apache.batik.bridge.svg12.SVG12ScriptingEnvironment;
import org.apache.batik.bridge.svg12.SVG12URIResolver;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.xbl.NodeXBL;
import org.apache.batik.dom.xbl.XBLManager;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterPool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;

public class SVG12BridgeContext
extends BridgeContext {
    protected XBLBindingListener bindingListener;
    protected XBLContentListener contentListener;
    protected EventTarget mouseCaptureTarget;
    protected boolean mouseCaptureSendAll;
    protected boolean mouseCaptureAutoRelease;

    public SVG12BridgeContext(UserAgent userAgent) {
        super(userAgent);
    }

    public SVG12BridgeContext(UserAgent userAgent, DocumentLoader loader) {
        super(userAgent, loader);
    }

    public SVG12BridgeContext(UserAgent userAgent, InterpreterPool interpreterPool, DocumentLoader documentLoader) {
        super(userAgent, interpreterPool, documentLoader);
    }

    @Override
    public URIResolver createURIResolver(SVGDocument doc, DocumentLoader dl) {
        return new SVG12URIResolver(doc, dl);
    }

    @Override
    public void addGVTListener(Document doc) {
        SVG12BridgeEventSupport.addGVTListener(this, doc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispose() {
        this.clearChildContexts();
        Set set = this.eventListenerSet;
        synchronized (set) {
            for (Object anEventListenerSet : this.eventListenerSet) {
                String ns;
                BridgeContext.EventListenerMememto m = (BridgeContext.EventListenerMememto)anEventListenerSet;
                NodeEventTarget et = m.getTarget();
                EventListener el = m.getListener();
                boolean uc = m.getUseCapture();
                String t = m.getEventType();
                boolean in = m.getNamespaced();
                if (et == null || el == null || t == null) continue;
                if (m instanceof ImplementationEventListenerMememto) {
                    ns = m.getNamespaceURI();
                    Node nde = (Node)et;
                    AbstractNode n = (AbstractNode)nde.getOwnerDocument();
                    if (n == null) continue;
                    XBLEventSupport es = (XBLEventSupport)n.initializeEventSupport();
                    es.removeImplementationEventListenerNS(ns, t, el, uc);
                    continue;
                }
                if (in) {
                    ns = m.getNamespaceURI();
                    et.removeEventListenerNS(ns, t, el, uc);
                    continue;
                }
                et.removeEventListener(t, el, uc);
            }
        }
        if (this.document != null) {
            this.removeDOMListeners();
            this.removeBindingListener();
        }
        if (this.animationEngine != null) {
            this.animationEngine.dispose();
            this.animationEngine = null;
        }
        for (Object o : this.interpreterMap.values()) {
            Interpreter interpreter = (Interpreter)o;
            if (interpreter == null) continue;
            interpreter.dispose();
        }
        this.interpreterMap.clear();
        if (this.focusManager != null) {
            this.focusManager.dispose();
        }
    }

    public void addBindingListener() {
        AbstractDocument doc = (AbstractDocument)this.document;
        DefaultXBLManager xm = (DefaultXBLManager)doc.getXBLManager();
        if (xm != null) {
            this.bindingListener = new XBLBindingListener();
            xm.addBindingListener(this.bindingListener);
            this.contentListener = new XBLContentListener();
            xm.addContentSelectionChangedListener(this.contentListener);
        }
    }

    public void removeBindingListener() {
        AbstractDocument doc = (AbstractDocument)this.document;
        XBLManager xm = doc.getXBLManager();
        if (xm instanceof DefaultXBLManager) {
            DefaultXBLManager dxm = (DefaultXBLManager)xm;
            dxm.removeBindingListener(this.bindingListener);
            dxm.removeContentSelectionChangedListener(this.contentListener);
        }
    }

    @Override
    public void addDOMListeners() {
        SVGOMDocument doc = (SVGOMDocument)this.document;
        XBLEventSupport evtSupport = (XBLEventSupport)doc.initializeEventSupport();
        this.domAttrModifiedEventListener = new EventListenerWrapper(new BridgeContext.DOMAttrModifiedEventListener(this));
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedEventListener, true);
        this.domNodeInsertedEventListener = new EventListenerWrapper(new BridgeContext.DOMNodeInsertedEventListener(this));
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedEventListener, true);
        this.domNodeRemovedEventListener = new EventListenerWrapper(new BridgeContext.DOMNodeRemovedEventListener(this));
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedEventListener, true);
        this.domCharacterDataModifiedEventListener = new EventListenerWrapper(new BridgeContext.DOMCharacterDataModifiedEventListener(this));
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.domCharacterDataModifiedEventListener, true);
        this.animatedAttributeListener = new BridgeContext.AnimatedAttrListener(this);
        doc.addAnimatedAttributeListener(this.animatedAttributeListener);
        this.focusManager = new SVG12FocusManager(this.document);
        CSSEngine cssEngine = doc.getCSSEngine();
        this.cssPropertiesChangedListener = new BridgeContext.CSSPropertiesChangedListener(this);
        cssEngine.addCSSEngineListener(this.cssPropertiesChangedListener);
    }

    @Override
    public void addUIEventListeners(Document doc) {
        EventTarget evtTarget = (EventTarget)((Object)doc.getDocumentElement());
        AbstractNode n = (AbstractNode)evtTarget;
        XBLEventSupport evtSupport = (XBLEventSupport)n.initializeEventSupport();
        EventListenerWrapper domMouseOverListener = new EventListenerWrapper(new BridgeContext.DOMMouseOverEventListener(this));
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", (EventListener)domMouseOverListener, true);
        this.storeImplementationEventListenerNS(evtTarget, "http://www.w3.org/2001/xml-events", "mouseover", domMouseOverListener, true);
        EventListenerWrapper domMouseOutListener = new EventListenerWrapper(new BridgeContext.DOMMouseOutEventListener(this));
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", (EventListener)domMouseOutListener, true);
        this.storeImplementationEventListenerNS(evtTarget, "http://www.w3.org/2001/xml-events", "mouseout", domMouseOutListener, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeUIEventListeners(Document doc) {
        EventTarget evtTarget = (EventTarget)((Object)doc.getDocumentElement());
        AbstractNode n = (AbstractNode)evtTarget;
        XBLEventSupport es = (XBLEventSupport)n.initializeEventSupport();
        Set set = this.eventListenerSet;
        synchronized (set) {
            for (Object anEventListenerSet : this.eventListenerSet) {
                String ns;
                BridgeContext.EventListenerMememto elm = (BridgeContext.EventListenerMememto)anEventListenerSet;
                NodeEventTarget et = elm.getTarget();
                if (et != evtTarget) continue;
                EventListener el = elm.getListener();
                boolean uc = elm.getUseCapture();
                String t = elm.getEventType();
                boolean in = elm.getNamespaced();
                if (et == null || el == null || t == null) continue;
                if (elm instanceof ImplementationEventListenerMememto) {
                    ns = elm.getNamespaceURI();
                    es.removeImplementationEventListenerNS(ns, t, el, uc);
                    continue;
                }
                if (in) {
                    ns = elm.getNamespaceURI();
                    et.removeEventListenerNS(ns, t, el, uc);
                    continue;
                }
                et.removeEventListener(t, el, uc);
            }
        }
    }

    @Override
    protected void removeDOMListeners() {
        SVGOMDocument doc = (SVGOMDocument)this.document;
        doc.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedEventListener, true);
        doc.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedEventListener, true);
        doc.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedEventListener, true);
        doc.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", this.domCharacterDataModifiedEventListener, true);
        doc.removeAnimatedAttributeListener(this.animatedAttributeListener);
        CSSEngine cssEngine = doc.getCSSEngine();
        if (cssEngine != null) {
            cssEngine.removeCSSEngineListener(this.cssPropertiesChangedListener);
            cssEngine.dispose();
            doc.setCSSEngine(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void storeImplementationEventListenerNS(EventTarget t, String ns, String s, EventListener l, boolean b) {
        Set set = this.eventListenerSet;
        synchronized (set) {
            ImplementationEventListenerMememto m = new ImplementationEventListenerMememto(t, ns, s, l, b, this);
            this.eventListenerSet.add(m);
        }
    }

    @Override
    public BridgeContext createSubBridgeContext(SVGOMDocument newDoc) {
        CSSEngine eng = newDoc.getCSSEngine();
        if (eng != null) {
            return (BridgeContext)newDoc.getCSSEngine().getCSSContext();
        }
        BridgeContext subCtx = super.createSubBridgeContext(newDoc);
        if (this.isDynamic() && subCtx.isDynamic()) {
            this.setUpdateManager(subCtx, this.updateManager);
            if (this.updateManager != null) {
                ScriptingEnvironment se = newDoc.isSVG12() ? new SVG12ScriptingEnvironment(subCtx) : new ScriptingEnvironment(subCtx);
                se.loadScripts();
                se.dispatchSVGLoadEvent();
                if (newDoc.isSVG12()) {
                    DefaultXBLManager xm = new DefaultXBLManager((Document)newDoc, subCtx);
                    this.setXBLManager(subCtx, xm);
                    newDoc.setXBLManager((XBLManager)xm);
                    xm.startProcessing();
                }
            }
        }
        return subCtx;
    }

    public void startMouseCapture(EventTarget target, boolean sendAll, boolean autoRelease) {
        this.mouseCaptureTarget = target;
        this.mouseCaptureSendAll = sendAll;
        this.mouseCaptureAutoRelease = autoRelease;
    }

    public void stopMouseCapture() {
        this.mouseCaptureTarget = null;
    }

    protected class XBLContentListener
    implements ContentSelectionChangedListener {
        protected XBLContentListener() {
        }

        @Override
        public void contentSelectionChanged(ContentSelectionChangedEvent csce) {
            BridgeUpdateHandler h;
            Element e = (Element)csce.getContentElement().getParentNode();
            if (e instanceof XBLOMShadowTreeElement) {
                e = ((NodeXBL)e).getXblBoundElement();
            }
            if ((h = SVG12BridgeContext.getBridgeUpdateHandler(e)) instanceof SVG12BridgeUpdateHandler) {
                SVG12BridgeUpdateHandler h12 = (SVG12BridgeUpdateHandler)h;
                try {
                    h12.handleContentSelectionChangedEvent(csce);
                }
                catch (Exception ex) {
                    SVG12BridgeContext.this.userAgent.displayError(ex);
                }
            }
        }
    }

    protected class XBLBindingListener
    implements BindingListener {
        protected XBLBindingListener() {
        }

        @Override
        public void bindingChanged(Element bindableElement, Element shadowTree) {
            BridgeUpdateHandler h = SVG12BridgeContext.getBridgeUpdateHandler(bindableElement);
            if (h instanceof SVG12BridgeUpdateHandler) {
                SVG12BridgeUpdateHandler h12 = (SVG12BridgeUpdateHandler)h;
                try {
                    h12.handleBindingEvent(bindableElement, shadowTree);
                }
                catch (Exception e) {
                    SVG12BridgeContext.this.userAgent.displayError(e);
                }
            }
        }
    }

    protected static class EventListenerWrapper
    implements EventListener {
        protected EventListener listener;

        public EventListenerWrapper(EventListener l) {
            this.listener = l;
        }

        @Override
        public void handleEvent(Event evt) {
            this.listener.handleEvent(EventSupport.getUltimateOriginalEvent((Event)evt));
        }

        public String toString() {
            return super.toString() + " [wrapping " + this.listener.toString() + "]";
        }
    }

    protected static class ImplementationEventListenerMememto
    extends BridgeContext.EventListenerMememto {
        public ImplementationEventListenerMememto(EventTarget t, String s, EventListener l, boolean b, BridgeContext c) {
            super(t, s, l, b, c);
        }

        public ImplementationEventListenerMememto(EventTarget t, String n, String s, EventListener l, boolean b, BridgeContext c) {
            super(t, n, s, l, b, c);
        }
    }
}

