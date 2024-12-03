/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Throwables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.stp.action.DefaultMessage;
import com.atlassian.troubleshooting.stp.audit.Auditor;
import com.atlassian.troubleshooting.stp.request.FileSanitizer;
import com.atlassian.troubleshooting.stp.request.SupportZipContext;
import com.atlassian.troubleshooting.stp.request.SupportZipCreationRequest;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.spi.HostApplication;
import com.atlassian.troubleshooting.stp.task.MonitoredCallable;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.atlassian.troubleshooting.stp.zip.SupportZipFileNameGenerator;
import com.atlassian.troubleshooting.stp.zip.SupportZipStats;
import com.atlassian.troubleshooting.stp.zip.ZipFileAppender;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSupportZipTask
implements MonitoredCallable<File, CreateSupportZipMonitor> {
    @VisibleForTesting
    static final String ZIP_INFO_FILE_PATH = "zip-stats.json";
    private static final Logger LOGGER_INFO = LoggerFactory.getLogger((String)"atlassian.plugin");
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateSupportZipTask.class);
    private final CreateSupportZipMonitor monitor;
    private final FileSanitizer fileSanitizer;
    private final HostApplication hostApplication;
    private final String username;
    private final SupportApplicationInfo applicationInfo;
    private final SupportZipCreationRequest request;
    private final SupportZipFileNameGenerator fileNameGenerator;
    private final Auditor auditor;
    private final TimeZone timeZone;

    public CreateSupportZipTask(SupportZipCreationRequest request, SupportZipFileNameGenerator fileNameGenerator, SupportApplicationInfo applicationInfo, HostApplication hostApplication, CreateSupportZipMonitor monitor, @Nullable String username, Auditor auditor, TimeZone userTimeZone) {
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.fileNameGenerator = Objects.requireNonNull(fileNameGenerator);
        this.fileSanitizer = Objects.requireNonNull(applicationInfo.getFileSanitizer());
        this.hostApplication = Objects.requireNonNull(hostApplication);
        this.monitor = Objects.requireNonNull(monitor);
        this.request = Objects.requireNonNull(request);
        this.username = username;
        this.auditor = Objects.requireNonNull(auditor);
        this.timeZone = Objects.requireNonNull(userTimeZone);
    }

    @Override
    @Nonnull
    public CreateSupportZipMonitor getMonitor() {
        return this.monitor;
    }

    @Override
    public File call() throws Exception {
        Callable<File> createZip = this.hostApplication.asUser(this.username, this::createZipFile);
        return SupportZipContext.wrap(this.request, createZip).call();
    }

    private File createZipFile() throws IOException {
        File zipFile = this.createEmptyZipFile();
        this.writeSupportZipFile(zipFile, zipOut -> {
            try {
                this.addSupportFilesToZip((ZipOutputStream)zipOut);
                this.auditor.audit("stp.audit.summary.support-zip.created");
            }
            catch (InterruptedException e) {
                LOGGER.debug("Support zip task {} was cancelled", (Object)this.monitor.getTaskId());
                FileUtils.deleteQuietly((File)zipFile);
            }
            catch (RuntimeException e) {
                this.handleException(e);
            }
        });
        return zipFile;
    }

    private File createEmptyZipFile() throws IOException {
        File supportDir = this.applicationInfo.getExportDirectory();
        if (!supportDir.exists() && !supportDir.mkdirs()) {
            throw new IOException("Couldn't create export directory " + supportDir.getAbsolutePath());
        }
        File zipFile = this.fileNameGenerator.generate(supportDir);
        try {
            if (!zipFile.createNewFile()) {
                throw new IOException("File already exists");
            }
            this.monitor.setZipFileName(zipFile.getName());
            return zipFile;
        }
        catch (IOException e) {
            throw new IOException(e.getMessage() + " - " + zipFile.getAbsolutePath());
        }
    }

    private void writeSupportZipFile(File outputFile, Consumer<ZipOutputStream> createZipLogic) {
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(outputFile));){
            long startTimeMillis = System.currentTimeMillis();
            zipOut.setComment("zip, created by Atlassian Troubleshooting and Support Tools Plugin");
            createZipLogic.accept(zipOut);
            this.addStatisticsFile(zipOut, startTimeMillis);
            this.monitor.updateProgress(100, this.applicationInfo.getText("stp.create.support.zip.success.message", new Serializable[]{outputFile.getAbsolutePath()}));
            LOGGER_INFO.info("Saved Support Zip to: {}", (Object)outputFile.getAbsolutePath());
            zipOut.finish();
        }
        catch (IOException e) {
            this.handleException(e);
        }
    }

    private void addStatisticsFile(ZipOutputStream zipOut, long startTimeMillis) throws IOException {
        SupportZipStats statsDto = SupportZipStats.supportZipStats(Duration.ofMillis(System.currentTimeMillis() - startTimeMillis));
        ZipEntry zipEntry = new ZipEntry(ZIP_INFO_FILE_PATH);
        zipOut.putNextEntry(zipEntry);
        IOUtils.copy((Reader)new StringReader(statsDto.toJson()), (OutputStream)zipOut);
        zipOut.closeEntry();
    }

    private void addSupportFilesToZip(ZipOutputStream out) throws InterruptedException {
        Integer maxBytesPerFile = this.request.getMaxBytesPerFile().orElse(null);
        new ZipFileAppender(this.fileSanitizer, this.applicationInfo, out, maxBytesPerFile, this.monitor, this.getLastModifiedCutOff()).process(this.request.getBundles());
    }

    private <E extends Throwable> void handleException(E e) {
        this.monitor.updateProgress(100, e.getMessage());
        this.monitor.addError(new DefaultMessage("Support Zip Creation", e.getMessage()));
        Throwables.throwIfUnchecked(e);
        throw new RuntimeException(e);
    }

    private ZonedDateTime getLastModifiedCutOff() {
        Integer ageConstraint = this.request.getFileConstraintLastModified().orElse(null);
        if (ageConstraint != null) {
            ZonedDateTime zdt = LocalDate.now(this.timeZone.toZoneId()).atTime(LocalTime.MIDNIGHT).atZone(this.timeZone.toZoneId());
            ZonedDateTime cutOffZonedDatetime = zdt.minusDays(ageConstraint.intValue());
            return cutOffZonedDatetime.withZoneSameInstant(ZoneId.of("UTC"));
        }
        return null;
    }
}

