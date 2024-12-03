/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.collection;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

public interface CollectionPersister
extends CollectionDefinition {
    public void initialize(Serializable var1, SharedSessionContractImplementor var2) throws HibernateException;

    public boolean hasCache();

    public CollectionDataAccess getCacheAccessStrategy();

    public NavigableRole getNavigableRole();

    public CacheEntryStructure getCacheEntryStructure();

    @Override
    public CollectionType getCollectionType();

    public Type getKeyType();

    public Type getIndexType();

    public Type getElementType();

    public Class getElementClass();

    public Object readKey(ResultSet var1, String[] var2, SharedSessionContractImplementor var3) throws HibernateException, SQLException;

    public Object readElement(ResultSet var1, Object var2, String[] var3, SharedSessionContractImplementor var4) throws HibernateException, SQLException;

    public Object readIndex(ResultSet var1, String[] var2, SharedSessionContractImplementor var3) throws HibernateException, SQLException;

    public Object readIdentifier(ResultSet var1, String var2, SharedSessionContractImplementor var3) throws HibernateException, SQLException;

    public boolean isPrimitiveArray();

    public boolean isArray();

    public boolean isOneToMany();

    public boolean isManyToMany();

    public String getManyToManyFilterFragment(String var1, Map var2);

    public boolean hasIndex();

    public boolean isLazy();

    public boolean isInverse();

    public void remove(Serializable var1, SharedSessionContractImplementor var2) throws HibernateException;

    public void recreate(PersistentCollection var1, Serializable var2, SharedSessionContractImplementor var3) throws HibernateException;

    public void deleteRows(PersistentCollection var1, Serializable var2, SharedSessionContractImplementor var3) throws HibernateException;

    public void updateRows(PersistentCollection var1, Serializable var2, SharedSessionContractImplementor var3) throws HibernateException;

    public void insertRows(PersistentCollection var1, Serializable var2, SharedSessionContractImplementor var3) throws HibernateException;

    public void processQueuedOps(PersistentCollection var1, Serializable var2, SharedSessionContractImplementor var3) throws HibernateException;

    public String getRole();

    public EntityPersister getOwnerEntityPersister();

    public IdentifierGenerator getIdentifierGenerator();

    public Type getIdentifierType();

    public boolean hasOrphanDelete();

    public boolean hasOrdering();

    public boolean hasManyToManyOrdering();

    public Serializable[] getCollectionSpaces();

    public CollectionMetadata getCollectionMetadata();

    public boolean isCascadeDeleteEnabled();

    public boolean isVersioned();

    public boolean isMutable();

    public void postInstantiate() throws MappingException;

    public SessionFactoryImplementor getFactory();

    public boolean isAffectedByEnabledFilters(SharedSessionContractImplementor var1);

    public String[] getKeyColumnAliases(String var1);

    public String[] getIndexColumnAliases(String var1);

    public String[] getElementColumnAliases(String var1);

    public String getIdentifierColumnAlias(String var1);

    public boolean isExtraLazy();

    public int getSize(Serializable var1, SharedSessionContractImplementor var2);

    public boolean indexExists(Serializable var1, Object var2, SharedSessionContractImplementor var3);

    public boolean elementExists(Serializable var1, Object var2, SharedSessionContractImplementor var3);

    public Object getElementByIndex(Serializable var1, Object var2, SharedSessionContractImplementor var3, Object var4);

    public int getBatchSize();

    public String getMappedByProperty();
}

