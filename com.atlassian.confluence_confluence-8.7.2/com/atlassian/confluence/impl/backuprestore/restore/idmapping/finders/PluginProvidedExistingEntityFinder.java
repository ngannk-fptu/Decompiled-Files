/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders;

import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.ExistingEntityFinder;
import com.atlassian.confluence.plugin.descriptor.restore.ImportedObjectModel;
import com.atlassian.confluence.plugin.descriptor.restore.PluginExistingEntityFinder;
import com.atlassian.confluence.plugin.descriptor.restore.PluginExistingEntityFinderModuleDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginProvidedExistingEntityFinder
implements ExistingEntityFinder {
    private static final Logger logger = LoggerFactory.getLogger(PluginProvidedExistingEntityFinder.class);
    private final PluginExistingEntityFinder pluginExistingEntityFinder;

    public PluginProvidedExistingEntityFinder(PluginExistingEntityFinderModuleDescriptor pluginExistingEntityFinderModuleDescriptor) {
        Objects.requireNonNull(pluginExistingEntityFinderModuleDescriptor);
        this.pluginExistingEntityFinder = pluginExistingEntityFinderModuleDescriptor.getModule();
    }

    @Override
    public Map<ImportedObjectV2, Object> findExistingObjectIds(Collection<ImportedObjectV2> objects) {
        logger.debug("Finding existing object id by class {}", this.getSupportedClass());
        Objects.requireNonNull(objects);
        List<ImportedObjectModel> listPluginModels = objects.stream().map(o -> new ImportedObjectModel(o.getId(), o.getEntityClass(), o.getPropertyValueMap())).collect(Collectors.toList());
        Map<Object, ImportedObjectV2> xmlIdCoreModelMap = objects.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        if (listPluginModels.isEmpty()) {
            logger.debug("There is no imported object provided");
            return Collections.emptyMap();
        }
        try {
            Map<ImportedObjectModel, Object> existingObjectMap = this.pluginExistingEntityFinder.findExistingObjectIds(listPluginModels);
            if (existingObjectMap == null) {
                return Collections.emptyMap();
            }
            Map<ImportedObjectV2, Object> existingObjectReturnMap = existingObjectMap.entrySet().stream().collect(Collectors.toMap(entry -> (ImportedObjectV2)xmlIdCoreModelMap.get(((ImportedObjectModel)entry.getKey()).getId()), entry -> entry.getValue()));
            return existingObjectReturnMap;
        }
        catch (Exception ex) {
            logger.error(String.format("Could not find existing object ids for class %s", this.getSupportedClass()), (Throwable)ex);
            return Collections.emptyMap();
        }
    }

    @Override
    public Class<?> getSupportedClass() {
        return this.pluginExistingEntityFinder.getSupportedClass();
    }

    @Override
    public boolean isSupportedJobSource(JobSource jobSource) {
        return JobSource.SERVER.equals((Object)jobSource);
    }
}

