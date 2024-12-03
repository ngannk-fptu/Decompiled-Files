/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSecondaryTableType;

public interface SecondaryTableContainer {
    public List<JaxbHbmSecondaryTableType> getJoin();
}

