/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.jmx.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.management.MBeanServer;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.lang.Nullable;

public class WebSphereMBeanServerFactoryBean
implements FactoryBean<MBeanServer>,
InitializingBean {
    private static final String ADMIN_SERVICE_FACTORY_CLASS = "com.ibm.websphere.management.AdminServiceFactory";
    private static final String GET_MBEAN_FACTORY_METHOD = "getMBeanFactory";
    private static final String GET_MBEAN_SERVER_METHOD = "getMBeanServer";
    @Nullable
    private MBeanServer mbeanServer;

    public void afterPropertiesSet() throws MBeanServerNotFoundException {
        try {
            Class<?> adminServiceClass = this.getClass().getClassLoader().loadClass(ADMIN_SERVICE_FACTORY_CLASS);
            Method getMBeanFactoryMethod = adminServiceClass.getMethod(GET_MBEAN_FACTORY_METHOD, new Class[0]);
            Object mbeanFactory = getMBeanFactoryMethod.invoke(null, new Object[0]);
            Method getMBeanServerMethod = mbeanFactory.getClass().getMethod(GET_MBEAN_SERVER_METHOD, new Class[0]);
            this.mbeanServer = (MBeanServer)getMBeanServerMethod.invoke(mbeanFactory, new Object[0]);
        }
        catch (ClassNotFoundException ex) {
            throw new MBeanServerNotFoundException("Could not find WebSphere's AdminServiceFactory class", ex);
        }
        catch (InvocationTargetException ex) {
            throw new MBeanServerNotFoundException("WebSphere's AdminServiceFactory.getMBeanFactory/getMBeanServer method failed", ex.getTargetException());
        }
        catch (Exception ex) {
            throw new MBeanServerNotFoundException("Could not access WebSphere's AdminServiceFactory.getMBeanFactory/getMBeanServer method", ex);
        }
    }

    @Nullable
    public MBeanServer getObject() {
        return this.mbeanServer;
    }

    public Class<? extends MBeanServer> getObjectType() {
        return this.mbeanServer != null ? this.mbeanServer.getClass() : MBeanServer.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

