/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.jgrapht.Graph;
import org.jgrapht.graph.GraphDelegator;

public class AsSynchronizedGraph<V, E>
extends GraphDelegator<V, E>
implements Graph<V, E>,
Serializable {
    private static final long serialVersionUID = 5144561442831050752L;
    private final ReentrantReadWriteLock readWriteLock;
    private transient CopyOnDemandSet<V> allVerticesSet;
    private transient CopyOnDemandSet<E> allEdgesSet;
    private CacheStrategy<V, E> cacheStrategy;

    public AsSynchronizedGraph(Graph<V, E> g) {
        this(g, false, false, false);
    }

    private AsSynchronizedGraph(Graph<V, E> g, boolean cacheEnable, boolean fair, boolean copyless) {
        super(g);
        this.readWriteLock = new ReentrantReadWriteLock(fair);
        this.cacheStrategy = copyless ? new NoCopy() : (cacheEnable ? new CacheAccess() : new NoCache());
        this.allEdgesSet = new CopyOnDemandSet(super.edgeSet(), this.readWriteLock, copyless);
        this.allVerticesSet = new CopyOnDemandSet(super.vertexSet(), this.readWriteLock, copyless);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        this.readWriteLock.readLock().lock();
        try {
            Set set = super.getAllEdges(sourceVertex, targetVertex);
            return set;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E getEdge(V sourceVertex, V targetVertex) {
        this.readWriteLock.readLock().lock();
        try {
            Object e = super.getEdge(sourceVertex, targetVertex);
            return e;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        this.readWriteLock.writeLock().lock();
        try {
            E e = this.cacheStrategy.addEdge(sourceVertex, targetVertex);
            if (e != null) {
                this.edgeSetModified();
            }
            E e2 = e;
            return e2;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        this.readWriteLock.writeLock().lock();
        try {
            if (this.cacheStrategy.addEdge(sourceVertex, targetVertex, e)) {
                this.edgeSetModified();
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean addVertex(V v) {
        this.readWriteLock.writeLock().lock();
        try {
            if (super.addVertex(v)) {
                this.vertexSetModified();
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsEdge(V sourceVertex, V targetVertex) {
        this.readWriteLock.readLock().lock();
        try {
            boolean bl = super.containsEdge(sourceVertex, targetVertex);
            return bl;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsEdge(E e) {
        this.readWriteLock.readLock().lock();
        try {
            boolean bl = super.containsEdge(e);
            return bl;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean containsVertex(V v) {
        this.readWriteLock.readLock().lock();
        try {
            boolean bl = super.containsVertex(v);
            return bl;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int degreeOf(V vertex) {
        this.readWriteLock.readLock().lock();
        try {
            int n = super.degreeOf(vertex);
            return n;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Set<E> edgeSet() {
        return this.allEdgesSet;
    }

    @Override
    public Set<E> edgesOf(V vertex) {
        this.readWriteLock.readLock().lock();
        try {
            Set<E> set = this.cacheStrategy.edgesOf(vertex);
            return set;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int inDegreeOf(V vertex) {
        this.readWriteLock.readLock().lock();
        try {
            int n = super.inDegreeOf(vertex);
            return n;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        this.readWriteLock.readLock().lock();
        try {
            Set<E> set = this.cacheStrategy.incomingEdgesOf(vertex);
            return set;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int outDegreeOf(V vertex) {
        this.readWriteLock.readLock().lock();
        try {
            int n = super.outDegreeOf(vertex);
            return n;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        this.readWriteLock.readLock().lock();
        try {
            Set<E> set = this.cacheStrategy.outgoingEdgesOf(vertex);
            return set;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> edges) {
        this.readWriteLock.writeLock().lock();
        try {
            boolean bl = super.removeAllEdges(edges);
            return bl;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
        this.readWriteLock.writeLock().lock();
        try {
            Set set = super.removeAllEdges(sourceVertex, targetVertex);
            return set;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        this.readWriteLock.writeLock().lock();
        try {
            boolean bl = super.removeAllVertices(vertices);
            return bl;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeEdge(E e) {
        this.readWriteLock.writeLock().lock();
        try {
            if (this.cacheStrategy.removeEdge(e)) {
                this.edgeSetModified();
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E removeEdge(V sourceVertex, V targetVertex) {
        this.readWriteLock.writeLock().lock();
        try {
            E e = this.cacheStrategy.removeEdge(sourceVertex, targetVertex);
            if (e != null) {
                this.edgeSetModified();
            }
            E e2 = e;
            return e2;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeVertex(V v) {
        this.readWriteLock.writeLock().lock();
        try {
            if (this.cacheStrategy.removeVertex(v)) {
                this.edgeSetModified();
                this.vertexSetModified();
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        this.readWriteLock.readLock().lock();
        try {
            String string = super.toString();
            return string;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public Set<V> vertexSet() {
        return this.allVerticesSet;
    }

    @Override
    public V getEdgeSource(E e) {
        this.readWriteLock.readLock().lock();
        try {
            Object v = super.getEdgeSource(e);
            return v;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public V getEdgeTarget(E e) {
        this.readWriteLock.readLock().lock();
        try {
            Object v = super.getEdgeTarget(e);
            return v;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public double getEdgeWeight(E e) {
        this.readWriteLock.readLock().lock();
        try {
            double d = super.getEdgeWeight(e);
            return d;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setEdgeWeight(E e, double weight) {
        this.readWriteLock.writeLock().lock();
        try {
            super.setEdgeWeight(e, weight);
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    public boolean isCacheEnabled() {
        this.readWriteLock.readLock().lock();
        try {
            boolean bl = this.cacheStrategy.isCacheEnabled();
            return bl;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    public boolean isCopyless() {
        return this.allVerticesSet.isCopyless();
    }

    public AsSynchronizedGraph<V, E> setCache(boolean cacheEnabled) {
        this.readWriteLock.writeLock().lock();
        try {
            if (cacheEnabled == this.isCacheEnabled()) {
                AsSynchronizedGraph asSynchronizedGraph = this;
                return asSynchronizedGraph;
            }
            this.cacheStrategy = cacheEnabled ? new CacheAccess() : new NoCache();
            AsSynchronizedGraph asSynchronizedGraph = this;
            return asSynchronizedGraph;
        }
        finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public int hashCode() {
        this.readWriteLock.readLock().lock();
        try {
            int n = this.getDelegate().hashCode();
            return n;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        this.readWriteLock.readLock().lock();
        try {
            boolean bl = this.getDelegate().equals(o);
            return bl;
        }
        finally {
            this.readWriteLock.readLock().unlock();
        }
    }

    private <C> Set<C> copySet(Set<C> set) {
        return Collections.unmodifiableSet(new LinkedHashSet<C>(set));
    }

    private void vertexSetModified() {
        this.allVerticesSet.modified();
    }

    private void edgeSetModified() {
        this.allEdgesSet.modified();
    }

    public boolean isFair() {
        return this.readWriteLock.isFair();
    }

    public ReentrantReadWriteLock getLock() {
        return this.readWriteLock;
    }

    private class NoCopy
    extends NoCache {
        private static final long serialVersionUID = -5046944235164395939L;

        private NoCopy() {
        }

        @Override
        public Set<E> edgesOf(V vertex) {
            return AsSynchronizedGraph.super.edgesOf(vertex);
        }

        @Override
        public Set<E> incomingEdgesOf(V vertex) {
            return AsSynchronizedGraph.super.incomingEdgesOf(vertex);
        }

        @Override
        public Set<E> outgoingEdgesOf(V vertex) {
            return AsSynchronizedGraph.super.outgoingEdgesOf(vertex);
        }
    }

    private static interface CacheStrategy<V, E> {
        public E addEdge(V var1, V var2);

        public boolean addEdge(V var1, V var2, E var3);

        public Set<E> edgesOf(V var1);

        public Set<E> incomingEdgesOf(V var1);

        public Set<E> outgoingEdgesOf(V var1);

        public boolean removeEdge(E var1);

        public E removeEdge(V var1, V var2);

        public boolean removeVertex(V var1);

        public boolean isCacheEnabled();
    }

    private class CacheAccess
    implements CacheStrategy<V, E>,
    Serializable {
        private static final long serialVersionUID = -18262921841829294L;
        private final transient Map<V, Set<E>> incomingEdgesMap = new ConcurrentHashMap();
        private final transient Map<V, Set<E>> outgoingEdgesMap = new ConcurrentHashMap();
        private final transient Map<V, Set<E>> edgesOfMap = new ConcurrentHashMap();

        private CacheAccess() {
        }

        @Override
        public E addEdge(V sourceVertex, V targetVertex) {
            Object e = AsSynchronizedGraph.super.addEdge(sourceVertex, targetVertex);
            if (e != null) {
                this.edgeModified(sourceVertex, targetVertex);
            }
            return e;
        }

        @Override
        public boolean addEdge(V sourceVertex, V targetVertex, E e) {
            if (AsSynchronizedGraph.super.addEdge(sourceVertex, targetVertex, e)) {
                this.edgeModified(sourceVertex, targetVertex);
                return true;
            }
            return false;
        }

        @Override
        public Set<E> edgesOf(V vertex) {
            Set<Object> s = this.edgesOfMap.get(vertex);
            if (s != null) {
                return s;
            }
            s = AsSynchronizedGraph.this.copySet(AsSynchronizedGraph.super.edgesOf(vertex));
            this.edgesOfMap.put((Set)vertex, s);
            return s;
        }

        @Override
        public Set<E> incomingEdgesOf(V vertex) {
            Set<Object> s = this.incomingEdgesMap.get(vertex);
            if (s != null) {
                return s;
            }
            s = AsSynchronizedGraph.this.copySet(AsSynchronizedGraph.super.incomingEdgesOf(vertex));
            this.incomingEdgesMap.put((Set)vertex, s);
            return s;
        }

        @Override
        public Set<E> outgoingEdgesOf(V vertex) {
            Set<Object> s = this.outgoingEdgesMap.get(vertex);
            if (s != null) {
                return s;
            }
            s = AsSynchronizedGraph.this.copySet(AsSynchronizedGraph.super.outgoingEdgesOf(vertex));
            this.outgoingEdgesMap.put((Set)vertex, s);
            return s;
        }

        @Override
        public boolean removeEdge(E e) {
            Object sourceVertex = AsSynchronizedGraph.this.getEdgeSource(e);
            Object targetVertex = AsSynchronizedGraph.this.getEdgeTarget(e);
            if (AsSynchronizedGraph.super.removeEdge(e)) {
                this.edgeModified(sourceVertex, targetVertex);
                return true;
            }
            return false;
        }

        @Override
        public E removeEdge(V sourceVertex, V targetVertex) {
            Object e = AsSynchronizedGraph.super.removeEdge(sourceVertex, targetVertex);
            if (e != null) {
                this.edgeModified(sourceVertex, targetVertex);
            }
            return e;
        }

        @Override
        public boolean removeVertex(V v) {
            if (AsSynchronizedGraph.super.removeVertex(v)) {
                this.edgesOfMap.clear();
                this.incomingEdgesMap.clear();
                this.outgoingEdgesMap.clear();
                return true;
            }
            return false;
        }

        private void edgeModified(V sourceVertex, V targetVertex) {
            this.outgoingEdgesMap.remove(sourceVertex);
            this.incomingEdgesMap.remove(targetVertex);
            this.edgesOfMap.remove(sourceVertex);
            this.edgesOfMap.remove(targetVertex);
            if (!AsSynchronizedGraph.super.getType().isDirected()) {
                this.outgoingEdgesMap.remove(targetVertex);
                this.incomingEdgesMap.remove(sourceVertex);
            }
        }

        @Override
        public boolean isCacheEnabled() {
            return true;
        }
    }

    private class NoCache
    implements CacheStrategy<V, E>,
    Serializable {
        private static final long serialVersionUID = 19246150051213471L;

        private NoCache() {
        }

        @Override
        public E addEdge(V sourceVertex, V targetVertex) {
            return AsSynchronizedGraph.super.addEdge(sourceVertex, targetVertex);
        }

        @Override
        public boolean addEdge(V sourceVertex, V targetVertex, E e) {
            return AsSynchronizedGraph.super.addEdge(sourceVertex, targetVertex, e);
        }

        @Override
        public Set<E> edgesOf(V vertex) {
            return AsSynchronizedGraph.this.copySet(AsSynchronizedGraph.super.edgesOf(vertex));
        }

        @Override
        public Set<E> incomingEdgesOf(V vertex) {
            return AsSynchronizedGraph.this.copySet(AsSynchronizedGraph.super.incomingEdgesOf(vertex));
        }

        @Override
        public Set<E> outgoingEdgesOf(V vertex) {
            return AsSynchronizedGraph.this.copySet(AsSynchronizedGraph.super.outgoingEdgesOf(vertex));
        }

        @Override
        public boolean removeEdge(E e) {
            return AsSynchronizedGraph.super.removeEdge(e);
        }

        @Override
        public E removeEdge(V sourceVertex, V targetVertex) {
            return AsSynchronizedGraph.super.removeEdge(sourceVertex, targetVertex);
        }

        @Override
        public boolean removeVertex(V v) {
            return AsSynchronizedGraph.super.removeVertex(v);
        }

        @Override
        public boolean isCacheEnabled() {
            return false;
        }
    }

    private static class CopyOnDemandSet<E>
    implements Set<E>,
    Serializable {
        private static final long serialVersionUID = 5553953818148294283L;
        private Set<E> set;
        private final boolean copyless;
        private volatile transient Set<E> copy;
        final ReadWriteLock readWriteLock;
        private static final String UNMODIFIABLE = "this set is unmodifiable";

        private CopyOnDemandSet(Set<E> s, ReadWriteLock readWriteLock, boolean copyless) {
            this.set = Objects.requireNonNull(s, "s must not be null");
            this.copy = null;
            this.readWriteLock = readWriteLock;
            this.copyless = copyless;
        }

        public boolean isCopyless() {
            return this.copyless;
        }

        @Override
        public int size() {
            this.readWriteLock.readLock().lock();
            try {
                int n = this.set.size();
                return n;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        @Override
        public boolean isEmpty() {
            this.readWriteLock.readLock().lock();
            try {
                boolean bl = this.set.isEmpty();
                return bl;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        @Override
        public boolean contains(Object o) {
            this.readWriteLock.readLock().lock();
            try {
                boolean bl = this.set.contains(o);
                return bl;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        @Override
        public Iterator<E> iterator() {
            return this.getCopy().iterator();
        }

        @Override
        public Object[] toArray() {
            this.readWriteLock.readLock().lock();
            try {
                Object[] objectArray = this.set.toArray();
                return objectArray;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        @Override
        public <T> T[] toArray(T[] a) {
            this.readWriteLock.readLock().lock();
            try {
                T[] TArray = this.set.toArray(a);
                return TArray;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            this.readWriteLock.readLock().lock();
            try {
                boolean bl = this.set.containsAll(c);
                return bl;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            this.readWriteLock.readLock().lock();
            try {
                this.set.forEach(action);
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw new UnsupportedOperationException(UNMODIFIABLE);
        }

        @Override
        public Spliterator<E> spliterator() {
            return this.getCopy().spliterator();
        }

        @Override
        public Stream<E> stream() {
            return this.getCopy().stream();
        }

        @Override
        public Stream<E> parallelStream() {
            return this.getCopy().parallelStream();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            this.readWriteLock.readLock().lock();
            try {
                boolean bl = this.set.equals(o);
                return bl;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        @Override
        public int hashCode() {
            this.readWriteLock.readLock().lock();
            try {
                int n = this.set.hashCode();
                return n;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        public String toString() {
            this.readWriteLock.readLock().lock();
            try {
                String string = this.set.toString();
                return string;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Set<E> getCopy() {
            if (this.copyless) {
                return this.set;
            }
            this.readWriteLock.readLock().lock();
            try {
                CopyOnDemandSet copyOnDemandSet;
                Set<E> tempCopy = this.copy;
                if (tempCopy == null) {
                    copyOnDemandSet = this;
                    synchronized (copyOnDemandSet) {
                        tempCopy = this.copy;
                        if (tempCopy == null) {
                            tempCopy = new LinkedHashSet<E>(this.set);
                            this.copy = tempCopy;
                        }
                    }
                }
                copyOnDemandSet = tempCopy;
                return copyOnDemandSet;
            }
            finally {
                this.readWriteLock.readLock().unlock();
            }
        }

        private void modified() {
            this.copy = null;
        }
    }

    public static class Builder<V, E> {
        private boolean cacheEnable;
        private boolean fair;
        private boolean copyless;

        public Builder() {
            this.cacheEnable = false;
            this.fair = false;
            this.copyless = false;
        }

        public Builder(AsSynchronizedGraph<V, E> graph) {
            this.cacheEnable = graph.isCacheEnabled();
            this.fair = graph.isFair();
            this.copyless = graph.isCopyless();
        }

        public Builder<V, E> cacheDisable() {
            this.cacheEnable = false;
            return this;
        }

        public Builder<V, E> cacheEnable() {
            this.cacheEnable = true;
            return this;
        }

        public boolean isCacheEnable() {
            return this.cacheEnable;
        }

        public Builder<V, E> setCopyless() {
            this.copyless = true;
            return this;
        }

        public Builder<V, E> clearCopyless() {
            this.copyless = false;
            return this;
        }

        public boolean isCopyless() {
            return this.copyless;
        }

        public Builder<V, E> setFair() {
            this.fair = true;
            return this;
        }

        public Builder<V, E> setNonfair() {
            this.fair = false;
            return this;
        }

        public boolean isFair() {
            return this.fair;
        }

        public AsSynchronizedGraph<V, E> build(Graph<V, E> graph) {
            return new AsSynchronizedGraph<V, E>(graph, this.cacheEnable, this.fair, this.copyless);
        }
    }
}

