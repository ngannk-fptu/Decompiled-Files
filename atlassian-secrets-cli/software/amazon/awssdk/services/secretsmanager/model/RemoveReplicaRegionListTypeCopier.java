/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;

final class RemoveReplicaRegionListTypeCopier {
    RemoveReplicaRegionListTypeCopier() {
    }

    static List<String> copy(Collection<String> removeReplicaRegionListTypeParam) {
        List<String> list;
        if (removeReplicaRegionListTypeParam == null || removeReplicaRegionListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            removeReplicaRegionListTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

