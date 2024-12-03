/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TypedQuery
 */
package org.hibernate.query.criteria.internal.compile;

import javax.persistence.TypedQuery;

public interface ImplicitParameterBinding {
    public String getParameterName();

    public Class getJavaType();

    public void bind(TypedQuery var1);
}

