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
import org.hibernate.engine.transaction.jta.platform.internal.WildFlyStandAloneJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

public class JBossStandAloneJtaPlatform
extends AbstractJtaPlatform {
    public static final String JBOSS_TM_CLASS_NAME = "com.arjuna.ats.jta.TransactionManager";
    public static final String JBOSS_UT_CLASS_NAME = "com.arjuna.ats.jta.UserTransaction";
    private static final WildFlyStandAloneJtaPlatform wildflyBasedAlternative = new WildFlyStandAloneJtaPlatform();

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            return wildflyBasedAlternative.locateTransactionManager();
        }
        catch (Exception exception) {
            try {
                Class jbossTmClass = this.serviceRegistry().getService(ClassLoaderService.class).classForName(JBOSS_TM_CLASS_NAME);
                return (TransactionManager)jbossTmClass.getMethod("transactionManager", new Class[0]).invoke(null, new Object[0]);
            }
            catch (Exception e) {
                throw new JtaPlatformException("Could not obtain JBoss Transactions transaction manager instance", e);
            }
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        try {
            return wildflyBasedAlternative.locateUserTransaction();
        }
        catch (Exception exception) {
            try {
                Class jbossUtClass = this.serviceRegistry().getService(ClassLoaderService.class).classForName(JBOSS_UT_CLASS_NAME);
                return (UserTransaction)jbossUtClass.getMethod("userTransaction", new Class[0]).invoke(null, new Object[0]);
            }
            catch (Exception e) {
                throw new JtaPlatformException("Could not obtain JBoss Transactions user transaction instance", e);
            }
        }
    }
}

