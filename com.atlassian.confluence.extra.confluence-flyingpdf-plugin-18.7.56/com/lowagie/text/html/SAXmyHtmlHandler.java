/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.html;

import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.html.HtmlPeer;
import com.lowagie.text.html.HtmlTagMap;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.xml.SAXiTextHandler;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Nullable;
import org.xml.sax.Attributes;

public class SAXmyHtmlHandler
extends SAXiTextHandler<HtmlPeer> {
    private Properties bodyAttributes = new Properties();
    private boolean tableBorder = false;

    public SAXmyHtmlHandler(DocListener document) {
        this(document, new HtmlTagMap());
    }

    public SAXmyHtmlHandler(DocListener document, HtmlTagMap htmlTags) {
        this(document, htmlTags, (BaseFont)null);
    }

    public SAXmyHtmlHandler(DocListener document, HtmlTagMap htmlTags, BaseFont bf) {
        super(document, htmlTags, bf);
    }

    public SAXmyHtmlHandler(DocListener document, BaseFont bf) {
        this(document, new HtmlTagMap(), bf);
    }

    @Override
    public void startElement(String uri, String localName, String name, @Nullable Attributes attrs) {
        String lowerCaseName = name.toLowerCase();
        if (HtmlTagMap.isHtml(lowerCaseName)) {
            return;
        }
        if (HtmlTagMap.isHead(lowerCaseName)) {
            return;
        }
        if (HtmlTagMap.isTitle(lowerCaseName)) {
            return;
        }
        if (HtmlTagMap.isMeta(lowerCaseName)) {
            String meta = null;
            String content = null;
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); ++i) {
                    String attribute = attrs.getQName(i);
                    if (attribute.equalsIgnoreCase("content")) {
                        content = attrs.getValue(i);
                        continue;
                    }
                    if (!attribute.equalsIgnoreCase("name")) continue;
                    meta = attrs.getValue(i);
                }
            }
            if (meta != null && content != null) {
                this.bodyAttributes.put(meta, content);
            }
            return;
        }
        if (HtmlTagMap.isLink(lowerCaseName)) {
            return;
        }
        if (HtmlTagMap.isBody(lowerCaseName)) {
            HtmlPeer peer = new HtmlPeer("itext", lowerCaseName);
            peer.addAlias("top", "topmargin");
            peer.addAlias("bottom", "bottommargin");
            peer.addAlias("right", "rightmargin");
            peer.addAlias("left", "leftmargin");
            this.bodyAttributes.putAll((Map<?, ?>)peer.getAttributes(attrs));
            this.handleStartingTags(peer.getTag(), this.bodyAttributes);
            return;
        }
        if (this.myTags.containsKey(lowerCaseName)) {
            HtmlPeer peer = (HtmlPeer)this.myTags.get(lowerCaseName);
            if ("table".equals(peer.getTag()) || "cell".equals(peer.getTag())) {
                String value;
                Properties p = peer.getAttributes(attrs);
                if ("table".equals(peer.getTag()) && (value = p.getProperty("borderwidth")) != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (Float.parseFloat(stringBuilder.append(value).append("f").toString()) > 0.0f) {
                        this.tableBorder = true;
                    }
                }
                if (this.tableBorder) {
                    p.put("left", String.valueOf(true));
                    p.put("right", String.valueOf(true));
                    p.put("top", String.valueOf(true));
                    p.put("bottom", String.valueOf(true));
                }
                this.handleStartingTags(peer.getTag(), p);
                return;
            }
            this.handleStartingTags(peer.getTag(), peer.getAttributes(attrs));
            return;
        }
        Properties attributes = new Properties();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); ++i) {
                String attribute = attrs.getQName(i).toLowerCase();
                attributes.setProperty(attribute, attrs.getValue(i).toLowerCase());
            }
        }
        this.handleStartingTags(lowerCaseName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String name) {
        String lowerCaseName = name.toLowerCase();
        if ("paragraph".equals(lowerCaseName)) {
            try {
                this.document.add((Element)this.stack.pop());
                return;
            }
            catch (DocumentException e) {
                throw new ExceptionConverter(e);
            }
        }
        if (HtmlTagMap.isHead(lowerCaseName)) {
            return;
        }
        if (HtmlTagMap.isTitle(lowerCaseName)) {
            if (this.currentChunk != null) {
                this.bodyAttributes.put("title", this.currentChunk.getContent());
            }
            return;
        }
        if (HtmlTagMap.isMeta(lowerCaseName)) {
            return;
        }
        if (HtmlTagMap.isLink(lowerCaseName)) {
            return;
        }
        if (HtmlTagMap.isBody(lowerCaseName)) {
            return;
        }
        if (this.myTags.containsKey(lowerCaseName)) {
            HtmlPeer peer = (HtmlPeer)this.myTags.get(lowerCaseName);
            if ("table".equals(peer.getTag())) {
                this.tableBorder = false;
            }
            super.handleEndingTags(peer.getTag());
            return;
        }
        this.handleEndingTags(lowerCaseName);
    }
}

