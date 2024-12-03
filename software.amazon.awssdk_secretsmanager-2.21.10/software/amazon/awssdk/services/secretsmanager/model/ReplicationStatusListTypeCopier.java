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
import software.amazon.awssdk.services.secretsmanager.model.ReplicationStatusType;

final class ReplicationStatusListTypeCopier {
    ReplicationStatusListTypeCopier() {
    }

    static List<ReplicationStatusType> copy(Collection<? extends ReplicationStatusType> replicationStatusListTypeParam) {
        Object list;
        if (replicationStatusListTypeParam == null || replicationStatusListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            replicationStatusListTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ReplicationStatusType> copyFromBuilder(Collection<? extends ReplicationStatusType.Builder> replicationStatusListTypeParam) {
        Object list;
        if (replicationStatusListTypeParam == null || replicationStatusListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            replicationStatusListTypeParam.forEach(entry -> {
                ReplicationStatusType member = entry == null ? null : (ReplicationStatusType)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ReplicationStatusType.Builder> copyToBuilder(Collection<? extends ReplicationStatusType> replicationStatusListTypeParam) {
        Object list;
        if (replicationStatusListTypeParam == null || replicationStatusListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            replicationStatusListTypeParam.forEach(entry -> {
                ReplicationStatusType.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

