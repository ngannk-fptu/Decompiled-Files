/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.afm.AFMParser
 *  org.apache.fontbox.afm.FontMetrics
 */
package org.apache.pdfbox.pdmodel.font;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.fontbox.afm.AFMParser;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

final class Standard14Fonts {
    private static final Map<String, String> ALIASES = new HashMap<String, String>(38);
    private static final Map<String, FontMetrics> FONTS = new HashMap<String, FontMetrics>(14);

    private Standard14Fonts() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadMetrics(String fontName) throws IOException {
        String resourceName = "/org/apache/pdfbox/resources/afm/" + fontName + ".afm";
        InputStream resourceAsStream = PDType1Font.class.getResourceAsStream(resourceName);
        if (resourceAsStream == null) {
            throw new IOException("resource '" + resourceName + "' not found");
        }
        BufferedInputStream afmStream = new BufferedInputStream(resourceAsStream);
        try {
            AFMParser parser = new AFMParser((InputStream)afmStream);
            FontMetrics metric = parser.parse(true);
            FONTS.put(fontName, metric);
        }
        finally {
            ((InputStream)afmStream).close();
        }
    }

    private static void mapName(String baseName) {
        ALIASES.put(baseName, baseName);
    }

    private static void mapName(String alias, String baseName) {
        ALIASES.put(alias, baseName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static FontMetrics getAFM(String fontName) {
        String baseName = ALIASES.get(fontName);
        if (baseName == null) {
            return null;
        }
        if (FONTS.get(baseName) == null) {
            Map<String, FontMetrics> map = FONTS;
            synchronized (map) {
                if (FONTS.get(baseName) == null) {
                    try {
                        Standard14Fonts.loadMetrics(baseName);
                    }
                    catch (IOException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                }
            }
        }
        return FONTS.get(baseName);
    }

    public static boolean containsName(String fontName) {
        return ALIASES.containsKey(fontName);
    }

    public static Set<String> getNames() {
        return Collections.unmodifiableSet(ALIASES.keySet());
    }

    public static String getMappedFontName(String fontName) {
        return ALIASES.get(fontName);
    }

    static {
        Standard14Fonts.mapName("Courier-Bold");
        Standard14Fonts.mapName("Courier-BoldOblique");
        Standard14Fonts.mapName("Courier");
        Standard14Fonts.mapName("Courier-Oblique");
        Standard14Fonts.mapName("Helvetica");
        Standard14Fonts.mapName("Helvetica-Bold");
        Standard14Fonts.mapName("Helvetica-BoldOblique");
        Standard14Fonts.mapName("Helvetica-Oblique");
        Standard14Fonts.mapName("Symbol");
        Standard14Fonts.mapName("Times-Bold");
        Standard14Fonts.mapName("Times-BoldItalic");
        Standard14Fonts.mapName("Times-Italic");
        Standard14Fonts.mapName("Times-Roman");
        Standard14Fonts.mapName("ZapfDingbats");
        Standard14Fonts.mapName("CourierCourierNew", "Courier");
        Standard14Fonts.mapName("CourierNew", "Courier");
        Standard14Fonts.mapName("CourierNew,Italic", "Courier-Oblique");
        Standard14Fonts.mapName("CourierNew,Bold", "Courier-Bold");
        Standard14Fonts.mapName("CourierNew,BoldItalic", "Courier-BoldOblique");
        Standard14Fonts.mapName("Arial", "Helvetica");
        Standard14Fonts.mapName("Arial,Italic", "Helvetica-Oblique");
        Standard14Fonts.mapName("Arial,Bold", "Helvetica-Bold");
        Standard14Fonts.mapName("Arial,BoldItalic", "Helvetica-BoldOblique");
        Standard14Fonts.mapName("TimesNewRoman", "Times-Roman");
        Standard14Fonts.mapName("TimesNewRoman,Italic", "Times-Italic");
        Standard14Fonts.mapName("TimesNewRoman,Bold", "Times-Bold");
        Standard14Fonts.mapName("TimesNewRoman,BoldItalic", "Times-BoldItalic");
        Standard14Fonts.mapName("Symbol,Italic", "Symbol");
        Standard14Fonts.mapName("Symbol,Bold", "Symbol");
        Standard14Fonts.mapName("Symbol,BoldItalic", "Symbol");
        Standard14Fonts.mapName("Times", "Times-Roman");
        Standard14Fonts.mapName("Times,Italic", "Times-Italic");
        Standard14Fonts.mapName("Times,Bold", "Times-Bold");
        Standard14Fonts.mapName("Times,BoldItalic", "Times-BoldItalic");
        Standard14Fonts.mapName("ArialMT", "Helvetica");
        Standard14Fonts.mapName("Arial-ItalicMT", "Helvetica-Oblique");
        Standard14Fonts.mapName("Arial-BoldMT", "Helvetica-Bold");
        Standard14Fonts.mapName("Arial-BoldItalicMT", "Helvetica-BoldOblique");
    }
}

