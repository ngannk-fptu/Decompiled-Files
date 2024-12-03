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
import software.amazon.awssdk.services.s3.model.NoncurrentVersionTransition;

final class NoncurrentVersionTransitionListCopier {
    NoncurrentVersionTransitionListCopier() {
    }

    static List<NoncurrentVersionTransition> copy(Collection<? extends NoncurrentVersionTransition> noncurrentVersionTransitionListParam) {
        Object list;
        if (noncurrentVersionTransitionListParam == null || noncurrentVersionTransitionListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            noncurrentVersionTransitionListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<NoncurrentVersionTransition> copyFromBuilder(Collection<? extends NoncurrentVersionTransition.Builder> noncurrentVersionTransitionListParam) {
        Object list;
        if (noncurrentVersionTransitionListParam == null || noncurrentVersionTransitionListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            noncurrentVersionTransitionListParam.forEach(entry -> {
                NoncurrentVersionTransition member = entry == null ? null : (NoncurrentVersionTransition)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<NoncurrentVersionTransition.Builder> copyToBuilder(Collection<? extends NoncurrentVersionTransition> noncurrentVersionTransitionListParam) {
        Object list;
        if (noncurrentVersionTransitionListParam == null || noncurrentVersionTransitionListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            noncurrentVersionTransitionListParam.forEach(entry -> {
                NoncurrentVersionTransition.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

