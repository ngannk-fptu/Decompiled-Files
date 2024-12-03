/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.core.persistence.VersionHistoryDao;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BuildNumberActivatedDarkFeatures {
    private static final Map<String, Integer> BUILD_NUMBER_FINALIZATION_FEATURES = ImmutableMap.builder().put((Object)"confluence.retention.rules", (Object)8803).build();
    private final VersionHistoryDao versionHistoryDao;
    private final Map<String, Integer> buildNumberActivatedFeatures;

    public BuildNumberActivatedDarkFeatures(VersionHistoryDao versionHistoryDao) {
        this(versionHistoryDao, BUILD_NUMBER_FINALIZATION_FEATURES);
    }

    @VisibleForTesting
    BuildNumberActivatedDarkFeatures(VersionHistoryDao versionHistoryDao, Map<String, Integer> buildNumberActivatedFeatures) {
        this.versionHistoryDao = versionHistoryDao;
        this.buildNumberActivatedFeatures = buildNumberActivatedFeatures;
    }

    public Set<String> getActivatedDarkFeatures() {
        int finalizedBuildNumber = this.versionHistoryDao.getFinalizedBuildNumber();
        return this.buildNumberActivatedFeatures.entrySet().stream().filter(entry -> finalizedBuildNumber >= (Integer)entry.getValue()).map(Map.Entry::getKey).collect(Collectors.toSet());
    }
}

