/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.osgi.rest.representations;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class CollectionRepresentation<T> {
    @JsonProperty
    private final Collection<T> entries;
    @JsonProperty
    private final boolean safeMode;
    @JsonProperty
    private final Map<String, URI> links;

    @JsonCreator
    public CollectionRepresentation(@JsonProperty(value="entries") Collection<T> entries, @JsonProperty(value="safeMode") boolean safeMode, @JsonProperty(value="links") Map<String, URI> links) {
        this.entries = Collections.unmodifiableCollection(new ArrayList<T>(entries));
        this.safeMode = safeMode;
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
    }

    public Collection<T> getEntries() {
        return this.entries;
    }

    public boolean isSafeMode() {
        return this.safeMode;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }
}

