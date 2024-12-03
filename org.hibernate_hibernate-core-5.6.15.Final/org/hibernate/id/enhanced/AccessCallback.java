/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.enhanced;

import org.hibernate.id.IntegralDataTypeHolder;

public interface AccessCallback {
    public IntegralDataTypeHolder getNextValue();

    public String getTenantIdentifier();
}

