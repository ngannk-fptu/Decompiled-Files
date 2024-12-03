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
import software.amazon.awssdk.services.s3.model.IntelligentTieringConfiguration;

final class IntelligentTieringConfigurationListCopier {
    IntelligentTieringConfigurationListCopier() {
    }

    static List<IntelligentTieringConfiguration> copy(Collection<? extends IntelligentTieringConfiguration> intelligentTieringConfigurationListParam) {
        Object list;
        if (intelligentTieringConfigurationListParam == null || intelligentTieringConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            intelligentTieringConfigurationListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<IntelligentTieringConfiguration> copyFromBuilder(Collection<? extends IntelligentTieringConfiguration.Builder> intelligentTieringConfigurationListParam) {
        Object list;
        if (intelligentTieringConfigurationListParam == null || intelligentTieringConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            intelligentTieringConfigurationListParam.forEach(entry -> {
                IntelligentTieringConfiguration member = entry == null ? null : (IntelligentTieringConfiguration)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<IntelligentTieringConfiguration.Builder> copyToBuilder(Collection<? extends IntelligentTieringConfiguration> intelligentTieringConfigurationListParam) {
        Object list;
        if (intelligentTieringConfigurationListParam == null || intelligentTieringConfigurationListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            intelligentTieringConfigurationListParam.forEach(entry -> {
                IntelligentTieringConfiguration.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

