/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.vault.authentication;

import java.util.Map;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.client.VaultHttpHeaders;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;

public class TokenAuthentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private final VaultToken token;

    public TokenAuthentication(String token) {
        Assert.hasText((String)token, (String)"Token must not be empty");
        this.token = VaultToken.of(token);
    }

    public TokenAuthentication(VaultToken token) {
        Assert.notNull((Object)token, (String)"Token must not be null");
        this.token = token;
    }

    public static AuthenticationSteps createAuthenticationSteps(VaultToken token, boolean selfLookup) {
        Assert.notNull((Object)token, (String)"VaultToken must not be null");
        if (selfLookup) {
            AuthenticationSteps.HttpRequest<VaultResponse> httpRequest = AuthenticationSteps.HttpRequestBuilder.get("auth/token/lookup-self", new String[0]).with(VaultHttpHeaders.from(token)).as(VaultResponse.class);
            return AuthenticationSteps.fromHttpRequest(httpRequest).login(response -> LoginTokenUtil.from(token.toCharArray(), (Map)response.getRequiredData()));
        }
        return AuthenticationSteps.just(token);
    }

    @Override
    public VaultToken login() {
        return this.token;
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return TokenAuthentication.createAuthenticationSteps(this.token, false);
    }
}

