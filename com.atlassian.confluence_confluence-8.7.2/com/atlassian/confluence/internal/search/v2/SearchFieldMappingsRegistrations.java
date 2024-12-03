/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Multimap
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.ReflectionUtils
 */
package com.atlassian.confluence.internal.search.v2;

import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;
import com.atlassian.confluence.search.v2.FieldMappings;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.search.v2.SearchIndexAccessor;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

public class SearchFieldMappingsRegistrations {
    private static final Logger LOG = LoggerFactory.getLogger(SearchFieldMappingsRegistrations.class);
    private static final Multimap<SearchIndex, FieldMapping> INDEX_SPECIFIC_MAPPINGS = ImmutableMultimap.builder().put((Object)SearchIndex.CONTENT, (Object)SearchFieldMappings.CONTENT_URL_PATH).put((Object)SearchIndex.CHANGE, (Object)SearchFieldMappings.CHANGE_URL_PATH).build();
    private static final Collection<FieldMapping> COMMON_MAPPINGS = SearchFieldMappingsRegistrations.getCommonMappingsReflectively();
    private final SearchIndexAccessor contentAccessor;
    private final SearchIndexAccessor changeAccessor;
    private final EventPublisher eventPublisher;

    public SearchFieldMappingsRegistrations(SearchIndexAccessor contentAccessor, SearchIndexAccessor changeAccessor, EventPublisher eventPublisher) {
        this.contentAccessor = contentAccessor;
        this.changeAccessor = changeAccessor;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onPluginStarted(PluginFrameworkStartedEvent e) {
        SearchFieldMappingsRegistrations.register(SearchIndex.CONTENT, this.contentAccessor.getFieldMappings());
        SearchFieldMappingsRegistrations.register(SearchIndex.CHANGE, this.changeAccessor.getFieldMappings());
    }

    public static void register(SearchIndex index, FieldMappings mappings) {
        SearchFieldMappingsRegistrations.register(mappings, COMMON_MAPPINGS);
        SearchFieldMappingsRegistrations.register(mappings, INDEX_SPECIFIC_MAPPINGS.get((Object)index));
    }

    private static void register(FieldMappings fieldMappings, Collection<FieldMapping> mappingsToRegister) {
        for (FieldMapping mapping : mappingsToRegister) {
            try {
                fieldMappings.addMapping(mapping);
            }
            catch (Throwable t) {
                LOG.error("Failed to register field mapping: {}", (Object)mapping.getName(), (Object)t);
            }
        }
    }

    private static List<FieldMapping> getCommonMappingsReflectively() {
        return Stream.of(SearchFieldMappings.class.getDeclaredFields()).filter(f -> FieldMapping.class.isAssignableFrom(f.getType())).map(f -> (FieldMapping)ReflectionUtils.getField((Field)f, null)).filter(Predicate.not(arg_0 -> INDEX_SPECIFIC_MAPPINGS.containsValue(arg_0))).collect(Collectors.toList());
    }
}

