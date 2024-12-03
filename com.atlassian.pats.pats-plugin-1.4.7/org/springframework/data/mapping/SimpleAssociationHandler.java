/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.mapping;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;

public interface SimpleAssociationHandler {
    public void doWithAssociation(Association<? extends PersistentProperty<?>> var1);
}

