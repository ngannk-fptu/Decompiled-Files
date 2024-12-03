/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeNode
 *  javax.persistence.EntityGraph
 *  javax.persistence.EntityManager
 *  javax.persistence.Query
 *  javax.persistence.Subgraph
 *  javax.persistence.TypedQuery
 */
package org.hibernate.graph;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.graph.Graph;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.spi.GraphImplementor;

public final class EntityGraphs {
    public static <T> EntityGraph<T> merge(EntityManager em, Class<T> rootType, EntityGraph<T> ... graphs) {
        return EntityGraphs.merge((SessionImplementor)em, rootType, (Object[])graphs);
    }

    @SafeVarargs
    public static <T> EntityGraph<T> merge(Session session, Class<T> rootType, Graph<T> ... graphs) {
        return EntityGraphs.merge((SessionImplementor)session, rootType, (Object[])graphs);
    }

    @SafeVarargs
    public static <T> EntityGraph<T> merge(SessionImplementor session, Class<T> rootType, GraphImplementor<T> ... graphs) {
        return EntityGraphs.merge(session, rootType, (Object[])graphs);
    }

    private static <T> EntityGraph<T> merge(SessionImplementor session, Class<T> rootType, Object ... graphs) {
        RootGraph merged = session.createEntityGraph((Class)rootType);
        if (graphs != null) {
            for (Object graph : graphs) {
                merged.merge(new GraphImplementor[]{(GraphImplementor)graph});
            }
        }
        return merged;
    }

    public static List executeList(Query query, EntityGraph graph, GraphSemantic semantic) {
        return ((org.hibernate.query.Query)query.unwrap(org.hibernate.query.Query.class)).applyGraph((RootGraph)graph, semantic).list();
    }

    public static <R> List<R> executeList(TypedQuery<R> query, EntityGraph<R> graph, GraphSemantic semantic) {
        return EntityGraphs.executeList(query, graph, semantic);
    }

    public static List executeList(Query query, EntityGraph graph, String semanticJpaHintName) {
        return ((org.hibernate.query.Query)query.unwrap(org.hibernate.query.Query.class)).applyGraph((RootGraph)graph, GraphSemantic.fromJpaHintName(semanticJpaHintName)).list();
    }

    public static <R> List<R> executeList(TypedQuery<R> query, EntityGraph<R> graph, String semanticJpaHintName) {
        return EntityGraphs.executeList(query, graph, semanticJpaHintName);
    }

    public static List executeList(Query query, EntityGraph graph) {
        return ((org.hibernate.query.Query)query.unwrap(org.hibernate.query.Query.class)).applyFetchGraph((RootGraph)graph).list();
    }

    public static <R> List<R> executeList(TypedQuery<R> query, EntityGraph<R> graph) {
        return EntityGraphs.executeList(query, graph, GraphSemantic.FETCH);
    }

    public static <T> boolean areEqual(EntityGraph<T> a, EntityGraph<T> b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        List aNodes = a.getAttributeNodes();
        List bNodes = b.getAttributeNodes();
        if (aNodes.size() != bNodes.size()) {
            return false;
        }
        for (AttributeNode aNode : aNodes) {
            String attributeName = aNode.getAttributeName();
            AttributeNode bNode = null;
            for (AttributeNode bCandidate : bNodes) {
                if (!attributeName.equals(bCandidate.getAttributeName())) continue;
                bNode = bCandidate;
                break;
            }
            if (EntityGraphs.areEqual(aNode, bNode)) continue;
            return false;
        }
        return true;
    }

    public static boolean areEqual(AttributeNode<?> a, AttributeNode<?> b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.getAttributeName().equals(b.getAttributeName())) {
            return EntityGraphs.areEqual(a.getSubgraphs(), b.getSubgraphs()) && EntityGraphs.areEqual(a.getKeySubgraphs(), b.getKeySubgraphs());
        }
        return false;
    }

    public static boolean areEqual(Map<Class, Subgraph> a, Map<Class, Subgraph> b) {
        Set<Class> bKeys;
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        Set<Class> aKeys = a.keySet();
        if (aKeys.equals(bKeys = b.keySet())) {
            for (Class clazz : aKeys) {
                if (!bKeys.contains(clazz)) {
                    return false;
                }
                if (EntityGraphs.areEqual(a.get(clazz), b.get(clazz))) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean areEqual(Subgraph a, Subgraph b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.getClassType() != b.getClassType()) {
            return false;
        }
        List aNodes = a.getAttributeNodes();
        List bNodes = b.getAttributeNodes();
        if (aNodes.size() != bNodes.size()) {
            return false;
        }
        for (AttributeNode aNode : aNodes) {
            String attributeName = aNode.getAttributeName();
            AttributeNode bNode = null;
            for (AttributeNode bCandidate : bNodes) {
                if (!attributeName.equals(bCandidate.getAttributeName())) continue;
                bNode = bCandidate;
                break;
            }
            if (EntityGraphs.areEqual(aNode, bNode)) continue;
            return false;
        }
        return true;
    }
}

