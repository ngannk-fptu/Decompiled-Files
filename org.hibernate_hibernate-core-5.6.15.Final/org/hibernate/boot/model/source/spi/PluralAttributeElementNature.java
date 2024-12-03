/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

public enum PluralAttributeElementNature {
    BASIC(false, false),
    AGGREGATE(false, true),
    ONE_TO_MANY,
    MANY_TO_MANY,
    MANY_TO_ANY;

    private final boolean isAssociation;
    private final boolean isCascadeable;

    private PluralAttributeElementNature() {
        this(true, true);
    }

    private PluralAttributeElementNature(boolean association, boolean cascadeable) {
        this.isAssociation = association;
        this.isCascadeable = cascadeable;
    }

    public boolean isAssociation() {
        return this.isAssociation;
    }

    public boolean isCascadeable() {
        return this.isCascadeable;
    }
}

