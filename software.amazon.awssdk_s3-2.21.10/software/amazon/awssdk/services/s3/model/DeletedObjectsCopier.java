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
import software.amazon.awssdk.services.s3.model.DeletedObject;

final class DeletedObjectsCopier {
    DeletedObjectsCopier() {
    }

    static List<DeletedObject> copy(Collection<? extends DeletedObject> deletedObjectsParam) {
        Object list;
        if (deletedObjectsParam == null || deletedObjectsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            deletedObjectsParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<DeletedObject> copyFromBuilder(Collection<? extends DeletedObject.Builder> deletedObjectsParam) {
        Object list;
        if (deletedObjectsParam == null || deletedObjectsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            deletedObjectsParam.forEach(entry -> {
                DeletedObject member = entry == null ? null : (DeletedObject)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<DeletedObject.Builder> copyToBuilder(Collection<? extends DeletedObject> deletedObjectsParam) {
        Object list;
        if (deletedObjectsParam == null || deletedObjectsParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            deletedObjectsParam.forEach(entry -> {
                DeletedObject.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

