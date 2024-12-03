/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.metrics;

import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public enum MetricCategory {
    CORE("Core"),
    HTTP_CLIENT("HttpClient"),
    CUSTOM("Custom"),
    ALL("All");

    private final String value;

    private MetricCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static MetricCategory fromString(String value) {
        for (MetricCategory mc : MetricCategory.values()) {
            if (!mc.value.equalsIgnoreCase(value)) continue;
            return mc;
        }
        throw new IllegalArgumentException("MetricCategory cannot be created from value: " + value);
    }
}

