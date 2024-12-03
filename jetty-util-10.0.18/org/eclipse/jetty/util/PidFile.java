/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.util.annotation.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PidFile
extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(PidFile.class);
    private static final Set<Path> activeFiles = ConcurrentHashMap.newKeySet();
    private final Path pidFile;

    public static void create(@Name(value="file") String filename) throws IOException {
        Path pidFile = Paths.get(filename, new String[0]).toAbsolutePath();
        if (activeFiles.add(pidFile)) {
            Runtime.getRuntime().addShutdownHook(new PidFile(pidFile));
            if (Files.exists(pidFile, new LinkOption[0])) {
                LOG.info("Overwriting existing PID file: {}", (Object)pidFile);
            }
            long pid = ProcessHandle.current().pid();
            Files.writeString(pidFile, (CharSequence)Long.toString(pid), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            if (LOG.isDebugEnabled()) {
                LOG.debug("PID file: {}", (Object)pidFile);
            }
        }
    }

    private PidFile(Path pidFile) {
        this.pidFile = pidFile;
    }

    @Override
    public void run() {
        try {
            Files.deleteIfExists(this.pidFile);
        }
        catch (Throwable t) {
            LOG.info("Unable to remove PID file: {}", (Object)this.pidFile, (Object)t);
        }
    }
}

