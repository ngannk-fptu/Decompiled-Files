/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.core.io.Resource
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.flyingpdf.config.CustomFontInstalledEvent;
import com.atlassian.confluence.extra.flyingpdf.config.CustomFontRemovedEvent;
import com.atlassian.confluence.extra.flyingpdf.config.FontDao;
import com.atlassian.confluence.extra.flyingpdf.config.FontManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class PdfExportFontManager
implements FontManager {
    private static final String FONT_NAMES_KEY = "com.atlassian.confluence.extra.flyingpdf.fontname";
    private static final String DEFAULT_FONT_NAME = "customfont";
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfExportFontManager.class);
    private final FontDao fontDao;
    private final BandanaManager bandanaManager;
    private final EventPublisher eventPublisher;

    public PdfExportFontManager(FontDao fontDao, @ComponentImport BandanaManager bandanaManager, @ComponentImport EventPublisher eventPublisher) {
        this.fontDao = fontDao;
        this.bandanaManager = bandanaManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public FileSystemResource getInstalledFont() {
        Resource fontResource;
        String customFont = this.getFontFileName(DEFAULT_FONT_NAME);
        if (StringUtils.isBlank((CharSequence)customFont)) {
            return null;
        }
        try {
            fontResource = this.fontDao.getFont(customFont);
        }
        catch (IOException ex) {
            LOGGER.debug("No font resource could be found with the name " + customFont, (Throwable)ex);
            return null;
        }
        if (!(fontResource instanceof FileSystemResource)) {
            LOGGER.warn("The fontDao did not return the font " + customFont + " as a FileSystemResource");
            return null;
        }
        return (FileSystemResource)fontResource;
    }

    @Override
    public void installFont(Resource fontResource) throws IOException {
        String fontName;
        try {
            fontName = fontResource.getFilename();
        }
        catch (IllegalStateException ex) {
            throw new IOException("The supplied fontResource did not include a filename property.");
        }
        this.fontDao.saveFont(fontName, fontResource);
        String oldFont = this.getFontFileName(DEFAULT_FONT_NAME);
        if (StringUtils.isNotBlank((CharSequence)oldFont) && !oldFont.equals(fontName)) {
            this.fontDao.removeFont(oldFont);
        }
        if (!fontName.equals(oldFont)) {
            this.storeFontFileName(DEFAULT_FONT_NAME, fontName);
        }
        this.eventPublisher.publish((Object)new CustomFontInstalledEvent("PDF Export Font Manager", fontName, this.getFontData(fontResource)));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] getFontData(Resource fontResource) throws IOException {
        if (!fontResource.exists()) {
            throw new IOException("The font resource cannot be found for transfer to other nodes in the cluster: " + fontResource.getDescription());
        }
        ByteArrayOutputStream fontDataStream = new ByteArrayOutputStream();
        InputStream istream = fontResource.getInputStream();
        try {
            IOUtils.copy((InputStream)istream, (OutputStream)fontDataStream);
        }
        finally {
            IOUtils.closeQuietly((OutputStream)fontDataStream);
        }
        return fontDataStream.toByteArray();
    }

    @Override
    public boolean isCustomFontInstalled() {
        FileSystemResource customFont = this.getInstalledFont();
        return customFont != null;
    }

    @Override
    public void removeInstalledFont() throws IOException {
        String customFont = this.getFontFileName(DEFAULT_FONT_NAME);
        if (StringUtils.isBlank((CharSequence)customFont)) {
            LOGGER.debug("No custom font is installed.");
            return;
        }
        try {
            this.fontDao.removeFont(customFont);
        }
        catch (IOException ex) {
            LOGGER.warn("Failed to remove the custom font " + customFont, (Throwable)ex);
            return;
        }
        this.storeFontFileName(DEFAULT_FONT_NAME, "");
        this.eventPublisher.publish((Object)new CustomFontRemovedEvent("PDF Export Font Manager", customFont));
    }

    private String getFontFileName(String fontName) {
        Map fontNameMap = (Map)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, FONT_NAMES_KEY);
        if (fontNameMap != null) {
            return (String)fontNameMap.get(fontName);
        }
        return null;
    }

    private void storeFontFileName(String fontName, String fontFileName) {
        HashMap<String, String> fontNameMap = (HashMap<String, String>)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, FONT_NAMES_KEY);
        if (fontNameMap == null) {
            fontNameMap = new HashMap<String, String>(1);
        }
        fontNameMap.put(fontName, fontFileName);
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, FONT_NAMES_KEY, fontNameMap);
    }
}

