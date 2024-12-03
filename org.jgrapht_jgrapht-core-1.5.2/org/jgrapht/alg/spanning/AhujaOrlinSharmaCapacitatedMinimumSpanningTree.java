/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.spanning;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.AhujaOrlinSharmaCyclicExchangeLocalAugmentation;
import org.jgrapht.alg.interfaces.CapacitatedSpanningTreeAlgorithm;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.spanning.AbstractCapacitatedMinimumSpanningTree;
import org.jgrapht.alg.spanning.EsauWilliamsCapacitatedMinimumSpanningTree;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.traverse.DepthFirstIterator;

public class AhujaOrlinSharmaCapacitatedMinimumSpanningTree<V, E>
extends AbstractCapacitatedMinimumSpanningTree<V, E> {
    private final int lengthBound;
    private final boolean bestImprovement;
    private final int numberOfOperationsParameter;
    private CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V, E> initialSolution;
    private final boolean useVertexOperation;
    private final boolean useSubtreeOperation;
    private final boolean useTabuSearch;
    private final int tabuTime;
    private final int upperLimitTabuExchanges;
    private boolean isAlgorithmExecuted;

    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> demands, int lengthBound, int numberOfOperationsParameter) {
        this(graph, root, capacity, demands, lengthBound, false, numberOfOperationsParameter, true, true, true, 10, 50);
    }

    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V, E> initialSolution, Graph<V, E> graph, V root, double capacity, Map<V, Double> demands, int lengthBound) {
        this(initialSolution, graph, root, capacity, demands, lengthBound, false, true, true, true, 10, 50);
    }

    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(Graph<V, E> graph, V root, double capacity, Map<V, Double> demands, int lengthBound, boolean bestImprovement, int numberOfOperationsParameter, boolean useVertexOperation, boolean useSubtreeOperation, boolean useTabuSearch, int tabuTime, int upperLimitTabuExchanges) {
        super(graph, root, capacity, demands);
        this.lengthBound = lengthBound;
        this.bestImprovement = bestImprovement;
        this.numberOfOperationsParameter = numberOfOperationsParameter;
        if (!useSubtreeOperation && !useVertexOperation) {
            throw new IllegalArgumentException("At least one of the options has to be enabled, otherwise it is not possible to excute the local search: useVertexOperation and useSubtreeOperation.");
        }
        this.useVertexOperation = useVertexOperation;
        this.useSubtreeOperation = useSubtreeOperation;
        this.useTabuSearch = useTabuSearch;
        this.tabuTime = tabuTime;
        this.upperLimitTabuExchanges = upperLimitTabuExchanges;
        this.isAlgorithmExecuted = false;
    }

    public AhujaOrlinSharmaCapacitatedMinimumSpanningTree(CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V, E> initialSolution, Graph<V, E> graph, V root, double capacity, Map<V, Double> demands, int lengthBound, boolean bestImprovement, boolean useVertexOperation, boolean useSubtreeOperation, boolean useTabuSearch, int tabuTime, int upperLimitTabuExchanges) {
        this(graph, root, capacity, demands, lengthBound, bestImprovement, 0, useVertexOperation, useSubtreeOperation, useTabuSearch, tabuTime, upperLimitTabuExchanges);
        if (!initialSolution.isCapacitatedSpanningTree(graph, root, capacity, demands)) {
            throw new IllegalArgumentException("The initial solution is not a valid capacitated spanning tree.");
        }
        this.initialSolution = initialSolution;
    }

    @Override
    public CapacitatedSpanningTreeAlgorithm.CapacitatedSpanningTree<V, E> getCapacitatedSpanningTree() {
        if (this.isAlgorithmExecuted) {
            return this.bestSolution.calculateResultingSpanningTree();
        }
        this.bestSolution = this.getInitialSolution();
        Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTrees = new HashMap<Integer, SpanningTreeAlgorithm.SpanningTree<E>>();
        Map subtrees = new HashMap();
        Pair<Set<Integer>, Set<Object>> affected = Pair.of(this.bestSolution.getLabels(), new HashSet());
        ImprovementGraph improvementGraph = new ImprovementGraph(this.bestSolution);
        HashSet tabuList = new HashSet();
        HashMap<Integer, Set<Object>> tabuTimeList = new HashMap<Integer, Set<Object>>();
        int tabuTimer = 0;
        int numberOfTabuExchanges = 0;
        AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution = this.bestSolution;
        double costDifference = 0.0;
        while (true) {
            partitionSpanningTrees = this.calculateSpanningTrees(currentSolution, partitionSpanningTrees, affected.getFirst());
            if (this.useSubtreeOperation) {
                subtrees = this.calculateSubtreesOfVertices(currentSolution, subtrees, partitionSpanningTrees, affected.getFirst());
            }
            improvementGraph.updateImprovementGraph(currentSolution, subtrees, partitionSpanningTrees, affected.getFirst(), tabuList);
            AhujaOrlinSharmaCyclicExchangeLocalAugmentation<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> ahujaOrlinSharmaCyclicExchangeLocalAugmentation = new AhujaOrlinSharmaCyclicExchangeLocalAugmentation<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge>(improvementGraph.improvementGraph, this.lengthBound, improvementGraph.cycleAugmentationLabels, this.bestImprovement);
            GraphWalk<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> cycle = ahujaOrlinSharmaCyclicExchangeLocalAugmentation.getLocalAugmentationCycle();
            double currentCost = cycle.getWeight();
            costDifference += currentCost;
            if (this.useTabuSearch) {
                Set set;
                if (currentCost < 0.0) {
                    affected = this.executeNeighborhoodOperation(currentSolution, improvementGraph.improvementGraphVertexMapping, improvementGraph.pathExchangeVertexMapping, subtrees, cycle);
                    if (costDifference < 0.0) {
                        this.bestSolution = currentSolution;
                        costDifference = 0.0;
                    }
                } else {
                    if (this.upperLimitTabuExchanges <= numberOfTabuExchanges) break;
                    if (currentSolution == this.bestSolution) {
                        currentSolution = currentSolution.clone();
                    }
                    affected = this.executeNeighborhoodOperation(currentSolution, improvementGraph.improvementGraphVertexMapping, improvementGraph.pathExchangeVertexMapping, subtrees, cycle);
                    tabuList.addAll(affected.getSecond());
                    tabuTimeList.put(tabuTimer, affected.getSecond());
                    ++numberOfTabuExchanges;
                }
                if ((set = (Set)tabuTimeList.remove(tabuTimer - this.tabuTime - 1)) != null) {
                    tabuList.removeAll(set);
                }
                ++tabuTimer;
                continue;
            }
            if (!(currentCost < 0.0)) break;
            affected = this.executeNeighborhoodOperation(currentSolution, improvementGraph.improvementGraphVertexMapping, improvementGraph.pathExchangeVertexMapping, subtrees, cycle);
        }
        this.isAlgorithmExecuted = true;
        return this.bestSolution.calculateResultingSpanningTree();
    }

    private AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation getInitialSolution() {
        if (this.initialSolution != null) {
            return new AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation(this.initialSolution.getLabels(), this.initialSolution.getPartition());
        }
        return new EsauWilliamsCapacitatedMinimumSpanningTree(this.graph, this.root, this.capacity, this.demands, this.numberOfOperationsParameter).getSolution();
    }

    /*
     * Unable to fully structure code
     */
    private Pair<Set<Integer>, Set<V>> executeNeighborhoodOperation(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution, Map<Integer, V> improvementGraphVertexMapping, Map<Pair<Integer, ImprovementGraphVertexType>, Integer> pathExchangeVertexMapping, Map<V, Pair<Set<V>, Double>> subtrees, GraphWalk<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> cycle) {
        block23: {
            affectedVertices = new HashSet<V>();
            affectedLabels = new HashSet<Integer>();
            it = cycle.getVertexList().iterator();
            if (!it.hasNext()) break block23;
            cur = it.next();
            switch (1.$SwitchMap$org$jgrapht$alg$spanning$AhujaOrlinSharmaCapacitatedMinimumSpanningTree$ImprovementGraphVertexType[cur.getSecond().ordinal()]) {
                case 1: {
                    firstLabel = currentSolution.getLabel(improvementGraphVertexMapping.get(cur.getFirst()));
                    break;
                }
                case 2: {
                    firstLabel = currentSolution.getLabel(improvementGraphVertexMapping.get(cur.getFirst()));
                    break;
                }
                default: {
                    firstLabel = -1;
                }
            }
            while (it.hasNext()) {
                next = it.next();
                switch (1.$SwitchMap$org$jgrapht$alg$spanning$AhujaOrlinSharmaCapacitatedMinimumSpanningTree$ImprovementGraphVertexType[cur.getSecond().ordinal()]) {
                    case 1: {
                        curVertex = improvementGraphVertexMapping.get(cur.getFirst());
                        curLabel = currentSolution.getLabel(curVertex);
                        if (!it.hasNext()) ** GOTO lbl34
                        switch (1.$SwitchMap$org$jgrapht$alg$spanning$AhujaOrlinSharmaCapacitatedMinimumSpanningTree$ImprovementGraphVertexType[next.getSecond().ordinal()]) {
                            case 1: {
                                nextLabel = currentSolution.getLabel(improvementGraphVertexMapping.get(next.getFirst()));
                                ** GOTO lbl35
                            }
                            case 2: {
                                nextLabel = currentSolution.getLabel(improvementGraphVertexMapping.get(next.getFirst()));
                                ** GOTO lbl35
                            }
                            case 3: {
                                nextLabel = pathExchangeVertexMapping.get(next);
                                ** GOTO lbl35
                            }
                            default: {
                                throw new IllegalStateException("This is a bug. There are invalid types of vertices in the cycle.");
                            }
                        }
lbl34:
                        // 1 sources

                        nextLabel = firstLabel;
lbl35:
                        // 4 sources

                        affectedVertices.add(curVertex);
                        affectedLabels.add(curLabel);
                        currentSolution.moveVertex(curVertex, curLabel, nextLabel);
                        break;
                    }
                    case 2: {
                        curVertex = improvementGraphVertexMapping.get(cur.getFirst());
                        curLabel = currentSolution.getLabel(curVertex);
                        if (!it.hasNext()) ** GOTO lbl57
                        switch (1.$SwitchMap$org$jgrapht$alg$spanning$AhujaOrlinSharmaCapacitatedMinimumSpanningTree$ImprovementGraphVertexType[next.getSecond().ordinal()]) {
                            case 1: {
                                nextLabel = currentSolution.getLabel(improvementGraphVertexMapping.get(next.getFirst()));
                                ** GOTO lbl58
                            }
                            case 2: {
                                nextLabel = currentSolution.getLabel(improvementGraphVertexMapping.get(next.getFirst()));
                                ** GOTO lbl58
                            }
                            case 3: {
                                nextLabel = pathExchangeVertexMapping.get(next);
                                ** GOTO lbl58
                            }
                            default: {
                                throw new IllegalStateException("This is a bug. There are invalid types of vertices in the cycle.");
                            }
                        }
lbl57:
                        // 1 sources

                        nextLabel = firstLabel;
lbl58:
                        // 4 sources

                        affectedVertices.add(curVertex);
                        affectedLabels.add(curLabel);
                        subtreeToMove = subtrees.get(curVertex).getFirst();
                        currentSolution.moveVertices(subtreeToMove, curLabel, nextLabel);
                        break;
                    }
                    case 3: {
                        curLabel = pathExchangeVertexMapping.get(cur);
                        affectedLabels.add(curLabel);
                        break;
                    }
                    case 4: {
                        break;
                    }
                    default: {
                        throw new IllegalStateException("This is a bug. There are invalid types of vertices in the cycle.");
                    }
                }
                cur = next;
            }
        }
        moreAffectedLabels = new HashSet<Integer>();
        affectedLabelIterator = affectedLabels.iterator();
        while (affectedLabelIterator.hasNext()) {
            label = (Integer)affectedLabelIterator.next();
            vertexSubset = currentSolution.getPartitionSet(label);
            if (vertexSubset.isEmpty()) {
                affectedLabelIterator.remove();
                continue;
            }
            moreAffectedLabels.addAll(currentSolution.partitionSubtreesOfSubset(vertexSubset, label));
        }
        affectedLabels.addAll(moreAffectedLabels);
        currentSolution.cleanUp();
        return Pair.of(affectedLabels, affectedVertices);
    }

    private Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> calculateSpanningTrees(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution, Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTrees, Set<Integer> affectedLabels) {
        for (Integer label : affectedLabels) {
            Set set = currentSolution.getPartitionSet(label);
            currentSolution.getPartitionSet(label).add(this.root);
            partitionSpanningTrees.put(label, new PrimMinimumSpanningTree(new AsSubgraph(this.graph, set)).getSpanningTree());
            currentSolution.getPartitionSet(label).remove(this.root);
        }
        return partitionSpanningTrees;
    }

    private Map<V, Pair<Set<V>, Double>> calculateSubtreesOfVertices(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution, Map<V, Pair<Set<V>, Double>> subtrees, Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTree, Set<Integer> affectedLabels) {
        for (Integer label : affectedLabels) {
            HashSet modifiableSet = new HashSet(currentSolution.getPartitionSet(label));
            modifiableSet.add(this.root);
            for (Object v : currentSolution.getPartitionSet(label)) {
                Pair currentSubtree = this.subtree(currentSolution, modifiableSet, v, partitionSpanningTree);
                subtrees.put(v, currentSubtree);
            }
        }
        return subtrees;
    }

    private Pair<Set<V>, Double> subtree(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution, Set<V> modifiableSet, V v, Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTree) {
        SpanningTreeAlgorithm.SpanningTree<E> partSpanningTree = partitionSpanningTree.get(currentSolution.getLabel(v));
        AsSubgraph spanningTree = new AsSubgraph(this.graph, modifiableSet, partSpanningTree.getEdges());
        HashSet subtree = new HashSet();
        double subtreeWeight = 0.0;
        DepthFirstIterator<V, E> depthFirstIterator = new DepthFirstIterator<V, E>(spanningTree, v);
        HashSet currentPath = new HashSet();
        double currentWeight = 0.0;
        boolean storeCurrentPath = true;
        while (depthFirstIterator.hasNext()) {
            Object next = depthFirstIterator.next();
            if (spanningTree.containsEdge(next, v)) {
                storeCurrentPath = true;
                subtree.addAll(currentPath);
                subtreeWeight += currentWeight;
                currentPath = new HashSet();
                currentWeight = 0.0;
            }
            if (next.equals(this.root)) {
                storeCurrentPath = false;
                currentPath = new HashSet();
                currentWeight = 0.0;
            }
            if (!storeCurrentPath) continue;
            currentPath.add(next);
            currentWeight += ((Double)this.demands.get(next)).doubleValue();
        }
        return Pair.of(subtree, subtreeWeight);
    }

    private class ImprovementGraph {
        Graph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> improvementGraph;
        AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation capacitatedSpanningTreeSolutionRepresentation;
        Map<Pair<Integer, ImprovementGraphVertexType>, Integer> cycleAugmentationLabels;
        Map<Integer, V> improvementGraphVertexMapping;
        Map<V, Integer> initialVertexMapping;
        Map<Integer, Pair<Integer, ImprovementGraphVertexType>> pseudoVertexMapping;
        Map<Pair<Integer, ImprovementGraphVertexType>, Integer> pathExchangeVertexMapping;
        Pair<Integer, ImprovementGraphVertexType> origin;
        final Integer originVertexLabel = -1;

        public ImprovementGraph(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation capacitatedSpanningTreeSolutionRepresentation) {
            this.capacitatedSpanningTreeSolutionRepresentation = capacitatedSpanningTreeSolutionRepresentation;
            this.improvementGraphVertexMapping = new HashMap();
            this.initialVertexMapping = new HashMap();
            this.pseudoVertexMapping = new HashMap<Integer, Pair<Integer, ImprovementGraphVertexType>>();
            this.pathExchangeVertexMapping = new HashMap<Pair<Integer, ImprovementGraphVertexType>, Integer>();
            this.cycleAugmentationLabels = this.getImprovementGraphLabelMap();
            this.improvementGraph = this.createImprovementGraph();
        }

        public Graph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> createImprovementGraph() {
            DefaultDirectedWeightedGraph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge> improvementGraph = new DefaultDirectedWeightedGraph<Pair<Integer, ImprovementGraphVertexType>, DefaultWeightedEdge>(DefaultWeightedEdge.class);
            int counter = 0;
            for (Object v : AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph.vertexSet()) {
                if (v.equals(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root)) continue;
                if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useVertexOperation) {
                    Pair<Integer, ImprovementGraphVertexType> pair = new Pair<Integer, ImprovementGraphVertexType>(counter, ImprovementGraphVertexType.SINGLE);
                    improvementGraph.addVertex(pair);
                }
                if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useSubtreeOperation) {
                    Pair<Integer, ImprovementGraphVertexType> pair = new Pair<Integer, ImprovementGraphVertexType>(counter, ImprovementGraphVertexType.SUBTREE);
                    improvementGraph.addVertex(pair);
                }
                this.improvementGraphVertexMapping.put(counter, v);
                this.initialVertexMapping.put((Integer)v, counter);
                ++counter;
            }
            Pair<Integer, ImprovementGraphVertexType> origin = new Pair<Integer, ImprovementGraphVertexType>(counter, ImprovementGraphVertexType.ORIGIN);
            improvementGraph.addVertex(origin);
            this.origin = origin;
            this.pathExchangeVertexMapping.put(origin, this.originVertexLabel);
            for (Integer n : this.capacitatedSpanningTreeSolutionRepresentation.getLabels()) {
                Pair<Integer, ImprovementGraphVertexType> pseudoVertex = new Pair<Integer, ImprovementGraphVertexType>(origin.getFirst() + n + 1, ImprovementGraphVertexType.PSEUDO);
                this.pseudoVertexMapping.put(n, pseudoVertex);
                this.pathExchangeVertexMapping.put(pseudoVertex, n);
                improvementGraph.addVertex(pseudoVertex);
            }
            for (Pair pair : this.pseudoVertexMapping.values()) {
                improvementGraph.setEdgeWeight((DefaultWeightedEdge)improvementGraph.addEdge(pair, origin), 0.0);
            }
            return improvementGraph;
        }

        public void updateImprovementGraph(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution, Map<V, Pair<Set<V>, Double>> subtrees, Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTrees, Set<Integer> labelsToUpdate, Set<V> tabuList) {
            this.capacitatedSpanningTreeSolutionRepresentation = currentSolution;
            this.cycleAugmentationLabels = this.getImprovementGraphLabelMap();
            this.updatePseudoNodesOfNewLabels(currentSolution);
            for (Object v1 : AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph.vertexSet()) {
                Pair<Integer, ImprovementGraphVertexType> vertexOfV1Subtree;
                Pair<Integer, ImprovementGraphVertexType> vertexOfV1Single;
                if (v1.equals(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root) || this.updateTabuVertices(tabuList, v1, vertexOfV1Single = Pair.of(this.initialVertexMapping.get(v1), ImprovementGraphVertexType.SINGLE), vertexOfV1Subtree = Pair.of(this.initialVertexMapping.get(v1), ImprovementGraphVertexType.SUBTREE))) continue;
                this.updateOriginNodeConnections(currentSolution, subtrees, partitionSpanningTrees, labelsToUpdate, v1, vertexOfV1Single, vertexOfV1Subtree);
                for (Integer label : currentSolution.getLabels()) {
                    if (label.equals(currentSolution.getLabel(v1)) || !labelsToUpdate.contains(currentSolution.getLabel(v1)) && !labelsToUpdate.contains(label)) continue;
                    Pair<Integer, ImprovementGraphVertexType> pseudoVertex = this.pseudoVertexMapping.get(label);
                    HashSet modifiableSet = new HashSet(currentSolution.getPartitionSet(label));
                    modifiableSet.add(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root);
                    double oldWeight = partitionSpanningTrees.get(label).getWeight();
                    this.updateSingleNode(currentSolution, subtrees, tabuList, label, oldWeight, modifiableSet, pseudoVertex, v1, vertexOfV1Single);
                    this.updateSubtreeNode(currentSolution, subtrees, tabuList, label, oldWeight, modifiableSet, pseudoVertex, v1, vertexOfV1Subtree);
                }
            }
        }

        private void updatePseudoNodesOfNewLabels(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution) {
            if (!currentSolution.getLabels().equals(this.pseudoVertexMapping.keySet())) {
                Pair<Integer, ImprovementGraphVertexType> pseudoVertex;
                for (Integer label : currentSolution.getLabels()) {
                    if (this.pseudoVertexMapping.keySet().contains(label)) continue;
                    pseudoVertex = new Pair<Integer, ImprovementGraphVertexType>(this.origin.getFirst() + label + 1, ImprovementGraphVertexType.PSEUDO);
                    this.pseudoVertexMapping.put(label, pseudoVertex);
                    this.pathExchangeVertexMapping.put(pseudoVertex, label);
                    this.improvementGraph.addVertex(pseudoVertex);
                    DefaultWeightedEdge newEdge = this.improvementGraph.addEdge(pseudoVertex, this.origin);
                    this.improvementGraph.setEdgeWeight(newEdge, 0.0);
                }
                if (currentSolution.getLabels().size() != this.pseudoVertexMapping.keySet().size()) {
                    Iterator<Integer> labelIterator = this.pseudoVertexMapping.keySet().iterator();
                    while (labelIterator.hasNext()) {
                        int label = labelIterator.next();
                        if (currentSolution.getLabels().contains(label)) continue;
                        pseudoVertex = new Pair<Integer, ImprovementGraphVertexType>(this.origin.getFirst() + label + 1, ImprovementGraphVertexType.PSEUDO);
                        labelIterator.remove();
                        this.pathExchangeVertexMapping.remove(pseudoVertex);
                        this.improvementGraph.removeVertex(pseudoVertex);
                    }
                }
            }
        }

        private boolean updateTabuVertices(Set<V> tabuList, V v1, Pair<Integer, ImprovementGraphVertexType> vertexOfV1Single, Pair<Integer, ImprovementGraphVertexType> vertexOfV1Subtree) {
            if (tabuList.contains(v1)) {
                if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useVertexOperation) {
                    this.improvementGraph.removeVertex(vertexOfV1Single);
                    this.improvementGraph.addVertex(vertexOfV1Single);
                }
                if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useSubtreeOperation) {
                    this.improvementGraph.removeVertex(vertexOfV1Subtree);
                    this.improvementGraph.addVertex(vertexOfV1Subtree);
                }
                return true;
            }
            return false;
        }

        private void updateOriginNodeConnections(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution, Map<V, Pair<Set<V>, Double>> subtrees, Map<Integer, SpanningTreeAlgorithm.SpanningTree<E>> partitionSpanningTrees, Set<Integer> labelsToUpdate, V v1, Pair<Integer, ImprovementGraphVertexType> vertexOfV1Single, Pair<Integer, ImprovementGraphVertexType> vertexOfV1Subtree) {
            if (labelsToUpdate.contains(currentSolution.getLabel(v1))) {
                double newWeight;
                SpanningTreeAlgorithm.SpanningTree spanningTree;
                double oldWeight = partitionSpanningTrees.get(currentSolution.getLabel(v1)).getWeight();
                Set partitionSetOfV1 = currentSolution.getPartitionSet(currentSolution.getLabel(v1));
                partitionSetOfV1.add(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root);
                if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useVertexOperation) {
                    partitionSetOfV1.remove(v1);
                    spanningTree = new PrimMinimumSpanningTree(new AsSubgraph(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph, partitionSetOfV1)).getSpanningTree();
                    newWeight = spanningTree.getEdges().size() == partitionSetOfV1.size() - 1 ? spanningTree.getWeight() : Double.NaN;
                    this.updateImprovementGraphEdge(this.origin, vertexOfV1Single, 0.0, newWeight - oldWeight);
                    partitionSetOfV1.add(v1);
                }
                if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useSubtreeOperation) {
                    if (subtrees.get(v1).getFirst().size() > 1 || !AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useVertexOperation) {
                        partitionSetOfV1.removeAll(subtrees.get(v1).getFirst());
                        spanningTree = new PrimMinimumSpanningTree(new AsSubgraph(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph, partitionSetOfV1)).getSpanningTree();
                        newWeight = spanningTree.getEdges().size() == partitionSetOfV1.size() - 1 ? spanningTree.getWeight() : Double.NaN;
                        this.updateImprovementGraphEdge(this.origin, vertexOfV1Subtree, 0.0, newWeight - oldWeight);
                        partitionSetOfV1.addAll(subtrees.get(v1).getFirst());
                    } else {
                        this.improvementGraph.removeVertex(vertexOfV1Subtree);
                        this.improvementGraph.addVertex(vertexOfV1Subtree);
                    }
                }
                partitionSetOfV1.remove(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root);
            }
        }

        private void updateSingleNode(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution, Map<V, Pair<Set<V>, Double>> subtrees, Set<V> tabuList, int label, double oldWeight, Set<V> modifiableSet, Pair<Integer, ImprovementGraphVertexType> pseudoVertex, V v1, Pair<Integer, ImprovementGraphVertexType> vertexOfV1Single) {
            modifiableSet.add(v1);
            if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useVertexOperation) {
                double newWeight;
                double newCapacity;
                SpanningTreeAlgorithm.SpanningTree spanningTree;
                for (Object v2 : currentSolution.getPartitionSet(label)) {
                    if (v2.equals(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root)) {
                        throw new IllegalStateException("The root is in the partition. This is a bug.");
                    }
                    if (tabuList.contains(v2)) continue;
                    modifiableSet.remove(v2);
                    spanningTree = new PrimMinimumSpanningTree(new AsSubgraph(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph, modifiableSet, AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph.edgeSet())).getSpanningTree();
                    if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                        newCapacity = this.calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, currentSolution.getPartitionWeight(label) + (Double)AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.demands.get(v1) - (Double)AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.demands.get(v2));
                        newWeight = spanningTree.getWeight();
                    } else {
                        newCapacity = Double.NaN;
                        newWeight = Double.NaN;
                    }
                    this.updateImprovementGraphEdge(vertexOfV1Single, Pair.of(this.initialVertexMapping.get(v2), ImprovementGraphVertexType.SINGLE), newCapacity, newWeight - oldWeight);
                    modifiableSet.add(v2);
                    if (!AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useSubtreeOperation || subtrees.get(v2).getFirst().size() <= 1) continue;
                    modifiableSet.removeAll(subtrees.get(v2).getFirst());
                    spanningTree = new PrimMinimumSpanningTree(new AsSubgraph(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph, modifiableSet, AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph.edgeSet())).getSpanningTree();
                    if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                        newCapacity = this.calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, currentSolution.getPartitionWeight(label) + (Double)AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.demands.get(v1) - subtrees.get(v2).getSecond());
                        newWeight = spanningTree.getWeight();
                    } else {
                        newCapacity = Double.NaN;
                        newWeight = Double.NaN;
                    }
                    this.updateImprovementGraphEdge(vertexOfV1Single, Pair.of(this.initialVertexMapping.get(v2), ImprovementGraphVertexType.SUBTREE), newCapacity, newWeight - oldWeight);
                    modifiableSet.addAll(subtrees.get(v2).getFirst());
                }
                spanningTree = new PrimMinimumSpanningTree(new AsSubgraph(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph, modifiableSet, AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph.edgeSet())).getSpanningTree();
                if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                    newCapacity = this.calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, currentSolution.getPartitionWeight(label) + (Double)AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.demands.get(v1));
                    newWeight = spanningTree.getWeight();
                } else {
                    newCapacity = Double.NaN;
                    newWeight = Double.NaN;
                }
                this.updateImprovementGraphEdge(vertexOfV1Single, pseudoVertex, newCapacity, newWeight - oldWeight);
                modifiableSet.remove(v1);
            }
        }

        private void updateSubtreeNode(AbstractCapacitatedMinimumSpanningTree.CapacitatedSpanningTreeSolutionRepresentation currentSolution, Map<V, Pair<Set<V>, Double>> subtrees, Set<V> tabuList, int label, double oldWeight, Set<V> modifiableSet, Pair<Integer, ImprovementGraphVertexType> pseudoVertex, V v1, Pair<Integer, ImprovementGraphVertexType> vertexOfV1Subtree) {
            if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useSubtreeOperation && (subtrees.get(v1).getFirst().size() > 1 || !AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useVertexOperation)) {
                double newWeight;
                double newCapacity;
                SpanningTreeAlgorithm.SpanningTree spanningTree;
                modifiableSet.addAll(subtrees.get(v1).getFirst());
                for (Object v2 : currentSolution.getPartitionSet(label)) {
                    if (v2.equals(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root)) {
                        throw new IllegalStateException("The root is in the partition. This is a bug.");
                    }
                    if (tabuList.contains(v2)) continue;
                    if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useVertexOperation) {
                        modifiableSet.remove(v2);
                        spanningTree = new PrimMinimumSpanningTree(new AsSubgraph(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph, modifiableSet, AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph.edgeSet())).getSpanningTree();
                        if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                            newCapacity = this.calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, currentSolution.getPartitionWeight(label) + subtrees.get(v1).getSecond() - (Double)AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.demands.get(v2));
                            newWeight = spanningTree.getWeight();
                        } else {
                            newCapacity = Double.NaN;
                            newWeight = Double.NaN;
                        }
                        this.updateImprovementGraphEdge(vertexOfV1Subtree, Pair.of(this.initialVertexMapping.get(v2), ImprovementGraphVertexType.SINGLE), newCapacity, newWeight - oldWeight);
                        modifiableSet.add(v2);
                    }
                    modifiableSet.removeAll(subtrees.get(v2).getFirst());
                    spanningTree = new PrimMinimumSpanningTree(new AsSubgraph(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph, modifiableSet, AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph.edgeSet())).getSpanningTree();
                    if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                        newCapacity = this.calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, currentSolution.getPartitionWeight(currentSolution.getLabel(v2)) + subtrees.get(v1).getSecond() - subtrees.get(v2).getSecond());
                        newWeight = spanningTree.getWeight();
                    } else {
                        newCapacity = Double.NaN;
                        newWeight = Double.NaN;
                    }
                    this.updateImprovementGraphEdge(vertexOfV1Subtree, Pair.of(this.initialVertexMapping.get(v2), ImprovementGraphVertexType.SUBTREE), newCapacity, newWeight - oldWeight);
                    modifiableSet.addAll(subtrees.get(v2).getFirst());
                }
                spanningTree = new PrimMinimumSpanningTree(new AsSubgraph(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph, modifiableSet, AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph.edgeSet())).getSpanningTree();
                if (spanningTree.getEdges().size() == modifiableSet.size() - 1) {
                    newCapacity = this.calculateMaximumDemandOfSubtrees(modifiableSet, spanningTree, currentSolution.getPartitionWeight(label) + subtrees.get(v1).getSecond());
                    newWeight = spanningTree.getWeight();
                } else {
                    newCapacity = Double.NaN;
                    newWeight = Double.NaN;
                }
                this.updateImprovementGraphEdge(vertexOfV1Subtree, pseudoVertex, newCapacity, newWeight - oldWeight);
            }
        }

        public void updateImprovementGraphEdge(Pair<Integer, ImprovementGraphVertexType> v1, Pair<Integer, ImprovementGraphVertexType> v2, double newCapacity, double newCost) {
            if (!Double.isNaN(newCapacity) && newCapacity <= AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.capacity && !Double.isNaN(newCost)) {
                DefaultWeightedEdge edge = this.improvementGraph.getEdge(v1, v2);
                if (edge == null) {
                    edge = this.improvementGraph.addEdge(v1, v2);
                }
                this.improvementGraph.setEdgeWeight(edge, newCost);
            } else {
                this.improvementGraph.removeEdge(v1, v2);
            }
        }

        public double calculateMaximumDemandOfSubtrees(Set<V> vertexSubset, SpanningTreeAlgorithm.SpanningTree<E> spanningTree, double totalDemand) {
            AsSubgraph spanningTreeGraph = new AsSubgraph(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.graph, vertexSubset, spanningTree.getEdges());
            int degreeOfRoot = spanningTreeGraph.degreeOf(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root);
            if (degreeOfRoot == 1) {
                return totalDemand;
            }
            double maximumDemand = 0.0;
            DepthFirstIterator depthFirstIterator = new DepthFirstIterator(spanningTreeGraph, AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root);
            if (depthFirstIterator.hasNext()) {
                depthFirstIterator.next();
            }
            int numberOfRootEdgesExplored = 0;
            double exploredVerticesDemand = 0.0;
            double currentDemand = 0.0;
            while (depthFirstIterator.hasNext()) {
                Object next = depthFirstIterator.next();
                if (spanningTreeGraph.containsEdge(AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.root, next)) {
                    exploredVerticesDemand += currentDemand;
                    if (maximumDemand < currentDemand) {
                        maximumDemand = currentDemand;
                    }
                    if (maximumDemand >= 0.5 * totalDemand || exploredVerticesDemand + maximumDemand >= totalDemand) {
                        return maximumDemand;
                    }
                    if (numberOfRootEdgesExplored + 1 == degreeOfRoot) {
                        return Math.max(maximumDemand, totalDemand - exploredVerticesDemand);
                    }
                    ++numberOfRootEdgesExplored;
                    currentDemand = 0.0;
                }
                currentDemand += ((Double)AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.demands.get(next)).doubleValue();
            }
            return maximumDemand;
        }

        private Map<Pair<Integer, ImprovementGraphVertexType>, Integer> getImprovementGraphLabelMap() {
            return new AbstractMap<Pair<Integer, ImprovementGraphVertexType>, Integer>(){

                @Override
                public int size() {
                    return ImprovementGraph.this.improvementGraphVertexMapping.size() + ImprovementGraph.this.pathExchangeVertexMapping.size() + (ImprovementGraph.this.origin == null ? 0 : 1);
                }

                @Override
                public boolean isEmpty() {
                    return ImprovementGraph.this.improvementGraphVertexMapping.isEmpty() && ImprovementGraph.this.pathExchangeVertexMapping.isEmpty() && ImprovementGraph.this.origin == null;
                }

                @Override
                public boolean containsKey(Object key) {
                    if (key instanceof Pair) {
                        return ImprovementGraph.this.improvementGraphVertexMapping.containsKey(((Pair)key).getFirst()) || ImprovementGraph.this.pathExchangeVertexMapping.containsKey(key) || key.equals(ImprovementGraph.this.origin);
                    }
                    return false;
                }

                @Override
                public boolean containsValue(Object value) {
                    return ImprovementGraph.this.improvementGraphVertexMapping.containsValue(value) || ImprovementGraph.this.pathExchangeVertexMapping.containsValue(value) || value.equals(ImprovementGraph.this.originVertexLabel);
                }

                @Override
                public Integer get(Object key) {
                    if (key instanceof Pair) {
                        if (ImprovementGraph.this.improvementGraphVertexMapping.containsKey(((Pair)key).getFirst())) {
                            return ImprovementGraph.this.capacitatedSpanningTreeSolutionRepresentation.getLabel(ImprovementGraph.this.improvementGraphVertexMapping.get(((Pair)key).getFirst()));
                        }
                        if (key.equals(ImprovementGraph.this.origin)) {
                            return ImprovementGraph.this.originVertexLabel;
                        }
                    }
                    return ImprovementGraph.this.pathExchangeVertexMapping.get(key);
                }

                @Override
                public Integer put(Pair<Integer, ImprovementGraphVertexType> key, Integer value) {
                    throw new IllegalStateException();
                }

                @Override
                public Integer remove(Object key) {
                    throw new IllegalStateException();
                }

                @Override
                public void putAll(Map<? extends Pair<Integer, ImprovementGraphVertexType>, ? extends Integer> m) {
                    throw new IllegalStateException();
                }

                @Override
                public void clear() {
                    throw new IllegalStateException();
                }

                @Override
                public Set<Pair<Integer, ImprovementGraphVertexType>> keySet() {
                    HashSet<Pair<Integer, ImprovementGraphVertexType>> keySet = new HashSet<Pair<Integer, ImprovementGraphVertexType>>();
                    for (Integer i : ImprovementGraph.this.improvementGraphVertexMapping.keySet()) {
                        if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useVertexOperation) {
                            keySet.add(Pair.of(i, ImprovementGraphVertexType.SINGLE));
                        }
                        if (!AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useSubtreeOperation) continue;
                        keySet.add(Pair.of(i, ImprovementGraphVertexType.SUBTREE));
                    }
                    keySet.addAll(ImprovementGraph.this.pathExchangeVertexMapping.keySet());
                    keySet.add(ImprovementGraph.this.origin);
                    return keySet;
                }

                @Override
                public Collection<Integer> values() {
                    return ImprovementGraph.this.capacitatedSpanningTreeSolutionRepresentation.getLabels();
                }

                @Override
                public Set<Map.Entry<Pair<Integer, ImprovementGraphVertexType>, Integer>> entrySet() {
                    HashSet<Map.Entry<Pair<Integer, ImprovementGraphVertexType>, Integer>> entrySet = new HashSet<Map.Entry<Pair<Integer, ImprovementGraphVertexType>, Integer>>();
                    for (Integer n : ImprovementGraph.this.improvementGraphVertexMapping.keySet()) {
                        Integer label = ImprovementGraph.this.capacitatedSpanningTreeSolutionRepresentation.getLabel(ImprovementGraph.this.improvementGraphVertexMapping.get(n));
                        if (AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useVertexOperation) {
                            entrySet.add(new AbstractMap.SimpleEntry<Pair<Integer, ImprovementGraphVertexType>, Integer>(Pair.of(n, ImprovementGraphVertexType.SINGLE), label));
                        }
                        if (!AhujaOrlinSharmaCapacitatedMinimumSpanningTree.this.useSubtreeOperation) continue;
                        entrySet.add(new AbstractMap.SimpleEntry<Pair<Integer, ImprovementGraphVertexType>, Integer>(Pair.of(n, ImprovementGraphVertexType.SUBTREE), label));
                    }
                    for (Pair pair : ImprovementGraph.this.pathExchangeVertexMapping.keySet()) {
                        entrySet.add(new AbstractMap.SimpleEntry<Pair, Integer>(pair, ImprovementGraph.this.pathExchangeVertexMapping.get(pair)));
                    }
                    entrySet.add(new AbstractMap.SimpleEntry<Pair<Integer, ImprovementGraphVertexType>, Integer>(ImprovementGraph.this.origin, ImprovementGraph.this.originVertexLabel));
                    return entrySet;
                }
            };
        }
    }

    private static enum ImprovementGraphVertexType {
        SINGLE,
        SUBTREE,
        PSEUDO,
        ORIGIN;

    }
}

