/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.OAuthRequestVerifier
 *  com.atlassian.sal.api.auth.OAuthRequestVerifierFactory
 *  javax.servlet.ServletRequest
 */
package com.atlassian.sal.core.auth;

import com.atlassian.sal.api.auth.OAuthRequestVerifier;
import com.atlassian.sal.api.auth.OAuthRequestVerifierFactory;
import com.atlassian.sal.core.auth.OAuthRequestVerifierImpl;
import javax.servlet.ServletRequest;

public class OAuthRequestVerifierFactoryImpl
implements OAuthRequestVerifierFactory {
    private static final OAuthRequestVerifier instance = new OAuthRequestVerifierImpl();

    public OAuthRequestVerifier getInstance(ServletRequest request) {
        return instance;
    }
}

