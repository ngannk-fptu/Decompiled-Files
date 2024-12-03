/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import java.util.List;
import javax.persistence.LockModeType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbQueryHint;

public interface NamedQuery
extends Serializable {
    public String getDescription();

    public void setDescription(String var1);

    public String getQuery();

    public void setQuery(String var1);

    public LockModeType getLockMode();

    public void setLockMode(LockModeType var1);

    public List<JaxbQueryHint> getHint();

    public String getName();

    public void setName(String var1);
}

