/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.eventmacro.events;

import com.atlassian.confluence.extra.calendar3.eventmacro.Reply;
import java.util.List;

public class WaitingAttendantPromoted {
    private String eventTitle;
    private String eventUrlPath;
    private List<Reply> promotedReplies;

    public WaitingAttendantPromoted(String eventTitle, String eventUrlPath, List<Reply> promotedReplies) {
        this.eventTitle = eventTitle;
        this.eventUrlPath = eventUrlPath;
        this.promotedReplies = promotedReplies;
    }

    public String getEventTitle() {
        return this.eventTitle;
    }

    public String getEventUrlPath() {
        return this.eventUrlPath;
    }

    public List<Reply> getPromotedReplies() {
        return this.promotedReplies;
    }
}

