/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.tomcat.dbcp.dbcp2.ListException;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.dbcp2.datasources.InstanceKeyDataSource;

abstract class InstanceKeyDataSourceFactory
implements ObjectFactory {
    private static final Map<String, InstanceKeyDataSource> INSTANCE_MAP = new ConcurrentHashMap<String, InstanceKeyDataSource>();

    InstanceKeyDataSourceFactory() {
    }

    public static void closeAll() throws ListException {
        ArrayList<Throwable> exceptionList = new ArrayList<Throwable>(INSTANCE_MAP.size());
        INSTANCE_MAP.entrySet().forEach(entry -> {
            if (entry != null) {
                InstanceKeyDataSource value = (InstanceKeyDataSource)entry.getValue();
                Utils.close(value, exceptionList::add);
            }
        });
        INSTANCE_MAP.clear();
        if (!exceptionList.isEmpty()) {
            throw new ListException("Could not close all InstanceKeyDataSource instances.", exceptionList);
        }
    }

    protected static final Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        Object object;
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new ByteArrayInputStream(data));
            object = in.readObject();
        }
        catch (Throwable throwable) {
            Utils.closeQuietly(in);
            throw throwable;
        }
        Utils.closeQuietly(in);
        return object;
    }

    static synchronized String registerNewInstance(InstanceKeyDataSource ds) {
        int max = 0;
        for (String s : INSTANCE_MAP.keySet()) {
            if (s == null) continue;
            try {
                max = Math.max(max, Integer.parseInt(s));
            }
            catch (NumberFormatException numberFormatException) {}
        }
        String instanceKey = String.valueOf(max + 1);
        INSTANCE_MAP.put(instanceKey, ds);
        return instanceKey;
    }

    static void removeInstance(String key) {
        if (key != null) {
            INSTANCE_MAP.remove(key);
        }
    }

    protected abstract InstanceKeyDataSource getNewInstance(Reference var1) throws IOException, ClassNotFoundException;

    @Override
    public Object getObjectInstance(Object refObj, Name name, Context context, Hashtable<?, ?> env) throws IOException, ClassNotFoundException {
        Reference ref;
        InstanceKeyDataSource obj = null;
        if (refObj instanceof Reference && this.isCorrectClass((ref = (Reference)refObj).getClassName())) {
            RefAddr refAddr = ref.get("instanceKey");
            if (refAddr != null && refAddr.getContent() != null) {
                obj = INSTANCE_MAP.get(refAddr.getContent());
            } else {
                String key = null;
                if (name != null) {
                    key = name.toString();
                    obj = INSTANCE_MAP.get(key);
                }
                if (obj == null) {
                    InstanceKeyDataSource ds = this.getNewInstance(ref);
                    this.setCommonProperties(ref, ds);
                    obj = ds;
                    if (key != null) {
                        INSTANCE_MAP.put(key, ds);
                    }
                }
            }
        }
        return obj;
    }

    protected abstract boolean isCorrectClass(String var1);

    boolean parseBoolean(RefAddr refAddr) {
        return Boolean.parseBoolean(this.toString(refAddr));
    }

    int parseInt(RefAddr refAddr) {
        return Integer.parseInt(this.toString(refAddr));
    }

    long parseLong(RefAddr refAddr) {
        return Long.parseLong(this.toString(refAddr));
    }

    private void setCommonProperties(Reference ref, InstanceKeyDataSource ikds) throws IOException, ClassNotFoundException {
        RefAddr refAddr = ref.get("dataSourceName");
        if (refAddr != null && refAddr.getContent() != null) {
            ikds.setDataSourceName(this.toString(refAddr));
        }
        if ((refAddr = ref.get("description")) != null && refAddr.getContent() != null) {
            ikds.setDescription(this.toString(refAddr));
        }
        if ((refAddr = ref.get("jndiEnvironment")) != null && refAddr.getContent() != null) {
            byte[] serialized = (byte[])refAddr.getContent();
            ikds.setJndiEnvironment((Properties)InstanceKeyDataSourceFactory.deserialize(serialized));
        }
        if ((refAddr = ref.get("loginTimeout")) != null && refAddr.getContent() != null) {
            ikds.setLoginTimeout(Duration.ofSeconds(this.parseInt(refAddr)));
        }
        if ((refAddr = ref.get("blockWhenExhausted")) != null && refAddr.getContent() != null) {
            ikds.setDefaultBlockWhenExhausted(this.parseBoolean(refAddr));
        }
        if ((refAddr = ref.get("evictionPolicyClassName")) != null && refAddr.getContent() != null) {
            ikds.setDefaultEvictionPolicyClassName(this.toString(refAddr));
        }
        if ((refAddr = ref.get("lifo")) != null && refAddr.getContent() != null) {
            ikds.setDefaultLifo(this.parseBoolean(refAddr));
        }
        if ((refAddr = ref.get("maxIdlePerKey")) != null && refAddr.getContent() != null) {
            ikds.setDefaultMaxIdle(this.parseInt(refAddr));
        }
        if ((refAddr = ref.get("maxTotalPerKey")) != null && refAddr.getContent() != null) {
            ikds.setDefaultMaxTotal(this.parseInt(refAddr));
        }
        if ((refAddr = ref.get("maxWaitMillis")) != null && refAddr.getContent() != null) {
            ikds.setDefaultMaxWait(Duration.ofMillis(this.parseLong(refAddr)));
        }
        if ((refAddr = ref.get("minEvictableIdleTimeMillis")) != null && refAddr.getContent() != null) {
            ikds.setDefaultMinEvictableIdle(Duration.ofMillis(this.parseLong(refAddr)));
        }
        if ((refAddr = ref.get("minIdlePerKey")) != null && refAddr.getContent() != null) {
            ikds.setDefaultMinIdle(this.parseInt(refAddr));
        }
        if ((refAddr = ref.get("numTestsPerEvictionRun")) != null && refAddr.getContent() != null) {
            ikds.setDefaultNumTestsPerEvictionRun(this.parseInt(refAddr));
        }
        if ((refAddr = ref.get("softMinEvictableIdleTimeMillis")) != null && refAddr.getContent() != null) {
            ikds.setDefaultSoftMinEvictableIdle(Duration.ofMillis(this.parseLong(refAddr)));
        }
        if ((refAddr = ref.get("testOnCreate")) != null && refAddr.getContent() != null) {
            ikds.setDefaultTestOnCreate(this.parseBoolean(refAddr));
        }
        if ((refAddr = ref.get("testOnBorrow")) != null && refAddr.getContent() != null) {
            ikds.setDefaultTestOnBorrow(this.parseBoolean(refAddr));
        }
        if ((refAddr = ref.get("testOnReturn")) != null && refAddr.getContent() != null) {
            ikds.setDefaultTestOnReturn(this.parseBoolean(refAddr));
        }
        if ((refAddr = ref.get("testWhileIdle")) != null && refAddr.getContent() != null) {
            ikds.setDefaultTestWhileIdle(this.parseBoolean(refAddr));
        }
        if ((refAddr = ref.get("timeBetweenEvictionRunsMillis")) != null && refAddr.getContent() != null) {
            ikds.setDefaultDurationBetweenEvictionRuns(Duration.ofMillis(this.parseLong(refAddr)));
        }
        if ((refAddr = ref.get("validationQuery")) != null && refAddr.getContent() != null) {
            ikds.setValidationQuery(this.toString(refAddr));
        }
        if ((refAddr = ref.get("validationQueryTimeout")) != null && refAddr.getContent() != null) {
            ikds.setValidationQueryTimeout(Duration.ofSeconds(this.parseInt(refAddr)));
        }
        if ((refAddr = ref.get("rollbackAfterValidation")) != null && refAddr.getContent() != null) {
            ikds.setRollbackAfterValidation(this.parseBoolean(refAddr));
        }
        if ((refAddr = ref.get("maxConnLifetimeMillis")) != null && refAddr.getContent() != null) {
            ikds.setMaxConnLifetime(Duration.ofMillis(this.parseLong(refAddr)));
        }
        if ((refAddr = ref.get("defaultAutoCommit")) != null && refAddr.getContent() != null) {
            ikds.setDefaultAutoCommit(Boolean.valueOf(this.toString(refAddr)));
        }
        if ((refAddr = ref.get("defaultTransactionIsolation")) != null && refAddr.getContent() != null) {
            ikds.setDefaultTransactionIsolation(this.parseInt(refAddr));
        }
        if ((refAddr = ref.get("defaultReadOnly")) != null && refAddr.getContent() != null) {
            ikds.setDefaultReadOnly(Boolean.valueOf(this.toString(refAddr)));
        }
    }

    String toString(RefAddr refAddr) {
        return refAddr.getContent().toString();
    }
}

