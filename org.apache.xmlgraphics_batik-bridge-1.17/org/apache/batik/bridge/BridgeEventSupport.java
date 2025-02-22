/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.events.DOMKeyEvent
 *  org.apache.batik.dom.events.DOMMouseEvent
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.util.DOMUtilities
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.event.EventDispatcher
 *  org.apache.batik.gvt.event.GraphicsNodeKeyEvent
 *  org.apache.batik.gvt.event.GraphicsNodeKeyListener
 *  org.apache.batik.gvt.event.GraphicsNodeMouseEvent
 *  org.apache.batik.gvt.event.GraphicsNodeMouseListener
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.bridge;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import java.text.AttributedCharacterIterator;
import java.util.List;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.FocusManager;
import org.apache.batik.bridge.StrokingTextPainter;
import org.apache.batik.bridge.TextHit;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.TextSpanLayout;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.events.DOMKeyEvent;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public abstract class BridgeEventSupport
implements SVGConstants {
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_ID = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_ID;

    protected BridgeEventSupport() {
    }

    public static void addGVTListener(BridgeContext ctx, Document doc) {
        EventDispatcher dispatcher;
        UserAgent ua = ctx.getUserAgent();
        if (ua != null && (dispatcher = ua.getEventDispatcher()) != null) {
            Listener listener = new Listener(ctx, ua);
            dispatcher.addGraphicsNodeMouseListener((GraphicsNodeMouseListener)listener);
            dispatcher.addGraphicsNodeKeyListener((GraphicsNodeKeyListener)listener);
            GVTUnloadListener l = new GVTUnloadListener(dispatcher, listener);
            NodeEventTarget target = (NodeEventTarget)doc;
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGUnload", (EventListener)l, false, null);
            BridgeEventSupport.storeEventListenerNS(ctx, (EventTarget)target, "http://www.w3.org/2001/xml-events", "SVGUnload", l, false);
        }
    }

    protected static void storeEventListener(BridgeContext ctx, EventTarget e, String t, EventListener l, boolean c) {
        ctx.storeEventListener(e, t, l, c);
    }

    protected static void storeEventListenerNS(BridgeContext ctx, EventTarget e, String n, String t, EventListener l, boolean c) {
        ctx.storeEventListenerNS(e, n, t, l, c);
    }

    protected static class Listener
    implements GraphicsNodeMouseListener,
    GraphicsNodeKeyListener {
        protected BridgeContext context;
        protected UserAgent ua;
        protected Element lastTargetElement;
        protected boolean isDown;

        public Listener(BridgeContext ctx, UserAgent u) {
            this.context = ctx;
            this.ua = u;
        }

        public void keyPressed(GraphicsNodeKeyEvent evt) {
            if (!this.isDown) {
                this.isDown = true;
                this.dispatchKeyEvent("keydown", evt);
            }
            if (evt.getKeyChar() == '\uffff') {
                this.dispatchKeyEvent("keypress", evt);
            }
        }

        public void keyReleased(GraphicsNodeKeyEvent evt) {
            this.dispatchKeyEvent("keyup", evt);
            this.isDown = false;
        }

        public void keyTyped(GraphicsNodeKeyEvent evt) {
            this.dispatchKeyEvent("keypress", evt);
        }

        protected void dispatchKeyEvent(String eventType, GraphicsNodeKeyEvent evt) {
            FocusManager fmgr = this.context.getFocusManager();
            if (fmgr == null) {
                return;
            }
            Element targetElement = (Element)((Object)fmgr.getCurrentEventTarget());
            if (targetElement == null) {
                targetElement = this.context.getDocument().getDocumentElement();
            }
            DocumentEvent d = (DocumentEvent)((Object)targetElement.getOwnerDocument());
            DOMKeyEvent keyEvt = (DOMKeyEvent)d.createEvent("KeyEvents");
            keyEvt.initKeyEvent(eventType, true, true, evt.isControlDown(), evt.isAltDown(), evt.isShiftDown(), evt.isMetaDown(), this.mapKeyCode(evt.getKeyCode()), (int)evt.getKeyChar(), null);
            try {
                ((EventTarget)((Object)targetElement)).dispatchEvent((Event)keyEvt);
            }
            catch (RuntimeException e) {
                this.ua.displayError(e);
            }
        }

        protected final int mapKeyCode(int keyCode) {
            switch (keyCode) {
                case 10: {
                    return 13;
                }
                case 262: {
                    return 0;
                }
                case 263: {
                    return 0;
                }
            }
            return keyCode;
        }

        public void mouseClicked(GraphicsNodeMouseEvent evt) {
            this.dispatchMouseEvent("click", evt, true);
        }

        public void mousePressed(GraphicsNodeMouseEvent evt) {
            this.dispatchMouseEvent("mousedown", evt, true);
        }

        public void mouseReleased(GraphicsNodeMouseEvent evt) {
            this.dispatchMouseEvent("mouseup", evt, true);
        }

        public void mouseEntered(GraphicsNodeMouseEvent evt) {
            Point clientXY = evt.getClientPoint();
            GraphicsNode node = evt.getGraphicsNode();
            Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            Element relatedElement = this.getRelatedElement(evt);
            this.dispatchMouseEvent("mouseover", targetElement, relatedElement, clientXY, evt, true);
        }

        public void mouseExited(GraphicsNodeMouseEvent evt) {
            Point clientXY = evt.getClientPoint();
            GraphicsNode node = evt.getRelatedNode();
            Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            if (this.lastTargetElement != null) {
                this.dispatchMouseEvent("mouseout", this.lastTargetElement, targetElement, clientXY, evt, true);
                this.lastTargetElement = null;
            }
        }

        public void mouseDragged(GraphicsNodeMouseEvent evt) {
            this.dispatchMouseEvent("mousemove", evt, false);
        }

        public void mouseMoved(GraphicsNodeMouseEvent evt) {
            Point clientXY = evt.getClientPoint();
            Element holdLTE = this.lastTargetElement;
            GraphicsNode node = evt.getGraphicsNode();
            Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            if (holdLTE != targetElement) {
                if (holdLTE != null) {
                    this.dispatchMouseEvent("mouseout", holdLTE, targetElement, clientXY, evt, true);
                }
                if (targetElement != null) {
                    this.dispatchMouseEvent("mouseover", targetElement, holdLTE, clientXY, evt, true);
                }
            }
            this.dispatchMouseEvent("mousemove", targetElement, null, clientXY, evt, false);
        }

        protected void dispatchMouseEvent(String eventType, GraphicsNodeMouseEvent evt, boolean cancelable) {
            Point clientXY = evt.getClientPoint();
            GraphicsNode node = evt.getGraphicsNode();
            Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            Element relatedElement = this.getRelatedElement(evt);
            this.dispatchMouseEvent(eventType, targetElement, relatedElement, clientXY, evt, cancelable);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void dispatchMouseEvent(String eventType, Element targetElement, Element relatedElement, Point clientXY, GraphicsNodeMouseEvent evt, boolean cancelable) {
            if (targetElement == null) {
                return;
            }
            Point screenXY = evt.getScreenPoint();
            DocumentEvent d = (DocumentEvent)((Object)targetElement.getOwnerDocument());
            DOMMouseEvent mouseEvt = (DOMMouseEvent)d.createEvent("MouseEvents");
            String modifiers = DOMUtilities.getModifiersList((int)evt.getLockState(), (int)evt.getModifiers());
            mouseEvt.initMouseEventNS("http://www.w3.org/2001/xml-events", eventType, true, cancelable, null, evt.getClickCount(), screenXY.x, screenXY.y, clientXY.x, clientXY.y, (short)(evt.getButton() - 1), (EventTarget)((Object)relatedElement), modifiers);
            try {
                ((EventTarget)((Object)targetElement)).dispatchEvent((Event)mouseEvt);
            }
            catch (RuntimeException e) {
                this.ua.displayError(e);
            }
            finally {
                this.lastTargetElement = targetElement;
            }
        }

        protected Element getRelatedElement(GraphicsNodeMouseEvent evt) {
            GraphicsNode relatedNode = evt.getRelatedNode();
            Element relatedElement = null;
            if (relatedNode != null) {
                relatedElement = this.context.getElement(relatedNode);
            }
            return relatedElement;
        }

        protected Element getEventTarget(GraphicsNode node, Point2D pt) {
            TextNode textNode;
            List list;
            Element target = this.context.getElement(node);
            if (target != null && node instanceof TextNode && (list = (textNode = (TextNode)node).getTextRuns()) != null) {
                float x = (float)pt.getX();
                float y = (float)pt.getY();
                for (Object aList : list) {
                    SoftReference sr;
                    Object delimiter;
                    StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
                    AttributedCharacterIterator aci = run.getACI();
                    TextSpanLayout layout = run.getLayout();
                    TextHit textHit = layout.hitTestChar(x, y);
                    Rectangle2D bounds = layout.getBounds2D();
                    if (textHit == null || bounds == null || !bounds.contains(x, y) || !((delimiter = (sr = (SoftReference)aci.getAttribute(TEXT_COMPOUND_ID)).get()) instanceof Element)) continue;
                    return (Element)delimiter;
                }
            }
            return target;
        }
    }

    protected static class GVTUnloadListener
    implements EventListener {
        protected EventDispatcher dispatcher;
        protected Listener listener;

        public GVTUnloadListener(EventDispatcher dispatcher, Listener listener) {
            this.dispatcher = dispatcher;
            this.listener = listener;
        }

        @Override
        public void handleEvent(Event evt) {
            this.dispatcher.removeGraphicsNodeMouseListener((GraphicsNodeMouseListener)this.listener);
            this.dispatcher.removeGraphicsNodeKeyListener((GraphicsNodeKeyListener)this.listener);
            NodeEventTarget et = (NodeEventTarget)evt.getTarget();
            et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGUnload", (EventListener)this, false);
        }
    }
}

