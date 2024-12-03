/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import javax.sql.DataSource;

public interface SmartDataSource
extends DataSource {
    public boolean shouldClose(Connection var1);
}

