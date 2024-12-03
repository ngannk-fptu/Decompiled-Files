/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.legacy.AbstractPluginResource;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ContextBatchPluginResource
extends AbstractPluginResource {
    private final List<String> contexts;
    private final Iterable<String> excludedContexts;
    private boolean removeSuperResources;

    public ContextBatchPluginResource(List<String> contexts, Iterable<String> excludedContexts, Set<String> completeKeys, boolean removeSuperResources) {
        super(Collections.unmodifiableSet(completeKeys));
        this.contexts = contexts;
        this.excludedContexts = excludedContexts;
        this.removeSuperResources = removeSuperResources;
    }

    public Iterable<String> getContexts() {
        return this.contexts;
    }

    public Iterable<String> getExcludedContexts() {
        return this.excludedContexts;
    }

    public Collection<ContextBatchPluginResource> splitIntoParts() {
        if (this.contexts.size() <= 1) {
            return Collections.singletonList(this);
        }
        LinkedList<ContextBatchPluginResource> result = new LinkedList<ContextBatchPluginResource>();
        ArrayList<String> furtherExcludes = new ArrayList<String>();
        for (String ctx : this.contexts) {
            Iterable totalExcludes = Iterables.concat(this.excludedContexts, furtherExcludes);
            List<String> newContexts = Collections.singletonList(ctx);
            result.add(new ContextBatchPluginResource(newContexts, Lists.newArrayList((Iterable)totalExcludes), this.completeKeys, this.removeSuperResources));
            furtherExcludes.add(ctx);
        }
        return result;
    }
}

