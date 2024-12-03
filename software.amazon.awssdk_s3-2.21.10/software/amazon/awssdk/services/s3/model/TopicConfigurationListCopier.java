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
import software.amazon.awssdk.services.s3.model.TopicConfiguration;

final class TopicConfigurationListCopier {
    TopicConfigurationListCopier() {
    }

    static List<TopicConfiguration> copy(Collection<? extends TopicConfiguration> topicConfigurationListParam) {
        Object list;
        if (topicConfigurationListParam == null || topicConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            topicConfigurationListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<TopicConfiguration> copyFromBuilder(Collection<? extends TopicConfiguration.Builder> topicConfigurationListParam) {
        Object list;
        if (topicConfigurationListParam == null || topicConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            topicConfigurationListParam.forEach(entry -> {
                TopicConfiguration member = entry == null ? null : (TopicConfiguration)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<TopicConfiguration.Builder> copyToBuilder(Collection<? extends TopicConfiguration> topicConfigurationListParam) {
        Object list;
        if (topicConfigurationListParam == null || topicConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            topicConfigurationListParam.forEach(entry -> {
                TopicConfiguration.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

