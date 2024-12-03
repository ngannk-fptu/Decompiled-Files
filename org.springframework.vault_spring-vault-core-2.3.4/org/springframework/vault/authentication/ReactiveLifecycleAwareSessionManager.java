/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.scheduling.Trigger
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.reactive.function.client.WebClient
 *  org.springframework.web.reactive.function.client.WebClient$RequestBodySpec
 *  org.springframework.web.reactive.function.client.WebClientResponseException
 *  reactor.core.publisher.Mono
 */
package org.springframework.vault.authentication;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.LifecycleAwareSessionManagerSupport;
import org.springframework.vault.authentication.LoginToken;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.ReactiveSessionManager;
import org.springframework.vault.authentication.VaultTokenLookupException;
import org.springframework.vault.authentication.VaultTokenRenewalException;
import org.springframework.vault.authentication.VaultTokenSupplier;
import org.springframework.vault.authentication.event.AfterLoginEvent;
import org.springframework.vault.authentication.event.AfterLoginTokenRenewedEvent;
import org.springframework.vault.authentication.event.AfterLoginTokenRevocationEvent;
import org.springframework.vault.authentication.event.AuthenticationErrorEvent;
import org.springframework.vault.authentication.event.BeforeLoginTokenRenewedEvent;
import org.springframework.vault.authentication.event.BeforeLoginTokenRevocationEvent;
import org.springframework.vault.authentication.event.LoginFailedEvent;
import org.springframework.vault.authentication.event.LoginTokenExpiredEvent;
import org.springframework.vault.authentication.event.LoginTokenRenewalFailedEvent;
import org.springframework.vault.authentication.event.LoginTokenRevocationFailedEvent;
import org.springframework.vault.client.VaultHttpHeaders;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class ReactiveLifecycleAwareSessionManager
extends LifecycleAwareSessionManagerSupport
implements ReactiveSessionManager,
DisposableBean {
    private static final Mono<TokenWrapper> EMPTY = Mono.empty();
    private static final Mono<TokenWrapper> TERMINATED = Mono.error((Throwable)new TerminatedException());
    private final VaultTokenSupplier clientAuthentication;
    private final WebClient webClient;
    private volatile AtomicReference<Mono<TokenWrapper>> token = new AtomicReference<Mono<TokenWrapper>>(EMPTY);

    public ReactiveLifecycleAwareSessionManager(VaultTokenSupplier clientAuthentication, TaskScheduler taskScheduler, WebClient webClient) {
        super(taskScheduler);
        Assert.notNull((Object)clientAuthentication, (String)"VaultTokenSupplier must not be null");
        Assert.notNull((Object)taskScheduler, (String)"TaskScheduler must not be null");
        Assert.notNull((Object)webClient, (String)"RestOperations must not be null");
        this.clientAuthentication = clientAuthentication;
        this.webClient = webClient;
    }

    public ReactiveLifecycleAwareSessionManager(VaultTokenSupplier clientAuthentication, TaskScheduler taskScheduler, WebClient webClient, LifecycleAwareSessionManagerSupport.RefreshTrigger refreshTrigger) {
        super(taskScheduler, refreshTrigger);
        Assert.notNull((Object)clientAuthentication, (String)"VaultTokenSupplier must not be null");
        Assert.notNull((Object)taskScheduler, (String)"TaskScheduler must not be null");
        Assert.notNull((Object)webClient, (String)"WebClient must not be null");
        Assert.notNull((Object)refreshTrigger, (String)"RefreshTrigger must not be null");
        this.clientAuthentication = clientAuthentication;
        this.webClient = webClient;
    }

    public void destroy() {
        Mono<TokenWrapper> tokenMono = this.token.get();
        this.token.set(TERMINATED);
        this.revokeNow(tokenMono);
    }

    protected void revokeNow(Mono<TokenWrapper> tokenMono) {
        this.doRevoke(tokenMono).block(Duration.ofSeconds(5L));
    }

    protected Mono<Void> doRevoke(Mono<TokenWrapper> tokenMono) {
        return tokenMono.filter(TokenWrapper::isRevocable).map(TokenWrapper::getToken).flatMap(this::revoke);
    }

    protected Mono<Void> revoke(VaultToken token) {
        return ((WebClient.RequestBodySpec)((WebClient.RequestBodySpec)this.webClient.post().uri("auth/token/revoke-self", new Object[0])).headers(httpHeaders -> httpHeaders.addAll((MultiValueMap)VaultHttpHeaders.from(token)))).retrieve().bodyToMono(String.class).doOnSubscribe(ignore -> this.dispatch(new BeforeLoginTokenRevocationEvent(token))).doOnNext(ignore -> this.dispatch(new AfterLoginTokenRevocationEvent(token))).onErrorResume(WebClientResponseException.class, e -> this.onRevokeFailed(token, (Throwable)e)).onErrorResume(Exception.class, e -> this.onRevokeFailed(token, (Throwable)e)).then();
    }

    private Mono<String> onRevokeFailed(VaultToken token, Throwable e) {
        if (LoginToken.hasAccessor(token)) {
            this.logger.warn((Object)String.format("Cannot revoke VaultToken with accessor: %s", ((LoginToken)token).getAccessor()), e);
        } else {
            this.logger.warn((Object)"Cannot revoke VaultToken", e);
        }
        this.dispatch(new LoginTokenRevocationFailedEvent(token, e));
        return Mono.empty();
    }

    public Mono<VaultToken> renewToken() {
        this.logger.info((Object)"Renewing token");
        Mono<TokenWrapper> tokenWrapper = this.token.get();
        if (tokenWrapper == TERMINATED) {
            return tokenWrapper.map(TokenWrapper::getToken);
        }
        if (tokenWrapper == EMPTY) {
            return this.getVaultToken();
        }
        return tokenWrapper.flatMap(this::doRenewToken).map(TokenWrapper::getToken);
    }

    private Mono<TokenWrapper> doRenewToken(TokenWrapper wrapper) {
        return this.doRenew(wrapper).onErrorResume(RuntimeException.class, e -> {
            VaultTokenRenewalException exception = new VaultTokenRenewalException(ReactiveLifecycleAwareSessionManager.format("Cannot renew token", e), (Throwable)e);
            boolean shouldDrop = this.getLeaseStrategy().shouldDrop((Throwable)((Object)exception));
            if (shouldDrop) {
                this.dropCurrentToken();
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)exception.getMessage(), (Throwable)((Object)exception));
            } else {
                this.logger.warn((Object)exception.getMessage());
            }
            this.dispatch(new LoginTokenRenewalFailedEvent(wrapper.getToken(), (Throwable)((Object)exception)));
            return shouldDrop ? EMPTY : Mono.just((Object)wrapper);
        });
    }

    private Mono<TokenWrapper> doRenew(TokenWrapper tokenWrapper) {
        Mono exchange = ((WebClient.RequestBodySpec)((WebClient.RequestBodySpec)this.webClient.post().uri("auth/token/renew-self", new Object[0])).headers(httpHeaders -> httpHeaders.putAll((Map)VaultHttpHeaders.from(tokenWrapper.token)))).retrieve().bodyToMono(VaultResponse.class);
        return exchange.doOnSubscribe(ignore -> this.dispatch(new BeforeLoginTokenRenewedEvent(tokenWrapper.getToken()))).handle((response, sink) -> {
            LoginToken renewed = LoginTokenUtil.from(response.getRequiredAuth());
            if (!this.isExpired(renewed)) {
                sink.next((Object)new TokenWrapper(renewed, tokenWrapper.revocable));
                this.dispatch(new AfterLoginTokenRenewedEvent(renewed));
                return;
            }
            if (this.logger.isDebugEnabled()) {
                Duration validTtlThreshold = this.getRefreshTrigger().getValidTtlThreshold(renewed);
                this.logger.info((Object)String.format("Token TTL (%s) exceeded validity TTL threshold (%s). Dropping token.", renewed.getLeaseDuration(), validTtlThreshold));
            } else {
                this.logger.info((Object)"Token TTL exceeded validity TTL threshold. Dropping token.");
            }
            this.dropCurrentToken();
            this.dispatch(new LoginTokenExpiredEvent(renewed));
        });
    }

    private void dropCurrentToken() {
        Mono<TokenWrapper> tokenWrapper = this.token.get();
        if (tokenWrapper != TERMINATED) {
            this.token.compareAndSet(tokenWrapper, EMPTY);
        }
    }

    @Override
    public Mono<VaultToken> getVaultToken() throws VaultException {
        Mono<TokenWrapper> tokenWrapper = this.token.get();
        if (tokenWrapper == EMPTY) {
            Mono obtainToken = this.clientAuthentication.getVaultToken().flatMap(this::doSelfLookup).onErrorMap(it -> {
                this.dispatch(new LoginFailedEvent(this.clientAuthentication, (Throwable)it));
                return it;
            }).doOnNext(it -> {
                if (this.isTokenRenewable(it.getToken())) {
                    this.scheduleRenewal(it.getToken());
                }
                this.dispatch(new AfterLoginEvent(it.getToken()));
            });
            this.token.compareAndSet(tokenWrapper, (Mono<TokenWrapper>)obtainToken.cache());
        }
        return this.token.get().map(TokenWrapper::getToken);
    }

    private Mono<TokenWrapper> doSelfLookup(VaultToken token) {
        TokenWrapper wrapper = new TokenWrapper(token, token instanceof LoginToken);
        if (this.isTokenSelfLookupEnabled() && !ClassUtils.isAssignableValue(LoginToken.class, (Object)token)) {
            Mono<VaultToken> loginTokenMono = ReactiveLifecycleAwareSessionManager.augmentWithSelfLookup(this.webClient, token);
            return loginTokenMono.onErrorResume(e -> {
                this.logger.warn((Object)String.format("Cannot enhance VaultToken to a LoginToken: %s", e.getMessage()));
                this.dispatch(new AuthenticationErrorEvent(token, (Throwable)e));
                return Mono.just((Object)token);
            }).map(it -> new TokenWrapper((VaultToken)it, false));
        }
        return Mono.just((Object)wrapper);
    }

    protected boolean isTokenRenewable(VaultToken token) {
        return Optional.of(token).filter(LoginToken.class::isInstance).filter(it -> {
            LoginToken loginToken = (LoginToken)it;
            return !loginToken.getLeaseDuration().isZero() && loginToken.isRenewable();
        }).isPresent();
    }

    private void scheduleRenewal(VaultToken token) {
        this.logger.info((Object)"Scheduling Token renewal");
        Runnable task = () -> {
            try {
                Mono<TokenWrapper> tokenWrapper = this.token.get();
                if (tokenWrapper == Mono.empty() || tokenWrapper == TERMINATED) {
                    return;
                }
                if (this.isTokenRenewable(token)) {
                    this.renewToken().subscribe(this::scheduleRenewal, e -> {
                        this.logger.error((Object)"Cannot renew VaultToken", e);
                        this.dispatch(new LoginTokenRenewalFailedEvent(token, (Throwable)e));
                    });
                }
            }
            catch (Exception e2) {
                this.logger.error((Object)"Cannot renew VaultToken", (Throwable)e2);
                this.dispatch(new LoginTokenRenewalFailedEvent(token, (Throwable)e2));
            }
        };
        this.getTaskScheduler().schedule(task, (Trigger)this.createTrigger(token));
    }

    private LifecycleAwareSessionManagerSupport.OneShotTrigger createTrigger(VaultToken token) {
        return new LifecycleAwareSessionManagerSupport.OneShotTrigger(this.getRefreshTrigger().nextExecutionTime((LoginToken)token));
    }

    private static Mono<VaultToken> augmentWithSelfLookup(WebClient webClient, VaultToken token) {
        Mono<Map<String, Object>> data = ReactiveLifecycleAwareSessionManager.lookupSelf(webClient, token);
        return data.map(it -> LoginTokenUtil.from(token.toCharArray(), it));
    }

    private static Mono<Map<String, Object>> lookupSelf(WebClient webClient, VaultToken token) {
        return webClient.get().uri("auth/token/lookup-self", new Object[0]).headers(httpHeaders -> httpHeaders.putAll((Map)VaultHttpHeaders.from(token))).retrieve().bodyToMono(VaultResponse.class).map(it -> {
            Assert.state((it.getData() != null ? 1 : 0) != 0, (String)"Token response is null");
            return (Map)it.getRequiredData();
        }).onErrorMap(WebClientResponseException.class, e -> new VaultTokenLookupException(ReactiveLifecycleAwareSessionManager.format("Token self-lookup", (RuntimeException)e), (Throwable)e));
    }

    private static String format(String message, RuntimeException e) {
        if (e instanceof WebClientResponseException) {
            WebClientResponseException wce = (WebClientResponseException)e;
            return String.format("%s: Status %s %s %s", message, wce.getRawStatusCode(), wce.getStatusText(), VaultResponses.getError(wce.getResponseBodyAsString()));
        }
        return message;
    }

    static class TerminatedException
    extends IllegalStateException {
        TerminatedException() {
            super("Session manager terminated");
            this.setStackTrace(new StackTraceElement[0]);
        }
    }

    protected static class TokenWrapper {
        private final VaultToken token;
        private final boolean revocable;

        public TokenWrapper(VaultToken token, boolean revocable) {
            this.token = token;
            this.revocable = revocable;
        }

        public VaultToken getToken() {
            return this.token;
        }

        public boolean isRevocable() {
            if (this.token instanceof LoginToken && ((LoginToken)this.token).isServiceToken()) {
                return this.revocable;
            }
            return false;
        }
    }
}

