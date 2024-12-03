/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 */
package org.hibernate.query.procedure;

import javax.persistence.ParameterMode;
import org.hibernate.Incubating;
import org.hibernate.query.QueryParameter;

@Incubating
public interface ProcedureParameter<T>
extends QueryParameter<T> {
    public ParameterMode getMode();

    public boolean isPassNullsEnabled();

    @Deprecated
    public void enablePassingNulls(boolean var1);
}

