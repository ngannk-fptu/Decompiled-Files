/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.users;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.catalina.users.MemoryUserDatabase;

public class MemoryUserDatabaseFactory
implements ObjectFactory {
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference)obj;
        if (!"org.apache.catalina.UserDatabase".equals(ref.getClassName())) {
            return null;
        }
        MemoryUserDatabase database = new MemoryUserDatabase(name.toString());
        RefAddr ra = null;
        ra = ref.get("pathname");
        if (ra != null) {
            database.setPathname(ra.getContent().toString());
        }
        if ((ra = ref.get("readonly")) != null) {
            database.setReadonly(Boolean.parseBoolean(ra.getContent().toString()));
        }
        if ((ra = ref.get("watchSource")) != null) {
            database.setWatchSource(Boolean.parseBoolean(ra.getContent().toString()));
        }
        database.open();
        if (!database.getReadonly()) {
            database.save();
        }
        return database;
    }
}

