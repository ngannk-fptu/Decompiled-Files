/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxCrashedException
 *  com.atlassian.confluence.util.sandbox.SandboxRegistry
 *  com.atlassian.confluence.util.sandbox.SandboxSpec
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.sandbox.SandboxTimeoutException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl.sandbox;

import com.atlassian.confluence.plugins.conversion.impl.sandbox.DocumentConversionSandboxEvent;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.SandboxConversionFeature;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.SandboxErrorType;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.SandboxHolder;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.SandboxMonitor;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxCrashedException;
import com.atlassian.confluence.util.sandbox.SandboxRegistry;
import com.atlassian.confluence.util.sandbox.SandboxSpec;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.sandbox.SandboxTimeoutException;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalDocumentConversionSandbox
implements Sandbox {
    private static final Logger log = LoggerFactory.getLogger(LocalDocumentConversionSandbox.class);
    private final Sandbox delegate;
    private final SandboxMonitor sandboxMonitor;
    private final AnalyticEventProducer analyticEventProducer;

    @Autowired
    public LocalDocumentConversionSandbox(@ComponentImport SandboxRegistry sandboxRegistry, @ComponentImport EventPublisher eventPublisher, SandboxMonitor sandboxMonitor) {
        this.sandboxMonitor = sandboxMonitor;
        this.analyticEventProducer = new AnalyticEventProducer(Objects.requireNonNull(eventPublisher));
        this.delegate = Objects.requireNonNull(sandboxRegistry).get(SandboxSpec.builder().withMinimumMemoryInMb(SandboxConversionFeature.MEMORY_LIMIT_MEGABYTES).build(Duration.ofSeconds(SandboxConversionFeature.REQUEST_TIME_LIMIT_SECS)));
        SandboxHolder.getInstance().setSandbox(this.delegate);
    }

    public <T, R> R execute(SandboxTask<T, R> sandboxTask, T input) {
        return this.execute(sandboxTask, input, Duration.ofSeconds(SandboxConversionFeature.REQUEST_TIME_LIMIT_SECS));
    }

    public <T, R> R execute(SandboxTask<T, R> sandboxTask, T input, Duration timeLimit) {
        long currentTimestamp = System.currentTimeMillis();
        try {
            return (R)this.delegate.execute(sandboxTask, input, timeLimit);
        }
        catch (SandboxCrashedException | SandboxTimeoutException e) {
            SandboxErrorType eventType = e instanceof SandboxCrashedException ? SandboxErrorType.CRASHED : SandboxErrorType.KILLED;
            Duration currentDuration = Duration.ofMillis(System.currentTimeMillis() - currentTimestamp);
            this.sandboxMonitor.alert(eventType, input, currentDuration);
            this.analyticEventProducer.alert(eventType, input, currentDuration);
            throw e;
        }
    }

    static long getFileSize(File inputFile) {
        try {
            return Files.size(inputFile.toPath());
        }
        catch (IOException ignore) {
            log.warn("Can't get size of file {}", (Object)inputFile.getAbsolutePath());
            return -1L;
        }
    }

    private static class AnalyticEventProducer {
        private final EventPublisher eventPublisher;

        private AnalyticEventProducer(EventPublisher eventPublisher) {
            this.eventPublisher = eventPublisher;
        }

        public void alert(SandboxErrorType eventType, Object input, Duration duration) {
            if (input instanceof SandboxConversionRequest) {
                SandboxConversionRequest conversionRequest = (SandboxConversionRequest)input;
                DocumentConversionSandboxEvent conversionEvent = new DocumentConversionSandboxEvent(conversionRequest.getInputFile().getAbsolutePath().hashCode(), LocalDocumentConversionSandbox.getFileSize(conversionRequest.getInputFile()), conversionRequest.getFileFormat(), conversionRequest.getConversionType(), eventType.name(), SandboxConversionFeature.REQUEST_TIME_LIMIT_SECS, duration.getSeconds());
                this.eventPublisher.publish((Object)conversionEvent);
            }
        }
    }
}

