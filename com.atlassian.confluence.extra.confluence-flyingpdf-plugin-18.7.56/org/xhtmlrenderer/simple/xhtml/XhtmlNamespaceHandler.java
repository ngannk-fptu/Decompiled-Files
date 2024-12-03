/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple.xhtml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.simple.extend.XhtmlCssOnlyNamespaceHandler;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;

public class XhtmlNamespaceHandler
extends XhtmlCssOnlyNamespaceHandler {
    @Override
    public boolean isImageElement(Element e) {
        return e != null && e.getNodeName().equalsIgnoreCase("img");
    }

    @Override
    public boolean isFormElement(Element e) {
        return e != null && e.getNodeName().equalsIgnoreCase("form");
    }

    @Override
    public String getImageSourceURI(Element e) {
        String uri = null;
        if (e != null) {
            uri = e.getAttribute("src");
        }
        return uri;
    }

    @Override
    public String getNonCssStyling(Element e) {
        if (e.getNodeName().equals("table")) {
            return this.applyTableStyles(e);
        }
        if (e.getNodeName().equals("td") || e.getNodeName().equals("th")) {
            return this.applyTableCellStyles(e);
        }
        if (e.getNodeName().equals("tr")) {
            return this.applyTableRowStyles(e);
        }
        if (e.getNodeName().equals("img")) {
            return this.applyImgStyles(e);
        }
        return "";
    }

    private String applyImgStyles(Element e) {
        StringBuffer style = new StringBuffer();
        this.applyFloatingAlign(e, style);
        return style.toString();
    }

    private String applyTableCellStyles(Element e) {
        String s;
        StringBuffer style = new StringBuffer();
        Element table = this.findTable(e);
        if (table != null) {
            s = this.getAttribute(table, "cellpadding");
            if (s != null) {
                style.append("padding: ");
                style.append(this.convertToLength(s));
                style.append(";");
            }
            if ((s = this.getAttribute(table, "border")) != null && !s.equals("0")) {
                style.append("border: 1px outset black;");
            }
        }
        if ((s = this.getAttribute(e, "width")) != null) {
            style.append("width: ");
            style.append(this.convertToLength(s));
            style.append(";");
        }
        if ((s = this.getAttribute(e, "height")) != null) {
            style.append("height: ");
            style.append(this.convertToLength(s));
            style.append(";");
        }
        this.applyAlignment(e, style);
        s = this.getAttribute(e, "bgcolor");
        if (s != null) {
            s = s.toLowerCase();
            style.append("background-color: ");
            if (this.looksLikeAMangledColor(s)) {
                style.append('#');
                style.append(s);
            } else {
                style.append(s);
            }
            style.append(';');
        }
        if ((s = this.getAttribute(e, "background")) != null) {
            style.append("background-image: url(");
            style.append(s);
            style.append(");");
        }
        return style.toString();
    }

    private String applyTableStyles(Element e) {
        StringBuffer style = new StringBuffer();
        String s = this.getAttribute(e, "width");
        if (s != null) {
            style.append("width: ");
            style.append(this.convertToLength(s));
            style.append(";");
        }
        if ((s = this.getAttribute(e, "border")) != null) {
            style.append("border: ");
            style.append(this.convertToLength(s));
            style.append(" inset black;");
        }
        if ((s = this.getAttribute(e, "cellspacing")) != null) {
            style.append("border-collapse: separate; border-spacing: ");
            style.append(this.convertToLength(s));
            style.append(";");
        }
        if ((s = this.getAttribute(e, "bgcolor")) != null) {
            s = s.toLowerCase();
            style.append("background-color: ");
            if (this.looksLikeAMangledColor(s)) {
                style.append('#');
                style.append(s);
            } else {
                style.append(s);
            }
            style.append(';');
        }
        if ((s = this.getAttribute(e, "background")) != null) {
            style.append("background-image: url(");
            style.append(s);
            style.append(");");
        }
        this.applyFloatingAlign(e, style);
        return style.toString();
    }

    private String applyTableRowStyles(Element e) {
        StringBuffer style = new StringBuffer();
        this.applyAlignment(e, style);
        return style.toString();
    }

    private void applyFloatingAlign(Element e, StringBuffer style) {
        String s = this.getAttribute(e, "align");
        if (s != null) {
            if ((s = s.toLowerCase().trim()).equals("left")) {
                style.append("float: left;");
            } else if (s.equals("right")) {
                style.append("float: right;");
            } else if (s.equals("center")) {
                style.append("margin-left: auto; margin-right: auto;");
            }
        }
    }

    private void applyAlignment(Element e, StringBuffer style) {
        String s = this.getAttribute(e, "align");
        if (s != null) {
            style.append("text-align: ");
            style.append(s.toLowerCase());
            style.append(";");
        }
        if ((s = this.getAttribute(e, "valign")) != null) {
            style.append("vertical-align: ");
            style.append(s.toLowerCase());
            style.append(";");
        }
    }

    private boolean looksLikeAMangledColor(String s) {
        if (s.length() != 6) {
            return false;
        }
        for (int i = 0; i < s.length(); ++i) {
            boolean valid;
            char c = s.charAt(i);
            boolean bl = valid = c >= '0' && c <= '9' || c >= 'a' && c <= 'f';
            if (valid) continue;
            return false;
        }
        return true;
    }

    private Element findTable(Element cell) {
        Element next;
        Node n = cell.getParentNode();
        if (n.getNodeType() == 1 && (next = (Element)n).getNodeName().equals("tr") && (n = next.getParentNode()).getNodeType() == 1) {
            next = (Element)n;
            String name = next.getNodeName();
            if (name.equals("table")) {
                return next;
            }
            if ((name.equals("tbody") || name.equals("tfoot") || name.equals("thead")) && (n = next.getParentNode()).getNodeType() == 1 && (next = (Element)n).getNodeName().equals("table")) {
                return next;
            }
        }
        return null;
    }

    public XhtmlForm createForm(Element e) {
        if (e == null) {
            return new XhtmlForm("", "get");
        }
        if (this.isFormElement(e)) {
            return new XhtmlForm(e.getAttribute("action"), e.getAttribute("method"));
        }
        return null;
    }
}

