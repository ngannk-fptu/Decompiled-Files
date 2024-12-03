/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.datasource.embedded;

import java.sql.Driver;

public interface ConnectionProperties {
    public void setDriverClass(Class<? extends Driver> var1);

    public void setUrl(String var1);

    public void setUsername(String var1);

    public void setPassword(String var1);
}

