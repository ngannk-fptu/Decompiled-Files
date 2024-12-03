/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.tinymceplugin;

import com.atlassian.confluence.tinymceplugin.FontProvider;
import com.atlassian.core.util.ClassLoaderUtils;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFontProvider
implements FontProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultFontProvider.class);
    private static final String FONT_CLASS_PATH = "fonts/";
    private static final String FONT_TRUE_TYPE_EXTENSION = ".ttf";
    private volatile Font desiredFont;
    private Map<String, Font> bundledFonts = Collections.synchronizedMap(new HashMap(1));

    @Override
    public Font getFirstAvailableFont(String ... desiredFontNames) {
        if (this.desiredFont == null) {
            for (String desiredFontName : desiredFontNames) {
                Font font = new Font(desiredFontName, 0, 12);
                if (!desiredFontName.equals(font.getName()) && !desiredFontName.equals(font.getFamily())) continue;
                this.desiredFont = font;
                break;
            }
            if (this.desiredFont == null) {
                throw new RuntimeException("None of the desired fonts: " + Arrays.toString(desiredFontNames) + " could be found on this system.");
            }
        }
        return this.desiredFont;
    }

    @Override
    public Font getConfluenceFont(String name) {
        String fontPath;
        InputStream fontStream;
        if (!this.bundledFonts.containsKey(name) && (fontStream = ClassLoaderUtils.getResourceAsStream((String)(fontPath = FONT_CLASS_PATH + name + FONT_TRUE_TYPE_EXTENSION), this.getClass())) != null) {
            try {
                Font font = Font.createFont(0, fontStream);
                this.bundledFonts.put(name, font.deriveFont(12.0f));
            }
            catch (FontFormatException ex) {
                log.info("Attempted to load a non true-type font: " + name, (Throwable)ex);
            }
            catch (IOException ex) {
                log.info("Exception while trying to load the font file: " + fontPath, (Throwable)ex);
            }
        }
        return this.bundledFonts.get(name);
    }
}

