/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FetchType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.FetchType;

public interface FetchableAttribute {
    public FetchType getFetch();

    public void setFetch(FetchType var1);
}

