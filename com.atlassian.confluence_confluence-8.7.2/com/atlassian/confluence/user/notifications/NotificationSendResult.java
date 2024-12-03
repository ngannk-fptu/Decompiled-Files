/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.user.notifications;

import com.google.errorprone.annotations.Immutable;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Immutable
public class NotificationSendResult {
    @XmlElement
    private final Set<String> sentAddresses;
    @XmlElement
    private final Set<String> failedAddresses;

    public NotificationSendResult(Set<String> sentAddresses, Set<String> failedAddresses) {
        this.sentAddresses = sentAddresses;
        this.failedAddresses = failedAddresses;
    }

    public Set<String> getSentAddresses() {
        return this.sentAddresses;
    }

    public Set<String> getFailedAddresses() {
        return this.failedAddresses;
    }
}

