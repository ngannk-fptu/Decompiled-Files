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
import software.amazon.awssdk.services.s3.model.CORSRule;

final class CORSRulesCopier {
    CORSRulesCopier() {
    }

    static List<CORSRule> copy(Collection<? extends CORSRule> corsRulesParam) {
        Object list;
        if (corsRulesParam == null || corsRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            corsRulesParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<CORSRule> copyFromBuilder(Collection<? extends CORSRule.Builder> corsRulesParam) {
        Object list;
        if (corsRulesParam == null || corsRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            corsRulesParam.forEach(entry -> {
                CORSRule member = entry == null ? null : (CORSRule)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<CORSRule.Builder> copyToBuilder(Collection<? extends CORSRule> corsRulesParam) {
        Object list;
        if (corsRulesParam == null || corsRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            corsRulesParam.forEach(entry -> {
                CORSRule.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

