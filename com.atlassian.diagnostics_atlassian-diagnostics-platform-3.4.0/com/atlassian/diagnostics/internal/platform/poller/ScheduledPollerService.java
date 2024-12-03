/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.platform.poller;

import com.atlassian.diagnostics.internal.platform.poller.DiagnosticPoller;
import com.atlassian.diagnostics.internal.platform.poller.PollerInfo;
import com.atlassian.diagnostics.internal.platform.poller.ScheduleInterval;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduledPollerService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledPollerService.class);
    private final Map<ScheduledPoller, ScheduledFuture> pollers = new HashMap<ScheduledPoller, ScheduledFuture>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Function<Runnable, Runnable> wrapper;

    public ScheduledPollerService(Function<Runnable, Runnable> wrapper) {
        this.wrapper = wrapper;
        logger.info("ScheduledPollerService is wrapped in product thread.");
    }

    public ScheduledPollerService() {
        this(Function.identity());
    }

    public synchronized void start(List<ScheduledPoller> pollers) {
        for (ScheduledPoller poller : pollers) {
            this.start(poller);
        }
    }

    public synchronized void start(ScheduledPoller poller) {
        if (!this.pollers.containsKey(poller)) {
            this.schedulePoller(poller);
        } else {
            this.start(poller.diagnosticPoller.getKey());
        }
    }

    public synchronized void start(@Nonnull String key) {
        this.findPoller(key).ifPresent(poller -> {
            boolean canStart = Optional.ofNullable(this.pollers.get(poller)).map(this::isCompleted).orElse(true);
            if (canStart) {
                this.schedulePoller((ScheduledPoller)poller);
            }
        });
    }

    private void schedulePoller(ScheduledPoller poller) {
        this.pollers.put(poller, this.scheduledExecutorService.scheduleAtFixedRate(this.wrapper.apply(this.run(poller)), 0L, poller.scheduleInterval.getDelay(), poller.scheduleInterval.getTimeUnit()));
    }

    private Runnable run(ScheduledPoller poller) {
        return () -> {
            try {
                if (poller.diagnosticPoller.isEnabled()) {
                    poller.diagnosticPoller.execute();
                }
            }
            catch (Throwable e) {
                logger.debug("Poller failed", e);
            }
        };
    }

    private Optional<ScheduledPoller> findPoller(@Nonnull String key) {
        return this.pollers.keySet().stream().filter(poller -> ((ScheduledPoller)poller).diagnosticPoller.getKey().equals(key)).findFirst();
    }

    public synchronized void shutdown() {
        this.scheduledExecutorService.shutdownNow();
    }

    public Set<PollerInfo> getPollerInfo() {
        return this.pollers.entrySet().stream().map(poller -> new PollerInfo(((ScheduledPoller)poller.getKey()).diagnosticPoller.getKey(), !this.isCompleted((ScheduledFuture)poller.getValue()))).collect(Collectors.toSet());
    }

    private boolean isCompleted(ScheduledFuture scheduledFuture) {
        return scheduledFuture == null || scheduledFuture.isDone() || scheduledFuture.isCancelled();
    }

    public static class ScheduledPoller {
        private final DiagnosticPoller diagnosticPoller;
        private final ScheduleInterval scheduleInterval;

        public static ScheduledPoller of(DiagnosticPoller diagnosticPoller, ScheduleInterval scheduleInterval) {
            return new ScheduledPoller(diagnosticPoller, scheduleInterval);
        }

        private ScheduledPoller(DiagnosticPoller diagnosticPoller, ScheduleInterval scheduleInterval) {
            this.diagnosticPoller = diagnosticPoller;
            this.scheduleInterval = scheduleInterval;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            ScheduledPoller that = (ScheduledPoller)o;
            return Objects.equals(this.diagnosticPoller.getKey(), that.diagnosticPoller.getKey());
        }

        public int hashCode() {
            return Objects.hash(this.diagnosticPoller.getKey());
        }
    }
}

