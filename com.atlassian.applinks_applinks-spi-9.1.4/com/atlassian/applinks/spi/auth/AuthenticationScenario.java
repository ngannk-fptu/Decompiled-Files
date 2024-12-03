/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.spi.auth;

public interface AuthenticationScenario {
    public boolean isCommonUserBase();

    public boolean isTrusted();
}

