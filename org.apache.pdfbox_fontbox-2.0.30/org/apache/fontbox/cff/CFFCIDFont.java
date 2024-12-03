/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CIDKeyedType2CharString;
import org.apache.fontbox.cff.FDSelect;
import org.apache.fontbox.cff.Type1CharString;
import org.apache.fontbox.cff.Type2CharStringParser;
import org.apache.fontbox.type1.Type1CharStringReader;

public class CFFCIDFont
extends CFFFont {
    private String registry;
    private String ordering;
    private int supplement;
    private List<Map<String, Object>> fontDictionaries = Collections.emptyList();
    private List<Map<String, Object>> privateDictionaries = Collections.emptyList();
    private FDSelect fdSelect;
    private final Map<Integer, CIDKeyedType2CharString> charStringCache = new ConcurrentHashMap<Integer, CIDKeyedType2CharString>();
    private final PrivateType1CharStringReader reader = new PrivateType1CharStringReader();

    public String getRegistry() {
        return this.registry;
    }

    void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getOrdering() {
        return this.ordering;
    }

    void setOrdering(String ordering) {
        this.ordering = ordering;
    }

    public int getSupplement() {
        return this.supplement;
    }

    void setSupplement(int supplement) {
        this.supplement = supplement;
    }

    public List<Map<String, Object>> getFontDicts() {
        return this.fontDictionaries;
    }

    void setFontDict(List<Map<String, Object>> fontDict) {
        this.fontDictionaries = fontDict;
    }

    public List<Map<String, Object>> getPrivDicts() {
        return this.privateDictionaries;
    }

    void setPrivDict(List<Map<String, Object>> privDict) {
        this.privateDictionaries = privDict;
    }

    public FDSelect getFdSelect() {
        return this.fdSelect;
    }

    void setFdSelect(FDSelect fdSelect) {
        this.fdSelect = fdSelect;
    }

    private int getDefaultWidthX(int gid) {
        int fdArrayIndex = this.fdSelect.getFDIndex(gid);
        if (fdArrayIndex == -1 || fdArrayIndex >= this.privateDictionaries.size()) {
            return 1000;
        }
        Map<String, Object> privDict = this.privateDictionaries.get(fdArrayIndex);
        return privDict.containsKey("defaultWidthX") ? ((Number)privDict.get("defaultWidthX")).intValue() : 1000;
    }

    private int getNominalWidthX(int gid) {
        int fdArrayIndex = this.fdSelect.getFDIndex(gid);
        if (fdArrayIndex == -1 || fdArrayIndex >= this.privateDictionaries.size()) {
            return 0;
        }
        Map<String, Object> privDict = this.privateDictionaries.get(fdArrayIndex);
        return privDict.containsKey("nominalWidthX") ? ((Number)privDict.get("nominalWidthX")).intValue() : 0;
    }

    private byte[][] getLocalSubrIndex(int gid) {
        int fdArrayIndex = this.fdSelect.getFDIndex(gid);
        if (fdArrayIndex == -1 || fdArrayIndex >= this.privateDictionaries.size()) {
            return null;
        }
        Map<String, Object> privDict = this.privateDictionaries.get(fdArrayIndex);
        return (byte[][])privDict.get("Subrs");
    }

    @Override
    public CIDKeyedType2CharString getType2CharString(int cid) throws IOException {
        CIDKeyedType2CharString type2 = this.charStringCache.get(cid);
        if (type2 == null) {
            int gid = this.charset.getGIDForCID(cid);
            byte[] bytes = this.charStrings[gid];
            if (bytes == null) {
                bytes = this.charStrings[0];
            }
            Type2CharStringParser parser = new Type2CharStringParser(this.fontName, cid);
            List<Object> type2seq = parser.parse(bytes, this.globalSubrIndex, this.getLocalSubrIndex(gid));
            type2 = new CIDKeyedType2CharString((Type1CharStringReader)this.reader, this.fontName, cid, gid, type2seq, this.getDefaultWidthX(gid), this.getNominalWidthX(gid));
            this.charStringCache.put(cid, type2);
        }
        return type2;
    }

    @Override
    public List<Number> getFontMatrix() {
        return (List)this.topDict.get("FontMatrix");
    }

    @Override
    public GeneralPath getPath(String selector) throws IOException {
        int cid = this.selectorToCID(selector);
        return this.getType2CharString(cid).getPath();
    }

    @Override
    public float getWidth(String selector) throws IOException {
        int cid = this.selectorToCID(selector);
        return this.getType2CharString(cid).getWidth();
    }

    @Override
    public boolean hasGlyph(String selector) throws IOException {
        int cid = this.selectorToCID(selector);
        return cid != 0;
    }

    private int selectorToCID(String selector) {
        if (!selector.startsWith("\\")) {
            throw new IllegalArgumentException("Invalid selector");
        }
        return Integer.parseInt(selector.substring(1));
    }

    private class PrivateType1CharStringReader
    implements Type1CharStringReader {
        private PrivateType1CharStringReader() {
        }

        @Override
        public Type1CharString getType1CharString(String name) throws IOException {
            return CFFCIDFont.this.getType2CharString(0);
        }
    }
}

