/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public final class SQLServerDataSourceObjectFactory
implements ObjectFactory {
    @Override
    public Object getObjectInstance(Object ref, Name name, Context c, Hashtable<?, ?> h) throws SQLServerException {
        try {
            String className;
            Reference r = (Reference)ref;
            RefAddr ra = r.get("class");
            if (null == ra) {
                this.throwInvalidDataSourceRefException();
            }
            if (null == (className = (String)ra.getContent())) {
                this.throwInvalidDataSourceRefException();
            }
            if ("com.microsoft.sqlserver.jdbc.SQLServerDataSource".equals(className) || "com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource".equals(className) || "com.microsoft.sqlserver.jdbc.SQLServerXADataSource".equals(className)) {
                Class<?> dataSourceClass = Class.forName(className);
                Object dataSourceClassInstance = dataSourceClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                SQLServerDataSource ds = (SQLServerDataSource)dataSourceClassInstance;
                ds.initializeFromReference(r);
                return dataSourceClassInstance;
            }
            this.throwInvalidDataSourceRefException();
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            this.throwInvalidDataSourceRefException();
        }
        return null;
    }

    private void throwInvalidDataSourceRefException() throws SQLServerException {
        SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_invalidDataSourceReference"), null, true);
    }
}

