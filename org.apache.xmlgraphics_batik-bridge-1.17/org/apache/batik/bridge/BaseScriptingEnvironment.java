/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractElement
 *  org.apache.batik.dom.events.AbstractEvent
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.script.Interpreter
 *  org.apache.batik.script.InterpreterException
 *  org.apache.batik.script.ScriptEventWrapper
 *  org.apache.batik.util.ParsedURL
 *  org.apache.batik.w3c.dom.Location
 *  org.apache.batik.w3c.dom.Window
 *  org.w3c.dom.svg.EventListenerInitializer
 *  org.w3c.dom.svg.SVGDocument
 *  org.w3c.dom.svg.SVGSVGElement
 */
package org.apache.batik.bridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.Manifest;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeExtension;
import org.apache.batik.bridge.DocumentJarClassLoader;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.Messages;
import org.apache.batik.bridge.ScriptHandler;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.Window;
import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.events.AbstractEvent;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterException;
import org.apache.batik.script.ScriptEventWrapper;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.w3c.dom.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.svg.EventListenerInitializer;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

public class BaseScriptingEnvironment {
    public static final String INLINE_SCRIPT_DESCRIPTION = "BaseScriptingEnvironment.constant.inline.script.description";
    public static final String EVENT_SCRIPT_DESCRIPTION = "BaseScriptingEnvironment.constant.event.script.description";
    protected static final String EVENT_NAME = "event";
    protected static final String ALTERNATE_EVENT_NAME = "evt";
    protected static final String APPLICATION_ECMASCRIPT = "application/ecmascript";
    protected BridgeContext bridgeContext;
    protected UserAgent userAgent;
    protected Document document;
    protected ParsedURL docPURL;
    protected Set languages = new HashSet();
    protected Interpreter interpreter;
    protected Map windowObjects = new HashMap();
    protected WeakHashMap executedScripts = new WeakHashMap();

