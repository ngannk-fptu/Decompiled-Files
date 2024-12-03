/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 */
package org.springframework.orm.jpa;

import javax.persistence.EntityManager;

public interface EntityManagerProxy
extends EntityManager {
    public EntityManager getTargetEntityManager() throws IllegalStateException;
}

