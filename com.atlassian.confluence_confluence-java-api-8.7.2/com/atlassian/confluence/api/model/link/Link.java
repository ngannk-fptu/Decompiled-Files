/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.link;

import com.atlassian.confluence.api.model.link.LinkType;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Link {
    @JsonProperty
    private final LinkType type;
    @JsonProperty
    private final String path;

    @JsonCreator
    public Link(@JsonProperty(value="type") LinkType type, @JsonProperty(value="path") String path) {
        this.type = type;
        this.path = path;
    }

    public LinkType getType() {
        return this.type;
    }

    public String getPath() {
        return this.path;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Link)) {
            return false;
        }
        Link that = (Link)obj;
        return Objects.equals(this.type, that.type) && Objects.equals(this.path, that.path);
    }

    public int hashCode() {
        return Objects.hash(this.type, this.path);
    }

    public String toString() {
        return this.type + "=" + this.path;
    }
}

