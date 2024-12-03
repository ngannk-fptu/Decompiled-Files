/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 */
package software.amazon.awssdk.core.internal.retry;

import java.util.OptionalDouble;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;

@SdkInternalApi
public class RateLimitingTokenBucket {
    private static final double MIN_FILL_RATE = 0.5;
    private static final double MIN_CAPACITY = 1.0;
    private static final double SMOOTH = 0.8;
    private static final double BETA = 0.7;
    private static final double SCALE_CONSTANT = 0.4;
    private final Clock clock;
    private Double fillRate;
    private Double maxCapacity;
    private double currentCapacity;
    private Double lastTimestamp;
    private boolean enabled;
    private double measuredTxRate;
    private double lastTxRateBucket;
    private long requestCount;
    private double lastMaxRate;
    private double lastThrottleTime;
    private double timeWindow;

    public RateLimitingTokenBucket() {
        this.clock = new DefaultClock();
        this.initialize();
    }

    @SdkTestInternalApi
    RateLimitingTokenBucket(Clock clock) {
        this.clock = clock;
        this.initialize();
    }

    public boolean acquire(double amount) {
        return this.acquire(amount, false);
    }

    public boolean acquire(double amount, boolean fastFail) {
        OptionalDouble waitTime = this.acquireNonBlocking(amount, fastFail);
        if (!waitTime.isPresent()) {
            return false;
        }
        double t = waitTime.getAsDouble();
        if (t > 0.0) {
            this.sleep(t);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OptionalDouble acquireNonBlocking(double amount, boolean fastFail) {
        double waitTime = 0.0;
        RateLimitingTokenBucket rateLimitingTokenBucket = this;
        synchronized (rateLimitingTokenBucket) {
            if (!this.enabled) {
                return OptionalDouble.of(0.0);
            }
            this.refill();
            double originalCapacity = this.currentCapacity;
            double unfulfilled = this.tryAcquireCapacity(amount);
            if (unfulfilled > 0.0 && fastFail) {
                this.currentCapacity = originalCapacity;
                return OptionalDouble.empty();
            }
            if (unfulfilled > 0.0) {
                waitTime = unfulfilled / this.fillRate;
            }
        }
        return OptionalDouble.of(waitTime);
    }

    double tryAcquireCapacity(double amount) {
        double result = amount <= this.currentCapacity ? 0.0 : amount - this.currentCapacity;
        this.currentCapacity -= amount;
        return result;
    }

    private void initialize() {
        this.fillRate = null;
        this.maxCapacity = null;
        this.currentCapacity = 0.0;
        this.lastTimestamp = null;
        this.enabled = false;
        this.measuredTxRate = 0.0;
        this.lastTxRateBucket = Math.floor(this.clock.time());
        this.requestCount = 0L;
        this.lastMaxRate = 0.0;
        this.lastThrottleTime = this.clock.time();
    }

    synchronized void refill() {
        double timestamp = this.clock.time();
        if (this.lastTimestamp == null) {
            this.lastTimestamp = timestamp;
            return;
        }
        double fillAmount = (timestamp - this.lastTimestamp) * this.fillRate;
        this.currentCapacity = Math.min(this.maxCapacity, this.currentCapacity + fillAmount);
        this.lastTimestamp = timestamp;
    }

    private synchronized void updateRate(double newRps) {
        this.refill();
        this.fillRate = Math.max(newRps, 0.5);
        this.maxCapacity = Math.max(newRps, 1.0);
        this.currentCapacity = Math.min(this.currentCapacity, this.maxCapacity);
    }

    private synchronized void updateMeasuredRate() {
        double t = this.clock.time();
        double timeBucket = Math.floor(t * 2.0) / 2.0;
        ++this.requestCount;
        if (timeBucket > this.lastTxRateBucket) {
            double currentRate = (double)this.requestCount / (timeBucket - this.lastTxRateBucket);
            this.measuredTxRate = currentRate * 0.8 + this.measuredTxRate * 0.19999999999999996;
            this.requestCount = 0L;
            this.lastTxRateBucket = timeBucket;
        }
    }

    synchronized void enable() {
        this.enabled = true;
    }

    public synchronized void updateClientSendingRate(boolean throttlingResponse) {
        double calculatedRate;
        this.updateMeasuredRate();
        if (throttlingResponse) {
            double rateToUse = !this.enabled ? this.measuredTxRate : Math.min(this.measuredTxRate, this.fillRate);
            this.lastMaxRate = rateToUse;
            this.calculateTimeWindow();
            this.lastThrottleTime = this.clock.time();
            calculatedRate = this.cubicThrottle(rateToUse);
            this.enable();
        } else {
            this.calculateTimeWindow();
            calculatedRate = this.cubicSuccess(this.clock.time());
        }
        double newRate = Math.min(calculatedRate, 2.0 * this.measuredTxRate);
        this.updateRate(newRate);
    }

    synchronized void calculateTimeWindow() {
        this.timeWindow = Math.pow(this.lastMaxRate * 0.30000000000000004 / 0.4, 0.3333333333333333);
    }

    void sleep(double seconds) {
        long millisToSleep = (long)(seconds * 1000.0);
        try {
            Thread.sleep(millisToSleep);
        }
        catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw SdkClientException.create("Sleep interrupted", ie);
        }
    }

    double cubicThrottle(double rateToUse) {
        double calculatedRate = rateToUse * 0.7;
        return calculatedRate;
    }

    synchronized double cubicSuccess(double timestamp) {
        double dt = timestamp - this.lastThrottleTime;
        double calculatedRate = 0.4 * Math.pow(dt - this.timeWindow, 3.0) + this.lastMaxRate;
        return calculatedRate;
    }

    @SdkTestInternalApi
    synchronized void setLastMaxRate(double lastMaxRate) {
        this.lastMaxRate = lastMaxRate;
    }

    @SdkTestInternalApi
    synchronized void setLastThrottleTime(double lastThrottleTime) {
        this.lastThrottleTime = lastThrottleTime;
    }

    @SdkTestInternalApi
    synchronized double getMeasuredTxRate() {
        return this.measuredTxRate;
    }

    @SdkTestInternalApi
    synchronized double getFillRate() {
        return this.fillRate;
    }

    @SdkTestInternalApi
    synchronized void setCurrentCapacity(double currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    @SdkTestInternalApi
    synchronized double getCurrentCapacity() {
        return this.currentCapacity;
    }

    @SdkTestInternalApi
    synchronized void setFillRate(double fillRate) {
        this.fillRate = fillRate;
    }

    static class DefaultClock
    implements Clock {
        DefaultClock() {
        }

        @Override
        public double time() {
            long timeMillis = System.nanoTime();
            return (double)timeMillis / 1.0E9;
        }
    }

    public static interface Clock {
        public double time();
    }
}

