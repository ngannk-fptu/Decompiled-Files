/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.impl.retention.status;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TrashCleanupJobStatus {
    static final long DEFAULT_NEXT_ID_OFFSET = 0L;
    @JsonProperty
    private long nextContentIdOffset;

    public TrashCleanupJobStatus() {
        this(0L);
    }

    public TrashCleanupJobStatus(long nextContentIdOffset) {
        this.nextContentIdOffset = nextContentIdOffset;
    }

    public long getNextContentIdOffset() {
        return this.nextContentIdOffset;
    }

    public void setNextContentIdOffset(long nextContentIdOffset) {
        this.nextContentIdOffset = nextContentIdOffset;
    }

    public String toString() {
        return "SoftCleanupJobStatus{  nextContentIdOffset=" + this.nextContentIdOffset + "}";
    }
}

