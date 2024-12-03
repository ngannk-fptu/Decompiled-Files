/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.ResponseEntity
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.HttpStatusCodeException
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.authentication;

import java.util.LinkedHashMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.GcpComputeAuthenticationOptions;
import org.springframework.vault.authentication.GcpJwtAuthenticationSupport;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

public class GcpComputeAuthentication
extends GcpJwtAuthenticationSupport
implements ClientAuthentication,
AuthenticationStepsFactory {
    public static final String COMPUTE_METADATA_URL_TEMPLATE = "http://metadata/computeMetadata/v1/instance/service-accounts/{serviceAccount}/identity?audience={audience}&format={format}";
    private final GcpComputeAuthenticationOptions options;
    private final RestOperations googleMetadataRestOperations;

    public GcpComputeAuthentication(GcpComputeAuthenticationOptions options, RestOperations vaultRestOperations) {
        this(options, vaultRestOperations, vaultRestOperations);
    }

    public GcpComputeAuthentication(GcpComputeAuthenticationOptions options, RestOperations vaultRestOperations, RestOperations googleMetadataRestOperations) {
        super(vaultRestOperations);
        Assert.notNull((Object)options, (String)"GcpGceAuthenticationOptions must not be null");
        Assert.notNull((Object)googleMetadataRestOperations, (String)"Google Metadata RestOperations must not be null");
        this.options = options;
        this.googleMetadataRestOperations = googleMetadataRestOperations;
    }

    public static AuthenticationSteps createAuthenticationSteps(GcpComputeAuthenticationOptions options) {
        Assert.notNull((Object)options, (String)"CubbyholeAuthenticationOptions must not be null");
        String serviceAccount = options.getServiceAccount();
        String audience = GcpComputeAuthentication.getAudience(options.getRole());
        AuthenticationSteps.HttpRequest<String> jwtRequest = AuthenticationSteps.HttpRequestBuilder.get(COMPUTE_METADATA_URL_TEMPLATE, serviceAccount, audience, "full").with(GcpComputeAuthentication.getMetadataHttpHeaders()).as(String.class);
        return AuthenticationSteps.fromHttpRequest(jwtRequest).map(jwt -> GcpComputeAuthentication.createRequestBody(options.getRole(), jwt)).login(AuthenticationUtil.getLoginPath(options.getPath()), new String[0]);
    }

    @Override
    public VaultToken login() throws VaultException {
        String signedJwt = this.signJwt();
        return this.doLogin("GCP-GCE", signedJwt, this.options.getPath(), this.options.getRole());
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return GcpComputeAuthentication.createAuthenticationSteps(this.options);
    }

    protected String signJwt() {
        try {
            LinkedHashMap<String, String> urlParameters = new LinkedHashMap<String, String>();
            urlParameters.put("serviceAccount", this.options.getServiceAccount());
            urlParameters.put("audience", GcpComputeAuthentication.getAudience(this.options.getRole()));
            urlParameters.put("format", "full");
            HttpHeaders headers = GcpComputeAuthentication.getMetadataHttpHeaders();
            HttpEntity entity = new HttpEntity((MultiValueMap)headers);
            ResponseEntity response = this.googleMetadataRestOperations.exchange(COMPUTE_METADATA_URL_TEMPLATE, HttpMethod.GET, entity, String.class, urlParameters);
            return (String)response.getBody();
        }
        catch (HttpStatusCodeException e) {
            throw new VaultLoginException("Cannot obtain signed identity", e);
        }
    }

    private static HttpHeaders getMetadataHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Metadata-Flavor", "Google");
        return headers;
    }

    private static String getAudience(String role) {
        return String.format("https://localhost:8200/vault/%s", role);
    }
}

