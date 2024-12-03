/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.troubleshooting.api.healthcheck.RuntimeHelper;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRuntimeHelper
implements RuntimeHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRuntimeHelper.class);

    @Override
    public Optional<Process> spawnProcessSafely(String ... command) {
        try {
            if (command.length > 0) {
                return Optional.of(Runtime.getRuntime().exec(command));
            }
        }
        catch (IOException e) {
            LOGGER.warn("Failed to spawn a process", (Throwable)e);
        }
        return Optional.empty();
    }
}

