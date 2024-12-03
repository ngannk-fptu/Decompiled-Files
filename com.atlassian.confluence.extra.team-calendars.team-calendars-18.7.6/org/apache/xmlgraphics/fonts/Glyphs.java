/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 */
package org.apache.xmlgraphics.fonts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.commons.io.IOUtils;

public final class Glyphs {
    public static final String NOTDEF = ".notdef";
    @Deprecated
    public static final String[] MAC_GLYPH_NAMES = new String[]{".notdef", ".null", "CR", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quotesingle", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "grave", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "braceleft", "bar", "braceright", "asciitilde", "Adieresis", "Aring", "Ccedilla", "Eacute", "Ntilde", "Odieresis", "Udieresis", "aacute", "agrave", "acircumflex", "adieresis", "atilde", "aring", "ccedilla", "eacute", "egrave", "ecircumflex", "edieresis", "iacute", "igrave", "icircumflex", "idieresis", "ntilde", "oacute", "ograve", "ocircumflex", "odieresis", "otilde", "uacute", "ugrave", "ucircumflex", "udieresis", "dagger", "degree", "cent", "sterling", "section", "bullet", "paragraph", "germandbls", "registered", "copyright", "trademark", "acute", "dieresis", "notequal", "AE", "Oslash", "infinity", "plusminus", "lessequal", "greaterequal", "yen", "mu", "partialdiff", "Sigma", "Pi", "pi", "integral", "ordfeminine", "ordmasculine", "Omega", "ae", "oslash", "questiondown", "exclamdown", "logicalnot", "radical", "florin", "approxequal", "Delta", "guillemotleft", "guillemotright", "ellipsis", "nbspace", "Agrave", "Atilde", "Otilde", "OE", "oe", "endash", "emdash", "quotedblleft", "quotedblright", "quoteleft", "quoteright", "divide", "lozenge", "ydieresis", "Ydieresis", "fraction", "currency", "guilsinglleft", "guilsinglright", "fi", "fl", "daggerdbl", "periodcentered", "quotesinglbase", "quotedblbase", "perthousand", "Acircumflex", "Ecircumflex", "Aacute", "Edieresis", "Egrave", "Iacute", "Icircumflex", "Idieresis", "Igrave", "Oacute", "Ocircumflex", "applelogo", "Ograve", "Uacute", "Ucircumflex", "Ugrave", "dotlessi", "circumflex", "tilde", "macron", "breve", "dotaccent", "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "Lslash", "lslash", "Scaron", "scaron", "Zcaron", "zcaron", "brokenbar", "Eth", "eth", "Yacute", "yacute", "Thorn", "thorn", "minus", "multiply", "onesuperior", "twosuperior", "threesuperior", "onehalf", "onequarter", "threequarters", "franc", "Gbreve", "gbreve", "Idot", "Scedilla", "scedilla", "Cacute", "cacute", "Ccaron", "ccaron", "dmacron"};
    public static final String[] TEX8R_GLYPH_NAMES = new String[]{".notdef", "dotaccent", "fi", "fl", "fraction", "hungarumlaut", "Lslash", "lslash", "ogonek", "ring", ".notdef", "breve", "minus", ".notdef", "Zcaron", "zcaron", "caron", "dotlessi", "dotlessj", "ff", "ffi", "ffl", ".notdef", ".notdef", ".notdef", ".notdef", ".notdef", ".notdef", ".notdef", ".notdef", "grave", "quotesingle", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quoteright", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "quoteleft", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "braceleft", "bar", "braceright", "asciitilde", ".notdef", "Euro", ".notdef", "quotesinglbase", "florin", "quotedblbase", "ellipsis", "dagger", "daggerdbl", "circumflex", "perthousand", "Scaron", "guilsinglleft", "OE", ".notdef", ".notdef", ".notdef", ".notdef", ".notdef", ".notdef", "quotedblleft", "quotedblright", "bullet", "endash", "emdash", "tilde", "trademark", "scaron", "guilsinglright", "oe", ".notdef", ".notdef", "Ydieresis", ".notdef", "exclamdown", "cent", "sterling", "currency", "yen", "brokenbar", "section", "dieresis", "copyright", "ordfeminine", "guillemotleft", "logicalnot", "hyphen", "registered", "macron", "degree", "plusminus", "twosuperior", "threesuperior", "acute", "mu", "paragraph", "periodcentered", "cedilla", "onesuperior", "ordmasculine", "guillemotright", "onequarter", "onehalf", "threequarters", "questiondown", "Agrave", "Aacute", "Acircumflex", "Atilde", "Adieresis", "Aring", "AE", "Ccedilla", "Egrave", "Eacute", "Ecircumflex", "Edieresis", "Igrave", "Iacute", "Icircumflex", "Idieresis", "Eth", "Ntilde", "Ograve", "Oacute", "Ocircumflex", "Otilde", "Odieresis", "multiply", "Oslash", "Ugrave", "Uacute", "Ucircumflex", "Udieresis", "Yacute", "Thorn", "germandbls", "agrave", "aacute", "acircumflex", "atilde", "adieresis", "aring", "ae", "ccedilla", "egrave", "eacute", "ecircumflex", "edieresis", "igrave", "iacute", "icircumflex", "idieresis", "eth", "ntilde", "ograve", "oacute", "ocircumflex", "otilde", "odieresis", "divide", "oslash", "ugrave", "uacute", "ucircumflex", "udieresis", "yacute", "thorn", "ydieresis"};
    public static final char[] WINANSI_ENCODING = new char[]{'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '\u2018', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u2022', '\u20ac', '\u2022', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\u2022', '\u017d', '\u2022', '\u2022', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014', '~', '\u2122', '\u0161', '\u203a', '\u0153', '\u2022', '\u017e', '\u0178', ' ', '\u00a1', '\u00a2', '\u00a3', '\u00a4', '\u00a5', '\u00a6', '\u00a7', '\u00a8', '\u00a9', '\u00aa', '\u00ab', '\u00ac', '\u00ad', '\u00ae', '\u00af', '\u00b0', '\u00b1', '\u00b2', '\u00b3', '\u00b4', '\u00b5', '\u00b6', '\u00b7', '\u00b8', '\u00b9', '\u00ba', '\u00bb', '\u00bc', '\u00bd', '\u00be', '\u00bf', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u00d0', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00de', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f0', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00fe', '\u00ff'};
    public static final char[] ADOBECYRILLIC_ENCODING = new char[]{'\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u0000', '\u0402', '\u0403', '\u201a', '\u0453', '\u201e', '\u2026', '\u2020', '\u2021', '\u20ac', '\u2030', '\u0409', '\u2039', '\u040a', '\u040c', '\u040b', '\u040f', '\u0452', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014', '\u0000', '\u2122', '\u0459', '\u203a', '\u045a', '\u045c', '\u045b', '\u045f', '\u00a0', '\u040e', '\u045e', '\u0408', '\u00a4', '\u0490', '\u00a6', '\u00a7', '\u0401', '\u00a9', '\u0404', '\u00ab', '\u00ac', '\u00ad', '\u00ae', '\u0407', '\u00b0', '\u00b1', '\u0406', '\u0456', '\u0491', '\u00b5', '\u00b6', '\u00b7', '\u0451', '\u2116', '\u0454', '\u00bb', '\u0458', '\u0405', '\u0455', '\u0457', '\u0410', '\u0411', '\u0412', '\u0413', '\u0414', '\u0415', '\u0416', '\u0417', '\u0418', '\u0419', '\u041a', '\u041b', '\u041c', '\u041d', '\u041e', '\u041f', '\u0420', '\u0421', '\u0422', '\u0423', '\u0424', '\u0425', '\u0426', '\u0427', '\u0428', '\u0429', '\u042a', '\u042b', '\u042c', '\u042d', '\u042e', '\u042f', '\u0430', '\u0431', '\u0432', '\u0433', '\u0434', '\u0435', '\u0436', '\u0437', '\u0438', '\u0439', '\u043a', '\u043b', '\u043c', '\u043d', '\u043e', '\u043f', '\u0440', '\u0441', '\u0442', '\u0443', '\u0444', '\u0445', '\u0446', '\u0447', '\u0448', '\u0449', '\u044a', '\u044b', '\u044c', '\u044d', '\u044e', '\u044f'};
    private static final String[] UNICODE_GLYPHS;
    private static final String[] DINGBATS_GLYPHS;
    private static final Map CHARNAME_ALTERNATIVES;
    private static final Map CHARNAMES_TO_UNICODE;

    private Glyphs() {
    }

    private static void addAlternatives(Map map, String[] alternatives) {
        int c = alternatives.length;
        for (int i = 0; i < c; ++i) {
            String[] alt = new String[c - 1];
            int idx = 0;
            for (int j = 0; j < c; ++j) {
                if (i == j) continue;
                alt[idx] = alternatives[j];
                ++idx;
            }
            map.put(alternatives[i], alt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String[] loadGlyphList(String filename, Map charNameToUnicodeMap) {
        ArrayList<String> lines = new ArrayList<String>();
        InputStream in = Glyphs.class.getResourceAsStream(filename);
        if (in == null) {
            throw new RuntimeException("Cannot load " + filename + ". The Glyphs class cannot properly be initialized!");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, "US-ASCII"));){
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                lines.add(line);
            }
        }
        catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Incompatible JVM! US-ASCII encoding is not supported. The Glyphs class cannot properly be initialized!");
        }
        catch (IOException ioe) {
            throw new RuntimeException("I/O error while loading " + filename + ". The Glyphs class cannot properly be initialized!");
        }
        finally {
            IOUtils.closeQuietly((InputStream)in);
        }
        String[] arr = new String[lines.size() * 2];
        int pos = 0;
        StringBuffer buf = new StringBuffer();
        for (Object e : lines) {
            String unicode;
            String line = (String)e;
            int semicolon = line.indexOf(59);
            if (semicolon <= 0) continue;
            String charName = line.substring(0, semicolon);
            String rawUnicode = line.substring(semicolon + 1);
            buf.setLength(0);
            StringTokenizer tokenizer = new StringTokenizer(rawUnicode, " ", false);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                assert (token.length() == 4);
                buf.append(Glyphs.hexToChar(token));
            }
            arr[pos] = unicode = buf.toString();
            arr[++pos] = charName;
            ++pos;
            assert (!charNameToUnicodeMap.containsKey(charName));
            charNameToUnicodeMap.put(charName, unicode);
        }
        return arr;
    }

