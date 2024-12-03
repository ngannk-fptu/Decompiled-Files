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
import software.amazon.awssdk.services.secretsmanager.model.Tag;

final class TagListTypeCopier {
    TagListTypeCopier() {
    }

    static List<Tag> copy(Collection<? extends Tag> tagListTypeParam) {
        List<Tag> list;
        if (tagListTypeParam == null || tagListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            tagListTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<Tag> copyFromBuilder(Collection<? extends Tag.Builder> tagListTypeParam) {
        List<Tag> list;
        if (tagListTypeParam == null || tagListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            tagListTypeParam.forEach(entry -> {
                Tag member = entry == null ? null : (Tag)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<Tag.Builder> copyToBuilder(Collection<? extends Tag> tagListTypeParam) {
        List<Tag.Builder> list;
        if (tagListTypeParam == null || tagListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            tagListTypeParam.forEach(entry -> {
                Tag.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

