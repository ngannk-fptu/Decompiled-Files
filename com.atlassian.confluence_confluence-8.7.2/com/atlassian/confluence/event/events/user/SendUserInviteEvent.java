/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.user;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.user.User;
import java.util.List;

public class SendUserInviteEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = 4130315451026336347L;
    private User sender;
    private String message;
    private List<String> recipients;

    public SendUserInviteEvent(Object src, User sender, String message, List<String> recipients) {
        super(src);
        this.sender = sender;
        this.message = message;
        this.recipients = recipients;
    }

    public String getMessage() {
        return this.message;
    }

    public List<String> getRecipients() {
        return this.recipients;
    }

    public User getSender() {
        return this.sender;
    }
}

