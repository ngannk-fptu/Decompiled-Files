/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.spi;

import org.hibernate.jpa.spi.ParameterRegistration;

public interface StoredProcedureQueryParameterRegistration<T>
extends ParameterRegistration<T> {
    public boolean isPassNullsEnabled();

    public void enablePassingNulls(boolean var1);
}

