/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pats.api;

import com.atlassian.pats.events.token.TokenEvent;

public interface TokenMailSenderService {
    public void sendTokenEventMail(TokenEvent var1);
}

