/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders;

import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.ExistingEntityFinder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompoundExistingEntityFinder
implements ExistingEntityFinder {
    private final Class<?> supportedClass;
    private final List<ExistingEntityFinder> finderList;

    public CompoundExistingEntityFinder(List<ExistingEntityFinder> finderList) {
        this.finderList = Objects.requireNonNull(finderList);
        Optional firstExistingEntityFinder = finderList.stream().findFirst();
        if (firstExistingEntityFinder.isEmpty()) {
            throw new IllegalArgumentException("Missing ExistingEntityFinder");
        }
        Class<?> firstSupportedClass = ((ExistingEntityFinder)firstExistingEntityFinder.get()).getSupportedClass();
        if (!finderList.stream().allMatch(finder -> firstSupportedClass.equals(finder.getSupportedClass()))) {
            throw new IllegalArgumentException("ALl inner ExistingEntityFinder must have same supported class");
        }
        this.supportedClass = firstSupportedClass;
    }

    public List<ExistingEntityFinder> getFinderList() {
        return this.finderList;
    }

    @Override
    public Map<ImportedObjectV2, Object> findExistingObjectIds(Collection<ImportedObjectV2> objects) {
        HashMap<ImportedObjectV2, Object> accumulatedMap = new HashMap<ImportedObjectV2, Object>();
        for (ExistingEntityFinder existingEntityFinder : this.finderList) {
            if (accumulatedMap.keySet().containsAll(objects)) {
                return accumulatedMap;
            }
            Collection queryObjects = accumulatedMap.size() == 0 ? objects : (Collection)objects.stream().filter(object -> !accumulatedMap.keySet().contains(object)).collect(Collectors.toList());
            Map<ImportedObjectV2, Object> resultFromAFinder = existingEntityFinder.findExistingObjectIds(queryObjects);
            if (resultFromAFinder == null || resultFromAFinder.size() == 0) continue;
            resultFromAFinder.entrySet().forEach(entry -> accumulatedMap.putIfAbsent((ImportedObjectV2)entry.getKey(), entry.getValue()));
        }
        return accumulatedMap;
    }

    @Override
    public Class<?> getSupportedClass() {
        return this.supportedClass;
    }

    @Override
    public boolean isSupportedJobSource(JobSource jobSource) {
        return this.finderList.stream().allMatch(finder -> finder.isSupportedJobSource(jobSource));
    }
}

