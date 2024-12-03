/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

public class WildFlyStandAloneJtaPlatform
extends AbstractJtaPlatform {
    public static final String WILDFLY_TM_CLASS_NAME = "org.wildfly.transaction.client.ContextTransactionManager";
    public static final String WILDFLY_UT_CLASS_NAME = "org.wildfly.transaction.client.LocalUserTransaction";

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            Class wildflyTmClass = this.serviceRegistry().getService(ClassLoaderService.class).classForName(WILDFLY_TM_CLASS_NAME);
            return (TransactionManager)wildflyTmClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            throw new JtaPlatformException("Could not obtain WildFly Transaction Client transaction manager instance", e);
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        try {
            Class jbossUtClass = this.serviceRegistry().getService(ClassLoaderService.class).classForName(WILDFLY_UT_CLASS_NAME);
            return (UserTransaction)jbossUtClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            throw new JtaPlatformException("Could not obtain WildFly Transaction Client user transaction instance", e);
        }
    }
}

