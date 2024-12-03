/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.builder;

import java.sql.Driver;
import net.java.ao.DisposableDataSource;

public interface DataSourceFactory {
    public DisposableDataSource getDataSource(Class<? extends Driver> var1, String var2, String var3, String var4);
}

