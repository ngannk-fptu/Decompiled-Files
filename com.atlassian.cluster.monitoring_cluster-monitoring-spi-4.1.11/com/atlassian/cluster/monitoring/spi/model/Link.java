/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.cluster.monitoring.spi.model;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;

public class Link
implements Serializable {
    @JsonProperty
    private final String text;
    @JsonProperty
    private final String href;

    public Link(String text, String href) {
        this.text = text;
        this.href = href;
    }

    public String getText() {
        return this.text;
    }

    public String getHref() {
        return this.href;
    }
}

