/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.TypeFactory;

public class SpecialOneToOneType
extends OneToOneType {
    @Deprecated
    public SpecialOneToOneType(TypeFactory.TypeScope scope, String referencedEntityName, ForeignKeyDirection foreignKeyType, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName) {
        this(scope, referencedEntityName, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName);
    }

    @Deprecated
    public SpecialOneToOneType(TypeFactory.TypeScope scope, String referencedEntityName, ForeignKeyDirection foreignKeyType, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName) {
        this(scope, referencedEntityName, foreignKeyType, referenceToPrimaryKey, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName, foreignKeyType != ForeignKeyDirection.TO_PARENT);
    }

    public SpecialOneToOneType(TypeFactory.TypeScope scope, String referencedEntityName, ForeignKeyDirection foreignKeyType, boolean referenceToPrimaryKey, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, String entityName, String propertyName, boolean constrained) {
        super(scope, referencedEntityName, foreignKeyType, referenceToPrimaryKey, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName, constrained);
    }

    public SpecialOneToOneType(SpecialOneToOneType original, String superTypeEntityName) {
        super(original, superTypeEntityName);
    }

    @Override
    public int getColumnSpan(Mapping mapping) throws MappingException {
        return super.getIdentifierOrUniqueKeyType(mapping).getColumnSpan(mapping);
    }

    @Override
    public int[] sqlTypes(Mapping mapping) throws MappingException {
        return super.getIdentifierOrUniqueKeyType(mapping).sqlTypes(mapping);
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return super.getIdentifierOrUniqueKeyType(mapping).dictatedSizes(mapping);
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return super.getIdentifierOrUniqueKeyType(mapping).defaultSizes(mapping);
    }

    @Override
    public boolean useLHSPrimaryKey() {
        return false;
    }

    @Override
    public Object hydrate(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return super.getIdentifierOrUniqueKeyType(session.getFactory()).nullSafeGet(rs, names, session, owner);
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (value == null) {
            return null;
        }
        Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved(this.getAssociatedEntityName(), value, session);
        if (id == null) {
            throw new AssertionFailure("cannot cache a reference to an object with a null id: " + this.getAssociatedEntityName());
        }
        return this.getIdentifierType(session).disassemble(id, session, owner);
    }

    @Override
    public Object assemble(Serializable oid, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        Serializable id = (Serializable)this.getIdentifierType(session).assemble(oid, session, null);
        if (id == null) {
            return null;
        }
        return this.resolveIdentifier(id, session);
    }
}

