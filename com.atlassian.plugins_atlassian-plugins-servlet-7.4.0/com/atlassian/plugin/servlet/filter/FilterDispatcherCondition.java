/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 */
package com.atlassian.plugin.servlet.filter;

import javax.servlet.DispatcherType;

@Deprecated
public enum FilterDispatcherCondition {
    REQUEST(DispatcherType.REQUEST),
    INCLUDE(DispatcherType.INCLUDE),
    FORWARD(DispatcherType.FORWARD),
    ERROR(DispatcherType.ERROR),
    ASYNC(DispatcherType.ASYNC);

    private final DispatcherType dispatcherType;

    private FilterDispatcherCondition(DispatcherType dispatcherType) {
        this.dispatcherType = dispatcherType;
    }

    public static boolean contains(String dispatcher) {
        for (FilterDispatcherCondition cond : FilterDispatcherCondition.values()) {
            if (!cond.toString().equals(dispatcher)) continue;
            return true;
        }
        return false;
    }

    public DispatcherType toDispatcherType() {
        return this.dispatcherType;
    }

    public static FilterDispatcherCondition fromDispatcherType(DispatcherType dispatcherType) {
        for (FilterDispatcherCondition cond : FilterDispatcherCondition.values()) {
            if (!cond.toDispatcherType().equals((Object)dispatcherType)) continue;
            return cond;
        }
        return null;
    }
}

