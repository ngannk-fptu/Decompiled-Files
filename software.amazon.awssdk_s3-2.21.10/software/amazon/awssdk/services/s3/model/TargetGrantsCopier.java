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
import software.amazon.awssdk.services.s3.model.TargetGrant;

final class TargetGrantsCopier {
    TargetGrantsCopier() {
    }

    static List<TargetGrant> copy(Collection<? extends TargetGrant> targetGrantsParam) {
        Object list;
        if (targetGrantsParam == null || targetGrantsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            targetGrantsParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<TargetGrant> copyFromBuilder(Collection<? extends TargetGrant.Builder> targetGrantsParam) {
        Object list;
        if (targetGrantsParam == null || targetGrantsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            targetGrantsParam.forEach(entry -> {
                TargetGrant member = entry == null ? null : (TargetGrant)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<TargetGrant.Builder> copyToBuilder(Collection<? extends TargetGrant> targetGrantsParam) {
        Object list;
        if (targetGrantsParam == null || targetGrantsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            targetGrantsParam.forEach(entry -> {
                TargetGrant.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

