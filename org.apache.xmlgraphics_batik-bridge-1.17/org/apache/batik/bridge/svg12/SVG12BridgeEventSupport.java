/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.events.DOMKeyboardEvent
 *  org.apache.batik.dom.events.DOMMouseEvent
 *  org.apache.batik.dom.events.DOMTextEvent
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.svg12.SVGOMWheelEvent
 *  org.apache.batik.dom.util.DOMUtilities
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.event.EventDispatcher
 *  org.apache.batik.gvt.event.GraphicsNodeKeyEvent
 *  org.apache.batik.gvt.event.GraphicsNodeKeyListener
 *  org.apache.batik.gvt.event.GraphicsNodeMouseEvent
 *  org.apache.batik.gvt.event.GraphicsNodeMouseListener
 *  org.apache.batik.gvt.event.GraphicsNodeMouseWheelEvent
 *  org.apache.batik.gvt.event.GraphicsNodeMouseWheelListener
 */
package org.apache.batik.bridge.svg12;

import java.awt.Point;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeEventSupport;
import org.apache.batik.bridge.FocusManager;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.svg12.DefaultXBLManager;
import org.apache.batik.bridge.svg12.SVG12BridgeContext;
import org.apache.batik.dom.events.DOMKeyboardEvent;
import org.apache.batik.dom.events.DOMMouseEvent;
import org.apache.batik.dom.events.DOMTextEvent;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.svg12.SVGOMWheelEvent;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseWheelEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseWheelListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public abstract class SVG12BridgeEventSupport
extends BridgeEventSupport {
    protected SVG12BridgeEventSupport() {
    }

    public static void addGVTListener(BridgeContext ctx, Document doc) {
        EventDispatcher dispatcher;
        UserAgent ua = ctx.getUserAgent();
        if (ua != null && (dispatcher = ua.getEventDispatcher()) != null) {
            Listener listener = new Listener(ctx, ua);
            dispatcher.addGraphicsNodeMouseListener((GraphicsNodeMouseListener)listener);
            dispatcher.addGraphicsNodeMouseWheelListener((GraphicsNodeMouseWheelListener)listener);
            dispatcher.addGraphicsNodeKeyListener((GraphicsNodeKeyListener)listener);
            BridgeEventSupport.GVTUnloadListener l = new BridgeEventSupport.GVTUnloadListener(dispatcher, listener);
            NodeEventTarget target = (NodeEventTarget)doc;
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGUnload", (EventListener)l, false, null);
            SVG12BridgeEventSupport.storeEventListenerNS(ctx, (EventTarget)target, "http://www.w3.org/2001/xml-events", "SVGUnload", l, false);
        }
    }

    protected static class Listener
    extends BridgeEventSupport.Listener
    implements GraphicsNodeMouseWheelListener {
        protected SVG12BridgeContext ctx12;
        protected static String[][] IDENTIFIER_KEY_CODES = new String[256][];

        public Listener(BridgeContext ctx, UserAgent u) {
            super(ctx, u);
            this.ctx12 = (SVG12BridgeContext)ctx;
        }

        @Override
        public void keyPressed(GraphicsNodeKeyEvent evt) {
            if (!this.isDown) {
                this.isDown = true;
                this.dispatchKeyboardEvent("keydown", evt);
            }
            if (evt.getKeyChar() == '\uffff') {
                this.dispatchTextEvent(evt);
            }
        }

        @Override
        public void keyReleased(GraphicsNodeKeyEvent evt) {
            this.dispatchKeyboardEvent("keyup", evt);
            this.isDown = false;
        }

        @Override
        public void keyTyped(GraphicsNodeKeyEvent evt) {
            this.dispatchTextEvent(evt);
        }

        protected void dispatchKeyboardEvent(String eventType, GraphicsNodeKeyEvent evt) {
            FocusManager fmgr = this.context.getFocusManager();
            if (fmgr == null) {
                return;
            }
            Element targetElement = (Element)((Object)fmgr.getCurrentEventTarget());
            if (targetElement == null) {
                targetElement = this.context.getDocument().getDocumentElement();
            }
            DocumentEvent d = (DocumentEvent)((Object)targetElement.getOwnerDocument());
            DOMKeyboardEvent keyEvt = (DOMKeyboardEvent)d.createEvent("KeyboardEvent");
            String modifiers = DOMUtilities.getModifiersList((int)evt.getLockState(), (int)evt.getModifiers());
            keyEvt.initKeyboardEventNS("http://www.w3.org/2001/xml-events", eventType, true, true, null, this.mapKeyCodeToIdentifier(evt.getKeyCode()), this.mapKeyLocation(evt.getKeyLocation()), modifiers);
            try {
                ((EventTarget)((Object)targetElement)).dispatchEvent((Event)keyEvt);
            }
            catch (RuntimeException e) {
                this.ua.displayError(e);
            }
        }

        protected void dispatchTextEvent(GraphicsNodeKeyEvent evt) {
            FocusManager fmgr = this.context.getFocusManager();
            if (fmgr == null) {
                return;
            }
            Element targetElement = (Element)((Object)fmgr.getCurrentEventTarget());
            if (targetElement == null) {
                targetElement = this.context.getDocument().getDocumentElement();
            }
            DocumentEvent d = (DocumentEvent)((Object)targetElement.getOwnerDocument());
            DOMTextEvent textEvt = (DOMTextEvent)d.createEvent("TextEvent");
            textEvt.initTextEventNS("http://www.w3.org/2001/xml-events", "textInput", true, true, null, String.valueOf(evt.getKeyChar()));
            try {
                ((EventTarget)((Object)targetElement)).dispatchEvent((Event)textEvt);
            }
            catch (RuntimeException e) {
                this.ua.displayError(e);
            }
        }

        protected int mapKeyLocation(int location) {
            return location - 1;
        }

        protected static void putIdentifierKeyCode(String keyIdentifier, int keyCode) {
            if (IDENTIFIER_KEY_CODES[keyCode / 256] == null) {
                Listener.IDENTIFIER_KEY_CODES[keyCode / 256] = new String[256];
            }
            Listener.IDENTIFIER_KEY_CODES[keyCode / 256][keyCode % 256] = keyIdentifier;
        }

        protected String mapKeyCodeToIdentifier(int keyCode) {
            String[] a = IDENTIFIER_KEY_CODES[keyCode / 256];
            if (a == null) {
                return "Unidentified";
            }
            return a[keyCode % 256];
        }

        public void mouseWheelMoved(GraphicsNodeMouseWheelEvent evt) {
            Document doc = this.context.getPrimaryBridgeContext().getDocument();
            Element targetElement = doc.getDocumentElement();
            DocumentEvent d = (DocumentEvent)((Object)doc);
            SVGOMWheelEvent wheelEvt = (SVGOMWheelEvent)d.createEvent("WheelEvent");
            wheelEvt.initWheelEventNS("http://www.w3.org/2001/xml-events", "wheel", true, true, null, evt.getWheelDelta());
            try {
                ((EventTarget)((Object)targetElement)).dispatchEvent((Event)wheelEvt);
            }
            catch (RuntimeException e) {
                this.ua.displayError(e);
            }
        }

        @Override
        public void mouseEntered(GraphicsNodeMouseEvent evt) {
            Point clientXY = evt.getClientPoint();
            GraphicsNode node = evt.getGraphicsNode();
            Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            Element relatedElement = this.getRelatedElement(evt);
            int n = 0;
            if (relatedElement != null && targetElement != null) {
                n = DefaultXBLManager.computeBubbleLimit(targetElement, relatedElement);
            }
            this.dispatchMouseEvent("mouseover", targetElement, relatedElement, clientXY, evt, true, n);
        }

        @Override
        public void mouseExited(GraphicsNodeMouseEvent evt) {
            Point clientXY = evt.getClientPoint();
            GraphicsNode node = evt.getRelatedNode();
            Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            if (this.lastTargetElement != null) {
                int n = 0;
                if (targetElement != null) {
                    n = DefaultXBLManager.computeBubbleLimit(this.lastTargetElement, targetElement);
                }
                this.dispatchMouseEvent("mouseout", this.lastTargetElement, targetElement, clientXY, evt, true, n);
                this.lastTargetElement = null;
            }
        }

        @Override
        public void mouseMoved(GraphicsNodeMouseEvent evt) {
            Point clientXY = evt.getClientPoint();
            Element holdLTE = this.lastTargetElement;
            GraphicsNode node = evt.getGraphicsNode();
            Element targetElement = this.getEventTarget(node, evt.getPoint2D());
            if (holdLTE != targetElement) {
                int n;
                if (holdLTE != null) {
                    n = 0;
                    if (targetElement != null) {
                        n = DefaultXBLManager.computeBubbleLimit(holdLTE, targetElement);
                    }
                    this.dispatchMouseEvent("mouseout", holdLTE, targetElement, clientXY, evt, true, n);
                }
                if (targetElement != null) {
                    n = 0;
                    if (holdLTE != null) {
                        n = DefaultXBLManager.computeBubbleLimit(targetElement, holdLTE);
                    }
                    this.dispatchMouseEvent("mouseover", targetElement, holdLTE, clientXY, evt, true, n);
                }
            }
            this.dispatchMouseEvent("mousemove", targetElement, null, clientXY, evt, false, 0);
        }

        @Override
        protected void dispatchMouseEvent(String eventType, Element targetElement, Element relatedElement, Point clientXY, GraphicsNodeMouseEvent evt, boolean cancelable) {
            this.dispatchMouseEvent(eventType, targetElement, relatedElement, clientXY, evt, cancelable, 0);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void dispatchMouseEvent(String eventType, Element targetElement, Element relatedElement, Point clientXY, GraphicsNodeMouseEvent evt, boolean cancelable, int bubbleLimit) {
            if (this.ctx12.mouseCaptureTarget != null) {
                NodeEventTarget net = null;
                if (targetElement != null) {
                    for (net = (NodeEventTarget)targetElement; net != null && net != this.ctx12.mouseCaptureTarget; net = net.getParentNodeEventTarget()) {
                    }
                }
                if (net == null) {
                    targetElement = this.ctx12.mouseCaptureSendAll ? (Element)((Object)this.ctx12.mouseCaptureTarget) : null;
                }
            }
            if (targetElement != null) {
                Point screenXY = evt.getScreenPoint();
                DocumentEvent d = (DocumentEvent)((Object)targetElement.getOwnerDocument());
                DOMMouseEvent mouseEvt = (DOMMouseEvent)d.createEvent("MouseEvents");
                String modifiers = DOMUtilities.getModifiersList((int)evt.getLockState(), (int)evt.getModifiers());
                mouseEvt.initMouseEventNS("http://www.w3.org/2001/xml-events", eventType, true, cancelable, null, evt.getClickCount(), screenXY.x, screenXY.y, clientXY.x, clientXY.y, (short)(evt.getButton() - 1), (EventTarget)((Object)relatedElement), modifiers);
                mouseEvt.setBubbleLimit(bubbleLimit);
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
            if (this.ctx12.mouseCaptureTarget != null && this.ctx12.mouseCaptureAutoRelease && "mouseup".equals(eventType)) {
                this.ctx12.stopMouseCapture();
            }
        }

        static {
            Listener.putIdentifierKeyCode("U+0030", 48);
            Listener.putIdentifierKeyCode("U+0031", 49);
            Listener.putIdentifierKeyCode("U+0032", 50);
            Listener.putIdentifierKeyCode("U+0033", 51);
            Listener.putIdentifierKeyCode("U+0034", 52);
            Listener.putIdentifierKeyCode("U+0035", 53);
            Listener.putIdentifierKeyCode("U+0036", 54);
            Listener.putIdentifierKeyCode("U+0037", 55);
            Listener.putIdentifierKeyCode("U+0038", 56);
            Listener.putIdentifierKeyCode("U+0039", 57);
            Listener.putIdentifierKeyCode("Accept", 30);
            Listener.putIdentifierKeyCode("Again", 65481);
            Listener.putIdentifierKeyCode("U+0041", 65);
            Listener.putIdentifierKeyCode("AllCandidates", 256);
            Listener.putIdentifierKeyCode("Alphanumeric", 240);
            Listener.putIdentifierKeyCode("AltGraph", 65406);
            Listener.putIdentifierKeyCode("Alt", 18);
            Listener.putIdentifierKeyCode("U+0026", 150);
            Listener.putIdentifierKeyCode("U+0027", 222);
            Listener.putIdentifierKeyCode("U+002A", 151);
            Listener.putIdentifierKeyCode("U+0040", 512);
            Listener.putIdentifierKeyCode("U+005C", 92);
            Listener.putIdentifierKeyCode("U+0008", 8);
            Listener.putIdentifierKeyCode("U+0042", 66);
            Listener.putIdentifierKeyCode("U+0018", 3);
            Listener.putIdentifierKeyCode("CapsLock", 20);
            Listener.putIdentifierKeyCode("U+005E", 514);
            Listener.putIdentifierKeyCode("U+0043", 67);
            Listener.putIdentifierKeyCode("Clear", 12);
            Listener.putIdentifierKeyCode("CodeInput", 258);
            Listener.putIdentifierKeyCode("U+003A", 513);
            Listener.putIdentifierKeyCode("U+0301", 129);
            Listener.putIdentifierKeyCode("U+0306", 133);
            Listener.putIdentifierKeyCode("U+030C", 138);
            Listener.putIdentifierKeyCode("U+0327", 139);
            Listener.putIdentifierKeyCode("U+0302", 130);
            Listener.putIdentifierKeyCode("U+0308", 135);
            Listener.putIdentifierKeyCode("U+0307", 134);
            Listener.putIdentifierKeyCode("U+030B", 137);
            Listener.putIdentifierKeyCode("U+0300", 128);
            Listener.putIdentifierKeyCode("U+0345", 141);
            Listener.putIdentifierKeyCode("U+0304", 132);
            Listener.putIdentifierKeyCode("U+0328", 140);
            Listener.putIdentifierKeyCode("U+030A", 136);
            Listener.putIdentifierKeyCode("U+0303", 131);
            Listener.putIdentifierKeyCode("U+002C", 44);
            Listener.putIdentifierKeyCode("Compose", 65312);
            Listener.putIdentifierKeyCode("Control", 17);
            Listener.putIdentifierKeyCode("Convert", 28);
            Listener.putIdentifierKeyCode("Copy", 65485);
            Listener.putIdentifierKeyCode("Cut", 65489);
            Listener.putIdentifierKeyCode("U+007F", 127);
            Listener.putIdentifierKeyCode("U+0044", 68);
            Listener.putIdentifierKeyCode("U+0024", 515);
            Listener.putIdentifierKeyCode("Down", 40);
            Listener.putIdentifierKeyCode("U+0045", 69);
            Listener.putIdentifierKeyCode("End", 35);
            Listener.putIdentifierKeyCode("Enter", 10);
            Listener.putIdentifierKeyCode("U+003D", 61);
            Listener.putIdentifierKeyCode("U+001B", 27);
            Listener.putIdentifierKeyCode("U+20AC", 516);
            Listener.putIdentifierKeyCode("U+0021", 517);
            Listener.putIdentifierKeyCode("F10", 121);
            Listener.putIdentifierKeyCode("F11", 122);
            Listener.putIdentifierKeyCode("F12", 123);
            Listener.putIdentifierKeyCode("F13", 61440);
            Listener.putIdentifierKeyCode("F14", 61441);
            Listener.putIdentifierKeyCode("F15", 61442);
            Listener.putIdentifierKeyCode("F16", 61443);
            Listener.putIdentifierKeyCode("F17", 61444);
            Listener.putIdentifierKeyCode("F18", 61445);
            Listener.putIdentifierKeyCode("F19", 61446);
            Listener.putIdentifierKeyCode("F1", 112);
            Listener.putIdentifierKeyCode("F20", 61447);
            Listener.putIdentifierKeyCode("F21", 61448);
            Listener.putIdentifierKeyCode("F22", 61449);
            Listener.putIdentifierKeyCode("F23", 61450);
            Listener.putIdentifierKeyCode("F24", 61451);
            Listener.putIdentifierKeyCode("F2", 113);
            Listener.putIdentifierKeyCode("F3", 114);
            Listener.putIdentifierKeyCode("F4", 115);
            Listener.putIdentifierKeyCode("F5", 116);
            Listener.putIdentifierKeyCode("F6", 117);
            Listener.putIdentifierKeyCode("F7", 118);
            Listener.putIdentifierKeyCode("F8", 119);
            Listener.putIdentifierKeyCode("F9", 120);
            Listener.putIdentifierKeyCode("FinalMode", 24);
            Listener.putIdentifierKeyCode("Find", 65488);
            Listener.putIdentifierKeyCode("U+0046", 70);
            Listener.putIdentifierKeyCode("U+002E", 46);
            Listener.putIdentifierKeyCode("FullWidth", 243);
            Listener.putIdentifierKeyCode("U+0047", 71);
            Listener.putIdentifierKeyCode("U+0060", 192);
            Listener.putIdentifierKeyCode("U+003E", 160);
            Listener.putIdentifierKeyCode("HalfWidth", 244);
            Listener.putIdentifierKeyCode("U+0023", 520);
            Listener.putIdentifierKeyCode("Help", 156);
            Listener.putIdentifierKeyCode("Hiragana", 242);
            Listener.putIdentifierKeyCode("U+0048", 72);
            Listener.putIdentifierKeyCode("Home", 36);
            Listener.putIdentifierKeyCode("U+0049", 73);
            Listener.putIdentifierKeyCode("Insert", 155);
            Listener.putIdentifierKeyCode("U+00A1", 518);
            Listener.putIdentifierKeyCode("JapaneseHiragana", 260);
            Listener.putIdentifierKeyCode("JapaneseKatakana", 259);
            Listener.putIdentifierKeyCode("JapaneseRomaji", 261);
            Listener.putIdentifierKeyCode("U+004A", 74);
            Listener.putIdentifierKeyCode("KanaMode", 262);
            Listener.putIdentifierKeyCode("KanjiMode", 25);
            Listener.putIdentifierKeyCode("Katakana", 241);
            Listener.putIdentifierKeyCode("U+004B", 75);
            Listener.putIdentifierKeyCode("U+007B", 161);
            Listener.putIdentifierKeyCode("Left", 37);
            Listener.putIdentifierKeyCode("U+0028", 519);
            Listener.putIdentifierKeyCode("U+005B", 91);
            Listener.putIdentifierKeyCode("U+003C", 153);
            Listener.putIdentifierKeyCode("U+004C", 76);
            Listener.putIdentifierKeyCode("Meta", 157);
            Listener.putIdentifierKeyCode("Meta", 157);
            Listener.putIdentifierKeyCode("U+002D", 45);
            Listener.putIdentifierKeyCode("U+004D", 77);
            Listener.putIdentifierKeyCode("ModeChange", 31);
            Listener.putIdentifierKeyCode("U+004E", 78);
            Listener.putIdentifierKeyCode("Nonconvert", 29);
            Listener.putIdentifierKeyCode("NumLock", 144);
            Listener.putIdentifierKeyCode("NumLock", 144);
            Listener.putIdentifierKeyCode("U+004F", 79);
            Listener.putIdentifierKeyCode("PageDown", 34);
            Listener.putIdentifierKeyCode("PageUp", 33);
            Listener.putIdentifierKeyCode("Paste", 65487);
            Listener.putIdentifierKeyCode("Pause", 19);
            Listener.putIdentifierKeyCode("U+0050", 80);
            Listener.putIdentifierKeyCode("U+002B", 521);
            Listener.putIdentifierKeyCode("PreviousCandidate", 257);
            Listener.putIdentifierKeyCode("PrintScreen", 154);
            Listener.putIdentifierKeyCode("Props", 65482);
            Listener.putIdentifierKeyCode("U+0051", 81);
            Listener.putIdentifierKeyCode("U+0022", 152);
            Listener.putIdentifierKeyCode("U+007D", 162);
            Listener.putIdentifierKeyCode("Right", 39);
            Listener.putIdentifierKeyCode("U+0029", 522);
            Listener.putIdentifierKeyCode("U+005D", 93);
            Listener.putIdentifierKeyCode("U+0052", 82);
            Listener.putIdentifierKeyCode("RomanCharacters", 245);
            Listener.putIdentifierKeyCode("Scroll", 145);
            Listener.putIdentifierKeyCode("Scroll", 145);
            Listener.putIdentifierKeyCode("U+003B", 59);
            Listener.putIdentifierKeyCode("U+309A", 143);
            Listener.putIdentifierKeyCode("Shift", 16);
            Listener.putIdentifierKeyCode("Shift", 16);
            Listener.putIdentifierKeyCode("U+0053", 83);
            Listener.putIdentifierKeyCode("U+002F", 47);
            Listener.putIdentifierKeyCode("U+0020", 32);
            Listener.putIdentifierKeyCode("Stop", 65480);
            Listener.putIdentifierKeyCode("U+0009", 9);
            Listener.putIdentifierKeyCode("U+0054", 84);
            Listener.putIdentifierKeyCode("U+0055", 85);
            Listener.putIdentifierKeyCode("U+005F", 523);
            Listener.putIdentifierKeyCode("Undo", 65483);
            Listener.putIdentifierKeyCode("Unidentified", 0);
            Listener.putIdentifierKeyCode("Up", 38);
            Listener.putIdentifierKeyCode("U+0056", 86);
            Listener.putIdentifierKeyCode("U+3099", 142);
            Listener.putIdentifierKeyCode("U+0057", 87);
            Listener.putIdentifierKeyCode("U+0058", 88);
            Listener.putIdentifierKeyCode("U+0059", 89);
            Listener.putIdentifierKeyCode("U+005A", 90);
            Listener.putIdentifierKeyCode("U+0030", 96);
            Listener.putIdentifierKeyCode("U+0031", 97);
            Listener.putIdentifierKeyCode("U+0032", 98);
            Listener.putIdentifierKeyCode("U+0033", 99);
            Listener.putIdentifierKeyCode("U+0034", 100);
            Listener.putIdentifierKeyCode("U+0035", 101);
            Listener.putIdentifierKeyCode("U+0036", 102);
            Listener.putIdentifierKeyCode("U+0037", 103);
            Listener.putIdentifierKeyCode("U+0038", 104);
            Listener.putIdentifierKeyCode("U+0039", 105);
            Listener.putIdentifierKeyCode("U+002A", 106);
            Listener.putIdentifierKeyCode("Down", 225);
            Listener.putIdentifierKeyCode("U+002E", 110);
            Listener.putIdentifierKeyCode("Left", 226);
            Listener.putIdentifierKeyCode("U+002D", 109);
            Listener.putIdentifierKeyCode("U+002B", 107);
            Listener.putIdentifierKeyCode("Right", 227);
            Listener.putIdentifierKeyCode("U+002F", 111);
            Listener.putIdentifierKeyCode("Up", 224);
        }
    }
}

