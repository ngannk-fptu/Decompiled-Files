/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hsqldb.persist.HsqlProperties
 */
package com.atlassian.hsqldb;

import com.atlassian.hsqldb.AbstractServerConfig;
import org.hsqldb.persist.HsqlProperties;

public class OpenServerConfig
extends AbstractServerConfig {
    @Override
    protected HsqlProperties getProperties() {
        HsqlProperties properties = super.getProperties();
        properties.setProperty("server.remote_open", Boolean.TRUE.booleanValue());
        return properties;
    }
}

