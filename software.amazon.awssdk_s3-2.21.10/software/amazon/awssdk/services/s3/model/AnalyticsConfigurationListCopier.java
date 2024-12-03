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
import software.amazon.awssdk.services.s3.model.AnalyticsConfiguration;

final class AnalyticsConfigurationListCopier {
    AnalyticsConfigurationListCopier() {
    }

    static List<AnalyticsConfiguration> copy(Collection<? extends AnalyticsConfiguration> analyticsConfigurationListParam) {
        Object list;
        if (analyticsConfigurationListParam == null || analyticsConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            analyticsConfigurationListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<AnalyticsConfiguration> copyFromBuilder(Collection<? extends AnalyticsConfiguration.Builder> analyticsConfigurationListParam) {
        Object list;
        if (analyticsConfigurationListParam == null || analyticsConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            analyticsConfigurationListParam.forEach(entry -> {
                AnalyticsConfiguration member = entry == null ? null : (AnalyticsConfiguration)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<AnalyticsConfiguration.Builder> copyToBuilder(Collection<? extends AnalyticsConfiguration> analyticsConfigurationListParam) {
        Object list;
        if (analyticsConfigurationListParam == null || analyticsConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            analyticsConfigurationListParam.forEach(entry -> {
                AnalyticsConfiguration.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

