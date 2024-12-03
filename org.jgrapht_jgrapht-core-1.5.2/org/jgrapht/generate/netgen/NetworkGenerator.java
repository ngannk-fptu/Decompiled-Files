/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate.netgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.generate.netgen.BipartiteMatchingProblem;
import org.jgrapht.generate.netgen.Distributor;
import org.jgrapht.generate.netgen.MaximumFlowProblem;
import org.jgrapht.generate.netgen.NetworkGeneratorConfig;
import org.jgrapht.generate.netgen.NetworkInfo;
import org.jgrapht.util.CollectionUtil;
import org.jgrapht.util.ElementsSequenceGenerator;

public class NetworkGenerator<V, E> {
    public static final int MAX_NODE_NUM = 100000000;
    public static final int MAX_SUPPLY = 200000000;
    public static final int MAX_ARC_NUM = 2000000000;
    public static final int CAPACITY_COST_BOUND = 2000000000;
    private final NetworkGeneratorConfig config;
    private final Random rng;
    private Graph<V, E> graph;
    private NetworkInfo<V, E> networkInfo;
    private List<Node> nodes;
    private Map<V, Node> graphVertexMapping;
    private Map<V, Integer> supplyMap;
    private Map<E, Integer> capacityMap;
    private Map<E, Integer> costMap;
    private long source2TSourceUB;
    private long source2TNodeUB;
    private long source2SinkUB;
    private long tNode2TSourceUB;
    private long tNode2TNodeUB;
    private long tNode2SinkUB;
    private long tSink2TSourceUB;
    private long tSink2TNodeUB;
    private long tSink2SinkUB;

    public NetworkGenerator(NetworkGeneratorConfig config) {
        this(config, System.nanoTime());
    }

    public NetworkGenerator(NetworkGeneratorConfig config, long seed) {
        this(config, new Random(seed));
    }

    public NetworkGenerator(NetworkGeneratorConfig config, Random rng) {
        this.config = config;
        this.rng = rng;
    }

    public BipartiteMatchingProblem<V, E> generateBipartiteMatchingProblem(Graph<V, E> graph) {
        if (!this.config.isAssignmentProblem()) {
            throw new IllegalArgumentException("Input config doesn't specify a bipartite matching problem");
        }
        GraphTests.requireDirected(graph);
        this.generate(graph);
        return new BipartiteMatchingProblem.BipartiteMatchingProblemImpl<V, Object>(graph, new HashSet<V>(this.networkInfo.getSources()), new HashSet<V>(this.networkInfo.getSinks()), e -> (double)this.costMap.get(e), this.config.isCostWeighted());
    }

    public MaximumFlowProblem<V, E> generateMaxFlowProblem(Graph<V, E> graph) {
        if (!this.config.isMaxFlowProblem()) {
            throw new IllegalArgumentException("Input config doesn't specify a maximum flow problem");
        }
        GraphTests.requireDirected(graph);
        this.generate(graph);
        return new MaximumFlowProblem.MaximumFlowProblemImpl<V, Object>(graph, new HashSet<V>(this.networkInfo.getSources()), new HashSet<V>(this.networkInfo.getSinks()), e -> (double)this.capacityMap.get(e));
    }

    public MinimumCostFlowProblem<V, E> generateMinimumCostFlowProblem(Graph<V, E> graph) {
        GraphTests.requireDirected(graph);
        this.generate(graph);
        return new MinimumCostFlowProblem.MinimumCostFlowProblemImpl<Object, Object>(graph, v -> this.supplyMap.getOrDefault(v, 0), e -> this.capacityMap.get(e), e -> this.costMap.get(e));
    }

    private void generate(Graph<V, E> graph) {
        this.init(graph);
        this.createSupply();
        this.initChains();
        this.generateChains();
        this.connectChainsToSinks();
        this.addAllRemainingArcs();
        this.networkInfo.vertices = this.nodes.stream().map(n -> n.graphVertex).collect(Collectors.toList());
    }

