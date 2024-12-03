/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.font.encoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.font.encoding.MacExpertEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.MacRomanEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.SymbolEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.ZapfDingbatsEncoding;

public final class GlyphList {
    private static final Log LOG = LogFactory.getLog(GlyphList.class);
    private static final GlyphList DEFAULT = GlyphList.load("glyphlist.txt", 4281);
    private static final GlyphList ZAPF_DINGBATS = GlyphList.load("zapfdingbats.txt", 201);
    private final Map<String, String> nameToUnicode;
    private final Map<String, String> unicodeToName;
    private final Map<String, String> uniNameToUnicodeCache = new ConcurrentHashMap<String, String>();

    private static GlyphList load(String filename, int numberOfEntries) {
        String path = "/org/apache/pdfbox/resources/glyphlist/" + filename;
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = GlyphList.class.getResourceAsStream(path);
            if (resourceAsStream == null) {
                throw new IOException("GlyphList '" + path + "' not found");
            }
            GlyphList glyphList = new GlyphList(resourceAsStream, numberOfEntries);
            return glyphList;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly(resourceAsStream);
        }
    }

    public static GlyphList getAdobeGlyphList() {
        return DEFAULT;
    }

    public static GlyphList getZapfDingbats() {
        return ZAPF_DINGBATS;
    }

    public GlyphList(InputStream input, int numberOfEntries) throws IOException {
        this.nameToUnicode = new HashMap<String, String>(numberOfEntries);
        this.unicodeToName = new HashMap<String, String>(numberOfEntries);
        this.loadList(input);
    }

    public GlyphList(GlyphList glyphList, InputStream input) throws IOException {
        this.nameToUnicode = new HashMap<String, String>(glyphList.nameToUnicode);
        this.unicodeToName = new HashMap<String, String>(glyphList.unicodeToName);
        this.loadList(input);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadList(InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input, "ISO-8859-1"));
        try {
            while (in.ready()) {
                boolean forceOverride;
                String line = in.readLine();
                if (line == null || line.startsWith("#")) continue;
                String[] parts = line.split(";");
                if (parts.length < 2) {
                    throw new IOException("Invalid glyph list entry: " + line);
                }
                String name = parts[0];
                String[] unicodeList = parts[1].split(" ");
                if (this.nameToUnicode.containsKey(name)) {
                    LOG.warn((Object)("duplicate value for " + name + " -> " + parts[1] + " " + this.nameToUnicode.get(name)));
                }
                int[] codePoints = new int[unicodeList.length];
                int index = 0;
                for (String hex : unicodeList) {
                    codePoints[index++] = Integer.parseInt(hex, 16);
                }
                String string = new String(codePoints, 0, codePoints.length);
                this.nameToUnicode.put(name, string);
                boolean bl = forceOverride = WinAnsiEncoding.INSTANCE.contains(name) || MacRomanEncoding.INSTANCE.contains(name) || MacExpertEncoding.INSTANCE.contains(name) || SymbolEncoding.INSTANCE.contains(name) || ZapfDingbatsEncoding.INSTANCE.contains(name);
                if (this.unicodeToName.containsKey(string) && !forceOverride) continue;
                this.unicodeToName.put(string, name);
            }
        }
        finally {
            in.close();
        }
    }

    public String codePointToName(int codePoint) {
        String name = this.unicodeToName.get(new String(new int[]{codePoint}, 0, 1));
        if (name == null) {
            return ".notdef";
        }
        return name;
    }

    public String sequenceToName(String unicodeSequence) {
        String name = this.unicodeToName.get(unicodeSequence);
        if (name == null) {
            return ".notdef";
        }
        return name;
    }

    public String toUnicode(String name) {
        if (name == null) {
            return null;
        }
        String unicode = this.nameToUnicode.get(name);
        if (unicode != null) {
            return unicode;
        }
        unicode = this.uniNameToUnicodeCache.get(name);
        if (unicode == null) {
            if (name.indexOf(46) > 0) {
                unicode = this.toUnicode(name.substring(0, name.indexOf(46)));
            } else if (name.startsWith("uni") && name.length() == 7) {
                int nameLength = name.length();
                StringBuilder uniStr = new StringBuilder();
                try {
                    int chPos = 3;
                    while (chPos + 4 <= nameLength) {
                        int codePoint = Integer.parseInt(name.substring(chPos, chPos + 4), 16);
                        if (codePoint > 55295 && codePoint < 57344) {
                            LOG.warn((Object)("Unicode character name with disallowed code area: " + name));
                        } else {
                            uniStr.append((char)codePoint);
                        }
                        chPos += 4;
                    }
                    unicode = uniStr.toString();
                }
                catch (NumberFormatException nfe) {
                    LOG.warn((Object)("Not a number in Unicode character name: " + name));
                }
            } else if (name.startsWith("u") && name.length() == 5) {
                try {
                    int codePoint = Integer.parseInt(name.substring(1), 16);
                    if (codePoint > 55295 && codePoint < 57344) {
                        LOG.warn((Object)("Unicode character name with disallowed code area: " + name));
                    } else {
                        unicode = String.valueOf((char)codePoint);
                    }
                }
                catch (NumberFormatException nfe) {
                    LOG.warn((Object)("Not a number in Unicode character name: " + name));
                }
            }
            if (unicode != null) {
                this.uniNameToUnicodeCache.put(name, unicode);
            }
        }
        return unicode;
    }

    static {
        try {
            String location = System.getProperty("glyphlist_ext");
            if (location != null) {
                throw new UnsupportedOperationException("glyphlist_ext is no longer supported, use GlyphList.DEFAULT.addGlyphs(Properties) instead");
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
    }
}

