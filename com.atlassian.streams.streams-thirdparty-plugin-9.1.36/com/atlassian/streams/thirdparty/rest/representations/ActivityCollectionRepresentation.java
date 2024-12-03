/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.thirdparty.rest.representations;

import com.atlassian.streams.thirdparty.rest.representations.ActivityRepresentation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ActivityCollectionRepresentation {
    @JsonProperty
    Collection<ActivityRepresentation> items;
    @JsonProperty
    private final Map<String, URI> links;

    @JsonCreator
    public ActivityCollectionRepresentation(@JsonProperty(value="items") Collection<ActivityRepresentation> items, @JsonProperty(value="links") Map<String, URI> links) {
        this.items = new ArrayList<ActivityRepresentation>(items);
        this.links = new HashMap<String, URI>(links);
    }

    public Iterable<ActivityRepresentation> getItems() {
        return Collections.unmodifiableCollection(this.items);
    }

    public Map<String, URI> getLinks() {
        return Collections.unmodifiableMap(this.links);
    }
}

