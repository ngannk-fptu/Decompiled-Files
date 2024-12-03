/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.service;

import com.atlassian.confluence.setup.BootstrapManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Optional;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarnLogFileWriter {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(WarnLogFileWriter.class);
    private final Path warnLogFilePath;

    public WarnLogFileWriter(BootstrapManager bootstrapManager) {
        Path basePath = Optional.ofNullable(bootstrapManager.getSharedHome()).orElse(bootstrapManager.getLocalHome()).toPath();
        this.warnLogFilePath = basePath.resolve("relationships-assessment-job-runner-warn.log");
    }

    public void writeError(String message) {
        String messageWithNewLine = message + System.lineSeparator();
        try (OutputStream os = Files.newOutputStream(this.warnLogFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);){
            os.write(messageWithNewLine.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            log.error("Failed to write to warn log file", (Throwable)e);
            throw new UncheckedIOException(e);
        }
    }

    public Path getWarnLogFilePath() {
        return this.warnLogFilePath;
    }

    public void clearWarnLogs() {
        try {
            Files.deleteIfExists(this.warnLogFilePath);
            Files.createFile(this.warnLogFilePath, new FileAttribute[0]);
        }
        catch (IOException e) {
            log.error("Failed to create the warn log file", (Throwable)e);
            throw new UncheckedIOException(e);
        }
    }

    public boolean hasWarnLogs() {
        try {
            if (Files.notExists(this.warnLogFilePath, new LinkOption[0])) {
                return false;
            }
            return Files.size(this.warnLogFilePath) > 0L;
        }
        catch (IOException e) {
            log.error("Failed to access the warn log file", (Throwable)e);
            throw new UncheckedIOException(e);
        }
    }
}

