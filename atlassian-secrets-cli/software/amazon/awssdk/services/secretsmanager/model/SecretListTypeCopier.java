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
import software.amazon.awssdk.services.secretsmanager.model.SecretListEntry;

final class SecretListTypeCopier {
    SecretListTypeCopier() {
    }

    static List<SecretListEntry> copy(Collection<? extends SecretListEntry> secretListTypeParam) {
        List<SecretListEntry> list;
        if (secretListTypeParam == null || secretListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            secretListTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<SecretListEntry> copyFromBuilder(Collection<? extends SecretListEntry.Builder> secretListTypeParam) {
        List<SecretListEntry> list;
        if (secretListTypeParam == null || secretListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            secretListTypeParam.forEach(entry -> {
                SecretListEntry member = entry == null ? null : (SecretListEntry)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<SecretListEntry.Builder> copyToBuilder(Collection<? extends SecretListEntry> secretListTypeParam) {
        List<SecretListEntry.Builder> list;
        if (secretListTypeParam == null || secretListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            secretListTypeParam.forEach(entry -> {
                SecretListEntry.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

