/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource.lookup;

import javax.sql.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class BeanFactoryDataSourceLookup
implements DataSourceLookup,
BeanFactoryAware {
    @Nullable
    private BeanFactory beanFactory;

    public BeanFactoryDataSourceLookup() {
    }

    public BeanFactoryDataSourceLookup(BeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, (String)"BeanFactory is required");
        this.beanFactory = beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public DataSource getDataSource(String dataSourceName) throws DataSourceLookupFailureException {
        Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"BeanFactory is required");
        try {
            return (DataSource)this.beanFactory.getBean(dataSourceName, DataSource.class);
        }
        catch (BeansException ex) {
            throw new DataSourceLookupFailureException("Failed to look up DataSource bean with name '" + dataSourceName + "'", ex);
        }
    }
}

