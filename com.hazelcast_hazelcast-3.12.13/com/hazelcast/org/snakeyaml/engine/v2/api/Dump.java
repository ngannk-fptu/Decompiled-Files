/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api;

import com.hazelcast.org.snakeyaml.engine.v2.api.DumpSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.StreamDataWriter;
import com.hazelcast.org.snakeyaml.engine.v2.api.StreamToStringWriter;
import com.hazelcast.org.snakeyaml.engine.v2.emitter.Emitter;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.representer.BaseRepresenter;
import com.hazelcast.org.snakeyaml.engine.v2.representer.StandardRepresenter;
import com.hazelcast.org.snakeyaml.engine.v2.serializer.Serializer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public class Dump {
    protected DumpSettings settings;
    protected BaseRepresenter representer;

    public Dump(DumpSettings settings) {
        this(settings, new StandardRepresenter(settings));
    }

    public Dump(DumpSettings settings, BaseRepresenter representer) {
        Objects.requireNonNull(settings, "DumpSettings cannot be null");
        Objects.requireNonNull(representer, "Representer cannot be null");
        this.settings = settings;
        this.representer = representer;
    }

    public void dumpAll(Iterator<? extends Object> instancesIterator, StreamDataWriter streamDataWriter) {
        Serializer serializer = new Serializer(this.settings, new Emitter(this.settings, streamDataWriter));
        serializer.open();
        while (instancesIterator.hasNext()) {
            Object instance = instancesIterator.next();
            Node node = this.representer.represent(instance);
            serializer.serialize(node);
        }
        serializer.close();
    }

    public void dump(Object yaml, StreamDataWriter streamDataWriter) {
        Iterator<Object> iter = Collections.singleton(yaml).iterator();
        this.dumpAll(iter, streamDataWriter);
    }

    public String dumpAllToString(Iterator<? extends Object> instancesIterator) {
        StreamToStringWriter writer = new StreamToStringWriter();
        this.dumpAll(instancesIterator, writer);
        return writer.toString();
    }

    public String dumpToString(Object yaml) {
        StreamToStringWriter writer = new StreamToStringWriter();
        this.dump(yaml, writer);
        return writer.toString();
    }
}

