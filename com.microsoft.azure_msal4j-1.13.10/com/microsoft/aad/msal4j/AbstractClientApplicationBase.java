/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.auth.ClientAuthentication
 *  org.slf4j.Logger
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.AADAuthority;
import com.microsoft.aad.msal4j.ADFSAuthority;
import com.microsoft.aad.msal4j.AadInstanceDiscoveryProvider;
import com.microsoft.aad.msal4j.AadInstanceDiscoveryResponse;
import com.microsoft.aad.msal4j.AccountsSupplier;
import com.microsoft.aad.msal4j.AcquireTokenByAuthorizationGrantSupplier;
import com.microsoft.aad.msal4j.AcquireTokenByClientCredentialSupplier;
import com.microsoft.aad.msal4j.AcquireTokenByDeviceCodeFlowSupplier;
import com.microsoft.aad.msal4j.AcquireTokenByInteractiveFlowSupplier;
import com.microsoft.aad.msal4j.AcquireTokenByOnBehalfOfSupplier;
import com.microsoft.aad.msal4j.AcquireTokenSilentSupplier;
import com.microsoft.aad.msal4j.AuthenticationResult;
import com.microsoft.aad.msal4j.AuthenticationResultSupplier;
import com.microsoft.aad.msal4j.Authority;
import com.microsoft.aad.msal4j.AuthorityType;
import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.AuthorizationCodeRequest;
import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters;
import com.microsoft.aad.msal4j.B2CAuthority;
import com.microsoft.aad.msal4j.CIAMAuthority;
import com.microsoft.aad.msal4j.ClientCredentialRequest;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.DefaultHttpClient;
import com.microsoft.aad.msal4j.DeviceCodeFlowRequest;
import com.microsoft.aad.msal4j.HttpHeaders;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientApplicationBase;
import com.microsoft.aad.msal4j.IHttpClient;
import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.InstanceDiscoveryMetadataEntry;
import com.microsoft.aad.msal4j.InteractiveRequest;
import com.microsoft.aad.msal4j.JsonHelper;
import com.microsoft.aad.msal4j.LogHelper;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.MsalRequest;
import com.microsoft.aad.msal4j.OnBehalfOfRequest;
import com.microsoft.aad.msal4j.ParameterValidationUtils;
import com.microsoft.aad.msal4j.PublicApi;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.RefreshTokenParameters;
import com.microsoft.aad.msal4j.RefreshTokenRequest;
import com.microsoft.aad.msal4j.RemoveAccountRunnable;
import com.microsoft.aad.msal4j.RequestContext;
import com.microsoft.aad.msal4j.ServiceBundle;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.SilentRequest;
import com.microsoft.aad.msal4j.TelemetryManager;
import com.microsoft.aad.msal4j.TokenCache;
import com.microsoft.aad.msal4j.TokenRequestExecutor;
import com.microsoft.aad.msal4j.UserIdentifier;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;

