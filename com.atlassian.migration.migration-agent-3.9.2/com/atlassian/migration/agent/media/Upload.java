/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.media;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Upload {
    @JsonProperty
    private final String id;
    @JsonProperty
    private final int created;
    @JsonProperty
    private final int expires;

    @JsonCreator
    public Upload(@JsonProperty(value="id") String id, @JsonProperty(value="created") int created, @JsonProperty(value="expires") int expires) {
        this.id = id;
        this.created = created;
        this.expires = expires;
    }

    public String getId() {
        return this.id;
    }

    public int getCreated() {
        return this.created;
    }

    public int getExpires() {
        return this.expires;
    }
}

