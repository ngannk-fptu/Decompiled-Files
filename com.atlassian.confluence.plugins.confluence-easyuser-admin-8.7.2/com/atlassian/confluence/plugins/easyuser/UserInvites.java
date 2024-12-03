/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.user.SendUserInviteEvent
 *  com.atlassian.user.User
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.easyuser;

import com.atlassian.confluence.event.events.user.SendUserInviteEvent;
import com.atlassian.user.User;
import com.google.errorprone.annotations.Immutable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
class UserInvites {
    @XmlElement
    private final String emailMessage;
    @XmlElement
    private final List<String> recipientList;

    public UserInvites() {
        this.emailMessage = null;
        this.recipientList = null;
    }

    public UserInvites(String emailMessage, List<String> recipientList) {
        this.emailMessage = emailMessage;
        this.recipientList = recipientList;
    }

    public SendUserInviteEvent buildEvent(Object src, User sender) {
        return new SendUserInviteEvent(src, sender, this.emailMessage, this.recipientList);
    }
}

