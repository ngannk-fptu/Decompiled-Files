/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.core.StandardContext
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class WrapperListenerSF
extends StoreFactoryBase {
    private static Log log = LogFactory.getLog(WrapperListenerSF.class);

    @Override
    public void store(PrintWriter aWriter, int indent, Object aElement) throws Exception {
        if (aElement instanceof StandardContext) {
            StoreDescription elementDesc = this.getRegistry().findDescription(aElement.getClass().getName() + ".[WrapperListener]");
            String[] listeners = ((StandardContext)aElement).findWrapperListeners();
            if (elementDesc != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("store " + elementDesc.getTag() + "( " + aElement + " )"));
                }
                this.getStoreAppender().printTagArray(aWriter, "WrapperListener", indent, listeners);
            }
        } else {
            log.warn((Object)sm.getString("storeFactory.noDescriptor", new Object[]{aElement.getClass(), "WrapperListener"}));
        }
    }
}

