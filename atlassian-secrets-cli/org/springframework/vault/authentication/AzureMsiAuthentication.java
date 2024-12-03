/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.AuthenticationSteps;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationUtil;
import org.springframework.vault.authentication.AzureMsiAuthenticationOptions;
import org.springframework.vault.authentication.AzureVmEnvironment;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.VaultLoginException;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

public class AzureMsiAuthentication
implements ClientAuthentication,
AuthenticationStepsFactory {
    private static final Log logger = LogFactory.getLog(AzureMsiAuthentication.class);
    private static final HttpEntity<Void> METADATA_HEADERS;
    private final AzureMsiAuthenticationOptions options;
    private final RestOperations vaultRestOperations;
    private final RestOperations azureMetadataRestOperations;

    public AzureMsiAuthentication(AzureMsiAuthenticationOptions options, RestOperations restOperations) {
        this(options, restOperations, restOperations);
    }

    public AzureMsiAuthentication(AzureMsiAuthenticationOptions options, RestOperations vaultRestOperations, RestOperations azureMetadataRestOperations) {
        Assert.notNull((Object)options, "AzureAuthenticationOptions must not be null");
        Assert.notNull((Object)vaultRestOperations, "Vault RestOperations must not be null");
        Assert.notNull((Object)azureMetadataRestOperations, "Azure Instance Metadata RestOperations must not be null");
        this.options = options;
        this.vaultRestOperations = vaultRestOperations;
        this.azureMetadataRestOperations = azureMetadataRestOperations;
    }

    public static AuthenticationSteps createAuthenticationSteps(AzureMsiAuthenticationOptions options) {
        Assert.notNull((Object)options, "AzureMsiAuthenticationOptions must not be null");
        return AzureMsiAuthentication.createAuthenticationSteps(options, options.getVmEnvironment());
    }

    protected static AuthenticationSteps createAuthenticationSteps(AzureMsiAuthenticationOptions options, @Nullable AzureVmEnvironment environment2) {
        AuthenticationSteps.Node<String> msiToken = AuthenticationSteps.fromHttpRequest(AuthenticationSteps.HttpRequestBuilder.get(options.getIdentityTokenServiceUri()).with(METADATA_HEADERS).as(Map.class)).map(token -> (String)token.get("access_token"));
        AuthenticationSteps.Node<AzureVmEnvironment> environmentSteps = environment2 == null ? AuthenticationSteps.fromHttpRequest(AuthenticationSteps.HttpRequestBuilder.get(options.getInstanceMetadataServiceUri()).with(METADATA_HEADERS).as(Map.class)).map(AzureMsiAuthentication::toAzureVmEnvironment) : AuthenticationSteps.fromValue(environment2);
        return environmentSteps.zipWith(msiToken).map(tuple -> AzureMsiAuthentication.getAzureLogin(options.getRole(), (AzureVmEnvironment)tuple.getLeft(), (String)tuple.getRight())).login(AuthenticationUtil.getLoginPath(options.getPath()), new String[0]);
    }

    @Override
    public VaultToken login() throws VaultException {
        return this.createTokenUsingAzureMsiCompute();
    }

    @Override
    public AuthenticationSteps getAuthenticationSteps() {
        return AzureMsiAuthentication.createAuthenticationSteps(this.options);
    }

    private VaultToken createTokenUsingAzureMsiCompute() {
        Map<String, String> login = AzureMsiAuthentication.getAzureLogin(this.options.getRole(), this.getVmEnvironment(), this.getAccessToken());
        try {
            VaultResponse response = this.vaultRestOperations.postForObject(AuthenticationUtil.getLoginPath(this.options.getPath()), login, VaultResponse.class, new Object[0]);
            Assert.state(response != null && response.getAuth() != null, "Auth field must not be null");
            if (logger.isDebugEnabled()) {
                logger.debug("Login successful using Azure authentication");
            }
            return LoginTokenUtil.from(response.getAuth());
        }
        catch (RestClientException e) {
            throw VaultLoginException.create("Azure", e);
        }
    }

    private static Map<String, String> getAzureLogin(String role, AzureVmEnvironment vmEnvironment, String jwt) {
        LinkedHashMap<String, String> loginBody = new LinkedHashMap<String, String>();
        loginBody.put("role", role);
        loginBody.put("jwt", jwt);
        loginBody.put("subscription_id", vmEnvironment.getSubscriptionId());
        loginBody.put("resource_group_name", vmEnvironment.getResourceGroupName());
        loginBody.put("vm_name", vmEnvironment.getVmName());
        loginBody.put("vmss_name", vmEnvironment.getVmScaleSetName());
        return loginBody;
    }

    private String getAccessToken() {
        ResponseEntity<Map> response = this.azureMetadataRestOperations.exchange(this.options.getIdentityTokenServiceUri(), HttpMethod.GET, METADATA_HEADERS, Map.class);
        return (String)((Map)response.getBody()).get("access_token");
    }

    private AzureVmEnvironment getVmEnvironment() {
        AzureVmEnvironment vmEnvironment = this.options.getVmEnvironment();
        return vmEnvironment != null ? vmEnvironment : this.fetchAzureVmEnvironment();
    }

    private AzureVmEnvironment fetchAzureVmEnvironment() {
        ResponseEntity<Map> response = this.azureMetadataRestOperations.exchange(this.options.getInstanceMetadataServiceUri(), HttpMethod.GET, METADATA_HEADERS, Map.class);
        return AzureMsiAuthentication.toAzureVmEnvironment((Map)response.getBody());
    }

    private static AzureVmEnvironment toAzureVmEnvironment(Map<String, Object> instanceMetadata) {
        Map compute = (Map)instanceMetadata.get("compute");
        String subscriptionId = (String)compute.get("subscriptionId");
        String resourceGroupName = (String)compute.get("resourceGroupName");
        String vmName = (String)compute.get("name");
        String vmScaleSetName = (String)compute.get("vmScaleSetName");
        return new AzureVmEnvironment(subscriptionId, resourceGroupName, vmName, vmScaleSetName);
    }

    static {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Metadata", "true");
        METADATA_HEADERS = new HttpEntity(headers);
    }
}

