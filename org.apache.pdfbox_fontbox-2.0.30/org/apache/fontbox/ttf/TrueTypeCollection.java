/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.fontbox.ttf.MemoryTTFDataStream;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.RAFDataStream;
import org.apache.fontbox.ttf.TTCDataStream;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;

public class TrueTypeCollection
implements Closeable {
    private final TTFDataStream stream;
    private final int numFonts;
    private final long[] fontOffsets;

    public TrueTypeCollection(File file) throws IOException {
        this(new RAFDataStream(file, "r"));
    }

    public TrueTypeCollection(InputStream stream) throws IOException {
        this(new MemoryTTFDataStream(stream));
    }

    TrueTypeCollection(TTFDataStream stream) throws IOException {
        this.stream = stream;
        String tag = stream.readTag();
        if (!tag.equals("ttcf")) {
            throw new IOException("Missing TTC header");
        }
        float version = stream.read32Fixed();
        this.numFonts = (int)stream.readUnsignedInt();
        if (this.numFonts <= 0 || this.numFonts > 1024) {
            throw new IOException("Invalid number of fonts " + this.numFonts);
        }
        this.fontOffsets = new long[this.numFonts];
        for (int i = 0; i < this.numFonts; ++i) {
            this.fontOffsets[i] = stream.readUnsignedInt();
        }
        if (version >= 2.0f) {
            int ulDsigTag = stream.readUnsignedShort();
            int ulDsigLength = stream.readUnsignedShort();
            int n = stream.readUnsignedShort();
        }
    }

    public void processAllFonts(TrueTypeFontProcessor trueTypeFontProcessor) throws IOException {
        for (int i = 0; i < this.numFonts; ++i) {
            TrueTypeFont font = this.getFontAtIndex(i);
            trueTypeFontProcessor.process(font);
        }
    }

    private TrueTypeFont getFontAtIndex(int idx) throws IOException {
        this.stream.seek(this.fontOffsets[idx]);
        TTFParser parser = this.stream.readTag().equals("OTTO") ? new OTFParser(false, true) : new TTFParser(false, true);
        this.stream.seek(this.fontOffsets[idx]);
        return parser.parse(new TTCDataStream(this.stream));
    }

    public TrueTypeFont getFontByName(String name) throws IOException {
        for (int i = 0; i < this.numFonts; ++i) {
            TrueTypeFont font = this.getFontAtIndex(i);
            if (!font.getName().equals(name)) continue;
            return font;
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

    public static interface TrueTypeFontProcessor {
        public void process(TrueTypeFont var1) throws IOException;
    }
}

