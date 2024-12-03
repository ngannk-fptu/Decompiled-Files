/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class VersionNumberFunction
implements SoyServerFunction<String> {
    public String apply(Object ... objects) {
        return GeneralUtil.getVersionNumber();
    }

    public String getName() {
        return "versionNumber";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)0);
    }
}

