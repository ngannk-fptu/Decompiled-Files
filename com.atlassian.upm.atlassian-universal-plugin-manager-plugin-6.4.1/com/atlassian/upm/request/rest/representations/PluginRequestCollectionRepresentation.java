/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.request.rest.representations;

import com.atlassian.upm.rest.representations.AvailablePluginCollectionRepresentation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PluginRequestCollectionRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final Collection<AvailablePluginCollectionRepresentation.RequestedPluginEntry> plugins;
    @JsonProperty
    private final Boolean hasRequests;

    @JsonCreator
    public PluginRequestCollectionRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="plugins") Collection<AvailablePluginCollectionRepresentation.RequestedPluginEntry> plugins) {
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.plugins = Collections.unmodifiableList(new ArrayList<AvailablePluginCollectionRepresentation.RequestedPluginEntry>(plugins));
        this.hasRequests = true;
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public Collection<AvailablePluginCollectionRepresentation.RequestedPluginEntry> getPlugins() {
        return this.plugins;
    }
}

