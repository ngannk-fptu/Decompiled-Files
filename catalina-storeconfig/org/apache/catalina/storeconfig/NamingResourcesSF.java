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

public class NamingResourcesSF
extends StoreFactoryBase {
    private static Log log = LogFactory.getLog(NamingResourcesSF.class);

    @Override
    public void store(PrintWriter aWriter, int indent, Object aElement) throws Exception {
        StoreDescription elementDesc = this.getRegistry().findDescription(aElement.getClass());
        if (elementDesc != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("store " + elementDesc.getTag() + "( " + aElement + " )"));
            }
            this.storeChildren(aWriter, indent, aElement, elementDesc);
        } else {
            log.warn((Object)sm.getString("storeFactory.noDescriptor", new Object[]{aElement.getClass(), "NamingResources"}));
        }
    }

    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aElement, StoreDescription elementDesc) throws Exception {
        if (aElement instanceof NamingResourcesImpl) {
            NamingResourcesImpl resources = (NamingResourcesImpl)aElement;
            Object[] ejbs = resources.findEjbs();
            this.storeElementArray(aWriter, indent, ejbs);
            Object[] envs = resources.findEnvironments();
            this.storeElementArray(aWriter, indent, envs);
            Object[] lejbs = resources.findLocalEjbs();
            this.storeElementArray(aWriter, indent, lejbs);
            Object[] dresources = resources.findResources();
            this.storeElementArray(aWriter, indent, dresources);
            Object[] resEnv = resources.findResourceEnvRefs();
            this.storeElementArray(aWriter, indent, resEnv);
            Object[] resourceLinks = resources.findResourceLinks();
            this.storeElementArray(aWriter, indent, resourceLinks);
        }
    }
}

