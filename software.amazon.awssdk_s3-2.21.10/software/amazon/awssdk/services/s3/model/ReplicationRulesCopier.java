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
import software.amazon.awssdk.services.s3.model.ReplicationRule;

final class ReplicationRulesCopier {
    ReplicationRulesCopier() {
    }

    static List<ReplicationRule> copy(Collection<? extends ReplicationRule> replicationRulesParam) {
        Object list;
        if (replicationRulesParam == null || replicationRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            replicationRulesParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ReplicationRule> copyFromBuilder(Collection<? extends ReplicationRule.Builder> replicationRulesParam) {
        Object list;
        if (replicationRulesParam == null || replicationRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            replicationRulesParam.forEach(entry -> {
                ReplicationRule member = entry == null ? null : (ReplicationRule)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ReplicationRule.Builder> copyToBuilder(Collection<? extends ReplicationRule> replicationRulesParam) {
        Object list;
        if (replicationRulesParam == null || replicationRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            replicationRulesParam.forEach(entry -> {
                ReplicationRule.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

