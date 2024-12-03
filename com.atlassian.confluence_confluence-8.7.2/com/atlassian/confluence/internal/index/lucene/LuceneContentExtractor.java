/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.HandleResolver
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.internal.search.v2.lucene.HibernateUnwrapper
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.beanutils.PropertyUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.HandleResolver;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.internal.search.v2.lucene.HibernateUnwrapper;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class LuceneContentExtractor
implements Extractor2 {
    private static final Logger log = LoggerFactory.getLogger(LuceneContentExtractor.class);
    private final HandleResolver handleResolver;

    public LuceneContentExtractor(HandleResolver handleResolver) {
        this.handleResolver = Objects.requireNonNull(handleResolver);
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object object) {
        if (!(object instanceof Searchable)) {
            return Collections.emptyList();
        }
        Searchable searchable = (Searchable)object;
        ImmutableList.Builder fieldDescriptors = ImmutableList.builder();
        try (Ticker ignored = Timers.start((String)"DefaultContentExtractor.extractFields");){
            fieldDescriptors.add((Object)this.createHandleField(searchable));
            fieldDescriptors.add((Object)this.createContentIdField(searchable));
            fieldDescriptors.add((Object)this.createClassNameField(searchable));
            this.createTypeField(searchable).ifPresent(arg_0 -> ((ImmutableList.Builder)fieldDescriptors).add(arg_0));
            this.createUrlPathField(searchable).ifPresent(arg_0 -> ((ImmutableList.Builder)fieldDescriptors).add(arg_0));
            ImmutableList immutableList = fieldDescriptors.build();
            return immutableList;
        }
    }

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    private Optional<FieldDescriptor> createUrlPathField(Searchable searchable) {
        return this.getStringProperty(searchable, "urlPath").map(SearchFieldMappings.CONTENT_URL_PATH::createField);
    }

    private Optional<FieldDescriptor> createTypeField(Searchable searchable) {
        return this.getStringProperty(searchable, "type").map(SearchFieldMappings.TYPE::createField);
    }

    private FieldDescriptor createClassNameField(Searchable searchable) {
        return SearchFieldMappings.CLASS_NAME.createField(HibernateUnwrapper.getUnderlyingClass((Object)searchable).getName());
    }

    private FieldDescriptor createHandleField(Searchable searchable) {
        return SearchFieldMappings.HANDLE.createField(this.getHandle(searchable).toString());
    }

    private FieldDescriptor createContentIdField(Searchable searchable) {
        return SearchFieldMappings.CONTENT_ID.createField(searchable.getId());
    }

    private Handle getHandle(Searchable obj) {
        return this.handleResolver.getHandle((Object)obj);
    }

    private Optional<String> getStringProperty(Searchable object, String field) {
        try {
            Object value = PropertyUtils.getProperty((Object)object, (String)field);
            return value != null ? Optional.of(value.toString()) : Optional.empty();
        }
        catch (NoSuchMethodException e) {
            log.debug("Unable to find field '{}' on {}", new Object[]{field, object, e});
        }
        catch (IllegalAccessException e) {
            log.debug("Unable to access field '{}' on {}", new Object[]{field, object, e});
        }
        catch (InvocationTargetException e) {
            log.debug("Problem accessing field '{}' on {}", new Object[]{field, object, e});
        }
        return Optional.empty();
    }
}

