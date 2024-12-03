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
import software.amazon.awssdk.services.s3.model.CommonPrefix;

final class CommonPrefixListCopier {
    CommonPrefixListCopier() {
    }

    static List<CommonPrefix> copy(Collection<? extends CommonPrefix> commonPrefixListParam) {
        Object list;
        if (commonPrefixListParam == null || commonPrefixListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            commonPrefixListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<CommonPrefix> copyFromBuilder(Collection<? extends CommonPrefix.Builder> commonPrefixListParam) {
        Object list;
        if (commonPrefixListParam == null || commonPrefixListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            commonPrefixListParam.forEach(entry -> {
                CommonPrefix member = entry == null ? null : (CommonPrefix)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<CommonPrefix.Builder> copyToBuilder(Collection<? extends CommonPrefix> commonPrefixListParam) {
        Object list;
        if (commonPrefixListParam == null || commonPrefixListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            commonPrefixListParam.forEach(entry -> {
                CommonPrefix.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

