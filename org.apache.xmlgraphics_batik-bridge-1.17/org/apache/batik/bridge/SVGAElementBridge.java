/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMAElement
 *  org.apache.batik.anim.dom.SVGOMAnimationElement
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.dom.events.AbstractEvent
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.dom.svg.SVGAElement
 */
package org.apache.batik.bridge;

import java.awt.Cursor;
import java.util.List;
import org.apache.batik.anim.dom.SVGOMAElement;
import org.apache.batik.anim.dom.SVGOMAnimationElement;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.CursorManager;
import org.apache.batik.bridge.SVGGElementBridge;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGAElement;

public class SVGAElementBridge
extends SVGGElementBridge {
    protected AnchorListener al;
    protected CursorMouseOverListener bl;
    protected CursorMouseOutListener cl;

    @Override
    public String getLocalName() {
        return "a";
    }

    @Override
    public Bridge getInstance() {
        return new SVGAElementBridge();
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        super.buildGraphicsNode(ctx, e, node);
        if (ctx.isInteractive()) {
            NodeEventTarget target = (NodeEventTarget)e;
            CursorHolder ch = new CursorHolder(CursorManager.DEFAULT_CURSOR);
            this.al = new AnchorListener(ctx.getUserAgent(), ch);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", (EventListener)this.al, false, null);
            ctx.storeEventListenerNS((EventTarget)target, "http://www.w3.org/2001/xml-events", "click", this.al, false);
            this.bl = new CursorMouseOverListener(ctx.getUserAgent(), ch);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", (EventListener)this.bl, false, null);
            ctx.storeEventListenerNS((EventTarget)target, "http://www.w3.org/2001/xml-events", "mouseover", this.bl, false);
            this.cl = new CursorMouseOutListener(ctx.getUserAgent(), ch);
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", (EventListener)this.cl, false, null);
            ctx.storeEventListenerNS((EventTarget)target, "http://www.w3.org/2001/xml-events", "mouseout", this.cl, false);
        }
    }

    @Override
    public void dispose() {
        NodeEventTarget target = (NodeEventTarget)this.e;
        if (this.al != null) {
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", (EventListener)this.al, false);
            this.al = null;
        }
        if (this.bl != null) {
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", (EventListener)this.bl, false);
            this.bl = null;
        }
        if (this.cl != null) {
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", (EventListener)this.cl, false);
            this.cl = null;
        }
        super.dispose();
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    public static class MouseOutDefaultActionable
    implements Runnable {
        protected SVGAElement elt;
        protected UserAgent userAgent;
        protected CursorHolder holder;

        public MouseOutDefaultActionable(SVGAElement e, UserAgent ua, CursorHolder ch) {
            this.elt = e;
            this.userAgent = ua;
            this.holder = ch;
        }

        @Override
        public void run() {
            if (this.elt != null) {
                this.userAgent.displayMessage("");
            }
        }
    }

    public static class CursorMouseOutListener
    implements EventListener {
        protected UserAgent userAgent;
        protected CursorHolder holder;

        public CursorMouseOutListener(UserAgent ua, CursorHolder ch) {
            this.userAgent = ua;
            this.holder = ch;
        }

        @Override
        public void handleEvent(Event evt) {
            if (!(evt instanceof AbstractEvent)) {
                return;
            }
            AbstractEvent ae = (AbstractEvent)evt;
            List l = ae.getDefaultActions();
            if (l != null) {
                for (Object o : l) {
                    if (!(o instanceof MouseOutDefaultActionable)) continue;
                    return;
                }
            }
            SVGAElement elt = (SVGAElement)evt.getCurrentTarget();
            ae.addDefaultAction((Runnable)new MouseOutDefaultActionable(elt, this.userAgent, this.holder));
        }
    }

    public static class MouseOverDefaultActionable
    implements Runnable {
        protected Element target;
        protected SVGAElement elt;
        protected UserAgent userAgent;
        protected CursorHolder holder;

        public MouseOverDefaultActionable(Element t, SVGAElement e, UserAgent ua, CursorHolder ch) {
            this.target = t;
            this.elt = e;
            this.userAgent = ua;
            this.holder = ch;
        }

        @Override
        public void run() {
            if (CSSUtilities.isAutoCursor(this.target)) {
                this.holder.holdCursor(CursorManager.DEFAULT_CURSOR);
                this.userAgent.setSVGCursor(CursorManager.ANCHOR_CURSOR);
            }
            if (this.elt != null) {
                String href = this.elt.getHref().getAnimVal();
                this.userAgent.displayMessage(href);
            }
        }
    }

    public static class CursorMouseOverListener
    implements EventListener {
        protected UserAgent userAgent;
        protected CursorHolder holder;

        public CursorMouseOverListener(UserAgent ua, CursorHolder ch) {
            this.userAgent = ua;
            this.holder = ch;
        }

        @Override
        public void handleEvent(Event evt) {
            if (!(evt instanceof AbstractEvent)) {
                return;
            }
            AbstractEvent ae = (AbstractEvent)evt;
            List l = ae.getDefaultActions();
            if (l != null) {
                for (Object o : l) {
                    if (!(o instanceof MouseOverDefaultActionable)) continue;
                    return;
                }
            }
            Element target = (Element)((Object)ae.getTarget());
            SVGAElement elt = (SVGAElement)ae.getCurrentTarget();
            ae.addDefaultAction((Runnable)new MouseOverDefaultActionable(target, elt, this.userAgent, this.holder));
        }
    }

    public static class AnchorDefaultActionable
    implements Runnable {
        protected SVGOMAElement elt;
        protected UserAgent userAgent;
        protected CursorHolder holder;

        public AnchorDefaultActionable(SVGAElement e, UserAgent ua, CursorHolder ch) {
            this.elt = (SVGOMAElement)e;
            this.userAgent = ua;
            this.holder = ch;
        }

        @Override
        public void run() {
            Element refElt;
            String frag;
            this.userAgent.setSVGCursor(this.holder.getCursor());
            String href = this.elt.getHref().getAnimVal();
            ParsedURL purl = new ParsedURL(this.elt.getBaseURI(), href);
            SVGOMDocument doc = (SVGOMDocument)this.elt.getOwnerDocument();
            ParsedURL durl = doc.getParsedURL();
            if (purl.sameFile(durl) && (frag = purl.getRef()) != null && frag.length() != 0 && (refElt = doc.getElementById(frag)) instanceof SVGOMAnimationElement) {
                SVGOMAnimationElement aelt = (SVGOMAnimationElement)refElt;
                float t = aelt.getHyperlinkBeginTime();
                if (Float.isNaN(t)) {
                    aelt.beginElement();
                } else {
                    doc.getRootElement().setCurrentTime(t);
                }
                return;
            }
            this.userAgent.openLink((SVGAElement)this.elt);
        }
    }

    public static class AnchorListener
    implements EventListener {
        protected UserAgent userAgent;
        protected CursorHolder holder;

        public AnchorListener(UserAgent ua, CursorHolder ch) {
            this.userAgent = ua;
            this.holder = ch;
        }

        @Override
        public void handleEvent(Event evt) {
            if (!(evt instanceof AbstractEvent)) {
                return;
            }
            AbstractEvent ae = (AbstractEvent)evt;
            List l = ae.getDefaultActions();
            if (l != null) {
                for (Object o : l) {
                    if (!(o instanceof AnchorDefaultActionable)) continue;
                    return;
                }
            }
            SVGAElement elt = (SVGAElement)evt.getCurrentTarget();
            ae.addDefaultAction((Runnable)new AnchorDefaultActionable(elt, this.userAgent, this.holder));
        }
    }

    public static class CursorHolder {
        Cursor cursor = null;

        public CursorHolder(Cursor c) {
            this.cursor = c;
        }

        public void holdCursor(Cursor c) {
            this.cursor = c;
        }

        public Cursor getCursor() {
            return this.cursor;
        }
    }
}

