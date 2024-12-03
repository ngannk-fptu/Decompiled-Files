/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backgroundjob.domain;

import java.io.Serializable;
import java.time.Instant;

public class BackgroundJob
implements Serializable {
    private static final long serialVersionUID = -7284939308866857068L;
    private Long id;
    private String type;
    private String description;
    private String owner;
    private String parameters;
    private int iterationNumber = 0;
    private int numberOfFailures = 0;
    private Instant creationTime;
    private Instant lastTouchTime;
    private Instant runAt;
    private long duration = 0L;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParameters() {
        return this.parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public int getIterationNumber() {
        return this.iterationNumber;
    }

    public void setIterationNumber(int iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public int getNumberOfFailures() {
        return this.numberOfFailures;
    }

    public void setNumberOfFailures(int numberOfFailures) {
        this.numberOfFailures = numberOfFailures;
    }

    public Instant getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getLastTouchTime() {
        return this.lastTouchTime;
    }

    public void setLastTouchTime(Instant lastTouchTime) {
        this.lastTouchTime = lastTouchTime;
    }

    public Instant getRunAt() {
        return this.runAt;
    }

    public void setRunAt(Instant runAt) {
        this.runAt = runAt;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}

