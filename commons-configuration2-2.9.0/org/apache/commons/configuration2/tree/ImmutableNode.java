/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ImmutableNode
implements Iterable<ImmutableNode> {
    private final String nodeName;
    private final Object value;
    private final List<ImmutableNode> children;
    private final Map<String, Object> attributes;

    private ImmutableNode(Builder b) {
        this.children = b.createChildren();
        this.attributes = b.createAttributes();
        this.nodeName = b.name;
        this.value = b.value;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public Object getValue() {
        return this.value;
    }

    public List<ImmutableNode> getChildren() {
        return this.children;
    }

    public List<ImmutableNode> getChildren(String name) {
        if (name == null) {
            return new ArrayList<ImmutableNode>();
        }
        return this.children.stream().filter(in -> name.equals(in.getNodeName())).collect(Collectors.toList());
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public ImmutableNode setName(String name) {
        return new Builder(this.children, this.attributes).name(name).value(this.value).create();
    }

    public ImmutableNode setValue(Object newValue) {
        return new Builder(this.children, this.attributes).name(this.nodeName).value(newValue).create();
    }

    public ImmutableNode addChild(ImmutableNode child) {
        ImmutableNode.checkChildNode(child);
        Builder builder = new Builder(this.children.size() + 1, this.attributes);
        builder.addChildren(this.children).addChild(child);
        return this.createWithBasicProperties(builder);
    }

    public ImmutableNode removeChild(ImmutableNode child) {
        Builder builder = new Builder(this.children.size(), this.attributes);
        boolean foundChild = false;
        for (ImmutableNode c : this.children) {
            if (c == child) {
                foundChild = true;
                continue;
            }
            builder.addChild(c);
        }
        return foundChild ? this.createWithBasicProperties(builder) : this;
    }

    public ImmutableNode replaceChild(ImmutableNode oldChild, ImmutableNode newChild) {
        ImmutableNode.checkChildNode(newChild);
        Builder builder = new Builder(this.children.size(), this.attributes);
        boolean foundChild = false;
        for (ImmutableNode c : this.children) {
            if (c == oldChild) {
                builder.addChild(newChild);
                foundChild = true;
                continue;
            }
            builder.addChild(c);
        }
        return foundChild ? this.createWithBasicProperties(builder) : this;
    }

    public ImmutableNode replaceChildren(Collection<ImmutableNode> newChildren) {
        Builder builder = new Builder(null, this.attributes);
        builder.addChildren(newChildren);
        return this.createWithBasicProperties(builder);
    }

    public ImmutableNode setAttribute(String name, Object value) {
        HashMap<String, Object> newAttrs = new HashMap<String, Object>(this.attributes);
        newAttrs.put(name, value);
        return this.createWithNewAttributes(newAttrs);
    }

    public ImmutableNode setAttributes(Map<String, ?> newAttributes) {
        if (newAttributes == null || newAttributes.isEmpty()) {
            return this;
        }
        HashMap<String, Object> newAttrs = new HashMap<String, Object>(this.attributes);
        newAttrs.putAll(newAttributes);
        return this.createWithNewAttributes(newAttrs);
    }

    public ImmutableNode removeAttribute(String name) {
        HashMap<String, Object> newAttrs = new HashMap<String, Object>(this.attributes);
        if (newAttrs.remove(name) != null) {
            return this.createWithNewAttributes(newAttrs);
        }
        return this;
    }

    private ImmutableNode createWithBasicProperties(Builder builder) {
        return builder.name(this.nodeName).value(this.value).create();
    }

    private ImmutableNode createWithNewAttributes(Map<String, Object> newAttrs) {
        return this.createWithBasicProperties(new Builder(this.children, null).addAttributes(newAttrs));
    }

    private static void checkChildNode(ImmutableNode child) {
        if (child == null) {
            throw new IllegalArgumentException("Child node must not be null!");
        }
    }

    @Override
    public Iterator<ImmutableNode> iterator() {
        return this.children.iterator();
    }

    public Stream<ImmutableNode> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public String toString() {
        return super.toString() + "(" + this.nodeName + ")";
    }

    public static final class Builder {
        private final List<ImmutableNode> directChildren;
        private final Map<String, Object> directAttributes;
        private List<ImmutableNode> children;
        private Map<String, Object> attributes;
        private String name;
        private Object value;

        public Builder() {
            this(null, null);
        }

        public Builder(int childCount) {
            this();
            this.initChildrenCollection(childCount);
        }

        private Builder(List<ImmutableNode> dirChildren, Map<String, Object> dirAttrs) {
            this.directChildren = dirChildren;
            this.directAttributes = dirAttrs;
        }

        private Builder(int childCount, Map<String, Object> dirAttrs) {
            this(null, dirAttrs);
            this.initChildrenCollection(childCount);
        }

        public Builder name(String n) {
            this.name = n;
            return this;
        }

        public Builder value(Object v) {
            this.value = v;
            return this;
        }

        public Builder addChild(ImmutableNode c) {
            if (c != null) {
                this.ensureChildrenExist();
                this.children.add(c);
            }
            return this;
        }

        public Builder addChildren(Collection<? extends ImmutableNode> children) {
            if (children != null) {
                this.ensureChildrenExist();
                this.children.addAll(Builder.filterNull(children));
            }
            return this;
        }

        public Builder addAttribute(String name, Object value) {
            this.ensureAttributesExist();
            this.attributes.put(name, value);
            return this;
        }

        public Builder addAttributes(Map<String, ?> attrs) {
            if (attrs != null) {
                this.ensureAttributesExist();
                this.attributes.putAll(attrs);
            }
            return this;
        }

        public ImmutableNode create() {
            ImmutableNode newNode = new ImmutableNode(this);
            this.children = null;
            this.attributes = null;
            return newNode;
        }

        List<ImmutableNode> createChildren() {
            if (this.directChildren != null) {
                return this.directChildren;
            }
            if (this.children != null) {
                return Collections.unmodifiableList(this.children);
            }
            return Collections.emptyList();
        }

        private Map<String, Object> createAttributes() {
            if (this.directAttributes != null) {
                return this.directAttributes;
            }
            if (this.attributes != null) {
                return Collections.unmodifiableMap(this.attributes);
            }
            return Collections.emptyMap();
        }

        private void ensureChildrenExist() {
            if (this.children == null) {
                this.children = new LinkedList<ImmutableNode>();
            }
        }

        private void ensureAttributesExist() {
            if (this.attributes == null) {
                this.attributes = new HashMap<String, Object>();
            }
        }

        private void initChildrenCollection(int childCount) {
            if (childCount > 0) {
                this.children = new ArrayList<ImmutableNode>(childCount);
            }
        }

        private static Collection<? extends ImmutableNode> filterNull(Collection<? extends ImmutableNode> children) {
            ArrayList result = new ArrayList(children.size());
            children.forEach(c -> {
                if (c != null) {
                    result.add(c);
                }
            });
            return result;
        }
    }
}

