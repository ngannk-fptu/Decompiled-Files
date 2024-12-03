/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.auth.Anonymous
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.internal.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.auth.Anonymous;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.applinks.JwtPeerService;
import com.atlassian.jwt.applinks.exception.JwtRegistrationFailedException;
import com.atlassian.jwt.internal.security.SecretGenerator;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import javax.annotation.Nonnull;

public class ApplinksJwtPeerService
implements JwtPeerService {
    private final HostApplication hostApplication;

    public ApplinksJwtPeerService(HostApplication hostApplication) {
        this.hostApplication = hostApplication;
    }

    @Override
    public void issueSharedSecret(@Nonnull ApplicationLink applicationLink, @Nonnull String path) throws JwtRegistrationFailedException {
        String sharedSecret = SecretGenerator.generateUrlSafeSharedSecret(SigningAlgorithm.HS256);
        Object addOnKey = applicationLink.getProperty("plugin-key");
        if (null == addOnKey) {
            throw new JwtRegistrationFailedException(String.format("Application link '%s' has no '%s' property. It should have been set during add-on installation! Please reinstall the add-on.", applicationLink.getId(), "plugin-key"));
        }
        try {
            ((ApplicationLinkRequest)applicationLink.createAuthenticatedRequestFactory(Anonymous.class).createRequest(Request.MethodType.POST, path).addRequestParameters(new String[]{"myId", this.hostApplication.getId().get(), "yourId", addOnKey.toString(), "secret", sharedSecret})).execute((ResponseHandler)new ResponseHandler<Response>(){

                public void handle(Response response) throws ResponseException {
                    if (!response.isSuccessful()) {
                        throw new ResponseException("Registration failed, received " + response.getStatusCode() + " " + response.getStatusText() + " from peer.");
                    }
                }
            });
        }
        catch (ResponseException e) {
            throw new JwtRegistrationFailedException(e);
        }
        catch (CredentialsRequiredException e) {
            throw new IllegalStateException(e);
        }
        applicationLink.putProperty("atlassian.jwt.shared.secret", (Object)sharedSecret);
    }

    @Override
    public void revokeSharedSecret(@Nonnull ApplicationLink applicationLink) {
        applicationLink.removeProperty("atlassian.jwt.shared.secret");
    }
}

