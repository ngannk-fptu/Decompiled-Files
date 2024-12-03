/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl.sandbox;

import com.atlassian.confluence.plugins.conversion.impl.sandbox.LocalDocumentConversionSandbox;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.RemoteDocumentConversionSandbox;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.time.Duration;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="delegatingDocumentConversionSandbox")
public class DelegatingDocumentConversionSandbox
implements Sandbox {
    private static final String DOCUMENT_CONVERSION_SANDBOX_ONCE_PER_CLUSTER_KEY = "document.conversion.sandbox.once.per.cluster";
    private final DarkFeaturesManager darkFeaturesManager;
    private final RemoteDocumentConversionSandbox remote;
    private final LocalDocumentConversionSandbox local;

    @Autowired
    public DelegatingDocumentConversionSandbox(@ComponentImport DarkFeaturesManager darkFeaturesManager, RemoteDocumentConversionSandbox remote, LocalDocumentConversionSandbox local) {
        this.darkFeaturesManager = Objects.requireNonNull(darkFeaturesManager);
        this.remote = Objects.requireNonNull(remote);
        this.local = Objects.requireNonNull(local);
    }

    public <T, R> R execute(SandboxTask<T, R> sandboxTask, T t) {
        return (R)this.getSandbox().execute(sandboxTask, t);
    }

    public <T, R> R execute(SandboxTask<T, R> sandboxTask, T t, Duration duration) {
        return (R)this.getSandbox().execute(sandboxTask, t, duration);
    }

    private Sandbox getSandbox() {
        if (this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(DOCUMENT_CONVERSION_SANDBOX_ONCE_PER_CLUSTER_KEY)) {
            return this.remote;
        }
        return this.local;
    }
}

