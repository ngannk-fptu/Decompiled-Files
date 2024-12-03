/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 */
package software.amazon.awssdk.services.s3.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.MetricsConfiguration;

final class MetricsConfigurationListCopier {
    MetricsConfigurationListCopier() {
    }

    static List<MetricsConfiguration> copy(Collection<? extends MetricsConfiguration> metricsConfigurationListParam) {
        Object list;
        if (metricsConfigurationListParam == null || metricsConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            metricsConfigurationListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<MetricsConfiguration> copyFromBuilder(Collection<? extends MetricsConfiguration.Builder> metricsConfigurationListParam) {
        Object list;
        if (metricsConfigurationListParam == null || metricsConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            metricsConfigurationListParam.forEach(entry -> {
                MetricsConfiguration member = entry == null ? null : (MetricsConfiguration)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<MetricsConfiguration.Builder> copyToBuilder(Collection<? extends MetricsConfiguration> metricsConfigurationListParam) {
        Object list;
        if (metricsConfigurationListParam == null || metricsConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            metricsConfigurationListParam.forEach(entry -> {
                MetricsConfiguration.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

