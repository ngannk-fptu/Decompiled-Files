/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.Buffer
 *  org.apache.commons.collections.BufferUtils
 *  org.apache.commons.collections.buffer.CircularFifoBuffer
 */
package com.atlassian.confluence.jmx;

import com.atlassian.confluence.jmx.CurrentTimeFacade;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class RequestMetrics {
    private AtomicInteger requestsServed = new AtomicInteger(0);
    private AtomicInteger requestsBegan = new AtomicInteger(0);
    private final Buffer lastTenRequestTimes = BufferUtils.synchronizedBuffer((Buffer)new CircularFifoBuffer(10));
    private final Buffer endTimesOfRequests = BufferUtils.synchronizedBuffer((Buffer)new CircularFifoBuffer(255));
    private static AtomicInteger errorCount = new AtomicInteger(0);

    public int getRequestsServed() {
        return this.requestsServed.get();
    }

    public int getRequestsBegan() {
        return this.requestsBegan.get();
    }

    public void beginRequest() {
        this.requestsBegan.incrementAndGet();
    }

    public void endRequest() {
        this.requestsServed.incrementAndGet();
        this.endTimesOfRequests.add((Object)CurrentTimeFacade.getCurrentTime().getTime());
    }

    public int getCurrentNumberOfRequestsBeingServed() {
        return this.requestsBegan.get() - this.requestsServed.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getAverageExecutionTimeForLastTenRequests() {
        int total = 0;
        Buffer buffer = this.lastTenRequestTimes;
        synchronized (buffer) {
            for (Object lastTenRequestTime : this.lastTenRequestTimes) {
                Long time = (Long)lastTenRequestTime;
                total = (int)((long)total + time);
            }
            if (total == 0) {
                return 0;
            }
            return total / this.lastTenRequestTimes.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNumberOfRequestsInLastTenSeconds() {
        long currentTime = CurrentTimeFacade.getCurrentTime().getTime();
        int totalInLastTenSeconds = 0;
        Buffer buffer = this.endTimesOfRequests;
        synchronized (buffer) {
            for (Object endTimesOfRequest : this.endTimesOfRequests) {
                Long requestServedAt = (Long)endTimesOfRequest;
                long ago = currentTime - requestServedAt;
                if (ago >= 10000L) continue;
                ++totalInLastTenSeconds;
            }
        }
        return totalInLastTenSeconds;
    }

    public void recordRequestTime(long requestTime) {
        this.lastTenRequestTimes.add((Object)requestTime);
    }

    public static void incrementErrorCount() {
        errorCount.getAndIncrement();
    }

    public int getErrorCount() {
        return errorCount.intValue();
    }
}

