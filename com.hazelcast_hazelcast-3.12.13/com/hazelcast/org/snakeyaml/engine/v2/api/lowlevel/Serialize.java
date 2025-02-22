/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api.lowlevel;

import com.hazelcast.org.snakeyaml.engine.v2.api.DumpSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.lowlevel.EmitableEvents;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.serializer.Serializer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Serialize {
    private final DumpSettings settings;

    public Serialize(DumpSettings settings) {
        Objects.requireNonNull(settings, "DumpSettings cannot be null");
        this.settings = settings;
    }

    public List<Event> serializeOne(Node node) {
        Objects.requireNonNull(node, "Node cannot be null");
        return this.serializeAll(Collections.singletonList(node));
    }

    public List<Event> serializeAll(List<Node> nodes) {
        Objects.requireNonNull(nodes, "Nodes cannot be null");
        EmitableEvents emitableEvents = new EmitableEvents();
        Serializer serializer = new Serializer(this.settings, emitableEvents);
        serializer.open();
        for (Node node : nodes) {
            serializer.serialize(node);
        }
        serializer.close();
        return emitableEvents.getEvents();
    }
}

