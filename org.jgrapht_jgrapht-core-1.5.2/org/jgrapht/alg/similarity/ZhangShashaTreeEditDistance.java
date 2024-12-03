/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.similarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;

public class ZhangShashaTreeEditDistance<V, E> {
    private Graph<V, E> tree1;
    private V root1;
    private Graph<V, E> tree2;
    private V root2;
    private ToDoubleFunction<V> insertCost;
    private ToDoubleFunction<V> removeCost;
    private ToDoubleBiFunction<V, V> changeCost;
    private double[][] treeDistances;
    private List<List<List<EditOperation<V>>>> editOperationLists;
    private boolean algorithmExecuted;

    public ZhangShashaTreeEditDistance(Graph<V, E> tree1, V root1, Graph<V, E> tree2, V root2) {
        this(tree1, root1, tree2, root2, v -> 1.0, v -> 1.0, (v1, v2) -> {
            if (v1.equals(v2)) {
                return 0.0;
            }
            return 1.0;
        });
    }

    public ZhangShashaTreeEditDistance(Graph<V, E> tree1, V root1, Graph<V, E> tree2, V root2, ToDoubleFunction<V> insertCost, ToDoubleFunction<V> removeCost, ToDoubleBiFunction<V, V> changeCost) {
        this.tree1 = Objects.requireNonNull(tree1, "graph1 cannot be null!");
        this.root1 = Objects.requireNonNull(root1, "root1 cannot be null!");
        this.tree2 = Objects.requireNonNull(tree2, "graph2 cannot be null!");
        this.root2 = Objects.requireNonNull(root2, "root2 cannot be null!");
        this.insertCost = Objects.requireNonNull(insertCost, "insertCost cannot be null!");
        this.removeCost = Objects.requireNonNull(removeCost, "removeCost cannot be null!");
        this.changeCost = Objects.requireNonNull(changeCost, "changeCost cannot be null!");
        if (!GraphTests.isTree(tree1)) {
            throw new IllegalArgumentException("graph1 must be a tree!");
        }
        if (!GraphTests.isTree(tree2)) {
            throw new IllegalArgumentException("graph2 must be a tree!");
        }
        int m = tree1.vertexSet().size();
        int n = tree2.vertexSet().size();
        this.treeDistances = new double[m][n];
        this.editOperationLists = new ArrayList<List<List<EditOperation<V>>>>(m);
        for (int i = 0; i < m; ++i) {
            this.editOperationLists.add(new ArrayList<Object>(Collections.nCopies(n, null)));
        }
    }

    public double getDistance() {
        this.lazyRunAlgorithm();
        int m = this.tree1.vertexSet().size();
        int n = this.tree2.vertexSet().size();
        return this.treeDistances[m - 1][n - 1];
    }

    public List<EditOperation<V>> getEditOperationLists() {
        this.lazyRunAlgorithm();
        int m = this.tree1.vertexSet().size();
        int n = this.tree2.vertexSet().size();
        return Collections.unmodifiableList(this.editOperationLists.get(m - 1).get(n - 1));
    }

    private void lazyRunAlgorithm() {
        if (!this.algorithmExecuted) {
            TreeOrdering ordering1 = new TreeOrdering(this.tree1, this.root1);
            TreeOrdering ordering2 = new TreeOrdering(this.tree2, this.root2);
            for (int keyroot1 : ordering1.keyroots) {
                for (Integer keyroot2 : ordering2.keyroots) {
                    this.treeDistance(keyroot1, keyroot2, ordering1, ordering2);
                }
            }
            this.algorithmExecuted = true;
        }
    }

