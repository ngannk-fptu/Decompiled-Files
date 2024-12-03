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
import software.amazon.awssdk.services.s3.model.QueueConfiguration;

final class QueueConfigurationListCopier {
    QueueConfigurationListCopier() {
    }

    static List<QueueConfiguration> copy(Collection<? extends QueueConfiguration> queueConfigurationListParam) {
        Object list;
        if (queueConfigurationListParam == null || queueConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            queueConfigurationListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<QueueConfiguration> copyFromBuilder(Collection<? extends QueueConfiguration.Builder> queueConfigurationListParam) {
        Object list;
        if (queueConfigurationListParam == null || queueConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            queueConfigurationListParam.forEach(entry -> {
                QueueConfiguration member = entry == null ? null : (QueueConfiguration)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<QueueConfiguration.Builder> copyToBuilder(Collection<? extends QueueConfiguration> queueConfigurationListParam) {
        Object list;
        if (queueConfigurationListParam == null || queueConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            queueConfigurationListParam.forEach(entry -> {
                QueueConfiguration.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

