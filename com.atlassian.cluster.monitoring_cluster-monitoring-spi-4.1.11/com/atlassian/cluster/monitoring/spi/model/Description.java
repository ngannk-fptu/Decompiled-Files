/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.cluster.monitoring.spi.model;

import com.atlassian.cluster.monitoring.spi.model.Link;
import java.io.Serializable;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Description
implements Serializable {
    @Nullable
    @JsonProperty
    private final String text;
    @Nullable
    @JsonProperty
    private final Link link;

    public Description(String text) {
        this(text, null);
    }

    public Description(Link link) {
        this(null, link);
    }

    public Description(String text, Link link) {
        this.text = text;
        this.link = link;
    }

    public String getText() {
        return this.text;
    }

    public Link getLink() {
        return this.link;
    }
}

