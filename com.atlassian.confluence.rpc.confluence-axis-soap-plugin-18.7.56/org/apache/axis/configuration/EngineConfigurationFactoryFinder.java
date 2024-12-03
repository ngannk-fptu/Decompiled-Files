/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.axis.AxisProperties;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.discovery.ResourceClassIterator;
import org.apache.commons.discovery.tools.ClassUtils;
import org.apache.commons.logging.Log;

public class EngineConfigurationFactoryFinder {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$configuration$EngineConfigurationFactoryFinder == null ? (class$org$apache$axis$configuration$EngineConfigurationFactoryFinder = EngineConfigurationFactoryFinder.class$("org.apache.axis.configuration.EngineConfigurationFactoryFinder")) : class$org$apache$axis$configuration$EngineConfigurationFactoryFinder).getName());
    private static final Class mySpi = class$org$apache$axis$EngineConfigurationFactory == null ? (class$org$apache$axis$EngineConfigurationFactory = EngineConfigurationFactoryFinder.class$("org.apache.axis.EngineConfigurationFactory")) : class$org$apache$axis$EngineConfigurationFactory;
    private static final Class[] newFactoryParamTypes = new Class[]{class$java$lang$Object == null ? (class$java$lang$Object = EngineConfigurationFactoryFinder.class$("java.lang.Object")) : class$java$lang$Object};
    private static final String requiredMethod = "public static EngineConfigurationFactory newFactory(Object)";
    static /* synthetic */ Class class$org$apache$axis$configuration$EngineConfigurationFactoryFinder;
    static /* synthetic */ Class class$org$apache$axis$EngineConfigurationFactory;
    static /* synthetic */ Class class$java$lang$Object;

    private EngineConfigurationFactoryFinder() {
    }

    public static EngineConfigurationFactory newFactory(Object obj) {
        final Object[] params = new Object[]{obj};
        return (EngineConfigurationFactory)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                ResourceClassIterator services = AxisProperties.getResourceClassIterator(mySpi);
                Object factory = null;
                while (factory == null && services.hasNext()) {
                    try {
                        Class service = services.nextResourceClass().loadClass();
                        if (service == null) continue;
                        factory = EngineConfigurationFactoryFinder.newFactory(service, newFactoryParamTypes, params);
                    }
                    catch (Exception exception) {}
                }
                if (factory != null) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)Messages.getMessage("engineFactory", factory.getClass().getName()));
                    }
                } else {
                    log.error((Object)Messages.getMessage("engineConfigFactoryMissing"));
                }
                return factory;
            }
        });
    }

    public static EngineConfigurationFactory newFactory() {
        return EngineConfigurationFactoryFinder.newFactory(null);
    }

    private static EngineConfigurationFactory newFactory(Class service, Class[] paramTypes, Object[] param) {
        block8: {
            try {
                Method method = ClassUtils.findPublicStaticMethod(service, class$org$apache$axis$EngineConfigurationFactory == null ? (class$org$apache$axis$EngineConfigurationFactory = EngineConfigurationFactoryFinder.class$("org.apache.axis.EngineConfigurationFactory")) : class$org$apache$axis$EngineConfigurationFactory, "newFactory", paramTypes);
                if (method == null) {
                    log.warn((Object)Messages.getMessage("engineConfigMissingNewFactory", service.getName(), requiredMethod));
                    break block8;
                }
                try {
                    return (EngineConfigurationFactory)method.invoke(null, param);
                }
                catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof NoClassDefFoundError) {
                        log.debug((Object)Messages.getMessage("engineConfigLoadFactory", service.getName()));
                    } else {
                        log.warn((Object)Messages.getMessage("engineConfigInvokeNewFactory", service.getName(), requiredMethod), (Throwable)e);
                    }
                }
                catch (Exception e) {
                    log.warn((Object)Messages.getMessage("engineConfigInvokeNewFactory", service.getName(), requiredMethod), (Throwable)e);
                }
            }
            catch (NoClassDefFoundError e) {
                log.debug((Object)Messages.getMessage("engineConfigLoadFactory", service.getName()));
            }
        }
        return null;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        AxisProperties.setClassOverrideProperty(class$org$apache$axis$EngineConfigurationFactory == null ? (class$org$apache$axis$EngineConfigurationFactory = EngineConfigurationFactoryFinder.class$("org.apache.axis.EngineConfigurationFactory")) : class$org$apache$axis$EngineConfigurationFactory, "axis.EngineConfigFactory");
        AxisProperties.setClassDefaults(class$org$apache$axis$EngineConfigurationFactory == null ? (class$org$apache$axis$EngineConfigurationFactory = EngineConfigurationFactoryFinder.class$("org.apache.axis.EngineConfigurationFactory")) : class$org$apache$axis$EngineConfigurationFactory, new String[]{"org.apache.axis.configuration.EngineConfigurationFactoryServlet", "org.apache.axis.configuration.EngineConfigurationFactoryDefault"});
    }
}