    private void treeDistance(int i, int j, TreeOrdering ordering1, TreeOrdering ordering2) {
        CacheEntry entry;
        Object i1Vertex;
        int i1;
        int li = ordering1.indexToLValueList.get(i);
        int lj = ordering2.indexToLValueList.get(j);
        int m = i - li + 2;
        int n = j - lj + 2;
        double[][] forestdist = new double[m][n];
        ArrayList<List<CacheEntry>> cachedOperations = new ArrayList<List<CacheEntry>>(m);
        for (int k = 0; k < m; ++k) {
            cachedOperations.add(new ArrayList<Object>(Collections.nCopies(n, null)));
        }
        int iOffset = li - 1;
        int jOffset = lj - 1;
        for (i1 = li; i1 <= i; ++i1) {
            i1Vertex = ordering1.indexToVertexList.get(i1);
            int iIndex = i1 - iOffset;
            forestdist[iIndex][0] = forestdist[iIndex - 1][0] + this.removeCost.applyAsDouble(i1Vertex);
            entry = new CacheEntry(iIndex - 1, 0, new EditOperation<Object>(OperationType.REMOVE, i1Vertex, null));
            ((List)cachedOperations.get(iIndex)).set(0, entry);
        }
        for (int j1 = lj; j1 <= j; ++j1) {
            Object j1Vertex = ordering2.indexToVertexList.get(j1);
            int jIndex = j1 - jOffset;
            forestdist[0][jIndex] = forestdist[0][jIndex - 1] + this.removeCost.applyAsDouble(j1Vertex);
            entry = new CacheEntry(0, jIndex - 1, new EditOperation<Object>(OperationType.INSERT, j1Vertex, null));
            ((List)cachedOperations.get(0)).set(jIndex, entry);
        }
        for (i1 = li; i1 <= i; ++i1) {
            i1Vertex = ordering1.indexToVertexList.get(i1);
            int li1 = ordering1.indexToLValueList.get(i1);
            for (int j1 = lj; j1 <= j; ++j1) {
                CacheEntry entry2;
                double result;
                Object j1Vertex = ordering2.indexToVertexList.get(j1);
                int lj1 = ordering2.indexToLValueList.get(j1);
                int iIndex = i1 - iOffset;
                int jIndex = j1 - jOffset;
                if (li1 == li && lj1 == lj) {
                    double dist3;
                    double dist2;
                    double dist1 = forestdist[iIndex - 1][jIndex] + this.removeCost.applyAsDouble(i1Vertex);
                    double result2 = Math.min(dist1, Math.min(dist2 = forestdist[iIndex][jIndex - 1] + this.insertCost.applyAsDouble(j1Vertex), dist3 = forestdist[iIndex - 1][jIndex - 1] + this.changeCost.applyAsDouble(i1Vertex, j1Vertex)));
                    CacheEntry entry3 = result2 == dist1 ? new CacheEntry(iIndex - 1, jIndex, new EditOperation<Object>(OperationType.REMOVE, i1Vertex, null)) : (result2 == dist2 ? new CacheEntry(iIndex, jIndex - 1, new EditOperation<Object>(OperationType.INSERT, j1Vertex, null)) : new CacheEntry(iIndex - 1, jIndex - 1, new EditOperation(OperationType.CHANGE, i1Vertex, j1Vertex)));
                    ((List)cachedOperations.get(iIndex)).set(jIndex, entry3);
                    forestdist[iIndex][jIndex] = result2;
                    this.treeDistances[i1 - 1][j1 - 1] = result2;
                    this.editOperationLists.get(i1 - 1).set(j1 - 1, this.restoreOperationsList(cachedOperations, iIndex, jIndex));
                    continue;
                }
                int i2 = li1 - 1 - iOffset;
                int j2 = lj1 - 1 - jOffset;
                double dist1 = forestdist[iIndex - 1][jIndex] + this.removeCost.applyAsDouble(i1Vertex);
                double dist2 = forestdist[iIndex][jIndex - 1] + this.insertCost.applyAsDouble(j1Vertex);
                double dist3 = forestdist[i2][j2] + this.treeDistances[i1 - 1][j1 - 1];
                forestdist[iIndex][jIndex] = result = Math.min(dist1, Math.min(dist2, dist3));
                if (result == dist1) {
                    entry2 = new CacheEntry(iIndex - 1, jIndex, new EditOperation<Object>(OperationType.REMOVE, i1Vertex, null));
                } else if (result == dist2) {
                    entry2 = new CacheEntry(iIndex, jIndex - 1, new EditOperation<Object>(OperationType.INSERT, j1Vertex, null));
                } else {
                    entry2 = new CacheEntry(i2, j2, null);
                    entry2.treeDistanceI = i1 - 1;
                    entry2.treeDistanceJ = j1 - 1;
                }
                ((List)cachedOperations.get(iIndex)).set(jIndex, entry2);
            }
        }
    }

    private List<EditOperation<V>> restoreOperationsList(List<List<CacheEntry>> cachedOperations, int i, int j) {
        ArrayList<EditOperation<V>> result = new ArrayList<EditOperation<V>>();
        CacheEntry it = cachedOperations.get(i).get(j);
        while (it != null) {
            if (it.editOperation == null) {
                result.addAll((Collection)this.editOperationLists.get(it.treeDistanceI).get(it.treeDistanceJ));
            } else {
                result.add(it.editOperation);
            }
            it = cachedOperations.get(it.cachePreviousPosI).get(it.cachePreviousPosJ);
        }
        return result;
    }

    private class TreeOrdering {
        final Graph<V, E> tree;
        final V treeRoot;
        List<Integer> keyroots;
        List<V> indexToVertexList;
        List<Integer> indexToLValueList;
        int currentIndex;

