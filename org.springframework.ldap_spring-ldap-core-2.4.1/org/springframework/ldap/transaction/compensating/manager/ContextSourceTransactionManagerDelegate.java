/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.transaction.compensating.manager;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.AbstractContextSource;
import org.springframework.ldap.transaction.compensating.LdapCompensatingTransactionOperationFactory;
import org.springframework.ldap.transaction.compensating.TempEntryRenamingStrategy;
import org.springframework.ldap.transaction.compensating.manager.DirContextHolder;
import org.springframework.ldap.transaction.compensating.manager.TransactionAwareContextSourceProxy;
import org.springframework.transaction.compensating.support.AbstractCompensatingTransactionManagerDelegate;
import org.springframework.transaction.compensating.support.CompensatingTransactionHolderSupport;
import org.springframework.transaction.compensating.support.DefaultCompensatingTransactionOperationManager;
import org.springframework.util.Assert;

public class ContextSourceTransactionManagerDelegate
extends AbstractCompensatingTransactionManagerDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(ContextSourceTransactionManagerDelegate.class);
    private ContextSource contextSource;
    private TempEntryRenamingStrategy renamingStrategy;

    public void setContextSource(ContextSource contextSource) {
        AbstractContextSource abstractContextSource;
        if (contextSource instanceof TransactionAwareContextSourceProxy) {
            TransactionAwareContextSourceProxy proxy = (TransactionAwareContextSourceProxy)contextSource;
            this.contextSource = proxy.getTarget();
        } else {
            this.contextSource = contextSource;
        }
        if (contextSource instanceof AbstractContextSource && (abstractContextSource = (AbstractContextSource)contextSource).isAnonymousReadOnly()) {
            throw new IllegalArgumentException("Compensating LDAP transactions cannot be used when context-source is anonymous-read-only");
        }
    }

    public ContextSource getContextSource() {
        return this.contextSource;
    }

    @Override
    protected Object getTransactionSynchronizationKey() {
        return this.getContextSource();
    }

    @Override
    protected CompensatingTransactionHolderSupport getNewHolder() {
        DirContext newCtx = this.getContextSource().getReadWriteContext();
        return new DirContextHolder(new DefaultCompensatingTransactionOperationManager(new LdapCompensatingTransactionOperationFactory(this.renamingStrategy)), newCtx);
    }

    @Override
    protected void closeTargetResource(CompensatingTransactionHolderSupport transactionHolderSupport) {
        DirContextHolder contextHolder = (DirContextHolder)transactionHolderSupport;
        DirContext ctx = contextHolder.getCtx();
        try {
            LOG.debug("Closing target context");
            ctx.close();
        }
        catch (NamingException e) {
            LOG.warn("Failed to close target context", (Throwable)e);
        }
    }

    public void setRenamingStrategy(TempEntryRenamingStrategy renamingStrategy) {
        this.renamingStrategy = renamingStrategy;
    }

    void checkRenamingStrategy() {
        Assert.notNull((Object)this.renamingStrategy, (String)"RenamingStrategy must be specified");
    }
}

