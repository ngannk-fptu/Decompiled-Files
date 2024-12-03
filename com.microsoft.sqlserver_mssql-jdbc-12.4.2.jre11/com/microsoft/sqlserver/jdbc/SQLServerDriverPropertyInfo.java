/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerDriverStringProperty;
import com.microsoft.sqlserver.jdbc.SQLServerResource;
import java.sql.DriverPropertyInfo;
import java.util.Properties;

final class SQLServerDriverPropertyInfo {
    private final String name;
    private final String description;
    private final String defaultValue;
    private final boolean required;
    private final String[] choices;

    final String getName() {
        return this.name;
    }

    SQLServerDriverPropertyInfo(String name, String defaultValue, boolean required, String[] choices) {
        this.name = name;
        this.description = SQLServerResource.getResource("R_" + name + "PropertyDescription");
        this.defaultValue = defaultValue;
        this.required = required;
        this.choices = choices;
    }

    DriverPropertyInfo build(Properties connProperties) {
        String propValue;
        String string = propValue = this.name.equals(SQLServerDriverStringProperty.PASSWORD.toString()) ? "" : connProperties.getProperty(this.name);
        if (null == propValue) {
            propValue = this.defaultValue;
        }
        DriverPropertyInfo info = new DriverPropertyInfo(this.name, propValue);
        info.description = this.description;
        info.required = this.required;
        info.choices = this.choices;
        return info;
    }
}

