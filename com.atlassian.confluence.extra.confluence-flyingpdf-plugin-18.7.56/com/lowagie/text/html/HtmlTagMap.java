/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.html;

import com.lowagie.text.ElementTags;
import com.lowagie.text.html.HtmlPeer;
import com.lowagie.text.html.HtmlTags;
import java.util.HashMap;

public class HtmlTagMap
extends HashMap<String, HtmlPeer> {
    private static final long serialVersionUID = 5287430058473705350L;

    public HtmlTagMap() {
        HtmlPeer peer = new HtmlPeer("itext", "html");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("phrase", "span");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("chunk", "font");
        peer.addAlias("font", "face");
        peer.addAlias("size", "point-size");
        peer.addAlias("color", "color");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("anchor", "a");
        peer.addAlias("name", "name");
        peer.addAlias("reference", "href");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("paragraph", "p");
        peer.addAlias("align", "align");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("paragraph", "div");
        peer.addAlias("align", "align");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("paragraph", HtmlTags.H[0]);
        peer.addValue("size", "20");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("paragraph", HtmlTags.H[1]);
        peer.addValue("size", "18");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("paragraph", HtmlTags.H[2]);
        peer.addValue("size", "16");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("paragraph", HtmlTags.H[3]);
        peer.addValue("size", "14");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("paragraph", HtmlTags.H[4]);
        peer.addValue("size", "12");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("paragraph", HtmlTags.H[5]);
        peer.addValue("size", "10");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("list", "ol");
        peer.addValue("numbered", "true");
        peer.addValue("symbolindent", "20");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("list", "ul");
        peer.addValue("numbered", "false");
        peer.addValue("symbolindent", "20");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("listitem", "li");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("phrase", "i");
        peer.addValue("fontstyle", "italic");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("phrase", "em");
        peer.addValue("fontstyle", "italic");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("phrase", "b");
        peer.addValue("fontstyle", "bold");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("phrase", "strong");
        peer.addValue("fontstyle", "bold");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("phrase", "s");
        peer.addValue("fontstyle", "line-through");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("phrase", "code");
        peer.addValue("font", "Courier");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("phrase", "var");
        peer.addValue("font", "Courier");
        peer.addValue("fontstyle", "italic");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("phrase", "u");
        peer.addValue("fontstyle", "underline");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("chunk", "sup");
        peer.addValue(ElementTags.SUBSUPSCRIPT, "6.0");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("chunk", "sub");
        peer.addValue(ElementTags.SUBSUPSCRIPT, "-6.0");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("horizontalrule", "hr");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("table", "table");
        peer.addAlias("width", "width");
        peer.addAlias("backgroundcolor", "bgcolor");
        peer.addAlias("bordercolor", "bordercolor");
        peer.addAlias("columns", "cols");
        peer.addAlias("cellpadding", "cellpadding");
        peer.addAlias("cellspacing", "cellspacing");
        peer.addAlias("borderwidth", "border");
        peer.addAlias("align", "align");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("row", "tr");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("cell", "td");
        peer.addAlias("width", "width");
        peer.addAlias("backgroundcolor", "bgcolor");
        peer.addAlias("bordercolor", "bordercolor");
        peer.addAlias("colspan", "colspan");
        peer.addAlias("rowspan", "rowspan");
        peer.addAlias("nowrap", "nowrap");
        peer.addAlias("horizontalalign", "align");
        peer.addAlias("verticalalign", "valign");
        peer.addValue("header", "false");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("cell", "th");
        peer.addAlias("width", "width");
        peer.addAlias("backgroundcolor", "bgcolor");
        peer.addAlias("bordercolor", "bordercolor");
        peer.addAlias("colspan", "colspan");
        peer.addAlias("rowspan", "rowspan");
        peer.addAlias("nowrap", "nowrap");
        peer.addAlias("horizontalalign", "align");
        peer.addAlias("verticalalign", "valign");
        peer.addValue("header", "true");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("image", "img");
        peer.addAlias("url", "src");
        peer.addAlias("alt", "alt");
        peer.addAlias("plainwidth", "width");
        peer.addAlias("plainheight", "height");
        this.put(peer.getAlias(), peer);
        peer = new HtmlPeer("newline", "br");
        this.put(peer.getAlias(), peer);
    }

    public static boolean isHtml(String tag) {
        return "html".equalsIgnoreCase(tag);
    }

    public static boolean isHead(String tag) {
        return "head".equalsIgnoreCase(tag);
    }

    public static boolean isMeta(String tag) {
        return "meta".equalsIgnoreCase(tag);
    }

    public static boolean isLink(String tag) {
        return "link".equalsIgnoreCase(tag);
    }

    public static boolean isTitle(String tag) {
        return "title".equalsIgnoreCase(tag);
    }

    public static boolean isBody(String tag) {
        return "body".equalsIgnoreCase(tag);
    }

    public static boolean isSpecialTag(String tag) {
        return HtmlTagMap.isHtml(tag) || HtmlTagMap.isHead(tag) || HtmlTagMap.isMeta(tag) || HtmlTagMap.isLink(tag) || HtmlTagMap.isBody(tag);
    }
}

