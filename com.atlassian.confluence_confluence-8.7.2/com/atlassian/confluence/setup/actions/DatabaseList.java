/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.PairType
 *  com.atlassian.core.util.PropertyUtils
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.core.util.PairType;
import com.atlassian.core.util.PropertyUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DatabaseList {
    private static final String SUPPORTED_DB_FILE = "com/atlassian/confluence/setup/actions/SupportedDatabases.properties";
    private List databases = new ArrayList(7);

    public DatabaseList() {
        Properties dbProps = PropertyUtils.getProperties((String)SUPPORTED_DB_FILE, DatabaseList.class);
        ArrayList<Object> c = new ArrayList<Object>(dbProps.keySet());
        Collections.sort(c);
        for (String string : c) {
            if (!string.startsWith("key.")) continue;
            this.databases.add(new PairType((Serializable)((Object)dbProps.getProperty(string)), (Serializable)((Object)dbProps.getProperty("value." + string.substring(4)))));
        }
    }

    public List getDatabases() {
        return this.databases;
    }
}

