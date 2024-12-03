/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Edition
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.cmpt.domain.Edition;
import java.util.Arrays;

public enum CloudEdition {
    FREE(Edition.FREE),
    STANDARD(Edition.STANDARD),
    PREMIUM(Edition.PREMIUM);

    private final Edition key;

    private CloudEdition(Edition key) {
        this.key = key;
    }

    public Edition getKey() {
        return this.key;
    }

    public static CloudEdition from(Edition value) {
        return Arrays.stream(CloudEdition.values()).filter(it -> it.getKey() == value).findAny().orElseThrow(() -> new IllegalArgumentException(String.format("Cloud Edition [%s] not found", value)));
    }
}

