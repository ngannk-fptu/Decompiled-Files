/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.util.Collections;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.ClientCertificateAuthenticationOptions;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class ClientCertificateAuthentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private static final Log logger = LogFactory.getLog(ClientCertificateAuthentication.class);
    private final ClientCertificateAuthenticationOptions options;
    private final RestOperations restOperations;

    public ClientCertificateAuthentication(RestOperations restOperations) {
        this(ClientCertificateAuthenticationOptions.builder().build(), restOperations);
    }

    public ClientCertificateAuthentication(ClientCertificateAuthenticationOptions options, RestOperations restOperations) {
        Assert.notNull((Object)options, "ClientCertificateAuthenticationOptions must not be null");
        Assert.notNull((Object)restOperations, "RestOperations must not be null");
        this.restOperations = restOperations;
        this.options = options;
    }

    public static AuthenticationSteps createAuthenticationSteps() {
        return ClientCertificateAuthentication.createAuthenticationSteps(ClientCertificateAuthenticationOptions.builder().build());
    }

    public static AuthenticationSteps createAuthenticationSteps(ClientCertificateAuthenticationOptions options) {
        Assert.notNull((Object)options, "ClientCertificateAuthenticationOptions must not be null");
        Map<String, Object> body = ClientCertificateAuthentication.getRequestBody(options);
        return AuthenticationSteps.fromSupplier(() -> body).login(AuthenticationSteps.HttpRequestBuilder.post(AuthenticationUtil.getLoginPath(options.getPath()), new String[0]).as(VaultResponse.class));
    }

    @Override
    public VaultToken login() {
        return this.createTokenUsingTlsCertAuthentication();
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return ClientCertificateAuthentication.createAuthenticationSteps(this.options);
    }

    private VaultToken createTokenUsingTlsCertAuthentication() {
        try {
            Map<String, Object> request = ClientCertificateAuthentication.getRequestBody(this.options);
            VaultResponse response = this.restOperations.postForObject(AuthenticationUtil.getLoginPath(this.options.getPath()), request, VaultResponse.class, new Object[0]);
            Assert.state(response.getAuth() != null, "Auth field must not be null");
            logger.debug("Login successful using TLS certificates");
            return LoginTokenUtil.from(response.getAuth());
        }
        catch (RestClientException e) {
            throw VaultLoginException.create("TLS Certificates", e);
        }
    }

    private static Map<String, Object> getRequestBody(ClientCertificateAuthenticationOptions options) {
        String name = options.getRole();
        return name != null ? Collections.singletonMap("name", name) : Collections.emptyMap();
    }
}

