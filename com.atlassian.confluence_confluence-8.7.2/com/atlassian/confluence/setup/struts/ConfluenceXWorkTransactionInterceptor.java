/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.atlassian.xwork.interceptors.XWorkTransactionInterceptor
 *  com.opensymphony.xwork2.ActionInvocation
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.setup.struts;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.DefaultSetupPersister;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.xwork.interceptors.XWorkTransactionInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import org.springframework.transaction.PlatformTransactionManager;

public class ConfluenceXWorkTransactionInterceptor
extends XWorkTransactionInterceptor {
    private final Supplier<PlatformTransactionManager> transactionManager = new LazyComponentReference("transactionManager");

    public PlatformTransactionManager getTransactionManager() {
        return (PlatformTransactionManager)this.transactionManager.get();
    }

    protected boolean shouldIntercept(ActionInvocation invocation) {
        return this.isHibernateSetup() && this.isSafeForMigration();
    }

    private boolean isSafeForMigration() {
        String setupType = BootstrapUtils.getBootstrapManager().getApplicationConfig().getSetupType();
        return !DefaultSetupPersister.MIGRATION_SETUP_TYPES.contains(setupType) || GeneralUtil.isSetupComplete();
    }

    private boolean isHibernateSetup() {
        return BootstrapUtils.getBootstrapManager().getHibernateConfig().isHibernateSetup();
    }
}

