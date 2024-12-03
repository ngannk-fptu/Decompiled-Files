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
import software.amazon.awssdk.services.s3.model.RoutingRule;

final class RoutingRulesCopier {
    RoutingRulesCopier() {
    }

    static List<RoutingRule> copy(Collection<? extends RoutingRule> routingRulesParam) {
        Object list;
        if (routingRulesParam == null || routingRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            routingRulesParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<RoutingRule> copyFromBuilder(Collection<? extends RoutingRule.Builder> routingRulesParam) {
        Object list;
        if (routingRulesParam == null || routingRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            routingRulesParam.forEach(entry -> {
                RoutingRule member = entry == null ? null : (RoutingRule)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<RoutingRule.Builder> copyToBuilder(Collection<? extends RoutingRule> routingRulesParam) {
        Object list;
        if (routingRulesParam == null || routingRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            routingRulesParam.forEach(entry -> {
                RoutingRule.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

