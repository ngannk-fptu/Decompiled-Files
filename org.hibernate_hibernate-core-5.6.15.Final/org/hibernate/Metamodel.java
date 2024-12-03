/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.EntityType
 *  javax.persistence.metamodel.Metamodel
 */
package org.hibernate;

import javax.persistence.metamodel.EntityType;
import org.hibernate.SessionFactory;

public interface Metamodel
extends javax.persistence.metamodel.Metamodel {
    public SessionFactory getSessionFactory();

    @Deprecated
    default public EntityType getEntityTypeByName(String entityName) {
        return this.entity(entityName);
    }

    public <X> EntityType<X> entity(String var1);

    public String getImportedClassName(String var1);

    public String[] getImplementors(String var1);
}

