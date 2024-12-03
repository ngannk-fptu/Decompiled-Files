/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class FilterQueryPlan
extends HQLQueryPlan
implements Serializable {
    private final String collectionRole;

    public FilterQueryPlan(String hql, String collectionRole, boolean shallow, Map enabledFilters, SessionFactoryImplementor factory) {
        super(hql, collectionRole, shallow, enabledFilters, factory, null);
        this.collectionRole = collectionRole;
    }

    public String getCollectionRole() {
        return this.collectionRole;
    }
}

