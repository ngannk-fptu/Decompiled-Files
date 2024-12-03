/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Projection;

public interface EnhancedProjection
extends Projection {
    public String[] getColumnAliases(int var1, Criteria var2, CriteriaQuery var3);

    public String[] getColumnAliases(String var1, int var2, Criteria var3, CriteriaQuery var4);
}

