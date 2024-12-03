/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.impl.user.RegisteredUsersCache;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.LicenseCalculator;
import com.atlassian.confluence.util.transaction.TransactionExecutor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadOnlyLicenseCalculator
implements LicenseCalculator {
    private static final Logger log = LoggerFactory.getLogger(ReadOnlyLicenseCalculator.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(2L);
    private final TransactionExecutor<Integer> executor;
    private final Supplier<UserAccessor> userAccessorRef;
    private final RegisteredUsersCache registeredUsersCache;
    private volatile Future<Integer> lastCall;

    public ReadOnlyLicenseCalculator(RegisteredUsersCache registeredUsersCache, TransactionExecutor<Integer> executor) {
        this.executor = Objects.requireNonNull(executor);
        this.registeredUsersCache = Objects.requireNonNull(registeredUsersCache);
        this.userAccessorRef = Lazy.supplier(() -> (UserAccessor)ContainerManager.getComponent((String)"userAccessor"));
    }

    @Override
    public boolean isRunning() {
        return this.lastCall != null && !this.lastCall.isDone();
    }

    @Override
    public Integer getNumberOfLicensedUsers() {
        Future<Integer> f = this.executor.performTransactionAction(status -> this.registeredUsersCache.getNumberOfRegisteredUsers(this::lookupNumberOfLicensedUsers));
        this.lastCall = f;
        try {
            return f.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | ExecutionException e) {
            return this.handleExecutionFailure(e);
        }
        catch (TimeoutException e) {
            log.warn("Licensing calculation took more than {}", (Object)TIMEOUT);
            return -1;
        }
    }

    private int lookupNumberOfLicensedUsers() {
        log.info("Licensed user check not cached - starting to count all registered users");
        try (Ticker ignored = Timers.start((String)"getNumberOfLicensedUsers");){
            long before = System.currentTimeMillis();
            int userCount = ((UserAccessor)this.userAccessorRef.get()).countLicenseConsumingUsers();
            long after = System.currentTimeMillis();
            log.info("Took {} ms to determine number of registered users: [ {} ] ", (Object)(after - before), (Object)userCount);
            int n = userCount;
            return n;
        }
    }

    private Integer handleExecutionFailure(Exception e) {
        throw new RuntimeException(e);
    }

    @Deprecated
    public void setCacheFactory(CacheFactory cacheFactory) {
    }
}

