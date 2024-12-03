/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.terracotta;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.config.TerracottaClientConfiguration;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import net.sf.ehcache.util.ClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TerracottaClusteredInstanceHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerracottaClusteredInstanceHelper.class);
    private static TerracottaClusteredInstanceHelper instance = new TerracottaClusteredInstanceHelper();
    private static final String ENTERPRISE_EXPRESS_FACTORY = "net.sf.ehcache.terracotta.ExpressEnterpriseTerracottaClusteredInstanceFactory";
    private static final String EXPRESS_FACTORY = "net.sf.ehcache.terracotta.StandaloneTerracottaClusteredInstanceFactory";
    private volatile TerracottaRuntimeType terracottaRuntimeType;

    private TerracottaClusteredInstanceHelper() {
        this.lookupTerracottaRuntime();
    }

    public static TerracottaClusteredInstanceHelper getInstance() {
        return instance;
    }

    private static void setTestMode(TerracottaClusteredInstanceHelper testHelper) {
        instance = testHelper;
    }

    private TerracottaRuntimeType lookupTerracottaRuntime() {
        if (this.terracottaRuntimeType == null) {
            TerracottaRuntimeType[] lookupSequence;
            for (TerracottaRuntimeType type : lookupSequence = new TerracottaRuntimeType[]{TerracottaRuntimeType.EnterpriseExpress, TerracottaRuntimeType.Express}) {
                if (type.getFactoryClassOrNull() == null) continue;
                this.terracottaRuntimeType = type;
                break;
            }
        }
        return this.terracottaRuntimeType;
    }

    ClusteredInstanceFactory newClusteredInstanceFactory(TerracottaClientConfiguration terracottaConfig, String cacheManagerName, ClassLoader loader) {
        Class factoryClass;
        this.lookupTerracottaRuntime();
        if (this.terracottaRuntimeType == null) {
            throw new CacheException("Terracotta cache classes are not available, you are missing jar(s) most likely");
        }
        if (this.terracottaRuntimeType != TerracottaRuntimeType.EnterpriseExpress && this.terracottaRuntimeType != TerracottaRuntimeType.Express) {
            throw new CacheException("Unknown Terracotta runtime type - " + this.terracottaRuntimeType);
        }
        TerracottaClusteredInstanceHelper.assertExpress(terracottaConfig);
        if (TerracottaClusteredInstanceHelper.class.getResource("/terracotta-ehcache-version.properties") != null) {
            LOGGER.warn("ehcache-terracotta jar is detected in the current classpath. The use of ehcache-terracotta jar is no longer needed in this version of Ehcache.");
        }
        if ((factoryClass = this.terracottaRuntimeType.getFactoryClassOrNull()) == null) {
            throw new CacheException("Not able to get factory class for: " + this.terracottaRuntimeType.name());
        }
        try {
            return (ClusteredInstanceFactory)ClassLoaderUtil.createNewInstance(this.getClass().getClassLoader(), factoryClass.getName(), new Class[]{TerracottaClientConfiguration.class, String.class, ClassLoader.class}, new Object[]{terracottaConfig, cacheManagerName, loader});
        }
        catch (CacheException ce) {
            if (ce.getCause() instanceof NoClassDefFoundError) {
                throw new CacheException("Could not create ClusteredInstanceFactory due to missing class. Please verify that terracotta-toolkit is in your classpath.", ce.getCause().getCause());
            }
            throw ce;
        }
    }

    private static void assertExpress(TerracottaClientConfiguration terracottaConfig) {
        if (terracottaConfig == null) {
            throw new CacheException("Terracotta caches are defined but no <terracottaConfig> element was used to specify the Terracotta configuration.");
        }
    }

    TerracottaRuntimeType getTerracottaRuntimeTypeOrNull() {
        return this.terracottaRuntimeType;
    }

    static enum TerracottaRuntimeType {
        EnterpriseExpress("net.sf.ehcache.terracotta.ExpressEnterpriseTerracottaClusteredInstanceFactory"),
        Express("net.sf.ehcache.terracotta.StandaloneTerracottaClusteredInstanceFactory");

        private final String factoryClassName;

        private TerracottaRuntimeType(String factoryClassName) {
            this.factoryClassName = factoryClassName;
        }

        public Class getFactoryClassOrNull() {
            try {
                return Class.forName(this.factoryClassName);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
    }
}

