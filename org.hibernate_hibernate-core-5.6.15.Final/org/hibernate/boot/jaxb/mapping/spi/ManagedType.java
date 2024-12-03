/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.AccessType;
import org.hibernate.boot.jaxb.mapping.spi.AttributesContainer;

public interface ManagedType {
    public String getDescription();

    public void setDescription(String var1);

    public String getClazz();

    public void setClazz(String var1);

    public Boolean isMetadataComplete();

    public void setMetadataComplete(Boolean var1);

    public AccessType getAccess();

    public void setAccess(AccessType var1);

    public AttributesContainer getAttributes();
}

