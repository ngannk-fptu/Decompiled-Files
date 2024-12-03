/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.service.catalogue.Links;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@ParametersAreNonnullByDefault
@JsonIgnoreProperties(ignoreUnknown=true)
public class Space {
    @JsonProperty
    public String key;
    @JsonProperty
    public String name;
    @JsonProperty(value="_links")
    public Links _links;

    @JsonCreator
    public Space(@JsonProperty(value="key") String key, @JsonProperty(value="name") String name, @JsonProperty(value="_links") Links _links) {
        this.key = key;
        this.name = name;
        this._links = _links;
    }

    @Generated
    public Space() {
    }
}

