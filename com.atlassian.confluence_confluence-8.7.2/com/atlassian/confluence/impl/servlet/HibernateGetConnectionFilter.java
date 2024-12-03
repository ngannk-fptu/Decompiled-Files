/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.servlet.DispatcherType
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.servlet;

import com.atlassian.confluence.impl.core.persistence.hibernate.ExceptionMonitorPredicates;
import com.atlassian.confluence.impl.profiling.ThreadLocalMethodHooks;
import com.atlassian.core.filters.AbstractHttpFilter;
import io.atlassian.util.concurrent.LazyReference;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateGetConnectionFilter
extends AbstractHttpFilter {
    private static final Logger log = LoggerFactory.getLogger(HibernateGetConnectionFilter.class);
    private static final String ERROR_MESSAGE = "DB connection pool is exhausted; obtaining a new DB connection for this request is disallowed.";
    private final Supplier<Method> getConnectionMethodRef = new LazyReference<Method>(){

        protected Method create() throws NoSuchMethodException {
            return ConnectionProvider.class.getDeclaredMethod("getConnection", new Class[0]);
        }
    };

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            if (HibernateGetConnectionFilter.shouldDisallowGetConnection(request)) {
                this.registerHook();
            }
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            ThreadLocalMethodHooks.unregisterHooks();
        }
    }

    private void registerHook() {
        log.debug("Registering callback to disallow this request from opening a DB connection");
        ThreadLocalMethodHooks.registerHook(this.getConnectionMethodRef.get(), (method, args, target) -> {
            throw new IllegalStateException(ERROR_MESSAGE);
        });
    }

    private static boolean shouldDisallowGetConnection(HttpServletRequest request) {
        return HibernateGetConnectionFilter.isHttp500Error(request) && ExceptionMonitorPredicates.shortCircuitRequestTester().test(request);
    }

    private static boolean isHttp500Error(HttpServletRequest request) {
        return request.getDispatcherType() == DispatcherType.ERROR && Integer.valueOf(500).equals(request.getAttribute("javax.servlet.error.status_code"));
    }
}

