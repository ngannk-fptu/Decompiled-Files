/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.spi;

import java.io.Serializable;

@Deprecated
public interface PermissionCheckEntityInformation {
    public Object getEntity();

    public String getEntityName();

    public Serializable getIdentifier();
}