        public TreeOrdering(Graph<V, E> tree, V treeRoot) {
            this.tree = tree;
            this.treeRoot = treeRoot;
            int numberOfVertices = tree.vertexSet().size();
            this.keyroots = new ArrayList<Integer>();
            this.indexToVertexList = new ArrayList<Object>(Collections.nCopies(numberOfVertices + 1, null));
            this.indexToLValueList = new ArrayList<Object>(Collections.nCopies(numberOfVertices + 1, null));
            this.currentIndex = 1;
            this.computeKeyrootsAndMapping(treeRoot);
        }

        private void computeKeyrootsAndMapping(V treeRoot) {
            ArrayList<StackEntry> stack = new ArrayList<StackEntry>();
            stack.add(new StackEntry(treeRoot, true));
            while (!stack.isEmpty()) {
                StackEntry entry = (StackEntry)stack.get(stack.size() - 1);
                if (entry.state == 0) {
                    if (stack.size() > 1) {
                        entry.vParent = ((StackEntry)stack.get((int)(stack.size() - 2))).v;
                    }
                    entry.vChildIterator = Graphs.successorListOf(this.tree, entry.v).iterator();
                    entry.state = 1;
                    continue;
                }
                if (entry.state == 1) {
                    if (entry.vChildIterator.hasNext()) {
                        entry.vChild = entry.vChildIterator.next();
                        if (entry.vParent != null && entry.vChild.equals(entry.vParent)) continue;
                        stack.add(new StackEntry(entry.vChild, entry.isKeyrootArg));
                        entry.state = 2;
                        continue;
                    }
                    entry.state = 3;
                    continue;
                }
                if (entry.state == 2) {
                    entry.isKeyrootArg = true;
                    if (entry.lValue == -1) {
                        entry.lValue = entry.lVChild;
                    }
                    entry.state = 1;
                    continue;
                }
                if (entry.state != 3) continue;
                if (entry.lValue == -1) {
                    entry.lValue = this.currentIndex;
                }
                if (entry.isKeyroot) {
                    this.keyroots.add(this.currentIndex);
                }
                this.indexToVertexList.set(this.currentIndex, entry.v);
                this.indexToLValueList.set(this.currentIndex, entry.lValue);
                ++this.currentIndex;
                if (stack.size() > 1) {
                    ((StackEntry)stack.get((int)(stack.size() - 2))).lVChild = entry.lValue;
                }
                stack.remove(stack.size() - 1);
            }
        }

        private class StackEntry {
            V v;
            boolean isKeyroot;
            V vParent;
            boolean isKeyrootArg;
            int lValue;
            Iterator<V> vChildIterator;
            V vChild;
            int lVChild;
            int state;

            public StackEntry(V v, boolean isKeyroot) {
                this.v = v;
                this.isKeyroot = isKeyroot;
                this.lValue = -1;
            }
        }
    }

    private class CacheEntry {
        int cachePreviousPosI;
        int cachePreviousPosJ;
        EditOperation<V> editOperation;
        int treeDistanceI;
        int treeDistanceJ;

        public CacheEntry(int cachePreviousPosI, int cachePreviousPosJ, EditOperation<V> editOperation) {
            this.cachePreviousPosI = cachePreviousPosI;
            this.cachePreviousPosJ = cachePreviousPosJ;
            this.editOperation = editOperation;
        }
    }

    public static class EditOperation<V> {
        private final OperationType type;
        private final V firstOperand;
        private final V secondOperand;

        public OperationType getType() {
            return this.type;
        }

        public V getFirstOperand() {
            return this.firstOperand;
        }

        public V getSecondOperand() {
            return this.secondOperand;
        }

        public EditOperation(OperationType type, V firstOperand, V secondOperand) {
            this.type = type;
            this.firstOperand = firstOperand;
            this.secondOperand = secondOperand;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            EditOperation editOperation = (EditOperation)o;
            if (this.type != editOperation.type) {
                return false;
            }
            if (!this.firstOperand.equals(editOperation.firstOperand)) {
                return false;
            }
            return this.secondOperand != null ? this.secondOperand.equals(editOperation.secondOperand) : editOperation.secondOperand == null;
        }

        public int hashCode() {
            int result = this.type.hashCode();
            result = 31 * result + this.firstOperand.hashCode();
            result = 31 * result + (this.secondOperand != null ? this.secondOperand.hashCode() : 0);
            return result;
        }

        public String toString() {
            if (this.type.equals((Object)OperationType.INSERT) || this.type.equals((Object)OperationType.REMOVE)) {
                return this.type + " " + this.firstOperand;
            }
            return this.type + " " + this.firstOperand + " -> " + this.secondOperand;
        }
    }

    public static enum OperationType {
        INSERT,
        REMOVE,
        CHANGE;

    }
}

