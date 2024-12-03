/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import zipkin2.DependencyLink;
import zipkin2.Span;
import zipkin2.internal.SpanNode;

public final class DependencyLinker {
    final Logger logger;
    final SpanNode.Builder builder;
    final Map<Pair, Long> callCounts = new LinkedHashMap<Pair, Long>();
    final Map<Pair, Long> errorCounts = new LinkedHashMap<Pair, Long>();

    public DependencyLinker() {
        this(Logger.getLogger(DependencyLinker.class.getName()));
    }

    DependencyLinker(Logger logger) {
        this.logger = logger;
        this.builder = SpanNode.newBuilder(logger);
    }

    public DependencyLinker putTrace(List<Span> spans) {
        if (spans.isEmpty()) {
            return this;
        }
        SpanNode traceTree = this.builder.build(spans);
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("traversing trace tree, breadth-first");
        }
        Iterator<SpanNode> i = traceTree.traverse();
        block4: while (i.hasNext()) {
            String remoteAncestorName;
            String parent;
            String child;
            Span.Kind kind;
            SpanNode current = i.next();
            Span currentSpan = current.span();
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("processing " + currentSpan);
            }
            if (Span.Kind.CLIENT.equals((Object)(kind = currentSpan.kind())) && !current.children().isEmpty()) continue;
            String serviceName = currentSpan.localServiceName();
            String remoteServiceName = currentSpan.remoteServiceName();
            if (kind == null) {
                if (serviceName != null && remoteServiceName != null) {
                    kind = Span.Kind.CLIENT;
                } else {
                    this.logger.fine("non remote span; skipping");
                    continue;
                }
            }
            switch (kind) {
                case SERVER: 
                case CONSUMER: {
                    child = serviceName;
                    parent = remoteServiceName;
                    if (current != traceTree || parent != null) break;
                    this.logger.fine("root's client is unknown; skipping");
                    continue block4;
                }
                case CLIENT: 
                case PRODUCER: {
                    parent = serviceName;
                    child = remoteServiceName;
                    break;
                }
                default: {
                    this.logger.fine("unknown kind; skipping");
                    continue block4;
                }
            }
            boolean isError = currentSpan.tags().containsKey("error");
            if (kind == Span.Kind.PRODUCER || kind == Span.Kind.CONSUMER) {
                if (parent == null || child == null) {
                    this.logger.fine("cannot link messaging span to its broker; skipping");
                    continue;
                }
                this.addLink(parent, child, isError);
                continue;
            }
            Span remoteAncestor = this.firstRemoteAncestor(current);
            if (remoteAncestor != null && (remoteAncestorName = remoteAncestor.localServiceName()) != null) {
                if (kind == Span.Kind.CLIENT && serviceName != null && !remoteAncestorName.equals(serviceName)) {
                    this.logger.fine("detected missing link to client span");
                    this.addLink(remoteAncestorName, serviceName, false);
                }
                if (kind == Span.Kind.SERVER || parent == null) {
                    parent = remoteAncestorName;
                }
                if (!isError && Span.Kind.CLIENT.equals((Object)remoteAncestor.kind()) && currentSpan.parentId() != null && currentSpan.parentId().equals(remoteAncestor.id())) {
                    isError = remoteAncestor.tags().containsKey("error");
                }
            }
            if (parent == null || child == null) {
                this.logger.fine("cannot find remote ancestor; skipping");
                continue;
            }
            this.addLink(parent, child, isError);
        }
        return this;
    }

    Span firstRemoteAncestor(SpanNode current) {
        for (SpanNode ancestor = current.parent(); ancestor != null; ancestor = ancestor.parent()) {
            Span maybeRemote = ancestor.span();
            if (maybeRemote == null || maybeRemote.kind() == null) continue;
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("found remote ancestor " + maybeRemote);
            }
            return maybeRemote;
        }
        return null;
    }

    void addLink(String parent, String child, boolean isError) {
        Pair key;
        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("incrementing " + (isError ? "error " : "") + "link " + parent + " -> " + child);
        }
        if (this.callCounts.containsKey(key = new Pair(parent, child))) {
            this.callCounts.put(key, this.callCounts.get(key) + 1L);
        } else {
            this.callCounts.put(key, 1L);
        }
        if (!isError) {
            return;
        }
        if (this.errorCounts.containsKey(key)) {
            this.errorCounts.put(key, this.errorCounts.get(key) + 1L);
        } else {
            this.errorCounts.put(key, 1L);
        }
    }

    public List<DependencyLink> link() {
        return DependencyLinker.link(this.callCounts, this.errorCounts);
    }

    public static List<DependencyLink> merge(Iterable<DependencyLink> in) {
        LinkedHashMap<Pair, Long> callCounts = new LinkedHashMap<Pair, Long>();
        LinkedHashMap<Pair, Long> errorCounts = new LinkedHashMap<Pair, Long>();
        for (DependencyLink link : in) {
            Pair parentChild = new Pair(link.parent(), link.child());
            long callCount = callCounts.containsKey(parentChild) ? (Long)callCounts.get(parentChild) : 0L;
            callCounts.put(parentChild, callCount += link.callCount());
            long errorCount = errorCounts.containsKey(parentChild) ? (Long)errorCounts.get(parentChild) : 0L;
            errorCounts.put(parentChild, errorCount += link.errorCount());
        }
        return DependencyLinker.link(callCounts, errorCounts);
    }

    static List<DependencyLink> link(Map<Pair, Long> callCounts, Map<Pair, Long> errorCounts) {
        ArrayList<DependencyLink> result = new ArrayList<DependencyLink>(callCounts.size());
        for (Map.Entry<Pair, Long> entry : callCounts.entrySet()) {
            Pair parentChild = entry.getKey();
            result.add(DependencyLink.newBuilder().parent(parentChild.left).child(parentChild.right).callCount(entry.getValue()).errorCount(errorCounts.containsKey(parentChild) ? errorCounts.get(parentChild) : 0L).build());
        }
        return result;
    }

    static final class Pair {
        final String left;
        final String right;

        Pair(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Pair)) {
                return false;
            }
            Pair that = (Pair)o;
            return this.left.equals(that.left) && this.right.equals(that.right);
        }

        public int hashCode() {
            int h$ = 1;
            h$ *= 1000003;
            h$ ^= this.left.hashCode();
            h$ *= 1000003;
            return h$ ^= this.right.hashCode();
        }
    }
}

