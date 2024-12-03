/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.Assert
 *  org.springframework.web.client.RestClientException
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.authentication;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public abstract class GcpJwtAuthenticationSupport {
    private static final Log logger = LogFactory.getLog(GcpJwtAuthenticationSupport.class);
    private final RestOperations restOperations;

    GcpJwtAuthenticationSupport(RestOperations restOperations) {
        Assert.notNull((Object)restOperations, (String)"Vault RestOperations must not be null");
        this.restOperations = restOperations;
    }

    VaultToken doLogin(String authenticationName, String signedJwt, String path, String role) {
        Map<String, String> login = GcpJwtAuthenticationSupport.createRequestBody(role, signedJwt);
        try {
            VaultResponse response = (VaultResponse)this.restOperations.postForObject(AuthenticationUtil.getLoginPath(path), login, VaultResponse.class, new Object[0]);
            Assert.state((response != null && response.getAuth() != null ? 1 : 0) != 0, (String)"Auth field must not be null");
            if (logger.isDebugEnabled()) {
                if (response.getAuth().get("metadata") instanceof Map) {
                    Map metadata = (Map)response.getAuth().get("metadata");
                    logger.debug((Object)String.format("Login successful using %s authentication for user id %s", authenticationName, metadata.get("service_account_email")));
                } else {
                    logger.debug((Object)("Login successful using " + authenticationName + " authentication"));
                }
            }
            return LoginTokenUtil.from(response.getAuth());
        }
        catch (RestClientException e) {
            throw VaultLoginException.create(authenticationName, e);
        }
    }

    static Map<String, String> createRequestBody(String role, String signedJwt) {
        HashMap<String, String> login = new HashMap<String, String>();
        login.put("role", role);
        login.put("jwt", signedJwt);
        return login;
    }
}

