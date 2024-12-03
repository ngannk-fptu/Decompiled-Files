/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 */
package org.hibernate.jpa.spi;

import javax.persistence.TemporalType;

public interface ParameterBind<T> {
    public T getValue();

    public TemporalType getSpecifiedTemporalType();
}