    private void init(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
        this.nodes = new ArrayList<Node>();
        this.graphVertexMapping = CollectionUtil.newHashMapWithExpectedSize(this.config.getNodeNum());
        this.supplyMap = new HashMap<V, Integer>();
        this.capacityMap = CollectionUtil.newHashMapWithExpectedSize(this.config.getArcNum());
        this.costMap = CollectionUtil.newHashMapWithExpectedSize(this.config.getArcNum());
        this.networkInfo = new NetworkInfo(this.config);
        this.source2TSourceUB = this.config.getMaxSource2TSourceArcNum();
        this.source2TNodeUB = this.config.getMaxSource2TNodeArcNum();
        this.source2SinkUB = this.config.getMaxSource2SinkArcNum();
        this.tNode2TSourceUB = this.config.getMaxTNode2TSourceArcNum();
        this.tNode2TNodeUB = this.config.getMaxTNode2TNodeArcNum();
        this.tNode2SinkUB = this.config.getMaxTNode2SinkArcNum();
        this.tSink2TSourceUB = this.config.getMaxTSink2TSourceArcNum();
        this.tSink2TNodeUB = this.config.getMaxTSink2TNodeArcNum();
        this.tSink2SinkUB = this.config.getMaxTSink2SinkArcNum();
        this.createNodes(this.config.getPureSourceNum(), NodeType.PURE_SOURCE);
        this.createNodes(this.config.getTransshipSourceNum(), NodeType.TRANSSHIP_SOURCE);
        this.createNodes(this.config.getTransshipNodeNum(), NodeType.TRANSSHIP_NODE);
        this.createNodes(this.config.getTransshipSinkNum(), NodeType.TRANSSHIP_SINK);
        this.createNodes(this.config.getPureSinkNum(), NodeType.PURE_SINK);
    }

    private void createNodes(int num, NodeType type) {
        for (int i = 0; i < num; ++i) {
            V vertex = this.graph.addVertex();
            Node node = new Node(vertex, type);
            this.nodes.add(node);
            this.graphVertexMapping.put((Node)vertex, node);
        }
    }

    private void createSupply() {
        int supplyPerSource = this.config.getTotalSupply() / this.config.getSourceNum();
        for (int sourceId = 0; sourceId < this.config.getSourceNum(); ++sourceId) {
            int partialSupply = this.generatePositiveRandom(supplyPerSource);
            this.nodes.get((int)sourceId).supply += partialSupply;
            int randomSourceId = this.generateRandom(this.config.getSourceNum());
            this.nodes.get((int)randomSourceId).supply += supplyPerSource - partialSupply;
        }
        int randomSourceId = this.generateRandom(this.config.getSourceNum());
        this.nodes.get((int)randomSourceId).supply += this.config.getTotalSupply() % this.config.getSourceNum();
        this.nodes.forEach(node -> {
            if (node.supply != 0) {
                this.supplyMap.put((Integer)node.graphVertex, node.supply);
            }
        });
    }

    private void initChains() {
        for (Node node : this.getSources()) {
            node.chainNodes.add(node);
        }
    }

    private void generateChains() {
        Node chainSource;
        int transshipmentNodeNum = this.config.getTransshipNodeNum();
        int sixtyPercent = 6 * transshipmentNodeNum / 10;
        ElementsSequenceGenerator<Node> tNodesGenerator = new ElementsSequenceGenerator<Node>(this.getTransshipNodes(), this.rng);
        int i = 0;
        int chainSourceId = 0;
        while (i < sixtyPercent) {
            if (chainSourceId == this.config.getSourceNum()) {
                chainSourceId = 0;
            }
            Node arcHead = tNodesGenerator.next();
            chainSource = this.nodes.get(chainSourceId);
            this.addSkeletonArc(chainSource, chainSource.getLastInChain(), arcHead);
            ++i;
            ++chainSourceId;
        }
        for (Node arcHead : tNodesGenerator) {
            int sourceId = this.rng.nextInt(this.config.getSourceNum());
            chainSource = this.nodes.get(sourceId);
            this.addSkeletonArc(chainSource, chainSource.getLastInChain(), arcHead);
        }
    }

