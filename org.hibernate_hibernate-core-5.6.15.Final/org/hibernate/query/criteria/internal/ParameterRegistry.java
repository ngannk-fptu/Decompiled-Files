/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.ParameterExpression
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.ParameterExpression;

public interface ParameterRegistry {
    public void registerParameter(ParameterExpression<?> var1);
}

