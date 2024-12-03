/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.engine.internal.JoinSequence;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.type.AssociationType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public class MapKeyEntityFromElement
extends FromElement {
    private final boolean useThetaJoin;

    public MapKeyEntityFromElement(boolean useThetaJoin) {
        this.useThetaJoin = useThetaJoin;
    }

    @Override
    public boolean isImplied() {
        return this.useThetaJoin;
    }

    public int getType() {
        return this.useThetaJoin ? 141 : 143;
    }

    public static MapKeyEntityFromElement buildKeyJoin(FromElement collectionFromElement) {
        HqlSqlWalker walker = collectionFromElement.getWalker();
        SessionFactoryHelper sfh = walker.getSessionFactoryHelper();
        SessionFactoryImplementor sf = sfh.getFactory();
        QueryableCollection collectionPersister = collectionFromElement.getQueryableCollection();
        Type indexType = collectionPersister.getIndexType();
        if (indexType == null) {
            throw new IllegalArgumentException("Given collection is not indexed");
        }
        if (!indexType.isEntityType()) {
            throw new IllegalArgumentException("Given collection does not have an entity index");
        }
        EntityType indexEntityType = (EntityType)indexType;
        EntityPersister indexEntityPersister = (EntityPersister)((Object)indexEntityType.getAssociatedJoinable(sf));
        String rhsAlias = walker.getAliasGenerator().createName(indexEntityPersister.getEntityName());
        boolean useThetaJoin = collectionFromElement.getJoinSequence().isThetaStyle();
        MapKeyEntityFromElement join = new MapKeyEntityFromElement(useThetaJoin);
        join.initialize(143, ((Joinable)((Object)indexEntityPersister)).getTableName());
        join.initialize(collectionFromElement.getWalker());
        join.initializeEntity(collectionFromElement.getFromClause(), indexEntityPersister.getEntityName(), indexEntityPersister, indexEntityType, "<map-key-join-" + collectionFromElement.getClassAlias() + ">", rhsAlias);
        String[] joinColumns = collectionPersister.getIndexColumnNames(collectionFromElement.getCollectionTableAlias());
        JoinSequence joinSequence = sfh.createJoinSequence(useThetaJoin, (AssociationType)indexEntityType, rhsAlias, collectionFromElement.getJoinSequence().getFirstJoin().getJoinType(), joinColumns);
        join.setJoinSequence(joinSequence);
        join.setOrigin(collectionFromElement, true);
        join.setColumns(joinColumns);
        join.setUseFromFragment(collectionFromElement.useFromFragment());
        join.setUseWhereFragment(collectionFromElement.useWhereFragment());
        walker.addQuerySpaces(indexEntityPersister.getQuerySpaces());
        return join;
    }
}

