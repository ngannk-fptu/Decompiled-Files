/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.shindig.auth.SecurityToken
 *  org.apache.shindig.auth.SecurityTokenDecoder
 *  org.apache.shindig.auth.SecurityTokenException
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.gadgets.renderer.internal.servlet;

import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.auth.SecurityTokenDecoder;
import org.apache.shindig.auth.SecurityTokenException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;

public class SecurityTokenServlet
extends HttpServlet {
    private final SecurityTokenDecoder decoder;
    private final UserManager userManager;

    public SecurityTokenServlet(@Qualifier(value="nonExpirableBlobCrypterSecurityTokenDecoder") SecurityTokenDecoder decoder, UserManager userManager) {
        this.decoder = (SecurityTokenDecoder)Preconditions.checkNotNull((Object)decoder, (Object)"decoder");
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = this.userManager.getRemoteUsername(request);
        JSONObject updatedTokens = new JSONObject();
        int i = 0;
        String stParamKey = "st." + i;
        while (request.getParameter(stParamKey) != null) {
            SecurityToken token = this.decode(request.getParameter(stParamKey), request.getRequestURL().toString());
            if (token == null || !Objects.equal((Object)user, (Object)token.getViewerId())) {
                response.sendError(400);
                return;
            }
            try {
                updatedTokens.put(stParamKey, (Object)token.getUpdatedToken());
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
            stParamKey = "st." + ++i;
        }
        response.setContentType("application/json");
        try {
            updatedTokens.write((Writer)response.getWriter());
        }
        catch (JSONException e) {
            throw new ServletException((Throwable)e);
        }
    }

    private SecurityToken decode(String securityToken, String activeUrl) {
        ImmutableMap tokenParameters = ImmutableMap.of((Object)"token", (Object)securityToken, (Object)"activeUrl", (Object)activeUrl);
        try {
            return this.decoder.createToken((Map)tokenParameters);
        }
        catch (SecurityTokenException e) {
            return null;
        }
    }
}

