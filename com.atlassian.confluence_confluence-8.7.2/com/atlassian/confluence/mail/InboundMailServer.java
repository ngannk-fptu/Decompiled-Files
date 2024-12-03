/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.server.MailServer
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.mail;

import com.atlassian.confluence.mail.Authorization;
import com.atlassian.mail.server.MailServer;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface InboundMailServer
extends MailServer {
    public String getToAddress();

    public void setToAddress(String var1);

    public @Nullable Authorization getAuthorization();

    public void setAuthorization(@Nullable Authorization var1);

    default public boolean isBasicAuth() {
        return this.getAuthorization() == null;
    }
}

