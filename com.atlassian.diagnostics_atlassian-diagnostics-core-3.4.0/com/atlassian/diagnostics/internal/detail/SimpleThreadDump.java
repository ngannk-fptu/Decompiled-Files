/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.detail.ThreadDump
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.diagnostics.internal.detail;

import com.atlassian.diagnostics.detail.ThreadDump;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class SimpleThreadDump
implements ThreadDump {
    private final long id;
    private final String name;
    private final boolean daemon;
    private final Thread.State state;
    private final String stackTrace;

    public SimpleThreadDump(Thread thread, String stackTrace) {
        this.stackTrace = stackTrace;
        this.id = thread.getId();
        this.name = thread.getName();
        this.daemon = thread.isDaemon();
        this.state = thread.getState();
    }

    public SimpleThreadDump(@JsonProperty(value="id") long id, @JsonProperty(value="name") String name, @JsonProperty(value="daemon") boolean daemon, @JsonProperty(value="state") String state, @JsonProperty(value="stackTrace") String stackTrace) {
        this.id = id;
        this.name = name;
        this.daemon = daemon;
        this.state = Thread.State.valueOf(state);
        this.stackTrace = stackTrace;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SimpleThreadDump that = (SimpleThreadDump)o;
        return this.id == that.id && this.daemon == that.daemon && Objects.equals(this.name, that.name) && this.state == that.state && Objects.equals(this.stackTrace, that.stackTrace);
    }

    public long getId() {
        return this.id;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public Thread.State getState() {
        return this.state;
    }

    @JsonIgnore
    @Nonnull
    public Optional<String> getStackTrace() {
        return Optional.ofNullable(this.stackTrace);
    }

    @JsonProperty(value="stackTrace")
    private String getStackTraceRaw() {
        return this.stackTrace;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.name, this.daemon, this.state, this.stackTrace});
    }

    public boolean isDaemon() {
        return this.daemon;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", this.id).add("name", (Object)this.name).add("daemon", this.daemon).add("state", (Object)this.state).add("stackTrace", (Object)this.stackTrace).toString();
    }
}

