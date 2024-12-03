/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Supplier
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  io.atlassian.fugue.Pair
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.masterdetail.services;

import com.atlassian.cache.Supplier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.masterdetail.CachingDetailsManager;
import com.atlassian.confluence.extra.masterdetail.ExtractedDetails;
import com.atlassian.confluence.extra.masterdetail.MasterDetailConfigurator;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroMetricsEvent;
import com.atlassian.confluence.extra.masterdetail.persistence.entities.BodyContentQuerier;
import com.atlassian.confluence.extra.masterdetail.services.InternalPagePropertiesService;
import com.atlassian.confluence.extra.masterdetail.services.PagePropertiesExtractor;
import com.atlassian.confluence.plugins.pageproperties.api.model.PagePropertiesMacroInstance;
import com.atlassian.confluence.plugins.pageproperties.api.model.PagePropertiesMacroReport;
import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import com.atlassian.confluence.plugins.pageproperties.api.service.PagePropertiesService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.atlassian.fugue.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PagePropertiesService.class})
public class DefaultPagePropertiesService
implements InternalPagePropertiesService {
    private static Logger logger = LoggerFactory.getLogger(DefaultPagePropertiesService.class);
    private final CachingDetailsManager cachingDetailsManager;
    private final PagePropertiesExtractor pagePropertiesExtractor;
    private final BodyContentQuerier bodyContentQuerier;
    private final MasterDetailConfigurator configurator;

    @Autowired
    public DefaultPagePropertiesService(CachingDetailsManager cachingDetailsManager, PagePropertiesExtractor pagePropertiesExtractor, BodyContentQuerier bodyContentQuerier, MasterDetailConfigurator configurator) {
        this.cachingDetailsManager = cachingDetailsManager;
        this.pagePropertiesExtractor = pagePropertiesExtractor;
        this.bodyContentQuerier = bodyContentQuerier;
        this.configurator = configurator;
    }

    @Override
    public PagePropertiesMacroReport getReportFromContent(ContentEntityObject contentEntity) {
        DetailsSummaryMacroMetricsEvent.Builder metrics = DetailsSummaryMacroMetricsEvent.builder(DetailsSummaryMacroMetricsEvent.Type.SERVICE_EXECUTION).maxResultConfig(this.configurator.getPagePropertiesReportContentRetrieverMaxResult());
        ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> detailsMap = this.extractPropertiesFromContent(contentEntity, metrics);
        HashMap<String, List<PagePropertiesMacroInstance>> macroInstancesMap = new HashMap<String, List<PagePropertiesMacroInstance>>();
        detailsMap.forEach((detailsId, pageProperties) -> macroInstancesMap.put((String)detailsId, (List<PagePropertiesMacroInstance>)ImmutableList.copyOf((Collection)pageProperties.stream().map(PagePropertiesMacroInstance::new).collect(Collectors.toList()))));
        return new PagePropertiesMacroReport(macroInstancesMap);
    }

    @Override
    public List<ExtractedDetails> getDetailsFromContent(Collection<ContentEntityObject> contents, String detailsId, DetailsSummaryMacroMetricsEvent.Builder metrics) {
        ArrayList detailsList = Lists.newArrayListWithCapacity((int)contents.size());
        Iterators.partition(contents.iterator(), (int)this.configurator.getPagePropertiesReportBodyContentRetrieverBatchSize()).forEachRemaining(batchCEO -> {
            Map<Long, String> contentBodies = this.bodyContentQuerier.retrieveBodyContentForIds(batchCEO.stream().map(content -> content.getId()).collect(Collectors.toList()));
            ArrayList contentToBody = new ArrayList(batchCEO.size());
            batchCEO.stream().forEach(content -> {
                long contentId = content.getId();
                String body = (String)contentBodies.get(content.getId());
                if (body == null) {
                    logger.error("Could not get body for content {}", (Object)contentId);
                    body = "";
                }
                contentToBody.add(new Pair(content, (Object)body));
            });
            contentToBody.forEach(entity -> detailsList.addAll(this.aggregatePageProperties(detailsId, (Map<String, ? extends List<ImmutableMap<String, PageProperty>>>)this.extractPropertiesFromContent((ContentEntityObject)entity.left(), (String)entity.right(), metrics)).stream().map(pageProperties -> new ExtractedDetails((ContentEntityObject)entity.left(), (Map<String, PageProperty>)pageProperties)).collect(Collectors.toList())));
        });
        return detailsList;
    }

    private List<Map<String, PageProperty>> aggregatePageProperties(String detailsId, Map<String, ? extends List<ImmutableMap<String, PageProperty>>> detailsByIdMap) {
        ArrayList result = Lists.newArrayList();
        if (StringUtils.isBlank((CharSequence)detailsId)) {
            detailsByIdMap.values().forEach(value -> this.combineResults((List<ImmutableMap<String, PageProperty>>)value, result));
        } else {
            this.combineResults(detailsByIdMap.get(detailsId), result);
        }
        return result;
    }

    private void combineResults(List<ImmutableMap<String, PageProperty>> incoming, List<Map<String, PageProperty>> detailMapList) {
        if (incoming == null) {
            return;
        }
        incoming.forEach(detailMap -> {
            if (detailMapList.isEmpty() || this.hasDupeKeys(detailMapList, (Map<String, PageProperty>)detailMap)) {
                detailMapList.add(Maps.newHashMap((Map)detailMap));
            } else {
                ((Map)detailMapList.get(detailMapList.size() - 1)).putAll(detailMap);
            }
        });
    }

    private boolean hasDupeKeys(List<Map<String, PageProperty>> detailMapList, Map<String, PageProperty> details) {
        Set<String> incomingKeys = details.keySet();
        return detailMapList.stream().anyMatch(existingMap -> CollectionUtils.containsAny(existingMap.keySet(), (Collection)incomingKeys));
    }

    ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> extractPropertiesFromContent(ContentEntityObject contentEntity, String contentBody, DetailsSummaryMacroMetricsEvent.Builder metrics) {
        return this.cachingDetailsManager.get(contentEntity.getId(), (Supplier<ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>>>)((Supplier)() -> this.pagePropertiesExtractor.extractProperties(contentEntity, contentBody, metrics)));
    }

    ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> extractPropertiesFromContent(ContentEntityObject contentEntity, DetailsSummaryMacroMetricsEvent.Builder metrics) {
        return this.cachingDetailsManager.get(contentEntity.getId(), (Supplier<ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>>>)((Supplier)() -> this.pagePropertiesExtractor.extractProperties(contentEntity, metrics)));
    }
}

