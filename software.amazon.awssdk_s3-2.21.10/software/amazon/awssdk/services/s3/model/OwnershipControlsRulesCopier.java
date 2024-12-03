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
import software.amazon.awssdk.services.s3.model.OwnershipControlsRule;

final class OwnershipControlsRulesCopier {
    OwnershipControlsRulesCopier() {
    }

    static List<OwnershipControlsRule> copy(Collection<? extends OwnershipControlsRule> ownershipControlsRulesParam) {
        Object list;
        if (ownershipControlsRulesParam == null || ownershipControlsRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            ownershipControlsRulesParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<OwnershipControlsRule> copyFromBuilder(Collection<? extends OwnershipControlsRule.Builder> ownershipControlsRulesParam) {
        Object list;
        if (ownershipControlsRulesParam == null || ownershipControlsRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            ownershipControlsRulesParam.forEach(entry -> {
                OwnershipControlsRule member = entry == null ? null : (OwnershipControlsRule)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<OwnershipControlsRule.Builder> copyToBuilder(Collection<? extends OwnershipControlsRule> ownershipControlsRulesParam) {
        Object list;
        if (ownershipControlsRulesParam == null || ownershipControlsRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            ownershipControlsRulesParam.forEach(entry -> {
                OwnershipControlsRule.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

