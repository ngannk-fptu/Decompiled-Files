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
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.KubernetesAuthenticationOptions;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class KubernetesAuthentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private static final Log logger = LogFactory.getLog(KubernetesAuthentication.class);
    private final KubernetesAuthenticationOptions options;
    private final RestOperations restOperations;

    public KubernetesAuthentication(KubernetesAuthenticationOptions options, RestOperations restOperations) {
        Assert.notNull((Object)options, (String)"KubernetesAuthenticationOptions must not be null");
        Assert.notNull((Object)restOperations, (String)"RestOperations must not be null");
        this.options = options;
        this.restOperations = restOperations;
    }

    public static AuthenticationSteps createAuthenticationSteps(KubernetesAuthenticationOptions options) {
        Assert.notNull((Object)options, (String)"KubernetesAuthenticationOptions must not be null");
        return AuthenticationSteps.fromSupplier(options.getJwtSupplier()).map(token -> KubernetesAuthentication.getKubernetesLogin(options.getRole(), token)).login(AuthenticationUtil.getLoginPath(options.getPath()), new String[0]);
    }

    @Override
    public VaultToken login() throws VaultException {
        Map<String, String> login = KubernetesAuthentication.getKubernetesLogin(this.options.getRole(), this.options.getJwtSupplier().get());
        try {
            VaultResponse response = (VaultResponse)this.restOperations.postForObject(AuthenticationUtil.getLoginPath(this.options.getPath()), login, VaultResponse.class, new Object[0]);
            Assert.state((response != null && response.getAuth() != null ? 1 : 0) != 0, (String)"Auth field must not be null");
            logger.debug((Object)"Login successful using Kubernetes authentication");
            return LoginTokenUtil.from(response.getAuth());
        }
        catch (RestClientException e) {
            throw VaultLoginException.create("Kubernetes", e);
        }
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return KubernetesAuthentication.createAuthenticationSteps(this.options);
    }

    private static Map<String, String> getKubernetesLogin(String role, String jwt) {
        Assert.hasText((String)role, (String)"Role must not be empty");
        Assert.hasText((String)jwt, (String)"JWT must not be empty");
        HashMap<String, String> login = new HashMap<String, String>();
        login.put("jwt", jwt);
        login.put("role", role);
        return login;
    }
}

