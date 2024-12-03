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
import software.amazon.awssdk.services.s3.model.ObjectVersion;

final class ObjectVersionListCopier {
    ObjectVersionListCopier() {
    }

    static List<ObjectVersion> copy(Collection<? extends ObjectVersion> objectVersionListParam) {
        Object list;
        if (objectVersionListParam == null || objectVersionListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            objectVersionListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ObjectVersion> copyFromBuilder(Collection<? extends ObjectVersion.Builder> objectVersionListParam) {
        Object list;
        if (objectVersionListParam == null || objectVersionListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            objectVersionListParam.forEach(entry -> {
                ObjectVersion member = entry == null ? null : (ObjectVersion)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ObjectVersion.Builder> copyToBuilder(Collection<? extends ObjectVersion> objectVersionListParam) {
        Object list;
        if (objectVersionListParam == null || objectVersionListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            objectVersionListParam.forEach(entry -> {
                ObjectVersion.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

