/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.SimpleGraph;

public class BergeGraphInspector<V, E> {
    private GraphPath<V, E> certificate = null;
    private boolean certify = false;

    private List<V> intersectGraphPaths(GraphPath<V, E> p1, GraphPath<V, E> p2) {
        LinkedList<V> res = new LinkedList<V>();
        res.addAll(p1.getVertexList());
        res.retainAll(p2.getVertexList());
        return res;
    }

    private GraphPath<V, E> p(Graph<V, E> g, GraphPath<V, E> pathS, GraphPath<V, E> pathT, V m, V b1, V b2, V b3, V s1, V s2, V s3) {
        if (s1 == b1) {
            if (b1 == m) {
                LinkedList edgeList = new LinkedList();
                return new GraphWalk<V, E>(g, s1, b1, edgeList, 0.0);
            }
            return null;
        }
        if (b1 == m) {
            return null;
        }
        if (g.containsEdge(m, b2) || g.containsEdge(m, b3) || g.containsEdge(m, s2) || g.containsEdge(m, s3) || pathS == null || pathT == null) {
            return null;
        }
        if (pathS.getVertexList().stream().anyMatch(t -> g.containsEdge(t, b2) || g.containsEdge(t, b3) || g.containsEdge(t, s2) || g.containsEdge(t, s3)) || pathT.getVertexList().stream().anyMatch(t -> t != b1 && (g.containsEdge(t, b2) || g.containsEdge(t, b3) || g.containsEdge(t, s2) || g.containsEdge(t, s3)))) {
            return null;
        }
        List<V> intersection = this.intersectGraphPaths(pathS, pathT);
        if (intersection.size() != 1 || !intersection.contains(m)) {
            return null;
        }
        if (pathS.getVertexList().stream().anyMatch(s -> s != m && pathT.getVertexList().stream().anyMatch(t -> t != m && g.containsEdge(s, t)))) {
            return null;
        }
        LinkedList<E> edgeList = new LinkedList<E>();
        edgeList.addAll(pathT.getEdgeList());
        edgeList.addAll(pathS.getEdgeList());
        double weight = edgeList.stream().mapToDouble(g::getEdgeWeight).sum();
        return new GraphWalk<V, E>(g, b1, s1, edgeList, weight);
    }

    private void bfOddHoleCertificate(Graph<V, E> g) {
        for (V start : g.vertexSet()) {
            if (g.degreeOf(start) < 2) continue;
            HashSet<V> set = new HashSet<V>();
            set.addAll(g.vertexSet());
            for (V neighborOfStart : g.vertexSet()) {
                if (neighborOfStart == start || !g.containsEdge(start, neighborOfStart) || g.degreeOf(neighborOfStart) != 2) continue;
                set.remove(neighborOfStart);
                AsSubgraph<V, E> subg = new AsSubgraph<V, E>(g, set);
                for (V neighborsNeighbor : g.vertexSet()) {
                    GraphPath<V, E> path;
                    if (neighborsNeighbor == start || neighborsNeighbor == neighborOfStart || !g.containsEdge(neighborsNeighbor, neighborOfStart) || g.containsEdge(neighborsNeighbor, start) || g.degreeOf(neighborsNeighbor) < 2 || (path = new DijkstraShortestPath<V, E>(subg).getPath(start, neighborsNeighbor)) == null || path.getLength() < 3 || path.getLength() % 2 == 0) continue;
                    LinkedList<E> edgeList = new LinkedList<E>();
                    edgeList.addAll(path.getEdgeList());
                    edgeList.add(g.getEdge(neighborsNeighbor, neighborOfStart));
                    edgeList.add(g.getEdge(neighborOfStart, start));
                    double weight = edgeList.stream().mapToDouble(g::getEdgeWeight).sum();
                    this.certificate = new GraphWalk<V, E>(g, start, start, edgeList, weight);
                    break;
                }
                if (this.certificate == null) continue;
                break;
            }
            if (this.certificate == null) continue;
            break;
        }
    }

