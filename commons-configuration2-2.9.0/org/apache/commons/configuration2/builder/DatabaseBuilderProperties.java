/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import javax.sql.DataSource;

public interface DatabaseBuilderProperties<T> {
    public T setDataSource(DataSource var1);

    public T setTable(String var1);

    public T setKeyColumn(String var1);

    public T setValueColumn(String var1);

    public T setConfigurationNameColumn(String var1);

    public T setConfigurationName(String var1);

    public T setAutoCommit(boolean var1);
}

