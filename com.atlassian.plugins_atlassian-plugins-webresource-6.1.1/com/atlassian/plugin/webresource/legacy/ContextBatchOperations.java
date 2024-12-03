/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  org.apache.commons.collections.CollectionUtils
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.legacy.ContextBatch;
import com.atlassian.plugin.webresource.legacy.ModuleDescriptorStub;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import org.apache.commons.collections.CollectionUtils;

public class ContextBatchOperations {
    public ContextBatch merge(Collection<ContextBatch> batchesToMerge) {
        if (CollectionUtils.isEmpty(batchesToMerge)) {
            return null;
        }
        if (batchesToMerge.size() == 1) {
            return batchesToMerge.iterator().next();
        }
        LinkedHashSet<String> includedContexts = new LinkedHashSet<String>();
        HashSet<ModuleDescriptorStub> resources = new HashSet<ModuleDescriptorStub>();
        boolean removeSuperResources = false;
        for (ContextBatch batch : batchesToMerge) {
            if (!Iterables.isEmpty(batch.getExcludedContexts())) {
                throw new IllegalArgumentException("The ContextBatch " + batch.getKey() + " has excludedContexts.");
            }
            removeSuperResources |= batch.isRemoveSuperResources();
            includedContexts.addAll(batch.getContexts());
            Iterables.addAll(resources, batch.getResources());
        }
        return new ContextBatch(new ArrayList<String>(includedContexts), null, resources, removeSuperResources);
    }

    public ContextBatch subtract(ContextBatch operand, Collection<ContextBatch> batchesToSubtract) {
        if (CollectionUtils.isEmpty(batchesToSubtract)) {
            return operand;
        }
        LinkedHashSet<String> excludedContexts = new LinkedHashSet<String>();
        Iterables.addAll(excludedContexts, operand.getExcludedContexts());
        LinkedHashSet<ModuleDescriptorStub> resources = new LinkedHashSet<ModuleDescriptorStub>();
        Iterables.addAll(resources, operand.getResources());
        boolean removeSuperResources = false;
        for (ContextBatch subtract : batchesToSubtract) {
            if (!Iterables.isEmpty(subtract.getExcludedContexts())) {
                throw new IllegalArgumentException("The ContextBatch " + subtract.getKey() + " has excludedContexts.");
            }
            removeSuperResources |= subtract.isRemoveSuperResources();
            Iterables.addAll(excludedContexts, subtract.getContexts());
            Iterable<ModuleDescriptorStub> subtractResources = subtract.getResources();
            for (ModuleDescriptorStub resource : subtractResources) {
                resources.remove((Object)resource);
            }
        }
        return new ContextBatch(operand.getContexts(), excludedContexts, resources, removeSuperResources);
    }
}

