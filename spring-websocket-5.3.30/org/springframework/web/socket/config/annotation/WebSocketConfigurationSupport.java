/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.web.servlet.HandlerMapping
 */
package org.springframework.web.socket.config.annotation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.config.annotation.ServletWebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

public class WebSocketConfigurationSupport {
    @Nullable
    private ServletWebSocketHandlerRegistry handlerRegistry;
    @Nullable
    private TaskScheduler scheduler;

    @Bean
    public HandlerMapping webSocketHandlerMapping(@Qualifier(value="defaultSockJsTaskScheduler") @Nullable TaskScheduler scheduler) {
        ServletWebSocketHandlerRegistry registry = this.initHandlerRegistry();
        if (registry.requiresTaskScheduler()) {
            Assert.notNull((Object)scheduler, (String)"Expected default TaskScheduler bean");
            registry.setTaskScheduler(scheduler);
        }
        return registry.getHandlerMapping();
    }

    private ServletWebSocketHandlerRegistry initHandlerRegistry() {
        if (this.handlerRegistry == null) {
            this.handlerRegistry = new ServletWebSocketHandlerRegistry();
            this.registerWebSocketHandlers(this.handlerRegistry);
        }
        return this.handlerRegistry;
    }

    protected void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    }

    @Bean
    @Nullable
    public TaskScheduler defaultSockJsTaskScheduler() {
        if (this.scheduler == null && this.initHandlerRegistry().requiresTaskScheduler()) {
            ThreadPoolTaskScheduler threadPoolScheduler = new ThreadPoolTaskScheduler();
            threadPoolScheduler.setThreadNamePrefix("SockJS-");
            threadPoolScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
            threadPoolScheduler.setRemoveOnCancelPolicy(true);
            this.scheduler = threadPoolScheduler;
        }
        return this.scheduler;
    }
}

