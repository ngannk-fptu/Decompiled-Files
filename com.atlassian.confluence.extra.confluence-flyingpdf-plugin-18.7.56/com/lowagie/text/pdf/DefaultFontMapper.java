/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.FontMapper;
import java.awt.Font;
import java.io.File;
import java.util.HashMap;

public class DefaultFontMapper
implements FontMapper {
    private HashMap<String, String> aliases = new HashMap();
    private HashMap<String, BaseFontParameters> mapper = new HashMap();

    @Override
    public BaseFont awtToPdf(Font font) {
        try {
            BaseFontParameters p = this.getBaseFontParameters(font.getFontName());
            if (p != null) {
                return BaseFont.createFont(p.fontName, p.encoding, p.embedded, p.cached, p.ttfAfm, p.pfb);
            }
            String logicalName = font.getName();
            String fontKey = logicalName.equalsIgnoreCase("DialogInput") || logicalName.equalsIgnoreCase("Monospaced") || logicalName.equalsIgnoreCase("Courier") ? (font.isItalic() ? (font.isBold() ? "Courier-BoldOblique" : "Courier-Oblique") : (font.isBold() ? "Courier-Bold" : "Courier")) : (logicalName.equalsIgnoreCase("Serif") || logicalName.equalsIgnoreCase("TimesRoman") ? (font.isItalic() ? (font.isBold() ? "Times-BoldItalic" : "Times-Italic") : (font.isBold() ? "Times-Bold" : "Times-Roman")) : (font.isItalic() ? (font.isBold() ? "Helvetica-BoldOblique" : "Helvetica-Oblique") : (font.isBold() ? "Helvetica-Bold" : "Helvetica")));
            return BaseFont.createFont(fontKey, "Cp1252", false);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    public Font pdfToAwt(BaseFont font, int size) {
        String finalName;
        String[][] names = font.getFullFontName();
        if (names.length == 1) {
            return new Font(names[0][3], 0, size);
        }
        String name10 = null;
        String name3x = null;
        for (String[] name : names) {
            if (name[0].equals("1") && name[1].equals("0")) {
                name10 = name[3];
                continue;
            }
            if (!name[2].equals("1033")) continue;
            name3x = name[3];
            break;
        }
        if ((finalName = name3x) == null) {
            finalName = name10;
        }
        if (finalName == null) {
            finalName = names[0][3];
        }
        return new Font(finalName, 0, size);
    }

    public void putName(String awtName, BaseFontParameters parameters) {
        this.mapper.put(awtName, parameters);
    }

    public void putAlias(String alias, String awtName) {
        this.aliases.put(alias, awtName);
    }

    public BaseFontParameters getBaseFontParameters(String name) {
        String alias = this.aliases.get(name);
        if (alias == null) {
            return this.mapper.get(name);
        }
        BaseFontParameters p = this.mapper.get(alias);
        if (p == null) {
            return this.mapper.get(name);
        }
        return p;
    }

    public void insertNames(Object[] allNames, String path) {
        String[][] names = (String[][])allNames[2];
        String main = null;
        for (String[] name : names) {
            if (!name[2].equals("1033")) continue;
            main = name[3];
            break;
        }
        if (main == null) {
            main = names[0][3];
        }
        BaseFontParameters p = new BaseFontParameters(path);
        this.mapper.put(main, p);
        for (String[] name : names) {
            this.aliases.put(name[3], main);
        }
        this.aliases.put((String)allNames[0], main);
    }

    public int insertDirectory(String dir) {
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) {
            return 0;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return 0;
        }
        int count = 0;
        File[] fileArray = files;
        int n = fileArray.length;
        for (int i = 0; i < n; ++i) {
            File file1;
            file = file1 = fileArray[i];
            String name = file.getPath().toLowerCase();
            try {
                if (name.endsWith(".ttf") || name.endsWith(".otf") || name.endsWith(".afm")) {
                    Object[] allNames = BaseFont.getAllFontNames(file.getPath(), "Cp1252", null);
                    this.insertNames(allNames, file.getPath());
                    ++count;
                    continue;
                }
                if (!name.endsWith(".ttc")) continue;
                String[] ttcs = BaseFont.enumerateTTCNames(file.getPath());
                for (int j = 0; j < ttcs.length; ++j) {
                    String nt = file.getPath() + "," + j;
                    Object[] allNames = BaseFont.getAllFontNames(nt, "Cp1252", null);
                    this.insertNames(allNames, nt);
                }
                ++count;
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return count;
    }

    public HashMap<String, BaseFontParameters> getMapper() {
        return this.mapper;
    }

    public HashMap<String, String> getAliases() {
        return this.aliases;
    }

    public static class BaseFontParameters {
        public String fontName;
        public String encoding;
        public boolean embedded;
        public boolean cached;
        public byte[] ttfAfm;
        public byte[] pfb;

        public BaseFontParameters(String fontName) {
            this.fontName = fontName;
            this.encoding = "Cp1252";
            this.embedded = true;
            this.cached = true;
        }
    }
}

