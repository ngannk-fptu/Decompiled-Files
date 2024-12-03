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
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

final class ObjectIdentifierListCopier {
    ObjectIdentifierListCopier() {
    }

    static List<ObjectIdentifier> copy(Collection<? extends ObjectIdentifier> objectIdentifierListParam) {
        Object list;
        if (objectIdentifierListParam == null || objectIdentifierListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            objectIdentifierListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ObjectIdentifier> copyFromBuilder(Collection<? extends ObjectIdentifier.Builder> objectIdentifierListParam) {
        Object list;
        if (objectIdentifierListParam == null || objectIdentifierListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            objectIdentifierListParam.forEach(entry -> {
                ObjectIdentifier member = entry == null ? null : (ObjectIdentifier)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ObjectIdentifier.Builder> copyToBuilder(Collection<? extends ObjectIdentifier> objectIdentifierListParam) {
        Object list;
        if (objectIdentifierListParam == null || objectIdentifierListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            objectIdentifierListParam.forEach(entry -> {
                ObjectIdentifier.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

