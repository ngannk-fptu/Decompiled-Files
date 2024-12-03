/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 */
package com.atlassian.confluence.persistence;

import javax.persistence.EntityManager;

public interface EntityManagerProvider {
    public EntityManager getEntityManager();
}

