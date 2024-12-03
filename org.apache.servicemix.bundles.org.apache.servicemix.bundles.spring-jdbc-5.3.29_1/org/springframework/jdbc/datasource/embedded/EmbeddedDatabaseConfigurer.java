/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource.embedded;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;

public interface EmbeddedDatabaseConfigurer {
    public void configureConnectionProperties(ConnectionProperties var1, String var2);

    public void shutdown(DataSource var1, String var2);
}

