/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.FilenameUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.enums.RecordingTemplate;
import com.atlassian.troubleshooting.jfr.service.JfrRecordingCleanUpService;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultJfrRecordingCleanUpService
implements JfrRecordingCleanUpService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJfrRecordingCleanUpService.class);
    private static final Pattern RECORDING_FILENAME_REGEXP = Pattern.compile("^" + RecordingTemplate.DEFAULT.getRecordingName() + "_[0-9]{4}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2}" + ".jfr" + "$");
    private static final Pattern RECORDING_DUMP_ON_EXIT_FILENAME_REGEXP = Pattern.compile("^" + RecordingTemplate.DEFAULT.getRecordingName() + "_dump_on_exit_[0-9]{4}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2}_[0-9]{2}" + ".jfr" + "$");
    private final SupportApplicationInfo applicationInfo;
    private final JfrProperties jfrProperties;

    @Autowired
    public DefaultJfrRecordingCleanUpService(SupportApplicationInfo applicationInfo, JfrProperties jfrProperties) {
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.jfrProperties = Objects.requireNonNull(jfrProperties);
    }

    @Override
    public void cleanUpStaleRecordings() {
        Path path = this.cleanUpRecordingInternally(RECORDING_FILENAME_REGEXP);
        LOG.debug("Old default recordings deleted. Cleanup package: {}", (Object)path);
    }

    @Override
    public void cleanUpDumpOnExitStaleRecordings() {
        Path path = this.cleanUpRecordingInternally(RECORDING_DUMP_ON_EXIT_FILENAME_REGEXP);
        LOG.debug("Old dump on exit recordings deleted. Cleanup package: {}", (Object)path);
    }

    private Path cleanUpRecordingInternally(Pattern fileNamePattern) {
        Path recordingPath = Paths.get(this.applicationInfo.getLocalApplicationHome(), this.jfrProperties.getRecordingPath());
        if (Files.exists(recordingPath, new LinkOption[0])) {
            try (DirectoryStream<Path> fileStream = Files.newDirectoryStream(recordingPath, x$0 -> Files.isRegularFile(x$0, new LinkOption[0]));){
                StreamSupport.stream(fileStream.spliterator(), false).map(Path::toFile).filter(file -> fileNamePattern.matcher(file.getName()).matches()).sorted(Comparator.comparingLong(File::lastModified).reversed()).skip(this.jfrProperties.getNumberOfFilesToRemain()).forEach(this::deleteRecording);
            }
            catch (IOException exc) {
                LOG.error("Error rotating recording files", (Throwable)exc);
            }
        }
        return recordingPath;
    }

    private void deleteRecording(File recordingFile) {
        String threadDumpsDir = FilenameUtils.removeExtension((String)recordingFile.getAbsolutePath());
        try {
            Files.delete(recordingFile.toPath());
            FileUtils.deleteDirectory((File)new File(threadDumpsDir));
        }
        catch (IOException exc) {
            LOG.error("Error deleting thread dump directory", (Throwable)exc);
        }
    }
}

