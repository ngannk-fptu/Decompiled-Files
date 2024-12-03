/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.Join;
import org.hibernate.loader.plan.spi.QuerySpaces;
import org.hibernate.persister.entity.PropertyMapping;

public interface QuerySpace {
    public String getUid();

    public QuerySpaces getQuerySpaces();

    public PropertyMapping getPropertyMapping();

    public String[] toAliasedColumns(String var1, String var2);

    public Disposition getDisposition();

    public Iterable<Join> getJoins();

    public static enum Disposition {
        ENTITY,
        COLLECTION,
        COMPOSITE;

    }
}

