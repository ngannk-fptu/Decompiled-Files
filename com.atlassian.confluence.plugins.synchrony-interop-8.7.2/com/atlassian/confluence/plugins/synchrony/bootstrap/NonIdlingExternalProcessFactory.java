/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.utils.process.DefaultExternalProcessFactory
 *  com.atlassian.utils.process.ExternalProcess
 *  com.atlassian.utils.process.ExternalProcessSettings
 *  javax.annotation.PreDestroy
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.confluence.plugins.synchrony.bootstrap.NonIdlingExternalProcess;
import com.atlassian.utils.process.DefaultExternalProcessFactory;
import com.atlassian.utils.process.ExternalProcess;
import com.atlassian.utils.process.ExternalProcessSettings;
import javax.annotation.PreDestroy;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Component;

@Component
public class NonIdlingExternalProcessFactory
extends DefaultExternalProcessFactory {
    public @NonNull ExternalProcess create(@NonNull ExternalProcessSettings settings) {
        ExternalProcess externalProcess = super.create(settings);
        return new NonIdlingExternalProcess(externalProcess);
    }

    @PreDestroy
    public void shutdown() {
        super.shutdown();
    }
}

