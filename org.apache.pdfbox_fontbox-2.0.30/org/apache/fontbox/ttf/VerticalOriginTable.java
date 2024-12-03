/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fontbox.ttf.TTFDataStream;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeFont;

public class VerticalOriginTable
extends TTFTable {
    public static final String TAG = "VORG";
    private float version;
    private int defaultVertOriginY;
    private Map<Integer, Integer> origins;

    VerticalOriginTable(TrueTypeFont font) {
        super(font);
    }

    @Override
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.version = data.read32Fixed();
        this.defaultVertOriginY = data.readSignedShort();
        int numVertOriginYMetrics = data.readUnsignedShort();
        this.origins = new ConcurrentHashMap<Integer, Integer>(numVertOriginYMetrics);
        for (int i = 0; i < numVertOriginYMetrics; ++i) {
            int g = data.readUnsignedShort();
            short y = data.readSignedShort();
            this.origins.put(g, Integer.valueOf(y));
        }
        this.initialized = true;
    }

    public float getVersion() {
        return this.version;
    }

    public int getOriginY(int gid) {
        if (this.origins.containsKey(gid)) {
            return this.origins.get(gid);
        }
        return this.defaultVertOriginY;
    }
}

