/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeMatcher;
import org.apache.commons.configuration2.tree.NodeNameMatchers;

abstract class AbstractImmutableNodeHandler
implements NodeHandler<ImmutableNode> {
    AbstractImmutableNodeHandler() {
    }

    @Override
    public String nodeName(ImmutableNode node) {
        return node.getNodeName();
    }

    @Override
    public Object getValue(ImmutableNode node) {
        return node.getValue();
    }

    @Override
    public List<ImmutableNode> getChildren(ImmutableNode node) {
        return node.getChildren();
    }

    @Override
    public <C> int getMatchingChildrenCount(ImmutableNode node, NodeMatcher<C> matcher, C criterion) {
        return this.getMatchingChildren(node, matcher, criterion).size();
    }

    @Override
    public <C> List<ImmutableNode> getMatchingChildren(ImmutableNode node, NodeMatcher<C> matcher, C criterion) {
        return Collections.unmodifiableList(node.stream().filter(c -> matcher.matches(c, this, criterion)).collect(Collectors.toList()));
    }

    @Override
    public List<ImmutableNode> getChildren(ImmutableNode node, String name) {
        return this.getMatchingChildren(node, (NodeMatcher)NodeNameMatchers.EQUALS, (Object)name);
    }

    @Override
    public ImmutableNode getChild(ImmutableNode node, int index) {
        return node.getChildren().get(index);
    }

    @Override
    public int indexOfChild(ImmutableNode parent, ImmutableNode child) {
        return parent.getChildren().indexOf(child);
    }

    @Override
    public int getChildrenCount(ImmutableNode node, String name) {
        if (name == null) {
            return node.getChildren().size();
        }
        return this.getMatchingChildrenCount(node, (NodeMatcher)NodeNameMatchers.EQUALS, (Object)name);
    }

    @Override
    public Set<String> getAttributes(ImmutableNode node) {
        return node.getAttributes().keySet();
    }

    @Override
    public boolean hasAttributes(ImmutableNode node) {
        return !node.getAttributes().isEmpty();
    }

    @Override
    public Object getAttributeValue(ImmutableNode node, String name) {
        return node.getAttributes().get(name);
    }

    @Override
    public boolean isDefined(ImmutableNode node) {
        return AbstractImmutableNodeHandler.checkIfNodeDefined(node);
    }

    static boolean checkIfNodeDefined(ImmutableNode node) {
        return node.getValue() != null || !node.getChildren().isEmpty() || !node.getAttributes().isEmpty();
    }
}

