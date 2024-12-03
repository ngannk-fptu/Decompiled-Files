/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 */
package com.atlassian.sal.api.auth;

import com.atlassian.sal.api.auth.OAuthRequestVerifier;
import javax.servlet.ServletRequest;

public interface OAuthRequestVerifierFactory {
    public OAuthRequestVerifier getInstance(ServletRequest var1);
}

