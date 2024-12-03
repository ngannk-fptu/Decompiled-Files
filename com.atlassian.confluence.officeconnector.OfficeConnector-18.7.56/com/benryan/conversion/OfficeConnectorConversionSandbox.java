/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxRegistry
 *  com.atlassian.confluence.util.sandbox.SandboxSpec
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.benryan.conversion;

import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxRegistry;
import com.atlassian.confluence.util.sandbox.SandboxSpec;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.time.Duration;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="officeConnectorConversionSandbox")
public class OfficeConnectorConversionSandbox
implements Sandbox {
    private static final Logger log = LoggerFactory.getLogger(OfficeConnectorConversionSandbox.class);
    private static final int MEMORY_LIMIT_MEGABYTES = Integer.getInteger("document.conversion.sandbox.memory.requirement.megabytes", 128);
    private static final int REQUEST_TIME_LIMIT_SECS = Integer.getInteger("document.conversion.sandbox.request.time.limit.secs", 30);
    private final Sandbox delegate;

    @Autowired
    public OfficeConnectorConversionSandbox(@ComponentImport SandboxRegistry sandboxRegistry, @ComponentImport EventPublisher eventPublisher) {
        this.delegate = Objects.requireNonNull(sandboxRegistry).get(SandboxSpec.builder().withMinimumMemoryInMb(MEMORY_LIMIT_MEGABYTES).build(Duration.ofSeconds(REQUEST_TIME_LIMIT_SECS)));
    }

    public <T, R> R execute(SandboxTask<T, R> sandboxTask, T input) {
        return this.execute(sandboxTask, input, Duration.ofSeconds(REQUEST_TIME_LIMIT_SECS));
    }

    public <T, R> R execute(SandboxTask<T, R> sandboxTask, T input, Duration timeLimit) {
        long currentTimestamp = System.currentTimeMillis();
        return (R)this.delegate.execute(sandboxTask, input, timeLimit);
    }
}

