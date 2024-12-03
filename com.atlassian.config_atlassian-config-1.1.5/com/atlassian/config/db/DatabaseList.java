/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config.db;

import com.atlassian.config.db.PropertyUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DatabaseList {
    private List<DatabaseType> databases = new ArrayList<DatabaseType>(7);

    public DatabaseList() {
        this("supportedDatabases.properties");
    }

    public DatabaseList(String supportedDbFile) {
        Properties dbProps = PropertyUtils.getProperties(supportedDbFile, DatabaseList.class);
        ArrayList<Object> c = new ArrayList<Object>(dbProps.keySet());
        Collections.sort(c);
        for (String string : c) {
            if (!string.startsWith("key.")) continue;
            this.databases.add(new DatabaseType(dbProps.getProperty(string), dbProps.getProperty("value." + string.substring(4))));
        }
    }

    public List<DatabaseType> getDatabases() {
        return this.databases;
    }

    public static class DatabaseType {
        private final String key;
        private final String value;

        public DatabaseType(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.key + "/" + this.value;
        }
    }
}

