/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.CSSNavigableDocumentListener
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.css.engine.CSSNavigableDocumentListener;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;

public class SVG12OMDocument
extends SVGOMDocument {
    protected SVG12OMDocument() {
    }

    public SVG12OMDocument(DocumentType dt, DOMImplementation impl) {
        super(dt, impl);
    }

    @Override
    protected Node newNode() {
        return new SVG12OMDocument();
    }

    @Override
    public void addCSSNavigableDocumentListener(CSSNavigableDocumentListener l) {
        if (this.cssNavigableDocumentListeners.containsKey(l)) {
            return;
        }
        SVGOMDocument.DOMNodeInsertedListenerWrapper nodeInserted = new SVGOMDocument.DOMNodeInsertedListenerWrapper(l);
        SVGOMDocument.DOMNodeRemovedListenerWrapper nodeRemoved = new SVGOMDocument.DOMNodeRemovedListenerWrapper(l);
        SVGOMDocument.DOMSubtreeModifiedListenerWrapper subtreeModified = new SVGOMDocument.DOMSubtreeModifiedListenerWrapper(l);
        SVGOMDocument.DOMCharacterDataModifiedListenerWrapper cdataModified = new SVGOMDocument.DOMCharacterDataModifiedListenerWrapper(l);
        SVGOMDocument.DOMAttrModifiedListenerWrapper attrModified = new SVGOMDocument.DOMAttrModifiedListenerWrapper(l);
        this.cssNavigableDocumentListeners.put(l, new EventListener[]{nodeInserted, nodeRemoved, subtreeModified, cdataModified, attrModified});
        XBLEventSupport es = (XBLEventSupport)this.initializeEventSupport();
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", nodeInserted, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", nodeRemoved, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", subtreeModified, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", cdataModified, false);
        es.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", attrModified, false);
    }

    @Override
    public void removeCSSNavigableDocumentListener(CSSNavigableDocumentListener l) {
        EventListener[] listeners = (EventListener[])this.cssNavigableDocumentListeners.get(l);
        if (listeners == null) {
            return;
        }
        XBLEventSupport es = (XBLEventSupport)this.initializeEventSupport();
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", listeners[0], false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", listeners[1], false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", listeners[2], false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMCharacterDataModified", listeners[3], false);
        es.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", listeners[4], false);
        this.cssNavigableDocumentListeners.remove(l);
    }
}

