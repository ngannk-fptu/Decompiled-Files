/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.servlet.ServletRequest
 */
package com.atlassian.confluence.plugins.remotepageview.api.service;

import com.atlassian.confluence.plugins.remotepageview.rest.response.TokenResponse;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Optional;
import javax.servlet.ServletRequest;

public interface TokenService {
    public Optional<TokenResponse> generateLoginTokenForUser(ConfluenceUser var1, long var2);

    public Optional<ConfluenceUser> getUserFromRequest(ServletRequest var1);
}

