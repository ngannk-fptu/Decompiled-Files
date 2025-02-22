/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public abstract class Event {
    private final Optional<Mark> startMark;
    private final Optional<Mark> endMark;

    public Event(Optional<Mark> startMark, Optional<Mark> endMark) {
        if (startMark.isPresent() && !endMark.isPresent() || !startMark.isPresent() && endMark.isPresent()) {
            throw new NullPointerException("Both marks must be either present or absent.");
        }
        this.startMark = startMark;
        this.endMark = endMark;
    }

    public Event() {
        this(Optional.empty(), Optional.empty());
    }

    public Optional<Mark> getStartMark() {
        return this.startMark;
    }

    public Optional<Mark> getEndMark() {
        return this.endMark;
    }

    public abstract ID getEventId();

    public static enum ID {
        Alias,
        DocumentEnd,
        DocumentStart,
        MappingEnd,
        MappingStart,
        Scalar,
        SequenceEnd,
        SequenceStart,
        StreamEnd,
        StreamStart;

    }
}

