/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.interceptor;

import org.springframework.transaction.interceptor.RollbackRuleAttribute;

public class NoRollbackRuleAttribute
extends RollbackRuleAttribute {
    public NoRollbackRuleAttribute(Class<?> exceptionType) {
        super(exceptionType);
    }

    public NoRollbackRuleAttribute(String exceptionPattern) {
        super(exceptionPattern);
    }

    @Override
    public String toString() {
        return "No" + super.toString();
    }
}

