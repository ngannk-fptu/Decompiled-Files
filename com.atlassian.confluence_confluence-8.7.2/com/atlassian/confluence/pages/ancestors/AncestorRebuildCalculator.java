/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.ancestors;

import com.atlassian.confluence.pages.ancestors.AncestorRebuildMetrics;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AncestorRebuildCalculator {
    private static final Logger log = LoggerFactory.getLogger(AncestorRebuildCalculator.class);
    private static final List<Long> NO_ANCESTORS = ImmutableList.of();
    private final List<Object[]> childParentPairs;
    private final Map<Long, Long> parentMap = Maps.newHashMap();
    private final Map<Long, List<Long>> ancestorMap = Maps.newHashMap();
    private final AncestorRebuildMetrics metrics;

    AncestorRebuildCalculator(List<Object[]> childParentPairs, AncestorRebuildMetrics metrics) {
        this.childParentPairs = childParentPairs;
        this.metrics = metrics;
    }

    Map<Long, List<Long>> calculate() {
        if (!this.ancestorMap.isEmpty()) {
            throw new IllegalStateException("This calculation has already been performed.");
        }
        this.metrics.totalChildren = this.childParentPairs.size();
        this.buildParentMap();
        return this.buildAncestorMap();
    }

    private void buildParentMap() {
        this.metrics.startStopwatch(AncestorRebuildMetrics.StopwatchKey.CALCULATE_PARENT_MAP);
        for (Object[] pair : this.childParentPairs) {
            this.parentMap.put((Long)pair[0], (Long)pair[1]);
        }
        this.metrics.stopStopwatch(AncestorRebuildMetrics.StopwatchKey.CALCULATE_PARENT_MAP);
    }

    private Map<Long, List<Long>> buildAncestorMap() {
        this.metrics.startStopwatch(AncestorRebuildMetrics.StopwatchKey.CALCULATE_ANCESTOR_MAP);
        for (Long id : this.parentMap.keySet()) {
            try {
                this.storeAncestors(id, Sets.newHashSet());
            }
            catch (CyclicAncestorException e) {
                log.warn("Ancestor cycle detected; breaking");
                for (Long cyclicId : e.cyclicIds) {
                    log.warn("Cyclic id: {}", (Object)cyclicId);
                    this.ancestorMap.put(cyclicId, NO_ANCESTORS);
                }
            }
        }
        this.metrics.stopStopwatch(AncestorRebuildMetrics.StopwatchKey.CALCULATE_ANCESTOR_MAP);
        return this.ancestorMap;
    }

    private List<Long> storeAncestors(Long id, Set<Long> visited) throws CyclicAncestorException {
        if (this.ancestorMap.containsKey(id)) {
            return this.ancestorMap.get(id);
        }
        if (!this.parentMap.containsKey(id)) {
            this.ancestorMap.put(id, NO_ANCESTORS);
            return NO_ANCESTORS;
        }
        visited.add(id);
        long parentId = this.parentMap.get(id);
        if (visited.contains(parentId)) {
            throw new CyclicAncestorException(visited);
        }
        List<Long> parentAncestors = this.storeAncestors(parentId, visited);
        ArrayList ancestorList = Lists.newArrayList(parentAncestors);
        ancestorList.add(parentId);
        this.ancestorMap.put(id, ancestorList);
        return ancestorList;
    }

    private static class CyclicAncestorException
    extends Throwable {
        private final Set<Long> cyclicIds;

        private CyclicAncestorException(Set<Long> cyclicIds) {
            this.cyclicIds = cyclicIds;
        }
    }
}

