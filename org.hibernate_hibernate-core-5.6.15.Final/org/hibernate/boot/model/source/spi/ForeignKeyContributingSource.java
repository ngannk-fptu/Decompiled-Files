/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

public interface ForeignKeyContributingSource {
    public String getExplicitForeignKeyName();

    public boolean createForeignKeyConstraint();

    public boolean isCascadeDeleteEnabled();
}

