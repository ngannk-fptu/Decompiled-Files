/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource.embedded;

import javax.sql.DataSource;

public interface EmbeddedDatabase
extends DataSource {
    public void shutdown();
}

