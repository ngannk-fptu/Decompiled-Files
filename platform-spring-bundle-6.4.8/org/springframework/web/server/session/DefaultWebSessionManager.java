/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server.session;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.InMemoryWebSessionStore;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.server.session.WebSessionManager;
import org.springframework.web.server.session.WebSessionStore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DefaultWebSessionManager
implements WebSessionManager {
    private static final Log logger = LogFactory.getLog(DefaultWebSessionManager.class);
    private WebSessionIdResolver sessionIdResolver = new CookieWebSessionIdResolver();
    private WebSessionStore sessionStore = new InMemoryWebSessionStore();

    public void setSessionIdResolver(WebSessionIdResolver sessionIdResolver) {
        Assert.notNull((Object)sessionIdResolver, "WebSessionIdResolver is required");
        this.sessionIdResolver = sessionIdResolver;
    }

    public WebSessionIdResolver getSessionIdResolver() {
        return this.sessionIdResolver;
    }

    public void setSessionStore(WebSessionStore sessionStore) {
        Assert.notNull((Object)sessionStore, "WebSessionStore is required");
        this.sessionStore = sessionStore;
    }

    public WebSessionStore getSessionStore() {
        return this.sessionStore;
    }

    @Override
    public Mono<WebSession> getSession(ServerWebExchange exchange2) {
        return Mono.defer(() -> this.retrieveSession(exchange2).switchIfEmpty(this.createWebSession()).doOnNext(session -> exchange2.getResponse().beforeCommit(() -> this.save(exchange2, (WebSession)session))));
    }

    private Mono<WebSession> createWebSession() {
        Mono session = this.sessionStore.createWebSession();
        if (logger.isDebugEnabled()) {
            session = session.doOnNext(s -> logger.debug((Object)"Created new WebSession."));
        }
        return session;
    }

    private Mono<WebSession> retrieveSession(ServerWebExchange exchange2) {
        return Flux.fromIterable(this.getSessionIdResolver().resolveSessionIds(exchange2)).concatMap(this.sessionStore::retrieveSession).next();
    }

    private Mono<Void> save(ServerWebExchange exchange2, WebSession session) {
        List<String> ids = this.getSessionIdResolver().resolveSessionIds(exchange2);
        if (!session.isStarted() || session.isExpired()) {
            if (!ids.isEmpty()) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)"WebSession expired or has been invalidated");
                }
                this.sessionIdResolver.expireSession(exchange2);
            }
            return Mono.empty();
        }
        if (ids.isEmpty() || !session.getId().equals(ids.get(0))) {
            this.sessionIdResolver.setSessionId(exchange2, session.getId());
        }
        return session.save();
    }
}

