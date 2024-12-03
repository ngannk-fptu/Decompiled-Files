/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.deploy.NamingResourcesImpl
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.deploy.NamingResourcesImpl;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class GlobalNamingResourcesSF
extends StoreFactoryBase {
    private static Log log = LogFactory.getLog(GlobalNamingResourcesSF.class);

    @Override
    public void store(PrintWriter aWriter, int indent, Object aElement) throws Exception {
        if (aElement instanceof NamingResourcesImpl) {
            StoreDescription elementDesc = this.getRegistry().findDescription(NamingResourcesImpl.class.getName() + ".[GlobalNamingResources]");
            if (elementDesc != null) {
                this.getStoreAppender().printIndent(aWriter, indent + 2);
                this.getStoreAppender().printOpenTag(aWriter, indent + 2, aElement, elementDesc);
                NamingResourcesImpl resources = (NamingResourcesImpl)aElement;
                StoreDescription resourcesdesc = this.getRegistry().findDescription(NamingResourcesImpl.class.getName());
                if (resourcesdesc != null) {
                    resourcesdesc.getStoreFactory().store(aWriter, indent + 2, resources);
                } else {
                    log.warn((Object)sm.getString("globalNamingResourcesSF.noFactory"));
                }
                this.getStoreAppender().printIndent(aWriter, indent + 2);
                this.getStoreAppender().printCloseTag(aWriter, elementDesc);
            } else {
                log.warn((Object)sm.getString("storeFactory.noDescriptor", new Object[]{aElement.getClass(), "GlobalNamingResources"}));
            }
        } else {
            log.warn((Object)sm.getString("globalNamingResourcesSF.wrongElement", new Object[]{aElement.getClass()}));
        }
    }
}

