/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ActiveDirectoryTokenHolder {
    @JsonProperty(value="invocationId")
    private final String invocationId;
    @JsonProperty(value="lastUsnChanged")
    private final long lastUsnChanged;

    @JsonCreator
    public ActiveDirectoryTokenHolder(@JsonProperty(value="invocationId") String invocationId, @JsonProperty(value="lastUsnChanged") long lastUsnChanged) {
        this.invocationId = invocationId;
        this.lastUsnChanged = lastUsnChanged;
    }

    public String getInvocationId() {
        return this.invocationId;
    }

    public long getLastUsnChanged() {
        return this.lastUsnChanged;
    }
}

