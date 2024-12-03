/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.google.common.collect.Maps
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.view.macro;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroWrapper;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class DefaultViewMacroWrapper
implements ViewMacroWrapper {
    private static final Logger log = LoggerFactory.getLogger(DefaultViewMacroWrapper.class);
    private final Map<String, Boolean> failedMacros = Maps.newConcurrentMap();
    private final HtmlToXmlConverter htmlToXmlConverter;
    private final PluginEventManager pluginEventManager;

    public DefaultViewMacroWrapper(HtmlToXmlConverter htmlToXmlConverter, PluginEventManager pluginEventManager) {
        this.htmlToXmlConverter = htmlToXmlConverter;
        this.pluginEventManager = pluginEventManager;
    }

    @PostConstruct
    public void init() {
        this.pluginEventManager.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.pluginEventManager.unregister((Object)this);
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent ignored) {
        this.failedMacros.clear();
    }

    @PluginEventListener
    public void onPluginModuleDisable(PluginModuleDisabledEvent ignored) {
        this.failedMacros.clear();
    }

    @Override
    public String wrap(ConversionContext context, Macro.OutputType outputType, String macroBody, MacroDefinition macroDefinition) {
        return this.wrapAndConvertMacro(context, outputType, macroBody, macroDefinition.getBody() != null, macroDefinition.getName());
    }

    private String wrapAndConvertMacro(ConversionContext context, Macro.OutputType outputType, String macroBody, boolean hasBody, String macroName) {
        MacroMetadata metadata = (MacroMetadata)context.getProperty("macroMetadata");
        if (metadata != null && Boolean.TRUE.equals(this.failedMacros.get(metadata.toString()))) {
            return macroBody;
        }
        try {
            return this.htmlToXmlConverter.convert(DefaultViewMacroWrapper.wrapMacroBody(macroBody, hasBody, macroName, outputType));
        }
        catch (Exception e) {
            log.error("Error wrapping or converting macro [{}] due to [{}].", (Object)macroName, (Object)e.getMessage());
            if (metadata != null) {
                this.failedMacros.put(metadata.toString(), Boolean.TRUE);
            }
            return macroBody;
        }
    }

    private static String wrapMacroBody(String macroBody, boolean hasBody, String macroName, Macro.OutputType outputType) {
        if (macroBody.trim().isEmpty()) {
            return "";
        }
        Document doc = Jsoup.parse((String)macroBody);
        doc.outputSettings().prettyPrint(false);
        Element bodyEl = doc.body();
        bodyEl.prependChildren((Collection)doc.head().children());
        Element rootElement = bodyEl.firstElementChild();
        if (bodyEl.childrenSize() != 1 || !bodyEl.html().trim().equals(rootElement.outerHtml())) {
            rootElement = bodyEl;
            outputType = rootElement.childrenSize() == 0 ? Macro.OutputType.INLINE : outputType;
            rootElement.tagName(Macro.OutputType.INLINE.equals((Object)outputType) ? "span" : "div");
        }
        rootElement.addClass("conf-macro");
        if (Macro.OutputType.INLINE.equals((Object)outputType)) {
            rootElement.addClass("output-inline");
        } else if (Macro.OutputType.BLOCK.equals((Object)outputType)) {
            rootElement.addClass("output-block");
        }
        rootElement.attr("data-hasbody", hasBody ? "true" : "false");
        rootElement.attr("data-macro-name", macroName);
        return rootElement.outerHtml();
    }
}

