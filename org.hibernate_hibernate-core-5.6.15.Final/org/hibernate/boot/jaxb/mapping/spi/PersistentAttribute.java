/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.AccessType;

public interface PersistentAttribute {
    public String getName();

    public AccessType getAccess();

    public void setAccess(AccessType var1);
}

