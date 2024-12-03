/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.legacy;

import java.util.Collections;
import java.util.Set;

public class InclusionState {
    public boolean superbatch;
    public final Set<String> webresources;
    public final Set<String> contexts;
    public final Set<String> topLevel;

    public InclusionState(boolean superbatch, Set<String> webresources, Set<String> contexts) {
        this(superbatch, webresources, contexts, Collections.emptySet());
    }

    public InclusionState(boolean superbatch, Set<String> webresources, Set<String> contexts, Set<String> topLevel) {
        this.superbatch = superbatch;
        this.webresources = webresources;
        this.contexts = contexts;
        this.topLevel = topLevel;
    }
}

