/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import org.hibernate.HibernateException;
import org.hibernate.action.internal.EntityAction;

public class EntityActionVetoException
extends HibernateException {
    private final EntityAction entityAction;

    public EntityActionVetoException(String message, EntityAction entityAction) {
        super(message);
        this.entityAction = entityAction;
    }

    public EntityAction getEntityAction() {
        return this.entityAction;
    }
}

