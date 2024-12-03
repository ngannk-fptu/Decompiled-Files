/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceConflict
implements Serializable {
    private static final long serialVersionUID = 1485561483079426961L;
    public final String key;
    public final String name;
    public final String url;

    @JsonCreator
    public SpaceConflict(@JsonProperty(value="key") String key, @JsonProperty(value="name") String name, @JsonProperty(value="url") String url) {
        this.key = key;
        this.name = name;
        this.url = url;
    }
}

