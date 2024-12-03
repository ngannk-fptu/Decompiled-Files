/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.mywork.rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonCount {
    @JsonProperty
    private int count;
    @JsonProperty
    private int timeout;
    @JsonProperty
    private int maxTimeout;

    private JsonCount() {
    }

    public JsonCount(int count, int timeout, int maxTimeout) {
        this.count = count;
        this.timeout = timeout;
        this.maxTimeout = maxTimeout;
    }

    public int getCount() {
        return this.count;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public int getMaxTimeout() {
        return this.maxTimeout;
    }
}

