/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.ResponseEntity
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.HttpStatusCodeException
 *  org.springframework.web.client.RestClientException
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.authentication;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LoginToken;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.VaultTokenLookupException;
import org.springframework.vault.client.VaultHttpHeaders;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class LoginTokenAdapter
implements ClientAuthentication {
    private final ClientAuthentication delegate;
    private final RestOperations restOperations;

    public LoginTokenAdapter(ClientAuthentication delegate, RestOperations restOperations) {
        Assert.notNull((Object)delegate, (String)"ClientAuthentication delegate must not be null");
        Assert.notNull((Object)restOperations, (String)"RestOperations must not be null");
        this.delegate = delegate;
        this.restOperations = restOperations;
    }

    @Override
    public LoginToken login() throws VaultException {
        return this.augmentWithSelfLookup(this.delegate.login());
    }

    private LoginToken augmentWithSelfLookup(VaultToken token) {
        return LoginTokenAdapter.augmentWithSelfLookup(this.restOperations, token);
    }

    static LoginToken augmentWithSelfLookup(RestOperations restOperations, VaultToken token) {
        Map<String, Object> data = LoginTokenAdapter.lookupSelf(restOperations, token);
        return LoginTokenUtil.from(token.toCharArray(), data);
    }

    private static Map<String, Object> lookupSelf(RestOperations restOperations, VaultToken token) {
        try {
            ResponseEntity entity = restOperations.exchange("auth/token/lookup-self", HttpMethod.GET, new HttpEntity((MultiValueMap)VaultHttpHeaders.from(token)), VaultResponse.class, new Object[0]);
            Assert.state((entity.getBody() != null && ((VaultResponse)entity.getBody()).getData() != null ? 1 : 0) != 0, (String)"Token response is null");
            return (Map)((VaultResponse)entity.getBody()).getData();
        }
        catch (HttpStatusCodeException e) {
            throw new VaultTokenLookupException(String.format("Token self-lookup failed: %s %s", e.getRawStatusCode(), VaultResponses.getError(e.getResponseBodyAsString())), e);
        }
        catch (RestClientException e) {
            throw new VaultTokenLookupException("Token self-lookup failed", e);
        }
    }
}

