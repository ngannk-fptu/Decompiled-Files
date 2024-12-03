/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.TypeNotInstalledException
 */
package com.atlassian.jwt.applinks;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.jwt.applinks.ApplinkJwt;
import com.atlassian.jwt.applinks.exception.NotAJwtPeerException;
import com.atlassian.jwt.exception.JwtIssuerLacksSharedSecretException;
import com.atlassian.jwt.exception.JwtParseException;
import com.atlassian.jwt.exception.JwtSigningException;
import com.atlassian.jwt.exception.JwtUnknownIssuerException;
import com.atlassian.jwt.exception.JwtVerificationException;
import com.atlassian.jwt.reader.JwtClaimVerifier;
import java.util.Map;

@Deprecated
public interface JwtService {
    public boolean isJwtPeer(ApplicationLink var1);

    public ApplinkJwt verifyJwt(String var1, Map<String, ? extends JwtClaimVerifier> var2) throws NotAJwtPeerException, JwtParseException, JwtVerificationException, TypeNotInstalledException, JwtIssuerLacksSharedSecretException, JwtUnknownIssuerException;

    @Deprecated
    public String issueJwt(String var1, ApplicationLink var2) throws NotAJwtPeerException, JwtSigningException;

    public String issueJwt(String var1, String var2) throws JwtSigningException;
}

