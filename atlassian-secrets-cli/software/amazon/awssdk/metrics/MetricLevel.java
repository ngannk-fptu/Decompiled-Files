/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.metrics;

import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public enum MetricLevel {
    TRACE,
    INFO,
    ERROR;


    public boolean includesLevel(MetricLevel level) {
        return this.compareTo(level) <= 0;
    }
}

