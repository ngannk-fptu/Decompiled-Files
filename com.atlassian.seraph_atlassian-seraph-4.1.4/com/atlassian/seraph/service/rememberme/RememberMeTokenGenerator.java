/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.service.rememberme;

import com.atlassian.seraph.service.rememberme.RememberMeToken;

public interface RememberMeTokenGenerator {
    public RememberMeToken generateToken(String var1);
}

