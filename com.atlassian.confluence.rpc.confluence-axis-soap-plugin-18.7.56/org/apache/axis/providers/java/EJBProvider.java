/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.rmi.PortableRemoteObject
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.providers.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.ServerException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class EJBProvider
extends RPCProvider {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$providers$java$EJBProvider == null ? (class$org$apache$axis$providers$java$EJBProvider = EJBProvider.class$("org.apache.axis.providers.java.EJBProvider")) : class$org$apache$axis$providers$java$EJBProvider).getName());
    protected static Log entLog = LogFactory.getLog("org.apache.axis.enterprise");
    public static final String OPTION_BEANNAME = "beanJndiName";
    public static final String OPTION_HOMEINTERFACENAME = "homeInterfaceName";
    public static final String OPTION_REMOTEINTERFACENAME = "remoteInterfaceName";
    public static final String OPTION_LOCALHOMEINTERFACENAME = "localHomeInterfaceName";
    public static final String OPTION_LOCALINTERFACENAME = "localInterfaceName";
    public static final String jndiContextClass = "jndiContextClass";
    public static final String jndiURL = "jndiURL";
    public static final String jndiUsername = "jndiUser";
    public static final String jndiPassword = "jndiPassword";
    protected static final Class[] empty_class_array = new Class[0];
    protected static final Object[] empty_object_array = new Object[0];
    private static InitialContext cached_context = null;
    static /* synthetic */ Class class$org$apache$axis$providers$java$EJBProvider;

    protected Object makeNewServiceObject(MessageContext msgContext, String clsName) throws Exception {
        String homeName;
        String remoteHomeName = this.getStrOption(OPTION_HOMEINTERFACENAME, msgContext.getService());
        String localHomeName = this.getStrOption(OPTION_LOCALHOMEINTERFACENAME, msgContext.getService());
        String string = homeName = remoteHomeName != null ? remoteHomeName : localHomeName;
        if (homeName == null) {
            throw new AxisFault(Messages.getMessage("noOption00", OPTION_HOMEINTERFACENAME, msgContext.getTargetService()));
        }
        Class homeClass = ClassUtils.forName(homeName, true, msgContext.getClassLoader());
        if (remoteHomeName != null) {
            return this.createRemoteEJB(msgContext, clsName, homeClass);
        }
        return this.createLocalEJB(msgContext, clsName, homeClass);
    }

    private Object createRemoteEJB(MessageContext msgContext, String beanJndiName, Class homeClass) throws Exception {
        Object ejbHome = this.getEJBHome(msgContext.getService(), msgContext, beanJndiName);
        Object ehome = PortableRemoteObject.narrow((Object)ejbHome, (Class)homeClass);
        Method createMethod = homeClass.getMethod("create", empty_class_array);
        Object result = createMethod.invoke(ehome, empty_object_array);
        return result;
    }

    private Object createLocalEJB(MessageContext msgContext, String beanJndiName, Class homeClass) throws Exception {
        Object ejbHome = this.getEJBHome(msgContext.getService(), msgContext, beanJndiName);
        if (!homeClass.isInstance(ejbHome)) {
            throw new ClassCastException(Messages.getMessage("badEjbHomeType"));
        }
        Object ehome = ejbHome;
        Method createMethod = homeClass.getMethod("create", empty_class_array);
        Object result = createMethod.invoke(ehome, empty_object_array);
        return result;
    }

    private boolean isRemoteEjb(SOAPService service) {
        return this.getStrOption(OPTION_HOMEINTERFACENAME, service) != null;
    }

    private boolean isLocalEjb(SOAPService service) {
        return !this.isRemoteEjb(service) && this.getStrOption(OPTION_LOCALHOMEINTERFACENAME, service) != null;
    }

    protected String getServiceClassNameOptionName() {
        return OPTION_BEANNAME;
    }

    protected String getStrOption(String optionName, Handler service) {
        String value = null;
        if (service != null) {
            value = (String)service.getOption(optionName);
        }
        if (value == null) {
            value = (String)this.getOption(optionName);
        }
        return value;
    }

    private Class getRemoteInterfaceClassFromHome(String beanJndiName, SOAPService service, MessageContext msgContext) throws Exception {
        Object ejbHome = this.getEJBHome(service, msgContext, beanJndiName);
        String homeName = this.getStrOption(OPTION_HOMEINTERFACENAME, service);
        if (homeName == null) {
            throw new AxisFault(Messages.getMessage("noOption00", OPTION_HOMEINTERFACENAME, service.getName()));
        }
        ClassLoader cl = msgContext != null ? msgContext.getClassLoader() : Thread.currentThread().getContextClassLoader();
        Class homeClass = ClassUtils.forName(homeName, true, cl);
        Object ehome = PortableRemoteObject.narrow((Object)ejbHome, (Class)homeClass);
        Method getEJBMetaData = homeClass.getMethod("getEJBMetaData", empty_class_array);
        Object metaData = getEJBMetaData.invoke(ehome, empty_object_array);
        Method getRemoteInterfaceClass = metaData.getClass().getMethod("getRemoteInterfaceClass", empty_class_array);
        return (Class)getRemoteInterfaceClass.invoke(metaData, empty_object_array);
    }

    protected Class getServiceClass(String beanJndiName, SOAPService service, MessageContext msgContext) throws AxisFault {
        Class interfaceClass;
        block5: {
            interfaceClass = null;
            try {
                String interfaceName;
                String remoteInterfaceName = this.getStrOption(OPTION_REMOTEINTERFACENAME, service);
                String localInterfaceName = this.getStrOption(OPTION_LOCALINTERFACENAME, service);
                String string = interfaceName = remoteInterfaceName != null ? remoteInterfaceName : localInterfaceName;
                if (interfaceName != null) {
                    ClassLoader cl = msgContext != null ? msgContext.getClassLoader() : Thread.currentThread().getContextClassLoader();
                    interfaceClass = ClassUtils.forName(interfaceName, true, cl);
                    break block5;
                }
                if (this.isRemoteEjb(service)) {
                    interfaceClass = this.getRemoteInterfaceClassFromHome(beanJndiName, service, msgContext);
                    break block5;
                }
                if (this.isLocalEjb(service)) {
                    throw new AxisFault(Messages.getMessage("noOption00", OPTION_LOCALINTERFACENAME, service.getName()));
                }
                throw new AxisFault(Messages.getMessage("noOption00", OPTION_HOMEINTERFACENAME, service.getName()));
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
        }
        return interfaceClass;
    }

    private Object getEJBHome(SOAPService serviceHandler, MessageContext msgContext, String beanJndiName) throws AxisFault {
        Object ejbHome = null;
        try {
            InitialContext context;
            String contextUrl;
            String factoryClass;
            String password;
            Properties properties = null;
            String username = this.getStrOption(jndiUsername, serviceHandler);
            if (username == null && msgContext != null) {
                username = msgContext.getUsername();
            }
            if (username != null) {
                if (properties == null) {
                    properties = new Properties();
                }
                properties.setProperty("java.naming.security.principal", username);
            }
            if ((password = this.getStrOption(jndiPassword, serviceHandler)) == null && msgContext != null) {
                password = msgContext.getPassword();
            }
            if (password != null) {
                if (properties == null) {
                    properties = new Properties();
                }
                properties.setProperty("java.naming.security.credentials", password);
            }
            if ((factoryClass = this.getStrOption(jndiContextClass, serviceHandler)) != null) {
                if (properties == null) {
                    properties = new Properties();
                }
                properties.setProperty("java.naming.factory.initial", factoryClass);
            }
            if ((contextUrl = this.getStrOption(jndiURL, serviceHandler)) != null) {
                if (properties == null) {
                    properties = new Properties();
                }
                properties.setProperty("java.naming.provider.url", contextUrl);
            }
            if ((context = this.getContext(properties)) == null) {
                throw new AxisFault(Messages.getMessage("cannotCreateInitialContext00"));
            }
            ejbHome = this.getEJBHome(context, beanJndiName);
            if (ejbHome == null) {
                throw new AxisFault(Messages.getMessage("cannotFindJNDIHome00", beanJndiName));
            }
        }
        catch (Exception exception) {
            entLog.info((Object)Messages.getMessage("toAxisFault00"), (Throwable)exception);
            throw AxisFault.makeFault(exception);
        }
        return ejbHome;
    }

    protected InitialContext getCachedContext() throws NamingException {
        if (cached_context == null) {
            cached_context = new InitialContext();
        }
        return cached_context;
    }

    protected InitialContext getContext(Properties properties) throws AxisFault, NamingException {
        return properties == null ? this.getCachedContext() : new InitialContext(properties);
    }

    protected Object getEJBHome(InitialContext context, String beanJndiName) throws AxisFault, NamingException {
        return context.lookup(beanJndiName);
    }

    protected Object invokeMethod(MessageContext msgContext, Method method, Object obj, Object[] argValues) throws Exception {
        try {
            return super.invokeMethod(msgContext, method, obj, argValues);
        }
        catch (InvocationTargetException ite) {
            Throwable cause = this.getCause(ite);
            if (cause instanceof ServerException) {
                throw new InvocationTargetException(this.getCause(cause));
            }
            throw ite;
        }
    }

    private Throwable getCause(Throwable original) {
        try {
            Method method = original.getClass().getMethod("getCause", null);
            Throwable cause = (Throwable)method.invoke((Object)original, null);
            if (cause != null) {
                return cause;
            }
        }
        catch (NoSuchMethodException nsme) {
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return original;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

