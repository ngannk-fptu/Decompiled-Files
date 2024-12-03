/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.SessionManager;
import org.springframework.vault.authentication.SimpleSessionManager;
import org.springframework.vault.client.RestTemplateBuilder;
import org.springframework.vault.client.SimpleVaultEndpointProvider;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.core.RestOperationsCallback;
import org.springframework.vault.core.VaultKeyValue1Template;
import org.springframework.vault.core.VaultKeyValue2Template;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultListResponse;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultPkiOperations;
import org.springframework.vault.core.VaultPkiTemplate;
import org.springframework.vault.core.VaultSysOperations;
import org.springframework.vault.core.VaultSysTemplate;
import org.springframework.vault.core.VaultTokenOperations;
import org.springframework.vault.core.VaultTokenTemplate;
import org.springframework.vault.core.VaultTransformOperations;
import org.springframework.vault.core.VaultTransformTemplate;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.core.VaultTransitTemplate;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.core.VaultVersionedKeyValueTemplate;
import org.springframework.vault.core.VaultWrappingOperations;
import org.springframework.vault.core.VaultWrappingTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class VaultTemplate
implements InitializingBean,
VaultOperations,
DisposableBean {
    private final RestTemplate statelessTemplate;
    private final RestTemplate sessionTemplate;
    @Nullable
    private SessionManager sessionManager;
    private final boolean dedicatedSessionManager;

    public VaultTemplate(VaultEndpoint vaultEndpoint) {
        this(SimpleVaultEndpointProvider.of(vaultEndpoint), (ClientHttpRequestFactory)new SimpleClientHttpRequestFactory());
    }

    public VaultTemplate(VaultEndpoint vaultEndpoint, ClientAuthentication clientAuthentication) {
        Assert.notNull((Object)vaultEndpoint, "VaultEndpoint must not be null");
        Assert.notNull((Object)clientAuthentication, "ClientAuthentication must not be null");
        this.sessionManager = new SimpleSessionManager(clientAuthentication);
        this.dedicatedSessionManager = true;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        VaultEndpointProvider endpointProvider = SimpleVaultEndpointProvider.of(vaultEndpoint);
        this.statelessTemplate = this.doCreateRestTemplate(endpointProvider, requestFactory);
        this.sessionTemplate = this.doCreateSessionTemplate(endpointProvider, requestFactory);
    }

    public VaultTemplate(VaultEndpoint vaultEndpoint, ClientHttpRequestFactory clientHttpRequestFactory) {
        this(SimpleVaultEndpointProvider.of(vaultEndpoint), clientHttpRequestFactory);
    }

    public VaultTemplate(VaultEndpoint vaultEndpoint, ClientHttpRequestFactory clientHttpRequestFactory, SessionManager sessionManager) {
        this(SimpleVaultEndpointProvider.of(vaultEndpoint), clientHttpRequestFactory, sessionManager);
    }

    public VaultTemplate(VaultEndpointProvider endpointProvider, ClientHttpRequestFactory requestFactory) {
        Assert.notNull((Object)endpointProvider, "VaultEndpointProvider must not be null");
        Assert.notNull((Object)requestFactory, "ClientHttpRequestFactory must not be null");
        RestTemplate restTemplate = this.doCreateRestTemplate(endpointProvider, requestFactory);
        this.sessionManager = NoSessionManager.INSTANCE;
        this.dedicatedSessionManager = false;
        this.statelessTemplate = restTemplate;
        this.sessionTemplate = restTemplate;
    }

    public VaultTemplate(VaultEndpointProvider endpointProvider, ClientHttpRequestFactory requestFactory, SessionManager sessionManager) {
        Assert.notNull((Object)endpointProvider, "VaultEndpointProvider must not be null");
        Assert.notNull((Object)requestFactory, "ClientHttpRequestFactory must not be null");
        Assert.notNull((Object)sessionManager, "SessionManager must not be null");
        this.sessionManager = sessionManager;
        this.dedicatedSessionManager = false;
        this.statelessTemplate = this.doCreateRestTemplate(endpointProvider, requestFactory);
        this.sessionTemplate = this.doCreateSessionTemplate(endpointProvider, requestFactory);
    }

    public VaultTemplate(RestTemplateBuilder restTemplateBuilder) {
        Assert.notNull((Object)restTemplateBuilder, "RestTemplateBuilder must not be null");
        RestTemplate restTemplate = restTemplateBuilder.build();
        this.sessionManager = NoSessionManager.INSTANCE;
        this.dedicatedSessionManager = false;
        this.statelessTemplate = restTemplate;
        this.sessionTemplate = restTemplate;
    }

    public VaultTemplate(RestTemplateBuilder restTemplateBuilder, SessionManager sessionManager) {
        Assert.notNull((Object)restTemplateBuilder, "RestTemplateBuilder must not be null");
        Assert.notNull((Object)sessionManager, "SessionManager must not be null");
        this.sessionManager = sessionManager;
        this.dedicatedSessionManager = false;
        this.statelessTemplate = restTemplateBuilder.build();
        this.sessionTemplate = restTemplateBuilder.build();
        this.sessionTemplate.getInterceptors().add(this.getSessionInterceptor());
    }

    protected RestTemplate doCreateRestTemplate(VaultEndpointProvider endpointProvider, ClientHttpRequestFactory requestFactory) {
        return RestTemplateBuilder.builder().endpointProvider(endpointProvider).requestFactory(requestFactory).build();
    }

    protected RestTemplate doCreateSessionTemplate(VaultEndpointProvider endpointProvider, ClientHttpRequestFactory requestFactory) {
        return RestTemplateBuilder.builder().endpointProvider(endpointProvider).requestFactory(requestFactory).customizers(restTemplate -> restTemplate.getInterceptors().add(this.getSessionInterceptor())).build();
    }

    private ClientHttpRequestInterceptor getSessionInterceptor() {
        return (request, body, execution) -> {
            Assert.notNull((Object)this.sessionManager, "SessionManager must not be null");
            request.getHeaders().add("X-Vault-Token", this.sessionManager.getSessionToken().getToken());
            return execution.execute(request, body);
        };
    }

    public void setSessionManager(SessionManager sessionManager) {
        Assert.notNull((Object)sessionManager, "SessionManager must not be null");
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull((Object)this.sessionManager, "SessionManager must not be null");
    }

    @Override
    public void destroy() throws Exception {
        if (this.dedicatedSessionManager && this.sessionManager instanceof DisposableBean) {
            ((DisposableBean)((Object)this.sessionManager)).destroy();
        }
    }

    @Override
    public VaultKeyValueOperations opsForKeyValue(String path, VaultKeyValueOperationsSupport.KeyValueBackend apiVersion) {
        switch (apiVersion) {
            case KV_1: {
                return new VaultKeyValue1Template(this, path);
            }
            case KV_2: {
                return new VaultKeyValue2Template(this, path);
            }
        }
        throw new UnsupportedOperationException(String.format("Key/Value backend version %s not supported", new Object[]{apiVersion}));
    }

    @Override
    public VaultVersionedKeyValueOperations opsForVersionedKeyValue(String path) {
        return new VaultVersionedKeyValueTemplate(this, path);
    }

    @Override
    public VaultPkiOperations opsForPki() {
        return this.opsForPki("pki");
    }

    @Override
    public VaultPkiOperations opsForPki(String path) {
        return new VaultPkiTemplate(this, path);
    }

    @Override
    public VaultSysOperations opsForSys() {
        return new VaultSysTemplate(this);
    }

    @Override
    public VaultTokenOperations opsForToken() {
        return new VaultTokenTemplate(this);
    }

    @Override
    public VaultTransformOperations opsForTransform() {
        return this.opsForTransform("transform");
    }

    @Override
    public VaultTransformOperations opsForTransform(String path) {
        return new VaultTransformTemplate(this, path);
    }

    @Override
    public VaultTransitOperations opsForTransit() {
        return this.opsForTransit("transit");
    }

    @Override
    public VaultTransitOperations opsForTransit(String path) {
        return new VaultTransitTemplate(this, path);
    }

    @Override
    public VaultWrappingOperations opsForWrapping() {
        return new VaultWrappingTemplate(this);
    }

    @Override
    public VaultResponse read(String path) {
        Assert.hasText(path, "Path must not be empty");
        return this.doRead(path, VaultResponse.class);
    }

    @Override
    @Nullable
    public <T> VaultResponseSupport<T> read(String path, Class<T> responseType) {
        ParameterizedTypeReference ref = VaultResponses.getTypeReference(responseType);
        return this.doWithSession(restOperations -> {
            try {
                ResponseEntity exchange2 = restOperations.exchange(path, HttpMethod.GET, null, ref, new Object[0]);
                return (VaultResponseSupport)exchange2.getBody();
            }
            catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return null;
                }
                throw VaultResponses.buildException(e, path);
            }
        });
    }

    @Override
    @Nullable
    public List<String> list(String path) {
        Assert.hasText(path, "Path must not be empty");
        VaultListResponse read = this.doRead(String.format("%s?list=true", path.endsWith("/") ? path : path + "/"), VaultListResponse.class);
        if (read == null) {
            return Collections.emptyList();
        }
        return (List)((Map)read.getRequiredData()).get("keys");
    }

    @Override
    @Nullable
    public VaultResponse write(String path, @Nullable Object body) {
        Assert.hasText(path, "Path must not be empty");
        return this.doWithSession(restOperations -> restOperations.postForObject(path, body, VaultResponse.class, new Object[0]));
    }

    @Override
    public void delete(String path) {
        Assert.hasText(path, "Path must not be empty");
        this.doWithSession(restOperations -> {
            try {
                restOperations.delete(path, new Object[0]);
            }
            catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return null;
                }
                throw VaultResponses.buildException(e, path);
            }
            return null;
        });
    }

    @Override
    public <T> T doWithVault(RestOperationsCallback<T> clientCallback) {
        Assert.notNull(clientCallback, "Client callback must not be null");
        try {
            return clientCallback.doWithRestOperations(this.statelessTemplate);
        }
        catch (HttpStatusCodeException e) {
            throw VaultResponses.buildException(e);
        }
    }

    @Override
    public <T> T doWithSession(RestOperationsCallback<T> sessionCallback) {
        Assert.notNull(sessionCallback, "Session callback must not be null");
        try {
            return sessionCallback.doWithRestOperations(this.sessionTemplate);
        }
        catch (HttpStatusCodeException e) {
            throw VaultResponses.buildException(e);
        }
    }

    @Nullable
    private <T> T doRead(String path, Class<T> responseType) {
        return (T)this.doWithSession(restOperations -> {
            try {
                return restOperations.getForObject(path, responseType, new Object[0]);
            }
            catch (HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return null;
                }
                throw VaultResponses.buildException(e, path);
            }
        });
    }

    private static enum NoSessionManager implements SessionManager
    {
        INSTANCE;


        @Override
        public VaultToken getSessionToken() {
            throw new UnsupportedOperationException();
        }
    }
}

