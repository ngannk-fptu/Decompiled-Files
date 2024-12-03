/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SAXSVGDocumentFactory
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.anim.dom.SVGOMScriptElement
 *  org.apache.batik.dom.AbstractElement
 *  org.apache.batik.dom.GenericDOMImplementation
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.util.DOMUtilities
 *  org.apache.batik.dom.util.SAXDocumentFactory
 *  org.apache.batik.script.Interpreter
 *  org.apache.batik.script.InterpreterException
 *  org.apache.batik.script.ScriptEventWrapper
 *  org.apache.batik.util.EncodingUtilities
 *  org.apache.batik.util.ParsedURL
 *  org.apache.batik.util.RunnableQueue
 *  org.apache.batik.util.XMLResourceDescriptor
 *  org.apache.batik.w3c.dom.Location
 *  org.apache.batik.w3c.dom.Window
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.bridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMScriptElement;
import org.apache.batik.bridge.BaseScriptingEnvironment;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.Location;
import org.apache.batik.bridge.Messages;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.bridge.Window;
import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.SAXDocumentFactory;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.ScriptEventWrapper;
import org.apache.batik.util.EncodingUtilities;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.RunnableQueue;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGDocument;

public class ScriptingEnvironment
extends BaseScriptingEnvironment {
    public static final String[] SVG_EVENT_ATTRS = new String[]{"onabort", "onerror", "onresize", "onscroll", "onunload", "onzoom", "onbegin", "onend", "onrepeat", "onfocusin", "onfocusout", "onactivate", "onclick", "onmousedown", "onmouseup", "onmouseover", "onmouseout", "onmousemove", "onkeypress", "onkeydown", "onkeyup"};
    public static final String[] SVG_DOM_EVENT = new String[]{"SVGAbort", "SVGError", "SVGResize", "SVGScroll", "SVGUnload", "SVGZoom", "beginEvent", "endEvent", "repeatEvent", "DOMFocusIn", "DOMFocusOut", "DOMActivate", "click", "mousedown", "mouseup", "mouseover", "mouseout", "mousemove", "keypress", "keydown", "keyup"};
    protected Timer timer = new Timer(true);
    protected UpdateManager updateManager;
    protected RunnableQueue updateRunnableQueue;
    protected EventListener domNodeInsertedListener;
    protected EventListener domNodeRemovedListener;
    protected EventListener domAttrModifiedListener;
    protected EventListener svgAbortListener = new ScriptingEventListener("onabort");
    protected EventListener svgErrorListener = new ScriptingEventListener("onerror");
    protected EventListener svgResizeListener = new ScriptingEventListener("onresize");
    protected EventListener svgScrollListener = new ScriptingEventListener("onscroll");
    protected EventListener svgUnloadListener = new ScriptingEventListener("onunload");
    protected EventListener svgZoomListener = new ScriptingEventListener("onzoom");
    protected EventListener beginListener = new ScriptingEventListener("onbegin");
    protected EventListener endListener = new ScriptingEventListener("onend");
    protected EventListener repeatListener = new ScriptingEventListener("onrepeat");
    protected EventListener focusinListener = new ScriptingEventListener("onfocusin");
    protected EventListener focusoutListener = new ScriptingEventListener("onfocusout");
    protected EventListener activateListener = new ScriptingEventListener("onactivate");
    protected EventListener clickListener = new ScriptingEventListener("onclick");
    protected EventListener mousedownListener = new ScriptingEventListener("onmousedown");
    protected EventListener mouseupListener = new ScriptingEventListener("onmouseup");
    protected EventListener mouseoverListener = new ScriptingEventListener("onmouseover");
    protected EventListener mouseoutListener = new ScriptingEventListener("onmouseout");
    protected EventListener mousemoveListener = new ScriptingEventListener("onmousemove");
    protected EventListener keypressListener = new ScriptingEventListener("onkeypress");
    protected EventListener keydownListener = new ScriptingEventListener("onkeydown");
    protected EventListener keyupListener = new ScriptingEventListener("onkeyup");
    protected EventListener[] listeners = new EventListener[]{this.svgAbortListener, this.svgErrorListener, this.svgResizeListener, this.svgScrollListener, this.svgUnloadListener, this.svgZoomListener, this.beginListener, this.endListener, this.repeatListener, this.focusinListener, this.focusoutListener, this.activateListener, this.clickListener, this.mousedownListener, this.mouseupListener, this.mouseoverListener, this.mouseoutListener, this.mousemoveListener, this.keypressListener, this.keydownListener, this.keyupListener};
    Map attrToDOMEvent = new HashMap(SVG_EVENT_ATTRS.length);
    Map attrToListener = new HashMap(SVG_EVENT_ATTRS.length);

    public ScriptingEnvironment(BridgeContext ctx) {
        super(ctx);
        for (int i = 0; i < SVG_EVENT_ATTRS.length; ++i) {
            this.attrToDOMEvent.put(SVG_EVENT_ATTRS[i], SVG_DOM_EVENT[i]);
            this.attrToListener.put(SVG_EVENT_ATTRS[i], this.listeners[i]);
        }
        this.updateManager = ctx.getUpdateManager();
        this.updateRunnableQueue = this.updateManager.getUpdateRunnableQueue();
        this.addScriptingListeners(this.document.getDocumentElement());
        this.addDocumentListeners();
    }

    protected void addDocumentListeners() {
        this.domNodeInsertedListener = new DOMNodeInsertedListener();
        this.domNodeRemovedListener = new DOMNodeRemovedListener();
        this.domAttrModifiedListener = new DOMAttrModifiedListener();
        NodeEventTarget et = (NodeEventTarget)this.document;
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedListener, false, null);
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedListener, false, null);
        et.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedListener, false, null);
    }

    protected void removeDocumentListeners() {
        NodeEventTarget et = (NodeEventTarget)this.document;
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", this.domNodeInsertedListener, false);
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.domNodeRemovedListener, false);
        et.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMAttrModified", this.domAttrModifiedListener, false);
    }

    @Override
    protected org.apache.batik.bridge.Window createWindow(Interpreter interp, String lang) {
        return new Window(interp, lang);
    }

    public void runEventHandler(String script, Event evt, String lang, String desc) {
        Interpreter interpreter = this.getInterpreter(lang);
        if (interpreter == null) {
            return;
        }
        try {
            this.checkCompatibleScriptURL(lang, this.docPURL);
            Object event = evt instanceof ScriptEventWrapper ? ((ScriptEventWrapper)evt).getEventObject() : evt;
            interpreter.bindObject("event", event);
            interpreter.bindObject("evt", event);
            interpreter.evaluate((Reader)new StringReader(script), desc);
        }
        catch (IOException event) {
        }
        catch (InterpreterException ie) {
            this.handleInterpreterException(ie);
        }
        catch (SecurityException se) {
            this.handleSecurityException(se);
        }
    }

    public void interrupt() {
        this.timer.cancel();
        this.removeScriptingListeners(this.document.getDocumentElement());
        this.removeDocumentListeners();
    }

    public void addScriptingListeners(Node node) {
        if (node.getNodeType() == 1) {
            this.addScriptingListenersOn((Element)node);
        }
        for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
            this.addScriptingListeners(n);
        }
    }

    protected void addScriptingListenersOn(Element elt) {
        NodeEventTarget target = (NodeEventTarget)elt;
        if ("http://www.w3.org/2000/svg".equals(elt.getNamespaceURI())) {
            if ("svg".equals(elt.getLocalName())) {
                if (elt.hasAttributeNS(null, "onabort")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGAbort", this.svgAbortListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onerror")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGError", this.svgErrorListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onresize")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGResize", this.svgResizeListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onscroll")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGScroll", this.svgScrollListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onunload")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGUnload", this.svgUnloadListener, false, null);
                }
                if (elt.hasAttributeNS(null, "onzoom")) {
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "SVGZoom", this.svgZoomListener, false, null);
                }
            } else {
                String name = elt.getLocalName();
                if (name.equals("set") || name.startsWith("animate")) {
                    if (elt.hasAttributeNS(null, "onbegin")) {
                        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "beginEvent", this.beginListener, false, null);
                    }
                    if (elt.hasAttributeNS(null, "onend")) {
                        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "endEvent", this.endListener, false, null);
                    }
                    if (elt.hasAttributeNS(null, "onrepeat")) {
                        target.addEventListenerNS("http://www.w3.org/2001/xml-events", "repeatEvent", this.repeatListener, false, null);
                    }
                    return;
                }
            }
        }
        if (elt.hasAttributeNS(null, "onfocusin")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.focusinListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onfocusout")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.focusoutListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onactivate")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMActivate", this.activateListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onclick")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.clickListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmousedown")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.mousedownListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmouseup")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.mouseupListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmouseover")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmouseout")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onmousemove")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.mousemoveListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onkeypress")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.keypressListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onkeydown")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.keydownListener, false, null);
        }
        if (elt.hasAttributeNS(null, "onkeyup")) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.keyupListener, false, null);
        }
    }

    protected void removeScriptingListeners(Node node) {
        if (node.getNodeType() == 1) {
            this.removeScriptingListenersOn((Element)node);
        }
        for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
            this.removeScriptingListeners(n);
        }
    }

    protected void removeScriptingListenersOn(Element elt) {
        NodeEventTarget target = (NodeEventTarget)elt;
        if ("http://www.w3.org/2000/svg".equals(elt.getNamespaceURI())) {
            if ("svg".equals(elt.getLocalName())) {
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGAbort", this.svgAbortListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGError", this.svgErrorListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGResize", this.svgResizeListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGScroll", this.svgScrollListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGUnload", this.svgUnloadListener, false);
                target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "SVGZoom", this.svgZoomListener, false);
            } else {
                String name = elt.getLocalName();
                if (name.equals("set") || name.startsWith("animate")) {
                    target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "beginEvent", this.beginListener, false);
                    target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "endEvent", this.endListener, false);
                    target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "repeatEvent", this.repeatListener, false);
                    return;
                }
            }
        }
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusIn", this.focusinListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMFocusOut", this.focusoutListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMActivate", this.activateListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "click", this.clickListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousedown", this.mousedownListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseup", this.mouseupListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", this.mouseoverListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", this.mouseoutListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "mousemove", this.mousemoveListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keypress", this.keypressListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keydown", this.keydownListener, false);
        target.removeEventListenerNS("http://www.w3.org/2001/xml-events", "keyup", this.keyupListener, false);
    }

    protected void updateScriptingListeners(Element elt, String attr) {
        String domEvt = (String)this.attrToDOMEvent.get(attr);
        if (domEvt == null) {
            return;
        }
        EventListener listener = (EventListener)this.attrToListener.get(attr);
        NodeEventTarget target = (NodeEventTarget)elt;
        if (elt.hasAttributeNS(null, attr)) {
            target.addEventListenerNS("http://www.w3.org/2001/xml-events", domEvt, listener, false, null);
        } else {
            target.removeEventListenerNS("http://www.w3.org/2001/xml-events", domEvt, listener, false);
        }
    }

    protected class ScriptingEventListener
    implements EventListener {
        protected String attribute;

        public ScriptingEventListener(String attr) {
            this.attribute = attr;
        }

        @Override
        public void handleEvent(Event evt) {
            Element elt = (Element)((Object)evt.getCurrentTarget());
            String script = elt.getAttributeNS(null, this.attribute);
            if (script.length() == 0) {
                return;
            }
            DocumentLoader dl = ScriptingEnvironment.this.bridgeContext.getDocumentLoader();
            SVGDocument d = (SVGDocument)elt.getOwnerDocument();
            int line = dl.getLineNumber(elt);
            String desc = Messages.formatMessage("BaseScriptingEnvironment.constant.event.script.description", new Object[]{d.getURL(), this.attribute, line});
            Element e = elt;
            while (!(e == null || "http://www.w3.org/2000/svg".equals(e.getNamespaceURI()) && "svg".equals(e.getLocalName()))) {
                e = SVGUtilities.getParentElement(e);
            }
            if (e == null) {
                return;
            }
            String lang = e.getAttributeNS(null, "contentScriptType");
            ScriptingEnvironment.this.runEventHandler(script, evt, lang, desc);
        }
    }

    protected class DOMAttrModifiedListener
    implements EventListener {
        protected DOMAttrModifiedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            MutationEvent me = (MutationEvent)evt;
            if (me.getAttrChange() != 1) {
                ScriptingEnvironment.this.updateScriptingListeners((Element)((Object)me.getTarget()), me.getAttrName());
            }
        }
    }

    protected class DOMNodeRemovedListener
    implements EventListener {
        protected DOMNodeRemovedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            ScriptingEnvironment.this.removeScriptingListeners((Node)((Object)evt.getTarget()));
        }
    }

    protected class DOMNodeInsertedListener
    implements EventListener {
        protected LinkedList toExecute = new LinkedList();

        protected DOMNodeInsertedListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            Node n = (Node)((Object)evt.getTarget());
            ScriptingEnvironment.this.addScriptingListeners(n);
            this.gatherScriptElements(n);
            while (!this.toExecute.isEmpty()) {
                ScriptingEnvironment.this.loadScript((AbstractElement)this.toExecute.removeFirst());
            }
        }

        protected void gatherScriptElements(Node n) {
            if (n.getNodeType() == 1) {
                if (n instanceof SVGOMScriptElement) {
                    this.toExecute.add(n);
                } else {
                    for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                        this.gatherScriptElements(n);
                    }
                }
            }
        }
    }

    protected class Window
    implements org.apache.batik.bridge.Window {
        protected Interpreter interpreter;
        protected String language;
        protected Location location;
        static final String DEFLATE = "deflate";
        static final String GZIP = "gzip";
        static final String UTF_8 = "UTF-8";

        public Window(Interpreter interp, String lang) {
            this.interpreter = interp;
            this.language = lang;
        }

        @Override
        public Object setInterval(String script, long interval) {
            IntervalScriptTimerTask tt = new IntervalScriptTimerTask(script);
            ScriptingEnvironment.this.timer.schedule((TimerTask)tt, interval, interval);
            return tt;
        }

        @Override
        public Object setInterval(Runnable r, long interval) {
            IntervalRunnableTimerTask tt = new IntervalRunnableTimerTask(r);
            ScriptingEnvironment.this.timer.schedule((TimerTask)tt, interval, interval);
            return tt;
        }

        @Override
        public void clearInterval(Object interval) {
            if (interval == null) {
                return;
            }
            ((TimerTask)interval).cancel();
        }

        @Override
        public Object setTimeout(String script, long timeout) {
            TimeoutScriptTimerTask tt = new TimeoutScriptTimerTask(script);
            ScriptingEnvironment.this.timer.schedule((TimerTask)tt, timeout);
            return tt;
        }

        @Override
        public Object setTimeout(Runnable r, long timeout) {
            TimeoutRunnableTimerTask tt = new TimeoutRunnableTimerTask(r);
            ScriptingEnvironment.this.timer.schedule((TimerTask)tt, timeout);
            return tt;
        }

        @Override
        public void clearTimeout(Object timeout) {
            if (timeout == null) {
                return;
            }
            ((TimerTask)timeout).cancel();
        }

        @Override
        public Node parseXML(String text, Document doc) {
            String uri;
            Node res;
            SAXSVGDocumentFactory df = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
            URL urlObj = null;
            if (doc instanceof SVGOMDocument) {
                urlObj = ((SVGOMDocument)doc).getURLObject();
            }
            if (urlObj == null) {
                urlObj = ((SVGOMDocument)ScriptingEnvironment.this.bridgeContext.getDocument()).getURLObject();
            }
            if ((res = DOMUtilities.parseXML((String)text, (Document)doc, (String)(uri = urlObj == null ? "" : urlObj.toString()), null, null, (SAXDocumentFactory)df)) != null) {
                return res;
            }
            if (doc instanceof SVGOMDocument) {
                HashMap<String, String> prefixes = new HashMap<String, String>();
                prefixes.put("xmlns", "http://www.w3.org/2000/xmlns/");
                prefixes.put("xmlns:xlink", "http://www.w3.org/1999/xlink");
                res = DOMUtilities.parseXML((String)text, (Document)doc, (String)uri, prefixes, (String)"svg", (SAXDocumentFactory)df);
                if (res != null) {
                    return res;
                }
            }
            SAXDocumentFactory sdf = doc != null ? new SAXDocumentFactory(doc.getImplementation(), XMLResourceDescriptor.getXMLParserClassName()) : new SAXDocumentFactory((DOMImplementation)new GenericDOMImplementation(), XMLResourceDescriptor.getXMLParserClassName());
            return DOMUtilities.parseXML((String)text, (Document)doc, (String)uri, null, null, (SAXDocumentFactory)sdf);
        }

        @Override
        public String printNode(Node n) {
            try {
                StringWriter writer = new StringWriter();
                DOMUtilities.writeNode((Node)n, (Writer)writer);
                ((Writer)writer).close();
                return ((Object)writer).toString();
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void getURL(String uri, Window.URLResponseHandler h) {
            this.getURL(uri, h, null);
        }

        @Override
        public void getURL(final String uri, final Window.URLResponseHandler h, final String enc) {
            Thread t = new Thread(){

                @Override
                public void run() {
                    try {
                        int read;
                        Reader r;
                        ParsedURL burl = ((SVGOMDocument)ScriptingEnvironment.this.document).getParsedURL();
                        final ParsedURL purl = new ParsedURL(burl, uri);
                        String e = null;
                        if (enc != null) {
                            e = EncodingUtilities.javaEncoding((String)enc);
                            e = e == null ? enc : e;
                        }
                        InputStream is = purl.openStream();
                        if (e == null) {
                            r = new InputStreamReader(is);
                        } else {
                            try {
                                r = new InputStreamReader(is, e);
                            }
                            catch (UnsupportedEncodingException uee) {
                                r = new InputStreamReader(is);
                            }
                        }
                        r = new BufferedReader(r);
                        final StringBuffer sb = new StringBuffer();
                        char[] buf = new char[4096];
                        while ((read = r.read(buf, 0, buf.length)) != -1) {
                            sb.append(buf, 0, read);
                        }
                        r.close();
                        ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable(){

                            @Override
                            public void run() {
                                block2: {
                                    try {
                                        h.getURLDone(true, purl.getContentType(), sb.toString());
                                    }
                                    catch (Exception e) {
                                        if (ScriptingEnvironment.this.userAgent == null) break block2;
                                        ScriptingEnvironment.this.userAgent.displayError(e);
                                    }
                                }
                            }
                        });
                    }
                    catch (Exception e) {
                        if (e instanceof SecurityException) {
                            ScriptingEnvironment.this.userAgent.displayError(e);
                        }
                        ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable(){

                            @Override
                            public void run() {
                                block2: {
                                    try {
                                        h.getURLDone(false, null, null);
                                    }
                                    catch (Exception e) {
                                        if (ScriptingEnvironment.this.userAgent == null) break block2;
                                        ScriptingEnvironment.this.userAgent.displayError(e);
                                    }
                                }
                            }
                        });
                    }
                }
            };
            t.setPriority(1);
            t.start();
        }

        @Override
        public void postURL(String uri, String content, Window.URLResponseHandler h) {
            this.postURL(uri, content, h, "text/plain", null);
        }

        @Override
        public void postURL(String uri, String content, Window.URLResponseHandler h, String mimeType) {
            this.postURL(uri, content, h, mimeType, null);
        }

        @Override
        public void postURL(final String uri, final String content, final Window.URLResponseHandler h, final String mimeType, final String fEnc) {
            Thread t = new Thread(){

                @Override
                public void run() {
                    try {
                        int read;
                        String base = ScriptingEnvironment.this.document.getDocumentURI();
                        URL url = base == null ? new URL(uri) : new URL(new URL(base), uri);
                        final URLConnection conn = url.openConnection();
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.setRequestProperty("Content-Type", mimeType);
                        OutputStream os = conn.getOutputStream();
                        String e = null;
                        String enc = fEnc;
                        if (enc != null) {
                            if (enc.startsWith(Window.DEFLATE)) {
                                os = new DeflaterOutputStream(os);
                                enc = enc.length() > Window.DEFLATE.length() ? enc.substring(Window.DEFLATE.length() + 1) : "";
                                conn.setRequestProperty("Content-Encoding", Window.DEFLATE);
                            }
                            if (enc.startsWith(Window.GZIP)) {
                                os = new GZIPOutputStream(os);
                                enc = enc.length() > Window.GZIP.length() ? enc.substring(Window.GZIP.length() + 1) : "";
                                conn.setRequestProperty("Content-Encoding", Window.DEFLATE);
                            }
                            if (enc.length() != 0) {
                                e = EncodingUtilities.javaEncoding((String)enc);
                                if (e == null) {
                                    e = Window.UTF_8;
                                }
                            } else {
                                e = Window.UTF_8;
                            }
                        }
                        OutputStreamWriter w = e == null ? new OutputStreamWriter(os) : new OutputStreamWriter(os, e);
                        w.write(content);
                        ((Writer)w).flush();
                        ((Writer)w).close();
                        os.close();
                        InputStream is = conn.getInputStream();
                        e = Window.UTF_8;
                        Reader r = e == null ? new InputStreamReader(is) : new InputStreamReader(is, e);
                        r = new BufferedReader(r);
                        final StringBuffer sb = new StringBuffer();
                        char[] buf = new char[4096];
                        while ((read = r.read(buf, 0, buf.length)) != -1) {
                            sb.append(buf, 0, read);
                        }
                        r.close();
                        ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable(){

                            @Override
                            public void run() {
                                block2: {
                                    try {
                                        h.getURLDone(true, conn.getContentType(), sb.toString());
                                    }
                                    catch (Exception e) {
                                        if (ScriptingEnvironment.this.userAgent == null) break block2;
                                        ScriptingEnvironment.this.userAgent.displayError(e);
                                    }
                                }
                            }
                        });
                    }
                    catch (Exception e) {
                        if (e instanceof SecurityException) {
                            ScriptingEnvironment.this.userAgent.displayError(e);
                        }
                        ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable(){

                            @Override
                            public void run() {
                                block2: {
                                    try {
                                        h.getURLDone(false, null, null);
                                    }
                                    catch (Exception e) {
                                        if (ScriptingEnvironment.this.userAgent == null) break block2;
                                        ScriptingEnvironment.this.userAgent.displayError(e);
                                    }
                                }
                            }
                        });
                    }
                }
            };
            t.setPriority(1);
            t.start();
        }

        @Override
        public void alert(String message) {
            if (ScriptingEnvironment.this.userAgent != null) {
                ScriptingEnvironment.this.userAgent.showAlert(message);
            }
        }

        @Override
        public boolean confirm(String message) {
            if (ScriptingEnvironment.this.userAgent != null) {
                return ScriptingEnvironment.this.userAgent.showConfirm(message);
            }
            return false;
        }

        @Override
        public String prompt(String message) {
            if (ScriptingEnvironment.this.userAgent != null) {
                return ScriptingEnvironment.this.userAgent.showPrompt(message);
            }
            return null;
        }

        @Override
        public String prompt(String message, String defVal) {
            if (ScriptingEnvironment.this.userAgent != null) {
                return ScriptingEnvironment.this.userAgent.showPrompt(message, defVal);
            }
            return null;
        }

        @Override
        public BridgeContext getBridgeContext() {
            return ScriptingEnvironment.this.bridgeContext;
        }

        @Override
        public Interpreter getInterpreter() {
            return this.interpreter;
        }

        public org.apache.batik.w3c.dom.Window getParent() {
            return null;
        }

        public org.apache.batik.w3c.dom.Location getLocation() {
            if (this.location == null) {
                this.location = new Location(ScriptingEnvironment.this.bridgeContext);
            }
            return this.location;
        }

        protected class TimeoutRunnableTimerTask
        extends TimerTask {
            private Runnable r;

            public TimeoutRunnableTimerTask(Runnable r) {
                this.r = r;
            }

            @Override
            public void run() {
                ScriptingEnvironment.this.updateRunnableQueue.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        block2: {
                            try {
                                TimeoutRunnableTimerTask.this.r.run();
                            }
                            catch (Exception e) {
                                if (ScriptingEnvironment.this.userAgent == null) break block2;
                                ScriptingEnvironment.this.userAgent.displayError(e);
                            }
                        }
                    }
                });
            }
        }

        protected class TimeoutScriptTimerTask
        extends TimerTask {
            private String script;

            public TimeoutScriptTimerTask(String script) {
                this.script = script;
            }

            @Override
            public void run() {
                ScriptingEnvironment.this.updateRunnableQueue.invokeLater((Runnable)new EvaluateRunnable(this.script, Window.this.interpreter));
            }
        }

        protected class IntervalRunnableTimerTask
        extends TimerTask {
            protected EvaluateRunnableRunnable eihr;

            public IntervalRunnableTimerTask(Runnable r) {
                this.eihr = new EvaluateRunnableRunnable(r);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                EvaluateRunnableRunnable evaluateRunnableRunnable = this.eihr;
                synchronized (evaluateRunnableRunnable) {
                    if (this.eihr.count > 1) {
                        return;
                    }
                    ++this.eihr.count;
                }
                ScriptingEnvironment.this.updateRunnableQueue.invokeLater((Runnable)this.eihr);
                evaluateRunnableRunnable = this.eihr;
                synchronized (evaluateRunnableRunnable) {
                    if (this.eihr.error) {
                        this.cancel();
                    }
                }
            }
        }

        protected class IntervalScriptTimerTask
        extends TimerTask {
            protected EvaluateIntervalRunnable eir;

            public IntervalScriptTimerTask(String script) {
                this.eir = new EvaluateIntervalRunnable(script, Window.this.interpreter);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                Object object = this.eir;
                synchronized (object) {
                    if (this.eir.count > 1) {
                        return;
                    }
                    ++this.eir.count;
                }
                object = ScriptingEnvironment.this.updateRunnableQueue.getIteratorLock();
                synchronized (object) {
                    if (ScriptingEnvironment.this.updateRunnableQueue.getThread() == null) {
                        this.cancel();
                        return;
                    }
                    ScriptingEnvironment.this.updateRunnableQueue.invokeLater((Runnable)this.eir);
                }
                object = this.eir;
                synchronized (object) {
                    if (this.eir.error) {
                        this.cancel();
                    }
                }
            }
        }
    }

    protected class EvaluateRunnableRunnable
    implements Runnable {
        public int count;
        public boolean error;
        protected Runnable runnable;

        public EvaluateRunnableRunnable(Runnable r) {
            this.runnable = r;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            EvaluateRunnableRunnable evaluateRunnableRunnable = this;
            synchronized (evaluateRunnableRunnable) {
                if (this.error) {
                    return;
                }
                --this.count;
            }
            try {
                this.runnable.run();
            }
            catch (Exception e) {
                if (ScriptingEnvironment.this.userAgent != null) {
                    ScriptingEnvironment.this.userAgent.displayError(e);
                } else {
                    e.printStackTrace();
                }
                EvaluateRunnableRunnable evaluateRunnableRunnable2 = this;
                synchronized (evaluateRunnableRunnable2) {
                    this.error = true;
                }
            }
        }
    }

    protected class EvaluateIntervalRunnable
    implements Runnable {
        public int count;
        public boolean error;
        protected Interpreter interpreter;
        protected String script;

        public EvaluateIntervalRunnable(String s, Interpreter interp) {
            this.interpreter = interp;
            this.script = s;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            EvaluateIntervalRunnable evaluateIntervalRunnable = this;
            synchronized (evaluateIntervalRunnable) {
                if (this.error) {
                    return;
                }
                --this.count;
            }
            try {
                this.interpreter.evaluate(this.script);
            }
            catch (InterpreterException ie) {
                ScriptingEnvironment.this.handleInterpreterException(ie);
                EvaluateIntervalRunnable evaluateIntervalRunnable2 = this;
                synchronized (evaluateIntervalRunnable2) {
                    this.error = true;
                }
            }
            catch (Exception e) {
                if (ScriptingEnvironment.this.userAgent != null) {
                    ScriptingEnvironment.this.userAgent.displayError(e);
                } else {
                    e.printStackTrace();
                }
                EvaluateIntervalRunnable evaluateIntervalRunnable3 = this;
                synchronized (evaluateIntervalRunnable3) {
                    this.error = true;
                }
            }
        }
    }

    protected class EvaluateRunnable
    implements Runnable {
        protected Interpreter interpreter;
        protected String script;

        public EvaluateRunnable(String s, Interpreter interp) {
            this.interpreter = interp;
            this.script = s;
        }

        @Override
        public void run() {
            try {
                this.interpreter.evaluate(this.script);
            }
            catch (InterpreterException ie) {
                ScriptingEnvironment.this.handleInterpreterException(ie);
            }
        }
    }
}

