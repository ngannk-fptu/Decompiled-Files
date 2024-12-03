/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.extra.calendar3.eventmacro;

import com.atlassian.confluence.extra.calendar3.eventmacro.Reply;
import com.atlassian.confluence.extra.calendar3.eventmacro.rest.ReplyResource;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class EventRepresentation {
    @JsonProperty
    private final String pageId;
    @JsonProperty
    private final String occurrence;
    @JsonProperty
    private final List<ReplyResource.ReplyRepresentation> replies;

    @JsonCreator
    public EventRepresentation(@JsonProperty(value="pageId") String pageId, @JsonProperty(value="occurrence") String occurrence, @JsonProperty(value="replies") List<Reply> replies) {
        this.pageId = pageId;
        this.occurrence = occurrence;
        this.replies = new ArrayList<ReplyResource.ReplyRepresentation>();
        for (Reply reply : replies) {
            this.replies.add(new ReplyResource.ReplyRepresentation(reply));
        }
    }

    public String getPageId() {
        return this.pageId;
    }

    public String getOccurrence() {
        return this.occurrence;
    }

    public List<ReplyResource.ReplyRepresentation> getReplies() {
        return this.replies;
    }
}

