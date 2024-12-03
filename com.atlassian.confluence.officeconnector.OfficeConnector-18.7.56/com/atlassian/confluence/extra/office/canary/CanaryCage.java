/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 *  org.apache.commons.exec.CommandLine
 *  org.apache.commons.exec.DefaultExecutor
 *  org.apache.commons.exec.ExecuteStreamHandler
 *  org.apache.commons.exec.ExecuteWatchdog
 *  org.apache.commons.exec.PumpStreamHandler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.office.canary;

import com.atlassian.confluence.extra.office.canary.CanaryEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
public class CanaryCage {
    private static final Logger log = LoggerFactory.getLogger(CanaryCage.class);
    public static final int MEMORY_VALUE = Integer.getInteger("com.atlassian.confluence.officeconnector.canary.memory_value", 1024);
    private static final String XMX = "-Xmx" + MEMORY_VALUE + "m";
    private static final int WATCHDOG_TIMEOUT_MILLIS = Integer.getInteger("com.atlassian.confluence.officeconnector.canary.timeout", 120000);
    private static final String HEADLESS = "-Djava.awt.headless=true";
    private final CanaryEnvironment canaryEnvironment;
    private final Path canaryJarFile;
    private final String mainClassFullname;

    CanaryCage(CanaryEnvironment canaryEnvironment, Path canaryJarFile, String mainClassFullname) {
        this.canaryJarFile = canaryJarFile;
        this.canaryEnvironment = canaryEnvironment;
        this.mainClassFullname = mainClassFullname;
    }

    @Nonnull
    public Result test(String ... args) {
        log.info("Checking inputs using {}", (Object)this.canaryJarFile.getFileName());
        CommandLine commandLine = this.buildCommandLine(args);
        log.info("Executing canary using " + commandLine);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler((OutputStream)outputStream);
        try {
            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler((ExecuteStreamHandler)streamHandler);
            executor.setWatchdog(new ExecuteWatchdog((long)WATCHDOG_TIMEOUT_MILLIS));
            executor.execute(commandLine);
            log.info("Canary still cheeping");
            return Result.HAPPY_CHEEPING;
        }
        catch (IOException e) {
            log.warn("Canary {} choked and died whilst processing {}", (Object)this.canaryJarFile.getFileName(), (Object)Arrays.toString(args));
            log.warn("Canary output: {}", (Object)outputStream.toString());
            return Result.CHOKED_AND_DIED;
        }
    }

    private CommandLine buildCommandLine(String[] args) {
        return new CommandLine(this.canaryEnvironment.getJavaExePath().toFile()).addArgument(XMX).addArgument(HEADLESS).addArgument("-cp").addArgument(this.canaryJarFile.toAbsolutePath().toString()).addArgument(this.mainClassFullname).addArguments(args);
    }

    public static enum Result {
        HAPPY_CHEEPING,
        CHOKED_AND_DIED,
        UNKNOWN;

    }
}

