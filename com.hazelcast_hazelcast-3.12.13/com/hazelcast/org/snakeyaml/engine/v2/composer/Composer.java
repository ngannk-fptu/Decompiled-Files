/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.composer;

import com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettings;
import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.events.AliasEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.events.MappingStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.NodeEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.ScalarEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.SequenceStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.ComposerException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.MappingNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeTuple;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeType;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.ScalarNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.SequenceNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import com.hazelcast.org.snakeyaml.engine.v2.parser.Parser;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Composer
implements Iterator<Node> {
    protected final Parser parser;
    private final ScalarResolver scalarResolver;
    private final Map<Anchor, Node> anchors;
    private final Set<Node> recursiveNodes;
    private int nonScalarAliasesCount = 0;
    private final LoadSettings settings;

    public Composer(Parser parser, LoadSettings settings) {
        this.parser = parser;
        this.scalarResolver = settings.getScalarResolver();
        this.settings = settings;
        this.anchors = new HashMap<Anchor, Node>();
        this.recursiveNodes = new HashSet<Node>();
    }

    @Override
    public boolean hasNext() {
        if (this.parser.checkEvent(Event.ID.StreamStart)) {
            this.parser.next();
        }
        return !this.parser.checkEvent(Event.ID.StreamEnd);
    }

    public Optional<Node> getSingleNode() {
        this.parser.next();
        Optional<Node> document = Optional.empty();
        if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
            document = Optional.of(this.next());
        }
        if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
            Event event = this.parser.next();
            Optional<Mark> previousDocMark = document.flatMap(Node::getStartMark);
            throw new ComposerException("expected a single document in the stream", previousDocMark, "but found another document", event.getStartMark());
        }
        this.parser.next();
        return document;
    }

    @Override
    public Node next() {
        this.parser.next();
        Node node = this.composeNode(Optional.empty());
        this.parser.next();
        this.anchors.clear();
        this.recursiveNodes.clear();
        this.nonScalarAliasesCount = 0;
        return node;
    }

    private Node composeNode(Optional<Node> parent) {
        Node node;
        parent.ifPresent(this.recursiveNodes::add);
        if (this.parser.checkEvent(Event.ID.Alias)) {
            AliasEvent event = (AliasEvent)this.parser.next();
            Anchor anchor = event.getAlias();
            if (!this.anchors.containsKey(anchor)) {
                throw new ComposerException("found undefined alias " + anchor, event.getStartMark());
            }
            node = this.anchors.get(anchor);
            if (node.getNodeType() != NodeType.SCALAR) {
                ++this.nonScalarAliasesCount;
                if (this.nonScalarAliasesCount > this.settings.getMaxAliasesForCollections()) {
                    throw new YamlEngineException("Number of aliases for non-scalar nodes exceeds the specified max=" + this.settings.getMaxAliasesForCollections());
                }
            }
            if (this.recursiveNodes.remove(node)) {
                node.setRecursive(true);
            }
        } else {
            NodeEvent event = (NodeEvent)this.parser.peekEvent();
            Optional<Anchor> anchor = event.getAnchor();
            node = this.parser.checkEvent(Event.ID.Scalar) ? this.composeScalarNode(anchor) : (this.parser.checkEvent(Event.ID.SequenceStart) ? this.composeSequenceNode(anchor) : this.composeMappingNode(anchor));
        }
        parent.ifPresent(this.recursiveNodes::remove);
        return node;
    }

    private void registerAnchor(Anchor anchor, Node node) {
        this.anchors.put(anchor, node);
        node.setAnchor(Optional.of(anchor));
    }

    protected Node composeScalarNode(Optional<Anchor> anchor) {
        Tag nodeTag;
        ScalarEvent ev = (ScalarEvent)this.parser.next();
        Optional<String> tag = ev.getTag();
        boolean resolved = false;
        if (!tag.isPresent() || tag.get().equals("!")) {
            nodeTag = this.scalarResolver.resolve(ev.getValue(), ev.getImplicit().canOmitTagInPlainScalar());
            resolved = true;
        } else {
            nodeTag = new Tag(tag.get());
        }
        ScalarNode node = new ScalarNode(nodeTag, resolved, ev.getValue(), ev.getScalarStyle(), ev.getStartMark(), ev.getEndMark());
        anchor.ifPresent(a -> this.registerAnchor((Anchor)a, node));
        return node;
    }

    protected Node composeSequenceNode(Optional<Anchor> anchor) {
        Tag nodeTag;
        SequenceStartEvent startEvent = (SequenceStartEvent)this.parser.next();
        Optional<String> tag = startEvent.getTag();
        boolean resolved = false;
        if (!tag.isPresent() || tag.get().equals("!")) {
            nodeTag = Tag.SEQ;
            resolved = true;
        } else {
            nodeTag = new Tag(tag.get());
        }
        ArrayList<Node> children = new ArrayList<Node>();
        SequenceNode node = new SequenceNode(nodeTag, resolved, children, startEvent.getFlowStyle(), startEvent.getStartMark(), Optional.empty());
        anchor.ifPresent(a -> this.registerAnchor((Anchor)a, node));
        while (!this.parser.checkEvent(Event.ID.SequenceEnd)) {
            children.add(this.composeNode(Optional.of(node)));
        }
        Event endEvent = this.parser.next();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }

    protected Node composeMappingNode(Optional<Anchor> anchor) {
        Tag nodeTag;
        MappingStartEvent startEvent = (MappingStartEvent)this.parser.next();
        Optional<String> tag = startEvent.getTag();
        boolean resolved = false;
        if (!tag.isPresent() || tag.get().equals("!")) {
            nodeTag = Tag.MAP;
            resolved = true;
        } else {
            nodeTag = new Tag(tag.get());
        }
        ArrayList<NodeTuple> children = new ArrayList<NodeTuple>();
        MappingNode node = new MappingNode(nodeTag, resolved, children, startEvent.getFlowStyle(), startEvent.getStartMark(), Optional.empty());
        anchor.ifPresent(a -> this.registerAnchor((Anchor)a, node));
        while (!this.parser.checkEvent(Event.ID.MappingEnd)) {
            this.composeMappingChildren(children, node);
        }
        Event endEvent = this.parser.next();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }

    protected void composeMappingChildren(List<NodeTuple> children, MappingNode node) {
        Node itemKey = this.composeKeyNode(node);
        Node itemValue = this.composeValueNode(node);
        children.add(new NodeTuple(itemKey, itemValue));
    }

    protected Node composeKeyNode(MappingNode node) {
        return this.composeNode(Optional.of(node));
    }

    protected Node composeValueNode(MappingNode node) {
        return this.composeNode(Optional.of(node));
    }
}

