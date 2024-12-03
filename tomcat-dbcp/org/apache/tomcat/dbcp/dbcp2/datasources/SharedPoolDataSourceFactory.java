/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import javax.naming.RefAddr;
import javax.naming.Reference;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSource;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSourceFactory;
import org.apache.tomcat.dbcp.dbcp2.datasources.SharedPoolDataSource;

public class SharedPoolDataSourceFactory
extends InstanceKeyDataSourceFactory {
    private static final String SHARED_POOL_CLASSNAME = SharedPoolDataSource.class.getName();

    @Override
    protected InstanceKeyDataSource getNewInstance(Reference ref) {
        SharedPoolDataSource spds = new SharedPoolDataSource();
        RefAddr ra = ref.get("maxTotal");
        if (ra != null && ra.getContent() != null) {
            spds.setMaxTotal(Integer.parseInt(ra.getContent().toString()));
        }
        return spds;
    }

    @Override
    protected boolean isCorrectClass(String className) {
        return SHARED_POOL_CLASSNAME.equals(className);
    }
}

