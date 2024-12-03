/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.impl.retention.RetentionType;

public interface RetentionRulesChecker {
    public boolean hasDeletingRule(RetentionType var1);
}

