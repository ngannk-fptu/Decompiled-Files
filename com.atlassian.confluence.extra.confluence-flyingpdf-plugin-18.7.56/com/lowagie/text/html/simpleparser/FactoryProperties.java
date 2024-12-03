/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.html.simpleparser;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.FontProvider;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.Markup;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import com.lowagie.text.pdf.HyphenationAuto;
import com.lowagie.text.pdf.HyphenationEvent;
import com.lowagie.text.utils.NumberUtilities;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.annotation.Nullable;

public class FactoryProperties {
    public static Map<String, String> followTags = new HashMap<String, String>();
    private FontProvider fontImp = FontFactory.getFontImp();

    private static void setParagraphLeading(Paragraph paragraph, @Nullable String leading) {
        if (leading == null) {
            paragraph.setLeading(0.0f, 1.5f);
            return;
        }
        try {
            StringTokenizer tokenizer = new StringTokenizer(leading, " ,");
            String v = tokenizer.nextToken();
            float v1 = Float.parseFloat(v);
            if (!tokenizer.hasMoreTokens()) {
                paragraph.setLeading(v1, 0.0f);
                return;
            }
            v = tokenizer.nextToken();
            float v2 = Float.parseFloat(v);
            paragraph.setLeading(v1, v2);
        }
        catch (Exception e) {
            paragraph.setLeading(0.0f, 1.5f);
        }
    }

    public static void createParagraph(Paragraph paragraph, ChainedProperties props) {
        props.findProperty("align").map(String::trim).ifPresent(align -> {
            if (align.equalsIgnoreCase("center")) {
                paragraph.setAlignment(1);
            } else if (align.equalsIgnoreCase("right")) {
                paragraph.setAlignment(2);
            } else if (align.equalsIgnoreCase("justify")) {
                paragraph.setAlignment(3);
            }
        });
        paragraph.setHyphenation(FactoryProperties.getHyphenation(props));
        FactoryProperties.setParagraphLeading(paragraph, props.getProperty("leading"));
        props.findProperty("before").flatMap(NumberUtilities::parseFloat).ifPresent(paragraph::setSpacingBefore);
        props.findProperty("after").flatMap(NumberUtilities::parseFloat).ifPresent(paragraph::setSpacingAfter);
        props.findProperty("extraparaspace").flatMap(NumberUtilities::parseFloat).ifPresent(paragraph::setExtraParagraphSpace);
    }

    public static Paragraph createParagraph(ChainedProperties props) {
        Paragraph paragraph = new Paragraph();
        FactoryProperties.createParagraph(paragraph, props);
        return paragraph;
    }

    public static ListItem createListItem(ChainedProperties props) {
        ListItem item = new ListItem();
        FactoryProperties.createParagraph(item, props);
        return item;
    }

    public static HyphenationEvent getHyphenation(ChainedProperties props) {
        return FactoryProperties.getHyphenation(props.getProperty("hyphenation"));
    }

    public static HyphenationEvent getHyphenation(HashMap props) {
        return FactoryProperties.getHyphenation((String)props.get("hyphenation"));
    }

    @Nullable
    public static HyphenationEvent getHyphenation(@Nullable String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        String lang = s;
        String country = null;
        int leftMin = 2;
        int rightMin = 2;
        int pos = s.indexOf(95);
        if (pos == -1) {
            return new HyphenationAuto(lang, country, leftMin, rightMin);
        }
        lang = s.substring(0, pos);
        country = s.substring(pos + 1);
        if ((pos = country.indexOf(44)) == -1) {
            return new HyphenationAuto(lang, country, leftMin, rightMin);
        }
        s = country.substring(pos + 1);
        country = country.substring(0, pos);
        pos = s.indexOf(44);
        if (pos == -1) {
            leftMin = Integer.parseInt(s);
        } else {
            leftMin = Integer.parseInt(s.substring(0, pos));
            rightMin = Integer.parseInt(s.substring(pos + 1));
        }
        return new HyphenationAuto(lang, country, leftMin, rightMin);
    }

    @Deprecated
    public static void insertStyle(HashMap h) {
        FactoryProperties.insertStyle((Map<String, String>)h);
    }

    public static void insertStyle(Map<String, String> h) {
        String style = h.get("style");
        if (style == null) {
            return;
        }
        Properties prop = Markup.parseAttributes(style);
        for (Object o : prop.keySet()) {
            String key;
            switch (key = (String)o) {
                case "font-family": {
                    h.put("face", prop.getProperty(key));
                    break;
                }
                case "font-size": {
                    h.put("size", Markup.parseLength(prop.getProperty(key)) + "pt");
                    break;
                }
                case "font-style": {
                    String ss = prop.getProperty(key).trim().toLowerCase();
                    if (!ss.equals("italic") && !ss.equals("oblique")) break;
                    h.put("i", null);
                    break;
                }
                case "font-weight": {
                    String ss = prop.getProperty(key).trim().toLowerCase();
                    if (!ss.equals("bold") && !ss.equals("700") && !ss.equals("800") && !ss.equals("900")) break;
                    h.put("b", null);
                    break;
                }
                case "text-decoration": {
                    String ss = prop.getProperty(key).trim().toLowerCase();
                    if (!ss.equals("underline")) break;
                    h.put("u", null);
                    break;
                }
                case "color": {
                    Color c = Markup.decodeColor(prop.getProperty(key));
                    if (c == null) break;
                    int hh = c.getRGB();
                    String hs = Integer.toHexString(hh);
                    hs = "000000" + hs;
                    hs = "#" + hs.substring(hs.length() - 6);
                    h.put("color", hs);
                    break;
                }
                case "line-height": {
                    String ss = prop.getProperty(key).trim();
                    float v = Markup.parseLength(prop.getProperty(key));
                    if (ss.endsWith("%")) {
                        h.put("leading", "0," + v / 100.0f);
                        break;
                    }
                    if ("normal".equalsIgnoreCase(ss)) {
                        h.put("leading", "0,1.5");
                        break;
                    }
                    h.put("leading", v + ",0");
                    break;
                }
                case "text-align": {
                    String ss = prop.getProperty(key).trim().toLowerCase();
                    h.put("align", ss);
                    break;
                }
            }
        }
    }

