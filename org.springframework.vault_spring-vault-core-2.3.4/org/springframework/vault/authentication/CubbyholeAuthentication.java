/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.ResponseEntity
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.RestClientException
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.authentication;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.CubbyholeAuthenticationOptions;
import org.springframework.vault.authentication.LoginToken;
import org.springframework.vault.authentication.LoginTokenAdapter;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.client.VaultHttpHeaders;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class CubbyholeAuthentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private static final Log logger = LogFactory.getLog(CubbyholeAuthentication.class);
    private final CubbyholeAuthenticationOptions options;
    private final RestOperations restOperations;

    public CubbyholeAuthentication(CubbyholeAuthenticationOptions options, RestOperations restOperations) {
        Assert.notNull((Object)options, (String)"CubbyholeAuthenticationOptions must not be null");
        Assert.notNull((Object)restOperations, (String)"RestOperations must not be null");
        this.options = options;
        this.restOperations = restOperations;
    }

    public static AuthenticationSteps createAuthenticationSteps(CubbyholeAuthenticationOptions options) {
        Assert.notNull((Object)options, (String)"CubbyholeAuthenticationOptions must not be null");
        String url = CubbyholeAuthentication.getRequestPath(options);
        HttpMethod unwrapMethod = CubbyholeAuthentication.getRequestMethod(options);
        HttpEntity<Object> requestEntity = CubbyholeAuthentication.getRequestEntity(options);
        AuthenticationSteps.HttpRequest<VaultResponse> initialRequest = AuthenticationSteps.HttpRequestBuilder.method(unwrapMethod, url, new String[0]).with(requestEntity).as(VaultResponse.class);
        return AuthenticationSteps.fromHttpRequest(initialRequest).login(it -> CubbyholeAuthentication.getToken(options, it, url));
    }

    @Override
    public VaultToken login() throws VaultException {
        String url = CubbyholeAuthentication.getRequestPath(this.options);
        VaultResponse data = this.lookupToken(url);
        VaultToken tokenToUse = CubbyholeAuthentication.getToken(this.options, data, url);
        if (this.shouldEnhanceTokenWithSelfLookup(tokenToUse)) {
            LoginTokenAdapter adapter = new LoginTokenAdapter(new TokenAuthentication(tokenToUse), this.restOperations);
            tokenToUse = adapter.login();
        }
        logger.debug((Object)"Login successful using Cubbyhole authentication");
        return tokenToUse;
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return CubbyholeAuthentication.createAuthenticationSteps(this.options);
    }

    @Nullable
    private VaultResponse lookupToken(String url) {
        try {
            HttpMethod unwrapMethod = CubbyholeAuthentication.getRequestMethod(this.options);
            HttpEntity<Object> requestEntity = CubbyholeAuthentication.getRequestEntity(this.options);
            ResponseEntity entity = this.restOperations.exchange(url, unwrapMethod, requestEntity, VaultResponse.class, new Object[0]);
            Assert.state((entity.getBody() != null ? 1 : 0) != 0, (String)"Auth response must not be null");
            return (VaultResponse)entity.getBody();
        }
        catch (RestClientException e) {
            throw VaultLoginException.create("Cubbyhole", e);
        }
    }

    private boolean shouldEnhanceTokenWithSelfLookup(VaultToken token) {
        LoginToken loginToken;
        if (!this.options.isSelfLookup()) {
            return false;
        }
        return !(token instanceof LoginToken) || !(loginToken = (LoginToken)token).getLeaseDuration().isZero();
    }

    private static HttpEntity<Object> getRequestEntity(CubbyholeAuthenticationOptions options) {
        return new HttpEntity((MultiValueMap)VaultHttpHeaders.from(options.getInitialToken()));
    }

    private static HttpMethod getRequestMethod(CubbyholeAuthenticationOptions options) {
        if (options.isWrappedToken()) {
            return options.getUnwrappingEndpoints().getUnwrapRequestMethod();
        }
        return HttpMethod.GET;
    }

    private static String getRequestPath(CubbyholeAuthenticationOptions options) {
        if (options.isWrappedToken()) {
            return options.getUnwrappingEndpoints().getPath();
        }
        return options.getPath();
    }

    private static VaultToken getToken(CubbyholeAuthenticationOptions options, VaultResponse response, String url) {
        if (options.isWrappedToken()) {
            VaultResponse responseToUse = options.getUnwrappingEndpoints().unwrap(response);
            Assert.state((responseToUse.getAuth() != null ? 1 : 0) != 0, (String)"Auth field must not be null");
            return LoginTokenUtil.from(responseToUse.getAuth());
        }
        Map data = (Map)response.getData();
        if (data == null || data.isEmpty()) {
            throw new VaultLoginException(String.format("Cannot retrieve Token from Cubbyhole: Response at %s does not contain a token", options.getPath()));
        }
        if (data.size() == 1) {
            String token = (String)data.get(data.keySet().iterator().next());
            return VaultToken.of(token);
        }
        throw new VaultLoginException(String.format("Cannot retrieve Token from Cubbyhole: Response at %s does not contain an unique token", url));
    }
}

