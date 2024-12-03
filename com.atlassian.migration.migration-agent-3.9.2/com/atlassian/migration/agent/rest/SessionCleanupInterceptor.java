/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ContainerResponse
 *  com.sun.jersey.spi.monitoring.ResponseListener
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.store.jpa.impl.ThreadBoundSessionContext;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.monitoring.ResponseListener;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SessionCleanupInterceptor
implements ResponseListener {
    public void onError(long id, Throwable ex) {
        ThreadBoundSessionContext.cleanupAnyOrphanedSessions();
    }

    public void onResponse(long id, ContainerResponse response) {
        ThreadBoundSessionContext.cleanupAnyOrphanedSessions();
    }

    public void onMappedException(long id, Throwable exception, ExceptionMapper mapper) {
        ThreadBoundSessionContext.cleanupAnyOrphanedSessions();
    }
}

