/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionCallback
 */
package com.atlassian.activeobjects.external;

import com.atlassian.activeobjects.external.ActiveObjectsModuleMetaData;
import com.atlassian.activeobjects.external.FailedFastCountException;
import com.atlassian.sal.api.transaction.TransactionCallback;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.java.ao.DBParam;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;
import net.java.ao.RawEntity;

public interface ActiveObjects {
    public void migrate(Class<? extends RawEntity<?>> ... var1);

    public void migrateDestructively(Class<? extends RawEntity<?>> ... var1);

    public void flushAll();

    public void flush(RawEntity<?> ... var1);

    public <T extends RawEntity<K>, K> T[] get(Class<T> var1, K ... var2);

    public <T extends RawEntity<K>, K> T get(Class<T> var1, K var2);

    public <T extends RawEntity<K>, K> T create(Class<T> var1, DBParam ... var2);

    public <T extends RawEntity<K>, K> T create(Class<T> var1, Map<String, Object> var2);

    public <T extends RawEntity<K>, K> void create(Class<T> var1, List<Map<String, Object>> var2);

    public void delete(RawEntity<?> ... var1);

    public <K> int deleteWithSQL(Class<? extends RawEntity<K>> var1, String var2, Object ... var3);

    public <T extends RawEntity<K>, K> T[] find(Class<T> var1);

    public <T extends RawEntity<K>, K> T[] find(Class<T> var1, String var2, Object ... var3);

    public <T extends RawEntity<K>, K> T[] find(Class<T> var1, Query var2);

    public <T extends RawEntity<K>, K> T[] find(Class<T> var1, String var2, Query var3);

    public <T extends RawEntity<K>, K> T[] findWithSQL(Class<T> var1, String var2, String var3, Object ... var4);

    public <T extends RawEntity<K>, K> void stream(Class<T> var1, EntityStreamCallback<T, K> var2);

    public <T extends RawEntity<K>, K> void stream(Class<T> var1, Query var2, EntityStreamCallback<T, K> var3);

    public <K> int count(Class<? extends RawEntity<K>> var1);

    public <K> int count(Class<? extends RawEntity<K>> var1, String var2, Object ... var3);

    public <K> int count(Class<? extends RawEntity<K>> var1, Query var2);

    public <K> int getFastCountEstimate(Class<? extends RawEntity<K>> var1) throws SQLException, FailedFastCountException;

    public <T> T executeInTransaction(TransactionCallback<T> var1);

    public ActiveObjectsModuleMetaData moduleMetaData();
}

