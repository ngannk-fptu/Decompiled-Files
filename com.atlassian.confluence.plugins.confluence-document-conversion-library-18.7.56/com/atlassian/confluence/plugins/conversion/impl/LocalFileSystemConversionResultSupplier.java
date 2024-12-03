/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentDataNotFoundException
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType
 *  com.atlassian.confluence.setup.settings.ConfluenceDirectories
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.capabilities.api.AppWithCapabilities
 *  com.atlassian.plugins.capabilities.api.CapabilityService
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionRequest
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionResponse
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionTask
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionType
 *  com.google.common.base.Stopwatch
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.plugins.conversion.api.ConversionResult;
import com.atlassian.confluence.plugins.conversion.api.ConversionResultSupplier;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.conversion.api.LocalFileSystemConversionResult;
import com.atlassian.confluence.plugins.conversion.impl.AttachmentDataTempFile;
import com.atlassian.confluence.plugins.conversion.impl.ConfigurationProperties;
import com.atlassian.confluence.plugins.conversion.impl.FileSystemConversionState;
import com.atlassian.confluence.plugins.conversion.impl.TimeoutConversionRunnable;
import com.atlassian.confluence.plugins.conversion.impl.runnable.JVMConversionRunnable;
import com.atlassian.confluence.plugins.conversion.impl.runnable.MemoryReserveService;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.DocumentConversionSandboxEvent;
import com.atlassian.confluence.plugins.conversion.impl.sandbox.SandboxConversionFeature;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.capabilities.api.AppWithCapabilities;
import com.atlassian.plugins.capabilities.api.CapabilityService;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionRequest;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionResponse;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionTask;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionType;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component(value="localFileSystemConversionResultSupplier")
public class LocalFileSystemConversionResultSupplier
extends ConversionResultSupplier {
    private static final Logger log = LoggerFactory.getLogger(LocalFileSystemConversionResultSupplier.class);
    private static final int SANDBOX_THREADS = Integer.getInteger("document.conversion.sandbox.launcher.threads", 4);
    private static final int INITIAL_THREADS = 1;
    private static final int N_THREADS_WAIT = ConfigurationProperties.getInt(ConfigurationProperties.PROP_NUM_THREADS_WAIT);
    private static final int N_THREADS_THUMBNAIL_WAIT = ConfigurationProperties.getInt(ConfigurationProperties.PROP_NUM_THUMBNAIL_THREADS_WAIT);
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(N_THREADS_WAIT), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("conversion-thread-%d").setPriority(1).build(), new ThreadPoolExecutor.AbortPolicy());
    private static final ThreadPoolExecutor executorPdfThumbnail = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(N_THREADS_THUMBNAIL_WAIT), new ThreadFactoryBuilder().setDaemon(true).setNameFormat("conversion-thread-pdf-thumb-%d").setPriority(1).build(), new ThreadPoolExecutor.AbortPolicy());
    private static final Map<ConversionType, String> CONVERSION_LOCK_PREFIXES = new HashMap<ConversionType, String>(){
        {
            for (ConversionType type : ConversionType.values()) {
                this.put(type, "document.conversion.lock." + type.name().toLowerCase() + ".");
            }
        }
    };
    private static final long CONVERSION_TIMEOUT = TimeUnit.MINUTES.toSeconds(4L);
    private final AttachmentManager attachmentManager;
    private final ClusterLockService clusterLockService;
    private final CapabilityService capabilityService;
    private final MemoryReserveService memoryReserveService;
    private final ConfluenceDirectories confluenceDirectories;
    private final SandboxConversionFeature sandboxConversionFeature;
    private final EventPublisher eventPublisher;
    private final ThreadPoolExecutor conversionExecutor;
    private final Sandbox sandbox;

    @Autowired
    public LocalFileSystemConversionResultSupplier(@ComponentImport AttachmentManager attachmentManager, @ComponentImport ClusterLockService clusterLockService, @ComponentImport CapabilityService capabilityService, @ComponentImport EventPublisher eventPublisher, @ComponentImport ConfluenceDirectories confluenceDirectories, @Qualifier(value="delegatingDocumentConversionSandbox") Sandbox sandbox, SandboxConversionFeature sandboxConversionFeature, MemoryReserveService memoryReserveService) {
        this.attachmentManager = attachmentManager;
        this.clusterLockService = clusterLockService;
        this.capabilityService = capabilityService;
        this.confluenceDirectories = confluenceDirectories;
        this.memoryReserveService = memoryReserveService;
        this.sandboxConversionFeature = Objects.requireNonNull(sandboxConversionFeature);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.sandbox = Objects.requireNonNull(sandbox);
        AtomicInteger threadNumber = new AtomicInteger(0);
        this.conversionExecutor = new ThreadPoolExecutor(SANDBOX_THREADS, SANDBOX_THREADS, 15L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(), r -> new Thread(r, "document-conversion-sandbox-launcher-" + threadNumber.incrementAndGet()));
        Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    public void preDestroy() {
        this.conversionExecutor.shutdownNow();
    }

    @Override
    public ConversionResult getConversionResult(Attachment attachment, ConversionType conversionType) {
        FileSystemConversionState conversionState = new FileSystemConversionState(attachment, conversionType);
        AppWithCapabilities hostApp = this.capabilityService.getHostApplication();
        boolean conversionsEnabled = hostApp.hasCapability(ConfigurationProperties.PROP_CAPABILITY.toString());
        if (conversionsEnabled && !conversionState.isConverted() && !conversionState.isError()) {
            if (this.sandboxConversionFeature.isEnable().booleanValue()) {
                this.performConversionInSandbox(attachment, conversionType, conversionState);
            } else {
                this.performNormalConversion(attachment, conversionType, conversionState);
            }
        }
        return new LocalFileSystemConversionResult(conversionType, attachment, conversionState.getStatus(), this.conversionManager.getConversionUrl(attachment.getId(), attachment.getVersion(), conversionType), conversionState.getConvertedFile());
    }

    private void performNormalConversion(final Attachment attachment, final ConversionType conversionType, FileSystemConversionState conversionState) {
        try {
            TimeoutConversionRunnable conversionTask = new TimeoutConversionRunnable(attachment, conversionType, this.clusterLockService, this.getConversionRunnable(attachment, conversionType, conversionState), conversionState, LocalFileSystemConversionResultSupplier.getConversionLockPrefix(conversionType), CONVERSION_TIMEOUT){

                @Override
                public void run() {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    super.run();
                    log.debug("Convert attachment {} to {} takes {}", new Object[]{attachment.getId(), conversionType, stopwatch});
                }
            };
            if (ConversionType.THUMBNAIL.equals((Object)conversionType) && FileFormat.PDF.equals((Object)this.conversionManager.getFileFormat(attachment))) {
                this.queueIfNotDuplicate(executorPdfThumbnail, conversionTask);
            } else {
                this.queueIfNotDuplicate(executor, conversionTask);
            }
        }
        catch (RejectedExecutionException e) {
            log.warn("The conversion service is currently busy.", (Throwable)e);
            conversionState.markAsBusy();
        }
    }

    private void performConversionInSandbox(final Attachment attachment, ConversionType conversionType, FileSystemConversionState conversionState) {
        FileFormat fileFormat = this.conversionManager.getFileFormat(attachment);
        if (fileFormat != null && (conversionType == ConversionType.THUMBNAIL || conversionType == ConversionType.DOCUMENT)) {
            try {
                AttachmentDataTempFile attachmentData = AttachmentDataTempFile.extract(this.confluenceDirectories.getTempDirectory(), this.attachmentManager.getAttachmentDao().getDataDao(), attachment, AttachmentDataStreamType.RAW_BINARY);
                SandboxConversionRequest conversionRequest = new SandboxConversionRequest(attachmentData.getFile().toFile(), fileFormat, conversionState.getTempFile(), conversionState.getConvertedFile(), conversionState.getErrorFile(), SandboxConversionType.valueOf((String)conversionType.name()), AuthenticatedUserThreadLocal.getUsername(), attachment.getFileName()){

                    public String toString() {
                        return new ToStringBuilder((Object)this).append("attachment", (Object)attachment.getDownloadPath()).append("conversionType", (Object)this.getConversionType().name()).toString();
                    }
                };
                Stopwatch stopwatch = Stopwatch.createStarted();
                ((CompletableFuture)CompletableFuture.supplyAsync(() -> (SandboxConversionResponse)this.sandbox.execute((SandboxTask)new SandboxConversionTask(), (Object)conversionRequest), this.conversionExecutor).handleAsync((r, e) -> {
                    if (e != null) {
                        log.warn("Error when performing conversion {} in the sandbox", (Object)conversionRequest, e);
                        conversionState.markAsError();
                        throw new RuntimeException((Throwable)e);
                    }
                    if (r != null && r.getStatus() == SandboxConversionStatus.ERROR) {
                        log.warn("Sandbox returned erroneous conversion status on {}", (Object)conversionRequest);
                        conversionState.markAsError();
                        return r;
                    }
                    log.debug("Convert attachment {} to {} takes {}", new Object[]{attachment.getId(), conversionType, stopwatch});
                    DocumentConversionSandboxEvent conversionEvent = new DocumentConversionSandboxEvent(attachment.hashCode(), attachment.getFileSize(), conversionRequest.getFileFormat(), conversionRequest.getConversionType(), r == null ? "" : r.getStatus().name(), SandboxConversionFeature.REQUEST_TIME_LIMIT_SECS, stopwatch.elapsed(TimeUnit.SECONDS));
                    this.eventPublisher.publish((Object)conversionEvent);
                    return r;
                })).whenComplete((r, th) -> attachmentData.close());
            }
            catch (AttachmentDataNotFoundException | IOException e2) {
                throw new RuntimeException(e2);
            }
        } else {
            log.info("Can't perform conversion {} to {}", (Object)attachment.getMediaType(), (Object)conversionType);
            conversionState.markAsError();
        }
    }

    private void queueIfNotDuplicate(ThreadPoolExecutor threadPoolExecutor, Runnable conversionTask) {
        if (!threadPoolExecutor.getQueue().contains(conversionTask)) {
            threadPoolExecutor.execute(conversionTask);
        }
    }

    private static String getConversionLockPrefix(ConversionType conversionType) {
        return CONVERSION_LOCK_PREFIXES.get((Object)conversionType);
    }

    private Runnable getConversionRunnable(Attachment attachment, ConversionType conversionType, FileSystemConversionState conversionState) {
        return new JVMConversionRunnable(conversionState, attachment, this.conversionManager.getFileFormat(attachment), this.attachmentManager, conversionType, this.conversionManager.getConverters(), this.memoryReserveService);
    }

    public void setJvmThreadPoolSize(int jvmThreadPoolSize) {
        executor.setCorePoolSize(jvmThreadPoolSize);
        executor.setMaximumPoolSize(jvmThreadPoolSize);
    }

    public int getJvmThreadPoolSize() {
        return executor.getCorePoolSize();
    }

    public int getConversionQueueSize() {
        return executor.getQueue().size();
    }
}

