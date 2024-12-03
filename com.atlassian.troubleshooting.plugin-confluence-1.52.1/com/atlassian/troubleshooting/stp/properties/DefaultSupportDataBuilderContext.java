/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.properties;

import com.atlassian.troubleshooting.stp.spi.SupportDataBuilderContext;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.Map;
import javax.annotation.Nonnull;

public class DefaultSupportDataBuilderContext
implements SupportDataBuilderContext {
    private final SupportDataDetail detail;
    private final Date start;
    private final Map<String, Object> properties;

    public DefaultSupportDataBuilderContext(@Nonnull SupportDataDetail detail, @Nonnull Date start) {
        this.detail = (SupportDataDetail)((Object)Preconditions.checkNotNull((Object)((Object)detail), (Object)"detail"));
        this.start = (Date)Preconditions.checkNotNull((Object)start, (Object)"start");
        this.properties = Maps.newHashMap();
    }

    public DefaultSupportDataBuilderContext(@Nonnull SupportDataDetail detail) {
        this(detail, new Date());
    }

    @Override
    public boolean removeProperty(String name) {
        return this.properties.remove(name) != null;
    }

    @Override
    public SupportDataDetail getRequestDetail() {
        return this.detail;
    }

    @Override
    public Date getStart() {
        return this.start;
    }

    @Override
    public int getProperty(String name, int defaultValue) {
        Number value = (Number)this.properties.get(name);
        return value == null ? defaultValue : value.intValue();
    }

    @Override
    public long getProperty(String name, long defaultValue) {
        Number value = (Number)this.properties.get(name);
        return value == null ? defaultValue : value.longValue();
    }

    @Override
    public float getProperty(String name, float defaultValue) {
        Number value = (Number)this.properties.get(name);
        return value == null ? defaultValue : value.floatValue();
    }

    @Override
    public double getProperty(String name, double defaultValue) {
        Number value = (Number)this.properties.get(name);
        return value == null ? defaultValue : value.doubleValue();
    }

    @Override
    public boolean getProperty(String name, boolean defaultValue) {
        Boolean value = (Boolean)this.properties.get(name);
        return value == null ? defaultValue : value;
    }

    @Override
    public <T> T getProperty(String name, T defaultValue) {
        Object value = this.properties.get(name);
        return (T)(value == null ? defaultValue : value);
    }

    @Override
    public void setProperty(String name, int value) {
        this.properties.put(name, value);
    }

    @Override
    public void setProperty(String name, long value) {
        this.properties.put(name, value);
    }

    @Override
    public void setProperty(String name, float value) {
        this.properties.put(name, Float.valueOf(value));
    }

    @Override
    public void setProperty(String name, double value) {
        this.properties.put(name, value);
    }

    @Override
    public void setProperty(String name, boolean value) {
        this.properties.put(name, value);
    }

    @Override
    public <T> void setProperty(String name, T value) {
        this.properties.put(name, value);
    }
}

