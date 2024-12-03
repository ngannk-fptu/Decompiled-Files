/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.confluence.internal.diagnostics.ipd.filesystem;

import com.atlassian.annotations.VisibleForTesting;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

abstract class IpdFileWriteLatencyMeter {
    protected static final byte[] SAMPLE_DATA = "Lorem ipsum dolor sit amet".getBytes(StandardCharsets.UTF_8);
    protected final Random random;
    protected final int numberOfMeasurements;
    protected final Clock clock;

    protected IpdFileWriteLatencyMeter(int numberOfMeasurements) {
        this(numberOfMeasurements, new Random(), Clock.systemUTC());
    }

    @VisibleForTesting
    IpdFileWriteLatencyMeter(int numberOfMeasurements, Random random, Clock clock) {
        this.random = random;
        this.numberOfMeasurements = numberOfMeasurements;
        this.clock = clock;
    }

    public List<Duration> makeWriteLatencyMeasurements() throws IOException, InterruptedException {
        ArrayList<Duration> measurements = new ArrayList<Duration>(this.numberOfMeasurements);
        for (int i = 0; i < this.numberOfMeasurements; ++i) {
            Duration writeLatency = this.measureWriteLatency();
            measurements.add(writeLatency);
            this.waitBetweenFileOperations();
        }
        return measurements;
    }

    protected abstract Duration measureWriteLatency() throws IOException;

    private void waitBetweenFileOperations() throws InterruptedException {
        Thread.sleep(5L + (long)this.random.nextInt(15));
    }

    public static long getMedian(List<Long> measurements) {
        List sortedValues = measurements.stream().sorted().collect(Collectors.toList());
        int middleIndex = sortedValues.size() / 2;
        if (sortedValues.size() % 2 == 0) {
            return ((Long)sortedValues.get(middleIndex) + (Long)sortedValues.get(middleIndex - 1)) / 2L;
        }
        return (Long)sortedValues.get(middleIndex);
    }
}

