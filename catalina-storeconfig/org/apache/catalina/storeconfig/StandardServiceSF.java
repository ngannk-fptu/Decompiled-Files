/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Engine
 *  org.apache.catalina.core.StandardService
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.Engine;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.storeconfig.IStoreFactory;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;

public class StandardServiceSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aService, StoreDescription parentDesc) throws Exception {
        if (aService instanceof StandardService) {
            StoreDescription elementDesc;
            StandardService service = (StandardService)aService;
            Object[] listeners = service.findLifecycleListeners();
            this.storeElementArray(aWriter, indent, listeners);
            Object[] executors = service.findExecutors();
            this.storeElementArray(aWriter, indent, executors);
            Object[] connectors = service.findConnectors();
            this.storeElementArray(aWriter, indent, connectors);
            Engine container = service.getContainer();
            if (container != null && (elementDesc = this.getRegistry().findDescription(container.getClass())) != null) {
                IStoreFactory factory = elementDesc.getStoreFactory();
                factory.store(aWriter, indent, container);
            }
        }
    }
}

