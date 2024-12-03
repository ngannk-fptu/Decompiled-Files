/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.ArrayUtils
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer.wysiwyg;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.macro.RadeoxCompatibilityMacro;
import com.atlassian.renderer.util.NodeUtil;
import com.atlassian.renderer.util.RendererUtil;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.renderer.v2.components.MacroRendererComponent;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.ResourceAwareMacroDecorator;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;
import com.atlassian.renderer.wysiwyg.MacroBodyConverter;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class WysiwygMacroHelper {
    private static final Logger log = LoggerFactory.getLogger(WysiwygMacroHelper.class);
    public static final String MACRO_TAG_PARAM = "MACRO_TAG_PARAM";
    public static final String MACRO_CLASS = "wysiwyg-macro";
    private static final String MACRO_INLINE_CLASS = "wysiwyg-macro-inline";
    private static final String MACRO_BODY_CLASS = "wysiwyg-macro-body";
    private static final String MACRO_BODY_PREFORMAT_CLASS = "wysiwyg-macro-body-preformat";
    private static final String MACRO_TAG_CLASS = "wysiwyg-macro-tag";
    private static final String MACRO_START_TAG_CLASS = "wysiwyg-macro-starttag";
    private static final String MACRO_END_TAG_CLASS = "wysiwyg-macro-endtag";
    private static final String MACRO_BODY_BREAK_CLASS = "wysiwyg-macro-body-newline";
    public static final String MACRO_NAME_ATTRIBUTE = "macroname";
    public static final String MACRO_START_TAG_ATTRIBUTE = "macrostarttag";
    public static final String MACRO_HAS_BODY_ATTRIBUTE = "macrohasbody";
    public static final String MACRO_HAS_NEWLINE_BEFORE_BODY_ATTRIBUTE = "wikihasnewlinebeforebody";
    public static final String MACRO_HAS_NEWLINE_AFTER_BODY_ATTRIBUTE = "wikihasnewlineafterbody";
    public static final String MACRO_HAS_PRECEDING_NEWLINE_ATTRIBUTE = "wikihasprecedingnewline";
    public static final String MACRO_HAS_TRAILING_NEWLINE_ATTRIBUTE = "wikihastrailingnewline";
    private static final String NEW_LINE = "\n";
    private final MacroRendererComponent macroRendererComponent;
    private static final String CLASS_ATTRIBUTE_NAME = "class";
    public static final String MACRO_PADDING_CLASS = "atl_conf_pad";

    public WysiwygMacroHelper(MacroRendererComponent macroRendererComponent) {
        this.macroRendererComponent = macroRendererComponent;
    }

    public void renderMacro(MacroTag startTag, Macro macro, String body, Map params, RenderContext context, StringBuffer buffer) {
        if (macro != null && macro.suppressSurroundingTagDuringWysiwygRendering()) {
            this.renderMacroResponsibleForOwnRendering(startTag, macro, body, params, context, buffer);
            switch (macro.getTokenType(params, body, context)) {
                case INLINE: {
                    break;
                }
                case INLINE_BLOCK: 
                case BLOCK: {
                    this.padForCursorPlacement(context, buffer);
                }
                default: {
                    log.warn("Unexpected tokenType:" + (Object)((Object)macro.getTokenType(params, body, context)));
                }
            }
            return;
        }
        this.renderWithSurroundingHtmlTag(startTag, macro, body, params, context, buffer);
    }

    private void renderWithSurroundingHtmlTag(MacroTag startTag, Macro macro, String body, Map params, RenderContext context, StringBuffer buffer) {
        StringBuffer result = new StringBuffer();
        result.append(this.createWrappingDivStartElement(startTag, macro, params, body, context));
        if (!this.requiresWikiTags(macro)) {
            this.renderWithoutWikiTags(startTag, macro, body, params, context, result);
        } else {
            this.renderWithWikiTags(startTag, macro, body, context, result);
        }
        result.append("</div>");
        String resultToken = context.addRenderedContent(result.toString(), TokenType.BLOCK);
        buffer.append(resultToken);
        this.padForCursorPlacement(context, buffer);
    }

    private boolean requiresWikiTags(Macro macro) {
        return macro == null || macro.suppressMacroRenderingDuringWysiwyg();
    }

    private void renderWithWikiTags(MacroTag startTag, Macro macro, String body, RenderContext context, StringBuffer result) {
        this.addStartMacroWikiMarkup(startTag, result);
        this.renderBody(startTag, macro, body, context, result);
        if (startTag.getEndTag() != null) {
            if (startTag.getEndTag().isNewlineBefore()) {
                result.append("<br class=\"wysiwyg-macro-body-newline\"/>");
            }
            this.addEndMacroWikiMarkup(startTag, result);
        }
    }

    private void renderBody(MacroTag startTag, Macro macro, String body, RenderContext context, StringBuffer result) {
        if (!this.shouldRenderBody(startTag, macro, body)) {
            return;
        }
        if (startTag.isNewlineAfter()) {
            result.append("<br class=\"wysiwyg-macro-body-newline\"/>");
        }
        body = RenderUtils.trimInitialNewline(body);
        body = StringUtils.chomp((String)body, (String)NEW_LINE);
        if (macro != null && macro.getWysiwygBodyType() == WysiwygBodyType.PREFORMAT) {
            WysiwygMacroHelper.appendDivStartWithClasses(result, MACRO_BODY_CLASS, MACRO_BODY_PREFORMAT_CLASS, "preformattedContent");
            result.append("<pre>");
            RenderMode renderMode = RenderMode.allow(128L);
            result.append(this.macroRendererComponent.getSubRenderer().render(body, context, renderMode));
            result.append("</pre>");
            result.append("</div>");
        } else {
            WysiwygMacroHelper.appendDivStartWithClasses(result, MACRO_BODY_CLASS);
            RenderMode renderMode = RenderMode.ALL;
            result.append(this.macroRendererComponent.getSubRenderer().render(body, context, renderMode));
            result.append("</div>");
        }
    }

    private boolean shouldRenderBody(MacroTag startTag, Macro macro, String body) {
        if (macro != null && !macro.hasBody()) {
            if (StringUtils.isNotEmpty((String)body) && log.isWarnEnabled()) {
                log.warn(startTag.command + " macro declares it doesn't take a body, so ignoring: " + body);
            }
            return false;
        }
        if (macro == null && StringUtils.isEmpty((String)body)) {
            return false;
        }
        return !WysiwygMacroHelper.isRadeoxCompatibilityMacroOrDecoratingOne(macro) || !StringUtils.isEmpty((String)body);
    }

    private static void appendDivStartWithClasses(StringBuffer result, String ... classNames) {
        result.append("<div class=\"");
        result.append(StringUtils.join((Object[])classNames, (char)' '));
        result.append("\">");
    }

    private void renderWithoutWikiTags(MacroTag startTag, Macro macro, String body, Map params, RenderContext context, StringBuffer result) {
        this.macroRendererComponent.processMacro(startTag.command, macro, body, params, context, result);
    }

    private void addStartMacroWikiMarkup(MacroTag startTag, StringBuffer buffer) {
        String escapedStartTag = HtmlEscaper.escapeAll(startTag.originalText, false);
        WysiwygMacroHelper.appendDivStartWithClasses(buffer, MACRO_TAG_CLASS, MACRO_START_TAG_CLASS);
        buffer.append(escapedStartTag);
        buffer.append("</div>");
    }

    private void addEndMacroWikiMarkup(MacroTag macroTag, StringBuffer result) {
        String escapedMacroTag = HtmlEscaper.escapeAll(macroTag.command, false);
        WysiwygMacroHelper.appendDivStartWithClasses(result, MACRO_TAG_CLASS, MACRO_END_TAG_CLASS);
        result.append("{").append(escapedMacroTag).append("}");
        result.append("</div>");
    }

    private void padForCursorPlacement(RenderContext context, StringBuffer buffer) {
        buffer.append(context.addRenderedContent("<p class=\"atl_conf_pad\"> </p>", TokenType.BLOCK));
    }

    private void renderMacroResponsibleForOwnRendering(MacroTag startTag, Macro macro, String body, Map<?, ?> params, RenderContext context, StringBuffer buffer) {
        HashMap amendedParams = new HashMap(params);
        amendedParams.put(MACRO_TAG_PARAM, startTag);
        String escapedMacroTag = HtmlEscaper.escapeAll(startTag.command, false);
        this.macroRendererComponent.processMacro(escapedMacroTag, macro, body, amendedParams, context, buffer);
    }

    private String createWrappingDivStartElement(MacroTag macroTag, Macro macro, Map params, String body, RenderContext context) {
        StringBuffer buffer = new StringBuffer("<div ");
        if (macro != null && macro.getTokenType(params, body, context) == TokenType.INLINE) {
            RendererUtil.appendAttribute(CLASS_ATTRIBUTE_NAME, "wysiwyg-macro wysiwyg-macro-inline", buffer);
        } else {
            RendererUtil.appendAttribute(CLASS_ATTRIBUTE_NAME, MACRO_CLASS, buffer);
        }
        RendererUtil.appendAttribute(MACRO_NAME_ATTRIBUTE, HtmlEscaper.escapeAll(macroTag.command, false), buffer);
        if (!this.requiresWikiTags(macro)) {
            String escapedStartTag = HtmlEscaper.escapeAll(macroTag.originalText, false);
            RendererUtil.appendAttribute(MACRO_START_TAG_ATTRIBUTE, escapedStartTag, buffer);
        }
        MacroTag endTag = macroTag.getEndTag();
        boolean hasBody = macro != null && macro.hasBody() || macro == null && endTag != null;
        RendererUtil.appendAttribute(MACRO_HAS_BODY_ATTRIBUTE, hasBody, buffer);
        if (hasBody && !this.requiresWikiTags(macro)) {
            RendererUtil.appendAttribute(MACRO_HAS_NEWLINE_BEFORE_BODY_ATTRIBUTE, macroTag.isNewlineAfter(), buffer);
            if (endTag != null) {
                RendererUtil.appendAttribute(MACRO_HAS_NEWLINE_AFTER_BODY_ATTRIBUTE, endTag.isNewlineBefore(), buffer);
            }
        }
        boolean hasTrailingNewline = endTag == null ? macroTag.isNewlineAfter() : endTag.isNewlineAfter();
        RendererUtil.appendAttribute(MACRO_HAS_PRECEDING_NEWLINE_ATTRIBUTE, macroTag.isNewlineBefore(), buffer);
        RendererUtil.appendAttribute(MACRO_HAS_TRAILING_NEWLINE_ATTRIBUTE, hasTrailingNewline, buffer);
        buffer.append(">");
        return buffer.toString();
    }

    private static boolean isRadeoxCompatibilityMacroOrDecoratingOne(Macro macro) {
        if (macro instanceof RadeoxCompatibilityMacro) {
            return true;
        }
        if (macro instanceof ResourceAwareMacroDecorator) {
            Macro decoratedMacro = ((ResourceAwareMacroDecorator)macro).getMacro();
            return decoratedMacro instanceof RadeoxCompatibilityMacro;
        }
        return false;
    }

    public static String getMacroName(Node node) {
        return NodeUtil.getAttribute(node, MACRO_NAME_ATTRIBUTE);
    }

    public static boolean isMacroBody(Node node) {
        String classValue = NodeUtil.getAttribute(node, CLASS_ATTRIBUTE_NAME);
        return classValue != null && ArrayUtils.contains((Object[])classValue.split(" "), (Object)MACRO_BODY_CLASS);
    }

    public static boolean isMacroTag(Node node) {
        String classValue = NodeUtil.getAttribute(node, CLASS_ATTRIBUTE_NAME);
        return classValue != null && ArrayUtils.contains((Object[])classValue.split(" "), (Object)MACRO_TAG_CLASS);
    }

    public static String convertMacroFromNode(NodeContext nodeContext, DefaultWysiwygConverter defaultWysiwygConverter, Macro macro) {
        String startTagText;
        StringBuffer result = new StringBuffer();
        if (WysiwygMacroHelper.shouldPadMacroWithPrecedingNewline(nodeContext)) {
            result.append(NEW_LINE);
        }
        if ((startTagText = nodeContext.getAttribute(MACRO_START_TAG_ATTRIBUTE)) != null) {
            WysiwygMacroHelper.convertWithoutWikiTags(nodeContext, defaultWysiwygConverter, macro, result, startTagText);
        } else {
            WysiwygMacroHelper.convertWithWikiTags(nodeContext, defaultWysiwygConverter, macro, result);
        }
        return result.toString();
    }

    private static boolean shouldPadMacroWithPrecedingNewline(NodeContext nodeContext) {
        if (!nodeContext.getBooleanAttributeValue(MACRO_HAS_PRECEDING_NEWLINE_ATTRIBUTE, true)) {
            return false;
        }
        Node previousSibling = nodeContext.getPreviousSibling();
        if (previousSibling == null) {
            return false;
        }
        return !DefaultWysiwygConverter.isUserNewline(previousSibling);
    }

    private static void convertWithoutWikiTags(NodeContext nodeContext, DefaultWysiwygConverter defaultWysiwygConverter, Macro macro, StringBuffer result, String startTagText) {
        boolean hasBody = nodeContext.getBooleanAttributeValue(MACRO_HAS_BODY_ATTRIBUTE, false);
        boolean hasNewLineBeforeBody = hasBody && nodeContext.getBooleanAttributeValue(MACRO_HAS_NEWLINE_BEFORE_BODY_ATTRIBUTE, true);
        boolean hasNewLineAfterBody = nodeContext.getBooleanAttributeValue(MACRO_HAS_NEWLINE_AFTER_BODY_ATTRIBUTE, true);
        result.append(startTagText);
        if (hasNewLineBeforeBody) {
            result.append(NEW_LINE);
        }
        if (!hasBody) {
            return;
        }
        String body = WysiwygMacroHelper.convertMacroBodyWithoutWikiTags(nodeContext, defaultWysiwygConverter, macro);
        if (body != null) {
            result.append(body);
            if (hasNewLineAfterBody) {
                result.append(NEW_LINE);
            }
            result.append("{").append(NodeUtil.getAttribute(nodeContext.getNode(), MACRO_NAME_ATTRIBUTE)).append("}");
        }
    }

    private static void convertWithWikiTags(NodeContext nodeContext, DefaultWysiwygConverter defaultWysiwygConverter, Macro macro, StringBuffer result) {
        if (macro == null) {
            result.append(WysiwygMacroHelper.convertMacroBody(nodeContext, defaultWysiwygConverter));
        } else if (!macro.suppressSurroundingTagDuringWysiwygRendering()) {
            if (!macro.hasBody()) {
                result.append(DefaultWysiwygConverter.getRawChildText(nodeContext.getNode(), true));
            } else if (WysiwygMacroHelper.getMacroBodyConverter(macro) != null) {
                MacroBodyConverter macroBodyConverter = WysiwygMacroHelper.getMacroBodyConverter(macro);
                result.append(macroBodyConverter.convertXhtmlToWikiMarkup(nodeContext, defaultWysiwygConverter));
            } else {
                result.append(WysiwygMacroHelper.convertMacroBody(nodeContext, defaultWysiwygConverter));
            }
        }
    }

    private static String convertMacroBodyWithoutWikiTags(NodeContext nodeContext, DefaultWysiwygConverter defaultWysiwygConverter, Macro macro) {
        if (macro.suppressSurroundingTagDuringWysiwygRendering()) {
            return null;
        }
        if (!macro.hasBody()) {
            return null;
        }
        MacroBodyConverter macroBodyConverter = WysiwygMacroHelper.getMacroBodyConverter(macro);
        if (macroBodyConverter != null) {
            return macroBodyConverter.convertXhtmlToWikiMarkup(nodeContext, defaultWysiwygConverter);
        }
        return WysiwygMacroHelper.convertMacroBody(nodeContext, defaultWysiwygConverter);
    }

    private static MacroBodyConverter getMacroBodyConverter(Macro macro) {
        if (macro instanceof ResourceAwareMacroDecorator) {
            macro = ((ResourceAwareMacroDecorator)macro).getMacro();
        }
        if (macro instanceof MacroBodyConverter) {
            return (MacroBodyConverter)((Object)macro);
        }
        return null;
    }

    private static String convertMacroBody(NodeContext nodeContext, DefaultWysiwygConverter defaultWysiwygConverter) {
        Node node = nodeContext.getNode();
        StringBuffer wikiText = new StringBuffer();
        if (node != null && node.getChildNodes() != null) {
            NodeContext childContext = nodeContext.getFirstChildNodeContext();
            while (childContext != null) {
                if (WysiwygMacroHelper.isMacroTag(childContext.getNode())) {
                    wikiText.append(DefaultWysiwygConverter.getRawChildText(childContext.getNode(), false));
                } else {
                    wikiText.append(defaultWysiwygConverter.convertNode(childContext));
                }
                childContext = nodeContext.getNodeContextForNextChild(childContext);
            }
        }
        return RenderUtils.trimNewlinesAndEscapedNewlines(wikiText.toString());
    }
}

