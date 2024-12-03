/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.ReplicaRegionType;

final class AddReplicaRegionListTypeCopier {
    AddReplicaRegionListTypeCopier() {
    }

    static List<ReplicaRegionType> copy(Collection<? extends ReplicaRegionType> addReplicaRegionListTypeParam) {
        Object list;
        if (addReplicaRegionListTypeParam == null || addReplicaRegionListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            addReplicaRegionListTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ReplicaRegionType> copyFromBuilder(Collection<? extends ReplicaRegionType.Builder> addReplicaRegionListTypeParam) {
        Object list;
        if (addReplicaRegionListTypeParam == null || addReplicaRegionListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            addReplicaRegionListTypeParam.forEach(entry -> {
                ReplicaRegionType member = entry == null ? null : (ReplicaRegionType)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ReplicaRegionType.Builder> copyToBuilder(Collection<? extends ReplicaRegionType> addReplicaRegionListTypeParam) {
        Object list;
        if (addReplicaRegionListTypeParam == null || addReplicaRegionListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            addReplicaRegionListTypeParam.forEach(entry -> {
                ReplicaRegionType.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

