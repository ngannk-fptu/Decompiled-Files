/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.synchronisation;

import java.util.Collection;
import java.util.Optional;

public class PartialSynchronisationResult<T> {
    private final Collection<T> results;
    private final String syncToken;

    public PartialSynchronisationResult(Collection<T> results) {
        this.results = results;
        this.syncToken = null;
    }

    public PartialSynchronisationResult(Collection<T> results, String syncToken) {
        this.results = results;
        this.syncToken = syncToken;
    }

    public Collection<T> getResults() {
        return this.results;
    }

    public Optional<String> getSyncToken() {
        return Optional.ofNullable(this.syncToken);
    }
}

