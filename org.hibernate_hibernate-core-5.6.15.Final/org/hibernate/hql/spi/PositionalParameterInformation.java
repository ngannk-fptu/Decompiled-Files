/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi;

import org.hibernate.hql.spi.ParameterInformation;

public interface PositionalParameterInformation
extends ParameterInformation {
    public int getLabel();
}