    private void connectChainsToSinks() {
        int remainingArcs = this.config.getArcNum() - this.graph.edgeSet().size();
        assert (remainingArcs >= this.config.getSinkNum());
        int chainToSinkArcs = Math.min(remainingArcs, 2 * Math.max(this.config.getSourceNum(), this.config.getSinkNum()));
        int chainToSinkArcUB = (int)Math.min(this.source2SinkUB + this.tNode2SinkUB, 2000000000L);
        chainToSinkArcs = Math.min(chainToSinkArcUB, chainToSinkArcs);
        List<Node> sources = this.getSources();
        int supplyAndSinkNumUB = 0;
        for (Node source2 : sources) {
            supplyAndSinkNumUB += Math.min(this.config.getSinkNum(), source2.supply);
        }
        chainToSinkArcs = Math.min(chainToSinkArcs, supplyAndSinkNumUB);
        Distributor<Node> sinkDistributor = new Distributor<Node>(this.rng);
        sinkDistributor.addLowerBound(source -> 1);
        sinkDistributor.addUpperBound(source -> source.supply);
        sinkDistributor.addUpperBound(source -> this.config.getSinkNum());
        List<Integer> sinksPerSourceDist = sinkDistributor.getDistribution(sources, chainToSinkArcs);
        List<Node> sinks = this.getSinks();
        int sinkId = 0;
        for (int i = 0; i < sources.size(); ++i) {
            Node chainSource = sources.get(i);
            int sinksPerSource = sinksPerSourceDist.get(i);
            ArrayList<Node> chainSinks = new ArrayList<Node>();
            int j = 0;
            while (j < sinksPerSource) {
                if (sinkId == sinks.size()) {
                    sinkId = 0;
                }
                chainSinks.add(sinks.get(sinkId));
                ++j;
                ++sinkId;
            }
            Distributor<Node> sinkSupplyDistributor = new Distributor<Node>(this.rng);
            sinkSupplyDistributor.addLowerBound(sink -> 1);
            List<Integer> supplyDist = sinkSupplyDistributor.getDistribution(chainSinks, chainSource.supply);
            for (int j2 = 0; j2 < sinksPerSource; ++j2) {
                Node sink2 = (Node)chainSinks.get(j2);
                int sinkSupply = supplyDist.get(j2);
                int arcTailIndex = this.generateRandom(chainSource.getChainLength());
                Node arcTail = chainSource.chainNodes.get(arcTailIndex);
                this.addSkeletonArc(chainSource, arcTail, sink2);
                this.supplyMap.put((Integer)sink2.graphVertex, this.supplyMap.getOrDefault(sink2.graphVertex, 0) - sinkSupply);
            }
        }
    }

    private void addAllRemainingArcs() {
        int remainingArcs = this.config.getArcNum() - this.graph.edgeSet().size();
        assert (remainingArcs >= 0);
        ArrayList<Long> upperBounds = new ArrayList<Long>(List.of(Long.valueOf(this.source2TSourceUB), Long.valueOf(this.source2TNodeUB), Long.valueOf(this.source2SinkUB), Long.valueOf(this.tNode2TSourceUB), Long.valueOf(this.tNode2TNodeUB), Long.valueOf(this.tNode2SinkUB), Long.valueOf(this.tSink2TSourceUB), Long.valueOf(this.tSink2TNodeUB), Long.valueOf(this.tSink2SinkUB)));
        long classBoundsSum = upperBounds.stream().mapToLong(l -> l).sum();
        if (classBoundsSum == 0L) {
            return;
        }
        Distributor<Integer> arcNumDistributor = new Distributor<Integer>(this.rng);
        arcNumDistributor.addUpperBound(classId -> (int)Math.min((Long)upperBounds.get((int)classId), 2000000000L));
        arcNumDistributor.addUpperBound(classId -> {
            double classWeight = (double)((Long)upperBounds.get((int)classId)).longValue() / (double)classBoundsSum;
            int weightBound = (int)(2.0 * classWeight * (double)remainingArcs);
            return weightBound + 1;
        });
        List<Integer> arcNumDistribution = arcNumDistributor.getDistribution(IntStream.range(0, upperBounds.size()).boxed().collect(Collectors.toList()), remainingArcs);
        this.generateArcs(this.getSources(), this.getTransshipSources(), arcNumDistribution.get(0));
        this.generateArcs(this.getSources(), this.getTransshipNodes(), arcNumDistribution.get(1));
        this.generateArcs(this.getSources(), this.getSinks(), arcNumDistribution.get(2));
        this.generateArcs(this.getTransshipNodes(), this.getTransshipSources(), arcNumDistribution.get(3));
        this.generateArcs(this.getTransshipNodes(), this.getTransshipNodes(), arcNumDistribution.get(4));
        this.generateArcs(this.getTransshipNodes(), this.getSinks(), arcNumDistribution.get(5));
        this.generateArcs(this.getTransshipSinks(), this.getTransshipSources(), arcNumDistribution.get(6));
        this.generateArcs(this.getTransshipSinks(), this.getTransshipNodes(), arcNumDistribution.get(7));
        this.generateArcs(this.getTransshipSinks(), this.getSinks(), arcNumDistribution.get(8));
        assert (this.config.getArcNum() - this.graph.edgeSet().size() == 0);
    }

