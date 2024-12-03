/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.security.LoginEvent
 *  com.atlassian.confluence.event.events.security.LogoutEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.userlister;

import com.atlassian.confluence.event.events.security.LoginEvent;
import com.atlassian.confluence.event.events.security.LogoutEvent;
import com.atlassian.confluence.extra.userlister.UserListManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserListener
implements InitializingBean,
DisposableBean {
    private final EventListenerRegistrar eventListenerRegistrar;
    private final UserListManager userListManager;
    private final ExecutorService eventListenerExecutor;

    @Autowired
    public UserListener(@ComponentImport EventListenerRegistrar eventListenerRegistrar, UserListManager userListManager) {
        this(eventListenerRegistrar, userListManager, UserListener.createEventListenerExecutor(1000, 1));
    }

    UserListener(EventListenerRegistrar eventListenerRegistrar, UserListManager userListManager, ExecutorService eventListenerExecutor) {
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.userListManager = userListManager;
        this.eventListenerExecutor = eventListenerExecutor;
    }

    @EventListener
    public void handleLoginEvent(LoginEvent loginEvent) {
        this.eventListenerExecutor.submit(() -> this.userListManager.registerLoggedInUser(loginEvent.getUsername(), loginEvent.getSessionId()));
    }

    @EventListener
    public void handleLogoutEvent(LogoutEvent logoutEvent) {
        this.eventListenerExecutor.submit(() -> this.userListManager.unregisterLoggedInUser(logoutEvent.getUsername(), logoutEvent.getSessionId()));
    }

    public void afterPropertiesSet() {
        this.eventListenerRegistrar.register((Object)this);
    }

    public void destroy() {
        this.eventListenerExecutor.shutdownNow();
        this.eventListenerRegistrar.unregister((Object)this);
    }

    private static ExecutorService createEventListenerExecutor(int capacity, int poolSize) {
        return new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(capacity), ThreadFactories.namedThreadFactory((String)UserListener.class.getName(), (ThreadFactories.Type)ThreadFactories.Type.DAEMON), new ThreadPoolExecutor.DiscardOldestPolicy());
    }
}

