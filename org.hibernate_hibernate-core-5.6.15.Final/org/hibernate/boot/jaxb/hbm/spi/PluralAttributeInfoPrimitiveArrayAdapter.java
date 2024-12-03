/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.util.Collections;
import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyWithExtraEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToAnyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToManyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToManyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.PluralAttributeInfo;

public abstract class PluralAttributeInfoPrimitiveArrayAdapter
extends JaxbHbmToolingHintContainer
implements PluralAttributeInfo {
    @Override
    public boolean isInverse() {
        return false;
    }

    @Override
    public JaxbHbmLazyWithExtraEnum getLazy() {
        return JaxbHbmLazyWithExtraEnum.FALSE;
    }

    @Override
    public JaxbHbmOneToManyCollectionElementType getOneToMany() {
        return null;
    }

    @Override
    public JaxbHbmCompositeCollectionElementType getCompositeElement() {
        return null;
    }

    @Override
    public JaxbHbmManyToManyCollectionElementType getManyToMany() {
        return null;
    }

    @Override
    public JaxbHbmManyToAnyCollectionElementType getManyToAny() {
        return null;
    }

    @Override
    public List<JaxbHbmFilterType> getFilter() {
        return Collections.emptyList();
    }

    @Override
    public String getCascade() {
        return null;
    }
}

