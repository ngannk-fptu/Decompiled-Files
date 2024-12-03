/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.BasicType
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.metamodel.model.domain;

import java.util.Objects;
import javax.persistence.metamodel.BasicType;
import javax.persistence.metamodel.Type;
import org.hibernate.HibernateException;
import org.hibernate.metamodel.model.domain.SimpleDomainType;

public interface BasicDomainType<J>
extends SimpleDomainType<J>,
BasicType<J> {
    default public Type.PersistenceType getPersistenceType() {
        return Type.PersistenceType.BASIC;
    }

    default public boolean areEqual(J x, J y) throws HibernateException {
        return Objects.equals(x, y);
    }
}

