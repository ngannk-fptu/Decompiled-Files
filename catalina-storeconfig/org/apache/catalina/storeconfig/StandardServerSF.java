/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.core.StandardServer
 *  org.apache.catalina.deploy.NamingResourcesImpl
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;

public class StandardServerSF
extends StoreFactoryBase {
    @Override
    public void store(PrintWriter aWriter, int indent, Object aServer) throws Exception {
        this.storeXMLHead(aWriter);
        super.store(aWriter, indent, aServer);
    }

    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aObject, StoreDescription parentDesc) throws Exception {
        if (aObject instanceof StandardServer) {
            StandardServer server = (StandardServer)aObject;
            Object[] listeners = server.findLifecycleListeners();
            this.storeElementArray(aWriter, indent, listeners);
            NamingResourcesImpl globalNamingResources = server.getGlobalNamingResources();
            StoreDescription elementDesc = this.getRegistry().findDescription(NamingResourcesImpl.class.getName() + ".[GlobalNamingResources]");
            if (elementDesc != null) {
                elementDesc.getStoreFactory().store(aWriter, indent, globalNamingResources);
            }
            Object[] services = server.findServices();
            this.storeElementArray(aWriter, indent, services);
        }
    }
}

