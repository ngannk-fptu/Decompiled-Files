/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.WikiMarkupParser;
import com.atlassian.renderer.v2.components.AbstractRendererComponent;
import com.atlassian.renderer.v2.components.HtmlEscapeRendererComponent;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentRendererHandler;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.MacroManager;
import com.atlassian.renderer.wysiwyg.WysiwygMacroHelper;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroRendererComponent
extends AbstractRendererComponent {
    private static final Logger log = LoggerFactory.getLogger(MacroRendererComponent.class);
    private final MacroManager macroManager;
    private final SubRenderer subRenderer;
    private final WysiwygMacroHelper wysiwygMacroHelper;

    public MacroRendererComponent(MacroManager macroManager, SubRenderer subRenderer) {
        this.macroManager = macroManager;
        this.subRenderer = subRenderer;
        this.wysiwygMacroHelper = new WysiwygMacroHelper(this);
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderMacros();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        WikiMarkupParser parser = new WikiMarkupParser(this.macroManager, new WikiContentRendererHandler(this, context));
        return parser.parse(wiki);
    }

    public void makeMacro(StringBuffer buffer, MacroTag startTag, String body, RenderContext context) {
        Macro macro = this.getMacroByName(startTag.command);
        Map params = this.makeParams(startTag.argString);
        if (context.isRenderingForWysiwyg()) {
            this.wysiwygMacroHelper.renderMacro(startTag, macro, body, params, context, buffer);
        } else if (macro != null) {
            this.processMacro(startTag.command, macro, body, params, context, buffer);
        } else {
            this.handleUnknownMacroTag(buffer, startTag, body, context);
        }
    }

    private void handleUnknownMacroTag(StringBuffer buffer, MacroTag startTag, String body, RenderContext context) {
        if (!context.getRenderMode().renderMacroErrorMessages()) {
            HtmlEscapeRendererComponent htmlEscapeRendererComponent = new HtmlEscapeRendererComponent();
            StringBuffer errorBuffer = new StringBuffer();
            errorBuffer.append(htmlEscapeRendererComponent.render(startTag.originalText, context));
            if (StringUtils.isNotBlank((String)body)) {
                errorBuffer.append(this.subRenderer.render(body, context, context.getRenderMode().and(RenderMode.suppress(1L))));
                errorBuffer.append("{").append(htmlEscapeRendererComponent.render(startTag.command, context)).append("}");
            }
            buffer.append(context.addRenderedContent(errorBuffer.toString()));
        } else {
            buffer.append(this.makeMacroError(context, "Unknown macro: {" + startTag.command + "}", body));
        }
    }

    private Macro getMacroByName(String name) {
        if (name == null) {
            return null;
        }
        return this.macroManager.getEnabledMacro(name.toLowerCase());
    }

    private Map makeParams(String paramString) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(": = | RAW | = :", paramString == null ? "" : paramString);
        if (StringUtils.isEmpty((String)paramString)) {
            return params;
        }
        String[] paramStrs = paramString.split("\\|");
        for (int i = 0; i < paramStrs.length; ++i) {
            String paramStr = paramStrs[i];
            int idx = paramStr.indexOf("=");
            if (idx != -1) {
                if (idx == paramStr.length() - 1) {
                    params.put(paramStr.substring(0, idx).trim(), "");
                    continue;
                }
                params.put(paramStr.substring(0, idx).trim(), paramStr.substring(idx + 1).trim());
                continue;
            }
            params.put(String.valueOf(i), paramStr);
        }
        return params;
    }

    public void processMacro(String command, Macro macro, String body, Map params, RenderContext context, StringBuffer buffer) {
        String renderedBody = body;
        try {
            if (StringUtils.isNotEmpty((String)body) && macro.getBodyRenderMode() != null && !macro.getBodyRenderMode().renderNothing()) {
                RenderMode macroMode = macro.getBodyRenderMode();
                if (context.isRenderingForWysiwyg() && macroMode.renderParagraphs()) {
                    renderedBody = RenderUtils.trimInitialNewline(renderedBody);
                }
                renderedBody = this.subRenderer.render(renderedBody, context, macroMode);
            }
            String macroResult = this.executeMacro(command, macro, params, renderedBody, context);
            if (macro.getBodyRenderMode() == null) {
                buffer.append(this.subRenderer.render(macroResult, context, RenderMode.MACROS_ONLY));
            } else {
                buffer.append(context.addRenderedContent(macroResult, macro.getTokenType(params, body, context)));
            }
        }
        catch (MacroException e) {
            log.info("Error rendering macro: " + command + ": " + e.getMessage());
            log.debug("Error rendering macro: " + command, (Throwable)e);
            buffer.append(this.makeMacroError(context, command + ": " + e.getMessage(), body));
        }
        catch (Throwable t) {
            log.error("Unexpected error rendering macro '{}': {}'", (Object)command, (Object)t.getMessage());
            log.debug("Unexpected error rendering macro '" + command + "'", t);
            buffer.append(this.makeMacroError(context, "Error formatting macro: " + command + ": " + t.toString(), body));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String executeMacro(String command, Macro macro, Map params, String renderedBody, RenderContext context) throws MacroException {
        String string;
        String profilingName = "Rendering macro: {" + command + "}";
        long startTime = System.currentTimeMillis();
        try {
            UtilTimerStack.push((String)profilingName);
            string = macro.execute(params, renderedBody, context);
        }
        catch (Throwable throwable) {
            log.debug("Rendering macro \\{{}} took {} ms with parameters: {}", new Object[]{command, System.currentTimeMillis() - startTime, params});
            UtilTimerStack.pop((String)profilingName);
            throw throwable;
        }
        log.debug("Rendering macro \\{{}} took {} ms with parameters: {}", new Object[]{command, System.currentTimeMillis() - startTime, params});
        UtilTimerStack.pop((String)profilingName);
        return string;
    }

    private String makeMacroError(RenderContext context, String errorMessage, String body) {
        return context.addRenderedContent(RenderUtils.blockError(errorMessage, this.renderErrorBody(body, context)));
    }

    private String renderErrorBody(String body, RenderContext context) {
        return context.addRenderedContent(this.subRenderer.render(body, context, null));
    }

    public SubRenderer getSubRenderer() {
        return this.subRenderer;
    }
}