    private void generateArcs(List<Node> tails, List<Node> heads, int arcsToGenerate) {
        HashSet<Node> headsSet = new HashSet<Node>(heads);
        List outDegrees = tails.stream().map(node -> this.getPossibleArcNum((Node)node, (Set<Node>)headsSet)).collect(Collectors.toList());
        long degreeSum = outDegrees.stream().mapToLong(i -> i.intValue()).sum();
        Distributor<Integer> arcNumDistributor = new Distributor<Integer>(this.rng);
        arcNumDistributor.addUpperBound(outDegrees::get);
        arcNumDistributor.addUpperBound(tailId -> {
            double tailWeight = (double)((Integer)outDegrees.get((int)tailId)).intValue() / (double)degreeSum;
            int tailArcWeightBound = (int)(2.0 * tailWeight * (double)arcsToGenerate);
            return tailArcWeightBound + 1;
        });
        List<Integer> arcNumDistribution = arcNumDistributor.getDistribution(IntStream.range(0, tails.size()).boxed().collect(Collectors.toList()), arcsToGenerate);
        for (int i2 = 0; i2 < tails.size(); ++i2) {
            Node tail = tails.get(i2);
            int tailArcNum = arcNumDistribution.get(i2);
            ElementsSequenceGenerator<Node> headGenerator = new ElementsSequenceGenerator<Node>(heads, this.rng);
            while (tailArcNum > 0 && headGenerator.hasNext()) {
                Node currentHead = headGenerator.next();
                if (!this.isValidArc(tail, currentHead)) continue;
                --tailArcNum;
                this.addArc(tail, currentHead);
            }
            assert (tailArcNum == 0);
        }
    }

    private int getPossibleArcNum(Node node, Set<Node> nodes) {
        int possibleArcNum = nodes.size();
        if (nodes.contains(node)) {
            --possibleArcNum;
        }
        for (E arc : this.graph.outgoingEdgesOf(node.graphVertex)) {
            Node arcHead = this.graphVertexMapping.get(Graphs.getOppositeVertex(this.graph, arc, node.graphVertex));
            if (!nodes.contains(arcHead)) continue;
            --possibleArcNum;
        }
        return possibleArcNum;
    }

    public NetworkInfo<V, E> getNetworkInfo() {
        return this.networkInfo;
    }

    private boolean isValidArc(Node tail, Node head) {
        return tail != head && !this.graph.containsEdge(tail.graphVertex, head.graphVertex);
    }

    private void addSkeletonArc(Node chainSource, Node tail, Node head) {
        assert (this.isValidArc(tail, head));
        E arc = this.graph.addEdge(tail.graphVertex, head.graphVertex);
        this.capacityMap.put(arc, Math.max(this.getCapacity(), chainSource.supply));
        this.costMap.put(arc, this.getCost());
        this.registerSkeletonArc(tail, head);
        this.networkInfo.registerChainArc(arc);
        if (head.type == NodeType.TRANSSHIP_NODE) {
            chainSource.chainNodes.add(head);
        }
    }

