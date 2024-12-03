/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl.helpers.url;

import com.atlassian.plugin.webresource.ResourceUrl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Resolved {
    private final Set<String> excludedResolved;
    private final List<ResourceUrl> urls;

    public Resolved(Set<String> excludedResolved) {
        this(Collections.emptyList(), excludedResolved);
    }

    public Resolved(List<ResourceUrl> urls, Set<String> excludedResolved) {
        this.urls = new ArrayList<ResourceUrl>(urls);
        this.excludedResolved = new HashSet<String>(excludedResolved);
    }

    public List<ResourceUrl> getUrls() {
        return this.urls;
    }

    public Set<String> getExcludedResolved() {
        return this.excludedResolved;
    }
}

