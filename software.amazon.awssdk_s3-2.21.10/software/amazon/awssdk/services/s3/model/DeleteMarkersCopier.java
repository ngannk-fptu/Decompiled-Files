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
import software.amazon.awssdk.services.s3.model.DeleteMarkerEntry;

final class DeleteMarkersCopier {
    DeleteMarkersCopier() {
    }

    static List<DeleteMarkerEntry> copy(Collection<? extends DeleteMarkerEntry> deleteMarkersParam) {
        Object list;
        if (deleteMarkersParam == null || deleteMarkersParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            deleteMarkersParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<DeleteMarkerEntry> copyFromBuilder(Collection<? extends DeleteMarkerEntry.Builder> deleteMarkersParam) {
        Object list;
        if (deleteMarkersParam == null || deleteMarkersParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            deleteMarkersParam.forEach(entry -> {
                DeleteMarkerEntry member = entry == null ? null : (DeleteMarkerEntry)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<DeleteMarkerEntry.Builder> copyToBuilder(Collection<? extends DeleteMarkerEntry> deleteMarkersParam) {
        Object list;
        if (deleteMarkersParam == null || deleteMarkersParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            deleteMarkersParam.forEach(entry -> {
                DeleteMarkerEntry.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

