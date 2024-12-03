/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.SessionIdGenerator
 *  org.apache.catalina.Store
 *  org.apache.catalina.session.PersistentManager
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.Store;
import org.apache.catalina.session.PersistentManager;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;

public class PersistentManagerSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aManager, StoreDescription parentDesc) throws Exception {
        if (aManager instanceof PersistentManager) {
            PersistentManager manager = (PersistentManager)aManager;
            Store store = manager.getStore();
            this.storeElement(aWriter, indent, store);
            SessionIdGenerator sessionIdGenerator = manager.getSessionIdGenerator();
            if (sessionIdGenerator != null) {
                this.storeElement(aWriter, indent, sessionIdGenerator);
            }
        }
    }
}

