/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;

public class XADataSource
extends DataSource
implements javax.sql.XADataSource {
    public XADataSource() {
    }

    public XADataSource(PoolConfiguration poolProperties) {
        super(poolProperties);
    }
}

