/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.ConfluenceDirectories
 *  com.atlassian.core.util.thumbnail.Thumber
 *  com.atlassian.core.util.thumbnail.Thumbnail$MimeType
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.lookandfeel;

import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.core.util.thumbnail.Thumber;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.File;
import java.net.MalformedURLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ImageScaler {
    private static final Logger log = LoggerFactory.getLogger(ImageScaler.class);
    public static final String TEMP_DIR = "scaledImages";
    private final Thumber thumber = new Thumber(Thumbnail.MimeType.PNG);
    private final ConfluenceDirectories confluenceDirectories;

    public ImageScaler(@ComponentImport ConfluenceDirectories confluenceDirectories) {
        this.confluenceDirectories = confluenceDirectories;
    }

    public File scaleImageToMaxHeight(File file, int maxHeight) {
        File outputFile = new File(this.getDirectoryForScaledImages(), file.getName());
        try {
            this.thumber.retrieveOrCreateThumbNail(file, outputFile, Integer.MAX_VALUE, maxHeight, 0L);
        }
        catch (MalformedURLException e) {
            log.error("Error when scaling the image.", (Throwable)e);
            return null;
        }
        return outputFile;
    }

    private File getDirectoryForScaledImages() {
        File dir = this.confluenceDirectories.getTempDirectory().resolve(TEMP_DIR).toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}

