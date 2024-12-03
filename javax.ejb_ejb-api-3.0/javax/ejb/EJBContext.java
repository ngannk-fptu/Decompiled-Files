/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.UserTransaction
 */
package javax.ejb;

import java.security.Identity;
import java.security.Principal;
import java.util.Properties;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.TimerService;
import javax.transaction.UserTransaction;

public interface EJBContext {
    public EJBHome getEJBHome();

    public EJBLocalHome getEJBLocalHome();

    public Properties getEnvironment();

    public Identity getCallerIdentity();

    public Principal getCallerPrincipal();

    public boolean isCallerInRole(Identity var1);

    public boolean isCallerInRole(String var1);

    public UserTransaction getUserTransaction() throws IllegalStateException;

    public void setRollbackOnly() throws IllegalStateException;

    public boolean getRollbackOnly() throws IllegalStateException;

    public TimerService getTimerService() throws IllegalStateException;

    public Object lookup(String var1);
}

