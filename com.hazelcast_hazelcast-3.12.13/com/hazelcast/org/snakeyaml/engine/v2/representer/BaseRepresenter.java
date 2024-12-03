/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.representer;

import com.hazelcast.org.snakeyaml.engine.v2.api.RepresentToNode;
import com.hazelcast.org.snakeyaml.engine.v2.common.FlowStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.AnchorNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.MappingNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeTuple;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.ScalarNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.SequenceNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseRepresenter {
    protected final Map<Class<?>, RepresentToNode> representers = new HashMap();
    protected RepresentToNode nullRepresenter;
    protected final Map<Class<?>, RepresentToNode> parentClassRepresenters = new LinkedHashMap();
    protected ScalarStyle defaultScalarStyle = ScalarStyle.PLAIN;
    protected FlowStyle defaultFlowStyle = FlowStyle.AUTO;
    protected final Map<Object, Node> representedObjects = new IdentityHashMap<Object, Node>(){

        @Override
        public Node put(Object key, Node value) {
            return super.put(key, new AnchorNode(value));
        }
    };
    protected Object objectToRepresent;

    public Node represent(Object data) {
        Node node = this.representData(data);
        this.representedObjects.clear();
        this.objectToRepresent = null;
        return node;
    }

    protected Optional<RepresentToNode> findRepresenterFor(Object data) {
        Class<?> clazz = data.getClass();
        if (this.representers.containsKey(clazz)) {
            return Optional.of(this.representers.get(clazz));
        }
        for (Map.Entry<Class<?>, RepresentToNode> parentRepresenterEntry : this.parentClassRepresenters.entrySet()) {
            if (!parentRepresenterEntry.getKey().isInstance(data)) continue;
            return Optional.of(parentRepresenterEntry.getValue());
        }
        return Optional.empty();
    }

    protected final Node representData(Object data) {
        this.objectToRepresent = data;
        if (this.representedObjects.containsKey(this.objectToRepresent)) {
            return this.representedObjects.get(this.objectToRepresent);
        }
        if (data == null) {
            return this.nullRepresenter.representData(null);
        }
        RepresentToNode representer = this.findRepresenterFor(data).orElseThrow(() -> new YamlEngineException("Representer is not defined for " + data.getClass()));
        return representer.representData(data);
    }

    protected Node representScalar(Tag tag, String value, ScalarStyle style) {
        if (style == ScalarStyle.PLAIN) {
            style = this.defaultScalarStyle;
        }
        return new ScalarNode(tag, value, style);
    }

    protected Node representScalar(Tag tag, String value) {
        return this.representScalar(tag, value, ScalarStyle.PLAIN);
    }

    protected Node representSequence(Tag tag, Iterable<?> sequence, FlowStyle flowStyle) {
        int size = 10;
        if (sequence instanceof List) {
            size = ((List)sequence).size();
        }
        ArrayList<Node> value = new ArrayList<Node>(size);
        SequenceNode node = new SequenceNode(tag, value, flowStyle);
        this.representedObjects.put(this.objectToRepresent, node);
        FlowStyle bestStyle = FlowStyle.FLOW;
        for (Object item : sequence) {
            Node nodeItem = this.representData(item);
            if (!(nodeItem instanceof ScalarNode) || !((ScalarNode)nodeItem).isPlain()) {
                bestStyle = FlowStyle.BLOCK;
            }
            value.add(nodeItem);
        }
        if (flowStyle == FlowStyle.AUTO) {
            if (this.defaultFlowStyle != FlowStyle.AUTO) {
                node.setFlowStyle(this.defaultFlowStyle);
            } else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }

    protected Node representMapping(Tag tag, Map<?, ?> mapping, FlowStyle flowStyle) {
        ArrayList<NodeTuple> value = new ArrayList<NodeTuple>(mapping.size());
        MappingNode node = new MappingNode(tag, value, flowStyle);
        this.representedObjects.put(this.objectToRepresent, node);
        FlowStyle bestStyle = FlowStyle.FLOW;
        for (Map.Entry<?, ?> entry : mapping.entrySet()) {
            Node nodeKey = this.representData(entry.getKey());
            Node nodeValue = this.representData(entry.getValue());
            if (!(nodeKey instanceof ScalarNode) || !((ScalarNode)nodeKey).isPlain()) {
                bestStyle = FlowStyle.BLOCK;
            }
            if (!(nodeValue instanceof ScalarNode) || !((ScalarNode)nodeValue).isPlain()) {
                bestStyle = FlowStyle.BLOCK;
            }
            value.add(new NodeTuple(nodeKey, nodeValue));
        }
        if (flowStyle == FlowStyle.AUTO) {
            if (this.defaultFlowStyle != FlowStyle.AUTO) {
                node.setFlowStyle(this.defaultFlowStyle);
            } else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }
}

