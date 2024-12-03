/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionHandler
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.WikiMarkupParser
 *  com.atlassian.renderer.v2.components.MacroTag
 *  com.atlassian.renderer.v2.components.WikiContentHandler
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.dashboard.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.WikiMarkupParser;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentHandler;
import com.atlassian.renderer.v2.macro.MacroManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentMacroNamesParser {
    private static final Logger log = LoggerFactory.getLogger(ContentMacroNamesParser.class);
    private final XhtmlContent xhtmlContent;
    private final MacroManager macroManager;

    public ContentMacroNamesParser(XhtmlContent xhtmlContent, MacroManager macroManager) {
        this.xhtmlContent = xhtmlContent;
        this.macroManager = macroManager;
    }

    public List<String> getMacroNames(ContentEntityObject ceo) {
        HashSet<String> macroNames = new HashSet<String>();
        BodyType bodyType = ceo.getBodyContent().getBodyType();
        if (bodyType.equals((Object)BodyType.XHTML)) {
            this.processXhtml(ceo, macro -> this.handleMacroDefinition(macro, macroNames));
        } else if (bodyType.equals((Object)BodyType.WIKI)) {
            this.processPotentialWikiMacro(ceo.getBodyAsString(), macroNames);
        }
        return new ArrayList<String>(macroNames);
    }

    private void processXhtml(ContentEntityObject ceo, MacroDefinitionHandler macroUsageCollector) {
        DefaultConversionContext context = new DefaultConversionContext((RenderContext)ceo.toPageContext());
        try {
            this.xhtmlContent.handleMacroDefinitions(ceo.getBodyAsString(), (ConversionContext)context, macroUsageCollector);
        }
        catch (XhtmlException ex) {
            log.warn("Failed to extracting macro usages on entity [{}] : {}", (Object)ceo.getId(), (Object)ex.getMessage());
            log.debug("Failed to extracting macro usages on entity [{}] : {}", (Object)ceo.getId(), (Object)ex);
        }
    }

    private void handleMacroDefinition(MacroDefinition macro, Collection<String> macroNames) {
        macroNames.add(macro.getName());
        if (macro.getName().equals("unmigrated-wiki-markup")) {
            this.processPotentialWikiMacro(macro.getBodyText(), macroNames);
        }
    }

    private void processPotentialWikiMacro(String wiki, final Collection<String> macroNames) {
        WikiContentHandler contentHandler = new WikiContentHandler(){

            public void handleMacro(StringBuffer buffer, MacroTag macroTag, String body) {
                macroNames.add(macroTag.command);
                if (body != null) {
                    ContentMacroNamesParser.this.processPotentialWikiMacro(body, macroNames);
                }
            }

            public void handleText(StringBuffer buffer, String s) {
            }
        };
        WikiMarkupParser parser = new WikiMarkupParser(this.macroManager, contentHandler);
        parser.parse(wiki);
    }
}

