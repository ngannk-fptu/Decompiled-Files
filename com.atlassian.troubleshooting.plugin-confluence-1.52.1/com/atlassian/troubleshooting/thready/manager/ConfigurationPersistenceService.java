/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.thready.manager;

import com.atlassian.troubleshooting.thready.manager.ThreadDiagnosticsConfigurationManager;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface ConfigurationPersistenceService {
    public Optional<ThreadDiagnosticsConfigurationManager.Configuration> findConfiguration();

    public ThreadDiagnosticsConfigurationManager.Configuration storeConfiguration(@Nonnull ThreadDiagnosticsConfigurationManager.Configuration var1);
}

