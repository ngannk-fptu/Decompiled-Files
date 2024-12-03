/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.supportzip;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.FileSupportZipArtifact;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.domain.JfrCapabilities;
import com.atlassian.troubleshooting.jfr.domain.JfrSettings;
import com.atlassian.troubleshooting.jfr.domain.RecordingDetails;
import com.atlassian.troubleshooting.jfr.enums.RecordingTemplate;
import com.atlassian.troubleshooting.jfr.event.JfrDumpAddedToSupportZipAnalyticsEvent;
import com.atlassian.troubleshooting.jfr.manager.JfrRecordingManager;
import com.atlassian.troubleshooting.jfr.service.JfrEventExtractorService;
import com.atlassian.troubleshooting.jfr.util.JfrRecordingUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class JfrDumpBundle
implements SupportZipBundle {
    private static final Logger LOG = LoggerFactory.getLogger(JfrDumpBundle.class);
    private static final String I18N_TITLE = "stp.zip.include.common.jfr.title";
    private static final String I18N_DESCRIPTION = "stp.zip.include.common.jfr.description";
    private static final String I18N_NOT_AVAILABLE = "stp.zip.include.common.jfr.not.available";
    private static final String I18N_NOT_INITIALIZED = "stp.zip.include.common.jfr.not.initialized";
    private static final String I18N_NOT_ENABLED = "stp.zip.include.common.jfr.not.enabled";
    private final JfrRecordingManager jfrRecordingManager;
    private final JfrEventExtractorService jfrExtractorService;
    private final I18nResolver i18nResolver;
    private final EventPublisher eventPublisher;
    private final JfrProperties jfrProperties;

    @Autowired
    public JfrDumpBundle(JfrRecordingManager jfrRecordingManager, JfrEventExtractorService jfrExtractorService, I18nResolver i18nResolver, EventPublisher eventPublisher, JfrProperties jfrProperties) {
        this.jfrRecordingManager = Objects.requireNonNull(jfrRecordingManager);
        this.jfrExtractorService = Objects.requireNonNull(jfrExtractorService);
        this.i18nResolver = Objects.requireNonNull(i18nResolver);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.jfrProperties = Objects.requireNonNull(jfrProperties);
    }

    @Override
    public String getTitle() {
        return this.i18nResolver.getText(I18N_TITLE);
    }

    @Override
    public BundleCategory getCategory() {
        return BundleCategory.OTHER;
    }

    @Override
    public String getDescription() {
        return this.i18nResolver.getText(I18N_DESCRIPTION);
    }

    @Override
    public Collection<SupportZipBundle.Artifact> getArtifacts() {
        ArrayList<SupportZipBundle.Artifact> artifacts = new ArrayList<SupportZipBundle.Artifact>();
        this.jfrRecordingManager.getRecordingDetails().stream().filter(details -> RecordingTemplate.DEFAULT.getRecordingName().equals(details.getName())).findAny().ifPresent(recordingDetails -> this.addToSupportZip((Collection<SupportZipBundle.Artifact>)artifacts, (RecordingDetails)recordingDetails));
        return artifacts;
    }

    @Override
    public String getKey() {
        return "jfr-bundle";
    }

    @Override
    public boolean isSelected() {
        return true;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public boolean isApplicable() {
        JfrCapabilities jfrCapabilities = this.jfrRecordingManager.getCapabilities();
        JfrSettings settings = this.jfrRecordingManager.getSettings();
        return jfrCapabilities.isAvailable() && jfrCapabilities.isInitialized() && settings.isEnabled();
    }

    @Override
    public String getApplicabilityReason() {
        JfrCapabilities jfrCapabilities = this.jfrRecordingManager.getCapabilities();
        JfrSettings settings = this.jfrRecordingManager.getSettings();
        if (!jfrCapabilities.isAvailable()) {
            return this.i18nResolver.getText(I18N_NOT_AVAILABLE);
        }
        if (!jfrCapabilities.isInitialized()) {
            return this.i18nResolver.getText(I18N_NOT_INITIALIZED);
        }
        if (!settings.isEnabled()) {
            return this.i18nResolver.getText(I18N_NOT_ENABLED);
        }
        return SupportZipBundle.super.getApplicabilityReason();
    }

    private void addToSupportZip(Collection<SupportZipBundle.Artifact> artifacts, RecordingDetails recordingDetails) {
        this.jfrRecordingManager.dumpRecording(recordingDetails.getId()).ifPresent(dumpPath -> {
            Path threadDumpsDir = this.jfrExtractorService.extractThreadDumps((Path)dumpPath);
            this.jfrExtractorService.extractThreadCpuLoadDumps((Path)dumpPath);
            Path threadDumpsPath = threadDumpsDir.getParent().getFileName().resolve(this.jfrProperties.getThreadDumpPath());
            Set<Path> dumpPaths = JfrRecordingUtils.listJfrDumps(dumpPath.getParent());
            dumpPaths.forEach(pathToDump -> artifacts.add(new FileSupportZipArtifact(pathToDump.toFile())));
            this.writeActiveRecordingSettings(recordingDetails).ifPresent(artifacts::add);
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(threadDumpsDir);){
                dirStream.forEach(threadDump -> {
                    if (!Files.isDirectory(threadDump, new LinkOption[0])) {
                        artifacts.add(new FileSupportZipArtifact(threadDump.toFile(), threadDumpsPath.toString()));
                    }
                });
                this.eventPublisher.publish((Object)new JfrDumpAddedToSupportZipAnalyticsEvent(recordingDetails.getId(), recordingDetails.getName(), recordingDetails.getSize()));
            }
            catch (IOException exc) {
                LOG.error("Failed to extract thread dumps from recording {}", dumpPath, (Object)exc);
            }
        });
        LOG.debug("JFR successfully added to the support zip");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Optional<SupportZipBundle.Artifact> writeActiveRecordingSettings(RecordingDetails recordingDetails) {
        TreeMap<String, String> sortedSettingsMap = new TreeMap<String, String>(recordingDetails.getSettings());
        Path destinationDir = Paths.get(recordingDetails.getDestination(), new String[0]).getParent();
        Path settingsPath = destinationDir.resolve(RecordingTemplate.DEFAULT.getRecordingName() + ".settings");
        try (BufferedWriter out = Files.newBufferedWriter(settingsPath, new OpenOption[0]);){
            for (Map.Entry entry : sortedSettingsMap.entrySet()) {
                out.write((String)entry.getKey() + "=" + (String)entry.getValue());
                out.newLine();
            }
            Optional<FileSupportZipArtifact> optional = Optional.of(new FileSupportZipArtifact(settingsPath.toFile()));
            return optional;
        }
        catch (IOException exc) {
            LOG.error("Cannot write ongoing recording settings to a file");
            return Optional.empty();
        }
    }
}

