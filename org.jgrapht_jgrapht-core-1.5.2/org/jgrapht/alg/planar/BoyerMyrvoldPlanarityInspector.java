/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.planar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.PlanarityTestingAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.util.DoublyLinkedList;
import org.jgrapht.util.TypeUtil;

public class BoyerMyrvoldPlanarityInspector<V, E>
implements PlanarityTestingAlgorithm<V, E> {
    private static final boolean DEBUG = false;
    private static final boolean PRINT_CASES = false;
    private Graph<V, E> graph;
    private int n;
    private PlanarityTestingAlgorithm.Embedding<V, E> embedding;
    private Graph<V, E> kuratowskiSubdivision;
    private List<Node> nodes;
    private List<Node> dfsTreeRoots;
    private List<Node> componentRoots;
    private List<MergeInfo> stack;
    private Node failedV;
    private boolean tested;
    private boolean planar;

    public BoyerMyrvoldPlanarityInspector(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph, "Graph can't be null");
        this.n = graph.vertexSet().size();
        this.nodes = new ArrayList<Node>(this.n);
        this.dfsTreeRoots = new ArrayList<Node>();
        this.componentRoots = new ArrayList<Node>(this.n);
        this.stack = new ArrayList<MergeInfo>();
    }

    private Node createNewNode(Map<V, Node> vertexMap, V graphVertex, E edge, Node parent, int dfsIndex) {
        Node child;
        if (parent == null) {
            child.outerFaceNeighbors[0] = child.outerFaceNeighbors[1] = (child = new Node(graphVertex, dfsIndex, 0, null, null));
            this.dfsTreeRoots.add(child);
        } else {
            Edge treeEdge = new Edge(edge, parent);
            Node componentRoot = new Node(parent.dfsIndex, treeEdge);
            treeEdge.target = child = new Node(graphVertex, dfsIndex, parent.height + 1, componentRoot, treeEdge);
            this.componentRoots.add(componentRoot);
            parent.treeEdges.add(treeEdge);
            child.outerFaceNeighbors[0] = child.outerFaceNeighbors[1] = componentRoot;
            componentRoot.outerFaceNeighbors[0] = componentRoot.outerFaceNeighbors[1] = child;
        }
        this.nodes.add(child);
        vertexMap.put((Node)graphVertex, child);
        return child;
    }

    private int orientDfs(Map<V, Node> vertexMap, V startGraphVertex, int currentDfsIndex) {
        ArrayList<OrientDfsStackInfo> stack = new ArrayList<OrientDfsStackInfo>();
        stack.add(new OrientDfsStackInfo(startGraphVertex, null, null, false));
        while (!stack.isEmpty()) {
            Node current;
            OrientDfsStackInfo info = (OrientDfsStackInfo)stack.remove(stack.size() - 1);
            if (info.backtrack) {
                current = vertexMap.get(info.current);
                current.leastAncestor = current.lowpoint = current.dfsIndex;
                for (Edge backEdge : current.backEdges) {
                    current.leastAncestor = Math.min(current.leastAncestor, backEdge.target.dfsIndex);
                }
                for (Edge treeEdge : current.treeEdges) {
                    current.lowpoint = Math.min(current.lowpoint, treeEdge.target.lowpoint);
                }
                current.lowpoint = Math.min(current.lowpoint, current.leastAncestor);
                continue;
            }
            if (vertexMap.containsKey(info.current)) continue;
            stack.add(new OrientDfsStackInfo(info.current, info.parent, info.parentEdge, true));
            current = this.createNewNode(vertexMap, info.current, info.parentEdge, vertexMap.get(info.parent), currentDfsIndex);
            ++currentDfsIndex;
            for (Edge e : this.graph.edgesOf(info.current)) {
                V opposite = Graphs.getOppositeVertex(this.graph, e, info.current);
                if (vertexMap.containsKey(opposite)) {
                    Node oppositeNode = vertexMap.get(opposite);
                    if (opposite.equals(info.parent)) continue;
                    Edge backEdge = new Edge(e, current, oppositeNode);
                    oppositeNode.downEdges.add(backEdge);
                    current.backEdges.add(backEdge);
                    continue;
                }
                stack.add(new OrientDfsStackInfo(opposite, current.graphVertex, e, false));
            }
        }
        return currentDfsIndex;
    }

    private void orient() {
        HashMap visited = new HashMap();
        int currentDfsIndex = 0;
        for (V vertex : this.graph.vertexSet()) {
            if (visited.containsKey(vertex)) continue;
            currentDfsIndex = this.orientDfs(visited, vertex, currentDfsIndex);
        }
        this.sortVertices();
    }

    private void sortVertices() {
        ArrayList<Object> sorted = new ArrayList<Object>(Collections.nCopies(this.n, null));
        for (Node node : this.nodes) {
            int n = node.lowpoint;
            if (sorted.get(n) == null) {
                sorted.set(n, new ArrayList());
            }
            ((List)sorted.get(n)).add(node);
        }
        int i = 0;
        for (List list : sorted) {
            if (i >= this.n) break;
            if (list == null) continue;
            for (Node node : list) {
                this.nodes.set(i++, node);
                if (node.parentEdge == null) continue;
                node.listNode = node.parentEdge.source.separatedDfsChildList.addElementLast(node);
            }
        }
    }

    private boolean lazyTestPlanarity() {
        if (!this.tested) {
            this.tested = true;
            this.orient();
            for (int currentNode = this.n - 1; currentNode >= 0; --currentNode) {
                Node current = this.nodes.get(currentNode);
                for (Edge downEdge : current.downEdges) {
                    this.walkUp(downEdge.source, current, downEdge);
                }
                for (Edge treeEdge : current.treeEdges) {
                    this.walkDown(treeEdge.target.initialComponentRoot);
                }
                for (Edge downEdge : current.downEdges) {
                    if (downEdge.embedded) continue;
                    this.failedV = current;
                    this.planar = false;
                    return false;
                }
            }
            this.planar = true;
        }
        return this.planar;
    }

    private void mergeBiconnectedComponent() {
        MergeInfo info = this.stack.get(this.stack.size() - 1);
        this.stack.remove(this.stack.size() - 1);
        Node virtualRoot = info.child;
        if (info.isInverted()) {
            virtualRoot.swapNeighbors();
        }
        Node root = info.parent;
        Node virtualRootChild = virtualRoot.parentEdge.target;
        root.pertinentRoots.removeNode(virtualRoot.listNode);
        root.separatedDfsChildList.removeNode(virtualRootChild.listNode);
        root.mergeChildEdges(virtualRoot.embedded, info.vIn, info.vOut, info.parentNext, virtualRoot.parentEdge);
        root.substituteAnother(info.parentNext, info.childPrev);
        info.childPrev.substitute(virtualRoot, root);
        virtualRoot.outerFaceNeighbors[1] = null;
        virtualRoot.outerFaceNeighbors[0] = null;
    }

    private OuterFaceCirculator embedBackEdge(Node root, int entryDir, Edge edge, Node childPrev) {
        assert (!edge.embedded);
        if (entryDir == 0) {
            root.embedded.addLast(edge);
        } else {
            root.embedded.addFirst(edge);
        }
        Node child = edge.source;
        child.embedBackEdge(edge, childPrev);
        child.edgeToEmbed = null;
        child.backEdgeFlag = this.n;
        edge.embedded = true;
        child.substitute(childPrev, root);
        root.outerFaceNeighbors[entryDir] = child;
        Node next = child.nextOnOuterFace(root);
        return new OuterFaceCirculator(next, child);
    }

    private void embedShortCircuit(Node componentRoot, int entryDir, OuterFaceCirculator circulator) {
        Node current = circulator.getCurrent();
        Node prev = circulator.getPrev();
        Edge shortCircuit = new Edge(current, componentRoot.getParent());
        if (entryDir == 0) {
            componentRoot.embedded.addLast(shortCircuit);
            componentRoot.outerFaceNeighbors[0] = current;
        } else {
            componentRoot.embedded.addFirst(shortCircuit);
            componentRoot.outerFaceNeighbors[1] = current;
        }
        current.embedBackEdge(shortCircuit, prev);
        current.substitute(prev, componentRoot);
    }

    private void walkDown(Node componentRoot) {
        block0: for (int componentEntryDir = 0; componentEntryDir < 2 && this.stack.isEmpty(); ++componentEntryDir) {
            int currentComponentEntryDir = componentEntryDir;
            OuterFaceCirculator circulator = componentRoot.iterator(currentComponentEntryDir);
            Node current = circulator.next();
            while (current != componentRoot) {
                if (current.hasBackEdgeWrtTo(componentRoot)) {
                    Node childPrev = circulator.getPrev();
                    while (!this.stack.isEmpty()) {
                        this.mergeBiconnectedComponent();
                    }
                    circulator = this.embedBackEdge(componentRoot, componentEntryDir, current.edgeToEmbed, childPrev);
                }
                if (!current.pertinentRoots.isEmpty()) {
                    int parentComponentEntryDir = currentComponentEntryDir;
                    Node root = current.pertinentRoots.getFirst();
                    OuterFaceCirculator ccwCirculator = this.getActiveSuccessorOnOuterFace(root, componentRoot, 0);
                    Node ccwActiveNode = ccwCirculator.getCurrent();
                    OuterFaceCirculator cwCirculator = this.getActiveSuccessorOnOuterFace(root, componentRoot, 1);
                    Node cwActiveNode = cwCirculator.getCurrent();
                    currentComponentEntryDir = ccwActiveNode.isInternallyActiveWrtTo(componentRoot) ? 0 : (cwActiveNode.isInternallyActiveWrtTo(componentRoot) ? 1 : (ccwActiveNode.isPertinentWrtTo(componentRoot) ? 0 : 1));
                    if (currentComponentEntryDir == 0) {
                        this.stack.add(new MergeInfo(current, circulator.next(), root, root.outerFaceNeighbors[1], parentComponentEntryDir, currentComponentEntryDir));
                        current = ccwActiveNode;
                        circulator = ccwCirculator;
                        if (cwActiveNode.hasRootNeighbor()) continue;
                        this.embedShortCircuit(root, 1, cwCirculator);
                        continue;
                    }
                    this.stack.add(new MergeInfo(current, circulator.next(), root, root.outerFaceNeighbors[0], parentComponentEntryDir, currentComponentEntryDir));
                    current = cwActiveNode;
                    circulator = cwCirculator;
                    if (ccwActiveNode.hasRootNeighbor()) continue;
                    this.embedShortCircuit(root, 0, ccwCirculator);
                    continue;
                }
                if (current.isInactiveWrtTo(componentRoot)) {
                    current = circulator.next();
                    continue;
                }
                if (current.hasRootNeighbor() || !this.stack.isEmpty()) continue block0;
                this.embedShortCircuit(componentRoot, componentEntryDir, circulator);
                continue block0;
            }
        }
    }

    private void walkUp(Node start, Node end, Edge edge) {
        int visited;
        start.backEdgeFlag = visited = end.dfsIndex;
        start.edgeToEmbed = edge;
        Node x = start.outerFaceNeighbors[0];
        Node y = start.outerFaceNeighbors[1];
        Node xPrev = start;
        Node yPrev = start;
        start.visited = visited;
        while (x != end && !x.isVisitedWrtTo(end) && !y.isVisitedWrtTo(end)) {
            x.visited = y.visited = visited;
            Node root = null;
            if (x.isRootVertex()) {
                root = x;
            } else if (y.isRootVertex()) {
                root = y;
            }
            if (root != null) {
                Node rootChild = root.parentEdge.target;
                Node newStart = root.parentEdge.source;
                if (newStart == end) break;
                root.listNode = rootChild.lowpoint < end.dfsIndex ? newStart.pertinentRoots.addElementLast(root) : newStart.pertinentRoots.addElementFirst(root);
                newStart.visited = visited;
                xPrev = yPrev = newStart;
                x = newStart.outerFaceNeighbors[0];
                y = newStart.outerFaceNeighbors[1];
                continue;
            }
            Node t = x;
            x = x.nextOnOuterFace(xPrev);
            xPrev = t;
            t = y;
            y = y.nextOnOuterFace(yPrev);
            yPrev = t;
        }
    }

    private PlanarityTestingAlgorithm.Embedding<V, E> lazyComputeEmbedding() {
        this.lazyTestPlanarity();
        if (!this.planar) {
            throw new IllegalArgumentException("Input graph is not planar, can't compute graph embedding");
        }
        if (this.embedding == null) {
            for (Node dfsTreeRoot : this.dfsTreeRoots) {
                this.cleanUpDfs(dfsTreeRoot);
            }
            HashMap embeddingMap = new HashMap();
            for (Node node : this.nodes) {
                for (Node child : node.separatedDfsChildList) {
                    Node virtualRoot = child.initialComponentRoot;
                    node.embedded.append(virtualRoot.embedded);
                }
                ArrayList embeddedEdges = new ArrayList(node.embedded.size());
                for (Edge edge : node.embedded) {
                    embeddedEdges.add(edge.graphEdge);
                }
                embeddingMap.put(node.graphVertex, embeddedEdges);
            }
            this.embedding = new PlanarityTestingAlgorithm.EmbeddingImpl<V, E>(this.graph, embeddingMap);
        }
        return this.embedding;
    }

    private void printBiconnectedComponent(Node node) {
        Node current;
        StringBuilder builder = new StringBuilder(node.toString(false));
        OuterFaceCirculator circulator = node.iterator(0);
        Node stop = current = circulator.next();
        do {
            builder.append(" -> ").append(current.toString(false));
        } while ((current = circulator.next()) != stop);
        System.out.println("Biconnected component after merge: " + builder.toString());
    }

    private void printState() {
        System.out.println("\nPrinting state:");
        System.out.println("Dfs roots: " + this.dfsTreeRoots);
        System.out.println("Nodes:");
        for (Node node : this.nodes) {
            System.out.println(node.toString(true));
        }
        System.out.println("Virtual nodes:");
        for (Node node : this.componentRoots) {
            System.out.println(node.toString(true));
        }
        ArrayList<Edge> inverted = new ArrayList<Edge>();
        for (Node node : this.nodes) {
            for (Edge edge : node.treeEdges) {
                if (edge.sign >= 0) continue;
                inverted.add(edge);
            }
        }
        System.out.println("Inverted edges = " + inverted);
    }

    private OuterFaceCirculator selectOnOuterFace(Predicate<Node> predicate, Node start, Node stop, int dir) {
        OuterFaceCirculator circulator = start.iterator(dir);
        Node current = circulator.next();
        while (current != stop && !predicate.test(current)) {
            current = circulator.next();
        }
        return circulator;
    }

    private OuterFaceCirculator getActiveSuccessorOnOuterFace(Node start, Node v, int dir) {
        return this.selectOnOuterFace(n -> n.isActiveWrtTo(v), start, start, dir);
    }

    private OuterFaceCirculator getExternallyActiveSuccessorOnOuterFace(Node start, Node stop, Node v, int dir) {
        return this.selectOnOuterFace(n -> n.isExternallyActiveWrtTo(v), start, stop, dir);
    }

    private Node getComponentRoot(Node node) {
        return this.selectOnOuterFace(Node::isRootVertex, node, node, 0).getCurrent();
    }

    private void addPathEdges(Set<Edge> edges, Edge startEdge, Node stop) {
        edges.add(startEdge);
        for (Node current = startEdge.source; current != stop; current = current.getParent()) {
            edges.add(current.parentEdge);
        }
    }

    private void addPathEdges(Set<Edge> edges, Node start, Node stop) {
        if (start != stop) {
            this.addPathEdges(edges, start.parentEdge, stop);
        }
    }

    private Edge searchEdge(Node current, int heightMax) {
        return this.searchEdge(current, heightMax, null);
    }

    private Edge searchEdge(Node current, int heightMax, Edge forbiddenEdge) {
        Predicate<Edge> isNeeded = e -> {
            if (forbiddenEdge == e) {
                return false;
            }
            return e.target.height < heightMax;
        };
        return this.searchEdge(current, isNeeded);
    }

    private Edge searchEdge(Node current, Predicate<Edge> isNeeded) {
        for (Node node : current.separatedDfsChildList) {
            Edge result = this.searchSubtreeDfs(node, isNeeded);
            if (result == null) continue;
            return result;
        }
        for (Edge edge : current.backEdges) {
            if (!isNeeded.test(edge)) continue;
            return edge;
        }
        return null;
    }

    private Edge searchSubtreeDfs(Node start, Predicate<Edge> isNeeded) {
        ArrayList<Node> stack = new ArrayList<Node>();
        stack.add(start);
        while (!stack.isEmpty()) {
            Node current = (Node)stack.remove(stack.size() - 1);
            for (Edge edge : current.backEdges) {
                if (!isNeeded.test(edge)) continue;
                return edge;
            }
            for (Edge edge : current.treeEdges) {
                stack.add(edge.target);
            }
        }
        return null;
    }

    private Node highest(Node a, Node b) {
        return a.height > b.height ? a : b;
    }

    private Node lowest(Node a, Node b) {
        return a.height < b.height ? a : b;
    }

    private void setBoundaryDepth(Node componentRoot, Node w, int dir, int delta) {
        OuterFaceCirculator circulator = componentRoot.iterator(dir);
        Node current = circulator.next();
        int currentHeight = delta;
        while (current != w) {
            current.boundaryHeight = currentHeight;
            currentHeight += delta;
            current = circulator.next();
        }
    }

    private void clearVisited() {
        this.nodes.forEach(n -> {
            n.visited = 0;
        });
        this.componentRoots.forEach(n -> {
            n.visited = 0;
        });
    }

    private boolean findPathDfs(Node start, Edge startPrev, Predicate<Node> canGo, Predicate<Node> isFinish, List<Edge> edges) {
        ArrayList<SearchInfo> stack = new ArrayList<SearchInfo>();
        stack.add(new SearchInfo(start, startPrev, false));
        while (!stack.isEmpty()) {
            SearchInfo info = (SearchInfo)stack.remove(stack.size() - 1);
            if (isFinish.test(info.current)) {
                edges.add(info.prevEdge);
                edges.remove(0);
                return true;
            }
            if (info.backtrack) {
                edges.remove(edges.size() - 1);
                continue;
            }
            if (info.current.visited != 0) continue;
            info.current.visited = 1;
            stack.add(new SearchInfo(info.current, info.prevEdge, true));
            edges.add(info.prevEdge);
            DoublyLinkedList.NodeIterator<Edge> iterator = info.current.embedded.reverseCircularIterator(info.prevEdge);
            while (iterator.hasNext()) {
                Edge currentEdge = (Edge)iterator.next();
                Node opposite = currentEdge.getOpposite(info.current);
                if ((!canGo.test(opposite) || opposite.visited != 0) && !isFinish.test(opposite)) continue;
                stack.add(new SearchInfo(opposite, currentEdge, false));
            }
        }
        return false;
    }

    private List<Edge> findHighestObstructingPath(Node componentRoot, Node w) {
        this.clearVisited();
        ArrayList<Edge> result = new ArrayList<Edge>();
        OuterFaceCirculator circulator = componentRoot.iterator(0);
        Node current = circulator.next();
        while (current != w) {
            if (this.findPathDfs(current, current.embedded.getFirst(), n -> !n.marked, n -> n.boundaryHeight < 0, result)) {
                return result;
            }
            current = circulator.next();
        }
        return result;
    }

    private Graph<V, E> finish(Set<Edge> subdivision) {
        HashSet edgeSubset = new HashSet();
        HashSet vertexSubset = new HashSet();
        subdivision.forEach(e -> {
            edgeSubset.add(e.graphEdge);
            vertexSubset.add(e.target.graphVertex);
            vertexSubset.add(e.source.graphVertex);
        });
        this.kuratowskiSubdivision = new AsSubgraph<V, E>(this.graph, vertexSubset, edgeSubset);
        return this.kuratowskiSubdivision;
    }

    private void addBoundaryEdges(Set<Edge> edges, Node componentRoot) {
        Node current;
        OuterFaceCirculator circulator = componentRoot.iterator(0);
        do {
            Edge edge = circulator.edgeToNext();
            edge.target.marked = true;
            edge.source.marked = true;
            edges.add(edge);
        } while ((current = circulator.next()) != componentRoot);
    }

    private void kuratowskiCleanUp() {
        for (Node dfsTreeRoot : this.dfsTreeRoots) {
            this.cleanUpDfs(dfsTreeRoot);
        }
        for (Node node : this.componentRoots) {
            if (node.outerFaceNeighbors[0] == null) continue;
            node.removeShortCircuitEdges();
            this.fixBoundaryOrder(node);
        }
    }

    private void cleanUpDfs(Node dfsTreeRoot) {
        ArrayList<Pair<Node, Integer>> stack = new ArrayList<Pair<Node, Integer>>();
        stack.add(Pair.of(dfsTreeRoot, 1));
        while (!stack.isEmpty()) {
            Pair entry = (Pair)stack.remove(stack.size() - 1);
            Node current = (Node)entry.getFirst();
            int sign = (Integer)entry.getSecond();
            if (sign < 0) {
                current.embedded.invert();
            }
            current.removeShortCircuitEdges();
            for (Node node : current.separatedDfsChildList) {
                node.parentEdge.sign = sign;
            }
            for (Edge treeEdge : current.treeEdges) {
                stack.add(Pair.of(treeEdge.target, sign * treeEdge.sign));
            }
        }
    }

    private void fixBoundaryOrder(Node componentRoot) {
        if (componentRoot.embedded.size() < 2) {
            return;
        }
        Node componentParent = componentRoot.getParent();
        Edge edgeToNext = componentRoot.embedded.getLast();
        Edge edgeToPrev = componentRoot.embedded.getFirst();
        Node next = edgeToNext.getOpposite(componentParent);
        Node prev = edgeToPrev.getOpposite(componentParent);
        componentRoot.outerFaceNeighbors[0] = next;
        componentRoot.outerFaceNeighbors[1] = prev;
        next.outerFaceNeighbors[1] = componentRoot;
        prev.outerFaceNeighbors[0] = componentRoot;
        Node current = componentRoot.outerFaceNeighbors[0];
        do {
            edgeToNext = current.embedded.getLast();
            edgeToPrev = current.embedded.getFirst();
            next = edgeToNext.getOpposite(current);
            prev = edgeToPrev.getOpposite(current);
            if (prev != componentParent) {
                current.outerFaceNeighbors[1] = prev;
            }
            if (next == componentParent) continue;
            current.outerFaceNeighbors[0] = next;
        } while ((current = next) != componentParent);
    }

    private void removeUp(Node start, Node end, int dir, Set<Edge> edges) {
        Node next;
        if (start == end) {
            return;
        }
        OuterFaceCirculator circulator = start.iterator(dir);
        do {
            Edge edge = circulator.edgeToNext();
            edges.remove(edge);
        } while ((next = circulator.next()) != end);
    }

    private Node getNextOnPath(Node w, Edge backEdge) {
        if (backEdge.source == w) {
            return null;
        }
        Node prev = backEdge.source;
        for (Node current = backEdge.source.getParent(); current != w; current = current.getParent()) {
            prev = current;
        }
        return prev;
    }

    private List<Edge> findPathToV(List<Edge> path, Node v) {
        this.clearVisited();
        int i = 0;
        Edge currentEdge = path.get(i);
        Node current = currentEdge.source.boundaryHeight != 0 ? currentEdge.target : currentEdge.source;
        ArrayList<Edge> result = new ArrayList<Edge>();
        while (i < path.size() - 1) {
            if (this.findPathDfs(current, currentEdge, n -> !n.marked, n -> n == v, result)) {
                return result;
            }
            currentEdge = path.get(++i);
            current = currentEdge.getOpposite(current);
        }
        return result;
    }

    private boolean firstStrictlyHigher(Node a, Node b, Node c) {
        return a.height > b.height && a.height > c.height;
    }

    private Edge checkComponentForFailedEdge(Node componentRoot, Node v) {
        OuterFaceCirculator firstDir = this.getExternallyActiveSuccessorOnOuterFace(componentRoot, componentRoot, v, 0);
        Node firstDirNode = firstDir.getCurrent();
        OuterFaceCirculator secondDir = this.getExternallyActiveSuccessorOnOuterFace(componentRoot, componentRoot, v, 1);
        Node secondDirNode = secondDir.getCurrent();
        if (firstDirNode != componentRoot && firstDirNode != secondDirNode) {
            Node current = firstDir.next();
            while (current != secondDirNode) {
                if (current.isPertinentWrtTo(v)) {
                    return this.searchEdge(current, e -> e.target == v && !e.embedded);
                }
                current = firstDir.next();
            }
        }
        return null;
    }

    private Edge findFailedEdge(Node v) {
        if (this.stack.isEmpty()) {
            for (Node child : v.separatedDfsChildList) {
                Node componentRoot = child.initialComponentRoot;
                Edge result = this.checkComponentForFailedEdge(componentRoot, v);
                if (result == null) continue;
                return result;
            }
            return null;
        }
        MergeInfo info = this.stack.get(this.stack.size() - 1);
        return this.checkComponentForFailedEdge(info.child, v);
    }

    private Graph<V, E> lazyExtractKuratowskiSubdivision() {
        if (this.kuratowskiSubdivision == null) {
            Node lastNode;
            Node y;
            Node x;
            Node componentRoot;
            this.kuratowskiCleanUp();
            HashSet<Edge> subdivision = new HashSet<Edge>();
            Edge failedEdge = this.findFailedEdge(this.failedV);
            assert (failedEdge != null);
            Node v = failedEdge.target;
            Node w = failedEdge.source;
            while (true) {
                componentRoot = this.getComponentRoot(w);
                x = this.getExternallyActiveSuccessorOnOuterFace(w, componentRoot, v, 1).getCurrent();
                y = this.getExternallyActiveSuccessorOnOuterFace(w, componentRoot, v, 0).getCurrent();
                if (x.isRootVertex()) {
                    w = x.getParent();
                    continue;
                }
                if (!y.isRootVertex()) break;
                w = y.getParent();
            }
            componentRoot = this.getComponentRoot(w);
            Edge xBackEdge = this.searchEdge(x, v.height);
            Edge yBackEdge = this.searchEdge(y, v.height);
            Node backLower = this.lowest(xBackEdge.target, yBackEdge.target);
            Node backHigher = this.highest(xBackEdge.target, yBackEdge.target);
            this.addPathEdges(subdivision, xBackEdge, x);
            this.addPathEdges(subdivision, yBackEdge, y);
            this.addBoundaryEdges(subdivision, componentRoot);
            if (componentRoot.getParent() != v) {
                this.addPathEdges(subdivision, componentRoot.getParent(), backLower);
                this.addPathEdges(subdivision, failedEdge, w);
                return this.finish(subdivision);
            }
            Node z = this.getNextOnPath(w, failedEdge);
            Edge backEdge = null;
            if (z != null) {
                backEdge = this.searchSubtreeDfs(z, e -> e.target.height < v.height && e != failedEdge);
            }
            if (backEdge != null) {
                this.addPathEdges(subdivision, backEdge, w);
                this.addPathEdges(subdivision, failedEdge, w);
                Node highest = this.highest(xBackEdge.target, this.highest(yBackEdge.target, backEdge.target));
                Node lowest = this.lowest(xBackEdge.target, this.lowest(yBackEdge.target, backEdge.target));
                this.addPathEdges(subdivision, highest, lowest);
                return this.finish(subdivision);
            }
            this.setBoundaryDepth(componentRoot, w, 0, 1);
            this.setBoundaryDepth(componentRoot, w, 1, -1);
            assert (x.boundaryHeight > 0);
            List<Edge> path = this.findHighestObstructingPath(componentRoot, w);
            assert (!path.isEmpty());
            Edge firstEdge = path.get(0);
            Edge lastEdge = path.get(path.size() - 1);
            Node firstNode = firstEdge.source.boundaryHeight > 0 ? firstEdge.source : firstEdge.target;
            Node node = lastNode = lastEdge.source.boundaryHeight < 0 ? lastEdge.source : lastEdge.target;
            if (firstNode.boundaryHeight < x.boundaryHeight || lastNode.boundaryHeight > y.boundaryHeight) {
                if (lastNode.boundaryHeight > y.boundaryHeight) {
                    Node removeStart = firstNode.boundaryHeight < x.boundaryHeight ? firstNode : x;
                    this.removeUp(removeStart, componentRoot, 1, subdivision);
                } else {
                    this.removeUp(y, componentRoot, 0, subdivision);
                }
                this.addPathEdges(subdivision, failedEdge, w);
                subdivision.addAll(path);
                this.addPathEdges(subdivision, v, backLower);
                return this.finish(subdivision);
            }
            path.forEach(e -> {
                e.target.marked = true;
                e.source.marked = true;
            });
            List<Edge> pathToV = this.findPathToV(path, v);
            if (!pathToV.isEmpty()) {
                this.removeUp(x, componentRoot, 1, subdivision);
                this.removeUp(y, componentRoot, 0, subdivision);
                subdivision.addAll(path);
                subdivision.addAll(pathToV);
                this.addPathEdges(subdivision, v, backLower);
                this.addPathEdges(subdivision, failedEdge, w);
                return this.finish(subdivision);
            }
            Edge externallyActive = this.searchEdge(w, v.height, failedEdge);
            assert (externallyActive != null);
            this.addPathEdges(subdivision, externallyActive, w);
            if (this.firstStrictlyHigher(externallyActive.target, xBackEdge.target, yBackEdge.target)) {
                this.addPathEdges(subdivision, componentRoot.getParent(), backLower);
            } else if (this.firstStrictlyHigher(xBackEdge.target, yBackEdge.target, externallyActive.target)) {
                this.removeUp(componentRoot, x, 0, subdivision);
                this.removeUp(w, lastNode, 0, subdivision);
                subdivision.addAll(path);
                this.addPathEdges(subdivision, failedEdge, w);
                this.addPathEdges(subdivision, v, this.lowest(backLower, externallyActive.target));
            } else if (this.firstStrictlyHigher(yBackEdge.target, xBackEdge.target, externallyActive.target)) {
                this.removeUp(y, componentRoot, 0, subdivision);
                this.removeUp(firstNode, w, 0, subdivision);
                subdivision.addAll(path);
                this.addPathEdges(subdivision, failedEdge, w);
                this.addPathEdges(subdivision, v, this.lowest(backLower, externallyActive.target));
            } else if (firstNode.boundaryHeight > x.boundaryHeight) {
                this.removeUp(w, lastNode, 0, subdivision);
                subdivision.addAll(path);
                this.addPathEdges(subdivision, failedEdge, w);
                this.addPathEdges(subdivision, this.highest(backHigher, externallyActive.target), this.lowest(backLower, externallyActive.target));
            } else if (lastNode.boundaryHeight < y.boundaryHeight) {
                this.removeUp(firstNode, w, 0, subdivision);
                subdivision.addAll(path);
                this.addPathEdges(subdivision, failedEdge, w);
                this.addPathEdges(subdivision, this.highest(backHigher, externallyActive.target), this.lowest(backLower, externallyActive.target));
            } else {
                subdivision.addAll(path);
                this.addPathEdges(subdivision, v, this.lowest(backLower, externallyActive.target));
                this.addPathEdges(subdivision, failedEdge, w);
            }
            return this.finish(subdivision);
        }
        return this.kuratowskiSubdivision;
    }

    @Override
    public boolean isPlanar() {
        return this.lazyTestPlanarity();
    }

    @Override
    public PlanarityTestingAlgorithm.Embedding<V, E> getEmbedding() {
        if (this.isPlanar()) {
            return this.lazyComputeEmbedding();
        }
        throw new IllegalArgumentException("Graph is not planar");
    }

    @Override
    public Graph<V, E> getKuratowskiSubdivision() {
        if (this.isPlanar()) {
            throw new IllegalArgumentException("Graph is planar");
        }
        return this.lazyExtractKuratowskiSubdivision();
    }

    private class Node {
        V graphVertex;
        boolean rootVertex;
        int dfsIndex;
        int height;
        int lowpoint;
        int leastAncestor;
        int visited;
        int backEdgeFlag;
        int boundaryHeight;
        boolean marked;
        Edge parentEdge;
        Edge edgeToEmbed;
        Node initialComponentRoot;
        Node[] outerFaceNeighbors;
        DoublyLinkedList<Node> separatedDfsChildList;
        DoublyLinkedList<Node> pertinentRoots;
        List<Edge> treeEdges;
        List<Edge> downEdges;
        List<Edge> backEdges;
        DoublyLinkedList.ListNode<Node> listNode;
        DoublyLinkedList<Edge> embedded;

        Node(V graphVertex, int dfsIndex, int height, Node initialComponentRoot, Edge parentEdge) {
            this(graphVertex, dfsIndex, parentEdge, false);
            this.height = height;
            this.initialComponentRoot = initialComponentRoot;
        }

        Node(int dfsIndex, Edge parentEdge) {
            this(null, dfsIndex, parentEdge, true);
        }

        Node(V graphVertex, int dfsIndex, Edge parentEdge, boolean rootVertex) {
            this.graphVertex = graphVertex;
            this.dfsIndex = dfsIndex;
            this.parentEdge = parentEdge;
            this.rootVertex = rootVertex;
            this.outerFaceNeighbors = (Node[])TypeUtil.uncheckedCast(Array.newInstance(Node.class, 2));
            this.embedded = new DoublyLinkedList();
            if (parentEdge != null) {
                this.embedded.add(parentEdge);
            }
            this.visited = this.backEdgeFlag = BoyerMyrvoldPlanarityInspector.this.n;
            if (!rootVertex) {
                this.separatedDfsChildList = new DoublyLinkedList();
                this.pertinentRoots = new DoublyLinkedList();
                this.treeEdges = new ArrayList<Edge>();
                this.downEdges = new ArrayList<Edge>();
                this.backEdges = new ArrayList<Edge>();
            }
        }

        boolean isVisitedWrtTo(Node node) {
            return node.dfsIndex == this.visited;
        }

        boolean isPertinentWrtTo(Node node) {
            return this.backEdgeFlag == node.dfsIndex || !this.pertinentRoots.isEmpty();
        }

        boolean hasBackEdgeWrtTo(Node node) {
            return this.backEdgeFlag == node.dfsIndex;
        }

        boolean isExternallyActiveWrtTo(Node node) {
            return this.leastAncestor < node.dfsIndex || !this.separatedDfsChildList.isEmpty() && this.separatedDfsChildList.getFirst().lowpoint < node.dfsIndex;
        }

        boolean isRootVertex() {
            return this.rootVertex;
        }

        boolean isInternallyActiveWrtTo(Node node) {
            return this.isPertinentWrtTo(node) && !this.isExternallyActiveWrtTo(node);
        }

        boolean isInactiveWrtTo(Node node) {
            return !this.isExternallyActiveWrtTo(node) && !this.isPertinentWrtTo(node);
        }

        boolean isActiveWrtTo(Node node) {
            return !this.isInactiveWrtTo(node);
        }

        OuterFaceCirculator iterator(int direction) {
            return new OuterFaceCirculator(this.outerFaceNeighbors[direction], this);
        }

        void removeShortCircuitEdges() {
            this.embedded.removeIf(e -> e.shortCircuit);
        }

        Node getParent() {
            return this.parentEdge == null ? null : this.parentEdge.source;
        }

        void checkIsAdjacent(Node node) {
            assert (node == this.outerFaceNeighbors[0] || node == this.outerFaceNeighbors[1]);
        }

        void swapNeighbors() {
            Node t = this.outerFaceNeighbors[0];
            this.outerFaceNeighbors[0] = this.outerFaceNeighbors[1];
            this.outerFaceNeighbors[1] = t;
        }

        void substitute(Node node, Node newNeighbor) {
            this.checkIsAdjacent(node);
            if (this.outerFaceNeighbors[0] == node) {
                this.outerFaceNeighbors[0] = newNeighbor;
            } else {
                this.outerFaceNeighbors[1] = newNeighbor;
            }
        }

        void substituteAnother(Node node, Node newNeighbor) {
            this.checkIsAdjacent(node);
            if (this.outerFaceNeighbors[0] == node) {
                this.outerFaceNeighbors[1] = newNeighbor;
            } else {
                this.outerFaceNeighbors[0] = newNeighbor;
            }
        }

        boolean hasRootNeighbor() {
            return this.outerFaceNeighbors[0].isRootVertex() || this.outerFaceNeighbors[1].isRootVertex();
        }

        Node nextOnOuterFace(Node prev) {
            this.checkIsAdjacent(prev);
            if (this.outerFaceNeighbors[0] == prev) {
                return this.outerFaceNeighbors[1];
            }
            return this.outerFaceNeighbors[0];
        }

        void embedBackEdge(Edge edge, Node prev) {
            Edge firstEdge;
            assert (!this.embedded.isEmpty());
            if (prev.isRootVertex()) {
                prev = prev.getParent();
            }
            if ((firstEdge = this.embedded.getFirst()).getOpposite(this) == prev) {
                this.embedded.addFirst(edge);
            } else {
                this.embedded.addLast(edge);
            }
        }

        void mergeChildEdges(DoublyLinkedList<Edge> edges, int vIn, int vOut, Node parentNext, Edge parentEdge) {
            assert (!this.embedded.isEmpty());
            Node firstOpposite = this.embedded.getFirst().getOpposite(this);
            boolean alongParentTraversal = firstOpposite != parentNext;
            boolean actionAppend = false;
            boolean invert = false;
            if (vIn == 0) {
                if (vOut == 0) {
                    if (!alongParentTraversal) {
                        actionAppend = true;
                        invert = true;
                    }
                } else if (alongParentTraversal) {
                    invert = true;
                } else {
                    actionAppend = true;
                }
            } else if (vOut == 0) {
                if (!alongParentTraversal) {
                    actionAppend = true;
                    invert = true;
                }
            } else if (alongParentTraversal) {
                invert = true;
            } else {
                actionAppend = true;
            }
            if (invert) {
                parentEdge.sign = -1;
                edges.invert();
            }
            if (actionAppend) {
                this.embedded.append(edges);
            } else {
                this.embedded.prepend(edges);
            }
        }

        public String toString() {
            String neighbor1 = this.outerFaceNeighbors[0] == null ? "null" : this.outerFaceNeighbors[0].toString(false);
            String neighbor2 = this.outerFaceNeighbors[1] == null ? "null" : this.outerFaceNeighbors[1].toString(false);
            String childListString = "null";
            if (this.separatedDfsChildList != null) {
                StringBuilder builder = new StringBuilder("{");
                this.separatedDfsChildList.forEach(n -> builder.append(n.toString(false)).append(", "));
                childListString = builder.append("}").toString();
            }
            if (this.rootVertex) {
                return String.format("R {%s}: neighbors = [%s, %s], embedded = %s, visited = %d, back_edge_flag = %d, dfs_index = %d", this.toString(false), neighbor1, neighbor2, this.embedded.toString(), this.visited, this.backEdgeFlag, this.dfsIndex);
            }
            return String.format("{%s}:  neighbors = [%s, %s], embedded = %s, visited = %d, back_edge_flag = %d, dfs_index = %d, separated = %s, tree_edges = %s, down_edges = %s, back_edges = %s, parent = %s, lowpoint = %d, least_ancestor = %d", this.toString(false), neighbor1, neighbor2, this.embedded.toString(), this.visited, this.backEdgeFlag, this.dfsIndex, childListString, this.treeEdges.toString(), this.downEdges.toString(), this.backEdges.toString(), this.parentEdge == null ? "null" : this.parentEdge.source.toString(false), this.lowpoint, this.leastAncestor);
        }

        public String toString(boolean full) {
            if (!full) {
                if (this.rootVertex) {
                    return String.format("%s^%s", this.parentEdge.source.graphVertex.toString(), this.parentEdge.target.graphVertex.toString());
                }
                return this.graphVertex.toString();
            }
            return this.toString();
        }
    }

    private class Edge {
        E graphEdge;
        Node source;
        Node target;
        int sign;
        boolean embedded;
        boolean shortCircuit;

        Edge(Node source, Node target) {
            this(null, source, target);
            this.shortCircuit = true;
            this.embedded = true;
        }

        Edge(E graphEdge, Node source) {
            this(graphEdge, source, null);
        }

        Edge(E graphEdge, Node source, Node target) {
            this.graphEdge = graphEdge;
            this.source = source;
            this.target = target;
            this.sign = 1;
        }

        boolean isIncidentTo(Node node) {
            return this.source == node || this.target == node;
        }

        Node getOpposite(Node node) {
            assert (this.isIncidentTo(node));
            return this.source == node ? this.target : this.source;
        }

        public String toString() {
            String formatString = "%s -> %s";
            if (this.shortCircuit) {
                formatString = "%s ~ %s";
            }
            return String.format(formatString, this.source.toString(false), this.target.toString(false));
        }
    }

    private class OrientDfsStackInfo {
        V current;
        V parent;
        E parentEdge;
        boolean backtrack;

        OrientDfsStackInfo(V current, V parent, E parentEdge, boolean backtrack) {
            this.current = current;
            this.parent = parent;
            this.parentEdge = parentEdge;
            this.backtrack = backtrack;
        }
    }

    private class MergeInfo {
        Node parent;
        Node parentNext;
        Node child;
        Node childPrev;
        int vIn;
        int vOut;

        MergeInfo(Node parent, Node parentNext, Node child, Node childPrev, int vIn, int vOut) {
            this.parent = parent;
            this.parentNext = parentNext;
            this.child = child;
            this.childPrev = childPrev;
            this.vIn = vIn;
            this.vOut = vOut;
        }

        boolean isInverted() {
            return this.vIn != this.vOut;
        }

        public String toString() {
            return String.format("Parent dir = {%s -> %s}, child_dir = {%s -> %s}, inverted = %b, vIn = %d, vOut = %d", this.parent.toString(false), this.parentNext.toString(false), this.childPrev.toString(false), this.child.toString(false), this.isInverted(), this.vIn, this.vOut);
        }
    }

    private class OuterFaceCirculator
    implements Iterator<Node> {
        private Node current;
        private Node prev;

        OuterFaceCirculator(Node current, Node prev) {
            this.current = current;
            this.prev = prev;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Node next() {
            Node t = this.current;
            this.current = this.current.nextOnOuterFace(this.prev);
            this.prev = t;
            return this.prev;
        }

        Edge edgeToNext() {
            Edge edge = this.prev.embedded.getFirst();
            Node target = this.toExistingNode(this.current);
            Node source = this.toExistingNode(this.prev);
            if (edge.getOpposite(source) == target) {
                return edge;
            }
            return this.prev.embedded.getLast();
        }

        Node getCurrent() {
            return this.prev;
        }

        Node getPrev() {
            return this.prev.nextOnOuterFace(this.current);
        }

        private Node toExistingNode(Node node) {
            return node.isRootVertex() ? node.getParent() : node;
        }

        public String toString() {
            return String.format("%s -> %s", this.prev.toString(false), this.current.toString(false));
        }
    }

    private class SearchInfo {
        Node current;
        Edge prevEdge;
        boolean backtrack;

        SearchInfo(Node current, Edge prevEdge, boolean backtrack) {
            this.current = current;
            this.prevEdge = prevEdge;
            this.backtrack = backtrack;
        }
    }
}

