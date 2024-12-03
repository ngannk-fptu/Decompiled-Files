/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.capabilities.api.AppWithCapabilities
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 *  org.joda.time.DateTime
 */
package com.atlassian.plugins.navlink.producer.capabilities;

import com.atlassian.plugins.capabilities.api.AppWithCapabilities;
import com.atlassian.plugins.navlink.producer.capabilities.CapabilityKey;
import com.atlassian.plugins.navlink.util.date.JodaDateToJavaTimeUtil;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import org.joda.time.DateTime;

@Immutable
public class ApplicationWithCapabilities
implements AppWithCapabilities {
    protected static final ZonedDateTime NULL_DATE = Instant.ofEpochSecond(0L).atZone(ZoneOffset.UTC);
    protected final String id;
    protected final String name;
    protected final String type;
    protected final ZonedDateTime buildDate;
    protected final Map<String, String> capabilities;

    public ApplicationWithCapabilities(@Nullable String type, @Nullable ZonedDateTime buildDate, Map<String, String> capabilities) {
        this.id = null;
        this.type = type;
        this.name = null;
        this.buildDate = this.toNonnull(buildDate);
        this.capabilities = Collections.unmodifiableMap(new HashMap<String, String>(capabilities));
    }

    private ZonedDateTime toNonnull(ZonedDateTime buildDate) {
        return buildDate != null ? buildDate : NULL_DATE;
    }

    @Deprecated
    public String getId() {
        return this.id;
    }

    @Deprecated
    public String getName() {
        return this.name;
    }

    @Nonnull
    public String getType() {
        return this.type;
    }

    @Nonnull
    @Deprecated
    public DateTime getBuildDate() {
        return JodaDateToJavaTimeUtil.javaTimeToJoda(this.buildDate);
    }

    @Nonnull
    public ZonedDateTime getBuildDateTime() {
        return this.buildDate;
    }

    public boolean hasCapabilities() {
        return !this.capabilities.isEmpty();
    }

    public boolean hasCapability(CapabilityKey key) {
        return this.capabilities.containsKey(key.getKey());
    }

    @Nullable
    public String getCapabilityUrl(CapabilityKey key) {
        return this.getCapabilityUrl(key.getKey());
    }

    @Deprecated
    @Nonnull
    public ImmutableMap<String, String> getCapabilities() {
        return ImmutableMap.copyOf(this.capabilities);
    }

    @Nonnull
    public Map<String, String> getAllCapabilities() {
        return this.capabilities;
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.type, this.buildDate);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ApplicationWithCapabilities)) {
            return false;
        }
        ApplicationWithCapabilities that = (ApplicationWithCapabilities)obj;
        return Objects.equals(this.type, that.type) && Objects.equals(this.name, that.name) && Objects.equals(this.id, that.id) && Objects.equals(this.buildDate, that.buildDate);
    }

    public String toString() {
        return "ApplicationWithCapabilities{id='" + this.id + '\'' + ", name='" + this.name + '\'' + ", type='" + this.type + '\'' + ", buildDate='" + this.buildDate + '\'' + ", capabilities=" + this.capabilities + '}';
    }

    public boolean hasCapability(String key) {
        return this.capabilities.containsKey(key);
    }

    @Nullable
    public String getCapabilityUrl(String key) {
        return this.capabilities.get(key);
    }
}

