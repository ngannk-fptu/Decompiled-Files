/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.Instrument;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

public class SimpleTimer
implements Instrument {
    private final String name;
    private Optional<Duration> duration = Optional.empty();
    private OptionalLong start = OptionalLong.empty();

    public SimpleTimer(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public SimpleTimer(String name, Duration duration) {
        this.name = Objects.requireNonNull(name);
        this.duration = Optional.of(duration);
    }

    public SimpleTimer(String name, long nanoSeconds) {
        this.name = Objects.requireNonNull(name);
        this.duration = Optional.of(Duration.ofNanos(nanoSeconds));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getValue() {
        this.ensureState(this.duration.isPresent(), "no end time created");
        return this.duration.get().toNanos();
    }

    public void start() {
        this.ensureState(!this.duration.isPresent(), "timer already used");
        this.start = OptionalLong.of(System.nanoTime());
    }

    public void end() {
        this.ensureState(!this.duration.isPresent(), "end already called");
        this.ensureState(this.start.isPresent(), "start has not been called");
        this.duration = Optional.of(Duration.ofNanos(System.nanoTime() - this.start.getAsLong()));
    }

    public Duration getDuration() {
        this.ensureState(this.duration.isPresent(), "end has not been called");
        return this.duration.get();
    }

    @Override
    public int compareTo(Instrument instrument) {
        SimpleTimer other = (SimpleTimer)instrument;
        return other.getDuration().compareTo(this.getDuration());
    }

    private void ensureState(boolean condition, String errorMessage) {
        if (!condition) {
            throw new IllegalStateException("timer: " + this.getName() + ": " + errorMessage);
        }
    }
}

