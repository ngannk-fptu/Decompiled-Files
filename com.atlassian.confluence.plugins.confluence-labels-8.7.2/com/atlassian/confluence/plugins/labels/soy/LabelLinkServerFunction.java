/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.labels.soy;

import com.atlassian.confluence.labels.Label;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class LabelLinkServerFunction
implements SoyServerFunction<String> {
    public String apply(Object ... args) {
        String spaceKey = (String)args[0];
        Label label = (Label)args[1];
        if (label != null) {
            return label.getUrlPath(spaceKey);
        }
        return "";
    }

    public String getName() {
        return "labelLink";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)2);
    }
}

