/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionHandler
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.masterdetail.services;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroMetricsEvent;
import com.atlassian.confluence.extra.masterdetail.services.DefaultPagePropertiesService;
import com.atlassian.confluence.extra.masterdetail.services.DetailsMacroBodyHandler;
import com.atlassian.confluence.extra.masterdetail.services.DetailsMacroBodyHandlerFastParse;
import com.atlassian.confluence.extra.masterdetail.services.DetailsMacroBodyHandlerLegacy;
import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PagePropertiesExtractor {
    private static final Logger log = LoggerFactory.getLogger(DefaultPagePropertiesService.class);
    private final XhtmlContent xhtmlContent;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final DarkFeatureManager darkFeatureManager;

    @Autowired
    public PagePropertiesExtractor(@ComponentImport XhtmlContent xhtmlContent, @ComponentImport XmlEventReaderFactory xmlEventReaderFactory, @ComponentImport DarkFeatureManager darkFeatureManager) {
        this.xhtmlContent = xhtmlContent;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.darkFeatureManager = darkFeatureManager;
    }

    public ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> extractProperties(ContentEntityObject entity, String contentBody, DetailsSummaryMacroMetricsEvent.Builder metrics) {
        DefaultConversionContext subContext = new DefaultConversionContext((RenderContext)entity.toPageContext());
        DetailsMacroBodyHandler macroBodyHandler = this.darkFeatureManager.isEnabledForAllUsers("masterdetail.legacy.parse").orElse(false).booleanValue() ? new DetailsMacroBodyHandlerLegacy(metrics) : new DetailsMacroBodyHandlerFastParse(metrics, arg_0 -> ((XmlEventReaderFactory)this.xmlEventReaderFactory).createXmlEventReader(arg_0));
        try {
            metrics.entityBodyFetchStart();
            this.xhtmlContent.handleMacroDefinitions(contentBody, (ConversionContext)subContext, (MacroDefinitionHandler)macroBodyHandler, MacroDefinitionMarshallingStrategy.MARSHALL_MACRO);
            metrics.entityBodyFetchFinish(StringUtils.defaultString((String)contentBody).length());
        }
        catch (XhtmlException e) {
            log.error("Cannot extract page properties from content with id: " + entity.getIdAsString(), (Throwable)e);
        }
        return macroBodyHandler.getDetails();
    }

    public ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> extractProperties(ContentEntityObject entity, DetailsSummaryMacroMetricsEvent.Builder metrics) {
        return this.extractProperties(entity, entity.getBodyAsString(), metrics);
    }
}

