/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.opensocial;

public interface OpenSocialRequestContext {
    public String getOwnerId();

    public String getViewerId();

    public boolean isAnonymous();

    public String getActiveUrl();
}

