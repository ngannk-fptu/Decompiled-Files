/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.radeox.macro.parameter.MacroParameter
 */
package com.atlassian.renderer.macro.macros;

import com.atlassian.renderer.macro.BaseMacro;
import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang.StringUtils;
import org.radeox.macro.parameter.MacroParameter;

public abstract class AbstractPanelMacro
extends BaseMacro {
    private String[] myParamDescription = new String[]{"?1: title", "?2: borderStyle", "?3: borderColor", "?4: borderWidth", "?5: bgColor", "?6: titleBGColor"};

    public String[] getParamDescription() {
        return this.myParamDescription;
    }

    protected abstract String getPanelCSSClass();

    protected abstract String getPanelHeaderCSSClass();

    protected abstract String getPanelContentCSSClass();

    protected String getTitle(MacroParameter macroParameter) {
        return macroParameter.get("title");
    }

    protected String getBodyContent(MacroParameter macroParameter) {
        return macroParameter.getContent();
    }

    public void execute(Writer writer, MacroParameter macroParameter) throws IllegalArgumentException, IOException {
        String title = this.getTitle(macroParameter);
        String content = this.getBodyContent(macroParameter);
        String borderStyle = macroParameter.get("borderStyle");
        String borderColor = macroParameter.get("borderColor");
        Integer borderWidth = null;
        String borderWidthString = macroParameter.get("borderWidth");
        if (borderWidthString != null) {
            if (borderWidthString.indexOf("px") != -1) {
                borderWidthString = borderWidthString.replaceAll("px", "");
            }
            borderWidth = new Integer(borderWidthString);
        }
        String backgroundColor = macroParameter.get("bgColor");
        String titleBackgroundColor = macroParameter.get("titleBGColor");
        if (StringUtils.isEmpty((String)titleBackgroundColor) && StringUtils.isNotEmpty((String)backgroundColor)) {
            titleBackgroundColor = backgroundColor;
        }
        writer.write("<div class=\"" + this.getPanelCSSClass() + "\"");
        if (StringUtils.isNotEmpty((String)borderStyle)) {
            writer.write(" style=\"border-style: " + borderStyle + "; ");
            if (borderWidth != null && borderWidth >= 1) {
                writer.write("border-width: " + borderWidth + "px; ");
            }
            if (StringUtils.isNotEmpty((String)borderColor)) {
                writer.write("border-color: " + borderColor + "; ");
            }
            writer.write("\"");
        }
        writer.write(">");
        if (StringUtils.isNotEmpty((String)title)) {
            this.writeHeader(writer, title, borderStyle, borderColor, borderWidth, titleBackgroundColor);
        }
        if (StringUtils.isNotEmpty((String)content)) {
            this.writeContent(writer, macroParameter, content, backgroundColor);
        }
        writer.write("</div>");
    }

    protected void writeHeader(Writer writer, String title, String borderStyle, String borderColor, Integer borderWidth, String titleBackgroundColor) throws IOException {
        writer.write("<div class=\"" + this.getPanelHeaderCSSClass() + "\"");
        if (StringUtils.isNotEmpty((String)borderStyle) || StringUtils.isNotEmpty((String)titleBackgroundColor)) {
            writer.write(" style=\"");
            if (StringUtils.isNotEmpty((String)borderStyle)) {
                writer.write("border-bottom-style: " + borderStyle + "; ");
                if (borderWidth != null && borderWidth >= 1) {
                    writer.write("border-bottom-width: " + borderWidth + "; ");
                }
                if (StringUtils.isNotEmpty((String)borderColor)) {
                    writer.write("border-bottom-color: " + borderColor + "; ");
                }
            }
            if (StringUtils.isNotEmpty((String)titleBackgroundColor)) {
                writer.write("background-color: " + titleBackgroundColor + "; ");
            }
            writer.write("\"");
        }
        writer.write("><b>");
        writer.write(title);
        writer.write("</b></div>");
    }

    protected void writeContent(Writer writer, MacroParameter macroParameter, String content, String backgroundColor) throws IOException {
        writer.write("<div class=\"" + this.getPanelContentCSSClass() + "\"");
        if (StringUtils.isNotEmpty((String)backgroundColor)) {
            writer.write(" style=\"background-color: " + backgroundColor + "; \"");
        }
        writer.write(">\n");
        writer.write(content.trim());
        writer.write("\n</div>");
    }
}

