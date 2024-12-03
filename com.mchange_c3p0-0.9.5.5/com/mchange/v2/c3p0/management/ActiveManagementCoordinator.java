/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.management;

import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.management.C3P0RegistryManager;
import com.mchange.v2.c3p0.management.DynamicPooledDataSourceManagerMBean;
import com.mchange.v2.c3p0.management.ManagementCoordinator;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ActiveManagementCoordinator
implements ManagementCoordinator {
    public static final String C3P0_REGISTRY_NAME_KEY = "com.mchange.v2.c3p0.management.RegistryName";
    private static final String C3P0_REGISTRY_NAME_PFX = "com.mchange.v2.c3p0:type=C3P0Registry";
    public static final String EXCLUDE_IDENTITY_TOKEN_KEY = "com.mchange.v2.c3p0.management.ExcludeIdentityToken";
    static final MLogger logger = MLog.getLogger(ActiveManagementCoordinator.class);
    static final boolean EXCLUDE_IDENTITY_TOKEN;
    final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    final String regName = ActiveManagementCoordinator.getRegistryName();

    @Override
    public void attemptManageC3P0Registry() {
        block4: {
            try {
                ObjectName name = new ObjectName(this.regName);
                C3P0RegistryManager mbean = new C3P0RegistryManager();
                if (this.mbs.isRegistered(name)) {
                    if (logger.isLoggable(MLevel.WARNING)) {
                        logger.warning("A C3P0Registry mbean is already registered. This probably means that an application using c3p0 was undeployed, but not all PooledDataSources were closed prior to undeployment. This may lead to resource leaks over time. Please take care to close all PooledDataSources.");
                    }
                    this.mbs.unregisterMBean(name);
                }
                this.mbs.registerMBean(mbean, name);
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block4;
                logger.log(MLevel.WARNING, "Failed to set up C3P0RegistryManager mBean. [c3p0 will still function normally, but management via JMX may not be possible.]", (Throwable)e);
            }
        }
    }

    @Override
    public void attemptUnmanageC3P0Registry() {
        block6: {
            try {
                ObjectName name = new ObjectName(this.regName);
                if (this.mbs.isRegistered(name)) {
                    this.mbs.unregisterMBean(name);
                    if (logger.isLoggable(MLevel.FINER)) {
                        logger.log(MLevel.FINER, "C3P0Registry mbean unregistered.");
                    }
                } else if (logger.isLoggable(MLevel.FINE)) {
                    logger.fine("The C3P0Registry mbean was not found in the registry, so could not be unregistered.");
                }
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block6;
                logger.log(MLevel.WARNING, "An Exception occurred while trying to unregister the C3P0RegistryManager mBean." + e);
            }
        }
    }

    @Override
    public void attemptManagePooledDataSource(PooledDataSource pds) {
        block3: {
            String name = null;
            try {
                name = ActiveManagementCoordinator.getPdsObjectNameStr(pds);
                ObjectName oname = new ObjectName(name);
                if (this.mbs.isRegistered(oname) && logger.isLoggable(MLevel.WARNING)) {
                    logger.warning("You are attempting to register an mbean '" + name + "', but an mbean by that name is already registered. The new mbean will replace the old one in the MBean server. " + (EXCLUDE_IDENTITY_TOKEN ? "Since you have excluded the guaranteed-unique identity token, you must take care to give each PooledDataSource a unique dataSourceName." : "This should not happen unless you have (pathologically) modified the DataSource's guaranteed-unique identityToken."));
                }
                DynamicPooledDataSourceManagerMBean dynamicPooledDataSourceManagerMBean = new DynamicPooledDataSourceManagerMBean(pds, name, this.mbs);
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block3;
                logger.log(MLevel.WARNING, "Failed to set up a PooledDataSourceManager mBean. [ " + (name == null ? pds.toString() : name) + " ] c3p0 will still function normally, but management of this DataSource by JMX may not be possible.", (Throwable)e);
            }
        }
    }

    @Override
    public void attemptUnmanagePooledDataSource(PooledDataSource pds) {
        block6: {
            String nameStr = null;
            try {
                nameStr = ActiveManagementCoordinator.getPdsObjectNameStr(pds);
                ObjectName name = new ObjectName(nameStr);
                if (this.mbs.isRegistered(name)) {
                    this.mbs.unregisterMBean(name);
                    if (logger.isLoggable(MLevel.FINE)) {
                        logger.log(MLevel.FINE, "MBean: " + nameStr + " unregistered.");
                    }
                } else if (logger.isLoggable(MLevel.FINE)) {
                    logger.fine("The mbean " + nameStr + " was not found in the registry, so could not be unregistered.");
                }
            }
            catch (Exception e) {
                if (!logger.isLoggable(MLevel.WARNING)) break block6;
                logger.log(MLevel.WARNING, "An Exception occurred while unregistering mBean. [" + (nameStr == null ? pds.toString() : nameStr) + "] ", (Throwable)e);
            }
        }
    }

    static String getPdsObjectNameStr(PooledDataSource pds) {
        String dataSourceName = pds.getDataSourceName();
        if (dataSourceName == null && EXCLUDE_IDENTITY_TOKEN) {
            dataSourceName = pds.getIdentityToken();
        }
        StringBuilder sb = new StringBuilder(256);
        sb.append("com.mchange.v2.c3p0:type=PooledDataSource");
        if (!EXCLUDE_IDENTITY_TOKEN) {
            sb.append(",identityToken=");
            sb.append(pds.getIdentityToken());
        }
        if (dataSourceName != null) {
            sb.append(",name=");
            sb.append(dataSourceName);
        }
        return sb.toString();
    }

    private static String getRegistryName() {
        String name = C3P0Config.getMultiPropertiesConfig().getProperty(C3P0_REGISTRY_NAME_KEY);
        name = name == null ? C3P0_REGISTRY_NAME_PFX : "com.mchange.v2.c3p0:type=C3P0Registry,name=" + name;
        return name;
    }

    static {
        String excludeStr = C3P0Config.getMultiPropertiesConfig().getProperty(EXCLUDE_IDENTITY_TOKEN_KEY);
        EXCLUDE_IDENTITY_TOKEN = excludeStr == null ? false : Boolean.parseBoolean(excludeStr.trim().toLowerCase());
        if (EXCLUDE_IDENTITY_TOKEN) {
            logger.info("com.mchange.v2.c3p0.management.ExcludeIdentityToken set to true; please ensure unique dataSourceName values are set for all PooledDataSources.");
        }
    }
}

