/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.AttributeMapping;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCacheType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCustomSqlDmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchStyleWithSubselectEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmKeyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyWithExtraEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLoaderType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToAnyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToManyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToManyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOuterJoinEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSynchronizeType;
import org.hibernate.boot.jaxb.hbm.spi.TableInformationContainer;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

public interface PluralAttributeInfo
extends AttributeMapping,
TableInformationContainer,
ToolingHintContainer {
    public JaxbHbmKeyType getKey();

    public JaxbHbmBasicCollectionElementType getElement();

    public JaxbHbmCompositeCollectionElementType getCompositeElement();

    public JaxbHbmOneToManyCollectionElementType getOneToMany();

    public JaxbHbmManyToManyCollectionElementType getManyToMany();

    public JaxbHbmManyToAnyCollectionElementType getManyToAny();

    public String getComment();

    public String getCheck();

    public String getWhere();

    public JaxbHbmLoaderType getLoader();

    public JaxbHbmCustomSqlDmlType getSqlInsert();

    public JaxbHbmCustomSqlDmlType getSqlUpdate();

    public JaxbHbmCustomSqlDmlType getSqlDelete();

    public JaxbHbmCustomSqlDmlType getSqlDeleteAll();

    public List<JaxbHbmSynchronizeType> getSynchronize();

    public JaxbHbmCacheType getCache();

    public List<JaxbHbmFilterType> getFilter();

    public String getCascade();

    public JaxbHbmFetchStyleWithSubselectEnum getFetch();

    public JaxbHbmLazyWithExtraEnum getLazy();

    public JaxbHbmOuterJoinEnum getOuterJoin();

    public int getBatchSize();

    public boolean isInverse();

    public boolean isMutable();

    public boolean isOptimisticLock();

    public String getCollectionType();

    public String getPersister();
}

