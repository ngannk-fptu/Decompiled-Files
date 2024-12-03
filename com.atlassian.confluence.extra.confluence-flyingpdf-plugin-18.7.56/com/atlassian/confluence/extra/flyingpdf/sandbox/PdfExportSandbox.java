/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.resource.DownloadResourceManager
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxRegistry
 *  com.atlassian.confluence.util.sandbox.SandboxSpec
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxRegistry;
import com.atlassian.confluence.util.sandbox.SandboxSpec;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.time.Duration;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
class PdfExportSandbox {
    private Sandbox sandbox;
    private final SandboxRegistry sandboxRegistry;
    private final DownloadResourceManager downloadResourceManager;
    private static final int MEMORY_REQUIREMENT = Integer.getInteger("pdf.export.sandbox.memory.requirement.megabytes", 64);

    PdfExportSandbox(@ComponentImport SandboxRegistry sandboxRegistry, @ComponentImport DownloadResourceManager downloadResourceManager) {
        this.sandboxRegistry = sandboxRegistry;
        this.downloadResourceManager = downloadResourceManager;
    }

    Duration requestTimeLimit() {
        return Duration.ofSeconds(Integer.getInteger("pdf.export.sandbox.request.time.limit.secs", 180).intValue());
    }

    @PostConstruct
    public void postConstruct() {
        this.sandbox = this.sandboxRegistry.get(SandboxSpec.builder().withMinimumMemoryInMb(MEMORY_REQUIREMENT).registerCallbackContextObject(DownloadResourceManager.class, (Object)this.downloadResourceManager).build(this.requestTimeLimit()));
    }

    public <R, T> R execute(SandboxTask<T, R> task, T request) {
        return (R)this.sandbox.execute(task, request);
    }
}

