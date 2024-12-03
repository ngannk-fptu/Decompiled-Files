/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.ConfluenceDirectories
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.core.io.Resource
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.upgrade;

import com.atlassian.confluence.extra.flyingpdf.config.FontManager;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class UpgradePdfLanguageSupport
implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpgradePdfLanguageSupport.class);
    private final FontManager pdfExportFontManager;
    private final ConfluenceDirectories confluenceDirectories;
    private final ApplicationProperties applicationProperties;

    public UpgradePdfLanguageSupport(@ComponentImport ConfluenceDirectories confluenceDirectories, @ComponentImport ApplicationProperties applicationProperties, FontManager pdfExportFontManager) {
        this.applicationProperties = applicationProperties;
        this.pdfExportFontManager = pdfExportFontManager;
        this.confluenceDirectories = confluenceDirectories;
    }

    public void afterPropertiesSet() {
        if (this.pdfExportFontManager.isCustomFontInstalled()) {
            return;
        }
        File fontDir = new File(this.applicationProperties.getHomeDirectory(), "fonts");
        if (!fontDir.exists()) {
            return;
        }
        File[] fontFiles = fontDir.listFiles();
        if (fontFiles == null || fontFiles.length == 0) {
            return;
        }
        if (fontFiles.length > 1) {
            LOGGER.warn("More than one file was found in the font directory - custom font will not be migrated for use with the PDF Export plugin.");
            return;
        }
        try {
            File tempFontDir = this.confluenceDirectories.getTempDirectory().resolve(String.valueOf(System.currentTimeMillis())).toFile();
            if (!tempFontDir.mkdir()) {
                throw new IOException("Failed to create the temporary font storage directory " + tempFontDir);
            }
            File tempFontFile = new File(tempFontDir, fontFiles[0].getName());
            if (!fontFiles[0].renameTo(tempFontFile)) {
                throw new IOException("Could not move the currently installed font " + fontFiles[0].getAbsolutePath() + " to a temporary location of " + tempFontDir);
            }
            this.pdfExportFontManager.installFont((Resource)new FileSystemResource(tempFontFile));
            LOGGER.info("Successfully made the font " + fontFiles[0] + " available for use in the PDF export plugin.");
        }
        catch (IOException ex) {
            LOGGER.error("Failed to make the font " + fontFiles[0] + " available for use in the PDF export plugin. Please install the font manually.", (Throwable)ex);
        }
    }
}

