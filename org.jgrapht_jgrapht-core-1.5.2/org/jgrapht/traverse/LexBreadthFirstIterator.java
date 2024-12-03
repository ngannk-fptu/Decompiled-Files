/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.traverse.LexBreadthFirstIterator$BucketList.Bucket
 */
package org.jgrapht.traverse;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.jgrapht.traverse.LexBreadthFirstIterator;
import org.jgrapht.util.CollectionUtil;

public class LexBreadthFirstIterator<V, E>
extends AbstractGraphIterator<V, E> {
    private BucketList bucketList;
    private V current;

    public LexBreadthFirstIterator(Graph<V, E> graph) {
        super(graph);
        GraphTests.requireUndirected(graph);
        this.bucketList = new BucketList(graph.vertexSet());
    }

    @Override
    public boolean hasNext() {
        if (this.current != null) {
            return true;
        }
        this.current = this.advance();
        if (this.current != null && this.nListeners != 0) {
            this.fireVertexTraversed(this.createVertexTraversalEvent(this.current));
        }
        return this.current != null;
    }

    @Override
    public V next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        V result = this.current;
        this.current = null;
        if (this.nListeners != 0) {
            this.fireVertexFinished(this.createVertexTraversalEvent(result));
        }
        return result;
    }

    @Override
    public boolean isCrossComponentTraversal() {
        return true;
    }

    @Override
    public void setCrossComponentTraversal(boolean crossComponentTraversal) {
        if (!crossComponentTraversal) {
            throw new IllegalArgumentException("Iterator is always cross-component");
        }
    }

    private V advance() {
        Object vertex = this.bucketList.poll();
        if (vertex != null) {
            this.bucketList.updateBuckets(this.getUnvisitedNeighbours(vertex));
        }
        return vertex;
    }

    private Set<V> getUnvisitedNeighbours(V vertex) {
        LinkedHashSet<V> unmapped = new LinkedHashSet<V>();
        Set edges = this.graph.edgesOf(vertex);
        for (Object edge : edges) {
            V oppositeVertex = Graphs.getOppositeVertex(this.graph, edge, vertex);
            if (!this.bucketList.containsBucketWith(oppositeVertex)) continue;
            unmapped.add(oppositeVertex);
        }
        return unmapped;
    }

    class BucketList {
        private org.jgrapht.traverse.LexBreadthFirstIterator$BucketList.Bucket head;
        private Map<V, org.jgrapht.traverse.LexBreadthFirstIterator$BucketList.Bucket> bucketMap;

        BucketList(Collection<V> vertices) {
            this.head = new Bucket(vertices);
            this.bucketMap = CollectionUtil.newHashMapWithExpectedSize(vertices.size());
            for (Object vertex : vertices) {
                this.bucketMap.put((org.jgrapht.traverse.LexBreadthFirstIterator$BucketList.Bucket)vertex, this.head);
            }
        }

        boolean containsBucketWith(V vertex) {
            return this.bucketMap.containsKey(vertex);
        }

        V poll() {
            if (this.bucketMap.size() > 0) {
                Object res = this.head.poll();
                this.bucketMap.remove(res);
                if (this.head.isEmpty()) {
                    this.head = this.head.next;
                    if (this.head != null) {
                        this.head.prev = null;
                    }
                }
                return res;
            }
            return null;
        }

        void updateBuckets(Set<V> vertices) {
            HashSet<Bucket> visitedBuckets = new HashSet<Bucket>();
            for (Object vertex : vertices) {
                Bucket bucket = (Bucket)this.bucketMap.get(vertex);
                if (visitedBuckets.contains(bucket)) {
                    bucket.prev.addVertex(vertex);
                    this.bucketMap.put((org.jgrapht.traverse.LexBreadthFirstIterator$BucketList.Bucket)vertex, bucket.prev);
                } else {
                    visitedBuckets.add(bucket);
                    Bucket newBucket = new Bucket(vertex);
                    newBucket.insertBefore(bucket);
                    this.bucketMap.put((org.jgrapht.traverse.LexBreadthFirstIterator$BucketList.Bucket)vertex, (org.jgrapht.traverse.LexBreadthFirstIterator$BucketList.Bucket)newBucket);
                    if (this.head == bucket) {
                        this.head = newBucket;
                    }
                }
                bucket.removeVertex(vertex);
                if (!bucket.isEmpty()) continue;
                visitedBuckets.remove(bucket);
                bucket.removeSelf();
            }
        }

        private class Bucket {
            private org.jgrapht.traverse.LexBreadthFirstIterator$BucketList.Bucket next;
            private org.jgrapht.traverse.LexBreadthFirstIterator$BucketList.Bucket prev;
            private Set<V> vertices;

            Bucket(Collection<V> vertices) {
                this.vertices = new LinkedHashSet(vertices);
            }

            Bucket(V vertex) {
                this.vertices = new LinkedHashSet();
                this.vertices.add(vertex);
            }

            void removeVertex(V vertex) {
                this.vertices.remove(vertex);
            }

            void removeSelf() {
                if (this.next != null) {
                    this.next.prev = this.prev;
                }
                if (this.prev != null) {
                    this.prev.next = this.next;
                }
            }

            /*
             * Ignored method signature, as it can't be verified against descriptor
             */
            void insertBefore(Bucket bucket) {
                this.next = bucket;
                if (bucket != null) {
                    this.prev = bucket.prev;
                    if (bucket.prev != null) {
                        bucket.prev.next = this;
                    }
                    bucket.prev = this;
                } else {
                    this.prev = null;
                }
            }

            void addVertex(V vertex) {
                this.vertices.add(vertex);
            }

            V poll() {
                if (this.vertices.isEmpty()) {
                    return null;
                }
                Object vertex = this.vertices.iterator().next();
                this.vertices.remove(vertex);
                return vertex;
            }

            boolean isEmpty() {
                return this.vertices.size() == 0;
            }
        }
    }
}

