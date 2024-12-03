/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.login;

public interface AsyncForgottenLoginManager {
    public void sendResetLink(String var1, int var2);

    public void sendUsernames(String var1);
}

