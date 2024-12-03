/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.MappingDeconflictDarkFeature;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.SearchIndexAccessException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldMappings {
    private static final Logger LOG = LoggerFactory.getLogger(FieldMappings.class);
    private final FieldMappingWriter writer;
    private final MappingDeconflictDarkFeature deconflictDarkFeature;
    private final ConcurrentMap<String, FieldMapping> fields = new ConcurrentHashMap<String, FieldMapping>();

    public FieldMappings(FieldMappingWriter writer, MappingDeconflictDarkFeature deconflictDarkFeature) {
        this.writer = Objects.requireNonNull(writer, "writer is required");
        this.deconflictDarkFeature = Objects.requireNonNull(deconflictDarkFeature, "deconflictDarkFeature");
    }

    public Collection<FieldMapping> getFields() {
        return this.fields.values();
    }

    public boolean addMapping(FieldMapping mapping) {
        AtomicBoolean newlyAdded = new AtomicBoolean(false);
        FieldMapping registeredMapping = this.fields.computeIfAbsent(mapping.getName(), s -> {
            this.writer.putIfAbsent(mapping);
            newlyAdded.set(true);
            return mapping;
        });
        if (!newlyAdded.get() && this.deconflictDarkFeature.isEnabled() && !Objects.equals(registeredMapping, mapping)) {
            LOG.error("Mapping for '{}' ({}) conflicts with existing mapping ({}).", new Object[]{mapping.getName(), mapping, registeredMapping});
        }
        return newlyAdded.get();
    }

    public void addDocumentFields(AtlassianDocument document) {
        for (FieldDescriptor f : document.getFields()) {
            FieldMapping mapping = f.getMapping();
            if (!this.addMapping(mapping)) continue;
            LOG.info("Mapping {} was registered implicitly", (Object)mapping.getName());
        }
    }

    public static interface FieldMappingWriter {
        public boolean putIfAbsent(FieldMapping var1) throws SearchIndexAccessException;
    }
}