public abstract class AbstractClientApplicationBase
implements IClientApplicationBase {
    protected Logger log;
    protected Authority authenticationAuthority;
    private ServiceBundle serviceBundle;
    private String clientId;
    private String authority;
    private boolean validateAuthority;
    private String correlationId;
    private boolean logPii;
    private Consumer<List<HashMap<String, String>>> telemetryConsumer;
    private Proxy proxy;
    private SSLSocketFactory sslSocketFactory;
    private Integer connectTimeoutForDefaultHttpClient;
    private Integer readTimeoutForDefaultHttpClient;
    protected TokenCache tokenCache;
    private String applicationName;
    private String applicationVersion;
    private AadInstanceDiscoveryResponse aadAadInstanceDiscoveryResponse;
    private String clientCapabilities;
    private boolean autoDetectRegion;
    protected String azureRegion;
    private boolean instanceDiscovery;

    protected abstract ClientAuthentication clientAuthentication();

    @Override
    public CompletableFuture<IAuthenticationResult> acquireToken(AuthorizationCodeParameters parameters) {
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        RequestContext context = new RequestContext(this, PublicApi.ACQUIRE_TOKEN_BY_AUTHORIZATION_CODE, parameters);
        AuthorizationCodeRequest authorizationCodeRequest = new AuthorizationCodeRequest(parameters, this, context);
        return this.executeRequest(authorizationCodeRequest);
    }

    @Override
    public CompletableFuture<IAuthenticationResult> acquireToken(RefreshTokenParameters parameters) {
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        RequestContext context = new RequestContext(this, PublicApi.ACQUIRE_TOKEN_BY_REFRESH_TOKEN, parameters);
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(parameters, this, context);
        return this.executeRequest(refreshTokenRequest);
    }

    @Override
    public CompletableFuture<IAuthenticationResult> acquireTokenSilently(SilentParameters parameters) throws MalformedURLException {
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        RequestContext context = parameters.account() != null ? new RequestContext(this, PublicApi.ACQUIRE_TOKEN_SILENTLY, parameters, UserIdentifier.fromHomeAccountId(parameters.account().homeAccountId())) : new RequestContext(this, PublicApi.ACQUIRE_TOKEN_SILENTLY, parameters);
        SilentRequest silentRequest = new SilentRequest(parameters, this, context, null);
        return this.executeRequest(silentRequest);
    }

    @Override
    public CompletableFuture<Set<IAccount>> getAccounts() {
        RequestContext context = new RequestContext(this, PublicApi.GET_ACCOUNTS, null);
        MsalRequest msalRequest = new MsalRequest(this, null, context){};
        AccountsSupplier supplier = new AccountsSupplier(this, msalRequest);
        return this.serviceBundle.getExecutorService() != null ? CompletableFuture.supplyAsync(supplier, this.serviceBundle.getExecutorService()) : CompletableFuture.supplyAsync(supplier);
    }

    @Override
    public CompletableFuture<Void> removeAccount(IAccount account) {
        RequestContext context = new RequestContext(this, PublicApi.REMOVE_ACCOUNTS, null);
        MsalRequest msalRequest = new MsalRequest(this, null, context){};
        RemoveAccountRunnable runnable = new RemoveAccountRunnable(msalRequest, account);
        return this.serviceBundle.getExecutorService() != null ? CompletableFuture.runAsync(runnable, this.serviceBundle.getExecutorService()) : CompletableFuture.runAsync(runnable);
    }

    @Override
    public URL getAuthorizationRequestUrl(AuthorizationRequestUrlParameters parameters) {
        ParameterValidationUtils.validateNotNull("parameters", parameters);
        parameters.requestParameters.put("client_id", Collections.singletonList(this.clientId));
        if (this.clientCapabilities != null) {
            if (parameters.requestParameters.containsKey("claims")) {
                String claims = String.valueOf(parameters.requestParameters.get("claims").get(0));
                String mergedClaimsCapabilities = JsonHelper.mergeJSONString(claims, this.clientCapabilities);
                parameters.requestParameters.put("claims", Collections.singletonList(mergedClaimsCapabilities));
            } else {
                parameters.requestParameters.put("claims", Collections.singletonList(this.clientCapabilities));
            }
        }
        return parameters.createAuthorizationURL(this.authenticationAuthority, parameters.requestParameters());
    }

    CompletableFuture<IAuthenticationResult> executeRequest(MsalRequest msalRequest) {
        AuthenticationResultSupplier supplier = this.getAuthenticationResultSupplier(msalRequest);
        ExecutorService executorService = this.serviceBundle.getExecutorService();
        return executorService != null ? CompletableFuture.supplyAsync(supplier, executorService) : CompletableFuture.supplyAsync(supplier);
    }

    AuthenticationResult acquireTokenCommon(MsalRequest msalRequest, Authority requestAuthority) throws Exception {
        HttpHeaders headers = msalRequest.headers();
        if (this.logPii) {
            this.log.debug(LogHelper.createMessage(String.format("Using Client Http Headers: %s", headers), headers.getHeaderCorrelationIdValue()));
        }
        TokenRequestExecutor requestExecutor = new TokenRequestExecutor(requestAuthority, msalRequest, this.serviceBundle);
        AuthenticationResult result = requestExecutor.executeTokenRequest();
        if (this.authenticationAuthority.authorityType.equals((Object)AuthorityType.AAD)) {
            InstanceDiscoveryMetadataEntry instanceDiscoveryMetadata = AadInstanceDiscoveryProvider.getMetadataEntry(requestAuthority.canonicalAuthorityUrl(), this.validateAuthority, msalRequest, this.serviceBundle);
            this.tokenCache.saveTokens(requestExecutor, result, instanceDiscoveryMetadata.preferredCache);
        } else {
            this.tokenCache.saveTokens(requestExecutor, result, this.authenticationAuthority.host);
        }
        return result;
    }

    private AuthenticationResultSupplier getAuthenticationResultSupplier(MsalRequest msalRequest) {
        AuthenticationResultSupplier supplier = msalRequest instanceof DeviceCodeFlowRequest ? new AcquireTokenByDeviceCodeFlowSupplier((PublicClientApplication)this, (DeviceCodeFlowRequest)msalRequest) : (msalRequest instanceof SilentRequest ? new AcquireTokenSilentSupplier(this, (SilentRequest)msalRequest) : (msalRequest instanceof InteractiveRequest ? new AcquireTokenByInteractiveFlowSupplier((PublicClientApplication)this, (InteractiveRequest)msalRequest) : (msalRequest instanceof ClientCredentialRequest ? new AcquireTokenByClientCredentialSupplier((ConfidentialClientApplication)this, (ClientCredentialRequest)msalRequest) : (msalRequest instanceof OnBehalfOfRequest ? new AcquireTokenByOnBehalfOfSupplier((ConfidentialClientApplication)this, (OnBehalfOfRequest)msalRequest) : new AcquireTokenByAuthorizationGrantSupplier(this, msalRequest, null)))));
        return supplier;
    }

    ServiceBundle getServiceBundle() {
        return this.serviceBundle;
    }

    AbstractClientApplicationBase(Builder<?> builder) {
        this.clientId = ((Builder)builder).clientId;
        this.authority = ((Builder)builder).authority;
        this.validateAuthority = ((Builder)builder).validateAuthority;
        this.correlationId = ((Builder)builder).correlationId;
        this.logPii = ((Builder)builder).logPii;
        this.applicationName = ((Builder)builder).applicationName;
        this.applicationVersion = ((Builder)builder).applicationVersion;
        this.telemetryConsumer = ((Builder)builder).telemetryConsumer;
        this.proxy = ((Builder)builder).proxy;
        this.sslSocketFactory = ((Builder)builder).sslSocketFactory;
        this.connectTimeoutForDefaultHttpClient = ((Builder)builder).connectTimeoutForDefaultHttpClient;
        this.readTimeoutForDefaultHttpClient = ((Builder)builder).readTimeoutForDefaultHttpClient;
        this.serviceBundle = new ServiceBundle(((Builder)builder).executorService, ((Builder)builder).httpClient == null ? new DefaultHttpClient(((Builder)builder).proxy, ((Builder)builder).sslSocketFactory, ((Builder)builder).connectTimeoutForDefaultHttpClient, ((Builder)builder).readTimeoutForDefaultHttpClient) : ((Builder)builder).httpClient, new TelemetryManager(this.telemetryConsumer, ((Builder)builder).onlySendFailureTelemetry));
        this.authenticationAuthority = ((Builder)builder).authenticationAuthority;
        this.tokenCache = new TokenCache(((Builder)builder).tokenCacheAccessAspect);
        this.aadAadInstanceDiscoveryResponse = ((Builder)builder).aadInstanceDiscoveryResponse;
        this.clientCapabilities = ((Builder)builder).clientCapabilities;
        this.autoDetectRegion = ((Builder)builder).autoDetectRegion;
        this.azureRegion = ((Builder)builder).azureRegion;
        this.instanceDiscovery = ((Builder)builder).instanceDiscovery;
        if (this.aadAadInstanceDiscoveryResponse != null) {
            AadInstanceDiscoveryProvider.cacheInstanceDiscoveryMetadata(this.authenticationAuthority.host, this.aadAadInstanceDiscoveryResponse);
        }
    }

    @Override
    public String clientId() {
        return this.clientId;
    }

    @Override
    public String authority() {
        return this.authority;
    }

    @Override
    public boolean validateAuthority() {
        return this.validateAuthority;
    }

    @Override
    public String correlationId() {
        return this.correlationId;
    }

    @Override
    public boolean logPii() {
        return this.logPii;
    }

    Consumer<List<HashMap<String, String>>> telemetryConsumer() {
        return this.telemetryConsumer;
    }

    @Override
    public Proxy proxy() {
        return this.proxy;
    }

    @Override
    public SSLSocketFactory sslSocketFactory() {
        return this.sslSocketFactory;
    }

    public Integer connectTimeoutForDefaultHttpClient() {
        return this.connectTimeoutForDefaultHttpClient;
    }

    public Integer readTimeoutForDefaultHttpClient() {
        return this.readTimeoutForDefaultHttpClient;
    }

    @Override
    public TokenCache tokenCache() {
        return this.tokenCache;
    }

    public String applicationName() {
        return this.applicationName;
    }

    public String applicationVersion() {
        return this.applicationVersion;
    }

    public AadInstanceDiscoveryResponse aadAadInstanceDiscoveryResponse() {
        return this.aadAadInstanceDiscoveryResponse;
    }

    public String clientCapabilities() {
        return this.clientCapabilities;
    }

    public boolean autoDetectRegion() {
        return this.autoDetectRegion;
    }

    public String azureRegion() {
        return this.azureRegion;
    }

    public boolean instanceDiscovery() {
        return this.instanceDiscovery;
    }

    public static abstract class Builder<T extends Builder<T>> {
        private String clientId;
        private String authority = "https://login.microsoftonline.com/common/";
        private Authority authenticationAuthority = Builder.createDefaultAADAuthority();
        private boolean validateAuthority = true;
        private String correlationId;
        private boolean logPii = false;
        private ExecutorService executorService;
        private Proxy proxy;
        private SSLSocketFactory sslSocketFactory;
        private IHttpClient httpClient;
        private Consumer<List<HashMap<String, String>>> telemetryConsumer;
        private Boolean onlySendFailureTelemetry = false;
        private String applicationName;
        private String applicationVersion;
        private ITokenCacheAccessAspect tokenCacheAccessAspect;
        private AadInstanceDiscoveryResponse aadInstanceDiscoveryResponse;
        private String clientCapabilities;
        private boolean autoDetectRegion;
        private String azureRegion;
        private Integer connectTimeoutForDefaultHttpClient;
        private Integer readTimeoutForDefaultHttpClient;
        private boolean instanceDiscovery = true;

        public Builder(String clientId) {
            ParameterValidationUtils.validateNotBlank("clientId", clientId);
            this.clientId = clientId;
        }

        abstract T self();

        public T authority(String val) throws MalformedURLException {
            this.authority = Authority.enforceTrailingSlash(val);
            URL authorityURL = new URL(this.authority);
            switch (Authority.detectAuthorityType(authorityURL)) {
                case AAD: {
                    this.authenticationAuthority = new AADAuthority(authorityURL);
                    break;
                }
                case ADFS: {
                    this.authenticationAuthority = new ADFSAuthority(authorityURL);
                    break;
                }
                case CIAM: {
                    this.authenticationAuthority = new CIAMAuthority(authorityURL);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unsupported authority type.");
                }
            }
            Authority.validateAuthority(this.authenticationAuthority.canonicalAuthorityUrl());
            return this.self();
        }

        public T b2cAuthority(String val) throws MalformedURLException {
            this.authority = Authority.enforceTrailingSlash(val);
            URL authorityURL = new URL(this.authority);
            Authority.validateAuthority(authorityURL);
            if (Authority.detectAuthorityType(authorityURL) != AuthorityType.B2C) {
                throw new IllegalArgumentException("Unsupported authority type. Please use B2C authority");
            }
            this.authenticationAuthority = new B2CAuthority(authorityURL);
            this.validateAuthority = false;
            return this.self();
        }

        public T validateAuthority(boolean val) {
            this.validateAuthority = val;
            return this.self();
        }

        public T correlationId(String val) {
            ParameterValidationUtils.validateNotBlank("correlationId", val);
            this.correlationId = val;
            return this.self();
        }

        public T logPii(boolean val) {
            this.logPii = val;
            return this.self();
        }

        public T executorService(ExecutorService val) {
            ParameterValidationUtils.validateNotNull("executorService", val);
            this.executorService = val;
            return this.self();
        }

        public T proxy(Proxy val) {
            ParameterValidationUtils.validateNotNull("proxy", val);
            this.proxy = val;
            return this.self();
        }

        public T httpClient(IHttpClient val) {
            ParameterValidationUtils.validateNotNull("httpClient", val);
            this.httpClient = val;
            return this.self();
        }

        public T sslSocketFactory(SSLSocketFactory val) {
            ParameterValidationUtils.validateNotNull("sslSocketFactory", val);
            this.sslSocketFactory = val;
            return this.self();
        }

        public T connectTimeoutForDefaultHttpClient(Integer val) {
            ParameterValidationUtils.validateNotNull("connectTimeoutForDefaultHttpClient", val);
            this.connectTimeoutForDefaultHttpClient = val;
            return this.self();
        }

        public T readTimeoutForDefaultHttpClient(Integer val) {
            ParameterValidationUtils.validateNotNull("readTimeoutForDefaultHttpClient", val);
            this.readTimeoutForDefaultHttpClient = val;
            return this.self();
        }

        T telemetryConsumer(Consumer<List<HashMap<String, String>>> val) {
            ParameterValidationUtils.validateNotNull("telemetryConsumer", val);
            this.telemetryConsumer = val;
            return this.self();
        }

        T onlySendFailureTelemetry(Boolean val) {
            this.onlySendFailureTelemetry = val;
            return this.self();
        }

        public T applicationName(String val) {
            ParameterValidationUtils.validateNotNull("applicationName", val);
            this.applicationName = val;
            return this.self();
        }

        public T applicationVersion(String val) {
            ParameterValidationUtils.validateNotNull("applicationVersion", val);
            this.applicationVersion = val;
            return this.self();
        }

        public T setTokenCacheAccessAspect(ITokenCacheAccessAspect val) {
            ParameterValidationUtils.validateNotNull("tokenCacheAccessAspect", val);
            this.tokenCacheAccessAspect = val;
            return this.self();
        }

        public T aadInstanceDiscoveryResponse(String val) {
            ParameterValidationUtils.validateNotNull("aadInstanceDiscoveryResponse", val);
            this.aadInstanceDiscoveryResponse = AadInstanceDiscoveryProvider.parseInstanceDiscoveryMetadata(val);
            return this.self();
        }

        private static Authority createDefaultAADAuthority() {
            AADAuthority authority;
            try {
                authority = new AADAuthority(new URL("https://login.microsoftonline.com/common/"));
            }
            catch (Exception e) {
                throw new MsalClientException(e);
            }
            return authority;
        }

        public T clientCapabilities(Set<String> capabilities) {
            this.clientCapabilities = JsonHelper.formCapabilitiesJson(capabilities);
            return this.self();
        }

        public T autoDetectRegion(boolean val) {
            this.autoDetectRegion = val;
            return this.self();
        }

        public T azureRegion(String val) {
            this.azureRegion = val;
            return this.self();
        }

        public T instanceDiscovery(boolean val) {
            this.instanceDiscovery = val;
            return this.self();
        }

        abstract AbstractClientApplicationBase build();
    }
}

