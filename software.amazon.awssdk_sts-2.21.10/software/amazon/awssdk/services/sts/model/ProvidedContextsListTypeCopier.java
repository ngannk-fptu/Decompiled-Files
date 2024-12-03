/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 */
package software.amazon.awssdk.services.sts.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.sts.model.ProvidedContext;

final class ProvidedContextsListTypeCopier {
    ProvidedContextsListTypeCopier() {
    }

    static List<ProvidedContext> copy(Collection<? extends ProvidedContext> providedContextsListTypeParam) {
        Object list;
        if (providedContextsListTypeParam == null || providedContextsListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            providedContextsListTypeParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ProvidedContext> copyFromBuilder(Collection<? extends ProvidedContext.Builder> providedContextsListTypeParam) {
        Object list;
        if (providedContextsListTypeParam == null || providedContextsListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            providedContextsListTypeParam.forEach(entry -> {
                ProvidedContext member = entry == null ? null : (ProvidedContext)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ProvidedContext.Builder> copyToBuilder(Collection<? extends ProvidedContext> providedContextsListTypeParam) {
        Object list;
        if (providedContextsListTypeParam == null || providedContextsListTypeParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            providedContextsListTypeParam.forEach(entry -> {
                ProvidedContext.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

