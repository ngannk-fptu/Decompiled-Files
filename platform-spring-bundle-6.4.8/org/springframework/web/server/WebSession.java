/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.web.server;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public interface WebSession {
    public String getId();

    public Map<String, Object> getAttributes();

    @Nullable
    default public <T> T getAttribute(String name) {
        return (T)this.getAttributes().get(name);
    }

    default public <T> T getRequiredAttribute(String name) {
        T value = this.getAttribute(name);
        Assert.notNull(value, () -> "Required attribute '" + name + "' is missing.");
        return value;
    }

    default public <T> T getAttributeOrDefault(String name, T defaultValue) {
        return (T)this.getAttributes().getOrDefault(name, defaultValue);
    }

    public void start();

    public boolean isStarted();

    public Mono<Void> changeSessionId();

    public Mono<Void> invalidate();

    public Mono<Void> save();

    public boolean isExpired();

    public Instant getCreationTime();

    public Instant getLastAccessTime();

    public void setMaxIdleTime(Duration var1);

    public Duration getMaxIdleTime();
}

