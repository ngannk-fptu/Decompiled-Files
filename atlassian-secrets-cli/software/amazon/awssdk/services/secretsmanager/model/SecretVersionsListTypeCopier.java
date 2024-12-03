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
import software.amazon.awssdk.services.secretsmanager.model.SecretVersionsListEntry;

final class SecretVersionsListTypeCopier {
    SecretVersionsListTypeCopier() {
    }

    static List<SecretVersionsListEntry> copy(Collection<? extends SecretVersionsListEntry> secretVersionsListTypeParam) {
        List<SecretVersionsListEntry> list;
        if (secretVersionsListTypeParam == null || secretVersionsListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            secretVersionsListTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<SecretVersionsListEntry> copyFromBuilder(Collection<? extends SecretVersionsListEntry.Builder> secretVersionsListTypeParam) {
        List<SecretVersionsListEntry> list;
        if (secretVersionsListTypeParam == null || secretVersionsListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            secretVersionsListTypeParam.forEach(entry -> {
                SecretVersionsListEntry member = entry == null ? null : (SecretVersionsListEntry)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<SecretVersionsListEntry.Builder> copyToBuilder(Collection<? extends SecretVersionsListEntry> secretVersionsListTypeParam) {
        List<SecretVersionsListEntry.Builder> list;
        if (secretVersionsListTypeParam == null || secretVersionsListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            secretVersionsListTypeParam.forEach(entry -> {
                SecretVersionsListEntry.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

