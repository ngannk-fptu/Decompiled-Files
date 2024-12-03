/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.Subgraph
 */
package org.hibernate.graph;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Subgraph;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.graph.Graph;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.spi.GraphImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;

public final class GraphParser {
    public static <T> RootGraph<T> parse(Class<T> rootType, CharSequence graphText, EntityManager entityManager) {
        return GraphParser.parse(rootType, graphText, (SessionImplementor)entityManager);
    }

    private static <T> RootGraphImplementor<T> parse(Class<T> rootType, CharSequence graphText, SessionImplementor session) {
        if (graphText == null) {
            return null;
        }
        RootGraph graph = session.createEntityGraph((Class)rootType);
        GraphParser.parseInto(graph, graphText, session.getSessionFactory());
        return graph;
    }

    public static <T> void parseInto(Graph<T> graph, CharSequence graphText, EntityManager entityManager) {
        GraphParser.parseInto((GraphImplementor)graph, graphText, ((SessionImplementor)entityManager).getSessionFactory());
    }

    public static <T> void parseInto(EntityGraph<T> graph, CharSequence graphText, EntityManager entityManager) {
        GraphParser.parseInto((GraphImplementor)graph, graphText, ((SessionImplementor)entityManager).getSessionFactory());
    }

    public static <T> void parseInto(Subgraph<T> graph, CharSequence graphText, EntityManager entityManager) {
        GraphParser.parseInto((GraphImplementor)graph, graphText, ((SessionImplementor)entityManager).getSessionFactory());
    }

    public static <T> void parseInto(Graph<T> graph, CharSequence graphText, EntityManagerFactory entityManagerFactory) {
        GraphParser.parseInto((GraphImplementor)graph, graphText, (SessionFactoryImplementor)entityManagerFactory);
    }

    public static <T> void parseInto(EntityGraph<T> graph, CharSequence graphText, EntityManagerFactory entityManagerFactory) {
        GraphParser.parseInto((GraphImplementor)graph, graphText, (SessionFactoryImplementor)entityManagerFactory);
    }

    public static <T> void parseInto(Subgraph<T> graph, CharSequence graphText, EntityManagerFactory entityManagerFactory) {
        GraphParser.parseInto((GraphImplementor)graph, graphText, (SessionFactoryImplementor)entityManagerFactory);
    }

    private static <T> void parseInto(GraphImplementor<T> graph, CharSequence graphText, SessionFactoryImplementor sessionFactory) {
        if (graphText != null) {
            org.hibernate.graph.internal.parse.GraphParser.parseInto(graph, graphText, sessionFactory);
        }
    }
}

