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
import software.amazon.awssdk.services.s3.model.CompletedPart;

final class CompletedPartListCopier {
    CompletedPartListCopier() {
    }

    static List<CompletedPart> copy(Collection<? extends CompletedPart> completedPartListParam) {
        Object list;
        if (completedPartListParam == null || completedPartListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            completedPartListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<CompletedPart> copyFromBuilder(Collection<? extends CompletedPart.Builder> completedPartListParam) {
        Object list;
        if (completedPartListParam == null || completedPartListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            completedPartListParam.forEach(entry -> {
                CompletedPart member = entry == null ? null : (CompletedPart)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<CompletedPart.Builder> copyToBuilder(Collection<? extends CompletedPart> completedPartListParam) {
        Object list;
        if (completedPartListParam == null || completedPartListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            completedPartListParam.forEach(entry -> {
                CompletedPart.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

