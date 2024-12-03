/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import javax.sql.DataSource;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.DatabaseBuilderProperties;

public class DatabaseBuilderParametersImpl
extends BasicBuilderParameters
implements DatabaseBuilderProperties<DatabaseBuilderParametersImpl> {
    private static final String PROP_DATA_SOURCE = "dataSource";
    private static final String PROP_TABLE = "table";
    private static final String PROP_KEY_COLUMN = "keyColumn";
    private static final String PROP_VALUE_COLUMN = "valueColumn";
    private static final String PROP_CONFIG_NAME_COLUMN = "configurationNameColumn";
    private static final String PROP_CONFIG_NAME = "configurationName";
    private static final String PROP_AUTO_COMMIT = "autoCommit";

    @Override
    public DatabaseBuilderParametersImpl setDataSource(DataSource src) {
        this.storeProperty(PROP_DATA_SOURCE, src);
        return this;
    }

    @Override
    public DatabaseBuilderParametersImpl setTable(String tname) {
        this.storeProperty(PROP_TABLE, tname);
        return this;
    }

    @Override
    public DatabaseBuilderParametersImpl setKeyColumn(String name) {
        this.storeProperty(PROP_KEY_COLUMN, name);
        return this;
    }

    @Override
    public DatabaseBuilderParametersImpl setValueColumn(String name) {
        this.storeProperty(PROP_VALUE_COLUMN, name);
        return this;
    }

    @Override
    public DatabaseBuilderParametersImpl setConfigurationNameColumn(String name) {
        this.storeProperty(PROP_CONFIG_NAME_COLUMN, name);
        return this;
    }

    @Override
    public DatabaseBuilderParametersImpl setConfigurationName(String name) {
        this.storeProperty(PROP_CONFIG_NAME, name);
        return this;
    }

    @Override
    public DatabaseBuilderParametersImpl setAutoCommit(boolean f) {
        this.storeProperty(PROP_AUTO_COMMIT, f);
        return this;
    }
}

