/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jndi.JndiLocatorSupport
 */
package org.springframework.jdbc.datasource.lookup;

import javax.naming.NamingException;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jndi.JndiLocatorSupport;

public class JndiDataSourceLookup
extends JndiLocatorSupport
implements DataSourceLookup {
    public JndiDataSourceLookup() {
        this.setResourceRef(true);
    }

    @Override
    public DataSource getDataSource(String dataSourceName) throws DataSourceLookupFailureException {
        try {
            return (DataSource)this.lookup(dataSourceName, DataSource.class);
        }
        catch (NamingException ex) {
            throw new DataSourceLookupFailureException("Failed to look up JNDI DataSource with name '" + dataSourceName + "'", ex);
        }
    }
}

