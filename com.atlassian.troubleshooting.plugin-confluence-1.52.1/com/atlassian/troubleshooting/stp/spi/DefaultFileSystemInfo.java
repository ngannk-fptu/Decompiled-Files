/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.troubleshooting.api.healthcheck.FileSystemInfo;
import com.atlassian.troubleshooting.api.healthcheck.OperatingSystemInfo;
import com.atlassian.troubleshooting.api.healthcheck.RuntimeHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultFileSystemInfo
implements FileSystemInfo {
    private static final String UNLIMITED = "unlimited";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileSystemInfo.class);
    private final RuntimeHelper runtime;
    private final OperatingSystemInfo operatingSystemInfo;

    @Autowired
    public DefaultFileSystemInfo(RuntimeHelper runtime, OperatingSystemInfo operatingSystemInfo) {
        this.runtime = runtime;
        this.operatingSystemInfo = operatingSystemInfo;
    }

    @Override
    public FileStore getFileStore(String path) throws IOException {
        return Files.getFileStore(Paths.get(path, new String[0]).toRealPath(new LinkOption[0]));
    }

    @Override
    public Optional<FileSystemInfo.ThreadLimit> getThreadLimit() {
        if (this.operatingSystemInfo.isOsUnix()) {
            return this.runtime.spawnProcessSafely("bash", "-c", "ulimit -u").map(process -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));){
                    String result = reader.readLine();
                    FileSystemInfo.ThreadLimit threadLimit = FileSystemInfo.ThreadLimit.threadLimit(UNLIMITED.equals(result) ? 0 : Integer.parseInt(result));
                    return threadLimit;
                }
                catch (IOException | NumberFormatException e) {
                    LOGGER.warn("Failed to determine the configured process limit", (Throwable)e);
                    return null;
                }
            });
        }
        return Optional.empty();
    }

    @Override
    public boolean isExecutable(@Nonnull String path) {
        Objects.requireNonNull(path);
        File file = new File(path);
        return file.isFile() && file.canExecute();
    }

    @Override
    public List<File> listFiles(@Nonnull File file, @Nonnull FilenameFilter filenameFilter) {
        Objects.requireNonNull(file);
        Objects.requireNonNull(filenameFilter);
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("The provided '%s' path does not exist", file.toString()));
        }
        return Arrays.asList((Object[])Optional.ofNullable(file.listFiles(filenameFilter)).orElse(new File[0]));
    }
}

