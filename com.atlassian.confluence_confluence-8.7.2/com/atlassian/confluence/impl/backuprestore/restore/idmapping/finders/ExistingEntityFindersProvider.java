/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders;

import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.CompoundExistingEntityFinder;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.ExistingEntityFinder;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.PluginProvidedExistingEntityFinder;
import com.atlassian.confluence.plugin.descriptor.restore.PluginExistingEntityFinderModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ExistingEntityFindersProvider {
    private final Map<Class<?>, Set<CompoundExistingEntityFinder>> entityFinders;
    private final PluginAccessor pluginAccessor;

    public ExistingEntityFindersProvider(List<ExistingEntityFinder> defaultExistingEntityFinders, PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
        Map<Class<?>, List<ExistingEntityFinder>> defaultEntityFinderGroupByClass = this.buildEntityFindersMap(defaultExistingEntityFinders);
        this.entityFinders = this.enhanceEntityFindersMap(defaultEntityFinderGroupByClass);
    }

    public ExistingEntityFinder getExistingEntityFinder(Class<?> clazz, JobSource jobSource) {
        Set<CompoundExistingEntityFinder> finderGroupByJobSource = this.entityFinders.get(clazz);
        if (finderGroupByJobSource == null) {
            return null;
        }
        Optional<CompoundExistingEntityFinder> optionalFinder = finderGroupByJobSource.stream().filter(compoundExistingEntityFinder -> compoundExistingEntityFinder.isSupportedJobSource(jobSource)).findFirst();
        return optionalFinder.isEmpty() ? null : (ExistingEntityFinder)optionalFinder.get();
    }

    private Map<Class<?>, Set<CompoundExistingEntityFinder>> enhanceEntityFindersMap(Map<Class<?>, List<ExistingEntityFinder>> finderMap) {
        List existingEntityFinderModuleDescriptorList = this.pluginAccessor.getEnabledModuleDescriptorsByClass(PluginExistingEntityFinderModuleDescriptor.class);
        if (existingEntityFinderModuleDescriptorList == null || existingEntityFinderModuleDescriptorList.isEmpty()) {
            return ExistingEntityFindersProvider.groupByClassAndJobSource(finderMap);
        }
        HashMap allFinderMap = new HashMap(finderMap);
        Map<Class, List<ExistingEntityFinder>> pluginProvidedEntityFinders = existingEntityFinderModuleDescriptorList.stream().map(PluginProvidedExistingEntityFinder::new).collect(Collectors.groupingBy(ExistingEntityFinder::getSupportedClass));
        allFinderMap.putAll(pluginProvidedEntityFinders);
        return ExistingEntityFindersProvider.groupByClassAndJobSource(allFinderMap);
    }

    private static Map<Class<?>, Set<CompoundExistingEntityFinder>> groupByClassAndJobSource(Map<Class<?>, List<ExistingEntityFinder>> allFinderMap) {
        HashMap returnFinderMap = new HashMap();
        for (Map.Entry<Class<?>, List<ExistingEntityFinder>> entry : allFinderMap.entrySet()) {
            List<ExistingEntityFinder> finderByClass = entry.getValue();
            HashSet<CompoundExistingEntityFinder> groupByJobSource = new HashSet<CompoundExistingEntityFinder>();
            for (JobSource jobSource : JobSource.values()) {
                List<ExistingEntityFinder> finderByClassAndJobSource = finderByClass.stream().filter(finder -> finder.isSupportedJobSource(jobSource)).collect(Collectors.toList());
                if (finderByClassAndJobSource.isEmpty()) continue;
                groupByJobSource.add(new CompoundExistingEntityFinder(finderByClassAndJobSource));
            }
            returnFinderMap.putIfAbsent(entry.getKey(), groupByJobSource);
        }
        return returnFinderMap;
    }

    private Map<Class<?>, List<ExistingEntityFinder>> buildEntityFindersMap(List<ExistingEntityFinder> finders) {
        return finders.stream().collect(Collectors.groupingBy(ExistingEntityFinder::getSupportedClass));
    }
}

