/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCustomSqlDmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchProfileType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLoaderType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmResultSetMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSynchronizeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

public interface EntityInfo
extends ToolingHintContainer {
    public String getName();

    public String getEntityName();

    public Boolean isAbstract();

    public Boolean isLazy();

    public String getProxy();

    public int getBatchSize();

    public boolean isDynamicInsert();

    public boolean isDynamicUpdate();

    public boolean isSelectBeforeUpdate();

    public List<JaxbHbmTuplizerType> getTuplizer();

    public String getPersister();

    public JaxbHbmLoaderType getLoader();

    public JaxbHbmCustomSqlDmlType getSqlInsert();

    public JaxbHbmCustomSqlDmlType getSqlUpdate();

    public JaxbHbmCustomSqlDmlType getSqlDelete();

    public List<JaxbHbmSynchronizeType> getSynchronize();

    public List<JaxbHbmFetchProfileType> getFetchProfile();

    public List<JaxbHbmResultSetMappingType> getResultset();

    public List<JaxbHbmNamedNativeQueryType> getSqlQuery();

    public List<JaxbHbmNamedQueryType> getQuery();

    public List getAttributes();
}

