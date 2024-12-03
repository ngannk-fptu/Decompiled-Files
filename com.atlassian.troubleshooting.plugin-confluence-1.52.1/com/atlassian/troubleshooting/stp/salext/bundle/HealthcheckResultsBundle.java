/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckManager;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.FileSupportZipArtifact;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.AbstractSupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class HealthcheckResultsBundle
extends AbstractSupportZipBundle {
    private static final Logger LOG = LoggerFactory.getLogger(HealthcheckResultsBundle.class);
    private final SupportApplicationInfo info;
    private final SupportHealthCheckManager supportHealthCheckManager;

    @Autowired
    public HealthcheckResultsBundle(SupportApplicationInfo info, SupportHealthCheckManager supportHealthCheckManager, I18nResolver i18nResolver) {
        super(i18nResolver, BundleManifest.HEALTHCHECKS, "stp.zip.include.healthchecks", "stp.zip.include.healthchecks.description");
        this.info = info;
        this.supportHealthCheckManager = supportHealthCheckManager;
    }

    @Override
    public BundleCategory getCategory() {
        return BundleCategory.OTHER;
    }

    @Override
    public Collection<SupportZipBundle.Artifact> getArtifacts() {
        File supportDir = new File(this.info.getApplicationHome(), "logs/support");
        if (supportDir.exists() || supportDir.mkdirs()) {
            return this.getHealthcheckResults(supportDir);
        }
        LOG.error("Couldn't create export directory {}", (Object)supportDir.getAbsolutePath());
        return Collections.emptyList();
    }

    private List<SupportZipBundle.Artifact> getHealthcheckResults(File directory) {
        ArrayList<SupportZipBundle.Artifact> tmpResults = new ArrayList<SupportZipBundle.Artifact>();
        StringBuilder sb = new StringBuilder();
        this.supportHealthCheckManager.runAllHealthChecks().forEach(status -> sb.append(status.toString()).append("\n"));
        try {
            File resultsFile = new File(directory, "healthcheckResults.txt");
            try (FileWriter writer = new FileWriter(resultsFile);){
                String resultsString = sb.toString();
                if (resultsString.isEmpty()) {
                    writer.write("No healthcheck results");
                }
                writer.write(resultsString);
            }
            catch (IOException e) {
                LOG.error("Failed to write healthchecks to {}.", (Object)resultsFile.getPath(), (Object)e);
            }
            tmpResults.add(new FileSupportZipArtifact(resultsFile));
        }
        catch (Exception e) {
            LOG.error("Can't generate healthchecks results file.", (Throwable)e);
        }
        return tmpResults;
    }
}

