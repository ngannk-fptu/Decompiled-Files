/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.cff.CFFCharset;
import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.cff.Type2CharString;
import org.apache.fontbox.util.BoundingBox;

public abstract class CFFFont
implements FontBoxFont {
    protected String fontName;
    protected final Map<String, Object> topDict = new LinkedHashMap<String, Object>();
    protected CFFCharset charset;
    protected byte[][] charStrings;
    protected byte[][] globalSubrIndex;
    private CFFParser.ByteSource source;

    @Override
    public String getName() {
        return this.fontName;
    }

    void setName(String name) {
        this.fontName = name;
    }

    public void addValueToTopDict(String name, Object value) {
        if (value != null) {
            this.topDict.put(name, value);
        }
    }

    public Map<String, Object> getTopDict() {
        return this.topDict;
    }

    @Override
    public abstract List<Number> getFontMatrix();

    @Override
    public BoundingBox getFontBBox() {
        List numbers = (List)this.topDict.get("FontBBox");
        return new BoundingBox(numbers);
    }

    public CFFCharset getCharset() {
        return this.charset;
    }

    void setCharset(CFFCharset charset) {
        this.charset = charset;
    }

    public final List<byte[]> getCharStringBytes() {
        return Arrays.asList(this.charStrings);
    }

    final void setData(CFFParser.ByteSource source) {
        this.source = source;
    }

    public byte[] getData() throws IOException {
        return this.source.getBytes();
    }

    public int getNumCharStrings() {
        return this.charStrings.length;
    }

    void setGlobalSubrIndex(byte[][] globalSubrIndexValue) {
        this.globalSubrIndex = globalSubrIndexValue;
    }

    public List<byte[]> getGlobalSubrIndex() {
        return Arrays.asList(this.globalSubrIndex);
    }

    public abstract Type2CharString getType2CharString(int var1) throws IOException;

    public String toString() {
        return this.getClass().getSimpleName() + "[name=" + this.fontName + ", topDict=" + this.topDict + ", charset=" + this.charset + ", charStrings=" + Arrays.deepToString((Object[])this.charStrings) + "]";
    }
}

