/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Parameter
 */
package org.hibernate.query;

import javax.persistence.Parameter;
import org.hibernate.Incubating;
import org.hibernate.type.Type;

@Incubating
public interface QueryParameter<T>
extends Parameter<T> {
    public Type getHibernateType();

    public int[] getSourceLocations();
}

