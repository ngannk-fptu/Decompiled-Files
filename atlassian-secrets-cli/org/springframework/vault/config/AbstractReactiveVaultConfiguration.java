/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.reactive.function.client.WebClient
 *  reactor.core.publisher.Mono
 */
package org.springframework.vault.config;

import java.time.Duration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.AuthenticationStepsFactory;
import org.springframework.vault.authentication.AuthenticationStepsOperator;
import org.springframework.vault.authentication.CachingVaultTokenSupplier;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.ReactiveLifecycleAwareSessionManager;
import org.springframework.vault.authentication.ReactiveSessionManager;
import org.springframework.vault.authentication.SessionManager;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.authentication.VaultTokenSupplier;
import org.springframework.vault.client.ClientHttpConnectorFactory;
import org.springframework.vault.client.ReactiveVaultClients;
import org.springframework.vault.client.ReactiveVaultEndpointProvider;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.vault.client.WebClientBuilder;
import org.springframework.vault.client.WebClientCustomizer;
import org.springframework.vault.client.WebClientFactory;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.config.DefaultWebClientFactory;
import org.springframework.vault.core.ReactiveVaultTemplate;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration(proxyBeanMethods=false)
public abstract class AbstractReactiveVaultConfiguration
extends AbstractVaultConfiguration {
    public ReactiveVaultEndpointProvider reactiveVaultEndpointProvider() {
        return ReactiveVaultClients.wrap(this.vaultEndpointProvider());
    }

    protected WebClientBuilder webClientBuilder(VaultEndpointProvider endpointProvider, ClientHttpConnector httpConnector) {
        return this.webClientBuilder(ReactiveVaultClients.wrap(endpointProvider), httpConnector);
    }

    protected WebClientBuilder webClientBuilder(ReactiveVaultEndpointProvider endpointProvider, ClientHttpConnector httpConnector) {
        ObjectProvider<WebClientCustomizer> customizers = this.getBeanFactory().getBeanProvider(WebClientCustomizer.class);
        WebClientBuilder builder = WebClientBuilder.builder().endpointProvider(endpointProvider).httpConnector(httpConnector);
        builder.customizers((WebClientCustomizer[])customizers.stream().toArray(WebClientCustomizer[]::new));
        return builder;
    }

    @Bean
    public WebClientFactory webClientFactory() {
        ClientHttpConnector httpConnector = this.clientHttpConnector();
        return new DefaultWebClientFactory(httpConnector, clientHttpConnector -> this.webClientBuilder(this.reactiveVaultEndpointProvider(), (ClientHttpConnector)clientHttpConnector));
    }

    @Bean
    public ReactiveVaultTemplate reactiveVaultTemplate() {
        return new ReactiveVaultTemplate(this.webClientBuilder(this.reactiveVaultEndpointProvider(), this.clientHttpConnector()), this.getReactiveSessionManager());
    }

    @Override
    @Bean
    public SessionManager sessionManager() {
        return new ReactiveSessionManagerAdapter(this.getReactiveSessionManager());
    }

    @Bean
    public ReactiveSessionManager reactiveSessionManager() {
        WebClient webClient = this.getWebClientFactory().create();
        return new ReactiveLifecycleAwareSessionManager(this.vaultTokenSupplier(), this.getVaultThreadPoolTaskScheduler(), webClient);
    }

    protected VaultTokenSupplier vaultTokenSupplier() {
        ClientAuthentication clientAuthentication = this.clientAuthentication();
        Assert.notNull((Object)clientAuthentication, "ClientAuthentication must not be null");
        if (clientAuthentication instanceof TokenAuthentication) {
            TokenAuthentication authentication = (TokenAuthentication)clientAuthentication;
            return () -> Mono.just((Object)authentication.login());
        }
        if (clientAuthentication instanceof AuthenticationStepsFactory) {
            AuthenticationStepsFactory factory = (AuthenticationStepsFactory)((Object)clientAuthentication);
            WebClient webClient = this.getWebClientFactory().create();
            AuthenticationStepsOperator stepsOperator = new AuthenticationStepsOperator(factory.getAuthenticationSteps(), webClient);
            return CachingVaultTokenSupplier.of(stepsOperator);
        }
        throw new IllegalStateException(String.format("Cannot construct VaultTokenSupplier from %s. ClientAuthentication must implement AuthenticationStepsFactory or be TokenAuthentication", clientAuthentication));
    }

    protected ClientHttpConnector clientHttpConnector() {
        return ClientHttpConnectorFactory.create(this.clientOptions(), this.sslConfiguration());
    }

    protected WebClientFactory getWebClientFactory() {
        return this.getBeanFactory().getBean(WebClientFactory.class);
    }

    private ReactiveSessionManager getReactiveSessionManager() {
        return this.getBeanFactory().getBean("reactiveSessionManager", ReactiveSessionManager.class);
    }

    static class ReactiveSessionManagerAdapter
    implements SessionManager {
        private final ReactiveSessionManager sessionManager;

        public ReactiveSessionManagerAdapter(ReactiveSessionManager sessionManager) {
            this.sessionManager = sessionManager;
        }

        @Override
        public VaultToken getSessionToken() {
            return (VaultToken)this.sessionManager.getSessionToken().block(Duration.ofSeconds(30L));
        }
    }
}

