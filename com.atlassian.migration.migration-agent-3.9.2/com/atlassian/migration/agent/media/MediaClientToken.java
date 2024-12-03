/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.media;

import java.io.Serializable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class MediaClientToken
implements Serializable {
    private static final long serialVersionUID = -8887888097036066974L;
    @JsonProperty
    private final String clientId;
    @JsonProperty
    private final String token;

    @JsonCreator
    public MediaClientToken(@JsonProperty(value="clientId") String clientId, @JsonProperty(value="token") String token) {
        this.clientId = clientId;
        this.token = token;
    }

    @Generated
    public String getClientId() {
        return this.clientId;
    }

    @Generated
    public String getToken() {
        return this.token;
    }
}

