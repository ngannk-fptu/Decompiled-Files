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
import software.amazon.awssdk.services.secretsmanager.model.Filter;

final class FiltersListTypeCopier {
    FiltersListTypeCopier() {
    }

    static List<Filter> copy(Collection<? extends Filter> filtersListTypeParam) {
        List<Filter> list;
        if (filtersListTypeParam == null || filtersListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            filtersListTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<Filter> copyFromBuilder(Collection<? extends Filter.Builder> filtersListTypeParam) {
        List<Filter> list;
        if (filtersListTypeParam == null || filtersListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            filtersListTypeParam.forEach(entry -> {
                Filter member = entry == null ? null : (Filter)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<Filter.Builder> copyToBuilder(Collection<? extends Filter> filtersListTypeParam) {
        List<Filter.Builder> list;
        if (filtersListTypeParam == null || filtersListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            filtersListTypeParam.forEach(entry -> {
                Filter.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

