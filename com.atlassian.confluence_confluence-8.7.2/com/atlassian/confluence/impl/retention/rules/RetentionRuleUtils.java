/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.impl.retention.RetentionType;
import java.util.Objects;

public class RetentionRuleUtils {
    public static boolean hasDeletingRules(RetentionPolicy policy, RetentionType type) {
        Objects.requireNonNull(policy);
        Objects.requireNonNull(type);
        switch (type) {
            case HISTORICAL_VERSION: {
                return !policy.getPageVersionRule().getKeepAll() || !policy.getAttachmentRetentionRule().getKeepAll();
            }
            case TRASH: {
                return !policy.getTrashRetentionRule().getKeepAll();
            }
        }
        throw new IllegalArgumentException("Unrecognised retention type: " + type.name());
    }
}

