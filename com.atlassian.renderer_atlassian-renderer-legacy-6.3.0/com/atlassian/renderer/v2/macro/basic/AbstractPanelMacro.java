/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2.macro.basic;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.basic.CssSizeValue;
import com.atlassian.renderer.v2.macro.basic.validator.BorderStyleValidator;
import com.atlassian.renderer.v2.macro.basic.validator.ColorStyleValidator;
import com.atlassian.renderer.v2.macro.basic.validator.CssSizeValidator;
import com.atlassian.renderer.v2.macro.basic.validator.ValidatedMacroParameters;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractPanelMacro
extends BaseMacro {
    private SubRenderer subRenderer;

    protected abstract String getPanelCSSClass();

    protected abstract String getPanelHeaderCSSClass();

    protected abstract String getPanelContentCSSClass();

    public void setSubRenderer(SubRenderer subRenderer) {
        this.subRenderer = subRenderer;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    protected String getBodyContent(Map parameters, String body, RenderContext renderContext) throws MacroException {
        return body;
    }

    protected String getTitle(Map parameters, String body, RenderContext renderContext) {
        return (String)parameters.get("title");
    }

    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    @Override
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        StringBuffer buffer = new StringBuffer(body.length() + 100);
        String title = this.subRenderer.render(this.getTitle(parameters, body, renderContext), renderContext, renderContext.getRenderMode().and(RenderMode.INLINE));
        body = this.getBodyContent(parameters, body, renderContext);
        ValidatedMacroParameters validatedParameters = new ValidatedMacroParameters(parameters);
        validatedParameters.setValidator("borderStyle", BorderStyleValidator.getInstance());
        validatedParameters.setValidator("borderColor", ColorStyleValidator.getInstance());
        validatedParameters.setValidator("bgColor", ColorStyleValidator.getInstance());
        validatedParameters.setValidator("titleBGColor", ColorStyleValidator.getInstance());
        validatedParameters.setValidator("borderWidth", CssSizeValidator.getInstance());
        validatedParameters.setValidator("titleColor", ColorStyleValidator.getInstance());
        String borderStyle = validatedParameters.getValue("borderStyle");
        String borderColor = validatedParameters.getValue("borderColor");
        String backgroundColor = validatedParameters.getValue("bgColor");
        String titleBackgroundColor = validatedParameters.getValue("titleBGColor");
        String borderWidthString = validatedParameters.getValue("borderWidth");
        String titleColor = validatedParameters.getValue("titleColor");
        int borderWidth = 1;
        if (borderWidthString != null) {
            CssSizeValue cssBorderWidth = new CssSizeValue(borderWidthString);
            borderWidth = cssBorderWidth.value();
        }
        Map explicitStyles = this.prepareExplicitStyles(borderWidth, borderStyle, borderColor, backgroundColor);
        if (StringUtils.isBlank((String)titleBackgroundColor) && StringUtils.isNotBlank((String)backgroundColor)) {
            titleBackgroundColor = backgroundColor;
        }
        buffer.append("<div class=\"").append(this.getPanelCSSClass()).append("\"");
        if (explicitStyles.size() > 0) {
            this.handleExplicitStyles(buffer, explicitStyles);
        }
        buffer.append(">");
        if (StringUtils.isNotBlank((String)title)) {
            this.writeHeader(renderContext, buffer, title, borderStyle, borderColor, borderWidth, titleBackgroundColor, titleColor);
        }
        if (StringUtils.isNotBlank((String)body)) {
            this.writeContent(buffer, parameters, body, backgroundColor);
        }
        buffer.append("</div>");
        return buffer.toString();
    }

    private void handleExplicitStyles(StringBuffer buffer, Map explicitStyles) {
        buffer.append(" style=\"");
        for (String styleAttribute : explicitStyles.keySet()) {
            String styleValue = (String)explicitStyles.get(styleAttribute);
            buffer.append(styleAttribute).append(": ").append(styleValue).append(";");
        }
        buffer.append("\"");
    }

    private Map prepareExplicitStyles(int borderWidth, String borderStyle, String borderColor, String backgroundColor) {
        TreeMap<String, String> explicitStyles = new TreeMap<String, String>();
        explicitStyles.put("border-width", borderWidth + "px");
        if (borderWidth > 0) {
            if (StringUtils.isNotBlank((String)borderStyle)) {
                explicitStyles.put("border-style", borderStyle);
            }
            if (StringUtils.isNotBlank((String)borderColor)) {
                explicitStyles.put("border-color", borderColor);
            }
        } else if (borderWidth == 0) {
            explicitStyles.put("border-bottom", "1px solid white");
        }
        if (StringUtils.isNotBlank((String)backgroundColor)) {
            explicitStyles.put("background-color", backgroundColor);
        }
        return explicitStyles;
    }

    protected final void writeHeader(RenderContext renderContext, StringBuffer buffer, String title, String borderStyle, String borderColor, int borderWidth, String titleBackgroundColor) {
        this.writeHeader(renderContext, buffer, title, borderStyle, borderColor, borderWidth, titleBackgroundColor, "");
    }

    protected final void writeHeader(RenderContext renderContext, StringBuffer buffer, String title, String borderStyle, String borderColor, int borderWidth, String titleBackgroundColor, String titleColor) {
        buffer.append("<div class=\"").append(this.getPanelHeaderCSSClass()).append("\"").append(renderContext.isRenderingForWysiwyg() ? " wysiwyg=\"ignore\" " : "");
        buffer.append(" style=\"");
        buffer.append("border-bottom-width: ").append(borderWidth).append("px;");
        if (borderWidth > 0) {
            if (StringUtils.isNotBlank((String)borderStyle)) {
                buffer.append("border-bottom-style: ").append(borderStyle).append(";");
            }
            if (StringUtils.isNotBlank((String)borderColor)) {
                buffer.append("border-bottom-color: ").append(borderColor).append(";");
            }
        }
        if (StringUtils.isNotBlank((String)titleBackgroundColor)) {
            buffer.append("background-color: ").append(titleBackgroundColor).append(";");
        }
        if (StringUtils.isNotBlank((String)titleColor)) {
            buffer.append("color: ").append(titleColor).append(";");
        }
        buffer.append("\"");
        buffer.append("><b>");
        buffer.append(title);
        buffer.append("</b></div>");
    }

    protected void writeContent(StringBuffer buffer, Map parameters, String content, String backgroundColor) {
        buffer.append("<div class=\"").append(this.getPanelContentCSSClass()).append("\"");
        if (StringUtils.isNotBlank((String)backgroundColor)) {
            buffer.append(" style=\"background-color: ").append(backgroundColor).append(";\"");
        }
        buffer.append(">\n");
        buffer.append(content.trim());
        buffer.append("\n</div>");
    }

    protected SubRenderer getSubRenderer() {
        return this.subRenderer;
    }
}

