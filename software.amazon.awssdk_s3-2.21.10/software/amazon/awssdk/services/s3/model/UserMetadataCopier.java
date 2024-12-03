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
import software.amazon.awssdk.services.s3.model.MetadataEntry;

final class UserMetadataCopier {
    UserMetadataCopier() {
    }

    static List<MetadataEntry> copy(Collection<? extends MetadataEntry> userMetadataParam) {
        Object list;
        if (userMetadataParam == null || userMetadataParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            userMetadataParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<MetadataEntry> copyFromBuilder(Collection<? extends MetadataEntry.Builder> userMetadataParam) {
        Object list;
        if (userMetadataParam == null || userMetadataParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            userMetadataParam.forEach(entry -> {
                MetadataEntry member = entry == null ? null : (MetadataEntry)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<MetadataEntry.Builder> copyToBuilder(Collection<? extends MetadataEntry> userMetadataParam) {
        Object list;
        if (userMetadataParam == null || userMetadataParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            userMetadataParam.forEach(entry -> {
                MetadataEntry.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

