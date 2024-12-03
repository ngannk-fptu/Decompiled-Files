/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.ApplicationProperties
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.file;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.file.CachingRetentionFileConfigService;
import com.atlassian.audit.file.FileMessagePublisher;
import com.atlassian.sal.api.ApplicationProperties;
import io.atlassian.util.concurrent.LazyReference;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RotatingFileManager
implements Supplier<Path> {
    public static final String DEFAULT_FILE_EXTENSION = ".audit.log";
    private static final Logger log = LoggerFactory.getLogger(FileMessagePublisher.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final ApplicationProperties appProperties;
    private final String auditSubFolder;
    private final LazyReference<Path> fileDirectory = new LazyReference<Path>(){

        protected Path create() throws Exception {
            try {
                return RotatingFileManager.this.appProperties.getLocalHomeDirectory().map(c -> c.resolve(RotatingFileManager.this.auditSubFolder)).orElseThrow(() -> new IllegalStateException("Unable to resolve local home directory"));
            }
            catch (Exception e) {
                log.error("Unable to determine audit log folder ", (Throwable)e);
                return Paths.get("", new String[0]).resolve(RotatingFileManager.this.auditSubFolder);
            }
        }
    };
    private final Clock clock;
    private final CachingRetentionFileConfigService cachingRetentionFileConfigService;
    private Path currentFilePath;

    public RotatingFileManager(@Nonnull ApplicationProperties appProperties, @Nonnull String auditSubFolder, CachingRetentionFileConfigService cachingRetentionFileConfigService) {
        this(appProperties, auditSubFolder, Clock.systemDefaultZone(), cachingRetentionFileConfigService);
    }

    @VisibleForTesting
    public RotatingFileManager(ApplicationProperties appProperties, @Nonnull String auditSubFolder, @Nonnull Clock clock, CachingRetentionFileConfigService cachingRetentionFileConfigService) {
        this.clock = Objects.requireNonNull(clock);
        this.appProperties = appProperties;
        this.auditSubFolder = auditSubFolder;
        this.cachingRetentionFileConfigService = cachingRetentionFileConfigService;
    }

    @Override
    @Nonnull
    public Path get() {
        if (this.currentFilePath == null || this.currentFilePath.toFile().length() > this.getFileSizeLimitB() || this.filePrefixChanged(this.currentFilePath)) {
            this.rotate();
        }
        return this.currentFilePath;
    }

    private boolean filePrefixChanged(Path currentFilePath) {
        String calculatedPrefix;
        String currentPrefix = this.getPrefixFromFileName(currentFilePath.getFileName().toString());
        return !Objects.equals(currentPrefix, calculatedPrefix = ZonedDateTime.now(this.clock).format(dateTimeFormatter));
    }

    private void rotate() {
        if (!((Path)this.fileDirectory.get()).toFile().exists() && !((Path)this.fileDirectory.get()).toFile().mkdirs()) {
            log.error("Unable to make audit log folder '{}'", (Object)((Path)this.fileDirectory.get()).toAbsolutePath());
        }
        this.currentFilePath = ((Path)this.fileDirectory.get()).resolve(this.buildFileName((Path)this.fileDirectory.get()));
        try (Stream<Path> pathStream = Files.list((Path)this.fileDirectory.get());){
            List fileList = pathStream.filter(path -> path.toString().endsWith(DEFAULT_FILE_EXTENSION)).map(Path::toFile).sorted().collect(Collectors.toList());
            for (int i = 0; i < fileList.size() - this.getFileCountLimit() + 1; ++i) {
                log.info("Total number of audit file exceeds {} , removing file {}", (Object)fileList.size(), (Object)((File)fileList.get(i)).getName());
                ((File)fileList.get(i)).delete();
            }
        }
        catch (IOException e) {
            log.error("Fail to limit file count on {}", (Object)((Path)this.fileDirectory.get()).toString(), (Object)e);
        }
    }

    private String buildFileName(Path fileHomePath) {
        String todayFormatted = ZonedDateTime.now(this.clock).format(dateTimeFormatter);
        File[] todaysAuditFiles = fileHomePath.toFile().listFiles((dir, name) -> name != null && name.endsWith(DEFAULT_FILE_EXTENSION) && name.contains(todayFormatted));
        if (todaysAuditFiles == null || todaysAuditFiles.length == 0) {
            return String.format("%s.%05d%s", todayFormatted, 0, DEFAULT_FILE_EXTENSION);
        }
        File latestAuditFile = Stream.of(todaysAuditFiles).max(Comparator.comparingInt(file -> this.getIterationFromFileName(file.getName()))).orElse(null);
        if (latestAuditFile == null) {
            return String.format("%s.%05d%s", todayFormatted, 0, DEFAULT_FILE_EXTENSION);
        }
        return latestAuditFile.length() < this.getFileSizeLimitB() ? latestAuditFile.getName() : String.format("%s.%05d%s", todayFormatted, this.getIterationFromFileName(latestAuditFile.getName()) + 1, DEFAULT_FILE_EXTENSION);
    }

    private int getIterationFromFileName(String fileName) {
        String[] parts = fileName.split("\\.");
        String iterationStr = parts[1];
        return Integer.parseInt(iterationStr);
    }

    private String getPrefixFromFileName(String fileName) {
        String[] parts = fileName.split("\\.");
        return parts[0];
    }

    private long getFileSizeLimitB() {
        return this.cachingRetentionFileConfigService.getConfig().getMaxFileSizeB();
    }

    private int getFileCountLimit() {
        return this.cachingRetentionFileConfigService.getConfig().getMaxFileCount();
    }
}

