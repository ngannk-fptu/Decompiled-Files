/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.scheduling.Trigger
 *  org.springframework.scheduling.TriggerContext
 *  org.springframework.util.Assert
 */
package org.springframework.vault.authentication;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;
import org.springframework.vault.authentication.AuthenticationEventPublisher;
import org.springframework.vault.authentication.LoginToken;
import org.springframework.vault.support.LeaseStrategy;

public abstract class LifecycleAwareSessionManagerSupport
extends AuthenticationEventPublisher {
    public static final int REFRESH_PERIOD_BEFORE_EXPIRY = 5;
    private static final RefreshTrigger DEFAULT_TRIGGER = new FixedTimeoutRefreshTrigger(5L, TimeUnit.SECONDS);
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final TaskScheduler taskScheduler;
    private final RefreshTrigger refreshTrigger;
    private boolean tokenSelfLookupEnabled = true;
    private LeaseStrategy leaseStrategy = LeaseStrategy.dropOnError();

    public LifecycleAwareSessionManagerSupport(TaskScheduler taskScheduler) {
        this(taskScheduler, DEFAULT_TRIGGER);
    }

    public LifecycleAwareSessionManagerSupport(TaskScheduler taskScheduler, RefreshTrigger refreshTrigger) {
        Assert.notNull((Object)taskScheduler, (String)"TaskScheduler must not be null");
        Assert.notNull((Object)refreshTrigger, (String)"RefreshTrigger must not be null");
        this.taskScheduler = taskScheduler;
        this.refreshTrigger = refreshTrigger;
    }

    protected boolean isTokenSelfLookupEnabled() {
        return this.tokenSelfLookupEnabled;
    }

    public void setTokenSelfLookupEnabled(boolean tokenSelfLookupEnabled) {
        this.tokenSelfLookupEnabled = tokenSelfLookupEnabled;
    }

    public void setLeaseStrategy(LeaseStrategy leaseStrategy) {
        Assert.notNull((Object)leaseStrategy, (String)"LeaseStrategy must not be null");
        this.leaseStrategy = leaseStrategy;
    }

    LeaseStrategy getLeaseStrategy() {
        return this.leaseStrategy;
    }

    protected TaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    protected RefreshTrigger getRefreshTrigger() {
        return this.refreshTrigger;
    }

    protected boolean isExpired(LoginToken loginToken) {
        Duration validTtlThreshold = this.getRefreshTrigger().getValidTtlThreshold(loginToken);
        return loginToken.getLeaseDuration().compareTo(validTtlThreshold) <= 0;
    }

    public static class FixedTimeoutRefreshTrigger
    implements RefreshTrigger {
        private static final Duration ONE_SECOND = Duration.ofSeconds(1L);
        private final Duration duration;
        private final Duration expiryThreshold;

        public FixedTimeoutRefreshTrigger(long refreshBeforeExpiry, TimeUnit timeUnit) {
            Assert.isTrue((refreshBeforeExpiry >= 0L ? 1 : 0) != 0, (String)"Duration must be greater or equal to zero");
            Assert.notNull((Object)((Object)timeUnit), (String)"TimeUnit must not be null");
            this.duration = Duration.ofMillis(timeUnit.toMillis(refreshBeforeExpiry));
            this.expiryThreshold = Duration.ofMillis(timeUnit.toMillis(refreshBeforeExpiry) + 2000L);
        }

        public FixedTimeoutRefreshTrigger(Duration refreshBeforeExpiry) {
            this(refreshBeforeExpiry, refreshBeforeExpiry.plus(Duration.ofSeconds(2L)));
        }

        public FixedTimeoutRefreshTrigger(Duration refreshBeforeExpiry, Duration expiryThreshold) {
            Assert.isTrue((refreshBeforeExpiry.toMillis() >= 0L ? 1 : 0) != 0, (String)"Refresh before expiry timeout must be greater or equal to zero");
            Assert.notNull((Object)expiryThreshold, (String)"Expiry threshold must not be null");
            this.duration = refreshBeforeExpiry;
            this.expiryThreshold = expiryThreshold;
        }

        @Override
        public Date nextExecutionTime(LoginToken loginToken) {
            long milliseconds = Math.max(ONE_SECOND.toMillis(), loginToken.getLeaseDuration().toMillis() - this.duration.toMillis());
            return new Date(System.currentTimeMillis() + milliseconds);
        }

        @Override
        public Duration getValidTtlThreshold(LoginToken loginToken) {
            return this.expiryThreshold;
        }
    }

    public static interface RefreshTrigger {
        public Date nextExecutionTime(LoginToken var1);

        public Duration getValidTtlThreshold(LoginToken var1);
    }

    protected static class OneShotTrigger
    implements Trigger {
        private final AtomicBoolean fired = new AtomicBoolean();
        private final Date nextExecutionTime;

        public OneShotTrigger(Date nextExecutionTime) {
            this.nextExecutionTime = nextExecutionTime;
        }

        @Nullable
        public Date nextExecutionTime(TriggerContext triggerContext) {
            if (this.fired.compareAndSet(false, true)) {
                return this.nextExecutionTime;
            }
            return null;
        }
    }
}

