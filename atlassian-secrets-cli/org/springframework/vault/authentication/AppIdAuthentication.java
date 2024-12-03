/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.AppIdAuthenticationOptions;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@Deprecated
public class AppIdAuthentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private static final Log logger = LogFactory.getLog(AppIdAuthentication.class);
    private final AppIdAuthenticationOptions options;
    private final RestOperations restOperations;

    public AppIdAuthentication(AppIdAuthenticationOptions options, RestOperations restOperations) {
        Assert.notNull((Object)options, "AppIdAuthenticationOptions must not be null");
        Assert.notNull((Object)restOperations, "RestOperations must not be null");
        this.options = options;
        this.restOperations = restOperations;
    }

    public static AuthenticationSteps createAuthenticationSteps(AppIdAuthenticationOptions options) {
        Assert.notNull((Object)options, "AppIdAuthenticationOptions must not be null");
        return AuthenticationSteps.fromSupplier(() -> AppIdAuthentication.getAppIdLogin(options.getAppId(), options.getUserIdMechanism().createUserId())).login(AuthenticationUtil.getLoginPath(options.getPath()), new String[0]);
    }

    @Override
    public VaultToken login() {
        return this.createTokenUsingAppId();
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return AppIdAuthentication.createAuthenticationSteps(this.options);
    }

    private VaultToken createTokenUsingAppId() {
        Map<String, String> login = AppIdAuthentication.getAppIdLogin(this.options.getAppId(), this.options.getUserIdMechanism().createUserId());
        try {
            VaultResponse response = this.restOperations.postForObject(AuthenticationUtil.getLoginPath(this.options.getPath()), login, VaultResponse.class, new Object[0]);
            Assert.state(response != null && response.getAuth() != null, "Auth field must not be null");
            logger.debug("Login successful using AppId authentication");
            return LoginTokenUtil.from(response.getAuth());
        }
        catch (RestClientException e) {
            throw VaultLoginException.create("app-id", e);
        }
    }

    private static Map<String, String> getAppIdLogin(String appId, String userId) {
        HashMap<String, String> login = new HashMap<String, String>();
        login.put("app_id", appId);
        login.put("user_id", userId);
        return login;
    }
}

