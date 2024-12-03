/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.ldap.transaction.compensating.manager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import javax.naming.directory.DirContext;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextProxy;
import org.springframework.ldap.core.support.DelegatingBaseLdapPathContextSourceSupport;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.ldap.transaction.compensating.manager.DirContextHolder;
import org.springframework.ldap.transaction.compensating.manager.TransactionAwareDirContextInvocationHandler;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionAwareContextSourceProxy
extends DelegatingBaseLdapPathContextSourceSupport
implements ContextSource {
    private ContextSource target;

    public TransactionAwareContextSourceProxy(ContextSource target) {
        this.target = target;
    }

    @Override
    public ContextSource getTarget() {
        return this.target;
    }

    @Override
    public DirContext getReadOnlyContext() {
        return this.getReadWriteContext();
    }

    private DirContext getTransactionAwareDirContextProxy(DirContext context, ContextSource target) {
        return (DirContext)Proxy.newProxyInstance(DirContextProxy.class.getClassLoader(), new Class[]{LdapUtils.getActualTargetClass(context), DirContextProxy.class}, (InvocationHandler)new TransactionAwareDirContextInvocationHandler(context, target));
    }

    @Override
    public DirContext getReadWriteContext() {
        DirContextHolder contextHolder = (DirContextHolder)((Object)TransactionSynchronizationManager.getResource((Object)this.target));
        DirContext ctx = null;
        if (contextHolder != null) {
            ctx = contextHolder.getCtx();
        }
        if (ctx == null) {
            ctx = this.target.getReadWriteContext();
            if (contextHolder != null) {
                contextHolder.setCtx(ctx);
            }
        }
        return this.getTransactionAwareDirContextProxy(ctx, this.target);
    }

    @Override
    public DirContext getContext(String principal, String credentials) {
        return this.target.getContext(principal, credentials);
    }
}

