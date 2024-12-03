/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.johnson.config;

import com.atlassian.johnson.event.ApplicationEventCheck;
import com.atlassian.johnson.event.EventCheck;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventType;
import com.atlassian.johnson.event.RequestEventCheck;
import com.atlassian.johnson.setup.ContainerFactory;
import com.atlassian.johnson.setup.SetupConfig;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JohnsonConfig {
    @Nonnull
    public List<ApplicationEventCheck> getApplicationEventChecks();

    @Nonnull
    public ContainerFactory getContainerFactory();

    @Nonnull
    public String getErrorPath();

    @Nullable
    public EventCheck getEventCheck(int var1);

    @Nonnull
    public List<EventCheck> getEventChecks();

    @Nullable
    public EventLevel getEventLevel(@Nonnull String var1);

    @Nullable
    public EventType getEventType(@Nonnull String var1);

    @Nonnull
    public List<String> getIgnorePaths();

    @Nonnull
    public Map<String, String> getParams();

    @Nonnull
    public List<RequestEventCheck> getRequestEventChecks();

    @Nonnull
    public SetupConfig getSetupConfig();

    @Nonnull
    public String getSetupPath();

    public boolean isIgnoredPath(@Nonnull String var1);
}

