/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core.lease;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.vault.VaultException;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.lease.LeaseEndpoints;
import org.springframework.vault.core.lease.SecretLeaseEventPublisher;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.util.KeyValueDelegate;
import org.springframework.vault.support.LeaseStrategy;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.client.HttpStatusCodeException;

public class SecretLeaseContainer
extends SecretLeaseEventPublisher
implements InitializingBean,
DisposableBean {
    private static final AtomicIntegerFieldUpdater<SecretLeaseContainer> UPDATER = AtomicIntegerFieldUpdater.newUpdater(SecretLeaseContainer.class, "status");
    private static final AtomicInteger poolId = new AtomicInteger();
    private static final int STATUS_INITIAL = 0;
    private static final int STATUS_STARTED = 1;
    private static final int STATUS_DESTROYED = 2;
    private static Log logger = LogFactory.getLog(SecretLeaseContainer.class);
    private final List<RequestedSecret> requestedSecrets = new CopyOnWriteArrayList<RequestedSecret>();
    private final Map<RequestedSecret, LeaseRenewalScheduler> renewals = new ConcurrentHashMap<RequestedSecret, LeaseRenewalScheduler>();
    private final VaultOperations operations;
    private final KeyValueDelegate keyValueDelegate;
    private LeaseEndpoints leaseEndpoints = LeaseEndpoints.Leases;
    private Duration minRenewal = Duration.ofSeconds(10L);
    private Duration expiryThreshold = Duration.ofSeconds(60L);
    private LeaseStrategy leaseStrategy = LeaseStrategy.dropOnError();
    @Nullable
    private TaskScheduler taskScheduler;
    private boolean manageTaskScheduler;
    private volatile boolean initialized;
    private volatile int status = 0;

    public SecretLeaseContainer(VaultOperations operations) {
        Assert.notNull((Object)operations, "VaultOperations must not be null");
        this.operations = operations;
        this.keyValueDelegate = new KeyValueDelegate(this.operations);
    }

    public SecretLeaseContainer(VaultOperations operations, TaskScheduler taskScheduler) {
        Assert.notNull((Object)operations, "VaultOperations must not be null");
        Assert.notNull((Object)taskScheduler, "TaskScheduler must not be null");
        this.operations = operations;
        this.keyValueDelegate = new KeyValueDelegate(this.operations);
        this.setTaskScheduler(taskScheduler);
    }

    public void setLeaseEndpoints(LeaseEndpoints leaseEndpoints) {
        Assert.notNull((Object)leaseEndpoints, "LeaseEndpoints must not be null");
        this.leaseEndpoints = leaseEndpoints;
    }

    @Deprecated
    public void setMinRenewalSeconds(int minRenewalSeconds) {
        this.setMinRenewal(Duration.ofSeconds(minRenewalSeconds));
    }

    public void setMinRenewal(Duration minRenewal) {
        Assert.notNull((Object)minRenewal, "Minimal renewal time must not be null");
        Assert.isTrue(!minRenewal.isNegative(), "Minimal renewal time must not be negative");
        this.minRenewal = minRenewal;
    }

    @Deprecated
    public void setExpiryThresholdSeconds(int expiryThresholdSeconds) {
        this.setExpiryThreshold(Duration.ofSeconds(expiryThresholdSeconds));
    }

    public void setExpiryThreshold(Duration expiryThreshold) {
        Assert.notNull((Object)expiryThreshold, "Expiry threshold must not be null");
        Assert.isTrue(!expiryThreshold.isNegative(), "Expiry threshold must not be negative");
        this.expiryThreshold = expiryThreshold;
    }

    public int getMinRenewalSeconds() {
        return Math.toIntExact(this.minRenewal.getSeconds());
    }

    public Duration getMinRenewal() {
        return this.minRenewal;
    }

    public int getExpiryThresholdSeconds() {
        return Math.toIntExact(this.expiryThreshold.getSeconds());
    }

    public Duration getExpiryThreshold() {
        return this.expiryThreshold;
    }

    public void setLeaseStrategy(LeaseStrategy leaseStrategy) {
        Assert.notNull((Object)leaseStrategy, "LeaseStrategy must not be null");
        this.leaseStrategy = leaseStrategy;
    }

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        Assert.notNull((Object)taskScheduler, "TaskScheduler must not be null");
        this.taskScheduler = taskScheduler;
    }

    public RequestedSecret requestRenewableSecret(String path) {
        return this.addRequestedSecret(RequestedSecret.renewable(path));
    }

    public RequestedSecret requestRotatingSecret(String path) {
        return this.addRequestedSecret(RequestedSecret.rotating(path));
    }

    public RequestedSecret addRequestedSecret(RequestedSecret requestedSecret) {
        Assert.notNull((Object)requestedSecret, "RequestedSecret must not be null");
        this.requestedSecrets.add(requestedSecret);
        if (this.initialized) {
            Assert.state(this.taskScheduler != null, "TaskScheduler must not be null");
            LeaseRenewalScheduler leaseRenewalScheduler = new LeaseRenewalScheduler(this.taskScheduler);
            this.renewals.put(requestedSecret, leaseRenewalScheduler);
            if (this.status == 1) {
                this.start(requestedSecret, leaseRenewalScheduler);
            }
        }
        return requestedSecret;
    }

    public void start() {
        Assert.state(this.initialized, "Container is not initialized");
        Assert.state(this.status != 2, "Container is destroyed and cannot be started");
        HashMap<RequestedSecret, LeaseRenewalScheduler> renewals = new HashMap<RequestedSecret, LeaseRenewalScheduler>(this.renewals);
        if (UPDATER.compareAndSet(this, 0, 1)) {
            for (Map.Entry entry : renewals.entrySet()) {
                this.start((RequestedSecret)entry.getKey(), (LeaseRenewalScheduler)entry.getValue());
            }
        }
    }

    private void start(RequestedSecret requestedSecret, LeaseRenewalScheduler renewalScheduler) {
        this.doStart(requestedSecret, renewalScheduler, (secrets, lease) -> this.onSecretsObtained(requestedSecret, (Lease)lease, (Map)secrets.getRequiredData()), () -> {});
    }

    private void doStart(RequestedSecret requestedSecret, LeaseRenewalScheduler renewalScheduler, BiConsumer<VaultResponseSupport<Map<String, Object>>, Lease> callback, Runnable cannotObtainSecretsCallback) {
        VaultResponseSupport<Map<String, Object>> secrets = this.doGetSecrets(requestedSecret);
        if (secrets != null) {
            Lease lease = StringUtils.hasText(secrets.getLeaseId()) ? Lease.of(secrets.getLeaseId(), Duration.ofSeconds(secrets.getLeaseDuration()), secrets.isRenewable()) : (SecretLeaseContainer.isRotatingGenericSecret(requestedSecret, secrets) ? Lease.fromTimeToLive(Duration.ofSeconds(secrets.getLeaseDuration())) : Lease.none());
            if (renewalScheduler.isLeaseRenewable(lease, requestedSecret)) {
                this.scheduleLeaseRenewal(requestedSecret, lease, renewalScheduler);
            } else if (renewalScheduler.isLeaseRotateOnly(lease, requestedSecret)) {
                this.scheduleLeaseRotation(requestedSecret, lease, renewalScheduler);
            }
            callback.accept(secrets, lease);
        } else {
            cannotObtainSecretsCallback.run();
        }
    }

    private static boolean isRotatingGenericSecret(RequestedSecret requestedSecret, VaultResponseSupport<Map<String, Object>> secrets) {
        return RequestedSecret.Mode.ROTATE.equals((Object)requestedSecret.getMode()) && !secrets.isRenewable() && secrets.getLeaseDuration() > 0L;
    }

    public void stop() {
        if (UPDATER.compareAndSet(this, 1, 0)) {
            for (LeaseRenewalScheduler leaseRenewal : this.renewals.values()) {
                leaseRenewal.disableScheduleRenewal();
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (!this.initialized) {
            super.afterPropertiesSet();
            this.initialized = true;
            if (this.taskScheduler == null) {
                ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
                scheduler.setDaemon(true);
                scheduler.setThreadNamePrefix(String.format("%s-%d-", this.getClass().getSimpleName(), poolId.incrementAndGet()));
                scheduler.afterPropertiesSet();
                this.taskScheduler = scheduler;
                this.manageTaskScheduler = true;
            }
            for (RequestedSecret requestedSecret : this.requestedSecrets) {
                this.renewals.put(requestedSecret, new LeaseRenewalScheduler(this.taskScheduler));
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        int status = this.status;
        if ((status == 0 || status == 1) && UPDATER.compareAndSet(this, status, 2)) {
            for (Map.Entry<RequestedSecret, LeaseRenewalScheduler> entry : this.renewals.entrySet()) {
                Lease lease = entry.getValue().getLease();
                entry.getValue().disableScheduleRenewal();
                if (lease == null || !lease.hasLeaseId()) continue;
                this.doRevokeLease(entry.getKey(), lease);
            }
            if (this.manageTaskScheduler && this.taskScheduler instanceof DisposableBean) {
                ((DisposableBean)((Object)this.taskScheduler)).destroy();
                this.taskScheduler = null;
            }
        }
    }

    public boolean renew(RequestedSecret secret) {
        LeaseRenewalScheduler renewalScheduler = this.getRenewalSchedulder(secret);
        Lease lease = renewalScheduler.getLease();
        if (lease == null) {
            throw new IllegalStateException(String.format("No lease associated with secret %s", secret));
        }
        if (!renewalScheduler.isLeaseRenewable(lease, secret)) {
            throw new IllegalStateException("Secret is not qualified for renewal");
        }
        return this.renewAndSchedule(secret, renewalScheduler, lease) != lease;
    }

    public void rotate(RequestedSecret secret) {
        LeaseRenewalScheduler renewalScheduler = this.getRenewalSchedulder(secret);
        Lease lease = renewalScheduler.getLease();
        if (lease == null) {
            throw new IllegalStateException(String.format("No lease associated with secret %s", secret));
        }
        if (!renewalScheduler.isLeaseRenewable(lease, secret) && !renewalScheduler.isLeaseRotateOnly(lease, secret)) {
            throw new IllegalStateException("Secret is not qualified for rotation");
        }
        this.onLeaseExpired(secret, lease);
    }

    private void scheduleLeaseRenewal(RequestedSecret requestedSecret, Lease lease, LeaseRenewalScheduler leaseRenewal) {
        SecretLeaseContainer.logRenewalCandidate(requestedSecret, lease, "renewal");
        leaseRenewal.scheduleRenewal(requestedSecret, leaseToRenew -> this.renewAndSchedule(requestedSecret, leaseRenewal, leaseToRenew), lease, this.getMinRenewal(), this.getExpiryThreshold());
    }

    private Lease renewAndSchedule(RequestedSecret requestedSecret, LeaseRenewalScheduler leaseRenewal, Lease leaseToRenew) {
        Lease newLease = this.doRenewLease(requestedSecret, leaseToRenew);
        if (!Lease.none().equals(newLease)) {
            this.scheduleLeaseRenewal(requestedSecret, newLease, leaseRenewal);
            this.onAfterLeaseRenewed(requestedSecret, newLease);
        }
        return newLease;
    }

    private void scheduleLeaseRotation(RequestedSecret secret, Lease lease, LeaseRenewalScheduler leaseRenewal) {
        SecretLeaseContainer.logRenewalCandidate(secret, lease, "rotation");
        leaseRenewal.scheduleRenewal(secret, leaseToRotate -> {
            this.onLeaseExpired(secret, lease);
            return Lease.none();
        }, lease, this.getMinRenewal(), this.getExpiryThreshold());
    }

    private LeaseRenewalScheduler getRenewalSchedulder(RequestedSecret secret) {
        LeaseRenewalScheduler renewalScheduler = this.renewals.get(secret);
        if (renewalScheduler == null) {
            throw new IllegalArgumentException(String.format("No such secret %s", secret));
        }
        return renewalScheduler;
    }

    private static void logRenewalCandidate(RequestedSecret requestedSecret, Lease lease, String action) {
        if (logger.isDebugEnabled()) {
            if (lease.hasLeaseId()) {
                logger.debug(String.format("Secret %s with Lease %s qualified for %s", requestedSecret.getPath(), lease.getLeaseId(), action));
            } else {
                logger.debug(String.format("Secret %s with cache hint is qualified for %s", requestedSecret.getPath(), action));
            }
        }
    }

    @Nullable
    protected VaultResponseSupport<Map<String, Object>> doGetSecrets(RequestedSecret requestedSecret) {
        try {
            VaultResponse secrets = this.keyValueDelegate.isVersioned(requestedSecret.getPath()) ? this.keyValueDelegate.getSecret(requestedSecret.getPath()) : this.operations.read(requestedSecret.getPath());
            if (secrets == null) {
                this.onSecretsNotFound(requestedSecret);
            }
            return secrets;
        }
        catch (RuntimeException e) {
            this.onError(requestedSecret, Lease.none(), e);
            return null;
        }
    }

    protected Lease doRenewLease(RequestedSecret requestedSecret, Lease lease) {
        try {
            Lease renewed;
            Lease lease2 = renewed = lease.hasLeaseId() ? this.doRenew(lease) : lease;
            if (!renewed.hasLeaseId() || renewed.getLeaseDuration().isZero() || renewed.getLeaseDuration().getSeconds() < this.minRenewal.getSeconds()) {
                this.onLeaseExpired(requestedSecret, lease);
                return Lease.none();
            }
            return renewed;
        }
        catch (RuntimeException e) {
            VaultException exceptionToUse;
            HttpStatusCodeException httpException = this.potentiallyUnwrapHttpStatusCodeException(e);
            boolean expired = false;
            if (httpException != null) {
                if (httpException.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    expired = true;
                    this.onLeaseExpired(requestedSecret, lease);
                }
                exceptionToUse = new VaultException(String.format("Cannot renew lease: Status %s %s %s", httpException.getRawStatusCode(), httpException.getStatusText(), VaultResponses.getError(httpException.getResponseBodyAsString())), e);
            } else {
                exceptionToUse = new VaultException("Cannot renew lease", e);
            }
            this.onError(requestedSecret, lease, exceptionToUse);
            if (expired || this.leaseStrategy.shouldDrop(exceptionToUse)) {
                return Lease.none();
            }
            return lease;
        }
    }

    @Nullable
    private HttpStatusCodeException potentiallyUnwrapHttpStatusCodeException(RuntimeException e) {
        if (e instanceof HttpStatusCodeException) {
            return (HttpStatusCodeException)e;
        }
        if (e.getCause() instanceof HttpStatusCodeException) {
            return (HttpStatusCodeException)e.getCause();
        }
        return null;
    }

    private Lease doRenew(Lease lease) {
        return this.operations.doWithSession(restOperations -> this.leaseEndpoints.renew(lease, restOperations));
    }

    @Override
    protected void onLeaseExpired(RequestedSecret requestedSecret, Lease lease) {
        if (requestedSecret.getMode() == RequestedSecret.Mode.ROTATE) {
            this.doStart(requestedSecret, this.renewals.get(requestedSecret), (secrets, currentLease) -> this.onSecretsRotated(requestedSecret, lease, (Lease)currentLease, (Map)secrets.getRequiredData()), () -> super.onLeaseExpired(requestedSecret, lease));
        } else {
            super.onLeaseExpired(requestedSecret, lease);
        }
    }

    protected void doRevokeLease(RequestedSecret requestedSecret, Lease lease) {
        try {
            this.onBeforeLeaseRevocation(requestedSecret, lease);
            this.operations.doWithSession(restOperations -> {
                this.leaseEndpoints.revoke(lease, restOperations);
                return null;
            });
            this.onAfterLeaseRevocation(requestedSecret, lease);
        }
        catch (HttpStatusCodeException e) {
            this.onError(requestedSecret, lease, new VaultException(String.format("Cannot revoke lease: %s", VaultResponses.getError(e.getResponseBodyAsString()))));
        }
        catch (RuntimeException e) {
            this.onError(requestedSecret, lease, e);
        }
    }

    static interface RenewLease {
        public Lease renewLease(Lease var1) throws VaultException;
    }

    static class OneShotTrigger
    implements Trigger {
        private static final AtomicIntegerFieldUpdater<OneShotTrigger> UPDATER = AtomicIntegerFieldUpdater.newUpdater(OneShotTrigger.class, "status");
        private static final int STATUS_ARMED = 0;
        private static final int STATUS_FIRED = 1;
        private volatile int status = 0;
        private final long seconds;

        OneShotTrigger(long seconds) {
            this.seconds = seconds;
        }

        @Override
        @Nullable
        public Date nextExecutionTime(TriggerContext triggerContext) {
            if (UPDATER.compareAndSet(this, 0, 1)) {
                return new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(this.seconds));
            }
            return null;
        }
    }

    static class LeaseRenewalScheduler {
        private static Log logger = LogFactory.getLog(LeaseRenewalScheduler.class);
        private final TaskScheduler taskScheduler;
        final AtomicReference<Lease> currentLeaseRef = new AtomicReference();
        final Map<Lease, ScheduledFuture<?>> schedules = new ConcurrentHashMap();

        LeaseRenewalScheduler(TaskScheduler taskScheduler) {
            this.taskScheduler = taskScheduler;
        }

        void scheduleRenewal(final RequestedSecret requestedSecret, final RenewLease renewLease, final Lease lease, Duration minRenewal, Duration expiryThreshold) {
            if (logger.isDebugEnabled()) {
                if (lease.hasLeaseId()) {
                    logger.debug(String.format("Scheduling renewal for secret %s with lease %s, lease duration %d", requestedSecret.getPath(), lease.getLeaseId(), lease.getLeaseDuration().getSeconds()));
                } else {
                    logger.debug(String.format("Scheduling renewal for secret %s, with cache hint duration %d", requestedSecret.getPath(), lease.getLeaseDuration().getSeconds()));
                }
            }
            Lease currentLease = this.currentLeaseRef.get();
            this.currentLeaseRef.set(lease);
            if (currentLease != null) {
                this.cancelSchedule(currentLease);
            }
            Runnable task = new Runnable(){

                @Override
                public void run() {
                    schedules.remove(lease);
                    if (currentLeaseRef.get() != lease) {
                        logger.debug("Current lease has changed. Skipping renewal");
                        return;
                    }
                    if (logger.isDebugEnabled()) {
                        if (lease.hasLeaseId()) {
                            logger.debug(String.format("Renewing lease %s for secret %s", lease.getLeaseId(), requestedSecret.getPath()));
                        } else {
                            logger.debug(String.format("Renewing secret without lease %s", requestedSecret.getPath()));
                        }
                    }
                    try {
                        currentLeaseRef.compareAndSet(lease, renewLease.renewLease(lease));
                    }
                    catch (Exception e) {
                        logger.error(String.format("Cannot renew lease %s", lease.getLeaseId()), e);
                    }
                }
            };
            ScheduledFuture<?> scheduledFuture = this.taskScheduler.schedule(task, new OneShotTrigger(this.getRenewalSeconds(lease, minRenewal, expiryThreshold)));
            this.schedules.put(lease, scheduledFuture);
        }

        private void cancelSchedule(Lease lease) {
            ScheduledFuture<?> scheduledFuture = this.schedules.get(lease);
            if (scheduledFuture != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Canceling previously registered schedule for lease %s", lease.getLeaseId()));
                }
                scheduledFuture.cancel(false);
            }
        }

        void disableScheduleRenewal() {
            this.currentLeaseRef.set(null);
            HashSet<Lease> leases = new HashSet<Lease>(this.schedules.keySet());
            for (Lease lease : leases) {
                this.cancelSchedule(lease);
                this.schedules.remove(lease);
            }
        }

        private long getRenewalSeconds(Lease lease, Duration minRenewal, Duration expiryThreshold) {
            return Math.max(minRenewal.getSeconds(), lease.getLeaseDuration().getSeconds() - expiryThreshold.getSeconds());
        }

        private boolean isLeaseRenewable(@Nullable Lease lease, RequestedSecret requestedSecret) {
            if (lease == null) {
                return false;
            }
            if (lease.isRenewable()) {
                return true;
            }
            return !lease.hasLeaseId() && !lease.getLeaseDuration().isZero() && requestedSecret.getMode() == RequestedSecret.Mode.ROTATE;
        }

        @Nullable
        public Lease getLease() {
            return this.currentLeaseRef.get();
        }

        private boolean isLeaseRotateOnly(Lease lease, RequestedSecret requestedSecret) {
            if (lease == null) {
                return false;
            }
            return lease.hasLeaseId() && !lease.getLeaseDuration().isZero() && !lease.isRenewable() && requestedSecret.getMode() == RequestedSecret.Mode.ROTATE;
        }
    }
}

