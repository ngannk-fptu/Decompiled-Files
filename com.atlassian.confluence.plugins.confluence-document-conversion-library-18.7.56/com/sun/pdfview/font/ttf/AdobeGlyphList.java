/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class AdobeGlyphList {
    private static HashMap<String, int[]> glyphToUnicodes;
    private static HashMap<Integer, String> unicodeToGlyph;
    static Thread glyphLoaderThread;

    private AdobeGlyphList() {
        glyphToUnicodes = new HashMap(4500);
        unicodeToGlyph = new HashMap(4500);
        glyphLoaderThread = new Thread(new Runnable(){

            @Override
            public void run() {
                ArrayList<String> unicodes = new ArrayList<String>();
                InputStream istr = this.getClass().getResourceAsStream("/com/sun/pdfview/font/ttf/resource/glyphlist.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(istr));
                String line = "";
                while (line != null) {
                    try {
                        unicodes.clear();
                        line = reader.readLine();
                        if (line == null) break;
                        if ((line = line.trim()).length() <= 0 || line.startsWith("#")) continue;
                        StringTokenizer tokens = new StringTokenizer(line, ";");
                        String glyphName = tokens.nextToken();
                        StringTokenizer codeTokens = new StringTokenizer(tokens.nextToken(), " ");
                        while (codeTokens.hasMoreTokens()) {
                            unicodes.add(codeTokens.nextToken());
                        }
                        int[] codes = new int[unicodes.size()];
                        for (int i = 0; i < unicodes.size(); ++i) {
                            codes[i] = Integer.parseInt((String)unicodes.get(i), 16);
                            unicodeToGlyph.put(new Integer(codes[i]), glyphName);
                        }
                        glyphToUnicodes.put(glyphName, codes);
                    }
                    catch (IOException ex) {
                        break;
                    }
                }
            }
        }, "Adobe Glyph Loader Thread");
        glyphLoaderThread.setDaemon(true);
        glyphLoaderThread.setPriority(1);
        glyphLoaderThread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int[] getUnicodeValues(String glyphName) {
        while (glyphLoaderThread != null && glyphLoaderThread.isAlive()) {
            HashMap<String, int[]> hashMap = glyphToUnicodes;
            synchronized (hashMap) {
                try {
                    glyphToUnicodes.wait(250L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
        return glyphToUnicodes.get(glyphName);
    }

    public static Integer getGlyphNameIndex(String glyphName) {
        int[] unicodes = AdobeGlyphList.getUnicodeValues(glyphName);
        if (unicodes == null) {
            return null;
        }
        return new Integer(unicodes[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getGlyphName(int unicode) {
        while (glyphLoaderThread != null && glyphLoaderThread.isAlive()) {
            HashMap<String, int[]> hashMap = glyphToUnicodes;
            synchronized (hashMap) {
                try {
                    glyphToUnicodes.wait(250L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
        return unicodeToGlyph.get(new Integer(unicode));
    }

    static {
        glyphLoaderThread = null;
        new AdobeGlyphList();
    }
}

