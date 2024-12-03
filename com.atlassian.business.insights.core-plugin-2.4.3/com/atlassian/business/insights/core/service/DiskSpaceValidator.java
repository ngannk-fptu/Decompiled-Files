/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.config.PropertiesProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.config.PropertiesProvider;
import com.atlassian.business.insights.core.service.exception.NotEnoughDiskSpaceException;
import com.atlassian.business.insights.core.util.DataSize;
import com.atlassian.business.insights.core.util.DataUnit;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiskSpaceValidator {
    @VisibleForTesting
    static final String MINIMUM_USABLE_SPACE_KEY = "plugin.data.pipeline.minimum.usable.disk.space.after.export";
    @VisibleForTesting
    static final String ERROR_MSG_KEY = "data-pipeline.full.export.not.enough.disk.space.remaining";
    @VisibleForTesting
    static final String DEFAULT_MINIMUM_USABLE_SPACE = "5GB";
    private static final Logger log = LoggerFactory.getLogger(DiskSpaceValidator.class);
    private static final Duration DISK_SPACE_CHECK_FREQUENCY = Duration.of(30L, ChronoUnit.SECONDS);
    private final long minimumUsableSpaceInBytes;
    private final boolean isDiskSpaceCheckEnabled;
    private final I18nResolver i18nResolver;
    private Instant lastCheckedTime = Instant.MIN;

    public DiskSpaceValidator(@Nonnull PropertiesProvider propertiesProvider, @Nonnull I18nResolver i18nResolver) {
        Objects.requireNonNull(propertiesProvider, "propertiesProvider must not be null");
        Objects.requireNonNull(i18nResolver, "i18nResolver must not be null");
        this.i18nResolver = i18nResolver;
        DataSize minimumRequiredFreeSpace = DataSize.parse(propertiesProvider.getProperty(MINIMUM_USABLE_SPACE_KEY, DEFAULT_MINIMUM_USABLE_SPACE), DataUnit.GIGABYTES);
        this.isDiskSpaceCheckEnabled = !minimumRequiredFreeSpace.isNegative() && !minimumRequiredFreeSpace.isZero();
        this.minimumUsableSpaceInBytes = minimumRequiredFreeSpace.toBytes();
    }

    public void verifyEnoughUsableSpaceRemaining(@Nonnull Path path) throws NotEnoughDiskSpaceException {
        Objects.requireNonNull(path, "path must not be null");
        if (this.isDiskSpaceCheckEnabled && this.isTimeToReCheck()) {
            this.lastCheckedTime = Instant.now();
            if (this.isNotEnoughSpaceRemainingFor(path)) {
                log.error(String.format("Not enough disk space remaining at %s. Data-Pipeline export requires at least %s of unallocated space", path, FileUtils.byteCountToDisplaySize((long)this.minimumUsableSpaceInBytes)));
                String translatedErrorMsg = this.i18nResolver.getText(ERROR_MSG_KEY, new Serializable[]{path.toString(), FileUtils.byteCountToDisplaySize((long)this.minimumUsableSpaceInBytes)});
                throw new NotEnoughDiskSpaceException(translatedErrorMsg);
            }
        }
    }

    private boolean isTimeToReCheck() {
        return Instant.now().isAfter(this.lastCheckedTime.plus(DISK_SPACE_CHECK_FREQUENCY));
    }

    private boolean isNotEnoughSpaceRemainingFor(@Nonnull Path path) throws NotEnoughDiskSpaceException {
        try {
            FileStore fileStore = Files.getFileStore(path);
            long freeSpaceOnDisk = fileStore.getUsableSpace();
            return freeSpaceOnDisk < this.minimumUsableSpaceInBytes && freeSpaceOnDisk > 0L;
        }
        catch (NoSuchFileException ignored) {
            log.info(String.format("Skipping unallocated space disk check because target path does not exist yet; %s", path));
            return false;
        }
        catch (Exception e) {
            String msg = String.format("Failed to determine filesystem size for path %s", path);
            log.error(msg, (Throwable)e);
            throw new NotEnoughDiskSpaceException(msg);
        }
    }
}

