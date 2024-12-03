/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import net.sf.ehcache.statistics.extended.AbstractStatistic;
import org.terracotta.statistics.Time;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.archive.Timestamped;

public class SemiExpiringStatistic<T extends Number>
extends AbstractStatistic<T> {
    private boolean active = false;
    private long touchTimestamp = -1L;

    public SemiExpiringStatistic(ValueStatistic<T> source, ScheduledExecutorService executor, int historySize, long historyNanos) {
        super(source, executor, historySize, historyNanos);
    }

    @Override
    public List<Timestamped<T>> history() {
        this.touch();
        return super.history();
    }

    @Override
    public final synchronized boolean active() {
        return this.active;
    }

    protected final synchronized void touch() {
        this.touchTimestamp = Time.absoluteTime();
        this.start();
    }

    protected final synchronized void start() {
        if (!this.active) {
            this.startStatistic();
            this.startSampling();
            this.active = true;
        }
    }

    protected final synchronized boolean expire(long expiry) {
        if (this.touchTimestamp < expiry) {
            if (this.active) {
                this.stopSampling();
                this.stopStatistic();
                this.active = false;
            }
            return true;
        }
        return false;
    }

    protected void stopStatistic() {
    }

    protected void startStatistic() {
    }
}

