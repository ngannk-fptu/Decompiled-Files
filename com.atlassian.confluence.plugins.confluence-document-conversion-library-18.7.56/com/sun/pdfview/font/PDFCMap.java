/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.cid.ToUnicodeMap;
import java.io.IOException;
import java.util.HashMap;

public abstract class PDFCMap {
    private static HashMap<String, PDFCMap> cache;

    protected PDFCMap() {
    }

    public static PDFCMap getCMap(PDFObject map) throws IOException {
        if (map.getType() == 4) {
            return PDFCMap.getCMap(map.getStringValue());
        }
        if (map.getType() == 7) {
            return PDFCMap.parseCMap(map);
        }
        throw new IOException("CMap type not Name or Stream!");
    }

    public static PDFCMap getCMap(String mapName) throws IOException {
        if (cache == null) {
            PDFCMap.populateCache();
        }
        if (!cache.containsKey(mapName)) {
            throw new IOException("Unknown CMap: " + mapName);
        }
        return cache.get(mapName);
    }

    protected static void populateCache() {
        cache = new HashMap();
        cache.put("Identity-H", new PDFCMap(){

            @Override
            public char map(char src) {
                return src;
            }
        });
    }

    protected static PDFCMap parseCMap(PDFObject map) throws IOException {
        return new ToUnicodeMap(map);
    }

    public abstract char map(char var1);

    public int getFontID(char src) {
        return 0;
    }
}

