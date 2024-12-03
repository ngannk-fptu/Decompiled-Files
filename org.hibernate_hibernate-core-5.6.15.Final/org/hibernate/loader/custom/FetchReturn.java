/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.custom;

import org.hibernate.LockMode;
import org.hibernate.loader.custom.NonScalarReturn;

public abstract class FetchReturn
extends NonScalarReturn {
    private final NonScalarReturn owner;
    private final String ownerProperty;

    public FetchReturn(NonScalarReturn owner, String ownerProperty, String alias, LockMode lockMode) {
        super(alias, lockMode);
        this.owner = owner;
        this.ownerProperty = ownerProperty;
    }

    public NonScalarReturn getOwner() {
        return this.owner;
    }

    public String getOwnerProperty() {
        return this.ownerProperty;
    }
}

