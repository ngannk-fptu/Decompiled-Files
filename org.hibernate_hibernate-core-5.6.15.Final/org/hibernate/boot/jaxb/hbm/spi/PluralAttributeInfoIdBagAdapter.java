/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToManyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.PluralAttributeInfo;

public abstract class PluralAttributeInfoIdBagAdapter
extends JaxbHbmToolingHintContainer
implements PluralAttributeInfo {
    @Override
    public JaxbHbmOneToManyCollectionElementType getOneToMany() {
        return null;
    }

    @Override
    public boolean isInverse() {
        return false;
    }
}

