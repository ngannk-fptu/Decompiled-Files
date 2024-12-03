/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Loader
 *  org.apache.catalina.loader.WebappLoader
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.Loader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class LoaderSF
extends StoreFactoryBase {
    private static Log log = LogFactory.getLog(LoaderSF.class);

    @Override
    public void store(PrintWriter aWriter, int indent, Object aElement) throws Exception {
        StoreDescription elementDesc = this.getRegistry().findDescription(aElement.getClass());
        if (elementDesc != null) {
            Loader loader = (Loader)aElement;
            if (!this.isDefaultLoader(loader)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("store " + elementDesc.getTag() + "( " + aElement + " )"));
                }
                this.getStoreAppender().printIndent(aWriter, indent + 2);
                this.getStoreAppender().printTag(aWriter, indent + 2, loader, elementDesc);
            }
        } else if (log.isWarnEnabled()) {
            log.warn((Object)("Descriptor for element" + aElement.getClass() + " not configured or element class not StandardManager!"));
        }
    }

    protected boolean isDefaultLoader(Loader loader) {
        if (!(loader instanceof WebappLoader)) {
            return false;
        }
        WebappLoader wloader = (WebappLoader)loader;
        return !wloader.getDelegate() && wloader.getLoaderClass().equals("org.apache.catalina.loader.WebappClassLoader");
    }
}

