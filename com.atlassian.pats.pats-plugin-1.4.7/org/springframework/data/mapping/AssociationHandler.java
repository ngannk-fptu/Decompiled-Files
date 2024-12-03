/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;

public interface AssociationHandler<P extends PersistentProperty<P>> {
    public void doWithAssociation(Association<P> var1);
}

