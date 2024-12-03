/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface PartitioningAlgorithm<V> {
    public Partitioning<V> getPartitioning();

    public boolean isValidPartitioning(Partitioning<V> var1);

    public static class PartitioningImpl<V>
    implements Partitioning<V>,
    Serializable {
        private static final long serialVersionUID = 3702471090706836080L;
        private final List<Set<V>> classes;

        public PartitioningImpl(List<Set<V>> classes) {
            this.classes = Collections.unmodifiableList(Objects.requireNonNull(classes).stream().map(Collections::unmodifiableSet).collect(Collectors.toList()));
        }

        public PartitioningImpl(Map<V, Integer> vertexToPartitionMap) {
            Objects.requireNonNull(vertexToPartitionMap);
            HashMap<Integer, Set> partitionIndexToVertexMap = new HashMap<Integer, Set>();
            for (Map.Entry<V, Integer> entry : vertexToPartitionMap.entrySet()) {
                partitionIndexToVertexMap.computeIfAbsent(entry.getValue(), x -> new HashSet()).add(entry.getKey());
            }
            this.classes = Collections.unmodifiableList(partitionIndexToVertexMap.values().stream().map(Collections::unmodifiableSet).collect(Collectors.toList()));
        }

        @Override
        public int getNumberPartitions() {
            return this.classes.size();
        }

        @Override
        public Set<V> getPartition(int index) {
            if (index < 0 || index >= this.classes.size()) {
                throw new IndexOutOfBoundsException(index + " is not valid");
            }
            return this.classes.get(index);
        }

        public String toString() {
            return "Partition [number-of-partitions=" + this.getNumberPartitions() + ", partitions=" + this.classes + "]";
        }

        @Override
        public Iterator<Set<V>> iterator() {
            return this.classes.iterator();
        }
    }

    public static interface Partitioning<V>
    extends Iterable<Set<V>> {
        public int getNumberPartitions();

        public Set<V> getPartition(int var1);

        default public List<Set<V>> getPartitions() {
            int n = this.getNumberPartitions();
            ArrayList<Set<V>> partitions = new ArrayList<Set<V>>(n);
            for (int i = 0; i < n; ++i) {
                partitions.add(this.getPartition(i));
            }
            return partitions;
        }
    }
}

