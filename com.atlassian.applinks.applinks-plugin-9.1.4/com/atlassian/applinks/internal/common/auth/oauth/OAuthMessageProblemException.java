/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.sal.api.net.ResponseException;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OAuthMessageProblemException
extends ResponseException {
    private final Map<String, String> parameters;
    private final String oAuthProblem;
    private final String oAuthAdvice;

    public OAuthMessageProblemException(String message) {
        this(message, null);
    }

    public OAuthMessageProblemException(@Nullable String message, @Nullable Map<String, String> parameters) {
        this(message, null, null, parameters);
    }

    public OAuthMessageProblemException(@Nullable String message, @Nullable String problem, @Nullable String oAuthAdvice, @Nullable Map<String, String> parameters) {
        super(message);
        this.oAuthProblem = problem;
        this.oAuthAdvice = oAuthAdvice;
        this.parameters = parameters != null ? ImmutableMap.copyOf(parameters) : Collections.emptyMap();
    }

    @Nonnull
    public Map<String, String> getParameters() {
        return this.parameters;
    }

    @Nullable
    public String getOAuthProblem() {
        return this.oAuthProblem;
    }

    @Nullable
    public String getOAuthAdvice() {
        return this.oAuthAdvice;
    }
}

