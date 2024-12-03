/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.flyingpdf.config.PdfExportSettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultPdfExportSettingsManager
implements PdfExportSettingsManager {
    private static final String STYLESHEET_KEY = "com.atlassian.confluence.extra.flyingpdf.config.style";
    private static final String TITLEPAGE_KEY = "com.atlassian.confluence.extra.flyingpdf.config.titlepage";
    private static final String HEADER_KEY = "com.atlassian.confluence.extra.flyingpdf.config.header";
    private static final String FOOTER_KEY = "com.atlassian.confluence.extra.flyingpdf.config.footer";
    private static final Logger log = LoggerFactory.getLogger(DefaultPdfExportSettingsManager.class);
    private final BandanaManager bandanaManager;

    public DefaultPdfExportSettingsManager(@ComponentImport BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public String getStyle(BandanaContext context) {
        return (String)this.bandanaManager.getValue(context, STYLESHEET_KEY);
    }

    @Override
    public void setStyle(BandanaContext context, String value) {
        value = this.nullOutIfEmpty(value);
        this.bandanaManager.setValue(context, STYLESHEET_KEY, (Object)value);
    }

    @Override
    public String getFooter(BandanaContext context) {
        return (String)this.bandanaManager.getValue(context, FOOTER_KEY);
    }

    @Override
    public String getHeader(BandanaContext context) {
        return (String)this.bandanaManager.getValue(context, HEADER_KEY);
    }

    @Override
    public String getTitlePage(BandanaContext context) {
        return (String)this.bandanaManager.getValue(context, TITLEPAGE_KEY);
    }

    @Override
    public void setFooter(BandanaContext context, String value) {
        value = this.nullOutIfEmpty(value);
        this.bandanaManager.setValue(context, FOOTER_KEY, (Object)value);
    }

    @Override
    public void setHeader(BandanaContext context, String value) {
        value = this.nullOutIfEmpty(value);
        this.bandanaManager.setValue(context, HEADER_KEY, (Object)value);
    }

    @Override
    public void setTitlePage(BandanaContext context, String value) {
        value = this.nullOutIfEmpty(value);
        this.bandanaManager.setValue(context, TITLEPAGE_KEY, (Object)value);
    }

    private String nullOutIfEmpty(String value) {
        if (StringUtils.isEmpty((CharSequence)value)) {
            value = null;
        }
        return value;
    }
}

