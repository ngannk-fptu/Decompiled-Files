/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.core.util.ClassLoaderUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarLanguageUtil {
    private static final Logger log = LoggerFactory.getLogger(CalendarLanguageUtil.class);
    private static final String PROPS_FILENAME = "calendar-language-util.properties";
    private static CalendarLanguageUtil instance;
    private Properties mappings = new Properties();

    public static synchronized CalendarLanguageUtil getInstance() {
        if (instance == null) {
            instance = new CalendarLanguageUtil();
        }
        return instance;
    }

    private CalendarLanguageUtil() {
        InputStream is = null;
        try {
            is = ClassLoaderUtils.getResourceAsStream((String)PROPS_FILENAME, CalendarLanguageUtil.class);
            this.mappings.load(is);
        }
        catch (IOException e) {
            log.warn("Unable to load the calendar-language-util.properties file");
        }
        if (is != null) {
            try {
                is.close();
            }
            catch (IOException e) {
                log.warn("Unable to close calendar-language-util.properties file.");
            }
        }
    }

    public boolean hasTranslationForLanguage(String language) {
        return language != null && this.mappings.containsKey(language);
    }

    public String getCalendarFilenameForLanguage(String language) {
        String fileName = null;
        if (language != null) {
            fileName = this.mappings.getProperty(language);
        }
        if (fileName == null) {
            fileName = this.mappings.getProperty(Locale.ENGLISH.getLanguage());
        }
        return fileName;
    }
}

