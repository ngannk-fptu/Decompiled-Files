/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  oshi.SystemInfo
 *  oshi.hardware.HardwareAbstractionLayer
 */
package com.atlassian.confluence.extra.office.canary;

import com.atlassian.confluence.extra.office.canary.CanaryCage;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.ThreadFactories;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public abstract class AbstractCanaryExecutor {
    private static final Logger log = LoggerFactory.getLogger(AbstractCanaryExecutor.class);
    private static final int MEMORY_ADDING = 500;
    private static final String DARK_FEATURE = "com.atlassian.confluence.officeconnector.canary";
    private static final int THREAD_POOL_KEEP_ALIVE_SECONDS = Integer.getInteger("com.atlassian.confluence.officeconnector.canary.threadPoolKeepAliveSeconds", 120);
    private static final int THREAD_POOL_WAIT_TIMEOUT_SECONDS = Integer.getInteger("com.atlassian.confluence.officeconnector.canary.threadPoolWaitTimeoutSeconds", 60);
    private static final int THREAD_POOL_SIZE = Integer.getInteger("com.atlassian.confluence.officeconnector.canary.threadPoolSize", 2);
    private static final int THREAD_POOL_QUEUE_SIZE = Integer.getInteger("com.atlassian.confluence.officeconnector.canary.threadPoolQueueSize", 100);
    private final LazyReference<CanaryCage> canaryCageRef;
    private final ExecutorService executorService = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, (long)THREAD_POOL_KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(THREAD_POOL_QUEUE_SIZE), ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName(), (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
    private final DarkFeatureManager darkFeatureManager;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public AbstractCanaryExecutor(@ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport ApplicationProperties applicationProperties) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
        this.canaryCageRef = this.getCanaryCageRef();
    }

    protected abstract LazyReference<CanaryCage> getCanaryCageRef();

    protected Path canaryCageDirectory() {
        return ((Path)this.applicationProperties.getLocalHomeDirectory().get()).resolve("temp").resolve(this.getClass().getName());
    }

    public CanaryCage.Result test(File docFile) {
        if (!this.isAvailable()) {
            throw new IllegalStateException("Canary cage is not available");
        }
        return this.internalTest(docFile);
    }

    private CanaryCage.Result internalTest(File docFile) {
        CanaryCage canaryCage = this.canaryCage();
        try {
            return this.executorService.submit(() -> canaryCage.test(docFile.getAbsolutePath())).get(THREAD_POOL_WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
        catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new RuntimeException(cause);
        }
        catch (InterruptedException ex) {
            log.warn("Interrupted whilst waiting for canary: {}", (Object)ex.getMessage());
            return CanaryCage.Result.UNKNOWN;
        }
        catch (TimeoutException ex) {
            log.warn("Time out whilst waiting for canary: {}", (Object)ex.getMessage());
            return CanaryCage.Result.UNKNOWN;
        }
    }

    public boolean verify(File docFile, String fileName) {
        if (this.isAvailable()) {
            CanaryCage.Result result = this.internalTest(docFile);
            switch (result) {
                case CHOKED_AND_DIED: {
                    log.error("Canary choked and died on doc file {}", (Object)fileName);
                    return false;
                }
                case HAPPY_CHEEPING: {
                    log.info("Canary seems happy with doc file {}", (Object)fileName);
                    return true;
                }
                case UNKNOWN: {
                    log.warn("Canary result unknown (possible timeout) for doc file {}", (Object)fileName);
                    return true;
                }
            }
            log.error("Canary result unhandled for doc file {}: {}", (Object)fileName, (Object)result);
            return false;
        }
        return true;
    }

    public boolean isAvailable() {
        boolean darkFeature = this.darkFeatureManager.isFeatureEnabledForCurrentUser(DARK_FEATURE);
        if (!darkFeature) {
            log.debug("Canary dark feature is not enabled");
        }
        return darkFeature && this.checkMemory();
    }

    private boolean checkMemory() {
        boolean result;
        long memoryValue = 0x100000L * (long)CanaryCage.MEMORY_VALUE;
        long requestMemory = memoryValue + 500L;
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        long availableMemory = hal.getMemory().getAvailable();
        boolean bl = result = availableMemory >= requestMemory;
        if (!result) {
            log.debug("Not enough memory to run the canary process. Request and available memory in byte are {}, {}", (Object)requestMemory, (Object)availableMemory);
        }
        return result;
    }

    private CanaryCage canaryCage() {
        return (CanaryCage)this.canaryCageRef.get();
    }
}

