/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.search.v2.SearchFieldMappings
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.WikiMarkupParser
 *  com.atlassian.renderer.v2.components.MacroTag
 *  com.atlassian.renderer.v2.components.WikiContentHandler
 *  com.atlassian.renderer.v2.macro.MacroManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macrousage.extractor;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.WikiMarkupParser;
import com.atlassian.renderer.v2.components.MacroTag;
import com.atlassian.renderer.v2.components.WikiContentHandler;
import com.atlassian.renderer.v2.macro.MacroManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroExtractor
implements Extractor2 {
    private static final Logger log = LoggerFactory.getLogger(MacroExtractor.class);
    private final XhtmlContent xhtmlContent;
    private final MacroManager macroManager;

    public MacroExtractor(XhtmlContent xhtmlContent, MacroManager macroManager) {
        this.xhtmlContent = xhtmlContent;
        this.macroManager = macroManager;
    }

    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof ContentEntityObject)) {
            return Collections.emptyList();
        }
        ArrayList macroDefinitions = new ArrayList();
        this.extractMacros((ContentEntityObject)searchable, macroDefinitions::add);
        return macroDefinitions.stream().flatMap(md -> Stream.of(SearchFieldMappings.MACRO_NAME.createField(md.getName()), SearchFieldMappings.MACRO_STORAGE_VERSION.createField(md.getStorageVersion() + "_" + md.getName()))).collect(Collectors.toList());
    }

    void extractMacros(ContentEntityObject searchable, Consumer<MacroDefinition> collector) {
        BodyType bodyType = searchable.getBodyContent().getBodyType();
        if (bodyType.equals((Object)BodyType.WIKI)) {
            this.extractWikiMacros(searchable.getBodyAsString(), collector);
        } else if (bodyType.equals((Object)BodyType.XHTML)) {
            this.extractXhtmlMacros(searchable, collector);
        } else {
            log.debug("Skipping macro extraction for entity '{}' of body type '{}'", (Object)searchable.getId(), (Object)bodyType);
        }
    }

    private void extractWikiMacros(String bodyContents, final Consumer<MacroDefinition> collector) {
        if (bodyContents == null) {
            return;
        }
        WikiMarkupParser wikiParser = new WikiMarkupParser(this.macroManager, new WikiContentHandler(){

            public void handleMacro(StringBuffer buffer, MacroTag macroTag, String body) {
                collector.accept(MacroDefinition.builder().withName(macroTag.command).withStorageVersion("0").build());
                MacroExtractor.this.extractWikiMacros(body, collector);
            }

            public void handleText(StringBuffer buffer, String s) {
            }
        });
        wikiParser.parse(bodyContents);
    }

    private void extractXhtmlMacros(ContentEntityObject searchable, Consumer<MacroDefinition> collector) {
        DefaultConversionContext context = new DefaultConversionContext((RenderContext)searchable.toPageContext());
        try {
            this.xhtmlContent.handleMacroDefinitions(searchable.getBodyAsString(), (ConversionContext)context, macroDefinition -> {
                collector.accept(macroDefinition);
                String macroName = macroDefinition.getName();
                if (macroName.equals("unmigrated-wiki-markup") || macroName.equals("unmigrated-inline-wiki-markup")) {
                    this.extractWikiMacros(macroDefinition.getBodyText(), collector);
                }
            });
        }
        catch (XhtmlException ex) {
            log.warn("Failed to extract macro usages on entity [{}] : {}", (Object)searchable.getId(), (Object)ex.getMessage());
            log.debug("Failed to extract macro usages on entity [{}] : {}", (Object)searchable.getId(), (Object)ex);
        }
    }
}

