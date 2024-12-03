/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class DebugEmailFunction
implements SoyServerFunction<String> {
    private static final ImmutableSet<Integer> ARGS = ImmutableSet.of((Object)1);

    public String apply(Object ... args) {
        if (Boolean.parseBoolean(System.getProperty("debug.email.border", "false"))) {
            return args[0].toString();
        }
        return "";
    }

    public String getName() {
        return "debugOutput";
    }

    public Set<Integer> validArgSizes() {
        return ARGS;
    }
}

