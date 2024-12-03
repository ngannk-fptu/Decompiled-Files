/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import org.apache.tomcat.dbcp.dbcp2.DataSourceMXBean;

public interface BasicDataSourceMXBean
extends DataSourceMXBean {
    @Deprecated
    public String getPassword();
}

