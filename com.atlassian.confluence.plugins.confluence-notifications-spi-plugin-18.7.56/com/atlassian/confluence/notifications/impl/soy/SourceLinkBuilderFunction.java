/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.notifications.impl.soy;

import com.atlassian.confluence.notifications.impl.soy.AnalyticsLinkBuilder;
import com.atlassian.fugue.Option;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class SourceLinkBuilderFunction
implements SoyServerFunction<String> {
    private static final ImmutableSet<Integer> ARG_SIZE = ImmutableSet.of((Object)1, (Object)2);
    private final AnalyticsLinkBuilder analyticsLinkBuilder;

    public SourceLinkBuilderFunction(AnalyticsLinkBuilder analyticsLinkBuilder) {
        this.analyticsLinkBuilder = analyticsLinkBuilder;
    }

    public String apply(Object ... args) {
        Option action;
        String linkBody;
        Preconditions.checkNotNull((Object)args);
        Preconditions.checkNotNull((Object)args[0]);
        Object link = args[0];
        if (link instanceof String) {
            linkBody = (String)link;
            action = args.length == 2 ? Option.some((Object)String.valueOf(args[1])) : Option.none();
        } else {
            throw new IllegalArgumentException("Expected parameter 0 to be a String");
        }
        return this.analyticsLinkBuilder.buildAnalyticsQuery(linkBody, (Option<String>)action);
    }

    public String getName() {
        return "sourceLink";
    }

    public Set<Integer> validArgSizes() {
        return ARG_SIZE;
    }
}

