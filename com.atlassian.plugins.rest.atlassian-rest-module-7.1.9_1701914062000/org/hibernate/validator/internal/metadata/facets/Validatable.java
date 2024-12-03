/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.metadata.facets;

import org.hibernate.validator.internal.metadata.facets.Cascadable;

public interface Validatable {
    public Iterable<Cascadable> getCascadables();

    public boolean hasCascadables();
}

