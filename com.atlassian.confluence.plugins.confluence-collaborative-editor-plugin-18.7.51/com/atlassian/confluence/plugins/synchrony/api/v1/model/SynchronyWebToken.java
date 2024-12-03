/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.synchrony.api.v1.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SynchronyWebToken {
    @JsonProperty
    private final String synchronyToken;
    @JsonProperty
    private final String synchronyExpiry;
    @JsonProperty
    private final Long contentId;

    @JsonCreator
    public SynchronyWebToken(@JsonProperty(value="synchronyToken") String synchronyToken, @JsonProperty(value="synchronyExpiry") String synchronyExpiry, @JsonProperty(value="contentId") Long contentId) {
        this.synchronyToken = synchronyToken;
        this.synchronyExpiry = synchronyExpiry;
        this.contentId = contentId;
    }
}

