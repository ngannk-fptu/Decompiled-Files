/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core.support;

import java.util.Properties;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.util.Assert;

public class PropertiesBasedNamedQueries
implements NamedQueries {
    private static final String NO_QUERY_FOUND = "No query with name %s found! Make sure you call hasQuery(\u2026) before calling this method!";
    public static final NamedQueries EMPTY = new PropertiesBasedNamedQueries(new Properties());
    private final Properties properties;

    public PropertiesBasedNamedQueries(Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean hasQuery(String queryName) {
        Assert.hasText((String)queryName, (String)"Query name must not be null or empty!");
        return this.properties.containsKey(queryName);
    }

    @Override
    public String getQuery(String queryName) {
        Assert.hasText((String)queryName, (String)"Query name must not be null or empty!");
        String query = this.properties.getProperty(queryName);
        if (query == null) {
            throw new IllegalArgumentException(String.format(NO_QUERY_FOUND, queryName));
        }
        return query;
    }
}

