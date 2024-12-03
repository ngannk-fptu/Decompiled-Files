/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTypeSpecificationType;

public interface SimpleValueTypeInfo {
    public String getTypeAttribute();

    public JaxbHbmTypeSpecificationType getType();
}

