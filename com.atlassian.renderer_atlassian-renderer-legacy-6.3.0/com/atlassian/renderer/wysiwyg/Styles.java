/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Node;

public class Styles {
    public static final String ITALIC = "font-style: italic";
    public static final String BOLD = "font-weight: bold";
    public static final String STRIKETHROUGH = "text-decoration: line-through";
    public static final String UNDERLINE = "text-decoration: underline";
    public static final String SUBSCRIPT = "baseline-shift: sub";
    public static final String SUPERSCRIPT = "baseline-shift: sup";
    public static final String CITE = "style-citation";
    public static final String MONOSPACE = "style-monospace";
    private Set styles = Collections.EMPTY_SET;
    private String colour = null;

    public Styles() {
    }

    public Styles(String style, Styles current) {
        this.styles = new HashSet();
        if (current != null) {
            this.styles.addAll(current.getStyles());
            this.colour = current.getColour();
        }
        this.styles.add(style);
    }

    public Styles(Node node, Styles current) {
        this.styles = new HashSet();
        this.styles.addAll(current.getStyles());
        this.colour = current.getColour();
        if (node.getAttributes() != null) {
            Node colorNode;
            Node styleNode = node.getAttributes().getNamedItem("style");
            if (styleNode != null) {
                String[] newStyles = styleNode.getNodeValue().split(";");
                for (int i = 0; i < newStyles.length; ++i) {
                    if (newStyles[i].trim().toLowerCase().startsWith("color:")) {
                        this.setColour(newStyles[i].trim());
                        continue;
                    }
                    this.styles.add(newStyles[i].trim());
                }
            }
            if ((colorNode = node.getAttributes().getNamedItem("color")) != null) {
                this.colour = colorNode.getNodeValue().toLowerCase();
            }
        }
    }

    private String getColour() {
        return this.colour;
    }

    public Set getStyles() {
        return this.styles;
    }

    private void setColour(String s) {
        if ((s = s.toLowerCase()).startsWith("color: rgb(")) {
            String numbers = s.substring("color: rgb(".length(), s.length() - 1);
            String[] number = numbers.split(",");
            if (number.length != 3) {
                throw new RuntimeException("Bad color style format:'" + s + "'");
            }
            try {
                this.colour = "#" + this.hexDigits(number[0]) + this.hexDigits(number[1]) + this.hexDigits(number[2]);
            }
            catch (NumberFormatException nfe) {
                throw new RuntimeException("Bad color style format:'" + s + "'", nfe);
            }
        } else {
            this.colour = s.equals("color:") ? null : s.substring("color: ".length());
        }
    }

    private String hexDigits(String number) {
        String s = Integer.toHexString(Integer.parseInt(number.trim())).toLowerCase();
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }

    public String decorateText(String text) {
        ArrayList<String> prefix = new ArrayList<String>();
        if (this.styles.contains(BOLD)) {
            prefix.add("*");
        }
        if (this.styles.contains(ITALIC)) {
            prefix.add("_");
        }
        if (this.styles.contains(STRIKETHROUGH)) {
            prefix.add("-");
        }
        if (this.styles.contains(UNDERLINE)) {
            prefix.add("+");
        }
        if (this.styles.contains(SUBSCRIPT)) {
            prefix.add("~");
        }
        if (this.styles.contains(SUPERSCRIPT)) {
            prefix.add("^");
        }
        if (this.styles.contains(CITE)) {
            prefix.add("??");
        }
        StringBuffer s = new StringBuffer();
        if (this.colour != null) {
            s.append("{color:").append(this.colour).append("}");
        }
        if (this.styles.contains(MONOSPACE)) {
            s.append("{{{}");
        }
        StringBuffer prefixsb = new StringBuffer();
        StringBuffer suffixsb = new StringBuffer();
        for (int i = 0; i < prefix.size(); ++i) {
            prefixsb.append('{').append(prefix.get(i)).append('}');
            suffixsb.append('{').append(prefix.get(prefix.size() - 1 - i)).append('}');
        }
        s.append(prefixsb).append(text).append(suffixsb);
        if (this.styles.contains(MONOSPACE)) {
            s.append("{}}}");
        }
        if (this.colour != null) {
            s.append("{color}");
        }
        return s.toString();
    }
}

