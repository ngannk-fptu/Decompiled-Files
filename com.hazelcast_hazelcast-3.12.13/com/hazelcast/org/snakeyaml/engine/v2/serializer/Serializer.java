/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.serializer;

import com.hazelcast.org.snakeyaml.engine.v2.api.DumpSettings;
import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.emitter.Emitable;
import com.hazelcast.org.snakeyaml.engine.v2.events.AliasEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.DocumentEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.DocumentStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.ImplicitTuple;
import com.hazelcast.org.snakeyaml.engine.v2.events.MappingEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.MappingStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.ScalarEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.SequenceEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.StreamEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.StreamStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.AnchorNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.CollectionNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.MappingNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeTuple;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeType;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.ScalarNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.SequenceNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Serializer {
    private final DumpSettings settings;
    private final Emitable emitable;
    private final Set<Node> serializedNodes;
    private final Map<Node, Anchor> anchors;

    public Serializer(DumpSettings settings, Emitable emitable) {
        this.settings = settings;
        this.emitable = emitable;
        this.serializedNodes = new HashSet<Node>();
        this.anchors = new HashMap<Node, Anchor>();
    }

    public void serialize(Node node) {
        this.emitable.emit(new DocumentStartEvent(this.settings.isExplicitStart(), this.settings.getYamlDirective(), this.settings.getTagDirective()));
        this.anchorNode(node);
        this.settings.getExplicitRootTag().ifPresent(node::setTag);
        this.serializeNode(node);
        this.emitable.emit(new DocumentEndEvent(this.settings.isExplicitEnd()));
        this.serializedNodes.clear();
        this.anchors.clear();
    }

    public void open() {
        this.emitable.emit(new StreamStartEvent());
    }

    public void close() {
        this.emitable.emit(new StreamEndEvent());
        this.anchors.clear();
        this.serializedNodes.clear();
    }

    private void anchorNode(Node node) {
        Node realNode = node.getNodeType() == NodeType.ANCHOR ? ((AnchorNode)node).getRealNode() : node;
        if (this.anchors.containsKey(realNode)) {
            this.anchors.computeIfAbsent(realNode, a -> this.settings.getAnchorGenerator().nextAnchor(realNode));
        } else {
            this.anchors.put(realNode, null);
            switch (realNode.getNodeType()) {
                case SEQUENCE: {
                    SequenceNode seqNode = (SequenceNode)realNode;
                    List<Node> list = seqNode.getValue();
                    for (Node item : list) {
                        this.anchorNode(item);
                    }
                    break;
                }
                case MAPPING: {
                    MappingNode mappingNode = (MappingNode)realNode;
                    List<NodeTuple> map = mappingNode.getValue();
                    for (NodeTuple object : map) {
                        Node key = object.getKeyNode();
                        Node value = object.getValueNode();
                        this.anchorNode(key);
                        this.anchorNode(value);
                    }
                    break;
                }
            }
        }
    }

    private void serializeNode(Node node) {
        if (node.getNodeType() == NodeType.ANCHOR) {
            node = ((AnchorNode)node).getRealNode();
        }
        Optional<Anchor> tAlias = Optional.ofNullable(this.anchors.get(node));
        if (this.serializedNodes.contains(node)) {
            this.emitable.emit(new AliasEvent(tAlias));
        } else {
            this.serializedNodes.add(node);
            switch (node.getNodeType()) {
                case SCALAR: {
                    ScalarNode scalarNode = (ScalarNode)node;
                    Tag detectedTag = this.settings.getScalarResolver().resolve(scalarNode.getValue(), true);
                    Tag defaultTag = this.settings.getScalarResolver().resolve(scalarNode.getValue(), false);
                    ImplicitTuple tuple = new ImplicitTuple(node.getTag().equals(detectedTag), node.getTag().equals(defaultTag));
                    ScalarEvent event = new ScalarEvent(tAlias, Optional.of(node.getTag().getValue()), tuple, scalarNode.getValue(), scalarNode.getScalarStyle());
                    this.emitable.emit(event);
                    break;
                }
                case SEQUENCE: {
                    SequenceNode seqNode = (SequenceNode)node;
                    boolean implicitS = node.getTag().equals(Tag.SEQ);
                    this.emitable.emit(new SequenceStartEvent(tAlias, Optional.of(node.getTag().getValue()), implicitS, seqNode.getFlowStyle()));
                    List<Node> list = seqNode.getValue();
                    for (Node item : list) {
                        this.serializeNode(item);
                    }
                    this.emitable.emit(new SequenceEndEvent());
                    break;
                }
                default: {
                    boolean implicitM = node.getTag().equals(Tag.MAP);
                    this.emitable.emit(new MappingStartEvent(tAlias, Optional.of(node.getTag().getValue()), implicitM, ((CollectionNode)node).getFlowStyle()));
                    MappingNode mappingNode = (MappingNode)node;
                    List<NodeTuple> map = mappingNode.getValue();
                    for (NodeTuple entry : map) {
                        Node key = entry.getKeyNode();
                        Node value = entry.getValueNode();
                        this.serializeNode(key);
                        this.serializeNode(value);
                    }
                    this.emitable.emit(new MappingEndEvent());
                }
            }
        }
    }
}

