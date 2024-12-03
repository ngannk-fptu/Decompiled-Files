/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.baseconditions;

import com.atlassian.plugin.web.baseconditions.BaseCondition;

public interface CompositeCondition<T extends BaseCondition>
extends BaseCondition {
    public void addCondition(T var1);
}