    private static char hexToChar(String hex) {
        return (char)Integer.parseInt(hex, 16);
    }

    public static String charToGlyphName(char ch) {
        return Glyphs.stringToGlyph(Character.toString(ch));
    }

    public static String getUnicodeSequenceForGlyphName(String glyphName) {
        int period = glyphName.indexOf(46);
        if (period >= 0) {
            glyphName = glyphName.substring(0, period);
        }
        StringBuffer sb = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(glyphName, "_", false);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            String sequence = (String)CHARNAMES_TO_UNICODE.get(token);
            if (sequence == null) {
                if (token.startsWith("uni")) {
                    int len = token.length();
                    int pos = 3;
                    while (pos + 4 <= len) {
                        try {
                            sb.append(Glyphs.hexToChar(token.substring(pos, pos + 4)));
                        }
                        catch (NumberFormatException nfe) {
                            return null;
                        }
                        pos += 4;
                    }
                    continue;
                }
                if (!token.startsWith("u")) continue;
                if (token.length() > 5) {
                    return null;
                }
                if (token.length() < 5) {
                    return null;
                }
                try {
                    sb.append(Glyphs.hexToChar(token.substring(1, 5)));
                    continue;
                }
                catch (NumberFormatException nfe) {
                    return null;
                }
            }
            sb.append(sequence);
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    @Deprecated
    public static String glyphToString(String name) {
        for (int i = 0; i < UNICODE_GLYPHS.length; i += 2) {
            if (!UNICODE_GLYPHS[i + 1].equals(name)) continue;
            return UNICODE_GLYPHS[i];
        }
        return "";
    }

    public static String stringToGlyph(String name) {
        int i;
        for (i = 0; i < UNICODE_GLYPHS.length; i += 2) {
            if (!UNICODE_GLYPHS[i].equals(name)) continue;
            return UNICODE_GLYPHS[i + 1];
        }
        for (i = 0; i < DINGBATS_GLYPHS.length; i += 2) {
            if (!DINGBATS_GLYPHS[i].equals(name)) continue;
            return DINGBATS_GLYPHS[i + 1];
        }
        return "";
    }

    public static String[] getCharNameAlternativesFor(String charName) {
        return (String[])CHARNAME_ALTERNATIVES.get(charName);
    }

    static {
        TreeMap map = new TreeMap();
        UNICODE_GLYPHS = Glyphs.loadGlyphList("glyphlist.txt", map);
        DINGBATS_GLYPHS = Glyphs.loadGlyphList("zapfdingbats.txt", map);
        CHARNAMES_TO_UNICODE = Collections.unmodifiableMap(map);
        map = new TreeMap();
        Glyphs.addAlternatives(map, new String[]{"Omega", "Omegagreek"});
        Glyphs.addAlternatives(map, new String[]{"Delta", "Deltagreek"});
        Glyphs.addAlternatives(map, new String[]{"fraction", "divisionslash"});
        Glyphs.addAlternatives(map, new String[]{"hyphen", "sfthyphen", "softhyphen", "minus"});
        Glyphs.addAlternatives(map, new String[]{"macron", "overscore"});
        Glyphs.addAlternatives(map, new String[]{"mu", "mu1", "mugreek"});
        Glyphs.addAlternatives(map, new String[]{"periodcentered", "middot", "bulletoperator", "anoteleia"});
        Glyphs.addAlternatives(map, new String[]{"space", "nonbreakingspace", "nbspace"});
        Glyphs.addAlternatives(map, new String[]{"zero", "zerooldstyle"});
        Glyphs.addAlternatives(map, new String[]{"one", "oneoldstyle"});
        Glyphs.addAlternatives(map, new String[]{"two", "twooldstyle"});
        Glyphs.addAlternatives(map, new String[]{"three", "threeoldstyle"});
        Glyphs.addAlternatives(map, new String[]{"four", "fouroldstyle"});
        Glyphs.addAlternatives(map, new String[]{"five", "fiveoldstyle"});
        Glyphs.addAlternatives(map, new String[]{"six", "sixoldstyle"});
        Glyphs.addAlternatives(map, new String[]{"seven", "sevenoldstyle"});
        Glyphs.addAlternatives(map, new String[]{"eight", "eightoldstyle"});
        Glyphs.addAlternatives(map, new String[]{"nine", "nineoldstyle"});
        Glyphs.addAlternatives(map, new String[]{"cent", "centoldstyle"});
        Glyphs.addAlternatives(map, new String[]{"dollar", "dollaroldstyle"});
        Glyphs.addAlternatives(map, new String[]{"Acyrillic", "afii10017"});
        Glyphs.addAlternatives(map, new String[]{"Becyrillic", "afii10018"});
        Glyphs.addAlternatives(map, new String[]{"Vecyrillic", "afii10019"});
        Glyphs.addAlternatives(map, new String[]{"Gecyrillic", "afii10020"});
        Glyphs.addAlternatives(map, new String[]{"Decyrillic", "afii10021"});
        Glyphs.addAlternatives(map, new String[]{"Iecyrillic", "afii10022"});
        Glyphs.addAlternatives(map, new String[]{"Iocyrillic", "afii10023"});
        Glyphs.addAlternatives(map, new String[]{"Zhecyrillic", "afii10024"});
        Glyphs.addAlternatives(map, new String[]{"Zecyrillic", "afii10025"});
        Glyphs.addAlternatives(map, new String[]{"Iicyrillic", "afii10026"});
        Glyphs.addAlternatives(map, new String[]{"Iishortcyrillic", "afii10027"});
        Glyphs.addAlternatives(map, new String[]{"Kacyrillic", "afii10028"});
        Glyphs.addAlternatives(map, new String[]{"Elcyrillic", "afii10029"});
        Glyphs.addAlternatives(map, new String[]{"Emcyrillic", "afii10030"});
        Glyphs.addAlternatives(map, new String[]{"Encyrillic", "afii10031"});
        Glyphs.addAlternatives(map, new String[]{"Ocyrillic", "afii10032"});
        Glyphs.addAlternatives(map, new String[]{"Pecyrillic", "afii10033"});
        Glyphs.addAlternatives(map, new String[]{"Ercyrillic", "afii10034"});
        Glyphs.addAlternatives(map, new String[]{"Escyrillic", "afii10035"});
        Glyphs.addAlternatives(map, new String[]{"Tecyrillic", "afii10036"});
        Glyphs.addAlternatives(map, new String[]{"Ucyrillic", "afii10037"});
        Glyphs.addAlternatives(map, new String[]{"Efcyrillic", "afii10038"});
        Glyphs.addAlternatives(map, new String[]{"Khacyrillic", "afii10039"});
        Glyphs.addAlternatives(map, new String[]{"Tsecyrillic", "afii10040"});
        Glyphs.addAlternatives(map, new String[]{"Checyrillic", "afii10041"});
        Glyphs.addAlternatives(map, new String[]{"Shacyrillic", "afii10042"});
        Glyphs.addAlternatives(map, new String[]{"Shchacyrillic", "afii10043"});
        Glyphs.addAlternatives(map, new String[]{"Hardsigncyrillic", "afii10044"});
        Glyphs.addAlternatives(map, new String[]{"Yericyrillic", "afii10045"});
        Glyphs.addAlternatives(map, new String[]{"Softsigncyrillic", "afii10046"});
        Glyphs.addAlternatives(map, new String[]{"Ereversedcyrillic", "afii10047"});
        Glyphs.addAlternatives(map, new String[]{"IUcyrillic", "afii10048"});
        Glyphs.addAlternatives(map, new String[]{"IAcyrillic", "afii10049"});
        Glyphs.addAlternatives(map, new String[]{"acyrillic", "afii10065"});
        Glyphs.addAlternatives(map, new String[]{"becyrillic", "afii10066"});
        Glyphs.addAlternatives(map, new String[]{"vecyrillic", "afii10067"});
        Glyphs.addAlternatives(map, new String[]{"gecyrillic", "afii10068"});
        Glyphs.addAlternatives(map, new String[]{"decyrillic", "afii10069"});
        Glyphs.addAlternatives(map, new String[]{"iecyrillic", "afii10070"});
        Glyphs.addAlternatives(map, new String[]{"iocyrillic", "afii10071"});
        Glyphs.addAlternatives(map, new String[]{"zhecyrillic", "afii10072"});
        Glyphs.addAlternatives(map, new String[]{"zecyrillic", "afii10073"});
        Glyphs.addAlternatives(map, new String[]{"iicyrillic", "afii10074"});
        Glyphs.addAlternatives(map, new String[]{"iishortcyrillic", "afii10075"});
        Glyphs.addAlternatives(map, new String[]{"kacyrillic", "afii10076"});
        Glyphs.addAlternatives(map, new String[]{"elcyrillic", "afii10077"});
        Glyphs.addAlternatives(map, new String[]{"emcyrillic", "afii10078"});
        Glyphs.addAlternatives(map, new String[]{"encyrillic", "afii10079"});
        Glyphs.addAlternatives(map, new String[]{"ocyrillic", "afii10080"});
        Glyphs.addAlternatives(map, new String[]{"pecyrillic", "afii10081"});
        Glyphs.addAlternatives(map, new String[]{"ercyrillic", "afii10082"});
        Glyphs.addAlternatives(map, new String[]{"escyrillic", "afii10083"});
        Glyphs.addAlternatives(map, new String[]{"tecyrillic", "afii10084"});
        Glyphs.addAlternatives(map, new String[]{"ucyrillic", "afii10085"});
        Glyphs.addAlternatives(map, new String[]{"efcyrillic", "afii10086"});
        Glyphs.addAlternatives(map, new String[]{"khacyrillic", "afii10087"});
        Glyphs.addAlternatives(map, new String[]{"tsecyrillic", "afii10088"});
        Glyphs.addAlternatives(map, new String[]{"checyrillic", "afii10089"});
        Glyphs.addAlternatives(map, new String[]{"shacyrillic", "afii10090"});
        Glyphs.addAlternatives(map, new String[]{"shchacyrillic", "afii10091"});
        Glyphs.addAlternatives(map, new String[]{"hardsigncyrillic", "afii10092"});
        Glyphs.addAlternatives(map, new String[]{"yericyrillic", "afii10093"});
        Glyphs.addAlternatives(map, new String[]{"softsigncyrillic", "afii10094"});
        Glyphs.addAlternatives(map, new String[]{"ereversedcyrillic", "afii10095"});
        Glyphs.addAlternatives(map, new String[]{"iucyrillic", "afii10096"});
        Glyphs.addAlternatives(map, new String[]{"iacyrillic", "afii10097"});
        Glyphs.addAlternatives(map, new String[]{"Gheupturncyrillic", "afii10050"});
        Glyphs.addAlternatives(map, new String[]{"Djecyrillic", "afii10051"});
        Glyphs.addAlternatives(map, new String[]{"Gjecyrillic", "afii10052"});
        Glyphs.addAlternatives(map, new String[]{"Ecyrillic", "afii10053"});
        Glyphs.addAlternatives(map, new String[]{"Dzecyrillic", "afii10054"});
        Glyphs.addAlternatives(map, new String[]{"Icyrillic", "afii10055"});
        Glyphs.addAlternatives(map, new String[]{"Yicyrillic", "afii10056"});
        Glyphs.addAlternatives(map, new String[]{"Jecyrillic", "afii10057"});
        Glyphs.addAlternatives(map, new String[]{"Ljecyrillic", "afii10058"});
        Glyphs.addAlternatives(map, new String[]{"Njecyrillic", "afii10059"});
        Glyphs.addAlternatives(map, new String[]{"Tshecyrillic", "afii10060"});
        Glyphs.addAlternatives(map, new String[]{"Kjecyrillic", "afii10061"});
        Glyphs.addAlternatives(map, new String[]{"Ushortcyrillic", "afii10062"});
        Glyphs.addAlternatives(map, new String[]{"Dzhecyrillic", "afii10145"});
        Glyphs.addAlternatives(map, new String[]{"Yatcyrillic", "afii10146"});
        Glyphs.addAlternatives(map, new String[]{"Fitacyrillic", "afii10147"});
        Glyphs.addAlternatives(map, new String[]{"Izhitsacyrillic", "afii10148"});
        Glyphs.addAlternatives(map, new String[]{"gheupturncyrillic", "afii10098"});
        Glyphs.addAlternatives(map, new String[]{"djecyrillic", "afii10099"});
        Glyphs.addAlternatives(map, new String[]{"gjecyrillic", "afii10100"});
        Glyphs.addAlternatives(map, new String[]{"ecyrillic", "afii10101"});
        Glyphs.addAlternatives(map, new String[]{"dzecyrillic", "afii10102"});
        Glyphs.addAlternatives(map, new String[]{"icyrillic", "afii10103"});
        Glyphs.addAlternatives(map, new String[]{"yicyrillic", "afii10104"});
        Glyphs.addAlternatives(map, new String[]{"jecyrillic", "afii10105"});
        Glyphs.addAlternatives(map, new String[]{"ljecyrillic", "afii10106"});
        Glyphs.addAlternatives(map, new String[]{"njecyrillic", "afii10107"});
        Glyphs.addAlternatives(map, new String[]{"tshecyrillic", "afii10108"});
        Glyphs.addAlternatives(map, new String[]{"kjecyrillic", "afii10109"});
        Glyphs.addAlternatives(map, new String[]{"ushortcyrillic", "afii10110"});
        Glyphs.addAlternatives(map, new String[]{"dzhecyrillic", "afii10193"});
        Glyphs.addAlternatives(map, new String[]{"yatcyrillic", "afii10194"});
        Glyphs.addAlternatives(map, new String[]{"fitacyrillic", "afii10195"});
        Glyphs.addAlternatives(map, new String[]{"izhitsacyrillic", "afii10196"});
        CHARNAME_ALTERNATIVES = Collections.unmodifiableMap(map);
    }
}

