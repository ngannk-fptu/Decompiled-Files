/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.usermodel;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.FontDetails;

final class StaticFontMetrics {
    private static final Logger LOGGER = LogManager.getLogger(StaticFontMetrics.class);
    private static Properties fontMetricsProps;
    private static final Map<String, FontDetails> fontDetailsMap;

    private StaticFontMetrics() {
    }

    public static synchronized FontDetails getFontDetails(Font font) {
        FontDetails fontDetails;
        if (fontMetricsProps == null) {
            try {
                fontMetricsProps = StaticFontMetrics.loadMetrics();
            }
            catch (IOException e) {
                throw new RuntimeException("Could not load font metrics", e);
            }
        }
        String fontName = font.getName();
        String fontStyle = "";
        if (font.isPlain()) {
            fontStyle = fontStyle + "plain";
        }
        if (font.isBold()) {
            fontStyle = fontStyle + "bold";
        }
        if (font.isItalic()) {
            fontStyle = fontStyle + "italic";
        }
        String fontHeight = FontDetails.buildFontHeightProperty(fontName);
        String styleHeight = FontDetails.buildFontHeightProperty(fontName + "." + fontStyle);
        if (fontMetricsProps.get(fontHeight) == null && fontMetricsProps.get(styleHeight) != null) {
            fontName = fontName + "." + fontStyle;
        }
        if ((fontDetails = fontDetailsMap.get(fontName)) == null) {
            fontDetails = FontDetails.create(fontName, fontMetricsProps);
            fontDetailsMap.put(fontName, fontDetails);
        }
        return fontDetails;
    }

    private static Properties loadMetrics() throws IOException {
        File propFile = null;
        try {
            String propFileName = System.getProperty("font.metrics.filename");
            if (propFileName != null && !(propFile = new File(propFileName)).exists()) {
                LOGGER.atWarn().log("font_metrics.properties not found at path {}", (Object)propFile.getAbsolutePath());
                propFile = null;
            }
        }
        catch (SecurityException e) {
            LOGGER.atWarn().withThrowable(e).log("Can't access font.metrics.filename system property");
        }
        try (InputStream metricsIn = propFile != null ? new FileInputStream(propFile) : FontDetails.class.getResourceAsStream("/font_metrics.properties");){
            if (metricsIn == null) {
                String err = "font_metrics.properties not found in classpath";
                throw new IOException(err);
            }
            Properties props = new Properties();
            props.load(metricsIn);
            Properties properties = props;
            return properties;
        }
    }

    static {
        fontDetailsMap = new HashMap<String, FontDetails>();
    }
}

