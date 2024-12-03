/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jmx.internal;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.hibernate.HibernateException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jmx.spi.JmxService;
import org.hibernate.service.Service;
import org.hibernate.service.spi.Manageable;
import org.hibernate.service.spi.OptionallyManageable;
import org.hibernate.service.spi.Stoppable;

public class JmxServiceImpl
implements JmxService,
Stoppable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(JmxServiceImpl.class);
    public static final String OBJ_NAME_TEMPLATE = "%s:sessionFactory=%s,serviceRole=%s,serviceType=%s";
    private final boolean usePlatformServer;
    private final String agentId;
    private final String defaultDomain;
    private final String sessionFactoryName;
    private boolean startedServer;
    private ArrayList<ObjectName> registeredMBeans;

    public JmxServiceImpl(Map configValues) {
        this.usePlatformServer = ConfigurationHelper.getBoolean("hibernate.jmx.usePlatformServer", configValues);
        this.agentId = (String)configValues.get("hibernate.jmx.agentId");
        this.defaultDomain = (String)configValues.get("hibernate.jmx.defaultDomain");
        String defaultSessionFactoryName = ConfigurationHelper.getString("hibernate.session_factory_name", configValues);
        if (defaultSessionFactoryName == null) {
            defaultSessionFactoryName = ConfigurationHelper.getString("hibernate.persistenceUnitName", configValues);
        }
        this.sessionFactoryName = ConfigurationHelper.getString("hibernate.jmx.sessionFactoryName", configValues, defaultSessionFactoryName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stop() {
        try {
            if (this.startedServer || this.registeredMBeans != null) {
                MBeanServer mBeanServer = this.findServer();
                if (mBeanServer == null) {
                    LOG.unableToLocateMBeanServer();
                    return;
                }
                if (this.registeredMBeans != null) {
                    for (ObjectName objectName : this.registeredMBeans) {
                        try {
                            LOG.tracev("Unregistering registered MBean [ON={0}]", objectName);
                            mBeanServer.unregisterMBean(objectName);
                        }
                        catch (Exception e) {
                            LOG.debugf("Unable to unregsiter registered MBean [ON=%s] : %s", objectName, e.toString());
                        }
                    }
                }
                if (this.startedServer) {
                    LOG.trace("Attempting to release created MBeanServer");
                    try {
                        MBeanServerFactory.releaseMBeanServer(mBeanServer);
                    }
                    catch (Exception e) {
                        LOG.unableToReleaseCreatedMBeanServer(e.toString());
                    }
                }
            }
        }
        finally {
            this.startedServer = false;
            if (this.registeredMBeans != null) {
                this.registeredMBeans.clear();
                this.registeredMBeans = null;
            }
        }
    }

    @Override
    public void registerService(Manageable service, Class<? extends Service> serviceRole) {
        if (service == null) {
            return;
        }
        DeprecationLogger.DEPRECATION_LOGGER.deprecatedJmxManageableServiceRegistration(service.getClass().getName());
        if (OptionallyManageable.class.isInstance(service)) {
            for (Manageable realManageable : ((OptionallyManageable)service).getRealManageables()) {
                this.registerService(realManageable, serviceRole);
            }
            return;
        }
        String domain = service.getManagementDomain() == null ? "org.hibernate.core" : service.getManagementDomain();
        String serviceType = service.getManagementServiceType() == null ? service.getClass().getName() : service.getManagementServiceType();
        try {
            ObjectName objectName = new ObjectName(String.format(OBJ_NAME_TEMPLATE, domain, this.sessionFactoryName, serviceRole.getName(), serviceType));
            this.registerMBean(objectName, service.getManagementBean());
        }
        catch (MalformedObjectNameException e) {
            throw new HibernateException("Unable to generate service IbjectName", e);
        }
    }

    @Override
    public void registerMBean(ObjectName objectName, Object mBean) {
        DeprecationLogger.DEPRECATION_LOGGER.deprecatedJmxBeanRegistration(mBean.getClass().getName());
        MBeanServer mBeanServer = this.findServer();
        if (mBeanServer == null) {
            if (this.startedServer) {
                throw new HibernateException("Could not locate previously started MBeanServer");
            }
            mBeanServer = this.startMBeanServer();
            this.startedServer = true;
        }
        try {
            mBeanServer.registerMBean(mBean, objectName);
            if (this.registeredMBeans == null) {
                this.registeredMBeans = new ArrayList();
            }
            this.registeredMBeans.add(objectName);
        }
        catch (Exception e) {
            throw new HibernateException("Unable to register MBean [ON=" + objectName + "]", e);
        }
    }

    private MBeanServer findServer() {
        if (this.usePlatformServer) {
            return ManagementFactory.getPlatformMBeanServer();
        }
        ArrayList<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(this.agentId);
        if (this.defaultDomain == null) {
            return mbeanServers.get(0);
        }
        for (MBeanServer mbeanServer : mbeanServers) {
            if (!this.defaultDomain.equals(mbeanServer.getDefaultDomain())) continue;
            return mbeanServer;
        }
        return null;
    }

    private MBeanServer startMBeanServer() {
        try {
            return MBeanServerFactory.createMBeanServer(this.defaultDomain);
        }
        catch (Exception e) {
            throw new HibernateException("Unable to start MBeanServer", e);
        }
    }
}