    private void addArc(Node tail, Node head) {
        assert (this.isValidArc(tail, head));
        E edge = this.graph.addEdge(tail.graphVertex, head.graphVertex);
        this.capacityMap.put(edge, this.getCapacity());
        this.costMap.put(edge, this.getCost());
    }

    private void registerSkeletonArc(Node tail, Node head) {
        block0 : switch (tail.type) {
            case PURE_SOURCE: 
            case TRANSSHIP_SOURCE: {
                switch (head.type) {
                    case TRANSSHIP_NODE: {
                        --this.source2TNodeUB;
                        break block0;
                    }
                    case TRANSSHIP_SINK: 
                    case PURE_SINK: {
                        --this.source2SinkUB;
                        break block0;
                    }
                }
                throw new RuntimeException();
            }
            case TRANSSHIP_NODE: {
                switch (head.type) {
                    case TRANSSHIP_NODE: {
                        --this.tNode2TNodeUB;
                        break block0;
                    }
                    case TRANSSHIP_SINK: 
                    case PURE_SINK: {
                        --this.tNode2SinkUB;
                        break block0;
                    }
                }
                throw new RuntimeException();
            }
            default: {
                throw new RuntimeException();
            }
        }
    }

    private int getCapacity() {
        int percent = this.generateBetween(1, 100);
        if (percent <= this.config.getPercentCapacitated()) {
            return this.generateBetween(this.config.getMinCap(), this.config.getMaxCap());
        }
        return Integer.MAX_VALUE;
    }

    private int getCost() {
        int percent = this.generateBetween(1, 100);
        if (percent <= this.config.getPercentWithInfCost()) {
            return Integer.MAX_VALUE;
        }
        return this.generateBetween(this.config.getMinCost(), this.config.getMaxCost());
    }

    private int generatePositiveRandom(int boundInclusive) {
        return this.rng.nextInt(boundInclusive) + 1;
    }

    private int generateBetween(int startInclusive, int endInclusive) {
        return this.rng.nextInt(endInclusive - startInclusive + 1) + startInclusive;
    }

    private int generateRandom(int endExclusive) {
        return this.rng.nextInt(endExclusive);
    }

    private List<Node> getTransshipSources() {
        return this.nodes.subList(this.config.getPureSourceNum(), this.config.getSourceNum());
    }

    private List<Node> getSources() {
        return this.nodes.subList(0, this.config.getSourceNum());
    }

    private List<Node> getTransshipNodes() {
        return this.nodes.subList(this.config.getSourceNum(), this.config.getSourceNum() + this.config.getTransshipNodeNum());
    }

    private List<Node> getTransshipSinks() {
        return this.nodes.subList(this.config.getSourceNum() + this.config.getTransshipNodeNum(), this.nodes.size() - this.config.getPureSinkNum());
    }

    private List<Node> getSinks() {
        return this.nodes.subList(this.config.getSourceNum() + this.config.getTransshipNodeNum(), this.nodes.size());
    }

    private static enum NodeType {
        PURE_SOURCE{

            @Override
            public String toString() {
                return "Pure source";
            }
        }
        ,
        TRANSSHIP_SOURCE{

            @Override
            public String toString() {
                return "Transship source";
            }
        }
        ,
        TRANSSHIP_NODE{

            @Override
            public String toString() {
                return "Transship node";
            }
        }
        ,
        TRANSSHIP_SINK{

            @Override
            public String toString() {
                return "Transship sink";
            }
        }
        ,
        PURE_SINK{

            @Override
            public String toString() {
                return "Pure sink";
            }
        };


        public abstract String toString();
    }

    private class Node {
        V graphVertex;
        int supply;
        NodeType type;
        List<Node> chainNodes;

        Node(V graphVertex, NodeType type) {
            this.graphVertex = graphVertex;
            this.type = type;
            this.chainNodes = new ArrayList<Node>();
        }

        Node getLastInChain() {
            return this.chainNodes.get(this.chainNodes.size() - 1);
        }

        int getChainLength() {
            return this.chainNodes.size();
        }

        public String toString() {
            return String.format("{%s}: type = %s, supply = %d", new Object[]{this.graphVertex, this.type, this.supply});
        }
    }
}

