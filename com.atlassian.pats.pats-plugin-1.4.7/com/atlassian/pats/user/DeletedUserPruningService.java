/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.event.user.UserDeletedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.pats.user;

import com.atlassian.crowd.event.user.UserDeletedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.pats.api.TokenService;
import com.atlassian.pats.checker.ProductUserProvider;
import com.atlassian.pats.db.Tables;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class DeletedUserPruningService
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DeletedUserPruningService.class);
    private final EventPublisher eventPublisher;
    private final TokenService tokenService;
    private final ProductUserProvider productUserProvider;

    public DeletedUserPruningService(EventPublisher eventPublisher, TokenService tokenService, ProductUserProvider productUserProvider) {
        this.eventPublisher = eventPublisher;
        this.tokenService = tokenService;
        this.productUserProvider = productUserProvider;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onUserDeleted(UserDeletedEvent userDeletedEvent) {
        String username = userDeletedEvent.getUsername();
        log.debug("Received user deleted event, deleting all tokens for user {}", (Object)username);
        Optional<UserKey> userKey = this.productUserProvider.getKeyForUsername(username);
        if (userKey.isPresent()) {
            int deleted = this.tokenService.delete(null, Tables.TOKEN.userKey.eq(userKey.get().getStringValue()));
            log.debug("Deleted {} tokens for user {}", (Object)deleted, (Object)username);
        } else {
            log.warn("Can not delete tokens for user {}, could not obtain user key", (Object)username);
        }
    }
}

