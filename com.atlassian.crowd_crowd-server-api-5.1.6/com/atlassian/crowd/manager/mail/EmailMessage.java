/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  javax.activation.DataSource
 *  javax.annotation.Nullable
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.crowd.manager.mail;

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.mail.internet.InternetAddress;

public interface EmailMessage {
    public Optional<InternetAddress> getFrom();

    @Nullable
    default public InternetAddress getRecipientAddress() {
        return (InternetAddress)Iterables.getFirst(this.getTo(), null);
    }

    public Collection<InternetAddress> getTo();

    public Collection<InternetAddress> getCc();

    public Collection<InternetAddress> getBcc();

    public Collection<InternetAddress> getReplyTo();

    public String getBody();

    public String getSubject();

    public Map<String, String> getHeaders();

    public Map<String, DataSource> getAttachments();
}