    public static void insertStyle(Map<String, String> h, ChainedProperties cprops) {
        String style = h.get("style");
        if (style == null) {
            return;
        }
        Properties prop = Markup.parseAttributes(style);
        for (Object o : prop.keySet()) {
            String key;
            switch (key = (String)o) {
                case "font-family": {
                    h.put("face", prop.getProperty(key));
                    break;
                }
                case "font-size": {
                    float actualFontSize = Markup.parseLength(cprops.getProperty("size"), 12.0f);
                    if (actualFontSize <= 0.0f) {
                        actualFontSize = 12.0f;
                    }
                    h.put("size", Markup.parseLength(prop.getProperty(key), actualFontSize) + "pt");
                    break;
                }
                case "font-style": {
                    String ss = prop.getProperty(key).trim().toLowerCase();
                    if (!ss.equals("italic") && !ss.equals("oblique")) break;
                    h.put("i", null);
                    break;
                }
                case "font-weight": {
                    String ss = prop.getProperty(key).trim().toLowerCase();
                    if (!ss.equals("bold") && !ss.equals("700") && !ss.equals("800") && !ss.equals("900")) break;
                    h.put("b", null);
                    break;
                }
                case "text-decoration": {
                    String ss = prop.getProperty(key).trim().toLowerCase();
                    if (!ss.equals("underline")) break;
                    h.put("u", null);
                    break;
                }
                case "color": {
                    Color c = Markup.decodeColor(prop.getProperty(key));
                    if (c == null) break;
                    int hh = c.getRGB();
                    String hs = Integer.toHexString(hh);
                    hs = "000000" + hs;
                    hs = "#" + hs.substring(hs.length() - 6);
                    h.put("color", hs);
                    break;
                }
                case "line-height": {
                    String ss = prop.getProperty(key).trim();
                    float actualFontSize = Markup.parseLength(cprops.getProperty("size"), 12.0f);
                    if (actualFontSize <= 0.0f) {
                        actualFontSize = 12.0f;
                    }
                    float v = Markup.parseLength(prop.getProperty(key), actualFontSize);
                    if (ss.endsWith("%")) {
                        h.put("leading", "0," + v / 100.0f);
                        return;
                    }
                    if ("normal".equalsIgnoreCase(ss)) {
                        h.put("leading", "0,1.5");
                        return;
                    }
                    if (v != 0.0f && Character.isDigit(ss.charAt(ss.length() - 1))) {
                        h.put("leading", "0," + v);
                        break;
                    }
                    h.put("leading", v + ",0");
                    break;
                }
                case "text-align": {
                    String ss = prop.getProperty(key).trim().toLowerCase();
                    h.put("align", ss);
                    break;
                }
                case "padding-left": {
                    String ss = prop.getProperty(key).trim().toLowerCase();
                    h.put("indent", Float.toString(Markup.parseLength(ss)));
                    break;
                }
            }
        }
    }

    public Chunk createChunk(String text, ChainedProperties props) {
        Font font = this.getFont(props);
        float size = font.getSize();
        size /= 2.0f;
        Chunk chunk = new Chunk(text, font);
        if (props.hasProperty("sub")) {
            chunk.setTextRise(-size);
        } else if (props.hasProperty("sup")) {
            chunk.setTextRise(size);
        }
        chunk.setHyphenation(FactoryProperties.getHyphenation(props));
        return chunk;
    }

    public Font getFont(ChainedProperties props) {
        String face = props.getProperty("face");
        if (face != null) {
            StringTokenizer tokenizer = new StringTokenizer(face, ",");
            while (tokenizer.hasMoreTokens()) {
                face = tokenizer.nextToken().trim();
                if (face.startsWith("\"")) {
                    face = face.substring(1);
                }
                if (face.endsWith("\"")) {
                    face = face.substring(0, face.length() - 1);
                }
                if (!this.fontImp.isRegistered(face)) continue;
            }
        }
        int style = 0;
        if (props.hasProperty("i")) {
            style |= 2;
        }
        if (props.hasProperty("b")) {
            style |= 1;
        }
        if (props.hasProperty("u")) {
            style |= 4;
        }
        if (props.hasProperty("s")) {
            style |= 8;
        }
        float size = props.findProperty("size").flatMap(NumberUtilities::parseFloat).orElse(Float.valueOf(12.0f)).floatValue();
        Color color = Markup.decodeColor(props.getProperty("color"));
        String encoding = props.getOrDefault("encoding", "Cp1252");
        return this.fontImp.getFont(face, encoding, true, size, style, color);
    }

    public FontProvider getFontImp() {
        return this.fontImp;
    }

    public void setFontImp(FontProvider fontImp) {
        this.fontImp = fontImp;
    }

    static {
        followTags.put("i", "i");
        followTags.put("b", "b");
        followTags.put("u", "u");
        followTags.put("sub", "sub");
        followTags.put("sup", "sup");
        followTags.put("em", "i");
        followTags.put("strong", "b");
        followTags.put("s", "s");
        followTags.put("strike", "s");
    }
}

