/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.color;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.util.CollectionUtil;

public class SaturationDegreeColoring<V, E>
implements VertexColoringAlgorithm<V> {
    private final Graph<V, E> graph;

    public SaturationDegreeColoring(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    @Override
    public VertexColoringAlgorithm.Coloring<V> getColoring() {
        int n = this.graph.vertexSet().size();
        int maxColor = -1;
        HashMap colors = CollectionUtil.newHashMapWithExpectedSize(n);
        HashMap<V, BitSet> adjColors = CollectionUtil.newHashMapWithExpectedSize(n);
        HashMap<V, Integer> saturation = CollectionUtil.newHashMapWithExpectedSize(n);
        int maxDegree = 0;
        HashMap<V, Integer> degree = CollectionUtil.newHashMapWithExpectedSize(n);
        for (V v : this.graph.vertexSet()) {
            int d = this.graph.edgesOf(v).size();
            degree.put(v, d);
            maxDegree = Math.max(maxDegree, d);
            adjColors.put(v, new BitSet());
            saturation.put(v, 0);
        }
        Heap heap = new Heap(n, new DSaturComparator(saturation, degree));
        HashMap<V, HeapHandle> handles = new HashMap<V, HeapHandle>();
        for (V v : this.graph.vertexSet()) {
            handles.put(v, new HeapHandle(v));
        }
        heap.bulkInsert(handles.values().toArray((HeapHandle[])Array.newInstance(HeapHandle.class, 0)));
        while (heap.size() > 0) {
            Object v = heap.deleteMin().vertex;
            BitSet used = (BitSet)adjColors.get(v);
            int c = used.nextClearBit(0);
            maxColor = Math.max(maxColor, c);
            colors.put(v, c);
            adjColors.remove(v);
            for (E e : this.graph.edgesOf(v)) {
                V u = Graphs.getOppositeVertex(this.graph, e, v);
                if (colors.containsKey(u)) continue;
                int uSaturation = (Integer)saturation.get(u);
                BitSet uAdjColors = (BitSet)adjColors.get(u);
                HeapHandle uHandle = (HeapHandle)handles.get(u);
                if (uAdjColors.get(c)) {
                    heap.delete(uHandle);
                    degree.put(u, (Integer)degree.get(u) - 1);
                    heap.insert(uHandle);
                    continue;
                }
                uAdjColors.set(c);
                saturation.put(u, uSaturation + 1);
                degree.put(u, (Integer)degree.get(u) - 1);
                heap.fixup(uHandle);
            }
        }
        return new VertexColoringAlgorithm.ColoringImpl(colors, maxColor + 1);
    }

    private class Heap {
        private Comparator<V> comparator;
        private int size;
        private HeapHandle[] array;

        public Heap(int capacity, Comparator<V> comparator) {
            this.comparator = comparator;
            this.size = 0;
            this.array = (HeapHandle[])Array.newInstance(HeapHandle.class, capacity + 1);
        }

        private void fixdown(int k) {
            HeapHandle h = this.array[k];
            while (2 * k <= this.size) {
                int j = 2 * k;
                if (j < this.size && this.comparator.compare(this.array[j].vertex, this.array[j + 1].vertex) > 0) {
                    ++j;
                }
                if (this.comparator.compare(h.vertex, this.array[j].vertex) <= 0) break;
                this.array[k] = this.array[j];
                this.array[k].index = k;
                k = j;
            }
            this.array[k] = h;
            h.index = k;
        }

        private void fixup(int k) {
            HeapHandle h = this.array[k];
            while (k > 1 && this.comparator.compare(this.array[k / 2].vertex, h.vertex) > 0) {
                this.array[k] = this.array[k / 2];
                this.array[k].index = k;
                k /= 2;
            }
            this.array[k] = h;
            h.index = k;
        }

        private void forceFixup(int k) {
            HeapHandle h = this.array[k];
            while (k > 1) {
                this.array[k] = this.array[k / 2];
                this.array[k].index = k;
                k /= 2;
            }
            this.array[k] = h;
            h.index = k;
        }

        public HeapHandle deleteMin() {
            HeapHandle result = this.array[1];
            if (this.size == 1) {
                this.array[1] = null;
                this.size = 0;
            } else {
                this.array[1] = this.array[this.size];
                this.array[this.size] = null;
                --this.size;
                this.fixdown(1);
            }
            result.index = -1;
            return result;
        }

        public int size() {
            return this.size;
        }

        public void fixup(HeapHandle handle) {
            this.fixup(handle.index);
        }

        public void delete(HeapHandle handle) {
            this.forceFixup(handle.index);
            this.deleteMin();
        }

        public void insert(HeapHandle handle) {
            ++this.size;
            this.array[this.size] = handle;
            handle.index = this.size;
            this.fixup(this.size);
        }

        public void bulkInsert(HeapHandle[] handles) {
            int i;
            for (i = 0; i < handles.length; ++i) {
                ++this.size;
                this.array[this.size] = handles[i];
                handles[i].index = this.size;
            }
            for (i = this.size / 2; i > 0; --i) {
                this.fixdown(i);
            }
        }
    }

    private class DSaturComparator
    implements Comparator<V> {
        private Map<V, Integer> saturation;
        private Map<V, Integer> degree;

        public DSaturComparator(Map<V, Integer> saturation, Map<V, Integer> degree) {
            this.saturation = saturation;
            this.degree = degree;
        }

        @Override
        public int compare(V o1, V o2) {
            int sat2;
            int sat1 = this.saturation.get(o1);
            if (sat1 > (sat2 = this.saturation.get(o2).intValue())) {
                return -1;
            }
            if (sat1 < sat2) {
                return 1;
            }
            return -1 * Integer.compare(this.degree.get(o1), this.degree.get(o2));
        }
    }

    private class HeapHandle {
        int index;
        V vertex;

        public HeapHandle(V vertex) {
            this.vertex = vertex;
            this.index = -1;
        }
    }
}

