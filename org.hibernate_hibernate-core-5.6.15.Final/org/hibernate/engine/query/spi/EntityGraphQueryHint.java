/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  javax.persistence.AttributeNode
 *  javax.persistence.EntityGraph
 *  javax.persistence.Subgraph
 */
package org.hibernate.engine.query.spi;

import antlr.SemanticException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.Subgraph;
import org.hibernate.QueryException;
import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.spi.AppliedGraph;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.FromClause;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromElementFactory;
import org.hibernate.hql.internal.ast.tree.ImpliedFromElement;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.sql.JoinType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class EntityGraphQueryHint
implements AppliedGraph {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(EntityGraphQueryHint.class);
    private final RootGraphImplementor<?> graph;
    private final GraphSemantic semantic;

    public EntityGraphQueryHint(String hintName, EntityGraph<?> graph) {
        assert (hintName != null);
        this.semantic = GraphSemantic.fromJpaHintName(hintName);
        this.graph = (RootGraphImplementor)graph;
    }

    public EntityGraphQueryHint(RootGraphImplementor<?> graph, GraphSemantic semantic) {
        this.semantic = semantic;
        this.graph = graph;
    }

    @Override
    public GraphSemantic getSemantic() {
        return this.semantic;
    }

    @Override
    public RootGraphImplementor<?> getGraph() {
        return this.graph;
    }

    public List<FromElement> toFromElements(FromClause fromClause, HqlSqlWalker walker) {
        String fromElementEntityName;
        HashMap<String, FromElement> explicitFetches = new HashMap<String, FromElement>();
        for (Object o : fromClause.getFromElements()) {
            FromElement fromElement = (FromElement)o;
            if (fromElement.getRole() == null || fromElement instanceof ImpliedFromElement) continue;
            explicitFetches.put(fromElement.getRole(), fromElement);
        }
        boolean applyEntityGraph = false;
        if (fromClause.getLevel() == 1 && !(applyEntityGraph = this.graph.appliesTo(fromElementEntityName = fromClause.getFromElement().getEntityPersister().getEntityName()))) {
            LOG.warnf("Entity graph is not applicable to the root entity [%s]; Ignored.", fromElementEntityName);
        }
        return this.getFromElements(applyEntityGraph ? this.graph.getAttributeNodes() : Collections.emptyList(), fromClause.getFromElement(), fromClause, walker, explicitFetches);
    }

    private List<FromElement> getFromElements(List attributeNodes, FromElement origin, FromClause fromClause, HqlSqlWalker walker, Map<String, FromElement> explicitFetches) {
        ArrayList<FromElement> fromElements = new ArrayList<FromElement>();
        for (Object obj : attributeNodes) {
            AttributeNode attributeNode = (AttributeNode)obj;
            String attributeName = attributeNode.getAttributeName();
            String className = origin.getClassName();
            String role = className + "." + attributeName;
            String classAlias = origin.getClassAlias();
            String originTableAlias = origin.getTableAlias();
            Type propertyType = origin.getPropertyType(attributeName, attributeName);
            try {
                FromElement fromElement = explicitFetches.get(role);
                boolean explicitFromElement = false;
                if (fromElement == null) {
                    String[] columns;
                    if (propertyType.isEntityType()) {
                        EntityType entityType = (EntityType)propertyType;
                        columns = origin.toColumns(originTableAlias, attributeName, false);
                        String tableAlias = walker.getAliasGenerator().createName(entityType.getAssociatedEntityName());
                        FromElementFactory fromElementFactory = new FromElementFactory(fromClause, origin, attributeName, classAlias, columns, false);
                        JoinSequence joinSequence = walker.getSessionFactoryHelper().createJoinSequence(false, (AssociationType)entityType, tableAlias, JoinType.LEFT_OUTER_JOIN, columns);
                        fromElement = fromElementFactory.createEntityJoin(entityType.getAssociatedEntityName(), tableAlias, joinSequence, true, walker.isInFrom(), entityType, role, null);
                    } else if (propertyType.isCollectionType()) {
                        CollectionType collectionType = (CollectionType)propertyType;
                        columns = origin.toColumns(originTableAlias, attributeName, false);
                        FromElementFactory fromElementFactory = new FromElementFactory(fromClause, origin, attributeName, classAlias, columns, false);
                        QueryableCollection queryableCollection = walker.getSessionFactoryHelper().requireQueryableCollection(collectionType.getRole());
                        fromElement = fromElementFactory.createCollection(queryableCollection, collectionType.getRole(), JoinType.LEFT_OUTER_JOIN, true, false);
                    }
                } else {
                    explicitFromElement = true;
                    fromElement.setInProjectionList(true);
                    fromElement.setFetch(true);
                }
                if (fromElement == null) continue;
                if (!explicitFromElement) {
                    fromElements.add(fromElement);
                }
                for (Subgraph subgraph : attributeNode.getSubgraphs().values()) {
                    fromElements.addAll(this.getFromElements(subgraph.getAttributeNodes(), fromElement, fromClause, walker, explicitFetches));
                }
            }
            catch (SemanticException e) {
                throw new QueryException("Could not apply the EntityGraph to the Query!", (Exception)((Object)e));
            }
        }
        return fromElements;
    }

    @Deprecated
    public EntityGraph<?> getOriginEntityGraph() {
        return this.getGraph();
    }

    @Deprecated
    public GraphSemantic getGraphSemantic() {
        return this.getSemantic();
    }

    @Deprecated
    public String getHintName() {
        return this.getGraphSemantic().getJpaHintName();
    }
}

