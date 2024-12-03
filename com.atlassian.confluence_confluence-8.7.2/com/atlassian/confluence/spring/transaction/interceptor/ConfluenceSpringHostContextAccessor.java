/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.spi.HostContextAccessor$HostTransactionCallback
 *  com.atlassian.sal.spring.component.SpringHostContextAccessor
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.WordUtils
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.spring.transaction.interceptor;

import com.atlassian.confluence.impl.util.Memoizer;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.confluence.vcache.VCacheRequestContextOperations;
import com.atlassian.sal.spi.HostContextAccessor;
import com.atlassian.sal.spring.component.SpringHostContextAccessor;
import java.util.function.BiFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class ConfluenceSpringHostContextAccessor
extends SpringHostContextAccessor
implements TransactionalHostContextAccessor {
    private final BiFunction<TransactionalHostContextAccessor.Propagation, TransactionalHostContextAccessor.Permission, TransactionTemplate> txTemplates;

    public ConfluenceSpringHostContextAccessor(PlatformTransactionManager transactionManager) {
        super(transactionManager);
        DefaultTransactionDefinition baseTxDef = this.getTransactionDefinition();
        this.txTemplates = Memoizer.memoize((arg_0, arg_1) -> ConfluenceSpringHostContextAccessor.lambda$new$0((TransactionDefinition)baseTxDef, transactionManager, arg_0, arg_1));
    }

    @Deprecated
    public ConfluenceSpringHostContextAccessor(PlatformTransactionManager transactionManager, VCacheRequestContextOperations ignored) {
        this(transactionManager);
    }

    @Override
    public <T> T doInTransaction(TransactionalHostContextAccessor.Propagation propagation, HostContextAccessor.HostTransactionCallback<T> callback) {
        return this.doInTransaction(propagation, TransactionalHostContextAccessor.Permission.READ_WRITE, callback);
    }

    @Override
    public <T> T doInTransaction(TransactionalHostContextAccessor.Permission permission, HostContextAccessor.HostTransactionCallback<T> callback) {
        return this.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRED, permission, callback);
    }

    @Override
    public <T> T doInTransaction(TransactionalHostContextAccessor.Propagation propagation, TransactionalHostContextAccessor.Permission permission, HostContextAccessor.HostTransactionCallback<T> callback) {
        TransactionTemplate txTemplate = this.txTemplates.apply(propagation, permission);
        return (T)txTemplate.execute(transactionStatus -> {
            try {
                return callback.doInTransaction();
            }
            catch (RuntimeException e) {
                transactionStatus.setRollbackOnly();
                throw e;
            }
        });
    }

    private static String transactionDefinitionName(TransactionalHostContextAccessor.Propagation propagation, TransactionalHostContextAccessor.Permission permission) {
        return "plugin" + StringUtils.strip((String)WordUtils.capitalizeFully((String)(propagation + "_" + permission), (char[])new char[]{'_'}), (String)"_") + "Tx";
    }

    private static void setTransactionPropagation(DefaultTransactionDefinition transactionDefinition, TransactionalHostContextAccessor.Propagation propagation) {
        switch (propagation) {
            case REQUIRED: {
                transactionDefinition.setPropagationBehavior(0);
                return;
            }
            case REQUIRES_NEW: {
                transactionDefinition.setPropagationBehavior(3);
                return;
            }
            case MANDATORY: {
                transactionDefinition.setPropagationBehavior(2);
                return;
            }
        }
        throw new UnsupportedOperationException("Unhandled propagation type " + propagation);
    }

    private static void setTransactionPermission(DefaultTransactionDefinition transactionDefinition, TransactionalHostContextAccessor.Permission permission) {
        switch (permission) {
            case READ_ONLY: {
                transactionDefinition.setReadOnly(true);
                return;
            }
            case READ_WRITE: {
                transactionDefinition.setReadOnly(false);
                return;
            }
        }
        throw new UnsupportedOperationException("Unhandled permission type " + permission);
    }

    private static /* synthetic */ TransactionTemplate lambda$new$0(TransactionDefinition baseTxDef, PlatformTransactionManager transactionManager, TransactionalHostContextAccessor.Propagation prop, TransactionalHostContextAccessor.Permission perm) {
        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(baseTxDef);
        txDef.setName(ConfluenceSpringHostContextAccessor.transactionDefinitionName(prop, perm));
        ConfluenceSpringHostContextAccessor.setTransactionPropagation(txDef, prop);
        ConfluenceSpringHostContextAccessor.setTransactionPermission(txDef, perm);
        return new TransactionTemplate(transactionManager, (TransactionDefinition)txDef);
    }
}

