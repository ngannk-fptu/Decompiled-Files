/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontProvider;
import com.lowagie.text.Utilities;
import com.lowagie.text.html.Markup;
import com.lowagie.text.pdf.BaseFont;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.Nullable;

public class FontFactoryImp
implements FontProvider {
    private Map<String, String> trueTypeFonts = new HashMap<String, String>();
    private static String[] TTFamilyOrder = new String[]{"3", "1", "1033", "3", "0", "1033", "1", "0", "0", "0", "3", "0"};
    private Map<String, List<String>> fontFamilies = new HashMap<String, List<String>>();
    public String defaultEncoding = "Cp1252";
    public boolean defaultEmbedding = false;

    public FontFactoryImp() {
        this.trueTypeFonts.put("Courier".toLowerCase(Locale.ROOT), "Courier");
        this.trueTypeFonts.put("Courier-Bold".toLowerCase(Locale.ROOT), "Courier-Bold");
        this.trueTypeFonts.put("Courier-Oblique".toLowerCase(Locale.ROOT), "Courier-Oblique");
        this.trueTypeFonts.put("Courier-BoldOblique".toLowerCase(Locale.ROOT), "Courier-BoldOblique");
        this.trueTypeFonts.put("Helvetica".toLowerCase(Locale.ROOT), "Helvetica");
        this.trueTypeFonts.put("Helvetica-Bold".toLowerCase(Locale.ROOT), "Helvetica-Bold");
        this.trueTypeFonts.put("Helvetica-Oblique".toLowerCase(Locale.ROOT), "Helvetica-Oblique");
        this.trueTypeFonts.put("Helvetica-BoldOblique".toLowerCase(Locale.ROOT), "Helvetica-BoldOblique");
        this.trueTypeFonts.put("Symbol".toLowerCase(Locale.ROOT), "Symbol");
        this.trueTypeFonts.put("Times-Roman".toLowerCase(Locale.ROOT), "Times-Roman");
        this.trueTypeFonts.put("Times-Bold".toLowerCase(Locale.ROOT), "Times-Bold");
        this.trueTypeFonts.put("Times-Italic".toLowerCase(Locale.ROOT), "Times-Italic");
        this.trueTypeFonts.put("Times-BoldItalic".toLowerCase(Locale.ROOT), "Times-BoldItalic");
        this.trueTypeFonts.put("ZapfDingbats".toLowerCase(Locale.ROOT), "ZapfDingbats");
        ArrayList<String> tmp = new ArrayList<String>();
        tmp.add("Courier");
        tmp.add("Courier-Bold");
        tmp.add("Courier-Oblique");
        tmp.add("Courier-BoldOblique");
        this.fontFamilies.put("Courier".toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList();
        tmp.add("Helvetica");
        tmp.add("Helvetica-Bold");
        tmp.add("Helvetica-Oblique");
        tmp.add("Helvetica-BoldOblique");
        this.fontFamilies.put("Helvetica".toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList();
        tmp.add("Symbol");
        this.fontFamilies.put("Symbol".toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList();
        tmp.add("Times-Roman");
        tmp.add("Times-Bold");
        tmp.add("Times-Italic");
        tmp.add("Times-BoldItalic");
        this.fontFamilies.put("Times".toLowerCase(Locale.ROOT), tmp);
        this.fontFamilies.put("Times-Roman".toLowerCase(Locale.ROOT), tmp);
        tmp = new ArrayList();
        tmp.add("ZapfDingbats");
        this.fontFamilies.put("ZapfDingbats".toLowerCase(Locale.ROOT), tmp);
    }

    @Override
    public Font getFont(@Nullable String fontName, String encoding, boolean embedded, float size, int style, @Nullable Color color) {
        return this.getFont(fontName, encoding, embedded, size, style, color, true);
    }

    public Font getFont(@Nullable String fontname, String encoding, boolean embedded, float size, int style, @Nullable Color color, boolean cached) {
        if (fontname == null) {
            return new Font(-1, size, style, color);
        }
        String lowerCaseFontname = fontname.toLowerCase(Locale.ROOT);
        List<String> tmp = this.fontFamilies.get(lowerCaseFontname);
        if (tmp != null) {
            int s = style == -1 ? 0 : style;
            for (String f : tmp) {
                int fs = this.getFontStyle(f);
                if ((s & 3) != fs) continue;
                fontname = f;
                style = s == fs ? 0 : s;
                break;
            }
        }
        BaseFont basefont = null;
        try {
            try {
                basefont = BaseFont.createFont(fontname, encoding, embedded, cached, null, null, true);
            }
            catch (DocumentException documentException) {
                // empty catch block
            }
            if (basefont == null) {
                fontname = this.trueTypeFonts.get(lowerCaseFontname);
                if (fontname == null) {
                    return new Font(-1, size, style, color);
                }
                basefont = BaseFont.createFont(fontname, encoding, embedded, cached, null, null);
            }
        }
        catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
        catch (IOException | NullPointerException ioe) {
            return new Font(-1, size, style, color);
        }
        return new Font(basefont, size, style, color);
    }

    private int getFontStyle(String fontname) {
        String lcf = fontname.toLowerCase(Locale.ROOT);
        int fontStyle = 0;
        if (lcf.contains("bold")) {
            fontStyle |= 1;
        }
        if (lcf.contains("italic") || lcf.contains("oblique")) {
            fontStyle |= 2;
        }
        return fontStyle;
    }

    public Font getFont(Properties attributes) {
        String fontname = null;
        String encoding = this.defaultEncoding;
        boolean embedded = this.defaultEmbedding;
        float size = -1.0f;
        int style = 0;
        Color color = null;
        String value = attributes.getProperty("style");
        if (value != null && value.length() > 0) {
            Properties styleAttributes = Markup.parseAttributes(value);
            if (styleAttributes.isEmpty()) {
                attributes.put("style", value);
            } else {
                fontname = styleAttributes.getProperty("font-family");
                if (fontname != null) {
                    while (fontname.indexOf(44) != -1) {
                        String tmp = fontname.substring(0, fontname.indexOf(44));
                        if (this.isRegistered(tmp)) {
                            fontname = tmp;
                            continue;
                        }
                        fontname = fontname.substring(fontname.indexOf(44) + 1);
                    }
                }
                if ((value = styleAttributes.getProperty("font-size")) != null) {
                    size = Markup.parseLength(value);
                }
                if ((value = styleAttributes.getProperty("font-weight")) != null) {
                    style |= Font.getStyleValue(value);
                }
                if ((value = styleAttributes.getProperty("font-style")) != null) {
                    style |= Font.getStyleValue(value);
                }
                if ((value = styleAttributes.getProperty("color")) != null) {
                    color = Markup.decodeColor(value);
                }
                attributes.putAll((Map<?, ?>)styleAttributes);
                Enumeration<Object> e = styleAttributes.keys();
                while (e.hasMoreElements()) {
                    Object o = e.nextElement();
                    attributes.put(o, styleAttributes.get(o));
                }
            }
        }
        if ((value = attributes.getProperty("encoding")) != null) {
            encoding = value;
        }
        if ("true".equals(attributes.getProperty("embedded"))) {
            embedded = true;
        }
        if ((value = attributes.getProperty("font")) != null) {
            fontname = value;
        }
        if ((value = attributes.getProperty("size")) != null) {
            size = Markup.parseLength(value);
        }
        if ((value = attributes.getProperty("style")) != null) {
            style |= Font.getStyleValue(value);
        }
        if ((value = attributes.getProperty("fontstyle")) != null) {
            style |= Font.getStyleValue(value);
        }
        String r = attributes.getProperty("red");
        String g = attributes.getProperty("green");
        String b = attributes.getProperty("blue");
        if (r != null || g != null || b != null) {
            int red = 0;
            int green = 0;
            int blue = 0;
            if (r != null) {
                red = Integer.parseInt(r);
            }
            if (g != null) {
                green = Integer.parseInt(g);
            }
            if (b != null) {
                blue = Integer.parseInt(b);
            }
            color = new Color(red, green, blue);
        } else {
            value = attributes.getProperty("color");
            if (value != null) {
                color = Markup.decodeColor(value);
            }
        }
        if (fontname == null) {
            return this.getFont(null, encoding, embedded, size, style, color);
        }
        return this.getFont(fontname, encoding, embedded, size, style, color);
    }

    public Font getFont(String fontname, String encoding, boolean embedded, float size, int style) {
        return this.getFont(fontname, encoding, embedded, size, style, null);
    }

    public Font getFont(String fontname, String encoding, boolean embedded, float size) {
        return this.getFont(fontname, encoding, embedded, size, -1, null);
    }

    public Font getFont(String fontname, String encoding, boolean embedded) {
        return this.getFont(fontname, encoding, embedded, -1.0f, -1, null);
    }

    public Font getFont(String fontname, String encoding, float size, int style, Color color) {
        return this.getFont(fontname, encoding, this.defaultEmbedding, size, style, color);
    }

    public Font getFont(String fontname, String encoding, float size, int style) {
        return this.getFont(fontname, encoding, this.defaultEmbedding, size, style, null);
    }

    public Font getFont(String fontname, String encoding, float size) {
        return this.getFont(fontname, encoding, this.defaultEmbedding, size, -1, null);
    }

    public Font getFont(String fontname, float size, Color color) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, -1, color);
    }

    public Font getFont(String fontname, String encoding) {
        return this.getFont(fontname, encoding, this.defaultEmbedding, -1.0f, -1, null);
    }

    public Font getFont(String fontname, float size, int style, Color color) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, style, color);
    }

    public Font getFont(String fontname, float size, int style) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, style, null);
    }

    public Font getFont(String fontname, float size) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, size, -1, null);
    }

    public Font getFont(String fontname) {
        return this.getFont(fontname, this.defaultEncoding, this.defaultEmbedding, -1.0f, -1, null);
    }

    public void registerFamily(String familyName, String fullName, String path) {
        List<String> tmp;
        if (path != null) {
            this.trueTypeFonts.put(fullName, path);
        }
        if ((tmp = this.fontFamilies.get(familyName)) == null) {
            tmp = new ArrayList<String>();
            tmp.add(fullName);
            this.fontFamilies.put(familyName, tmp);
        } else {
            int fullNameLength = fullName.length();
            boolean inserted = false;
            for (int j = 0; j < tmp.size(); ++j) {
                if (tmp.get(j).length() < fullNameLength) continue;
                tmp.add(j, fullName);
                inserted = true;
                break;
            }
            if (!inserted) {
                tmp.add(fullName);
            }
        }
    }

    public void register(String path) {
        this.register(path, null);
    }

    public void register(String path, String alias) {
        try {
            if (path.toLowerCase().endsWith(".ttf") || path.toLowerCase().endsWith(".otf") || path.toLowerCase().indexOf(".ttc,") > 0) {
                String[][] names;
                Object[] allNames = BaseFont.getAllFontNames(path, "Cp1252", null);
                this.trueTypeFonts.put(((String)allNames[0]).toLowerCase(), path);
                if (alias != null) {
                    this.trueTypeFonts.put(alias.toLowerCase(), path);
                }
                for (String[] name1 : names = (String[][])allNames[2]) {
                    this.trueTypeFonts.put(name1[3].toLowerCase(), path);
                }
                String familyName = null;
                names = (String[][])allNames[1];
                block3: for (int k = 0; k < TTFamilyOrder.length; k += 3) {
                    String[][] stringArray = names;
                    int n = stringArray.length;
                    for (int i = 0; i < n; ++i) {
                        String[] name = stringArray[i];
                        if (name.length != 4 || TTFamilyOrder.length <= k + 2 || !TTFamilyOrder[k].equals(name[0]) || !TTFamilyOrder[k + 1].equals(name[1]) || !TTFamilyOrder[k + 2].equals(name[2])) continue;
                        familyName = name[3].toLowerCase();
                        k = TTFamilyOrder.length;
                        continue block3;
                    }
                }
                if (familyName != null) {
                    String lastName = "";
                    block5: for (String[] name : names = (String[][])allNames[2]) {
                        for (int k = 0; k < TTFamilyOrder.length; k += 3) {
                            String fullName;
                            if (name.length != 4 || TTFamilyOrder.length <= k + 2 || !TTFamilyOrder[k].equals(name[0]) || !TTFamilyOrder[k + 1].equals(name[1]) || !TTFamilyOrder[k + 2].equals(name[2]) || (fullName = name[3]).equals(lastName)) continue;
                            lastName = fullName;
                            this.registerFamily(familyName, fullName, path);
                            continue block5;
                        }
                    }
                }
            } else if (path.toLowerCase().endsWith(".ttc")) {
                if (alias != null) {
                    System.err.println("class FontFactory: You can't define an alias for a true type collection.");
                }
                String[] names = BaseFont.enumerateTTCNames(path);
                for (int i = 0; i < names.length; ++i) {
                    this.register(path + "," + i);
                }
            } else if (path.toLowerCase().endsWith(".afm") || path.toLowerCase().endsWith(".pfm")) {
                BaseFont bf = BaseFont.createFont(path, "Cp1252", false);
                String fullName = bf.getFullFontName()[0][3].toLowerCase();
                String familyName = bf.getFamilyFontName()[0][3].toLowerCase();
                String psName = bf.getPostscriptFontName().toLowerCase();
                this.registerFamily(familyName, fullName, null);
                this.trueTypeFonts.put(psName, path);
                this.trueTypeFonts.put(fullName, path);
            }
        }
        catch (DocumentException | IOException de) {
            throw new ExceptionConverter(de);
        }
    }

    public int registerDirectory(String dir) {
        return this.registerDirectory(dir, false);
    }

    public int registerDirectory(String dir, boolean scanSubdirectories) {
        int count = 0;
        try {
            File file = new File(dir);
            if (!file.exists() || !file.isDirectory()) {
                return 0;
            }
            String[] files = file.list();
            if (files == null) {
                return 0;
            }
            for (String file1 : files) {
                try {
                    String suffix;
                    file = new File(dir, file1);
                    if (file.isDirectory()) {
                        if (!scanSubdirectories) continue;
                        count += this.registerDirectory(file.getAbsolutePath(), true);
                        continue;
                    }
                    String name = file.getPath();
                    String string = suffix = name.length() < 4 ? null : name.substring(name.length() - 4).toLowerCase();
                    if (".afm".equals(suffix) || ".pfm".equals(suffix)) {
                        File pfb = new File(name.substring(0, name.length() - 4) + ".pfb");
                        if (!pfb.exists()) continue;
                        this.register(name, null);
                        ++count;
                        continue;
                    }
                    if (".ttf".equals(suffix) || ".otf".equals(suffix)) {
                        this.register(name, file1);
                        ++count;
                        continue;
                    }
                    if (!".ttc".equals(suffix)) continue;
                    this.register(name, null);
                    ++count;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return count;
    }

    public int registerDirectories() {
        int count = 0;
        count += this.registerDirectory("c:/windows/fonts");
        count += this.registerDirectory("c:/winnt/fonts");
        count += this.registerDirectory("d:/windows/fonts");
        count += this.registerDirectory("d:/winnt/fonts");
        count += this.registerDirectory("/usr/share/X11/fonts", true);
        count += this.registerDirectory("/usr/X/lib/X11/fonts", true);
        count += this.registerDirectory("/usr/openwin/lib/X11/fonts", true);
        count += this.registerDirectory("/usr/share/fonts", true);
        count += this.registerDirectory("/usr/X11R6/lib/X11/fonts", true);
        count += this.registerDirectory("/Library/Fonts");
        return count += this.registerDirectory("/System/Library/Fonts");
    }

    public Set<String> getRegisteredFonts() {
        return Utilities.getKeySet(this.trueTypeFonts);
    }

    public Set<String> getRegisteredFamilies() {
        return Utilities.getKeySet(this.fontFamilies);
    }

    @Override
    public boolean isRegistered(String fontName) {
        return this.trueTypeFonts.containsKey(fontName.toLowerCase());
    }

    public Object getFontPath(String fontname) {
        return this.trueTypeFonts.get(fontname.toLowerCase());
    }
}

