/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  javax.transaction.UserTransaction
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.transaction.jta.platform.internal;

import java.lang.reflect.Method;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;
import org.jboss.logging.Logger;

public class WebSphereJtaPlatform
extends AbstractJtaPlatform {
    private static final Logger log = Logger.getLogger(WebSphereJtaPlatform.class);
    private final Class transactionManagerAccessClass;
    private final WebSphereEnvironment webSphereEnvironment;

    public WebSphereJtaPlatform() {
        Class<?> tmAccessClass = null;
        WebSphereEnvironment webSphereEnvironment = null;
        for (WebSphereEnvironment check : WebSphereEnvironment.values()) {
            try {
                tmAccessClass = Class.forName(check.getTmAccessClassName());
                webSphereEnvironment = check;
                log.debugf("WebSphere version : %s", (Object)webSphereEnvironment.getWebSphereVersion());
                break;
            }
            catch (Exception exception) {
            }
        }
        if (webSphereEnvironment == null) {
            throw new JtaPlatformException("Could not locate WebSphere TransactionManager access class");
        }
        this.transactionManagerAccessClass = tmAccessClass;
        this.webSphereEnvironment = webSphereEnvironment;
    }

    public WebSphereJtaPlatform(Class transactionManagerAccessClass, WebSphereEnvironment webSphereEnvironment) {
        this.transactionManagerAccessClass = transactionManagerAccessClass;
        this.webSphereEnvironment = webSphereEnvironment;
    }

    @Override
    protected TransactionManager locateTransactionManager() {
        try {
            Method method = this.transactionManagerAccessClass.getMethod("getTransactionManager", new Class[0]);
            return (TransactionManager)method.invoke(null, new Object[0]);
        }
        catch (Exception e) {
            throw new JtaPlatformException("Could not obtain WebSphere TransactionManager", e);
        }
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        String utName = this.webSphereEnvironment.getUtName();
        return (UserTransaction)this.jndiService().locate(utName);
    }

    public static enum WebSphereEnvironment {
        WS_4_0("4.x", "com.ibm.ejs.jts.jta.JTSXA", "jta/usertransaction"),
        WS_5_0("5.0", "com.ibm.ejs.jts.jta.TransactionManagerFactory", "java:comp/UserTransaction"),
        WS_5_1("5.1", "com.ibm.ws.Transaction.TransactionManagerFactory", "java:comp/UserTransaction");

        private final String webSphereVersion;
        private final String tmAccessClassName;
        private final String utName;

        private WebSphereEnvironment(String webSphereVersion, String tmAccessClassName, String utName) {
            this.webSphereVersion = webSphereVersion;
            this.tmAccessClassName = tmAccessClassName;
            this.utName = utName;
        }

        public String getWebSphereVersion() {
            return this.webSphereVersion;
        }

        public String getTmAccessClassName() {
            return this.tmAccessClassName;
        }

        public String getUtName() {
            return this.utName;
        }
    }
}

