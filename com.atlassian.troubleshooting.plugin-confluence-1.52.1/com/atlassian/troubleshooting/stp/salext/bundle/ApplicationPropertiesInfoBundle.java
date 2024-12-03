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
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.FileSupportZipArtifact;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.AbstractSupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationPropertiesInfoBundle
extends AbstractSupportZipBundle {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationPropertiesInfoBundle.class);
    private final SupportApplicationInfo info;

    @Autowired
    public ApplicationPropertiesInfoBundle(SupportApplicationInfo info, I18nResolver i18nResolver) {
        super(i18nResolver, BundleManifest.APPLICATION_PROPERTIES, "stp.zip.include.application.properties", "stp.zip.include.application.properties.description");
        this.info = info;
    }

    @Override
    public BundleCategory getCategory() {
        return BundleCategory.OTHER;
    }

    @Override
    public Collection<SupportZipBundle.Artifact> getArtifacts() {
        ArrayList<SupportZipBundle.Artifact> files = new ArrayList<SupportZipBundle.Artifact>();
        File supportDir = new File(this.info.getApplicationHome(), "logs/support");
        if (!supportDir.exists() && !supportDir.mkdirs()) {
            LOG.error("Couldn't create export directory {}", (Object)supportDir.getAbsolutePath());
            return files;
        }
        File propertiesFile = new File(supportDir, "application.xml");
        try (FileWriter out = new FileWriter(propertiesFile);){
            try {
                String propertiesString = this.info.saveProperties(SupportDataDetail.FULL);
                out.write(propertiesString);
            }
            catch (IOException e) {
                LOG.error("Failed to write application properties to {}.", (Object)propertiesFile.getPath(), (Object)e);
            }
            files.add(new FileSupportZipArtifact(propertiesFile));
        }
        catch (Exception e) {
            LOG.error("Can't generate properties file.", (Throwable)e);
        }
        return files;
    }
}

