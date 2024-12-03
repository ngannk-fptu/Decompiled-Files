/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.pdf.ITextFontResolver;

public class TrueTypeUtil {
    private static IdentValue guessStyle(BaseFont font) {
        String[][] names;
        for (String[] name : names = font.getFullFontName()) {
            String lower = name[3].toLowerCase();
            if (lower.contains("italic")) {
                return IdentValue.ITALIC;
            }
            if (!lower.contains("oblique")) continue;
            return IdentValue.OBLIQUE;
        }
        return IdentValue.NORMAL;
    }

    public static String[] getFamilyNames(BaseFont font) {
        String[][] names = font.getFamilyFontName();
        if (names.length == 1) {
            return new String[]{names[0][3]};
        }
        ArrayList<String> result = new ArrayList<String>();
        for (String[] name : names) {
            if ((!name[0].equals("1") || !name[1].equals("0")) && !name[2].equals("1033")) continue;
            result.add(name[3]);
        }
        return result.toArray(new String[result.size()]);
    }

    private static Map<String, int[]> extractTables(BaseFont font) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        for (Class<?> current = font.getClass(); current != null; current = current.getSuperclass()) {
            if (!current.getName().endsWith(".TrueTypeFont")) continue;
            Field field = current.getDeclaredField("tables");
            field.setAccessible(true);
            return (Map)field.get(font);
        }
        throw new NoSuchFieldException("Could not find tables field");
    }

    private static String getTTCName(String name) {
        int index = name.toLowerCase().indexOf(".ttc,");
        return index < 0 ? name : name.substring(0, index + 4);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void populateDescription(String path, BaseFont font, ITextFontResolver.FontDescription descr) throws IOException, NoSuchFieldException, IllegalAccessException, DocumentException {
        RandomAccessFileOrArray rf = null;
        try {
            rf = new RandomAccessFileOrArray(TrueTypeUtil.getTTCName(path));
            rf = TrueTypeUtil.populateDescription0(path, font, descr, rf);
        }
        finally {
            if (rf != null) {
                try {
                    rf.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void populateDescription(String path, byte[] contents, BaseFont font, ITextFontResolver.FontDescription descr) throws IOException, NoSuchFieldException, IllegalAccessException, DocumentException {
        RandomAccessFileOrArray rf = null;
        try {
            rf = new RandomAccessFileOrArray(contents);
            rf = TrueTypeUtil.populateDescription0(path, font, descr, rf);
        }
        finally {
            if (rf != null) {
                try {
                    rf.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private static RandomAccessFileOrArray populateDescription0(String path, BaseFont font, ITextFontResolver.FontDescription descr, RandomAccessFileOrArray rf) throws NoSuchFieldException, IllegalAccessException, DocumentException, IOException {
        Map<String, int[]> tables = TrueTypeUtil.extractTables(font);
        descr.setStyle(TrueTypeUtil.guessStyle(font));
        int[] location = tables.get("OS/2");
        if (location == null) {
            throw new DocumentException("Table 'OS/2' does not exist in " + path);
        }
        rf.seek(location[0]);
        int want = 4;
        long got = rf.skip(want);
        if (got < (long)want) {
            throw new DocumentException("Skip TT font weight, expect read " + want + " bytes, but only got " + got);
        }
        descr.setWeight(rf.readUnsignedShort());
        want = 20;
        got = rf.skip(want);
        if (got < (long)want) {
            throw new DocumentException("Skip TT font strikeout, expect read " + want + " bytes, but only got " + got);
        }
        descr.setYStrikeoutSize(rf.readShort());
        descr.setYStrikeoutPosition(rf.readShort());
        location = tables.get("post");
        if (location != null) {
            rf.seek(location[0]);
            want = 8;
            got = rf.skip(want);
            if (got < (long)want) {
                throw new DocumentException("Skip TT font underline, expect read " + want + " bytes, but only got " + got);
            }
            descr.setUnderlinePosition(rf.readShort());
            descr.setUnderlineThickness(rf.readShort());
        }
        rf.close();
        rf = null;
        return rf;
    }
}

