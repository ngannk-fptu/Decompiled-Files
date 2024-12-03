/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.fontbox.ttf.CFFTable;
import org.apache.fontbox.ttf.OTLTable;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;

public final class OTFParser
extends TTFParser {
    public OTFParser() {
    }

    public OTFParser(boolean isEmbedded) {
        this(isEmbedded, false);
    }

    public OTFParser(boolean isEmbedded, boolean parseOnDemand) {
        super(isEmbedded, parseOnDemand);
    }

    @Override
    public OpenTypeFont parse(String file) throws IOException {
        return (OpenTypeFont)super.parse(file);
    }

    @Override
    public OpenTypeFont parse(File file) throws IOException {
        return (OpenTypeFont)super.parse(file);
    }

    @Override
    public OpenTypeFont parse(InputStream data) throws IOException {
        return (OpenTypeFont)super.parse(data);
    }

    @Override
    OpenTypeFont parse(TTFDataStream raf) throws IOException {
        return (OpenTypeFont)super.parse(raf);
    }

    @Override
    OpenTypeFont newFont(TTFDataStream raf) {
        return new OpenTypeFont(raf);
    }

    @Override
    protected TTFTable readTable(TrueTypeFont font, String tag) {
        if (tag.equals("BASE") || tag.equals("GDEF") || tag.equals("GPOS") || tag.equals("GSUB") || tag.equals("JSTF")) {
            return new OTLTable(font);
        }
        if (tag.equals("CFF ")) {
            return new CFFTable(font);
        }
        return super.readTable(font, tag);
    }

    @Override
    protected boolean allowCFF() {
        return true;
    }
}

