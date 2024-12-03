/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xhtmlrenderer.css.extend.StylesheetFactory;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;
import org.xhtmlrenderer.extend.NamespaceHandler;

public class NoNamespaceHandler
implements NamespaceHandler {
    static final String _namespace = "http://www.w3.org/XML/1998/namespace";
    private Pattern _typePattern = Pattern.compile("type\\s?=\\s?");
    private Pattern _hrefPattern = Pattern.compile("href\\s?=\\s?");
    private Pattern _titlePattern = Pattern.compile("title\\s?=\\s?");
    private Pattern _alternatePattern = Pattern.compile("alternate\\s?=\\s?");
    private Pattern _mediaPattern = Pattern.compile("media\\s?=\\s?");

    @Override
    public String getNamespace() {
        return _namespace;
    }

    @Override
    public String getAttributeValue(Element e, String attrName) {
        return e.getAttribute(attrName);
    }

    @Override
    public String getAttributeValue(Element e, String namespaceURI, String attrName) {
        if (namespaceURI == "") {
            return e.getAttribute(attrName);
        }
        if (namespaceURI == null) {
            if (e.getLocalName() == null) {
                return e.getAttribute(attrName);
            }
            NamedNodeMap attrs = e.getAttributes();
            int l = attrs.getLength();
            for (int i = 0; i < l; ++i) {
                Attr attr = (Attr)attrs.item(i);
                if (!attrName.equals(attr.getLocalName())) continue;
                return attr.getValue();
            }
            return "";
        }
        return e.getAttributeNS(namespaceURI, attrName);
    }

    @Override
    public String getClass(Element e) {
        return null;
    }

    @Override
    public String getID(Element e) {
        return null;
    }

    @Override
    public String getLang(Element e) {
        if (e == null) {
            return "";
        }
        return e.getAttribute("lang");
    }

    @Override
    public String getElementStyling(Element e) {
        return null;
    }

    @Override
    public String getNonCssStyling(Element e) {
        return null;
    }

    @Override
    public String getLinkUri(Element e) {
        return null;
    }

    @Override
    public String getDocumentTitle(Document doc) {
        return null;
    }

    @Override
    public String getAnchorName(Element e) {
        return null;
    }

    @Override
    public boolean isImageElement(Element e) {
        return false;
    }

    @Override
    public String getImageSourceURI(Element e) {
        return null;
    }

    @Override
    public boolean isFormElement(Element e) {
        return false;
    }

    @Override
    public StylesheetInfo[] getStylesheets(Document doc) {
        ArrayList<StylesheetInfo> list = new ArrayList<StylesheetInfo>();
        NodeList nl = doc.getChildNodes();
        int len = nl.getLength();
        for (int i = 0; i < len; ++i) {
            int start;
            String alternate;
            ProcessingInstruction piNode;
            Node node = nl.item(i);
            if (node.getNodeType() != 7 || !(piNode = (ProcessingInstruction)node).getTarget().equals("xml-stylesheet")) continue;
            StylesheetInfo info = new StylesheetInfo();
            info.setOrigin(2);
            String pi = piNode.getData();
            Matcher m = this._alternatePattern.matcher(pi);
            if (m.matches() && (alternate = pi.substring((start = m.end()) + 1, pi.indexOf(pi.charAt(start), start + 1))).equals("yes")) continue;
            m = this._typePattern.matcher(pi);
            if (m.find()) {
                start = m.end();
                String type = pi.substring(start + 1, pi.indexOf(pi.charAt(start), start + 1));
                if (!type.equals("text/css")) continue;
                info.setType(type);
            }
            if ((m = this._hrefPattern.matcher(pi)).find()) {
                start = m.end();
                String href = pi.substring(start + 1, pi.indexOf(pi.charAt(start), start + 1));
                info.setUri(href);
            }
            if ((m = this._titlePattern.matcher(pi)).find()) {
                start = m.end();
                String title = pi.substring(start + 1, pi.indexOf(pi.charAt(start), start + 1));
                info.setTitle(title);
            }
            if ((m = this._mediaPattern.matcher(pi)).find()) {
                start = m.end();
                String media = pi.substring(start + 1, pi.indexOf(pi.charAt(start), start + 1));
                info.setMedia(media);
            } else {
                info.addMedium("screen");
            }
            list.add(info);
        }
        return list.toArray(new StylesheetInfo[list.size()]);
    }

    @Override
    public StylesheetInfo getDefaultStylesheet(StylesheetFactory factory) {
        return null;
    }
}

