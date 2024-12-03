/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxCallback
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers$CompositeByteArraySerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers$DurationSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers$IntSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxSerializers$StringSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.sandbox.SandboxTaskContext
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.math.IntMath
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.AbstractSandboxPool;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessage;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessageType;
import com.atlassian.confluence.impl.util.sandbox.SandboxPoolConfiguration;
import com.atlassian.confluence.impl.util.sandbox.SandboxProcess;
import com.atlassian.confluence.impl.util.sandbox.SandboxRequest;
import com.atlassian.confluence.impl.util.sandbox.SandboxServer;
import com.atlassian.confluence.impl.util.sandbox.SandboxServerClassLoader;
import com.atlassian.confluence.impl.util.sandbox.SandboxServerContext;
import com.atlassian.confluence.impl.util.sandbox.SandboxServerWorker;
import com.atlassian.confluence.util.sandbox.SandboxCallback;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxSerializers;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.IntMath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SandboxLocalProcessPool
extends AbstractSandboxPool {
    private static final int ERROR_DELAY_MILLIS = Integer.getInteger("sandbox.error.delay.millis", 50);
    private static final int TERMINATION_TOLERANCE = Integer.getInteger("sandbox.termination.tolerance", 3);
    private static final boolean KEEP_WORKING_DIRECTORY = Boolean.getBoolean("sandbox.keep.working.directory");
    private static final List<Class<?>> SANDBOX_SERVER_CLASSES = ImmutableList.builder().add((Object[])new Class[]{SandboxProcess.class, SandboxTask.class, SandboxSerializer.class, SandboxSerializers.class, SandboxSerializers.IntSerializer.class, SandboxSerializers.StringSerializer.class, SandboxSerializers.DurationSerializer.class, SandboxSerializers.CompositeByteArraySerializer.class, SandboxMessage.ApplicationPayLoadSerializer.class, SandboxMessage.class, SandboxMessage.ApplicationPayload.class, SandboxMessageType.class, SandboxServer.class, SandboxServerWorker.class, SandboxServerClassLoader.class, SandboxServerContext.class, SandboxCallback.class, SandboxTaskContext.class}).addAll(Stream.of(SandboxMessageType.values()).map(Object::getClass).iterator()).build();
    private static final Logger logger = LoggerFactory.getLogger(SandboxLocalProcessPool.class);
    private final Path workingDirectory;
    private final SandboxProcess[] processes;
    private volatile boolean shutdown;
    private final Thread terminator;
    private final Thread errorLogger;

    SandboxLocalProcessPool(FilesystemPath directory, SandboxPoolConfiguration configuration) {
        super(configuration);
        this.processes = new SandboxProcess[configuration.getConcurrencyLevel()];
        this.shutdown = false;
        this.workingDirectory = SandboxLocalProcessPool.prepareWorkingDirectory(directory, configuration);
        for (int i = 0; i < configuration.getConcurrencyLevel(); ++i) {
            this.processes[i] = new SandboxProcess(configuration, this.workingDirectory, i);
        }
        this.terminator = this.startTerminator();
        this.errorLogger = this.startErrorLogger();
    }

    @Override
    public <T, R> R execute(SandboxRequest<T, R> request) {
        SandboxProcess process = this.processes[IntMath.mod((int)request.getInput().hashCode(), (int)this.configuration.getConcurrencyLevel())];
        return process.execute(request);
    }

    @Override
    public void shutdown() {
        this.shutdown = true;
        for (SandboxProcess process : this.processes) {
            process.flushStdError();
            process.shutdown();
        }
        this.terminator.interrupt();
        this.errorLogger.interrupt();
        if (!KEEP_WORKING_DIRECTORY) {
            try {
                FileUtils.deleteDirectory((File)this.workingDirectory.toFile());
            }
            catch (IOException e) {
                logger.error("Can't remove " + this.workingDirectory, (Throwable)e);
            }
        }
    }

    private Thread startTerminator() {
        Thread terminator = new Thread(() -> {
            while (!this.shutdown) {
                try {
                    Thread.sleep(this.getTerminatorActivationInMillis());
                    for (SandboxProcess process : this.processes) {
                        if (this.configuration.getStartupTimeLimit().compareTo(process.getStartupDuration()) < 0) {
                            try {
                                logger.error("Startup has taken {}ms exceeds limit {}ms terminating sandbox", (Object)process.getStartupDuration().toMillis(), (Object)this.configuration.getStartupTimeLimit().toMillis());
                                process.terminate(true);
                            }
                            catch (InterruptedException e) {
                                logger.error("Can't terminate the sandbox process", (Throwable)e);
                                Thread.currentThread().interrupt();
                            }
                        }
                        if (process.getRequestTimeLimit().compareTo(process.getRequestDuration()) >= 0) continue;
                        try {
                            logger.warn("Request has taken {}ms exceeds limit {}ms terminating sandbox", (Object)process.getRequestDuration().toMillis(), (Object)process.getRequestTimeLimit().toMillis());
                            process.terminate(false);
                        }
                        catch (InterruptedException e) {
                            logger.error("Can't terminate the sandbox process", (Throwable)e);
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "sandbox-terminator");
        terminator.setUncaughtExceptionHandler((t, e) -> logger.error("Sandbox terminator thread crashed", e));
        terminator.setDaemon(true);
        terminator.start();
        return terminator;
    }

    private Thread startErrorLogger() {
        Thread errorLogger = new Thread(() -> {
            while (!this.shutdown) {
                try {
                    Thread.sleep(ERROR_DELAY_MILLIS);
                    for (SandboxProcess process : this.processes) {
                        process.flushStdError();
                    }
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            for (SandboxProcess process : this.processes) {
                process.flushStdError();
            }
        }, "sandbox-logger");
        errorLogger.setUncaughtExceptionHandler((t, e) -> logger.error("Sandbox terminator thread crashed", e));
        errorLogger.setDaemon(true);
        errorLogger.start();
        return errorLogger;
    }

    private long getTerminatorActivationInMillis() {
        return TimeUnit.SECONDS.toMillis(TERMINATION_TOLERANCE);
    }

    private static Path prepareWorkingDirectory(FilesystemPath directory, SandboxPoolConfiguration configuration) {
        Path working;
        try {
            Files.createDirectories(directory.asJavaPath(), new FileAttribute[0]);
            working = Files.createTempDirectory(directory.asJavaPath(), "sandbox", new FileAttribute[0]);
            logger.info("start sandbox process in " + working.toString());
            ImmutableSet bootstrapClasses = ImmutableSet.builder().addAll(SANDBOX_SERVER_CLASSES).addAll(configuration.getBootstrapClasses()).build();
            for (Class bootstrapClass : bootstrapClasses) {
                SandboxLocalProcessPool.copyClassToDirectory(bootstrapClass, working);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return working;
    }

    private static void copyClassToDirectory(Class<?> klass, Path folder) throws IOException {
        String path = klass.getName().replace('.', '/') + ".class";
        Path completePath = folder.resolve(path);
        if (Files.exists(completePath, new LinkOption[0])) {
            return;
        }
        try (InputStream input = klass.getClassLoader().getResourceAsStream(path);){
            Path parent = completePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent, new FileAttribute[0]);
            }
            Files.copy(input, completePath, new CopyOption[0]);
        }
    }
}

