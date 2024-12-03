/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckContext
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.cmpt.check.base.CheckContext;
import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface CheckContextProvider<T extends CheckContext>
extends Function<Map<String, Object>, T> {
}

