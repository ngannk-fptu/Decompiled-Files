/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.macro.basic.CssSizeValue
 *  com.atlassian.renderer.v2.macro.basic.validator.BorderStyleValidator
 *  com.atlassian.renderer.v2.macro.basic.validator.ColorStyleValidator
 *  com.atlassian.renderer.v2.macro.basic.validator.CssSizeValidator
 *  com.atlassian.renderer.v2.macro.basic.validator.MacroParameterValidationException
 *  com.atlassian.renderer.v2.macro.basic.validator.ParameterValidator
 *  com.atlassian.renderer.v2.macro.basic.validator.ValidatedMacroParameters
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.basic.CssSizeValue;
import com.atlassian.renderer.v2.macro.basic.validator.BorderStyleValidator;
import com.atlassian.renderer.v2.macro.basic.validator.ColorStyleValidator;
import com.atlassian.renderer.v2.macro.basic.validator.CssSizeValidator;
import com.atlassian.renderer.v2.macro.basic.validator.MacroParameterValidationException;
import com.atlassian.renderer.v2.macro.basic.validator.ParameterValidator;
import com.atlassian.renderer.v2.macro.basic.validator.ValidatedMacroParameters;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public abstract class AbstractPanelMacro
implements Macro {
    protected abstract String getPanelCSSClass();

    protected abstract String getPanelHeaderCSSClass();

    protected abstract String getPanelContentCSSClass();

    protected String getBodyContent(Map<String, String> parameters, String body, ConversionContext conversionContext) {
        return body;
    }

    protected String getTitle(Map<String, String> parameters, String body) {
        return parameters.get("title");
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        String borderWidthString;
        String titleBackgroundColor;
        String backgroundColor;
        String borderColor;
        String borderStyle;
        StringBuilder buffer = new StringBuilder(body.length() + 100);
        body = this.getBodyContent(parameters, body, conversionContext);
        ValidatedMacroParameters validatedParameters = new ValidatedMacroParameters(parameters);
        validatedParameters.setValidator("borderStyle", (ParameterValidator)BorderStyleValidator.getInstance());
        validatedParameters.setValidator("borderColor", (ParameterValidator)ColorStyleValidator.getInstance());
        validatedParameters.setValidator("bgColor", (ParameterValidator)ColorStyleValidator.getInstance());
        validatedParameters.setValidator("titleBGColor", (ParameterValidator)ColorStyleValidator.getInstance());
        validatedParameters.setValidator("borderWidth", (ParameterValidator)CssSizeValidator.getInstance());
        try {
            borderStyle = validatedParameters.getValue("borderStyle");
            borderColor = validatedParameters.getValue("borderColor");
            backgroundColor = validatedParameters.getValue("bgColor");
            titleBackgroundColor = validatedParameters.getValue("titleBGColor");
            borderWidthString = validatedParameters.getValue("borderWidth");
        }
        catch (MacroParameterValidationException e) {
            throw new MacroExecutionException((Throwable)e);
        }
        int borderWidth = 1;
        if (borderWidthString != null) {
            CssSizeValue cssBorderWidth = new CssSizeValue(borderWidthString);
            borderWidth = cssBorderWidth.value();
        }
        Map<String, String> explicitStyles = this.prepareExplicitStyles(borderWidth, borderStyle, borderColor, backgroundColor);
        if (StringUtils.isBlank((CharSequence)titleBackgroundColor) && StringUtils.isNotBlank((CharSequence)backgroundColor)) {
            titleBackgroundColor = backgroundColor;
        }
        buffer.append("<div class=\"").append(this.getPanelCSSClass()).append("\"");
        if (explicitStyles.size() > 0) {
            this.handleExplicitStyles(buffer, explicitStyles);
        }
        buffer.append(">");
        String title = this.getTitle(parameters, body);
        if (StringUtils.isNotBlank((CharSequence)title)) {
            this.writeHeader((RenderContext)conversionContext.getPageContext(), buffer, title, borderStyle, borderColor, borderWidth, titleBackgroundColor);
        }
        if (StringUtils.isNotBlank((CharSequence)body)) {
            this.writeContent(buffer, parameters, body, backgroundColor);
        }
        buffer.append("</div>");
        return buffer.toString();
    }

    private void handleExplicitStyles(StringBuilder buffer, Map<String, String> explicitStyles) {
        buffer.append(" style=\"");
        for (String styleAttribute : explicitStyles.keySet()) {
            String styleValue = explicitStyles.get(styleAttribute);
            buffer.append(styleAttribute).append(": ").append(styleValue).append(";");
        }
        buffer.append("\"");
    }

    private Map<String, String> prepareExplicitStyles(int borderWidth, String borderStyle, String borderColor, String backgroundColor) {
        TreeMap<String, String> explicitStyles = new TreeMap<String, String>();
        explicitStyles.put("border-width", borderWidth + "px");
        if (borderWidth > 0) {
            if (StringUtils.isNotBlank((CharSequence)borderStyle)) {
                explicitStyles.put("border-style", borderStyle);
            }
            if (StringUtils.isNotBlank((CharSequence)borderColor)) {
                explicitStyles.put("border-color", borderColor);
            }
        } else if (borderWidth == 0) {
            explicitStyles.put("border-bottom", "1px solid white");
        }
        if (StringUtils.isNotBlank((CharSequence)backgroundColor)) {
            explicitStyles.put("background-color", backgroundColor);
        }
        return explicitStyles;
    }

    protected void writeHeader(RenderContext renderContext, StringBuilder buffer, String title, String borderStyle, String borderColor, int borderWidth, String titleBackgroundColor) {
        buffer.append("<div class=\"").append(this.getPanelHeaderCSSClass()).append("\"").append(renderContext.isRenderingForWysiwyg() ? " wysiwyg=\"ignore\" " : "");
        buffer.append(" style=\"");
        buffer.append("border-bottom-width: ").append(borderWidth).append("px;");
        if (borderWidth > 0) {
            if (StringUtils.isNotBlank((CharSequence)borderStyle)) {
                buffer.append("border-bottom-style: ").append(borderStyle).append(";");
            }
            if (StringUtils.isNotBlank((CharSequence)borderColor)) {
                buffer.append("border-bottom-color: ").append(borderColor).append(";");
            }
        }
        if (StringUtils.isNotBlank((CharSequence)titleBackgroundColor)) {
            buffer.append("background-color: ").append(titleBackgroundColor).append(";");
        }
        buffer.append("\"");
        buffer.append("><b>");
        buffer.append(StringEscapeUtils.escapeHtml4((String)title));
        buffer.append("</b></div>");
    }

    protected void writeContent(StringBuilder buffer, Map parameters, String content, String backgroundColor) {
        buffer.append("<div class=\"").append(this.getPanelContentCSSClass()).append("\"");
        if (StringUtils.isNotBlank((CharSequence)backgroundColor)) {
            buffer.append(" style=\"background-color: ").append(backgroundColor).append(";\"");
        }
        buffer.append(">\n");
        buffer.append(content.trim());
        buffer.append("\n</div>");
    }
}

