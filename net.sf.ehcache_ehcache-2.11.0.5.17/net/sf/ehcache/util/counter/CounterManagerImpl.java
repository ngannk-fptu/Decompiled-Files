/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.counter;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import net.sf.ehcache.util.FailSafeTimer;
import net.sf.ehcache.util.counter.Counter;
import net.sf.ehcache.util.counter.CounterConfig;
import net.sf.ehcache.util.counter.CounterManager;
import net.sf.ehcache.util.counter.sampled.SampledCounter;
import net.sf.ehcache.util.counter.sampled.SampledCounterImpl;

public class CounterManagerImpl
implements CounterManager {
    private final FailSafeTimer timer;
    private boolean shutdown;
    private final List<Counter> counters = new ArrayList<Counter>();

    public CounterManagerImpl(FailSafeTimer timer) {
        if (timer == null) {
            throw new IllegalArgumentException("Timer cannot be null");
        }
        this.timer = timer;
    }

    @Override
    public synchronized void shutdown() {
        if (this.shutdown) {
            return;
        }
        try {
            for (Counter counter : this.counters) {
                if (!(counter instanceof SampledCounter)) continue;
                ((SampledCounter)counter).shutdown();
            }
        }
        finally {
            this.shutdown = true;
        }
    }

    @Override
    public synchronized Counter createCounter(CounterConfig config) {
        if (this.shutdown) {
            throw new IllegalStateException("counter manager is shutdown");
        }
        if (config == null) {
            throw new NullPointerException("config cannot be null");
        }
        Counter counter = config.createCounter();
        this.addCounter(counter);
        return counter;
    }

    @Override
    public synchronized void addCounter(Counter counter) {
        if (counter instanceof SampledCounterImpl) {
            final SampledCounterImpl sampledCounter = (SampledCounterImpl)counter;
            TimerTask timerTask = new TimerTask(){

                @Override
                public void run() {
                    AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            sampledCounter.getTimerTask().run();
                            return null;
                        }
                    });
                }
            };
            this.timer.schedule(timerTask, sampledCounter.getIntervalMillis(), sampledCounter.getIntervalMillis());
        }
        this.counters.add(counter);
    }

    @Override
    public void shutdownCounter(Counter counter) {
        if (counter instanceof SampledCounter) {
            SampledCounter sc = (SampledCounter)counter;
            sc.shutdown();
        }
    }
}

