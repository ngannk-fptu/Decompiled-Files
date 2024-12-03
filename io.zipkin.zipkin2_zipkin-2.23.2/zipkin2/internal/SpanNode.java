/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.internal.Nullable;
import zipkin2.internal.Trace;

public final class SpanNode {
    static final Comparator<SpanNode> NODE_COMPARATOR = new Comparator<SpanNode>(){

        @Override
        public int compare(SpanNode left, SpanNode right) {
            long y;
            long x = left.span().timestampAsLong();
            return x < (y = right.span().timestampAsLong()) ? -1 : (x == y ? 0 : 1);
        }
    };
    @Nullable
    SpanNode parent;
    @Nullable
    Span span;
    List<SpanNode> children = Collections.emptyList();

    public static Builder newBuilder(Logger logger) {
        return new Builder(logger);
    }

    SpanNode(@Nullable Span span) {
        this.span = span;
    }

    @Nullable
    public SpanNode parent() {
        return this.parent;
    }

    @Nullable
    public Span span() {
        return this.span;
    }

    public List<SpanNode> children() {
        return this.children;
    }

    public Iterator<SpanNode> traverse() {
        return new BreadthFirstIterator(this);
    }

    SpanNode addChild(SpanNode child) {
        if (child == null) {
            throw new NullPointerException("child == null");
        }
        if (child == this) {
            throw new IllegalArgumentException("circular dependency on " + this);
        }
        if (this.children.equals(Collections.emptyList())) {
            this.children = new ArrayList<SpanNode>();
        }
        this.children.add(child);
        child.parent = this;
        return this;
    }

    static Object createKey(String id, boolean shared, @Nullable Endpoint endpoint) {
        if (!shared) {
            return id;
        }
        return new SharedKey(id, endpoint);
    }

    public String toString() {
        ArrayList<Span> childrenSpans = new ArrayList<Span>();
        int length = this.children.size();
        for (int i = 0; i < length; ++i) {
            childrenSpans.add(this.children.get((int)i).span);
        }
        return "SpanNode{parent=" + (this.parent != null ? this.parent.span : null) + ", span=" + this.span + ", children=" + childrenSpans + "}";
    }

    static final class SharedKey {
        final String id;
        @Nullable
        final Endpoint endpoint;

        SharedKey(String id, @Nullable Endpoint endpoint) {
            if (id == null) {
                throw new NullPointerException("id == null");
            }
            this.id = id;
            this.endpoint = endpoint;
        }

        public String toString() {
            return "SharedKey{id=" + this.id + ", endpoint=" + this.endpoint + "}";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof SharedKey)) {
                return false;
            }
            SharedKey that = (SharedKey)o;
            return this.id.equals(that.id) && SharedKey.equal(this.endpoint, that.endpoint);
        }

        static boolean equal(Object a, Object b) {
            return a == b || a != null && a.equals(b);
        }

        public int hashCode() {
            int result = 1;
            result *= 1000003;
            result ^= this.id.hashCode();
            result *= 1000003;
            return result ^= this.endpoint == null ? 0 : this.endpoint.hashCode();
        }
    }

    public static final class Builder {
        final Logger logger;
        SpanNode rootSpan = null;
        Map<Object, SpanNode> keyToNode = new LinkedHashMap<Object, SpanNode>();
        Map<Object, Object> spanToParent = new LinkedHashMap<Object, Object>();

        Builder(Logger logger) {
            this.logger = logger;
        }

        void clear() {
            this.rootSpan = null;
            this.keyToNode.clear();
            this.spanToParent.clear();
        }

        public SpanNode build(List<Span> spans) {
            int i;
            if (spans.isEmpty()) {
                throw new IllegalArgumentException("spans were empty");
            }
            this.clear();
            List<Span> cleaned = Trace.merge(spans);
            int length = cleaned.size();
            String traceId = cleaned.get(0).traceId();
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("building trace tree: traceId=" + traceId);
            }
            for (i = 0; i < length; ++i) {
                this.index(cleaned.get(i));
            }
            for (i = 0; i < length; ++i) {
                this.process(cleaned.get(i));
            }
            if (this.rootSpan == null) {
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("substituting dummy node for missing root span: traceId=" + traceId);
                }
                this.rootSpan = new SpanNode(null);
            }
            for (Map.Entry<Object, Object> entry : this.spanToParent.entrySet()) {
                SpanNode child = this.keyToNode.get(entry.getKey());
                SpanNode parent = this.keyToNode.get(entry.getValue());
                if (parent == null) {
                    this.rootSpan.addChild(child);
                    continue;
                }
                parent.addChild(child);
            }
            this.sortTreeByTimestamp(this.rootSpan);
            return this.rootSpan;
        }

        void sortTreeByTimestamp(SpanNode root) {
            ArrayDeque<SpanNode> queue = new ArrayDeque<SpanNode>();
            queue.add(root);
            while (!queue.isEmpty()) {
                SpanNode current = (SpanNode)queue.pop();
                if (current.children().isEmpty()) continue;
                Collections.sort(current.children(), NODE_COMPARATOR);
                queue.addAll(current.children());
            }
        }

        void index(Span span) {
            String parentKey;
            Object idKey;
            if (Boolean.TRUE.equals(span.shared())) {
                idKey = SpanNode.createKey(span.id(), true, span.localEndpoint());
                parentKey = span.id();
            } else {
                idKey = span.id();
                parentKey = span.parentId();
            }
            this.spanToParent.put(idKey, parentKey);
        }

        void process(Span span) {
            Endpoint endpoint = span.localEndpoint();
            boolean shared = Boolean.TRUE.equals(span.shared());
            Object key = SpanNode.createKey(span.id(), shared, span.localEndpoint());
            Object noEndpointKey = endpoint != null ? SpanNode.createKey(span.id(), shared, null) : key;
            Object parent = null;
            if (shared) {
                parent = span.id();
            } else if (span.parentId() != null) {
                parent = SpanNode.createKey(span.parentId(), true, endpoint);
                if (this.spanToParent.containsKey(parent)) {
                    this.spanToParent.put(noEndpointKey, parent);
                } else {
                    parent = span.parentId();
                }
            } else if (this.rootSpan != null && this.logger.isLoggable(Level.FINE)) {
                this.logger.fine(String.format("attributing span missing parent to root: traceId=%s, rootSpanId=%s, spanId=%s", span.traceId(), this.rootSpan.span().id(), span.id()));
            }
            SpanNode node = new SpanNode(span);
            if (parent == null && this.rootSpan == null) {
                this.rootSpan = node;
                this.spanToParent.remove(noEndpointKey);
            } else if (shared) {
                this.keyToNode.put(key, node);
                this.keyToNode.put(noEndpointKey, node);
            } else {
                this.keyToNode.put(noEndpointKey, node);
            }
        }
    }

    static final class BreadthFirstIterator
    implements Iterator<SpanNode> {
        final ArrayDeque<SpanNode> queue = new ArrayDeque();

        BreadthFirstIterator(SpanNode root) {
            if (root.span == null) {
                int length = root.children.size();
                for (int i = 0; i < length; ++i) {
                    this.queue.add(root.children.get(i));
                }
            } else {
                this.queue.add(root);
            }
        }

        @Override
        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        @Override
        public SpanNode next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            SpanNode result = this.queue.remove();
            int length = result.children.size();
            for (int i = 0; i < length; ++i) {
                this.queue.add(result.children.get(i));
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}

