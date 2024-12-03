/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.datasource.embedded;

import javax.sql.DataSource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.lang.Nullable;

public class EmbeddedDatabaseFactoryBean
extends EmbeddedDatabaseFactory
implements FactoryBean<DataSource>,
InitializingBean,
DisposableBean {
    @Nullable
    private DatabasePopulator databaseCleaner;

    public void setDatabaseCleaner(DatabasePopulator databaseCleaner) {
        this.databaseCleaner = databaseCleaner;
    }

    public void afterPropertiesSet() {
        this.initDatabase();
    }

    @Nullable
    public DataSource getObject() {
        return this.getDataSource();
    }

    public Class<? extends DataSource> getObjectType() {
        return DataSource.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        if (this.databaseCleaner != null && this.getDataSource() != null) {
            DatabasePopulatorUtils.execute(this.databaseCleaner, this.getDataSource());
        }
        this.shutdownDatabase();
    }
}

