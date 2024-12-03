/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
import org.hibernate.boot.model.CustomSql;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.source.spi.EntityNamingSourceContributor;
import org.hibernate.boot.model.source.spi.FilterSource;
import org.hibernate.boot.model.source.spi.IdentifiableTypeSource;
import org.hibernate.boot.model.source.spi.SecondaryTableSource;
import org.hibernate.boot.model.source.spi.TableSpecificationSource;
import org.hibernate.boot.model.source.spi.ToolingHintContextContainer;

public interface EntitySource
extends IdentifiableTypeSource,
ToolingHintContextContainer,
EntityNamingSourceContributor {
    public TableSpecificationSource getPrimaryTable();

    public Map<String, SecondaryTableSource> getSecondaryTableMap();

    public String getXmlNodeName();

    public Map<EntityMode, String> getTuplizerClassMap();

    public String getCustomPersisterClassName();

    public boolean isLazy();

    public String getProxy();

    public int getBatchSize();

    public Boolean isAbstract();

    public boolean isDynamicInsert();

    public boolean isDynamicUpdate();

    public boolean isSelectBeforeUpdate();

    public String getCustomLoaderName();

    public CustomSql getCustomSqlInsert();

    public CustomSql getCustomSqlUpdate();

    public CustomSql getCustomSqlDelete();

    public String[] getSynchronizedTableNames();

    public String getDiscriminatorMatchValue();

    public FilterSource[] getFilterSources();

    public List<JaxbHbmNamedQueryType> getNamedQueries();

    public List<JaxbHbmNamedNativeQueryType> getNamedNativeQueries();

    public TruthValue quoteIdentifiersLocalToEntity();
}

