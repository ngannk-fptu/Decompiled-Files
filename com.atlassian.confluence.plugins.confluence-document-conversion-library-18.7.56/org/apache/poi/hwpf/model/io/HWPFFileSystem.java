/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model.io;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.util.Internal;

@Internal
public final class HWPFFileSystem {
    private Map<String, ByteArrayOutputStream> _streams = new HashMap<String, ByteArrayOutputStream>();

    public HWPFFileSystem() {
        this._streams.put("WordDocument", new ByteArrayOutputStream(100000));
        this._streams.put("1Table", new ByteArrayOutputStream(100000));
        this._streams.put("Data", new ByteArrayOutputStream(100000));
    }

    public ByteArrayOutputStream getStream(String name) {
        return this._streams.get(name);
    }
}

