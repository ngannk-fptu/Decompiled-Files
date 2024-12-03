/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.links;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class OutgoingLinkMeta {
    @JsonProperty
    private final long contentId;
    @JsonProperty
    private final String destinationLinkLowerTitle;
    @JsonProperty
    private final String destinationSpaceKey;
    @JsonProperty
    private final long incomingLinkCount;

    @JsonCreator
    public OutgoingLinkMeta(long contentId, String destinationSpaceKey, String destinationLinkLowerTitle, long incomingLinkCount) {
        this.contentId = contentId;
        this.destinationSpaceKey = destinationSpaceKey;
        this.destinationLinkLowerTitle = destinationLinkLowerTitle;
        this.incomingLinkCount = incomingLinkCount;
    }

    public long getContentId() {
        return this.contentId;
    }

    public String getDestinationLinkLowerTitle() {
        return this.destinationLinkLowerTitle;
    }

    public String getDestinationSpaceKey() {
        return this.destinationSpaceKey;
    }

    public long getIncomingLinkCount() {
        return this.incomingLinkCount;
    }
}

