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
import software.amazon.awssdk.services.s3.model.ServerSideEncryptionRule;

final class ServerSideEncryptionRulesCopier {
    ServerSideEncryptionRulesCopier() {
    }

    static List<ServerSideEncryptionRule> copy(Collection<? extends ServerSideEncryptionRule> serverSideEncryptionRulesParam) {
        Object list;
        if (serverSideEncryptionRulesParam == null || serverSideEncryptionRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            serverSideEncryptionRulesParam.forEach(entry -> modifiableList.add(entry));
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ServerSideEncryptionRule> copyFromBuilder(Collection<? extends ServerSideEncryptionRule.Builder> serverSideEncryptionRulesParam) {
        Object list;
        if (serverSideEncryptionRulesParam == null || serverSideEncryptionRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            serverSideEncryptionRulesParam.forEach(entry -> {
                ServerSideEncryptionRule member = entry == null ? null : (ServerSideEncryptionRule)entry.build();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }

    static List<ServerSideEncryptionRule.Builder> copyToBuilder(Collection<? extends ServerSideEncryptionRule> serverSideEncryptionRulesParam) {
        Object list;
        if (serverSideEncryptionRulesParam == null || serverSideEncryptionRulesParam instanceof SdkAutoConstructList) {
            list = DefaultSdkAutoConstructList.getInstance();
        } else {
            ArrayList modifiableList = new ArrayList();
            serverSideEncryptionRulesParam.forEach(entry -> {
                ServerSideEncryptionRule.Builder member = entry == null ? null : entry.toBuilder();
                modifiableList.add(member);
            });
            list = Collections.unmodifiableList(modifiableList);
        }
        return list;
    }
}

