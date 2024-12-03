/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.transaction.compensating.manager;

import javax.naming.directory.DirContext;
import org.springframework.transaction.compensating.CompensatingTransactionOperationManager;
import org.springframework.transaction.compensating.support.CompensatingTransactionHolderSupport;

public class DirContextHolder
extends CompensatingTransactionHolderSupport {
    private DirContext ctx;

    public DirContextHolder(CompensatingTransactionOperationManager manager, DirContext ctx) {
        super(manager);
        this.ctx = ctx;
    }

    public void setCtx(DirContext ctx) {
        this.ctx = ctx;
    }

    public DirContext getCtx() {
        return this.ctx;
    }

    @Override
    protected Object getTransactedResource() {
        return this.ctx;
    }
}

