/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import javax.naming.RefAddr;
import javax.naming.Reference;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSource;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSourceFactory;
import org.apache.tomcat.dbcp.dbcp2.datasources.PerUserPoolDataSource;

public class PerUserPoolDataSourceFactory
extends InstanceKeyDataSourceFactory {
    private static final String PER_USER_POOL_CLASSNAME = PerUserPoolDataSource.class.getName();

    @Override
    protected InstanceKeyDataSource getNewInstance(Reference ref) throws IOException, ClassNotFoundException {
        byte[] serialized;
        PerUserPoolDataSource pupds = new PerUserPoolDataSource();
        RefAddr refAddr = ref.get("defaultMaxTotal");
        if (refAddr != null && refAddr.getContent() != null) {
            pupds.setDefaultMaxTotal(this.parseInt(refAddr));
        }
        if ((refAddr = ref.get("defaultMaxIdle")) != null && refAddr.getContent() != null) {
            pupds.setDefaultMaxIdle(this.parseInt(refAddr));
        }
        if ((refAddr = ref.get("defaultMaxWaitMillis")) != null && refAddr.getContent() != null) {
            pupds.setDefaultMaxWait(Duration.ofMillis(this.parseInt(refAddr)));
        }
        if ((refAddr = ref.get("perUserDefaultAutoCommit")) != null && refAddr.getContent() != null) {
            serialized = (byte[])refAddr.getContent();
            pupds.setPerUserDefaultAutoCommit((Map)PerUserPoolDataSourceFactory.deserialize(serialized));
        }
        if ((refAddr = ref.get("perUserDefaultTransactionIsolation")) != null && refAddr.getContent() != null) {
            serialized = (byte[])refAddr.getContent();
            pupds.setPerUserDefaultTransactionIsolation((Map)PerUserPoolDataSourceFactory.deserialize(serialized));
        }
        if ((refAddr = ref.get("perUserMaxTotal")) != null && refAddr.getContent() != null) {
            serialized = (byte[])refAddr.getContent();
            pupds.setPerUserMaxTotal((Map)PerUserPoolDataSourceFactory.deserialize(serialized));
        }
        if ((refAddr = ref.get("perUserMaxIdle")) != null && refAddr.getContent() != null) {
            serialized = (byte[])refAddr.getContent();
            pupds.setPerUserMaxIdle((Map)PerUserPoolDataSourceFactory.deserialize(serialized));
        }
        if ((refAddr = ref.get("perUserMaxWaitMillis")) != null && refAddr.getContent() != null) {
            serialized = (byte[])refAddr.getContent();
            pupds.setPerUserMaxWaitMillis((Map)PerUserPoolDataSourceFactory.deserialize(serialized));
        }
        if ((refAddr = ref.get("perUserDefaultReadOnly")) != null && refAddr.getContent() != null) {
            serialized = (byte[])refAddr.getContent();
            pupds.setPerUserDefaultReadOnly((Map)PerUserPoolDataSourceFactory.deserialize(serialized));
        }
        return pupds;
    }

    @Override
    protected boolean isCorrectClass(String className) {
        return PER_USER_POOL_CLASSNAME.equals(className);
    }
}

