/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AssociationType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

public abstract class AbstractType
implements Type {
    protected static final Size LEGACY_DICTATED_SIZE = new Size();
    protected static final Size LEGACY_DEFAULT_SIZE = new Size(19, 2, 255L, Size.LobMultiplier.NONE);

    @Override
    public boolean isAssociationType() {
        return false;
    }

    @Override
    public boolean isCollectionType() {
        return false;
    }

    @Override
    public boolean isComponentType() {
        return false;
    }

    @Override
    public boolean isEntityType() {
        return false;
    }

    @Override
    public int compare(Object x, Object y) {
        return ((Comparable)x).compareTo(y);
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (value == null) {
            return null;
        }
        return (Serializable)this.deepCopy(value, session.getFactory());
    }

    @Override
    public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (cached == null) {
            return null;
        }
        return this.deepCopy(cached, session.getFactory());
    }

    @Override
    public boolean isDirty(Object old, Object current, SharedSessionContractImplementor session) throws HibernateException {
        return !this.isSame(old, current);
    }

    @Override
    public Object hydrate(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.nullSafeGet(rs, names, session, owner);
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return value;
    }

    @Override
    public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return value;
    }

    @Override
    public boolean isAnyType() {
        return false;
    }

    @Override
    public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        return this.isDirty(old, current, session);
    }

    @Override
    public boolean isSame(Object x, Object y) throws HibernateException {
        return this.isEqual(x, y);
    }

    @Override
    public boolean isEqual(Object x, Object y) {
        return Objects.equals(x, y);
    }

    @Override
    public int getHashCode(Object x) {
        return x.hashCode();
    }

    @Override
    public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
        return this.isEqual(x, y);
    }

    @Override
    public int getHashCode(Object x, SessionFactoryImplementor factory) {
        return this.getHashCode(x);
    }

    @Override
    public Type getSemiResolvedType(SessionFactoryImplementor factory) {
        return this;
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache, ForeignKeyDirection foreignKeyDirection) throws HibernateException {
        AssociationType atype;
        boolean include = this.isAssociationType() ? (atype = (AssociationType)((Object)this)).getForeignKeyDirection() == foreignKeyDirection : ForeignKeyDirection.FROM_PARENT == foreignKeyDirection;
        return include ? this.replace(original, target, session, owner, copyCache) : target;
    }

    @Override
    public void beforeAssemble(Serializable cached, SharedSessionContractImplementor session) {
    }
}

