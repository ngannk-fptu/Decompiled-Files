/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableList
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.FileSupportZipArtifact;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.AbstractSupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class CloudMigrationLogsBundle
extends AbstractSupportZipBundle {
    private static final String MIG_TO_CLOUD_DIR = "migration-to-cloud";
    private final SupportApplicationInfo applicationInfo;

    @Autowired
    public CloudMigrationLogsBundle(SupportApplicationInfo applicationInfo, I18nResolver i18nResolver) {
        super(i18nResolver, BundleManifest.CLOUD_MIGRATION_LOGS, "stp.zip.include.cloud-migration.logs", "stp.zip.include.cloud-migration.logs.description");
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
    }

    @Override
    public BundleCategory getCategory() {
        return BundleCategory.LOGS;
    }

    public List<SupportZipBundle.Artifact> getArtifacts() {
        return ImmutableList.builder().addAll(this.getLegacyMigrationLogFiles()).addAll(this.getMigrationLogFiles()).build();
    }

    private List<SupportZipBundle.Artifact> getLegacyMigrationLogFiles() {
        return this.getLogFiles(new File(this.applicationInfo.getApplicationLogDir(), MIG_TO_CLOUD_DIR), "logs.zip");
    }

    private List<SupportZipBundle.Artifact> getMigrationLogFiles() {
        return this.applicationInfo.getExportFile(MIG_TO_CLOUD_DIR).map(dir -> this.getLogFiles((File)dir, "log/logs.zip")).orElse(Collections.emptyList());
    }

    private List<SupportZipBundle.Artifact> getLogFiles(File baseDir, String logFilePath) {
        ArrayList<SupportZipBundle.Artifact> fileArtifacts = new ArrayList<SupportZipBundle.Artifact>();
        File[] migrationDirs = baseDir.listFiles(File::isDirectory);
        if (migrationDirs != null) {
            for (File migration : migrationDirs) {
                File logFile = new File(migration, logFilePath);
                if (!logFile.isFile()) continue;
                fileArtifacts.add(new FileSupportZipArtifact(logFile, migration.getName()));
            }
        }
        return fileArtifacts;
    }
}

