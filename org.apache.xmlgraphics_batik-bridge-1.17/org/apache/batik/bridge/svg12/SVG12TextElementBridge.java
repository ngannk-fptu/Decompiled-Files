/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.XBLEventSupport
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.events.EventSupport
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.xbl.NodeXBL
 */
package org.apache.batik.bridge.svg12;

import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGTextElementBridge;
import org.apache.batik.bridge.svg12.ContentSelectionChangedEvent;
import org.apache.batik.bridge.svg12.SVG12BridgeContext;
import org.apache.batik.bridge.svg12.SVG12BridgeUpdateHandler;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.xbl.NodeXBL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

public class SVG12TextElementBridge
extends SVGTextElementBridge
implements SVG12BridgeUpdateHandler {
    @Override
    public Bridge getInstance() {
        return new SVG12TextElementBridge();
    }

    @Override
    protected void addTextEventListeners(BridgeContext ctx, NodeEventTarget e) {
        if (this.childNodeRemovedEventListener == null) {
            this.childNodeRemovedEventListener = new DOMChildNodeRemovedEventListener();
        }
        if (this.subtreeModifiedEventListener == null) {
            this.subtreeModifiedEventListener = new DOMSubtreeModifiedEventListener();
        }
        SVG12BridgeContext ctx12 = (SVG12BridgeContext)ctx;
        AbstractNode n = (AbstractNode)e;
        XBLEventSupport evtSupport = (XBLEventSupport)n.initializeEventSupport();
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)this.childNodeRemovedEventListener, true);
        ctx12.storeImplementationEventListenerNS((EventTarget)e, "http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.childNodeRemovedEventListener, true);
        evtSupport.addImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)this.subtreeModifiedEventListener, false);
        ctx12.storeImplementationEventListenerNS((EventTarget)e, "http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.subtreeModifiedEventListener, false);
    }

    @Override
    protected void removeTextEventListeners(BridgeContext ctx, NodeEventTarget e) {
        AbstractNode n = (AbstractNode)e;
        XBLEventSupport evtSupport = (XBLEventSupport)n.initializeEventSupport();
        evtSupport.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)this.childNodeRemovedEventListener, true);
        evtSupport.removeImplementationEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)this.subtreeModifiedEventListener, false);
    }

    @Override
    protected Node getFirstChild(Node n) {
        return ((NodeXBL)n).getXblFirstChild();
    }

    @Override
    protected Node getNextSibling(Node n) {
        return ((NodeXBL)n).getXblNextSibling();
    }

    @Override
    protected Node getParentNode(Node n) {
        return ((NodeXBL)n).getXblParentNode();
    }

    @Override
    public void handleDOMCharacterDataModified(MutationEvent evt) {
        Node childNode = (Node)((Object)evt.getTarget());
        if (this.isParentDisplayed(childNode)) {
            if (this.getParentNode(childNode) != childNode.getParentNode()) {
                this.computeLaidoutText(this.ctx, this.e, this.node);
            } else {
                this.laidoutText = null;
            }
        }
    }

    @Override
    public void handleBindingEvent(Element bindableElement, Element shadowTree) {
    }

    @Override
    public void handleContentSelectionChangedEvent(ContentSelectionChangedEvent csce) {
        this.computeLaidoutText(this.ctx, this.e, this.node);
    }

    protected class DOMSubtreeModifiedEventListener
    extends SVGTextElementBridge.DOMSubtreeModifiedEventListener {
        protected DOMSubtreeModifiedEventListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent((Event)evt));
        }
    }

    protected class DOMChildNodeRemovedEventListener
    extends SVGTextElementBridge.DOMChildNodeRemovedEventListener {
        protected DOMChildNodeRemovedEventListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            super.handleEvent(EventSupport.getUltimateOriginalEvent((Event)evt));
        }
    }
}

