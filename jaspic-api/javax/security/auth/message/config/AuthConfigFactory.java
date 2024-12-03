/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl
 */
package javax.security.auth.message.config;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.Security;
import java.security.SecurityPermission;
import java.util.Map;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.RegistrationListener;
import org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl;

public abstract class AuthConfigFactory {
    public static final String DEFAULT_FACTORY_SECURITY_PROPERTY = "authconfigprovider.factory";
    public static final String GET_FACTORY_PERMISSION_NAME = "getProperty.authconfigprovider.factory";
    public static final String SET_FACTORY_PERMISSION_NAME = "setProperty.authconfigprovider.factory";
    public static final String PROVIDER_REGISTRATION_PERMISSION_NAME = "setProperty.authconfigfactory.provider";
    public static final SecurityPermission getFactorySecurityPermission = new SecurityPermission("getProperty.authconfigprovider.factory");
    public static final SecurityPermission setFactorySecurityPermission = new SecurityPermission("setProperty.authconfigprovider.factory");
    public static final SecurityPermission providerRegistrationSecurityPermission = new SecurityPermission("setProperty.authconfigfactory.provider");
    private static final String DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL = "org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl";
    private static volatile AuthConfigFactory factory;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static AuthConfigFactory getFactory() {
        AuthConfigFactory.checkPermission(getFactorySecurityPermission);
        if (factory != null) {
            return factory;
        }
        Class<AuthConfigFactory> clazz = AuthConfigFactory.class;
        synchronized (AuthConfigFactory.class) {
            if (factory == null) {
                String className = AuthConfigFactory.getFactoryClassName();
                try {
                    factory = AccessController.doPrivileged(() -> {
                        if (className.equals(DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL)) {
                            return new AuthConfigFactoryImpl();
                        }
                        Class<?> clazz = Class.forName(className);
                        return (AuthConfigFactory)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    });
                }
                catch (PrivilegedActionException e) {
                    Exception inner = e.getException();
                    if (inner instanceof InstantiationException) {
                        throw new SecurityException("AuthConfigFactory error:" + inner.getCause().getMessage(), inner.getCause());
                    }
                    throw new SecurityException("AuthConfigFactory error: " + inner, inner);
                }
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return factory;
        }
    }

    public static synchronized void setFactory(AuthConfigFactory factory) {
        AuthConfigFactory.checkPermission(setFactorySecurityPermission);
        AuthConfigFactory.factory = factory;
    }

    public abstract AuthConfigProvider getConfigProvider(String var1, String var2, RegistrationListener var3);

    public abstract String registerConfigProvider(String var1, Map var2, String var3, String var4, String var5);

    public abstract String registerConfigProvider(AuthConfigProvider var1, String var2, String var3, String var4);

    public abstract boolean removeRegistration(String var1);

    public abstract String[] detachListener(RegistrationListener var1, String var2, String var3);

    public abstract String[] getRegistrationIDs(AuthConfigProvider var1);

    public abstract RegistrationContext getRegistrationContext(String var1);

    public abstract void refresh();

    private static void checkPermission(Permission permission) {
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(permission);
        }
    }

    private static String getFactoryClassName() {
        String className = AccessController.doPrivileged(() -> Security.getProperty(DEFAULT_FACTORY_SECURITY_PROPERTY));
        if (className != null) {
            return className;
        }
        return DEFAULT_JASPI_AUTHCONFIGFACTORYIMPL;
    }

    public static interface RegistrationContext {
        public String getMessageLayer();

        public String getAppContext();

        public String getDescription();

        public boolean isPersistent();
    }
}

