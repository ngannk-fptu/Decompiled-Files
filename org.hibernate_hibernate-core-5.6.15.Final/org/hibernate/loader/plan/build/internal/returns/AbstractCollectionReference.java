/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import java.io.Serializable;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.CollectionFetchableElementAnyGraph;
import org.hibernate.loader.plan.build.internal.returns.CollectionFetchableElementCompositeGraph;
import org.hibernate.loader.plan.build.internal.returns.CollectionFetchableElementEntityGraph;
import org.hibernate.loader.plan.build.internal.returns.CollectionFetchableIndexAnyGraph;
import org.hibernate.loader.plan.build.internal.returns.CollectionFetchableIndexCompositeGraph;
import org.hibernate.loader.plan.build.internal.returns.CollectionFetchableIndexEntityGraph;
import org.hibernate.loader.plan.build.internal.spaces.CompositePropertyMapping;
import org.hibernate.loader.plan.build.internal.spaces.QuerySpaceHelper;
import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.spi.CollectionFetchableElement;
import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

public abstract class AbstractCollectionReference
implements CollectionReference {
    private final ExpandingCollectionQuerySpace collectionQuerySpace;
    private final PropertyPath propertyPath;
    private final CollectionFetchableIndex index;
    private final CollectionFetchableElement element;
    private final boolean allowElementJoin;
    private final boolean allowIndexJoin;

    protected AbstractCollectionReference(ExpandingCollectionQuerySpace collectionQuerySpace, PropertyPath propertyPath, boolean shouldIncludeJoins) {
        String[] indexFormulas;
        int nNonNullFormulas;
        this.collectionQuerySpace = collectionQuerySpace;
        this.propertyPath = propertyPath;
        this.allowElementJoin = shouldIncludeJoins;
        this.allowIndexJoin = shouldIncludeJoins && collectionQuerySpace.getCollectionPersister().hasIndex() && collectionQuerySpace.getCollectionPersister().getIndexType().isEntityType() ? (nNonNullFormulas = ArrayHelper.countNonNull((Serializable[])(indexFormulas = ((QueryableCollection)collectionQuerySpace.getCollectionPersister()).getIndexFormulas()))) == 0 : false;
        this.index = this.buildIndexGraph();
        this.element = this.buildElementGraph();
    }

    private CollectionFetchableIndex buildIndexGraph() {
        CollectionPersister persister = this.collectionQuerySpace.getCollectionPersister();
        if (persister.hasIndex()) {
            Type type = persister.getIndexType();
            if (type.isAssociationType()) {
                if (type.isEntityType()) {
                    EntityPersister indexPersister = persister.getFactory().getEntityPersister(((EntityType)type).getAssociatedEntityName());
                    ExpandingEntityQuerySpace entityQuerySpace = QuerySpaceHelper.INSTANCE.makeEntityQuerySpace(this.collectionQuerySpace, indexPersister, "indices", (EntityType)persister.getIndexType(), this.collectionQuerySpace.getExpandingQuerySpaces().generateImplicitUid(), this.collectionQuerySpace.canJoinsBeRequired(), this.allowIndexJoin);
                    return new CollectionFetchableIndexEntityGraph(this, entityQuerySpace);
                }
                if (type.isAnyType()) {
                    return new CollectionFetchableIndexAnyGraph(this);
                }
            } else if (type.isComponentType()) {
                ExpandingCompositeQuerySpace compositeQuerySpace = QuerySpaceHelper.INSTANCE.makeCompositeQuerySpace(this.collectionQuerySpace, new CompositePropertyMapping((CompositeType)persister.getIndexType(), (PropertyMapping)((Object)persister), ""), "indices", (CompositeType)persister.getIndexType(), this.collectionQuerySpace.getExpandingQuerySpaces().generateImplicitUid(), this.collectionQuerySpace.canJoinsBeRequired(), this.allowIndexJoin);
                return new CollectionFetchableIndexCompositeGraph(this, compositeQuerySpace);
            }
        }
        return null;
    }

    private CollectionFetchableElement buildElementGraph() {
        CollectionPersister persister = this.collectionQuerySpace.getCollectionPersister();
        Type type = persister.getElementType();
        if (type.isAssociationType()) {
            if (type.isEntityType()) {
                EntityPersister elementPersister = persister.getFactory().getEntityPersister(((EntityType)type).getAssociatedEntityName());
                ExpandingEntityQuerySpace entityQuerySpace = QuerySpaceHelper.INSTANCE.makeEntityQuerySpace(this.collectionQuerySpace, elementPersister, "elements", (EntityType)persister.getElementType(), this.collectionQuerySpace.getExpandingQuerySpaces().generateImplicitUid(), this.collectionQuerySpace.canJoinsBeRequired(), this.allowElementJoin);
                return new CollectionFetchableElementEntityGraph(this, entityQuerySpace);
            }
            if (type.isAnyType()) {
                return new CollectionFetchableElementAnyGraph(this);
            }
        } else if (type.isComponentType()) {
            ExpandingCompositeQuerySpace compositeQuerySpace = QuerySpaceHelper.INSTANCE.makeCompositeQuerySpace(this.collectionQuerySpace, new CompositePropertyMapping((CompositeType)persister.getElementType(), (PropertyMapping)((Object)persister), ""), "elements", (CompositeType)persister.getElementType(), this.collectionQuerySpace.getExpandingQuerySpaces().generateImplicitUid(), this.collectionQuerySpace.canJoinsBeRequired(), this.allowElementJoin);
            return new CollectionFetchableElementCompositeGraph(this, compositeQuerySpace);
        }
        return null;
    }

    @Override
    public boolean allowElementJoin() {
        return this.allowElementJoin;
    }

    @Override
    public boolean allowIndexJoin() {
        return this.allowIndexJoin;
    }

    @Override
    public String getQuerySpaceUid() {
        return this.collectionQuerySpace.getUid();
    }

    @Override
    public CollectionPersister getCollectionPersister() {
        return this.collectionQuerySpace.getCollectionPersister();
    }

    @Override
    public CollectionFetchableIndex getIndexGraph() {
        return this.index;
    }

    @Override
    public CollectionFetchableElement getElementGraph() {
        return this.element;
    }

    @Override
    public PropertyPath getPropertyPath() {
        return this.propertyPath;
    }
}

