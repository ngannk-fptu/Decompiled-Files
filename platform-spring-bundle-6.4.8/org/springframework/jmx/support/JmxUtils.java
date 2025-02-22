/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.jmx.support;

import java.beans.PropertyDescriptor;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.management.DynamicMBean;
import javax.management.JMX;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class JmxUtils {
    public static final String IDENTITY_OBJECT_NAME_KEY = "identity";
    private static final String MBEAN_SUFFIX = "MBean";
    private static final Log logger = LogFactory.getLog(JmxUtils.class);

    public static MBeanServer locateMBeanServer() throws MBeanServerNotFoundException {
        return JmxUtils.locateMBeanServer(null);
    }

    public static MBeanServer locateMBeanServer(@Nullable String agentId) throws MBeanServerNotFoundException {
        ArrayList<MBeanServer> servers;
        MBeanServer server = null;
        if (!"".equals(agentId) && !CollectionUtils.isEmpty(servers = MBeanServerFactory.findMBeanServer(agentId))) {
            if (servers.size() > 1 && logger.isInfoEnabled()) {
                logger.info((Object)("Found more than one MBeanServer instance" + (agentId != null ? " with agent id [" + agentId + "]" : "") + ". Returning first from list."));
            }
            server = (MBeanServer)servers.get(0);
        }
        if (server == null && !StringUtils.hasLength(agentId)) {
            try {
                server = ManagementFactory.getPlatformMBeanServer();
            }
            catch (SecurityException ex) {
                throw new MBeanServerNotFoundException("No specific MBeanServer found, and not allowed to obtain the Java platform MBeanServer", ex);
            }
        }
        if (server == null) {
            throw new MBeanServerNotFoundException("Unable to locate an MBeanServer instance" + (agentId != null ? " with agent id [" + agentId + "]" : ""));
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Found MBeanServer: " + server));
        }
        return server;
    }

    @Nullable
    public static Class<?>[] parameterInfoToTypes(@Nullable MBeanParameterInfo[] paramInfo) throws ClassNotFoundException {
        return JmxUtils.parameterInfoToTypes(paramInfo, ClassUtils.getDefaultClassLoader());
    }

    @Nullable
    public static Class<?>[] parameterInfoToTypes(@Nullable MBeanParameterInfo[] paramInfo, @Nullable ClassLoader classLoader) throws ClassNotFoundException {
        Class[] types = null;
        if (paramInfo != null && paramInfo.length > 0) {
            types = new Class[paramInfo.length];
            for (int x = 0; x < paramInfo.length; ++x) {
                types[x] = ClassUtils.forName(paramInfo[x].getType(), classLoader);
            }
        }
        return types;
    }

    public static String[] getMethodSignature(Method method) {
        Class<?>[] types = method.getParameterTypes();
        String[] signature = new String[types.length];
        for (int x = 0; x < types.length; ++x) {
            signature[x] = types[x].getName();
        }
        return signature;
    }

    public static String getAttributeName(PropertyDescriptor property, boolean useStrictCasing) {
        if (useStrictCasing) {
            return StringUtils.capitalize(property.getName());
        }
        return property.getName();
    }

    public static ObjectName appendIdentityToObjectName(ObjectName objectName, Object managedResource) throws MalformedObjectNameException {
        Hashtable<String, String> keyProperties = objectName.getKeyPropertyList();
        keyProperties.put(IDENTITY_OBJECT_NAME_KEY, ObjectUtils.getIdentityHexString(managedResource));
        return ObjectNameManager.getInstance(objectName.getDomain(), keyProperties);
    }

    public static Class<?> getClassToExpose(Object managedBean) {
        return ClassUtils.getUserClass(managedBean);
    }

    public static Class<?> getClassToExpose(Class<?> clazz) {
        return ClassUtils.getUserClass(clazz);
    }

    public static boolean isMBean(@Nullable Class<?> clazz) {
        return clazz != null && (DynamicMBean.class.isAssignableFrom(clazz) || JmxUtils.getMBeanInterface(clazz) != null || JmxUtils.getMXBeanInterface(clazz) != null);
    }

    @Nullable
    public static Class<?> getMBeanInterface(@Nullable Class<?> clazz) {
        Class<?>[] implementedInterfaces;
        if (clazz == null || clazz.getSuperclass() == null) {
            return null;
        }
        String mbeanInterfaceName = clazz.getName() + MBEAN_SUFFIX;
        for (Class<?> iface : implementedInterfaces = clazz.getInterfaces()) {
            if (!iface.getName().equals(mbeanInterfaceName)) continue;
            return iface;
        }
        return JmxUtils.getMBeanInterface(clazz.getSuperclass());
    }

    @Nullable
    public static Class<?> getMXBeanInterface(@Nullable Class<?> clazz) {
        Class<?>[] implementedInterfaces;
        if (clazz == null || clazz.getSuperclass() == null) {
            return null;
        }
        for (Class<?> iface : implementedInterfaces = clazz.getInterfaces()) {
            if (!JMX.isMXBeanInterface(iface)) continue;
            return iface;
        }
        return JmxUtils.getMXBeanInterface(clazz.getSuperclass());
    }
}