    boolean containsPyramid(Graph<V, E> g) {
        HashSet visitedTriangles = new HashSet();
        for (Object b1 : g.vertexSet()) {
            for (Object b2 : g.vertexSet()) {
                if (b1 == b2 || !g.containsEdge(b1, b2)) continue;
                for (Object b3 : g.vertexSet()) {
                    if (b3 == b1 || b3 == b2 || !g.containsEdge(b2, b3) || !g.containsEdge(b1, b3)) continue;
                    HashSet<V> triangles = new HashSet<V>();
                    triangles.add(b1);
                    triangles.add(b2);
                    triangles.add(b3);
                    if (visitedTriangles.contains(triangles)) continue;
                    visitedTriangles.add(triangles);
                    for (V aCandidate : g.vertexSet()) {
                        if (aCandidate == b1 || aCandidate == b2 || aCandidate == b3 || g.containsEdge(aCandidate, b1) && g.containsEdge(aCandidate, b2) || g.containsEdge(aCandidate, b2) && g.containsEdge(aCandidate, b3) || g.containsEdge(aCandidate, b1) && g.containsEdge(aCandidate, b3)) continue;
                        for (Object s1 : g.vertexSet()) {
                            if (s1 == aCandidate || !g.containsEdge(s1, aCandidate) || s1 == b2 || s1 == b3 || s1 != b1 && (g.containsEdge(s1, b2) || g.containsEdge(s1, b3))) continue;
                            for (Object s2 : g.vertexSet()) {
                                if (s2 == aCandidate || !g.containsEdge(s2, aCandidate) || g.containsEdge(s1, s2) || s1 == s2 || s2 == b1 || s2 == b3 || s2 != b2 && (g.containsEdge(s2, b1) || g.containsEdge(s2, b3))) continue;
                                for (Object s3 : g.vertexSet()) {
                                    AsSubgraph<V, E> subg;
                                    HashSet<Object> validInterior;
                                    if (s3 == aCandidate || !g.containsEdge(s3, aCandidate) || g.containsEdge(s3, s2) || s1 == s3 || s3 == s2 || g.containsEdge(s1, s3) || s3 == b1 || s3 == b2 || s3 != b3 && (g.containsEdge(s3, b1) || g.containsEdge(s3, b2))) continue;
                                    HashSet<V> setM = new HashSet<V>();
                                    setM.addAll(g.vertexSet());
                                    setM.remove(b1);
                                    setM.remove(b2);
                                    setM.remove(b3);
                                    setM.remove(s1);
                                    setM.remove(s2);
                                    setM.remove(s3);
                                    HashMap mapS1 = new HashMap();
                                    HashMap mapS2 = new HashMap();
                                    HashMap mapS3 = new HashMap();
                                    HashMap mapT1 = new HashMap();
                                    HashMap mapT2 = new HashMap();
                                    HashMap mapT3 = new HashMap();
                                    for (Object m1 : setM) {
                                        validInterior = new HashSet<Object>();
                                        validInterior.addAll(setM);
                                        validInterior.removeIf(i -> g.containsEdge(i, b2) || g.containsEdge(i, s2) || g.containsEdge(i, b3) || g.containsEdge(i, s3));
                                        validInterior.add(m1);
                                        validInterior.add(s1);
                                        subg = new AsSubgraph<V, E>(g, validInterior);
                                        mapS1.put(m1, new DijkstraShortestPath(subg).getPath(m1, s1));
                                        validInterior.remove(s1);
                                        validInterior.add(b1);
                                        subg = new AsSubgraph<V, E>(g, validInterior);
                                        mapT1.put(m1, new DijkstraShortestPath<V, E>(subg).getPath(b1, m1));
                                    }
                                    for (Object m2 : setM) {
                                        validInterior = new HashSet();
                                        validInterior.addAll(setM);
                                        validInterior.removeIf(i -> g.containsEdge(i, b1) || g.containsEdge(i, s1) || g.containsEdge(i, b3) || g.containsEdge(i, s3));
                                        validInterior.add(m2);
                                        validInterior.add(s2);
                                        subg = new AsSubgraph<V, E>(g, validInterior);
                                        mapS2.put(m2, new DijkstraShortestPath(subg).getPath(m2, s2));
                                        validInterior.remove(s2);
                                        validInterior.add(b2);
                                        subg = new AsSubgraph<V, E>(g, validInterior);
                                        mapT2.put(m2, new DijkstraShortestPath<V, E>(subg).getPath(b2, m2));
                                    }
                                    for (Object m3 : setM) {
                                        validInterior = new HashSet();
                                        validInterior.addAll(setM);
                                        validInterior.removeIf(i -> g.containsEdge(i, b1) || g.containsEdge(i, s1) || g.containsEdge(i, b2) || g.containsEdge(i, s2));
                                        validInterior.add(m3);
                                        validInterior.add(s3);
                                        subg = new AsSubgraph<V, E>(g, validInterior);
                                        mapS3.put(m3, new DijkstraShortestPath(subg).getPath(m3, s3));
                                        validInterior.remove(s3);
                                        validInterior.add(b3);
                                        subg = new AsSubgraph<V, E>(g, validInterior, null);
                                        mapT3.put(m3, new DijkstraShortestPath<V, E>(subg).getPath(b3, m3));
                                    }
                                    HashSet<V> setM1 = new HashSet<V>();
                                    setM1.addAll(setM);
                                    setM1.add(b1);
                                    for (Object m1 : setM1) {
                                        GraphPath<V, E> pathP1 = this.p(g, (GraphPath)mapS1.get(m1), (GraphPath)mapT1.get(m1), m1, b1, b2, b3, s1, s2, s3);
                                        if (pathP1 == null) continue;
                                        HashSet<V> setM2 = new HashSet<V>();
                                        setM2.addAll(setM);
                                        setM2.add(b2);
                                        for (Object m2 : setM) {
                                            GraphPath<V, E> pathP2 = this.p(g, (GraphPath)mapS2.get(m2), (GraphPath)mapT2.get(m2), m2, b2, b1, b3, s2, s1, s3);
                                            if (pathP2 == null) continue;
                                            HashSet<V> setM3 = new HashSet<V>();
                                            setM3.addAll(setM);
                                            setM3.add(b3);
                                            for (Object m3 : setM3) {
                                                GraphPath<V, E> pathP3 = this.p(g, (GraphPath)mapS3.get(m3), (GraphPath)mapT3.get(m3), m3, b3, b1, b2, s3, s1, s2);
                                                if (pathP3 == null) continue;
                                                if (this.certify) {
                                                    if ((pathP1.getLength() + pathP2.getLength()) % 2 == 0) {
                                                        HashSet<V> set = new HashSet<V>();
                                                        set.addAll(pathP1.getVertexList());
                                                        set.addAll(pathP2.getVertexList());
                                                        set.add(aCandidate);
                                                        this.bfOddHoleCertificate(new AsSubgraph<V, E>(g, set));
                                                    } else if ((pathP1.getLength() + pathP3.getLength()) % 2 == 0) {
                                                        HashSet<V> set = new HashSet<V>();
                                                        set.addAll(pathP1.getVertexList());
                                                        set.addAll(pathP3.getVertexList());
                                                        set.add(aCandidate);
                                                        this.bfOddHoleCertificate(new AsSubgraph<V, E>(g, set));
                                                    } else {
                                                        HashSet<V> set = new HashSet<V>();
                                                        set.addAll(pathP3.getVertexList());
                                                        set.addAll(pathP2.getVertexList());
                                                        set.add(aCandidate);
                                                        this.bfOddHoleCertificate(new AsSubgraph<V, E>(g, set));
                                                    }
                                                }
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private List<Set<V>> findAllComponents(Graph<V, E> g, Set<V> f) {
        return new ConnectivityInspector<V, E>(new AsSubgraph<V, E>(g, f)).connectedSets();
    }

    boolean containsJewel(Graph<V, E> g) {
        for (V v2 : g.vertexSet()) {
            for (V v3 : g.vertexSet()) {
                if (v2 == v3 || !g.containsEdge(v2, v3)) continue;
                for (V v5 : g.vertexSet()) {
                    if (v2 == v5 || v3 == v5) continue;
                    HashSet<V> setF = new HashSet<V>();
                    for (V f : g.vertexSet()) {
                        if (f == v2 || f == v3 || f == v5 || g.containsEdge(f, v2) || g.containsEdge(f, v3) || g.containsEdge(f, v5)) continue;
                        setF.add(f);
                    }
                    List<Set<V>> componentsOfF = this.findAllComponents(g, setF);
                    HashSet<V> setX1 = new HashSet<V>();
                    for (V x1 : g.vertexSet()) {
                        if (x1 == v2 || x1 == v3 || x1 == v5 || !g.containsEdge(x1, v2) || !g.containsEdge(x1, v5) || g.containsEdge(x1, v3)) continue;
                        setX1.add(x1);
                    }
                    HashSet<V> setX2 = new HashSet<V>();
                    for (V x2 : g.vertexSet()) {
                        if (x2 == v2 || x2 == v3 || x2 == v5 || g.containsEdge(x2, v2) || !g.containsEdge(x2, v5) || !g.containsEdge(x2, v3)) continue;
                        setX2.add(x2);
                    }
                    for (Object v1 : setX1) {
                        if (g.containsEdge(v1, v3)) continue;
                        for (Object v4 : setX2) {
                            if (v1 == v4 || g.containsEdge(v1, v4) || g.containsEdge(v2, v4)) continue;
                            for (Set<V> fPrime : componentsOfF) {
                                if (!this.hasANeighbour(g, fPrime, v1) || !this.hasANeighbour(g, fPrime, v4)) continue;
                                if (this.certify) {
                                    HashSet<Object> validSet = new HashSet<Object>();
                                    validSet.addAll(fPrime);
                                    validSet.add(v1);
                                    validSet.add(v4);
                                    GraphPath<Object, E> p = new DijkstraShortestPath<Object, E>(new AsSubgraph<V, E>(g, validSet)).getPath(v1, v4);
                                    LinkedList<E> edgeList = new LinkedList<E>();
                                    edgeList.addAll(p.getEdgeList());
                                    if (p.getLength() % 2 == 1) {
                                        edgeList.add(g.getEdge(v4, v5));
                                        edgeList.add(g.getEdge(v5, v1));
                                    } else {
                                        edgeList.add(g.getEdge(v4, v3));
                                        edgeList.add(g.getEdge(v3, v2));
                                        edgeList.add(g.getEdge(v2, v1));
                                    }
                                    double weight = edgeList.stream().mapToDouble(g::getEdgeWeight).sum();
                                    this.certificate = new GraphWalk<Object, E>(g, v1, v1, edgeList, weight);
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    boolean containsCleanShortestOddHole(Graph<V, E> g) {
        for (V u : g.vertexSet()) {
            for (V v : g.vertexSet()) {
                GraphPath<V, E> puv;
                if (u == v || g.containsEdge(u, v) || (puv = new DijkstraShortestPath<V, E>(g).getPath(u, v)) == null) continue;
                for (V w : g.vertexSet()) {
                    GraphPath<V, E> pwu;
                    GraphPath<V, E> pvw;
                    if (w == u || w == v || g.containsEdge(w, u) || g.containsEdge(w, v) || (pvw = new DijkstraShortestPath<V, E>(g).getPath(v, w)) == null || (pwu = new DijkstraShortestPath<V, E>(g).getPath(w, u)) == null) continue;
                    HashSet<V> set = new HashSet<V>();
                    set.addAll(puv.getVertexList());
                    set.addAll(pvw.getVertexList());
                    set.addAll(pwu.getVertexList());
                    AsSubgraph subg = new AsSubgraph(g, set);
                    if (set.size() < 7 || subg.vertexSet().size() != set.size() || subg.edgeSet().size() != subg.vertexSet().size() || subg.vertexSet().size() % 2 == 0 || subg.vertexSet().stream().anyMatch(t -> subg.degreeOf(t) != 2)) continue;
                    if (this.certify) {
                        LinkedList<E> edgeList = new LinkedList<E>();
                        edgeList.addAll(puv.getEdgeList());
                        edgeList.addAll(pvw.getEdgeList());
                        edgeList.addAll(pwu.getEdgeList());
                        double weight = edgeList.stream().mapToDouble(g::getEdgeWeight).sum();
                        this.certificate = new GraphWalk<V, E>(g, u, u, edgeList, weight);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private GraphPath<V, E> getPathAvoidingX(Graph<V, E> g, V start, V end, Set<V> x) {
        HashSet<V> vertexSet = new HashSet<V>();
        vertexSet.addAll(g.vertexSet());
        vertexSet.removeAll(x);
        vertexSet.add(start);
        vertexSet.add(end);
        AsSubgraph<V, E> subg = new AsSubgraph<V, E>(g, vertexSet, null);
        return new DijkstraShortestPath<V, E>(subg).getPath(start, end);
    }

    private boolean containsShortestOddHole(Graph<V, E> g, Set<V> x) {
        for (V y1 : g.vertexSet()) {
            if (x.contains(y1)) continue;
            for (V x1 : g.vertexSet()) {
                if (x1 == y1) continue;
                GraphPath<V, E> rx1y1 = this.getPathAvoidingX(g, x1, y1, x);
                for (V x3 : g.vertexSet()) {
                    if (x3 == x1 || x3 == y1 || !g.containsEdge(x1, x3)) continue;
                    for (V x2 : g.vertexSet()) {
                        if (x2 == x3 || x2 == x1 || x2 == y1 || g.containsEdge(x2, x1) || !g.containsEdge(x3, x2)) continue;
                        GraphPath<V, E> rx2y1 = this.getPathAvoidingX(g, x2, y1, x);
                        if (rx1y1 == null || rx2y1 == null) continue;
                        V y2 = null;
                        for (V y2Candidate : rx2y1.getVertexList()) {
                            if (!g.containsEdge(y1, y2Candidate) || y2Candidate == x1 || y2Candidate == x2 || y2Candidate == x3 || y2Candidate == y1) continue;
                            y2 = y2Candidate;
                            break;
                        }
                        if (y2 == null) continue;
                        GraphPath<V, E> rx3y1 = this.getPathAvoidingX(g, x3, y1, x);
                        GraphPath<Object, E> rx3y2 = this.getPathAvoidingX(g, x3, y2, x);
                        GraphPath<Object, E> rx1y2 = this.getPathAvoidingX(g, x1, y2, x);
                        if (rx3y1 == null || rx3y2 == null || rx1y2 == null) continue;
                        double n = rx1y1.getLength() + 1;
                        if ((double)rx2y1.getLength() != n || n != (double)rx1y2.getLength() || !((double)rx3y1.getLength() >= n) || !((double)rx3y2.getLength() >= n)) continue;
                        if (this.certify) {
                            LinkedList<E> edgeList = new LinkedList<E>();
                            edgeList.addAll(rx1y1.getEdgeList());
                            for (int i = rx2y1.getLength() - 1; i >= 0; --i) {
                                edgeList.add(rx2y1.getEdgeList().get(i));
                            }
                            edgeList.add(g.getEdge(x2, x3));
                            edgeList.add(g.getEdge(x3, x1));
                            double weight = edgeList.stream().mapToDouble(g::getEdgeWeight).sum();
                            this.certificate = new GraphWalk<V, E>(g, x1, x1, edgeList, weight);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean routine1(Graph<V, E> g, Set<V> x) {
        return this.containsCleanShortestOddHole(g) || this.containsShortestOddHole(g, x);
    }

    private boolean hasConfigurationType1(Graph<V, E> g) {
        for (V v1 : g.vertexSet()) {
            Set<V> temp = new ConnectivityInspector<V, E>(g).connectedSetOf(v1);
            for (V v2 : temp) {
                if (v1 == v2 || !g.containsEdge(v1, v2)) continue;
                for (V v3 : temp) {
                    if (v3 == v1 || v3 == v2 || !g.containsEdge(v2, v3) || g.containsEdge(v1, v3)) continue;
                    for (V v4 : temp) {
                        if (v4 == v1 || v4 == v2 || v4 == v3 || g.containsEdge(v1, v4) || g.containsEdge(v2, v4) || !g.containsEdge(v3, v4)) continue;
                        for (V v5 : temp) {
                            if (v5 == v1 || v5 == v2 || v5 == v3 || v5 == v4 || g.containsEdge(v2, v5) || g.containsEdge(v3, v5) || !g.containsEdge(v1, v5) || !g.containsEdge(v4, v5)) continue;
                            if (this.certify) {
                                LinkedList<E> edgeList = new LinkedList<E>();
                                edgeList.add(g.getEdge(v1, v2));
                                edgeList.add(g.getEdge(v2, v3));
                                edgeList.add(g.getEdge(v3, v4));
                                edgeList.add(g.getEdge(v4, v5));
                                edgeList.add(g.getEdge(v5, v1));
                                double weight = edgeList.stream().mapToDouble(g::getEdgeWeight).sum();
                                this.certificate = new GraphWalk<V, E>(g, v1, v1, edgeList, weight);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    boolean isYXComplete(Graph<V, E> g, V y, Set<V> x) {
        return x.stream().allMatch(t -> g.containsEdge(t, y));
    }

    private List<Set<V>> findAllAnticomponentsOfY(Graph<V, E> g, Set<V> y) {
        AbstractBaseGraph target = g.getType().isSimple() ? new SimpleGraph<V, E>(g.getVertexSupplier(), g.getEdgeSupplier(), g.getType().isWeighted()) : new Multigraph<V, E>(g.getVertexSupplier(), g.getEdgeSupplier(), g.getType().isWeighted());
        new ComplementGraphGenerator<V, E>(g).generateGraph(target);
        return this.findAllComponents(target, y);
    }

    boolean hasConfigurationType2(Graph<V, E> g) {
        for (V v1 : g.vertexSet()) {
            for (V v2 : g.vertexSet()) {
                if (v1 == v2 || !g.containsEdge(v1, v2)) continue;
                for (V v3 : g.vertexSet()) {
                    if (v3 == v2 || v1 == v3 || g.containsEdge(v1, v3) || !g.containsEdge(v2, v3)) continue;
                    for (V v4 : g.vertexSet()) {
                        if (v4 == v1 || v4 == v2 || v4 == v3 || g.containsEdge(v4, v2) || g.containsEdge(v4, v1) || !g.containsEdge(v3, v4)) continue;
                        HashSet<V> temp = new HashSet<V>();
                        temp.add(v1);
                        temp.add(v2);
                        temp.add(v4);
                        HashSet<V> setY = new HashSet<V>();
                        for (V y : g.vertexSet()) {
                            if (!this.isYXComplete(g, y, temp)) continue;
                            setY.add(y);
                        }
                        List<Set<V>> anticomponentsOfY = this.findAllAnticomponentsOfY(g, setY);
                        for (Set<V> setX : anticomponentsOfY) {
                            List<V> listP;
                            GraphPath<V, E> path;
                            HashSet<V> v2v3 = new HashSet<V>();
                            v2v3.addAll(g.vertexSet());
                            v2v3.remove(v2);
                            v2v3.remove(v3);
                            v2v3.removeAll(setX);
                            if (!v2v3.contains(v1) || !v2v3.contains(v4) || (path = new DijkstraShortestPath<V, E>(new AsSubgraph<V, E>(g, v2v3)).getPath(v1, v4)) == null || !(listP = path.getVertexList()).contains(v1) || !listP.contains(v4)) continue;
                            boolean cont = true;
                            for (V p : listP) {
                                if (p == v1 || p == v4 || !g.containsEdge(p, v2) && !g.containsEdge(p, v3) && !this.isYXComplete(g, p, setX)) continue;
                                cont = false;
                                break;
                            }
                            if (!cont) continue;
                            if (this.certify) {
                                LinkedList<E> edgeList = new LinkedList<E>();
                                if (path.getLength() % 2 == 0) {
                                    edgeList.add(g.getEdge(v1, v2));
                                    edgeList.add(g.getEdge(v2, v3));
                                    edgeList.add(g.getEdge(v3, v4));
                                    edgeList.addAll(path.getEdgeList());
                                } else {
                                    edgeList.addAll(path.getEdgeList());
                                    V x = setX.iterator().next();
                                    edgeList.add(g.getEdge(v4, x));
                                    edgeList.add(g.getEdge(x, v1));
                                }
                                double weight = edgeList.stream().mapToDouble(g::getEdgeWeight).sum();
                                this.certificate = new GraphWalk<V, E>(g, v1, v1, edgeList, weight);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasANeighbour(Graph<V, E> g, Set<V> set, V v) {
        return set.stream().anyMatch(s -> g.containsEdge(s, v));
    }

    private Set<V> findMaximalConnectedSubset(Graph<V, E> g, Set<V> setX, V v1, V v2, V v5) {
        Set<V> fPrime = new ConnectivityInspector<V, E>(g).connectedSetOf(v5);
        fPrime.removeIf(t -> t != v5 && this.isYXComplete(g, t, setX) || v1 == t || v2 == t || g.containsEdge(v1, t) || g.containsEdge(v2, t));
        return fPrime;
    }

    private boolean hasANonneighbourInX(Graph<V, E> g, V v, Set<V> setX) {
        return setX.stream().anyMatch(x -> !g.containsEdge(v, x));
    }

    boolean hasConfigurationType3(Graph<V, E> g) {
        for (V v1 : g.vertexSet()) {
            for (V v2 : g.vertexSet()) {
                if (v1 == v2 || !g.containsEdge(v1, v2)) continue;
                for (V v5 : g.vertexSet()) {
                    if (v1 == v5 || v2 == v5 || g.containsEdge(v1, v5) || g.containsEdge(v2, v5)) continue;
                    HashSet<V> triple = new HashSet<V>();
                    triple.add(v1);
                    triple.add(v2);
                    triple.add(v5);
                    HashSet<V> setY = new HashSet<V>();
                    for (V y : g.vertexSet()) {
                        if (!this.isYXComplete(g, y, triple)) continue;
                        setY.add(y);
                    }
                    List<Set<V>> anticomponents = this.findAllAnticomponentsOfY(g, setY);
                    for (Set<V> setX : anticomponents) {
                        Set<V> fPrime = this.findMaximalConnectedSubset(g, setX, v1, v2, v5);
                        HashSet<V> setF = new HashSet<V>();
                        setF.addAll(fPrime);
                        for (V x : setX) {
                            if (g.containsEdge(x, v1) || g.containsEdge(x, v2) || g.containsEdge(x, v5) || !this.hasANeighbour(g, fPrime, x)) continue;
                            setF.add(x);
                        }
                        for (V v4 : g.vertexSet()) {
                            if (v4 == v1 || v4 == v2 || v4 == v5 || g.containsEdge(v2, v4) || g.containsEdge(v5, v4) || !g.containsEdge(v1, v4) || !this.hasANeighbour(g, setF, v4) || !this.hasANonneighbourInX(g, v4, setX) || this.isYXComplete(g, v4, setX)) continue;
                            for (V v3 : g.vertexSet()) {
                                if (v3 == v1 || v3 == v2 || v3 == v4 || v3 == v5 || !g.containsEdge(v2, v3) || !g.containsEdge(v3, v4) || !g.containsEdge(v5, v3) || g.containsEdge(v1, v3) || !this.hasANonneighbourInX(g, v3, setX) || this.isYXComplete(g, v3, setX)) continue;
                                for (Object v6 : setF) {
                                    if (v6 == v1 || v6 == v2 || v6 == v3 || v6 == v4 || v6 == v5 || !g.containsEdge(v4, v6) || g.containsEdge(v1, v6) || g.containsEdge(v2, v6) || g.containsEdge(v5, v6) && !this.isYXComplete(g, v6, setX)) continue;
                                    HashSet<Object> verticesForPv5v6 = new HashSet<Object>();
                                    verticesForPv5v6.addAll(fPrime);
                                    verticesForPv5v6.add(v5);
                                    verticesForPv5v6.add(v6);
                                    verticesForPv5v6.remove(v1);
                                    verticesForPv5v6.remove(v2);
                                    verticesForPv5v6.remove(v3);
                                    verticesForPv5v6.remove(v4);
                                    if (!new ConnectivityInspector(new AsSubgraph<V, E>(g, verticesForPv5v6)).pathExists(v6, v5)) continue;
                                    if (this.certify) {
                                        LinkedList<E> edgeList = new LinkedList<E>();
                                        edgeList.add(g.getEdge(v1, v4));
                                        edgeList.add(g.getEdge(v4, v6));
                                        GraphPath<V, E> path = new DijkstraShortestPath(g).getPath(v6, v5);
                                        edgeList.addAll(path.getEdgeList());
                                        if (path.getLength() % 2 == 1) {
                                            V x = setX.iterator().next();
                                            edgeList.add(g.getEdge(v5, x));
                                            edgeList.add(g.getEdge(x, v1));
                                        } else {
                                            edgeList.add(g.getEdge(v5, v3));
                                            edgeList.add(g.getEdge(v3, v4));
                                            edgeList.add(g.getEdge(v4, v1));
                                        }
                                        double weight = edgeList.stream().mapToDouble(g::getEdgeWeight).sum();
                                        this.certificate = new GraphWalk<V, E>(g, v1, v1, edgeList, weight);
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean routine2(Graph<V, E> g) {
        return this.containsPyramid(g) || this.containsJewel(g) || this.hasConfigurationType1(g) || this.hasConfigurationType2(g) || this.hasConfigurationType3(g);
    }

    private Set<V> n(Graph<V, E> g, V a, V b) {
        return g.vertexSet().stream().filter(t -> g.containsEdge(t, a) && g.containsEdge(t, b)).collect(Collectors.toSet());
    }

    private int r(Graph<V, E> g, Set<V> nAB, V c) {
        if (this.isYXComplete(g, c, nAB)) {
            return 0;
        }
        List<Set<V>> anticomponents = this.findAllAnticomponentsOfY(g, nAB);
        return anticomponents.stream().mapToInt(Set::size).max().getAsInt();
    }

    private Set<V> y(Graph<V, E> g, Set<V> nAB, V c) {
        int cutoff = this.r(g, nAB, c);
        List<Set<V>> anticomponents = this.findAllAnticomponentsOfY(g, nAB);
        HashSet<V> res = new HashSet<V>();
        for (Set<V> anticomponent : anticomponents) {
            if (anticomponent.size() <= cutoff) continue;
            res.addAll(anticomponent);
        }
        return res;
    }

    private Set<V> w(Graph<V, E> g, Set<V> nAB, V c) {
        HashSet<V> temp = new HashSet<V>();
        temp.addAll(nAB);
        temp.add(c);
        List<Set<V>> anticomponents = this.findAllAnticomponentsOfY(g, temp);
        for (Set<V> anticomponent : anticomponents) {
            if (!anticomponent.contains(c)) continue;
            return anticomponent;
        }
        return null;
    }

    private Set<V> z(Graph<V, E> g, Set<V> nAB, V c) {
        HashSet<V> temp = new HashSet<V>();
        temp.addAll(this.y(g, nAB, c));
        temp.addAll(this.w(g, nAB, c));
        HashSet<V> res = new HashSet<V>();
        for (V it : g.vertexSet()) {
            if (!this.isYXComplete(g, it, temp)) continue;
            res.add(it);
        }
        return res;
    }

    private Set<V> x(Graph<V, E> g, Set<V> nAB, V c) {
        HashSet<V> res = new HashSet<V>();
        res.addAll(this.y(g, nAB, c));
        res.addAll(this.z(g, nAB, c));
        return res;
    }

    private boolean isTripleRelevant(Graph<V, E> g, V a, V b, V c) {
        return a != b && !g.containsEdge(a, b) && !this.n(g, a, b).contains(c);
    }

    Set<Set<V>> routine3(Graph<V, E> g) {
        HashSet<Set<Object>> nUVList = new HashSet<Set<Object>>();
        for (Iterator<Object> u : g.vertexSet()) {
            for (Object object : g.vertexSet()) {
                if (u == object || !g.containsEdge(u, object)) continue;
                nUVList.add(this.n(g, u, object));
            }
        }
        HashSet<Set<Object>> tripleList = new HashSet<Set<Object>>();
        for (Object a : g.vertexSet()) {
            for (Object b : g.vertexSet()) {
                if (a == b || g.containsEdge(a, b)) continue;
                Set<Object> set = this.n(g, a, b);
                for (Object c : g.vertexSet()) {
                    if (!this.isTripleRelevant(g, a, b, c)) continue;
                    tripleList.add(this.x(g, set, c));
                }
            }
        }
        HashSet<Set<V>> res = new HashSet<Set<V>>();
        for (Set set : nUVList) {
            for (Set set2 : tripleList) {
                HashSet temp = new HashSet();
                temp.addAll(set);
                temp.addAll(set2);
                res.add(temp);
            }
        }
        return res;
    }

    public boolean isBerge(Graph<V, E> g, boolean computeCertificate) {
        GraphTests.requireDirectedOrUndirected(g);
        AbstractBaseGraph complementGraph = g.getType().isSimple() ? new SimpleGraph<V, E>(g.getVertexSupplier(), g.getEdgeSupplier(), g.getType().isWeighted()) : new Multigraph<V, E>(g.getVertexSupplier(), g.getEdgeSupplier(), g.getType().isWeighted());
        new ComplementGraphGenerator<V, E>(g).generateGraph(complementGraph);
        this.certify = computeCertificate;
        if (this.routine2(g) || this.routine2(complementGraph)) {
            this.certify = false;
            return false;
        }
        for (Set<V> it : this.routine3(g)) {
            if (!this.routine1(g, it)) continue;
            this.certify = false;
            return false;
        }
        for (Set<V> it : this.routine3(complementGraph)) {
            if (!this.routine1(complementGraph, it)) continue;
            this.certify = false;
            return false;
        }
        this.certify = false;
        return true;
    }

    public boolean isBerge(Graph<V, E> g) {
        return this.isBerge(g, false);
    }

    public GraphPath<V, E> getCertificate() {
        return this.certificate;
    }
}

