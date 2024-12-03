/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.extend;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xhtmlrenderer.css.extend.StylesheetFactory;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;
import org.xhtmlrenderer.simple.NoNamespaceHandler;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.XRLog;

public class XhtmlCssOnlyNamespaceHandler
extends NoNamespaceHandler {
    static final String _namespace = "http://www.w3.org/1999/xhtml";
    private static StylesheetInfo _defaultStylesheet;
    private static boolean _defaultStylesheetError;
    private final Map _metadata = null;

    @Override
    public String getNamespace() {
        return _namespace;
    }

    @Override
    public String getClass(Element e) {
        return e.getAttribute("class");
    }

    @Override
    public String getID(Element e) {
        String result = e.getAttribute("id").trim();
        return result.length() == 0 ? null : result;
    }

    protected String convertToLength(String value) {
        if (this.isInteger(value)) {
            return value + "px";
        }
        return value;
    }

    protected boolean isInteger(String value) {
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c >= '0' && c <= '9') continue;
            return false;
        }
        return true;
    }

    protected String getAttribute(Element e, String attrName) {
        String result = e.getAttribute(attrName);
        return (result = result.trim()).length() == 0 ? null : result;
    }

    @Override
    public String getElementStyling(Element e) {
        StringBuffer style = new StringBuffer();
        if (e.getNodeName().equals("td") || e.getNodeName().equals("th")) {
            String s = this.getAttribute(e, "colspan");
            if (s != null) {
                style.append("-fs-table-cell-colspan: ");
                style.append(s);
                style.append(";");
            }
            if ((s = this.getAttribute(e, "rowspan")) != null) {
                style.append("-fs-table-cell-rowspan: ");
                style.append(s);
                style.append(";");
            }
        } else if (e.getNodeName().equals("img")) {
            String s = this.getAttribute(e, "width");
            if (s != null) {
                style.append("width: ");
                style.append(this.convertToLength(s));
                style.append(";");
            }
            if ((s = this.getAttribute(e, "height")) != null) {
                style.append("height: ");
                style.append(this.convertToLength(s));
                style.append(";");
            }
        } else if (e.getNodeName().equals("colgroup") || e.getNodeName().equals("col")) {
            String s = this.getAttribute(e, "span");
            if (s != null) {
                style.append("-fs-table-cell-colspan: ");
                style.append(s);
                style.append(";");
            }
            if ((s = this.getAttribute(e, "width")) != null) {
                style.append("width: ");
                style.append(this.convertToLength(s));
                style.append(";");
            }
        }
        style.append(e.getAttribute("style"));
        return style.toString();
    }

    @Override
    public String getLinkUri(Element e) {
        String href = null;
        if (e.getNodeName().equalsIgnoreCase("a") && e.hasAttribute("href")) {
            href = e.getAttribute("href");
        }
        return href;
    }

    @Override
    public String getAnchorName(Element e) {
        if (e != null && e.getNodeName().equalsIgnoreCase("a") && e.hasAttribute("name")) {
            return e.getAttribute("name");
        }
        return null;
    }

    private static String readTextContent(Element element) {
        StringBuffer result = new StringBuffer();
        for (Node current = element.getFirstChild(); current != null; current = current.getNextSibling()) {
            short nodeType = current.getNodeType();
            if (nodeType != 3 && nodeType != 4) continue;
            Text t = (Text)current;
            result.append(t.getData());
        }
        return result.toString();
    }

    private static String collapseWhiteSpace(String text) {
        StringBuffer result = new StringBuffer();
        int l = text.length();
        block0: for (int i = 0; i < l; ++i) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                result.append(' ');
                while (++i < l) {
                    c = text.charAt(i);
                    if (Character.isWhitespace(c)) continue;
                    --i;
                    continue block0;
                }
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    @Override
    public String getDocumentTitle(Document doc) {
        Element titleElem;
        String title = "";
        Element html = doc.getDocumentElement();
        Element head = this.findFirstChild(html, "head");
        if (head != null && (titleElem = this.findFirstChild(head, "title")) != null) {
            title = XhtmlCssOnlyNamespaceHandler.collapseWhiteSpace(XhtmlCssOnlyNamespaceHandler.readTextContent(titleElem).trim());
        }
        return title;
    }

    private Element findFirstChild(Element parent, String targetName) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node n = children.item(i);
            if (n.getNodeType() != 1 || !n.getNodeName().equals(targetName)) continue;
            return (Element)n;
        }
        return null;
    }

    protected StylesheetInfo readStyleElement(Element style) {
        String media = style.getAttribute("media");
        if ("".equals(media)) {
            media = "all";
        }
        StylesheetInfo info = new StylesheetInfo();
        info.setMedia(media);
        info.setType(style.getAttribute("type"));
        info.setTitle(style.getAttribute("title"));
        info.setOrigin(2);
        StringBuffer buf = new StringBuffer();
        for (Node current = style.getFirstChild(); current != null; current = current.getNextSibling()) {
            if (!(current instanceof CharacterData)) continue;
            buf.append(((CharacterData)current).getData());
        }
        String css = buf.toString().trim();
        if (css.length() > 0) {
            info.setContent(css.toString());
            return info;
        }
        return null;
    }

    protected StylesheetInfo readLinkElement(Element link) {
        String rel = link.getAttribute("rel").toLowerCase();
        if (rel.indexOf("alternate") != -1) {
            return null;
        }
        if (rel.indexOf("stylesheet") == -1) {
            return null;
        }
        String type = link.getAttribute("type");
        if (!type.equals("") && !type.equals("text/css")) {
            return null;
        }
        StylesheetInfo info = new StylesheetInfo();
        if (type.equals("")) {
            type = "text/css";
        }
        info.setType(type);
        info.setOrigin(2);
        info.setUri(link.getAttribute("href"));
        String media = link.getAttribute("media");
        if ("".equals(media)) {
            media = "all";
        }
        info.setMedia(media);
        String title = link.getAttribute("title");
        info.setTitle(title);
        return info;
    }

    @Override
    public StylesheetInfo[] getStylesheets(Document doc) {
        ArrayList<StylesheetInfo> result = new ArrayList<StylesheetInfo>();
        result.addAll(Arrays.asList(super.getStylesheets(doc)));
        Element html = doc.getDocumentElement();
        Element head = this.findFirstChild(html, "head");
        if (head != null) {
            for (Node current = head.getFirstChild(); current != null; current = current.getNextSibling()) {
                if (current.getNodeType() != 1) continue;
                Element elem = (Element)current;
                StylesheetInfo info = null;
                String elemName = elem.getLocalName();
                if (elemName == null) {
                    elemName = elem.getTagName();
                }
                if (elemName.equals("link")) {
                    info = this.readLinkElement(elem);
                } else if (elemName.equals("style")) {
                    info = this.readStyleElement(elem);
                }
                if (info == null) continue;
                result.add(info);
            }
        }
        return result.toArray(new StylesheetInfo[result.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public StylesheetInfo getDefaultStylesheet(StylesheetFactory factory) {
        Class<XhtmlCssOnlyNamespaceHandler> clazz = XhtmlCssOnlyNamespaceHandler.class;
        synchronized (XhtmlCssOnlyNamespaceHandler.class) {
            StylesheetInfo info;
            block21: {
                StylesheetInfo stylesheetInfo;
                InputStream is;
                block20: {
                    if (_defaultStylesheet != null) {
                        // ** MonitorExit[var2_2] (shouldn't be in output)
                        return _defaultStylesheet;
                    }
                    if (_defaultStylesheetError) {
                        // ** MonitorExit[var2_2] (shouldn't be in output)
                        return null;
                    }
                    info = new StylesheetInfo();
                    info.setUri(this.getNamespace());
                    info.setOrigin(0);
                    info.setMedia("all");
                    info.setType("text/css");
                    is = null;
                    try {
                        is = this.getDefaultStylesheetStream();
                        if (_defaultStylesheetError) {
                            stylesheetInfo = null;
                            break block20;
                        }
                        Stylesheet sheet = factory.parse(new InputStreamReader(is), info);
                        info.setStylesheet(sheet);
                        is.close();
                        is = null;
                        break block21;
                    }
                    catch (Exception e) {
                        _defaultStylesheetError = true;
                        XRLog.exception("Could not parse default stylesheet", e);
                        break block21;
                    }
                }
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return stylesheetInfo;
                finally {
                    if (is != null) {
                        try {
                            is.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
            }
            _defaultStylesheet = info;
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return _defaultStylesheet;
        }
    }

    private InputStream getDefaultStylesheetStream() {
        InputStream stream = null;
        String defaultStyleSheet = Configuration.valueFor("xr.css.user-agent-default-css") + "XhtmlNamespaceHandler.css";
        stream = this.getClass().getResourceAsStream(defaultStyleSheet);
        if (stream == null) {
            XRLog.exception("Can't load default CSS from " + defaultStyleSheet + ".This file must be on your CLASSPATH. Please check before continuing.");
            _defaultStylesheetError = true;
        }
        return stream;
    }

    private Map getMetaInfo(Document doc) {
        if (this._metadata != null) {
            return this._metadata;
        }
        HashMap<String, String> metadata = new HashMap<String, String>();
        Element html = doc.getDocumentElement();
        Element head = this.findFirstChild(html, "head");
        if (head != null) {
            for (Node current = head.getFirstChild(); current != null; current = current.getNextSibling()) {
                if (current.getNodeType() != 1) continue;
                Element elem = (Element)current;
                String elemName = elem.getLocalName();
                if (elemName == null) {
                    elemName = elem.getTagName();
                }
                if (!elemName.equals("meta")) continue;
                String http_equiv = elem.getAttribute("http-equiv");
                String content = elem.getAttribute("content");
                if (http_equiv.equals("") || content.equals("")) continue;
                metadata.put(http_equiv, content);
            }
        }
        return metadata;
    }

    @Override
    public String getLang(Element e) {
        if (e == null) {
            return "";
        }
        String lang = e.getAttribute("lang");
        if ("".equals(lang) && (lang = (String)this.getMetaInfo(e.getOwnerDocument()).get("Content-Language")) == null) {
            lang = "";
        }
        return lang;
    }

    static {
        _defaultStylesheetError = false;
    }
}

