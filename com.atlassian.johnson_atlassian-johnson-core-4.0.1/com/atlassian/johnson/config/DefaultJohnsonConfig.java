/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.johnson.config;

import com.atlassian.johnson.config.JohnsonConfig;
import com.atlassian.johnson.event.ApplicationEventCheck;
import com.atlassian.johnson.event.EventCheck;
import com.atlassian.johnson.event.EventLevel;
import com.atlassian.johnson.event.EventType;
import com.atlassian.johnson.event.RequestEventCheck;
import com.atlassian.johnson.setup.ContainerFactory;
import com.atlassian.johnson.setup.DefaultContainerFactory;
import com.atlassian.johnson.setup.DefaultSetupConfig;
import com.atlassian.johnson.setup.SetupConfig;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public final class DefaultJohnsonConfig
implements JohnsonConfig {
    private static final DefaultJohnsonConfig instance = new DefaultJohnsonConfig();
    private final ContainerFactory containerFactory = new DefaultContainerFactory();
    private final SetupConfig setupConfig = new DefaultSetupConfig();

    private DefaultJohnsonConfig() {
    }

    @Nonnull
    public static JohnsonConfig getInstance() {
        return instance;
    }

    @Override
    @Nonnull
    public List<ApplicationEventCheck> getApplicationEventChecks() {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public ContainerFactory getContainerFactory() {
        return this.containerFactory;
    }

    @Override
    @Nonnull
    public String getErrorPath() {
        return "/unavailable";
    }

    @Override
    public EventCheck getEventCheck(int id) {
        return null;
    }

    @Override
    @Nonnull
    public List<EventCheck> getEventChecks() {
        return Collections.emptyList();
    }

    @Override
    public EventLevel getEventLevel(@Nonnull String level) {
        Preconditions.checkNotNull((Object)level, (Object)"level");
        return null;
    }

    @Override
    public EventType getEventType(@Nonnull String type) {
        Preconditions.checkNotNull((Object)type, (Object)"type");
        return null;
    }

    @Override
    @Nonnull
    public List<String> getIgnorePaths() {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public Map<String, String> getParams() {
        return Collections.emptyMap();
    }

    @Override
    @Nonnull
    public List<RequestEventCheck> getRequestEventChecks() {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public SetupConfig getSetupConfig() {
        return this.setupConfig;
    }

    @Override
    @Nonnull
    public String getSetupPath() {
        return "/setup";
    }

    @Override
    public boolean isIgnoredPath(@Nonnull String uri) {
        Preconditions.checkNotNull((Object)uri, (Object)"uri");
        return true;
    }
}

