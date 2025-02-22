/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api.lowlevel;

import com.hazelcast.org.snakeyaml.engine.v2.api.DumpSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.lowlevel.YamlStringWriterStream;
import com.hazelcast.org.snakeyaml.engine.v2.emitter.Emitter;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import java.util.Iterator;
import java.util.Objects;

public class Present {
    private final DumpSettings settings;

    public Present(DumpSettings settings) {
        Objects.requireNonNull(settings, "DumpSettings cannot be null");
        this.settings = settings;
    }

    public String emitToString(Iterator<Event> events) {
        Objects.requireNonNull(events, "events cannot be null");
        YamlStringWriterStream writer = new YamlStringWriterStream();
        Emitter emitter = new Emitter(this.settings, writer);
        events.forEachRemaining(emitter::emit);
        return writer.getString();
    }
}

