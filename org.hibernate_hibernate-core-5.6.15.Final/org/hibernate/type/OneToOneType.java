/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.EntityType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.TypeFactory;

public class OneToOneType
extends EntityType {
    private final ForeignKeyDirection foreignKeyType;
    private final String propertyName;
    private final String entityName;
    private final boolean constrained;
    private static final Size[] SIZES = new Size[0];

    @Deprecated
    public OneToOneType(TypeFactory.TypeScope scope, String referencedEntityName, ForeignKeyDirection foreignKeyType, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName) {
        this(scope, referencedEntityName, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName);
    }

    @Deprecated
    public OneToOneType(TypeFactory.TypeScope scope, String referencedEntityName, ForeignKeyDirection foreignKeyType, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName) {
        this(scope, referencedEntityName, foreignKeyType, referenceToPrimaryKey, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName, foreignKeyType != ForeignKeyDirection.TO_PARENT);
    }

    public OneToOneType(TypeFactory.TypeScope scope, String referencedEntityName, ForeignKeyDirection foreignKeyType, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName, boolean constrained) {
        super(scope, referencedEntityName, referenceToPrimaryKey, uniqueKeyPropertyName, !lazy, unwrapProxy);
        this.foreignKeyType = foreignKeyType;
        this.propertyName = propertyName;
        this.entityName = entityName;
        this.constrained = constrained;
    }

    public OneToOneType(OneToOneType original, String superTypeEntityName) {
        super(original, superTypeEntityName);
        this.foreignKeyType = original.foreignKeyType;
        this.propertyName = original.propertyName;
        this.entityName = original.entityName;
        this.constrained = original.constrained;
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public boolean isNull(Object owner, SharedSessionContractImplementor session) {
        if (this.propertyName != null) {
            EntityPersister ownerPersister = session.getFactory().getMetamodel().entityPersister(this.entityName);
            Serializable id = session.getContextEntityIdentifier(owner);
            EntityKey entityKey = session.generateEntityKey(id, ownerPersister);
            return session.getPersistenceContextInternal().isPropertyNull(entityKey, this.getPropertyName());
        }
        return false;
    }

    @Override
    public int getColumnSpan(Mapping session) throws MappingException {
        return 0;
    }

    @Override
    public int[] sqlTypes(Mapping session) throws MappingException {
        return ArrayHelper.EMPTY_INT_ARRAY;
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return SIZES;
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return SIZES;
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) {
    }

    @Override
    public boolean isOneToOne() {
        return true;
    }

    @Override
    public boolean isDirty(Object old, Object current, SharedSessionContractImplementor session) {
        return false;
    }

    @Override
    public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) {
        return false;
    }

    @Override
    public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) {
        return false;
    }

    @Override
    public ForeignKeyDirection getForeignKeyDirection() {
        return this.foreignKeyType;
    }

    @Override
    public Object hydrate(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return session.getContextEntityIdentifier(owner);
    }

    @Override
    public boolean isNullable() {
        return !this.constrained;
    }

    @Override
    public NotFoundAction getNotFoundAction() {
        return null;
    }

    @Override
    public boolean useLHSPrimaryKey() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return null;
    }

    @Override
    public Object assemble(Serializable oid, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return this.resolve(session.getContextEntityIdentifier(owner), session, owner);
    }

    @Override
    public boolean isAlwaysDirtyChecked() {
        return false;
    }
}

