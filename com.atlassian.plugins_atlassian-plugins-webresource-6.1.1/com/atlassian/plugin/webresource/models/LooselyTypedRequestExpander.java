/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.models;

import com.atlassian.plugin.webresource.models.RawRequest;
import com.atlassian.plugin.webresource.models.Requestable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LooselyTypedRequestExpander {
    private final RawRequest request;

    public LooselyTypedRequestExpander(RawRequest r) {
        this.request = r;
    }

    public LooselyTypedRequestExpander normalise() {
        RawRequest raw = new RawRequest();
        this.request.getIncluded().stream().filter(Objects::nonNull).filter(item -> !this.request.getExcluded().contains(item)).forEach(raw::include);
        this.request.getExcluded().stream().filter(Objects::nonNull).filter(item -> !this.request.getIncluded().contains(item)).forEach(raw::exclude);
        return new LooselyTypedRequestExpander(raw);
    }

    public Set<String> getIncluded() {
        return this.request.getIncluded().stream().map(this::convert).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> getExcluded() {
        return this.request.getExcluded().stream().map(this::convert).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String convert(Requestable r) {
        return r.toLooseType();
    }
}

