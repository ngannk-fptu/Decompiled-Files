/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.dto;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Generated;

public enum ConcurrencySettingsEnum {
    SPACE_USERS_CONCURRENCY_MAX(2),
    SPACE_USERS_CONCURRENCY_NODE_MAX(2),
    ATTACHMENT_CONCURRENCY_CLUSTER_MAX(1),
    ATTACHMENT_CONCURRENCY_NODE_MAX(1),
    EXPORT_CONCURRENCY_CLUSTER_MAX(1),
    EXPORT_CONCURRENCY_NODE_MAX(1),
    IMPORT_CONCURRENCY_CLUSTER_MAX(4),
    IMPORT_CONCURRENCY_NODE_MAX(4),
    UPLOAD_CONCURRENCY_CLUSTER_MAX(2),
    UPLOAD_CONCURRENCY_NODE_MAX(2),
    ATTACHMENT_UPLOAD_CONCURRENCY(5);

    private final int defaultConcurrency;

    private ConcurrencySettingsEnum(int defaultConcurrency) {
        this.defaultConcurrency = defaultConcurrency;
    }

    public static Map<ConcurrencySettingsEnum, Integer> getDefaultMap() {
        return Arrays.stream(ConcurrencySettingsEnum.values()).collect(Collectors.toMap(concurrencySettingsEnum -> concurrencySettingsEnum, ConcurrencySettingsEnum::getDefaultConcurrency));
    }

    @Generated
    public int getDefaultConcurrency() {
        return this.defaultConcurrency;
    }
}

