/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.migration.agent.dto.assessment;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public enum FeatureDifferenceState {
    yes,
    no,
    unknown;

    private static final Map<String, FeatureDifferenceState> featureDiff;

    public static FeatureDifferenceState fromFeatureDifference(String rawString) {
        return featureDiff.getOrDefault(rawString, unknown);
    }

    static {
        featureDiff = ImmutableMap.of((Object)"HAS_DIFFERENCE", (Object)((Object)yes), (Object)"CONTACT_VENDOR", (Object)((Object)no), (Object)"UNKNOWN", (Object)((Object)unknown));
    }
}

