/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.Manager
 *  org.apache.catalina.SessionIdGenerator
 *  org.apache.catalina.session.StandardManager
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionIdGenerator;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ManagerSF
extends StoreFactoryBase {
    private static Log log = LogFactory.getLog(ManagerSF.class);

    @Override
    public void store(PrintWriter aWriter, int indent, Object aElement) throws Exception {
        StoreDescription elementDesc = this.getRegistry().findDescription(aElement.getClass());
        if (elementDesc != null) {
            if (aElement instanceof StandardManager) {
                StandardManager manager = (StandardManager)aElement;
                if (!this.isDefaultManager(manager)) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)sm.getString("factory.storeTag", new Object[]{elementDesc.getTag(), aElement}));
                    }
                    super.store(aWriter, indent, aElement);
                }
            } else {
                super.store(aWriter, indent, aElement);
            }
        } else if (log.isWarnEnabled()) {
            log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aElement.getClass()}));
        }
    }

    protected boolean isDefaultManager(StandardManager smanager) {
        return "SESSIONS.ser".equals(smanager.getPathname()) && smanager.getMaxActiveSessions() == -1;
    }

    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aManager, StoreDescription parentDesc) throws Exception {
        Manager manager;
        SessionIdGenerator sessionIdGenerator;
        if (aManager instanceof Manager && (sessionIdGenerator = (manager = (Manager)aManager).getSessionIdGenerator()) != null) {
            this.storeElement(aWriter, indent, sessionIdGenerator);
        }
    }
}

