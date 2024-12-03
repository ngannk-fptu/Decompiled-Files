/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.ResponseEntity
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.HttpStatusCodeException
 *  org.springframework.web.client.RestClientException
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.authentication;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.AppRoleTokens;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.UnwrappingEndpoints;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class AppRoleAuthentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private static final Log logger = LogFactory.getLog(AppRoleAuthentication.class);
    private final AppRoleAuthenticationOptions options;
    private final RestOperations restOperations;

    public AppRoleAuthentication(AppRoleAuthenticationOptions options, RestOperations restOperations) {
        Assert.notNull((Object)options, (String)"AppRoleAuthenticationOptions must not be null");
        Assert.notNull((Object)restOperations, (String)"RestOperations must not be null");
        this.options = options;
        this.restOperations = restOperations;
    }

    public static AuthenticationSteps createAuthenticationSteps(AppRoleAuthenticationOptions options) {
        Assert.notNull((Object)options, (String)"AppRoleAuthenticationOptions must not be null");
        AppRoleAuthenticationOptions.RoleId roleId = options.getRoleId();
        AppRoleAuthenticationOptions.SecretId secretId = options.getSecretId();
        return AppRoleAuthentication.getAuthenticationSteps(options, roleId, secretId).login(AuthenticationUtil.getLoginPath(options.getPath()), new String[0]);
    }

    private static AuthenticationSteps.Node<Map<String, String>> getAuthenticationSteps(AppRoleAuthenticationOptions options, AppRoleAuthenticationOptions.RoleId roleId, AppRoleAuthenticationOptions.SecretId secretId) {
        AuthenticationSteps.Node<String> roleIdSteps = AppRoleAuthentication.getRoleIdSteps(options, roleId);
        if (!AppRoleAuthentication.hasSecretId(options.getSecretId())) {
            return roleIdSteps.map(it -> AppRoleAuthentication.getAppRoleLoginBody(it, null));
        }
        AuthenticationSteps.Node<String> secretIdSteps = AppRoleAuthentication.getSecretIdSteps(options, secretId);
        return roleIdSteps.zipWith(secretIdSteps).map(it -> AppRoleAuthentication.getAppRoleLoginBody((String)it.getLeft(), (String)it.getRight()));
    }

    private static AuthenticationSteps.Node<String> getRoleIdSteps(AppRoleAuthenticationOptions options, AppRoleAuthenticationOptions.RoleId roleId) {
        if (roleId instanceof AppRoleTokens.Provided) {
            return AuthenticationSteps.fromValue(((AppRoleTokens.Provided)roleId).getValue());
        }
        if (roleId instanceof AppRoleTokens.Pull) {
            HttpHeaders headers = AppRoleAuthentication.createHttpHeaders(((AppRoleTokens.Pull)roleId).getInitialToken());
            return AuthenticationSteps.fromHttpRequest(AuthenticationSteps.HttpRequestBuilder.get(AppRoleAuthentication.getRoleIdIdPath(options), new String[0]).with(headers).as(VaultResponse.class)).map(vaultResponse -> (String)((Map)vaultResponse.getRequiredData()).get("role_id"));
        }
        if (roleId instanceof AppRoleTokens.Wrapped) {
            return AppRoleAuthentication.unwrapResponse(options.getUnwrappingEndpoints(), ((AppRoleTokens.Wrapped)roleId).getInitialToken()).map(vaultResponse -> (String)((Map)vaultResponse.getRequiredData()).get("role_id"));
        }
        throw new IllegalArgumentException("Unknown RoleId configuration: " + roleId);
    }

    private static AuthenticationSteps.Node<String> getSecretIdSteps(AppRoleAuthenticationOptions options, AppRoleAuthenticationOptions.SecretId secretId) {
        if (secretId instanceof AppRoleTokens.Provided) {
            return AuthenticationSteps.fromValue(((AppRoleTokens.Provided)secretId).getValue());
        }
        if (secretId instanceof AppRoleTokens.Pull) {
            HttpHeaders headers = AppRoleAuthentication.createHttpHeaders(((AppRoleTokens.Pull)secretId).getInitialToken());
            return AuthenticationSteps.fromHttpRequest(AuthenticationSteps.HttpRequestBuilder.post(AppRoleAuthentication.getSecretIdPath(options), new String[0]).with(headers).as(VaultResponse.class)).map(vaultResponse -> (String)((Map)vaultResponse.getRequiredData()).get("secret_id"));
        }
        if (secretId instanceof AppRoleTokens.Wrapped) {
            return AppRoleAuthentication.unwrapResponse(options.getUnwrappingEndpoints(), ((AppRoleTokens.Wrapped)secretId).getInitialToken()).map(vaultResponse -> (String)((Map)vaultResponse.getRequiredData()).get("secret_id"));
        }
        throw new IllegalArgumentException("Unknown SecretId configuration: " + secretId);
    }

    private static AuthenticationSteps.Node<VaultResponse> unwrapResponse(UnwrappingEndpoints unwrappingEndpoints, VaultToken token) {
        return AuthenticationSteps.fromHttpRequest(AuthenticationSteps.HttpRequestBuilder.method(unwrappingEndpoints.getUnwrapRequestMethod(), unwrappingEndpoints.getPath(), new String[0]).with(AppRoleAuthentication.createHttpHeaders(token)).as(VaultResponse.class)).map(unwrappingEndpoints::unwrap);
    }

    @Override
    public VaultToken login() {
        return this.createTokenUsingAppRole();
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return AppRoleAuthentication.createAuthenticationSteps(this.options);
    }

    private VaultToken createTokenUsingAppRole() {
        Map<String, String> login = this.getAppRoleLoginBody(this.options.getRoleId(), this.options.getSecretId());
        try {
            VaultResponse response = (VaultResponse)this.restOperations.postForObject(AuthenticationUtil.getLoginPath(this.options.getPath()), login, VaultResponse.class, new Object[0]);
            Assert.state((response != null && response.getAuth() != null ? 1 : 0) != 0, (String)"Auth field must not be null");
            logger.debug((Object)"Login successful using AppRole authentication");
            return LoginTokenUtil.from(response.getAuth());
        }
        catch (RestClientException e) {
            throw VaultLoginException.create("AppRole", e);
        }
    }

    private String getRoleId(AppRoleAuthenticationOptions.RoleId roleId) throws VaultLoginException {
        if (roleId instanceof AppRoleTokens.Provided) {
            return ((AppRoleTokens.Provided)roleId).getValue();
        }
        if (roleId instanceof AppRoleTokens.Pull) {
            VaultToken token = ((AppRoleTokens.Pull)roleId).getInitialToken();
            try {
                ResponseEntity entity = this.restOperations.exchange(AppRoleAuthentication.getRoleIdIdPath(this.options), HttpMethod.GET, AppRoleAuthentication.createHttpEntity(token), VaultResponse.class, new Object[0]);
                return (String)((Map)((VaultResponse)entity.getBody()).getRequiredData()).get("role_id");
            }
            catch (HttpStatusCodeException e) {
                throw new VaultLoginException(String.format("Cannot get Role id using AppRole: %s", VaultResponses.getError(e.getResponseBodyAsString())), e);
            }
        }
        if (roleId instanceof AppRoleTokens.Wrapped) {
            VaultToken token = ((AppRoleTokens.Wrapped)roleId).getInitialToken();
            try {
                UnwrappingEndpoints unwrappingEndpoints = this.options.getUnwrappingEndpoints();
                ResponseEntity entity = this.restOperations.exchange(unwrappingEndpoints.getPath(), unwrappingEndpoints.getUnwrapRequestMethod(), AppRoleAuthentication.createHttpEntity(token), VaultResponse.class, new Object[0]);
                VaultResponse response = unwrappingEndpoints.unwrap((VaultResponse)entity.getBody());
                return (String)((Map)response.getRequiredData()).get("role_id");
            }
            catch (HttpStatusCodeException e) {
                throw new VaultLoginException(String.format("Cannot unwrap Role id using AppRole: %s", VaultResponses.getError(e.getResponseBodyAsString())), e);
            }
        }
        throw new IllegalArgumentException("Unknown RoleId configuration: " + roleId);
    }

    private String getSecretId(AppRoleAuthenticationOptions.SecretId secretId) throws VaultLoginException {
        if (secretId instanceof AppRoleTokens.Provided) {
            return ((AppRoleTokens.Provided)secretId).getValue();
        }
        if (secretId instanceof AppRoleTokens.Pull) {
            VaultToken token = ((AppRoleTokens.Pull)secretId).getInitialToken();
            try {
                VaultResponse response = (VaultResponse)this.restOperations.postForObject(AppRoleAuthentication.getSecretIdPath(this.options), AppRoleAuthentication.createHttpEntity(token), VaultResponse.class, new Object[0]);
                return (String)((Map)response.getRequiredData()).get("secret_id");
            }
            catch (HttpStatusCodeException e) {
                throw new VaultLoginException(String.format("Cannot get Secret id using AppRole: %s", VaultResponses.getError(e.getResponseBodyAsString())), e);
            }
        }
        if (secretId instanceof AppRoleTokens.Wrapped) {
            VaultToken token = ((AppRoleTokens.Wrapped)secretId).getInitialToken();
            try {
                UnwrappingEndpoints unwrappingEndpoints = this.options.getUnwrappingEndpoints();
                ResponseEntity entity = this.restOperations.exchange(unwrappingEndpoints.getPath(), unwrappingEndpoints.getUnwrapRequestMethod(), AppRoleAuthentication.createHttpEntity(token), VaultResponse.class, new Object[0]);
                VaultResponse response = unwrappingEndpoints.unwrap((VaultResponse)entity.getBody());
                return (String)((Map)response.getRequiredData()).get("secret_id");
            }
            catch (HttpStatusCodeException e) {
                throw new VaultLoginException(String.format("Cannot unwrap Role id using AppRole: %s", VaultResponses.getError(e.getResponseBodyAsString())), e);
            }
        }
        throw new IllegalArgumentException("Unknown SecretId configuration: " + secretId);
    }

    private static HttpHeaders createHttpHeaders(VaultToken token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Vault-Token", token.getToken());
        return headers;
    }

    private static HttpEntity<String> createHttpEntity(VaultToken token) {
        return new HttpEntity(null, (MultiValueMap)AppRoleAuthentication.createHttpHeaders(token));
    }

    private Map<String, String> getAppRoleLoginBody(AppRoleAuthenticationOptions.RoleId roleId, AppRoleAuthenticationOptions.SecretId secretId) {
        HashMap<String, String> login = new HashMap<String, String>();
        login.put("role_id", this.getRoleId(roleId));
        if (AppRoleAuthentication.hasSecretId(secretId)) {
            login.put("secret_id", this.getSecretId(secretId));
        }
        return login;
    }

    private static boolean hasSecretId(AppRoleAuthenticationOptions.SecretId secretId) {
        return !ClassUtils.isAssignableValue(AppRoleTokens.AbsentSecretId.class, (Object)secretId);
    }

    private static Map<String, String> getAppRoleLoginBody(String roleId, @Nullable String secretId) {
        HashMap<String, String> login = new HashMap<String, String>();
        login.put("role_id", roleId);
        if (secretId != null) {
            login.put("secret_id", secretId);
        }
        return login;
    }

    private static String getSecretIdPath(AppRoleAuthenticationOptions options) {
        return String.format("auth/%s/role/%s/secret-id", options.getPath(), options.getAppRole());
    }

    private static String getRoleIdIdPath(AppRoleAuthenticationOptions options) {
        return String.format("auth/%s/role/%s/role-id", options.getPath(), options.getAppRole());
    }
}

