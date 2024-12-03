/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.action.DefaultMessage;
import com.atlassian.troubleshooting.stp.request.FileSanitizer;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.atlassian.troubleshooting.stp.zip.NestedProgressTracker;
import com.atlassian.troubleshooting.stp.zip.ZipFileConstants;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ZipFileAppender
extends NestedProgressTracker<SupportZipBundle, SupportZipBundle.Artifact> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipFileAppender.class);
    private static final int COPY_BUFFER_SIZE = 4096;
    private static final String ZIP_PATH_SEPARATOR = "/";
    private final ByteBuffer buffer = ByteBuffer.allocate(4096);
    private final FileSanitizer fileSanitizer;
    private final Integer maxBytesPerFile;
    private final CreateSupportZipMonitor monitor;
    private final Set<String> uniqueFilenames = Sets.newHashSet();
    private final SupportApplicationInfo applicationInfo;
    private final ZipOutputStream out;
    private final ZonedDateTime zonedDatetimeLastModifiedCutOff;

    ZipFileAppender(FileSanitizer fileSanitizer, SupportApplicationInfo applicationInfo, ZipOutputStream out, @Nullable Integer maxBytesPerFile, CreateSupportZipMonitor monitor, @Nullable ZonedDateTime zonedDatetimeLastModifiedCutOff) {
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.fileSanitizer = Objects.requireNonNull(fileSanitizer);
        this.maxBytesPerFile = maxBytesPerFile;
        this.monitor = Objects.requireNonNull(monitor);
        this.out = Objects.requireNonNull(out);
        this.zonedDatetimeLastModifiedCutOff = zonedDatetimeLastModifiedCutOff;
    }

    private static boolean checkFileExists(File file, String bundleKey) {
        if (!file.exists()) {
            LOGGER.debug("Unable to find {} for {}", (Object)file.getName(), (Object)bundleKey);
            return false;
        }
        if (file.isDirectory()) {
            LOGGER.debug("{} is a directory in {}", (Object)file.getName(), (Object)bundleKey);
            return false;
        }
        return true;
    }

    @VisibleForTesting
    protected static boolean skipFileForSizeLimiting(File file) {
        return file.getName().endsWith(".jfr") || ZipFileConstants.TOMCAT_ACCESS_LOG_PATTERN.matcher(file.getName()).matches();
    }

    private String getPathWithinZip(String directory, String subDirectory, String filename) {
        ArrayList pathElements = Lists.newArrayList();
        String uniqueFileName = this.createUniqueFilename(filename, subDirectory);
        pathElements.add(directory);
        if (StringUtils.isNotBlank((CharSequence)subDirectory)) {
            pathElements.add(subDirectory);
        }
        pathElements.add(uniqueFileName);
        return StringUtils.join((Iterable)pathElements, (String)ZIP_PATH_SEPARATOR);
    }

    @Override
    @Nonnull
    protected Collection<SupportZipBundle.Artifact> getInnerItems(SupportZipBundle bundle) {
        try {
            return bundle.getArtifacts();
        }
        catch (Throwable e) {
            String bundleTitle = this.applicationInfo.getText(bundle.getTitle());
            String warning = this.applicationInfo.getText("stp.create.support.zip.warning", new Serializable[]{bundleTitle, e.getClass().getSimpleName() + ": " + e.getMessage()});
            this.monitor.addWarning(new DefaultMessage(bundleTitle, warning));
            LOGGER.warn(warning, e);
            return Collections.emptyList();
        }
    }

    @Override
    protected void recordOuterProgress(int progress, SupportZipBundle bundle) {
        this.recordProgress(progress, bundle, "");
    }

    private void recordProgress(int progress, SupportZipBundle bundle, String messageDetail) {
        String bundleTitle = this.applicationInfo.getText(bundle.getTitle());
        String message = this.applicationInfo.getText("stp.create.support.zip.progress.bundle.message", new Serializable[]{bundleTitle, messageDetail});
        this.monitor.updateProgress(progress, message);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void processInnerItem(SupportZipBundle bundle, SupportZipBundle.Artifact artifact, int innerProgress) throws InterruptedException {
        try {
            File file = artifact.getFile();
            if (ZipFileAppender.checkFileExists(file, bundle.getKey()) && this.checkAgeConstraint(file, bundle.getCategory())) {
                String path = this.getPathWithinZip(bundle.getKey(), artifact.getTargetPath(), file.getName());
                this.recordProgress(innerProgress, bundle, path);
                LOGGER.debug("adding entry: {}, as {}", (Object)file.getPath(), (Object)path);
                try {
                    File sanitizedFile = this.fileSanitizer.sanitize(file);
                    this.addToZipFile(this.out, file.lastModified(), path, () -> this.copyToZip(sanitizedFile, this.out));
                }
                catch (Exception e) {
                    String warning = this.applicationInfo.getText("stp.create.support.zip.adding-file.warning", new Serializable[]{file.getPath(), e.getClass().getSimpleName() + ": " + e.getMessage()});
                    this.monitor.addWarning(new DefaultMessage("Problem adding file", warning));
                    LOGGER.warn(warning, (Throwable)e);
                }
            }
        }
        finally {
            IOUtils.closeQuietly((Closeable)artifact);
        }
    }

    @VisibleForTesting
    String createUniqueFilename(String source, String pathInZip) {
        String filename = this.fileSanitizer.sanitizeExtensions(source);
        int suffix = 0;
        while (!this.uniqueFilenames.add(pathInZip + ZIP_PATH_SEPARATOR + filename)) {
            filename = source + suffix++;
        }
        return filename;
    }

    private void addToZipFile(ZipOutputStream out, long lastModified, String pathInZip, Runnable doCopy) throws IOException {
        ZipEntry zipEntry = new ZipEntry(pathInZip);
        zipEntry.setTime(lastModified);
        out.putNextEntry(zipEntry);
        doCopy.run();
        out.closeEntry();
    }

    private void copyToZip(File file, OutputStream zipStream) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");){
            int bytesInBuffer;
            this.maybeSkipFirstPartOfFile(file, randomAccessFile);
            long totalBytes = 0L;
            while ((bytesInBuffer = randomAccessFile.read(this.buffer.array())) != -1) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                zipStream.write(this.buffer.array(), 0, bytesInBuffer);
                totalBytes += (long)bytesInBuffer;
            }
            zipStream.flush();
            LOGGER.debug("Copied {} bytes for {}", (Object)totalBytes, (Object)file.getName());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void maybeSkipFirstPartOfFile(File file, RandomAccessFile randomAccessFile) throws IOException {
        if (ZipFileAppender.skipFileForSizeLimiting(file)) {
            return;
        }
        if (this.maxBytesPerFile != null) {
            int maxBytesToCopy = this.maxBytesPerFile;
            long bytesToSkip = file.length() - (long)maxBytesToCopy;
            if (bytesToSkip > 0L) {
                randomAccessFile.seek(bytesToSkip);
                String warning = this.applicationInfo.getText("stp.zip.file.size.limited", new Serializable[]{file.getName(), maxBytesToCopy / 0x100000 + "Mb"});
                this.monitor.addTruncatedFile(file.getPath());
                LOGGER.warn(warning);
            }
        }
    }

    private boolean checkAgeConstraint(File file, BundleCategory category) {
        if (category == BundleCategory.LOGS && this.zonedDatetimeLastModifiedCutOff != null) {
            boolean keep;
            long threshold = this.zonedDatetimeLastModifiedCutOff.toInstant().toEpochMilli();
            boolean bl = keep = file.lastModified() >= threshold;
            if (!keep) {
                String warning = this.applicationInfo.getText("stp.zip.file.excluded.fileConstraint.lastModified", new Serializable[]{file.getName(), this.zonedDatetimeLastModifiedCutOff});
                this.monitor.addAgeExcludedFile(file.getPath());
                LOGGER.warn(warning);
            } else {
                LOGGER.debug("Including {} last modified ({}ms) is newer than threshold ({}ms) from {}", new Object[]{file.getAbsolutePath(), file.lastModified(), threshold, this.zonedDatetimeLastModifiedCutOff});
            }
            return keep;
        }
        return true;
    }
}

