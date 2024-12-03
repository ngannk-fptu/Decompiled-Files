/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.TooManyFailuresException;

public class DirectedScaleFreeGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    private final Random rng;
    private final float alpha;
    private final float alphaPlusBeta;
    private final float deltaIn;
    private final float deltaOut;
    private final int targetEdges;
    private final int targetNodes;
    private int maxFailures = 1000;
    private boolean allowingMultipleEdges = true;
    private boolean allowingSelfLoops = true;

    public DirectedScaleFreeGraphGenerator(float alpha, float gamma, float deltaIn, float deltaOut, int targetEdges, int targetNodes) {
        this(alpha, gamma, deltaIn, deltaOut, targetEdges, targetNodes, new Random());
    }

    public DirectedScaleFreeGraphGenerator(float alpha, float gamma, float deltaIn, float deltaOut, int targetEdges, int targetNodes, long seed) {
        this(alpha, gamma, deltaIn, deltaOut, targetEdges, targetNodes, new Random(seed));
    }

    public DirectedScaleFreeGraphGenerator(float alpha, float gamma, float deltaIn, float deltaOut, int targetEdges, int targetNodes, long seed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        this(alpha, gamma, deltaIn, deltaOut, targetEdges, targetNodes, seed);
        this.allowingMultipleEdges = allowingMultipleEdges;
        this.allowingSelfLoops = allowingSelfLoops;
    }

    public DirectedScaleFreeGraphGenerator(float alpha, float gamma, float deltaIn, float deltaOut, int targetEdges, int targetNodes, Random rng) {
        this.alpha = alpha;
        this.alphaPlusBeta = 1.0f - gamma;
        this.deltaIn = deltaIn;
        this.deltaOut = deltaOut;
        this.targetEdges = targetEdges;
        this.targetNodes = targetNodes;
        this.rng = Objects.requireNonNull(rng, "Random number generator cannot be null");
        if (alpha < 0.0f || gamma < 0.0f || alpha + gamma > 1.0f) {
            throw new IllegalArgumentException(String.format("alpha and gamma values of (%f, %f) are invalid", Float.valueOf(alpha), Float.valueOf(gamma)));
        }
        if (deltaIn < 0.0f || deltaOut < 0.0f) {
            throw new IllegalArgumentException(String.format("deltaIn and deltaOut values of (%f, %f) are invalid", Float.valueOf(deltaIn), Float.valueOf(deltaOut)));
        }
        if (targetEdges < 0 && targetNodes < 0) {
            throw new IllegalArgumentException("can not have both targetEdges and targetNodes not set.");
        }
    }

    public DirectedScaleFreeGraphGenerator(float alpha, float gamma, float deltaIn, float deltaOut, int targetEdges, int targetNodes, Random rng, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        this(alpha, gamma, deltaIn, deltaOut, targetEdges, targetNodes, rng);
        this.allowingMultipleEdges = allowingMultipleEdges;
        this.allowingSelfLoops = allowingSelfLoops;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        if (this.allowingMultipleEdges && !target.getType().isAllowingMultipleEdges()) {
            throw new IllegalArgumentException("Generator allows Multiple Edges while graph does not. Consider changing this generator parameters or the target graph type.");
        }
        if (this.allowingSelfLoops && !target.getType().isAllowingSelfLoops()) {
            throw new IllegalArgumentException("Generator allows Self loops while graph does not. Consider changing this generator parameters or the target graph type.");
        }
        HashSet<V> newNodesSet = new HashSet<V>();
        HashSet<E> newEdgesSet = new HashSet<E>();
        if (this.targetEdges == 0 || this.targetEdges < 0 && this.targetNodes == 0) {
            return;
        }
        V initV = target.addVertex();
        newNodesSet.add(initV);
        int failuresCounter = 0;
        while (this.targetEdges >= 0 ? this.targetEdges > newEdgesSet.size() : this.targetNodes >= newNodesSet.size()) {
            if (failuresCounter >= this.maxFailures) {
                throw new TooManyFailuresException(failuresCounter + " consecutive failures is more than maximum allowed number (" + this.maxFailures + ").");
            }
            Object v = null;
            Object w = null;
            boolean newV = false;
            boolean newW = false;
            float tributaries = this.rng.nextFloat();
            if (tributaries <= this.alpha) {
                if (this.targetEdges < 0 && newNodesSet.size() == this.targetNodes) break;
                newV = true;
                w = this.pickAVertex(target, newNodesSet, newEdgesSet, Direction.IN, this.deltaIn);
            } else if (tributaries <= this.alphaPlusBeta) {
                v = this.pickAVertex(target, newNodesSet, newEdgesSet, Direction.OUT, this.deltaOut);
                w = this.pickAVertex(target, newNodesSet, newEdgesSet, Direction.IN, this.deltaIn);
            } else {
                if (this.targetEdges < 0 && newNodesSet.size() == this.targetNodes) break;
                v = this.pickAVertex(target, newNodesSet, newEdgesSet, Direction.OUT, this.deltaOut);
                newW = true;
            }
            if (newV && w == null || newW && v == null) {
                ++failuresCounter;
                continue;
            }
            if (!this.allowingSelfLoops && v == w) {
                ++failuresCounter;
                continue;
            }
            if (!this.allowingMultipleEdges && target.containsEdge(v, w)) {
                ++failuresCounter;
                continue;
            }
            if (newV) {
                v = target.addVertex();
            }
            if (newW) {
                w = target.addVertex();
            }
            E e = target.addEdge(v, w);
            failuresCounter = 0;
            newNodesSet.add(v);
            newNodesSet.add(w);
            newEdgesSet.add(e);
        }
    }

    private V pickAVertex(Graph<V, E> target, Set<V> allNewNodes, Set<E> allNewEdgesSet, Direction direction, float bias) {
        V ret;
        float numerator;
        int allNewNodesSize = allNewNodes.size();
        if (allNewNodesSize == 0) {
            return null;
        }
        if (allNewNodesSize == 1) {
            return allNewNodes.iterator().next();
        }
        float indicatorAccumulator = 0.0f;
        float denominator = (float)allNewEdgesSet.size() + (float)allNewNodesSize * bias;
        float r = this.rng.nextFloat();
        r *= denominator;
        Iterator<V> verticesIterator = allNewNodes.iterator();
        do {
            ret = verticesIterator.next();
            numerator = direction == Direction.IN ? (float)target.inDegreeOf(ret) + bias : (float)target.outDegreeOf(ret) + bias;
        } while (verticesIterator.hasNext() && (indicatorAccumulator += numerator) < r);
        return ret;
    }

    public int getMaxFailures() {
        return this.maxFailures;
    }

    public void setMaxFailures(int maxFailures) {
        if (maxFailures < 0) {
            throw new IllegalArgumentException("value must be non negative");
        }
        this.maxFailures = maxFailures;
    }

    public boolean isAllowingMultipleEdges() {
        return this.allowingMultipleEdges;
    }

    public void setAllowingMultipleEdges(boolean allowingMultipleEdges) {
        this.allowingMultipleEdges = allowingMultipleEdges;
    }

    public boolean isAllowingSelfLoops() {
        return this.allowingSelfLoops;
    }

    public void setAllowingSelfLoops(boolean allowingSelfLoops) {
        this.allowingSelfLoops = allowingSelfLoops;
    }

    private static enum Direction {
        IN,
        OUT;

    }
}

