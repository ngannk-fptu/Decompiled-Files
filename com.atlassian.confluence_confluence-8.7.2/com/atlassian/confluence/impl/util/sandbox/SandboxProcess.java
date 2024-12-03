/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxCrashedException
 *  com.atlassian.confluence.util.sandbox.SandboxErrorConsumer
 *  com.atlassian.confluence.util.sandbox.SandboxException
 *  com.atlassian.confluence.util.sandbox.SandboxStartupException
 *  com.atlassian.confluence.util.sandbox.SandboxTimeoutException
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.SystemUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.ClassLoadersRegistry;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessage;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessageExchanger;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessageType;
import com.atlassian.confluence.impl.util.sandbox.SandboxPoolConfiguration;
import com.atlassian.confluence.impl.util.sandbox.SandboxRequest;
import com.atlassian.confluence.impl.util.sandbox.SandboxServer;
import com.atlassian.confluence.util.sandbox.SandboxCrashedException;
import com.atlassian.confluence.util.sandbox.SandboxErrorConsumer;
import com.atlassian.confluence.util.sandbox.SandboxException;
import com.atlassian.confluence.util.sandbox.SandboxStartupException;
import com.atlassian.confluence.util.sandbox.SandboxTimeoutException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SandboxProcess {
    private static final Logger logger = LoggerFactory.getLogger(SandboxProcess.class);
    private static final long PROCESS_DEATH_WAIT_TIME = Integer.getInteger("sandbox.process.death.wait.time.seconds", 10).intValue();
    private final SandboxPoolConfiguration configuration;
    private final Path workingDirectory;
    private final int num;
    private volatile boolean shutdown;
    private volatile Process process;
    private volatile long processStart = 0L;
    private volatile SandboxRequest currentRequest = null;
    private volatile boolean brokenPipe = false;
    private volatile ByteArrayOutputStream errorLineBuff;
    private volatile SandboxStatus lastStatus = SandboxStatus.NEW;
    private final ClassLoadersRegistry classLoadersRegistry = new ClassLoadersRegistry();

    SandboxProcess(SandboxPoolConfiguration configuration, Path workingDirectory, int num) {
        this.configuration = Objects.requireNonNull(configuration);
        this.workingDirectory = Objects.requireNonNull(workingDirectory);
        this.num = num;
        this.errorLineBuff = new ByteArrayOutputStream();
        this.process = this.start();
    }

    public void terminate(boolean force) throws InterruptedException {
        if (force || this.currentRequest != null) {
            if (!this.shutdown) {
                this.lastStatus = SandboxStatus.KILLED;
            }
            this.currentRequest = null;
            this.process.destroyForcibly();
            this.process.waitFor(PROCESS_DEATH_WAIT_TIME, TimeUnit.SECONDS);
        }
    }

    public synchronized <T, R> R execute(SandboxRequest<T, R> request) {
        Preconditions.checkState((this.lastStatus != SandboxStatus.NEW ? 1 : 0) != 0, (Object)"Sandbox should be started before serving any requests");
        Preconditions.checkState((!this.shutdown ? 1 : 0) != 0, (Object)"Sandbox has been shut down");
        SandboxMessageExchanger<T, R> messageExchange = SandboxMessageExchanger.createFrom(request, this.classLoadersRegistry);
        this.startIfDead();
        try {
            logger.debug("Send a request for {} : {}", (Object)SandboxMessageType.APPLICATION_REQUEST, request.getInput());
            this.currentRequest = request;
            this.sendMessage(messageExchange.createInitialMessage(request.getInput()));
        }
        catch (IOException e) {
            this.setBrokenPipe(true);
            throw new SandboxException("Attempt to send request to sandbox failed", (Throwable)e);
        }
        while (!this.shutdown) {
            SandboxMessage message;
            try {
                message = this.receiveMessage();
            }
            catch (IOException e) {
                this.setBrokenPipe(true);
                this.tryToThrowSpecificException(e, request);
                throw new SandboxException("Error while receiving message from sandbox", (Throwable)e);
            }
            try {
                Optional<R> result = messageExchange.handleMessage(message).resultOrReply(this::sendMessage);
                if (!result.isPresent()) continue;
                this.currentRequest = null;
                logger.debug("Sandbox result received");
                return result.get();
            }
            catch (Throwable e) {
                this.setBrokenPipe(true);
                this.tryToThrowSpecificException(e, request);
                throw new SandboxException("Can't handle the request " + message.getType(), e);
            }
        }
        throw new SandboxException("Shutting down");
    }

    private void tryToThrowSpecificException(Throwable t, SandboxRequest request) {
        switch (this.lastStatus) {
            case KILLED: {
                throw new SandboxTimeoutException("Sandbox request has been killed because it exceeded time limit of " + request.getTimeLimit().getSeconds() + " seconds", t);
            }
            case STARTED: {
                throw new SandboxCrashedException("Sandbox has crashed while serving the request", t);
            }
        }
    }

    Duration getRequestDuration() {
        return this.currentRequest == null ? Duration.ofNanos(0L) : this.currentRequest.currentDuration();
    }

    Duration getRequestTimeLimit() {
        return this.currentRequest == null ? Duration.ofNanos(0L) : this.currentRequest.getTimeLimit();
    }

    Duration getStartupDuration() {
        if (this.processStart == 0L) {
            return Duration.ofNanos(0L);
        }
        return Duration.ofNanos(System.nanoTime() - this.processStart);
    }

    public void shutdown() {
        this.shutdown = true;
        try {
            this.terminate(true);
        }
        catch (InterruptedException e) {
            logger.warn("There was a problem shutting down sandbox process", (Throwable)e);
        }
    }

    private void setBrokenPipe(boolean brokenPipe) {
        if (brokenPipe) {
            this.currentRequest = null;
        }
        this.brokenPipe = brokenPipe;
    }

    private void sendMessage(SandboxMessage message) throws IOException {
        SandboxMessage.sendMessage(message, this.process.getOutputStream());
    }

    private SandboxMessage receiveMessage() throws IOException {
        return SandboxMessage.receiveMessage(this.process.getInputStream());
    }

    private synchronized void startIfDead() {
        if (this.brokenPipe || this.processIsDead()) {
            logger.warn("Sandbox {} has died", (Object)this.num);
            this.setBrokenPipe(false);
            this.flushStdError();
            if (this.process != null) {
                this.process.destroyForcibly();
            }
            logger.warn("Attempting to restart the sandbox {}", (Object)this.num);
            this.process = this.start();
        }
    }

    private boolean processIsDead() {
        return this.process != null && !this.process.isAlive();
    }

    private Process start() {
        this.processStart = System.nanoTime();
        ImmutableList command = ImmutableList.builder().add((Object)SandboxProcess.getJavaRuntime().toString()).add((Object)String.format("-Xmx%dm", this.configuration.getMemoryInMegabytes())).add((Object)String.format("-Xss%dm", this.configuration.getStackInMegabytes())).addAll(this.configuration.getJavaOptions()).addAll((Iterable)Optional.ofNullable(this.configuration.getDebugPortOffset()).map(offset -> Collections.singletonList("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=" + (offset + this.num))).orElse(Collections.emptyList())).add((Object)"-classpath").add((Object)".").add((Object)SandboxServer.class.getName()).add((Object)this.configuration.getLogLevel().getName()).build();
        String commandString = String.join((CharSequence)" ", (Iterable<? extends CharSequence>)command);
        Path workingDirectoryPath = this.workingDirectory.toAbsolutePath();
        logger.info("Sandbox {}: Starting sandbox process: {} in directory {}", new Object[]{this.num, commandString, workingDirectoryPath});
        ProcessBuilder processBuilder = new ProcessBuilder((List<String>)command).directory(this.workingDirectory.toFile());
        Process process = null;
        try {
            process = processBuilder.start();
            SandboxMessage.waitForStartMarker(process.getInputStream());
            this.lastStatus = SandboxStatus.STARTED;
        }
        catch (IOException e) {
            if (process != null) {
                this.flushStdError(process.getErrorStream());
            }
            this.lastStatus = SandboxStatus.STARTUP_FAILED;
            throw new SandboxStartupException(String.format("Error starting sandbox process %s in directory %s. Please see logs for detailed errors.", commandString, workingDirectoryPath), (Throwable)e);
        }
        finally {
            this.processStart = 0L;
        }
        return process;
    }

    void flushStdError() {
        this.flushStdError(this.process.getErrorStream());
    }

    private void flushStdError(InputStream errorStream) {
        try {
            int b;
            for (int n = errorStream.available(); n > 0 && (b = errorStream.read()) != -1; --n) {
                this.errorLineBuff.write(b);
                if (b != 10) continue;
                this.getErrorConsumer().accept(this.getSandboxName(), this.errorLineBuff.toString(StandardCharsets.UTF_8.name()));
                this.errorLineBuff.reset();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static Path getJavaRuntime() {
        String home = System.getProperty("java.home");
        if (StringUtils.isBlank((CharSequence)home)) {
            home = StringUtils.defaultString((String)System.getenv("JRE_HOME"), (String)System.getenv("JAVA_HOME"));
        }
        if (StringUtils.isBlank((CharSequence)home)) {
            throw new SandboxException("Both JRE_HOME and JAVA_HOME are not defined!");
        }
        Path path = Paths.get(home, "bin", SystemUtils.IS_OS_WINDOWS ? "java.exe" : "java");
        if (!Files.isExecutable(path)) {
            throw new SandboxException(path.toString() + " is not an executable");
        }
        return path;
    }

    private String getSandboxName() {
        return "worker" + this.num;
    }

    private SandboxErrorConsumer getErrorConsumer() {
        return this.configuration.getErrorConsumer();
    }

    private static enum SandboxStatus {
        NEW,
        STARTED,
        STARTUP_FAILED,
        KILLED;

    }
}

