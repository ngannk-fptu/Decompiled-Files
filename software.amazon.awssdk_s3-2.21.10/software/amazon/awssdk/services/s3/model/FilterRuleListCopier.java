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
import software.amazon.awssdk.services.s3.model.FilterRule;

final class FilterRuleListCopier {
    FilterRuleListCopier() {
    }

    static List<FilterRule> copy(Collection<? extends FilterRule> filterRuleListParam) {
        Object list;
        if (filterRuleListParam == null || filterRuleListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            filterRuleListParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<FilterRule> copyFromBuilder(Collection<? extends FilterRule.Builder> filterRuleListParam) {
        Object list;
        if (filterRuleListParam == null || filterRuleListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            filterRuleListParam.forEach(entry -> {
                FilterRule member = entry == null ? null : (FilterRule)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<FilterRule.Builder> copyToBuilder(Collection<? extends FilterRule> filterRuleListParam) {
        Object list;
        if (filterRuleListParam == null || filterRuleListParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            filterRuleListParam.forEach(entry -> {
                FilterRule.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

