/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.FileSupportZipArtifact;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.AbstractSupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationConfigurationFileBundle
extends AbstractSupportZipBundle {
    public static final String TITLE = "stp.zip.include.auth.cfg";
    public static final String DESCRIPTION = "stp.zip.include.auth.cfg.description";
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationConfigurationFileBundle.class);
    private static final String NO_CROWD_DIRECTORY_INFO_MSG = "Troubleshooting and Support Tool did not find any directory configuration information.";
    private final CrowdDirectoryService dirService;
    private final SupportApplicationInfo info;
    private final String[] files;

    public AuthenticationConfigurationFileBundle(SupportApplicationInfo info, CrowdDirectoryService dirService, I18nResolver i18nResolver, String ... files) {
        super(i18nResolver, BundleManifest.AUTH_CONFIG, TITLE, DESCRIPTION);
        this.dirService = dirService;
        this.info = info;
        this.files = files;
    }

    @Override
    public BundleCategory getCategory() {
        return BundleCategory.CONFIG;
    }

    @Override
    public Collection<SupportZipBundle.Artifact> getArtifacts() {
        File supportDir = new File(this.info.getApplicationHome(), "logs/support");
        if (!supportDir.exists() && !supportDir.mkdirs()) {
            LOG.error("Couldn't create export directory {}", (Object)supportDir.getAbsolutePath());
            return Collections.emptyList();
        }
        List<SupportZipBundle.Artifact> fileArtifacts = Arrays.stream(this.files).map(f -> new FileSupportZipArtifact(new File((String)f))).collect(Collectors.toList());
        File dirConfigFile = new File(supportDir, "directoryConfigurationSummary.txt");
        try (FileWriter configWriter = new FileWriter(dirConfigFile);){
            try {
                configWriter.write(this.getDirectories());
            }
            catch (IOException e) {
                LOG.error("Failed to write directory configuration to {}.", (Object)dirConfigFile.getPath(), (Object)e);
            }
            fileArtifacts.add(new FileSupportZipArtifact(dirConfigFile));
        }
        catch (Exception e) {
            LOG.error("Can't generate directory configuration file.", (Throwable)e);
        }
        return fileArtifacts;
    }

    private String getDirectories() {
        StringBuilder configString = new StringBuilder();
        try {
            List dirs = this.dirService.findAllDirectories();
            if (dirs.isEmpty()) {
                configString.append(NO_CROWD_DIRECTORY_INFO_MSG);
            } else {
                this.formatDirectoryInformation(configString, dirs);
            }
        }
        catch (RuntimeException re) {
            LOG.error("Error getting or formatting directory information for zip file.", (Throwable)re);
            configString.append(NO_CROWD_DIRECTORY_INFO_MSG);
        }
        return configString.toString();
    }

    private StringBuilder formatDirectoryInformation(StringBuilder configString, List<Directory> dirs) {
        configString.append("=== Directories configured ===\n\n");
        for (Directory dir : dirs) {
            configString.append("Directory ID: ").append(dir.getId()).append("\n");
            configString.append("Name: ").append(dir.getName()).append("\n");
            configString.append("Active: ").append(dir.isActive()).append("\n");
            configString.append("Type: ").append(dir.getType()).append("\n");
            configString.append("Created date: ").append(dir.getCreatedDate()).append("\n");
            configString.append("Updated date: ").append(dir.getUpdatedDate()).append("\n");
            configString.append("Allowed operations: ").append(dir.getAllowedOperations()).append("\n");
            configString.append("Implementation class: ").append(dir.getImplementationClass()).append("\n");
            configString.append("Encryption type: ").append(dir.getEncryptionType()).append("\n");
            configString.append("Attributes: \n");
            for (Map.Entry attribute : dir.getAttributes().entrySet()) {
                configString.append("\t").append((String)attribute.getKey()).append(": ").append((String)attribute.getValue()).append("\n");
            }
            configString.append("\n");
        }
        return configString;
    }
}

