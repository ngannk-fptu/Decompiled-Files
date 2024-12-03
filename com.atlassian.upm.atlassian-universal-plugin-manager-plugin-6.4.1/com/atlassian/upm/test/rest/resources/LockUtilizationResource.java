/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.user.UserKey
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.impl.Locks;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/test/locks")
public class LockUtilizationResource {
    private static final Logger log = LoggerFactory.getLogger(LockUtilizationResource.class);
    private static final Random random = new Random(System.currentTimeMillis());
    private final PermissionEnforcer permissionEnforcer;
    private final ClusterLockService lockService;
    private final ExecutorService executor;

    public LockUtilizationResource(PermissionEnforcer permissionEnforcer, ClusterLockService lockService) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.lockService = Objects.requireNonNull(lockService, "lockServiceFactory");
        this.executor = Executors.newFixedThreadPool(4, ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName()));
    }

    @POST
    @XsrfProtectionExcluded
    public Response utilizeClassLevelLock(@QueryParam(value="count") @DefaultValue(value="10") int count) {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        this.utilizeLock(Locks.getLock(this.lockService, this.getClass()), count, this.getClass().getSimpleName());
        return Response.status((Response.Status)Response.Status.ACCEPTED).build();
    }

    @Path(value="/{userKey}")
    @POST
    @XsrfProtectionExcluded
    public Response utilizeUserLevelLock(@PathParam(value="userKey") String userKey, @QueryParam(value="count") @DefaultValue(value="10") int count) {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        this.utilizeLock(Locks.getLock(this.lockService, this.getClass(), new UserKey(userKey)), count, userKey);
        return Response.status((Response.Status)Response.Status.ACCEPTED).build();
    }

    private void utilizeLock(ClusterLock lock, int count, String lockName) {
        log.warn("Utilizing lock service of implementation " + this.lockService.getClass().getName());
        this.executor.submit(() -> {
            int i = 0;
            while (i < count) {
                long seconds = 1 + random.nextInt(2);
                int cnt = i++;
                log.warn("Acquiring lock #" + cnt + " for: " + lockName);
                Locks.writeWithLock(lock, () -> {
                    log.warn("Acquired lock #" + cnt + " for: " + lockName);
                    this.simpleSleep(seconds);
                    log.warn("Releasing lock #" + cnt + " for: " + lockName);
                });
                log.warn("Released lock #" + cnt + " for: " + lockName);
                this.simpleSleep(seconds);
            }
            log.warn("Completed lock utilization for: " + lockName);
        });
    }

    private void simpleSleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

