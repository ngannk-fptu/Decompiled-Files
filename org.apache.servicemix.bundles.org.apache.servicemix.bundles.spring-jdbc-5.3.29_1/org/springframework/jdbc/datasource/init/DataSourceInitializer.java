/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource.init;

import javax.sql.DataSource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class DataSourceInitializer
implements InitializingBean,
DisposableBean {
    @Nullable
    private DataSource dataSource;
    @Nullable
    private DatabasePopulator databasePopulator;
    @Nullable
    private DatabasePopulator databaseCleaner;
    private boolean enabled = true;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDatabasePopulator(DatabasePopulator databasePopulator) {
        this.databasePopulator = databasePopulator;
    }

    public void setDatabaseCleaner(DatabasePopulator databaseCleaner) {
        this.databaseCleaner = databaseCleaner;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void afterPropertiesSet() {
        this.execute(this.databasePopulator);
    }

    public void destroy() {
        this.execute(this.databaseCleaner);
    }

    private void execute(@Nullable DatabasePopulator populator) {
        Assert.state((this.dataSource != null ? 1 : 0) != 0, (String)"DataSource must be set");
        if (this.enabled && populator != null) {
            DatabasePopulatorUtils.execute(populator, this.dataSource);
        }
    }
}

