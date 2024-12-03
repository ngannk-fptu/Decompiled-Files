/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.file;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMessagePublisher {
    private static final Logger log = LoggerFactory.getLogger(FileMessagePublisher.class);
    private final Supplier<Path> currentAuditFilePathSupplier;

    public FileMessagePublisher(Supplier<Path> currentAuditFilePathSupplier) {
        this.currentAuditFilePathSupplier = currentAuditFilePathSupplier;
    }

    public synchronized void publish(String ... messages) {
        Objects.requireNonNull(messages);
        Path currentPath = this.currentAuditFilePathSupplier.get();
        try (OutputStream out = Files.newOutputStream(currentPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);){
            String data = Arrays.stream(messages).filter(this::isValidMessage).collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
            out.write(data.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
            log.error("Unable to write {} to {}", new Object[]{messages, currentPath, e});
        }
    }

    private boolean isValidMessage(@Nullable String message) {
        if (message == null) {
            log.debug("message is null. Not publishing.");
            return false;
        }
        return true;
    }
}

