/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql;

import com.mchange.v1.db.sql.PSManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashPSManager
implements PSManager {
    WeakHashMap wmap = new WeakHashMap();

    @Override
    public PreparedStatement getPS(Connection connection, String string) {
        Map map = (Map)this.wmap.get(connection);
        return map == null ? null : (PreparedStatement)map.get(string);
    }

    @Override
    public void putPS(Connection connection, String string, PreparedStatement preparedStatement) {
        HashMap<String, PreparedStatement> hashMap = (HashMap<String, PreparedStatement>)this.wmap.get(connection);
        if (hashMap == null) {
            hashMap = new HashMap<String, PreparedStatement>();
            this.wmap.put(connection, hashMap);
        }
        hashMap.put(string, preparedStatement);
    }
}

