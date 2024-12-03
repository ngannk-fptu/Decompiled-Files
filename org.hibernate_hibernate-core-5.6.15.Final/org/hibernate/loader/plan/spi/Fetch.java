/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.type.Type;

public interface Fetch {
    public FetchSource getSource();

    public PropertyPath getPropertyPath();

    public FetchStrategy getFetchStrategy();

    public Type getFetchedType();

    public boolean isNullable();

    public String getAdditionalJoinConditions();

    public String[] toSqlSelectFragments(String var1);
}

