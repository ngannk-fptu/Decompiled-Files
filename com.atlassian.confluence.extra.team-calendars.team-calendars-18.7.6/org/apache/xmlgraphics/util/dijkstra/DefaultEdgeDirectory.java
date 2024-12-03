/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util.dijkstra;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.xmlgraphics.util.dijkstra.Edge;
import org.apache.xmlgraphics.util.dijkstra.EdgeDirectory;
import org.apache.xmlgraphics.util.dijkstra.Vertex;

public class DefaultEdgeDirectory
implements EdgeDirectory {
    private Map edges = new HashMap();

    public void addEdge(Edge edge) {
        HashMap<Vertex, Edge> directEdges = (HashMap<Vertex, Edge>)this.edges.get(edge.getStart());
        if (directEdges == null) {
            directEdges = new HashMap<Vertex, Edge>();
            this.edges.put(edge.getStart(), directEdges);
        }
        directEdges.put(edge.getEnd(), edge);
    }

    @Override
    public int getPenalty(Vertex start, Vertex end) {
        Edge route;
        Map edgeMap = (Map)this.edges.get(start);
        if (edgeMap != null && (route = (Edge)edgeMap.get(end)) != null) {
            int penalty = route.getPenalty();
            if (penalty < 0) {
                throw new IllegalStateException("Penalty must not be negative");
            }
            return penalty;
        }
        return 0;
    }

    @Override
    public Iterator getDestinations(Vertex origin) {
        Map directRoutes = (Map)this.edges.get(origin);
        if (directRoutes != null) {
            Iterator iter = directRoutes.keySet().iterator();
            return iter;
        }
        return Collections.EMPTY_LIST.iterator();
    }

    public Iterator getEdges(Vertex origin) {
        Map directRoutes = (Map)this.edges.get(origin);
        if (directRoutes != null) {
            Iterator iter = directRoutes.values().iterator();
            return iter;
        }
        return Collections.EMPTY_LIST.iterator();
    }

    public Edge getBestEdge(Vertex start, Vertex end) {
        Edge best = null;
        Iterator iter = this.getEdges(start);
        while (iter.hasNext()) {
            Edge edge = (Edge)iter.next();
            if (!edge.getEnd().equals(end) || best != null && edge.getPenalty() >= best.getPenalty()) continue;
            best = edge;
        }
        return best;
    }
}

