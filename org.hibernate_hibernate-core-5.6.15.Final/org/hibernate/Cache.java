/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Cache
 */
package org.hibernate;

import java.io.Serializable;
import org.hibernate.SessionFactory;

public interface Cache
extends javax.persistence.Cache {
    public SessionFactory getSessionFactory();

    public boolean containsEntity(Class var1, Serializable var2);

    public boolean containsEntity(String var1, Serializable var2);

    public void evictEntityData(Class var1, Serializable var2);

    public void evictEntityData(String var1, Serializable var2);

    public void evictEntityData(Class var1);

    public void evictEntityData(String var1);

    public void evictEntityData();

    public void evictNaturalIdData(Class var1);

    public void evictNaturalIdData(String var1);

    public void evictNaturalIdData();

    public boolean containsCollection(String var1, Serializable var2);

    public void evictCollectionData(String var1, Serializable var2);

    public void evictCollectionData(String var1);

    public void evictCollectionData();

    public boolean containsQuery(String var1);

    public void evictDefaultQueryRegion();

    public void evictQueryRegion(String var1);

    public void evictQueryRegions();

    public void evictRegion(String var1);

    default public void evictAll() {
        this.evictEntityData();
    }

    default public void evictAllRegions() {
        this.evictEntityData();
        this.evictNaturalIdData();
        this.evictCollectionData();
        this.evictDefaultQueryRegion();
        this.evictQueryRegions();
    }

    @Deprecated
    default public void evictEntity(Class entityClass, Serializable identifier) {
        this.evictEntityData(entityClass, identifier);
    }

    @Deprecated
    default public void evictEntity(String entityName, Serializable identifier) {
        this.evictEntityData(entityName, identifier);
    }

    @Deprecated
    default public void evictEntityRegion(Class entityClass) {
        this.evictEntityData(entityClass);
    }

    @Deprecated
    default public void evictEntityRegion(String entityName) {
        this.evictEntityData(entityName);
    }

    @Deprecated
    default public void evictEntityRegions() {
        this.evictEntityData();
    }

    @Deprecated
    default public void evictNaturalIdRegion(Class entityClass) {
        this.evictNaturalIdData(entityClass);
    }

    @Deprecated
    default public void evictNaturalIdRegion(String entityName) {
        this.evictNaturalIdData(entityName);
    }

    @Deprecated
    default public void evictNaturalIdRegions() {
        this.evictNaturalIdData();
    }

    @Deprecated
    default public void evictCollection(String role, Serializable ownerIdentifier) {
        this.evictCollectionData(role, ownerIdentifier);
    }

    @Deprecated
    default public void evictCollectionRegion(String role) {
        this.evictCollectionData(role);
    }

    @Deprecated
    default public void evictCollectionRegions() {
        this.evictCollectionData();
    }
}

