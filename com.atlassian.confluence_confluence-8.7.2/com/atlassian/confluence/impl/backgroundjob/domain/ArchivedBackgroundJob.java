/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backgroundjob.domain;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.impl.backgroundjob.domain.BackgroundJobState;
import java.io.Serializable;
import java.time.Instant;

public class ArchivedBackgroundJob
implements Serializable,
NotExportable {
    private static final long serialVersionUID = -3853226768951564300L;
    private Long id;
    private String type;
    private String description;
    private String owner;
    private String errorMessage;
    private int iterationNumber = 0;
    private int numberOfFailures = 0;
    private Instant creationTime;
    private Instant completionTime;
    private BackgroundJobState state;
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

    public BackgroundJobState getState() {
        return this.state;
    }

    public void setState(BackgroundJobState state) {
        this.state = state;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Instant getCompletionTime() {
        return this.completionTime;
    }

    public void setCompletionTime(Instant completionTime) {
        this.completionTime = completionTime;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}

