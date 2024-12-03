/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.user;

import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensRevoke;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessTokensRevokeServlet
extends HttpServlet {
    private final AccessTokensRevoke accessTokensRevoke;

    public AccessTokensRevokeServlet(AccessTokensRevoke accessTokensRevoke) {
        this.accessTokensRevoke = Objects.requireNonNull(accessTokensRevoke, "accessTokensRevoke");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<Integer> errorCode = this.accessTokensRevoke.revoke(request);
        if (errorCode.isPresent()) {
            response.sendError(errorCode.get().intValue());
        }
    }
}

