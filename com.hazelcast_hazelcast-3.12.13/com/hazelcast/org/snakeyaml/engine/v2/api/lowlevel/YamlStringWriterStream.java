/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api.lowlevel;

import com.hazelcast.org.snakeyaml.engine.v2.api.StreamDataWriter;
import java.io.StringWriter;

class YamlStringWriterStream
implements StreamDataWriter {
    StringWriter writer = new StringWriter();

    YamlStringWriterStream() {
    }

    @Override
    public void write(String str) {
        this.writer.write(str);
    }

    @Override
    public void write(String str, int off, int len) {
        this.writer.write(str, off, len);
    }

    public String getString() {
        return this.writer.toString();
    }
}

