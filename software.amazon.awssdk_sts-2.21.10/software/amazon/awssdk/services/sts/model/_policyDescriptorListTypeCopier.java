/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 */
package software.amazon.awssdk.services.sts.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.sts.model.PolicyDescriptorType;

final class _policyDescriptorListTypeCopier {
    _policyDescriptorListTypeCopier() {
    }

    static List<PolicyDescriptorType> copy(Collection<? extends PolicyDescriptorType> policyDescriptorListTypeParam) {
        Object list;
        if (policyDescriptorListTypeParam == null || policyDescriptorListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            policyDescriptorListTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<PolicyDescriptorType> copyFromBuilder(Collection<? extends PolicyDescriptorType.Builder> policyDescriptorListTypeParam) {
        Object list;
        if (policyDescriptorListTypeParam == null || policyDescriptorListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            policyDescriptorListTypeParam.forEach(entry -> {
                PolicyDescriptorType member = entry == null ? null : (PolicyDescriptorType)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<PolicyDescriptorType.Builder> copyToBuilder(Collection<? extends PolicyDescriptorType> policyDescriptorListTypeParam) {
        Object list;
        if (policyDescriptorListTypeParam == null || policyDescriptorListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            policyDescriptorListTypeParam.forEach(entry -> {
                PolicyDescriptorType.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

