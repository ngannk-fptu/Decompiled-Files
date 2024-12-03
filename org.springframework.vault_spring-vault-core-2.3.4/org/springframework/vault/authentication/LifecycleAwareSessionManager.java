/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.http.HttpEntity
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.scheduling.Trigger
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.HttpStatusCodeException
 *  org.springframework.web.client.RestOperations
 */
package org.springframework.vault.authentication;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.LifecycleAwareSessionManagerSupport;
import org.springframework.vault.authentication.LoginToken;
import org.springframework.vault.authentication.LoginTokenAdapter;
import org.springframework.vault.authentication.LoginTokenUtil;
import org.springframework.vault.authentication.SessionManager;
import org.springframework.vault.authentication.VaultTokenLookupException;
import org.springframework.vault.authentication.VaultTokenRenewalException;
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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

public class LifecycleAwareSessionManager
extends LifecycleAwareSessionManagerSupport
implements SessionManager,
DisposableBean {
    private final ClientAuthentication clientAuthentication;
    private final RestOperations restOperations;
    private final Object lock = new Object();
    private volatile Optional<TokenWrapper> token = Optional.empty();

    public LifecycleAwareSessionManager(ClientAuthentication clientAuthentication, TaskScheduler taskScheduler, RestOperations restOperations) {
        super(taskScheduler);
        Assert.notNull((Object)clientAuthentication, (String)"ClientAuthentication must not be null");
        Assert.notNull((Object)taskScheduler, (String)"TaskScheduler must not be null");
        Assert.notNull((Object)restOperations, (String)"RestOperations must not be null");
        this.clientAuthentication = clientAuthentication;
        this.restOperations = restOperations;
    }

    public LifecycleAwareSessionManager(ClientAuthentication clientAuthentication, TaskScheduler taskScheduler, RestOperations restOperations, LifecycleAwareSessionManagerSupport.RefreshTrigger refreshTrigger) {
        super(taskScheduler, refreshTrigger);
        Assert.notNull((Object)clientAuthentication, (String)"ClientAuthentication must not be null");
        Assert.notNull((Object)taskScheduler, (String)"TaskScheduler must not be null");
        Assert.notNull((Object)restOperations, (String)"RestOperations must not be null");
        Assert.notNull((Object)refreshTrigger, (String)"RefreshTrigger must not be null");
        this.clientAuthentication = clientAuthentication;
        this.restOperations = restOperations;
    }

    protected Optional<TokenWrapper> getToken() {
        return this.token;
    }

    protected void setToken(Optional<TokenWrapper> token) {
        this.token = token;
    }

    public void destroy() {
        Optional<TokenWrapper> token = this.getToken();
        this.setToken(Optional.empty());
        token.filter(TokenWrapper::isRevocable).map(TokenWrapper::getToken).ifPresent(this::revoke);
    }

    protected void revoke(VaultToken token) {
        try {
            this.dispatch(new BeforeLoginTokenRevocationEvent(token));
            this.restOperations.postForObject("auth/token/revoke-self", (Object)new HttpEntity((MultiValueMap)VaultHttpHeaders.from(token)), Map.class, new Object[0]);
            this.dispatch(new AfterLoginTokenRevocationEvent(token));
        }
        catch (RuntimeException e) {
            if (LoginToken.hasAccessor(token)) {
                this.logger.warn((Object)String.format("Cannot revoke VaultToken with accessor: %s", ((LoginToken)token).getAccessor()), (Throwable)e);
            } else {
                this.logger.warn((Object)"Cannot revoke VaultToken", (Throwable)e);
            }
            this.dispatch(new LoginTokenRevocationFailedEvent(token, (Throwable)e));
        }
    }

    public boolean renewToken() {
        return this.tryRenewToken().successful;
    }

    private RenewOutcome tryRenewToken() {
        this.logger.info((Object)"Renewing token");
        Optional<TokenWrapper> token = this.getToken();
        if (!token.isPresent()) {
            this.getSessionToken();
            return RenewOutcome.TERMINAL_ERROR;
        }
        TokenWrapper tokenWrapper = token.get();
        try {
            return this.doRenew(tokenWrapper);
        }
        catch (RuntimeException e) {
            VaultTokenRenewalException exception = new VaultTokenRenewalException(LifecycleAwareSessionManager.format("Cannot renew token", e), e);
            boolean shouldDrop = this.getLeaseStrategy().shouldDrop((Throwable)((Object)exception));
            if (shouldDrop) {
                this.setToken(Optional.empty());
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)exception.getMessage(), (Throwable)((Object)exception));
            } else {
                this.logger.warn((Object)exception.getMessage());
            }
            this.dispatch(new LoginTokenRenewalFailedEvent(tokenWrapper.getToken(), (Throwable)((Object)exception)));
            return shouldDrop ? RenewOutcome.TERMINAL_ERROR : RenewOutcome.RENEWABLE_ERROR;
        }
    }

    private RenewOutcome doRenew(TokenWrapper wrapper) {
        this.dispatch(new BeforeLoginTokenRenewedEvent(wrapper.getToken()));
        VaultResponse vaultResponse = (VaultResponse)this.restOperations.postForObject("auth/token/renew-self", (Object)new HttpEntity((MultiValueMap)VaultHttpHeaders.from(wrapper.token)), VaultResponse.class, new Object[0]);
        LoginToken renewed = LoginTokenUtil.from(vaultResponse.getRequiredAuth());
        if (this.isExpired(renewed)) {
            if (this.logger.isDebugEnabled()) {
                Duration validTtlThreshold = this.getRefreshTrigger().getValidTtlThreshold(renewed);
                this.logger.info((Object)String.format("Token TTL (%s) exceeded validity TTL threshold (%s). Dropping token.", renewed.getLeaseDuration(), validTtlThreshold));
            } else {
                this.logger.info((Object)"Token TTL exceeded validity TTL threshold. Dropping token.");
            }
            this.setToken(Optional.empty());
            this.dispatch(new LoginTokenExpiredEvent(renewed));
            return RenewOutcome.TERMINAL_ERROR;
        }
        this.setToken(Optional.of(new TokenWrapper(renewed, wrapper.revocable)));
        this.dispatch(new AfterLoginTokenRenewedEvent(renewed));
        return RenewOutcome.SUCCESS;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public VaultToken getSessionToken() {
        if (!this.getToken().isPresent()) {
            Object object = this.lock;
            synchronized (object) {
                if (!this.getToken().isPresent()) {
                    this.doGetSessionToken();
                }
            }
        }
        return this.getToken().map(TokenWrapper::getToken).orElseThrow(() -> new IllegalStateException("Cannot obtain VaultToken"));
    }

    private void doGetSessionToken() {
        VaultToken token;
        try {
            token = this.clientAuthentication.login();
        }
        catch (VaultException e) {
            this.dispatch(new LoginFailedEvent(this.clientAuthentication, (Throwable)((Object)e)));
            throw e;
        }
        TokenWrapper wrapper = new TokenWrapper(token, token instanceof LoginToken);
        if (this.isTokenSelfLookupEnabled() && !ClassUtils.isAssignableValue(LoginToken.class, (Object)token)) {
            try {
                token = LoginTokenAdapter.augmentWithSelfLookup(this.restOperations, token);
                wrapper = new TokenWrapper(token, false);
            }
            catch (VaultTokenLookupException e) {
                this.logger.warn((Object)String.format("Cannot enhance VaultToken to a LoginToken: %s", e.getMessage()));
                this.dispatch(new AuthenticationErrorEvent(token, (Throwable)((Object)e)));
            }
        }
        this.setToken(Optional.of(wrapper));
        this.dispatch(new AfterLoginEvent(token));
        if (this.isTokenRenewable()) {
            this.scheduleRenewal();
        }
    }

    protected VaultToken login() {
        return this.clientAuthentication.login();
    }

    protected boolean isTokenRenewable() {
        return this.getToken().map(TokenWrapper::getToken).filter(LoginToken.class::isInstance).filter(it -> {
            LoginToken loginToken = (LoginToken)it;
            return !loginToken.getLeaseDuration().isZero() && loginToken.isRenewable();
        }).isPresent();
    }

    private void scheduleRenewal() {
        this.logger.info((Object)"Scheduling Token renewal");
        Runnable task = () -> {
            Optional<TokenWrapper> tokenWrapper = this.getToken();
            if (!tokenWrapper.isPresent()) {
                return;
            }
            VaultToken token = tokenWrapper.get().getToken();
            try {
                RenewOutcome result;
                if (this.isTokenRenewable() && (result = this.tryRenewToken()).shouldRenew()) {
                    this.scheduleRenewal();
                }
            }
            catch (Exception e) {
                this.logger.error((Object)"Cannot renew VaultToken", (Throwable)e);
                this.dispatch(new LoginTokenRenewalFailedEvent(token, (Throwable)e));
            }
        };
        Optional<TokenWrapper> token = this.getToken();
        token.ifPresent(tokenWrapper -> this.getTaskScheduler().schedule(task, (Trigger)this.createTrigger((TokenWrapper)tokenWrapper)));
    }

    private LifecycleAwareSessionManagerSupport.OneShotTrigger createTrigger(TokenWrapper tokenWrapper) {
        return new LifecycleAwareSessionManagerSupport.OneShotTrigger(this.getRefreshTrigger().nextExecutionTime((LoginToken)tokenWrapper.getToken()));
    }

    private static String format(String message, RuntimeException e) {
        if (e instanceof HttpStatusCodeException) {
            HttpStatusCodeException hsce = (HttpStatusCodeException)((Object)e);
            return String.format("%s: Status %s %s %s", message, hsce.getRawStatusCode(), hsce.getStatusText(), VaultResponses.getError(hsce.getResponseBodyAsString()));
        }
        return message;
    }

    static class RenewOutcome {
        private static final RenewOutcome SUCCESS = new RenewOutcome(false, true);
        private static final RenewOutcome TERMINAL_ERROR = new RenewOutcome(true, false);
        private static final RenewOutcome RENEWABLE_ERROR = new RenewOutcome(false, false);
        private final boolean terminalError;
        private final boolean successful;

        private RenewOutcome(boolean terminalError, boolean successful) {
            this.terminalError = terminalError;
            this.successful = successful;
        }

        public boolean shouldRenew() {
            return !this.terminalError;
        }
    }

    protected static class TokenWrapper {
        private final VaultToken token;
        private final boolean revocable;

        TokenWrapper(VaultToken token, boolean revocable) {
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

