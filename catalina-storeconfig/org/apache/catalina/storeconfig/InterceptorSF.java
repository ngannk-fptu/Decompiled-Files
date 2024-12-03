/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.tribes.ChannelInterceptor
 *  org.apache.catalina.tribes.group.interceptors.StaticMembershipInterceptor
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.group.interceptors.StaticMembershipInterceptor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class InterceptorSF
extends StoreFactoryBase {
    private static Log log = LogFactory.getLog(InterceptorSF.class);

    @Override
    public void store(PrintWriter aWriter, int indent, Object aElement) throws Exception {
        if (aElement instanceof StaticMembershipInterceptor) {
            StoreDescription elementDesc = this.getRegistry().findDescription(aElement.getClass());
            if (elementDesc != null) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("factory.storeTag", new Object[]{elementDesc.getTag(), aElement}));
                }
                this.getStoreAppender().printIndent(aWriter, indent + 2);
                this.getStoreAppender().printOpenTag(aWriter, indent + 2, aElement, elementDesc);
                this.storeChildren(aWriter, indent + 2, aElement, elementDesc);
                this.getStoreAppender().printIndent(aWriter, indent + 2);
                this.getStoreAppender().printCloseTag(aWriter, elementDesc);
            } else if (log.isWarnEnabled()) {
                log.warn((Object)sm.getString("factory.storeNoDescriptor", new Object[]{aElement.getClass()}));
            }
        } else {
            super.store(aWriter, indent, aElement);
        }
    }

    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aInterceptor, StoreDescription parentDesc) throws Exception {
        if (aInterceptor instanceof StaticMembershipInterceptor) {
            ChannelInterceptor interceptor = (ChannelInterceptor)aInterceptor;
            this.storeElementArray(aWriter, indent + 2, interceptor.getMembers());
        }
    }
}

