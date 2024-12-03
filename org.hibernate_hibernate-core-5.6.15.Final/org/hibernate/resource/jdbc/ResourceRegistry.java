/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.resource.jdbc;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.Statement;

public interface ResourceRegistry {
    public boolean hasRegisteredResources();

    public void releaseResources();

    public void register(Statement var1, boolean var2);

    public void release(Statement var1);

    public void register(ResultSet var1, Statement var2);

    public void release(ResultSet var1, Statement var2);

    public void register(Blob var1);

    public void release(Blob var1);

    public void register(Clob var1);

    public void release(Clob var1);

    public void register(NClob var1);

    public void release(NClob var1);

    public void cancelLastQuery();
}

