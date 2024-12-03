/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.storeconfig.StoreAppender;
import org.apache.catalina.storeconfig.StoreRegistry;

public interface IStoreFactory {
    public StoreAppender getStoreAppender();

    public void setStoreAppender(StoreAppender var1);

    public void setRegistry(StoreRegistry var1);

    public StoreRegistry getRegistry();

    public void store(PrintWriter var1, int var2, Object var3) throws Exception;

    public void storeXMLHead(PrintWriter var1);
}