    public static boolean isDynamicDocument(BridgeContext ctx, Document doc) {
        Element elt = doc.getDocumentElement();
        if (elt != null && "http://www.w3.org/2000/svg".equals(elt.getNamespaceURI())) {
            if (elt.getAttributeNS(null, "onabort").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onerror").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onresize").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onunload").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onscroll").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onzoom").length() > 0) {
                return true;
            }
            return BaseScriptingEnvironment.isDynamicElement(ctx, doc.getDocumentElement());
        }
        return false;
    }

    public static boolean isDynamicElement(BridgeContext ctx, Element elt) {
        List bridgeExtensions = ctx.getBridgeExtensions(elt.getOwnerDocument());
        return BaseScriptingEnvironment.isDynamicElement(elt, ctx, bridgeExtensions);
    }

    public static boolean isDynamicElement(Element elt, BridgeContext ctx, List bridgeExtensions) {
        for (Object bridgeExtension1 : bridgeExtensions) {
            BridgeExtension bridgeExtension = (BridgeExtension)bridgeExtension1;
            if (!bridgeExtension.isDynamicElement(elt)) continue;
            return true;
        }
        if ("http://www.w3.org/2000/svg".equals(elt.getNamespaceURI())) {
            if (elt.getAttributeNS(null, "onkeyup").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onkeydown").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onkeypress").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onload").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onerror").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onactivate").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onclick").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onfocusin").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onfocusout").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmousedown").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmousemove").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmouseout").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmouseover").length() > 0) {
                return true;
            }
            if (elt.getAttributeNS(null, "onmouseup").length() > 0) {
                return true;
            }
        }
        for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1 || !BaseScriptingEnvironment.isDynamicElement(ctx, (Element)n)) continue;
            return true;
        }
        return false;
    }

    public BaseScriptingEnvironment(BridgeContext ctx) {
        this.bridgeContext = ctx;
        this.document = ctx.getDocument();
        this.docPURL = new ParsedURL(((SVGDocument)this.document).getURL());
        this.userAgent = this.bridgeContext.getUserAgent();
    }

    public org.apache.batik.bridge.Window getWindow(Interpreter interp, String lang) {
        org.apache.batik.bridge.Window w = (org.apache.batik.bridge.Window)this.windowObjects.get(interp);
        if (w == null) {
            w = interp == null ? new Window(null, null) : this.createWindow(interp, lang);
            this.windowObjects.put(interp, w);
        }
        return w;
    }

    public org.apache.batik.bridge.Window getWindow() {
        return this.getWindow(null, null);
    }

    protected org.apache.batik.bridge.Window createWindow(Interpreter interp, String lang) {
        return new Window(interp, lang);
    }

    public Interpreter getInterpreter() {
        if (this.interpreter != null) {
            return this.interpreter;
        }
        SVGSVGElement root = (SVGSVGElement)this.document.getDocumentElement();
        String lang = root.getContentScriptType();
        return this.getInterpreter(lang);
    }

    public Interpreter getInterpreter(String lang) {
        this.interpreter = this.bridgeContext.getInterpreter(lang);
        if (this.interpreter == null) {
            if (this.languages.contains(lang)) {
                return null;
            }
            this.languages.add(lang);
            return null;
        }
        if (!this.languages.contains(lang)) {
            this.languages.add(lang);
            this.initializeEnvironment(this.interpreter, lang);
        }
        return this.interpreter;
    }

    public void initializeEnvironment(Interpreter interp, String lang) {
        interp.bindObject("window", (Object)this.getWindow(interp, lang));
    }

    public void loadScripts() {
        NodeList scripts = this.document.getElementsByTagNameNS("http://www.w3.org/2000/svg", "script");
        int len = scripts.getLength();
        for (int i = 0; i < len; ++i) {
            AbstractElement script = (AbstractElement)scripts.item(i);
            this.loadScript(script);
        }
    }

    protected void loadScript(AbstractElement script) {
        block42: {
            if (this.executedScripts.containsKey(script)) {
                return;
            }
            Object n = script;
            do {
                if ((n = n.getParentNode()) != null) continue;
                return;
            } while (n.getNodeType() != 9);
            String type = script.getAttributeNS(null, "type");
            if (type.length() == 0) {
                type = "text/ecmascript";
            }
            if (type.equals("application/java-archive")) {
                block41: {
                    try {
                        String href = XLinkSupport.getXLinkHref((Element)script);
                        ParsedURL purl = new ParsedURL(script.getBaseURI(), href);
                        this.checkCompatibleScriptURL(type, purl);
                        URL docURL = null;
                        try {
                            docURL = new URL(this.docPURL.toString());
                        }
                        catch (MalformedURLException malformedURLException) {
                            // empty catch block
                        }
                        DocumentJarClassLoader cll = new DocumentJarClassLoader(new URL(purl.toString()), docURL);
                        URL url = cll.findResource("META-INF/MANIFEST.MF");
                        if (url == null) {
                            return;
                        }
                        Manifest man = new Manifest(url.openStream());
                        this.executedScripts.put(script, null);
                        String sh = man.getMainAttributes().getValue("Script-Handler");
                        if (sh != null) {
                            ScriptHandler h = (ScriptHandler)cll.loadClass(sh).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                            h.run(this.document, this.getWindow());
                        }
                        if ((sh = man.getMainAttributes().getValue("SVG-Handler-Class")) != null) {
                            EventListenerInitializer initializer = (EventListenerInitializer)cll.loadClass(sh).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                            this.getWindow();
                            initializer.initializeEventListeners((SVGDocument)this.document);
                        }
                    }
                    catch (Exception e) {
                        if (this.userAgent == null) break block41;
                        this.userAgent.displayError(e);
                    }
                }
                return;
            }
            Interpreter interpreter = this.getInterpreter(type);
            if (interpreter == null) {
                return;
            }
            try {
                String href = XLinkSupport.getXLinkHref((Element)script);
                String desc = null;
                Reader reader = null;
                if (href.length() > 0) {
                    desc = href;
                    ParsedURL purl = new ParsedURL(script.getBaseURI(), href);
                    this.checkCompatibleScriptURL(type, purl);
                    InputStream is = purl.openStream();
                    String mediaType = purl.getContentTypeMediaType();
                    String enc = purl.getContentTypeCharset();
                    if (enc != null) {
                        try {
                            reader = new InputStreamReader(is, enc);
                        }
                        catch (UnsupportedEncodingException uee) {
                            enc = null;
                        }
                    }
                    if (reader == null) {
                        if (APPLICATION_ECMASCRIPT.equals(mediaType)) {
                            if (purl.hasContentTypeParameter("version")) {
                                return;
                            }
                            PushbackInputStream pbis = new PushbackInputStream(is, 8);
                            byte[] buf = new byte[4];
                            int read = pbis.read(buf);
                            if (read > 0) {
                                pbis.unread(buf, 0, read);
                                if (read >= 2) {
                                    if (buf[0] == -1 && buf[1] == -2) {
                                        if (read >= 4 && buf[2] == 0 && buf[3] == 0) {
                                            enc = "UTF32-LE";
                                            pbis.skip(4L);
                                        } else {
                                            enc = "UTF-16LE";
                                            pbis.skip(2L);
                                        }
                                    } else if (buf[0] == -2 && buf[1] == -1) {
                                        enc = "UTF-16BE";
                                        pbis.skip(2L);
                                    } else if (read >= 3 && buf[0] == -17 && buf[1] == -69 && buf[2] == -65) {
                                        enc = "UTF-8";
                                        pbis.skip(3L);
                                    } else if (read >= 4 && buf[0] == 0 && buf[1] == 0 && buf[2] == -2 && buf[3] == -1) {
                                        enc = "UTF-32BE";
                                        pbis.skip(4L);
                                    }
                                }
                                if (enc == null) {
                                    enc = "UTF-8";
                                }
                            }
                            reader = new InputStreamReader((InputStream)pbis, enc);
                        } else {
                            reader = new InputStreamReader(is);
                        }
                    }
                } else {
                    Node n2;
                    this.checkCompatibleScriptURL(type, this.docPURL);
                    DocumentLoader dl = this.bridgeContext.getDocumentLoader();
                    AbstractElement e = script;
                    SVGDocument d = (SVGDocument)e.getOwnerDocument();
                    int line = dl.getLineNumber((Element)script);
                    desc = Messages.formatMessage(INLINE_SCRIPT_DESCRIPTION, new Object[]{d.getURL(), "<" + script.getNodeName() + ">", line});
                    if (n2 != null) {
                        StringBuffer sb = new StringBuffer();
                        for (n2 = script.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                            if (n2.getNodeType() != 4 && n2.getNodeType() != 3) continue;
                            sb.append(n2.getNodeValue());
                        }
                        reader = new StringReader(sb.toString());
                    } else {
                        return;
                    }
                }
                this.executedScripts.put(script, null);
                interpreter.evaluate(reader, desc);
            }
            catch (IOException e) {
                if (this.userAgent != null) {
                    this.userAgent.displayError(e);
                }
                return;
            }
            catch (InterpreterException e) {
                System.err.println("InterpExcept: " + (Object)((Object)e));
                this.handleInterpreterException(e);
                return;
            }
            catch (SecurityException e) {
                if (this.userAgent == null) break block42;
                this.userAgent.displayError(e);
            }
        }
    }

    protected void checkCompatibleScriptURL(String scriptType, ParsedURL scriptPURL) {
        this.userAgent.checkLoadScript(scriptType, scriptPURL, this.docPURL);
    }

    public void dispatchSVGLoadEvent() {
        SVGSVGElement root = (SVGSVGElement)this.document.getDocumentElement();
        String lang = root.getContentScriptType();
        long documentStartTime = System.currentTimeMillis();
        this.bridgeContext.getAnimationEngine().start(documentStartTime);
        this.dispatchSVGLoad((Element)root, true, lang);
    }

    protected void dispatchSVGLoad(Element elt, boolean checkCanRun, String lang) {
        for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            this.dispatchSVGLoad((Element)n, checkCanRun, lang);
        }
        DocumentEvent de = (DocumentEvent)((Object)elt.getOwnerDocument());
        AbstractEvent ev = (AbstractEvent)de.createEvent("SVGEvents");
        String type = this.bridgeContext.isSVG12() ? "load" : "SVGLoad";
        ev.initEventNS("http://www.w3.org/2001/xml-events", type, false, false);
        NodeEventTarget t = (NodeEventTarget)elt;
        final String s = elt.getAttributeNS(null, "onload");
        if (s.length() == 0) {
            t.dispatchEvent((Event)ev);
            return;
        }
        final Interpreter interp = this.getInterpreter();
        if (interp == null) {
            t.dispatchEvent((Event)ev);
            return;
        }
        if (checkCanRun) {
            this.checkCompatibleScriptURL(lang, this.docPURL);
            checkCanRun = false;
        }
        DocumentLoader dl = this.bridgeContext.getDocumentLoader();
        SVGDocument d = (SVGDocument)elt.getOwnerDocument();
        int line = dl.getLineNumber(elt);
        final String desc = Messages.formatMessage(EVENT_SCRIPT_DESCRIPTION, new Object[]{d.getURL(), "onload", line});
        EventListener l = new EventListener(){

            @Override
            public void handleEvent(Event evt) {
                try {
                    Object event = evt instanceof ScriptEventWrapper ? ((ScriptEventWrapper)evt).getEventObject() : evt;
                    interp.bindObject(BaseScriptingEnvironment.EVENT_NAME, event);
                    interp.bindObject(BaseScriptingEnvironment.ALTERNATE_EVENT_NAME, event);
                    interp.evaluate((Reader)new StringReader(s), desc);
                }
                catch (IOException event) {
                }
                catch (InterpreterException e) {
                    BaseScriptingEnvironment.this.handleInterpreterException(e);
                }
            }
        };
        t.addEventListenerNS("http://www.w3.org/2001/xml-events", type, l, false, null);
        t.dispatchEvent((Event)ev);
        t.removeEventListenerNS("http://www.w3.org/2001/xml-events", type, l, false);
    }

    protected void dispatchSVGZoomEvent() {
        if (this.bridgeContext.isSVG12()) {
            this.dispatchSVGDocEvent("zoom");
        } else {
            this.dispatchSVGDocEvent("SVGZoom");
        }
    }

    protected void dispatchSVGScrollEvent() {
        if (this.bridgeContext.isSVG12()) {
            this.dispatchSVGDocEvent("scroll");
        } else {
            this.dispatchSVGDocEvent("SVGScroll");
        }
    }

    protected void dispatchSVGResizeEvent() {
        if (this.bridgeContext.isSVG12()) {
            this.dispatchSVGDocEvent("resize");
        } else {
            this.dispatchSVGDocEvent("SVGResize");
        }
    }

    protected void dispatchSVGDocEvent(String eventType) {
        SVGSVGElement root;
        SVGSVGElement t = root = (SVGSVGElement)this.document.getDocumentElement();
        DocumentEvent de = (DocumentEvent)((Object)this.document);
        AbstractEvent ev = (AbstractEvent)de.createEvent("SVGEvents");
        ev.initEventNS("http://www.w3.org/2001/xml-events", eventType, false, false);
        t.dispatchEvent((Event)ev);
    }

    protected void handleInterpreterException(InterpreterException ie) {
        if (this.userAgent != null) {
            Exception ex = ie.getException();
            this.userAgent.displayError((Exception)(ex == null ? ie : ex));
        }
    }

    protected void handleSecurityException(SecurityException se) {
        if (this.userAgent != null) {
            this.userAgent.displayError(se);
        }
    }

    protected class Window
    implements org.apache.batik.bridge.Window {
        protected Interpreter interpreter;
        protected String language;

        public Window(Interpreter interp, String lang) {
            this.interpreter = interp;
            this.language = lang;
        }

        @Override
        public Object setInterval(String script, long interval) {
            return null;
        }

        @Override
        public Object setInterval(Runnable r, long interval) {
            return null;
        }

        @Override
        public void clearInterval(Object interval) {
        }

        @Override
        public Object setTimeout(String script, long timeout) {
            return null;
        }

        @Override
        public Object setTimeout(Runnable r, long timeout) {
            return null;
        }

        @Override
        public void clearTimeout(Object timeout) {
        }

        @Override
        public Node parseXML(String text, Document doc) {
            return null;
        }

        @Override
        public String printNode(Node n) {
            return null;
        }

        @Override
        public void getURL(String uri, Window.URLResponseHandler h) {
            this.getURL(uri, h, "UTF8");
        }

        @Override
        public void getURL(String uri, Window.URLResponseHandler h, String enc) {
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
        public void postURL(String uri, String content, Window.URLResponseHandler h, String mimeType, String fEnc) {
        }

        @Override
        public void alert(String message) {
        }

        @Override
        public boolean confirm(String message) {
            return false;
        }

        @Override
        public String prompt(String message) {
            return null;
        }

        @Override
        public String prompt(String message, String defVal) {
            return null;
        }

        @Override
        public BridgeContext getBridgeContext() {
            return BaseScriptingEnvironment.this.bridgeContext;
        }

        @Override
        public Interpreter getInterpreter() {
            return this.interpreter;
        }

        public Location getLocation() {
            return null;
        }

        public org.apache.batik.w3c.dom.Window getParent() {
            return null;
        }
    }
}

