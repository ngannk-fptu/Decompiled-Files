/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.CredentialHandler
 *  org.apache.catalina.Realm
 *  org.apache.catalina.realm.CombinedRealm
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.CredentialHandler;
import org.apache.catalina.Realm;
import org.apache.catalina.realm.CombinedRealm;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class RealmSF
extends StoreFactoryBase {
    private static Log log = LogFactory.getLog(RealmSF.class);

    @Override
    public void store(PrintWriter aWriter, int indent, Object aElement) throws Exception {
        if (aElement instanceof CombinedRealm) {
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
    public void storeChildren(PrintWriter aWriter, int indent, Object aRealm, StoreDescription parentDesc) throws Exception {
        CredentialHandler credentialHandler;
        if (aRealm instanceof CombinedRealm) {
            CombinedRealm combinedRealm = (CombinedRealm)aRealm;
            Object[] realms = combinedRealm.getNestedRealms();
            this.storeElementArray(aWriter, indent, realms);
        }
        if ((credentialHandler = ((Realm)aRealm).getCredentialHandler()) != null && !credentialHandler.getClass().getName().equals("org.apache.catalina.realm.CombinedRealm$CombinedRealmCredentialHandler")) {
            this.storeElement(aWriter, indent, credentialHandler);
        }
    }
}

