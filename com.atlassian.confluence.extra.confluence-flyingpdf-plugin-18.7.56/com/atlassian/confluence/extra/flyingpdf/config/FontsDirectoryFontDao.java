/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.core.io.Resource
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.confluence.extra.flyingpdf.config.FontDao;
import com.atlassian.core.util.FileUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class FontsDirectoryFontDao
implements FontDao {
    public static final String FONTS_DIR = "fonts";
    private final ApplicationProperties applicationProperties;

    public FontsDirectoryFontDao(@ComponentImport ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public Resource getFont(String fontName) throws IOException {
        File fontFile = new File(this.getBaseFontsDir(), fontName);
        if (!fontFile.exists()) {
            throw new IOException("No font file could be found with the name " + fontName);
        }
        return new FileSystemResource(fontFile);
    }

    @Override
    public void saveFont(String fontName, Resource fontResource) throws IOException {
        this.saveFont(fontName, fontResource, true);
    }

    @Override
    public void saveFont(String fontName, Resource fontResource, boolean overwrite) throws IOException {
        if (!fontResource.exists()) {
            throw new IOException("Attempting to install a font resource that does not exist: " + fontResource.getDescription());
        }
        File installedFontFile = new File(this.getBaseFontsDir(), fontName);
        try (InputStream istream = fontResource.getInputStream();){
            FileUtils.copyFile((InputStream)istream, (File)installedFontFile, (boolean)overwrite);
        }
    }

    @Override
    public void removeFont(String fontName) throws IOException {
        FileSystemResource fontResource = (FileSystemResource)this.getFont(fontName);
        File fontFile = fontResource.getFile();
        if (!fontFile.delete()) {
            throw new IOException("Failed to remove the font file " + fontFile.getAbsolutePath());
        }
    }

    private File getBaseFontsDir() throws IOException {
        File fontDir = new File(this.applicationProperties.getHomeDirectory(), FONTS_DIR);
        if (!fontDir.exists() && !fontDir.mkdir()) {
            throw new IOException("Failed to create the font directory " + fontDir.getAbsolutePath());
        }
        return fontDir;
    }
}

