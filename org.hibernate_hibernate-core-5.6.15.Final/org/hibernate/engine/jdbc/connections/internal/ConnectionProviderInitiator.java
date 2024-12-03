/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.HibernateException;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.beans.BeanInfoHelper;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public class ConnectionProviderInitiator
implements StandardServiceInitiator<ConnectionProvider> {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ConnectionProviderInitiator.class);
    public static final ConnectionProviderInitiator INSTANCE = new ConnectionProviderInitiator();
    public static final String C3P0_STRATEGY = "c3p0";
    public static final String PROXOOL_STRATEGY = "proxool";
    public static final String HIKARI_STRATEGY = "hikari";
    public static final String VIBUR_STRATEGY = "vibur";
    public static final String AGROAL_STRATEGY = "agroal";
    public static final String INJECTION_DATA = "hibernate.connection_provider.injection_data";
    private static final Map<String, String> LEGACY_CONNECTION_PROVIDER_MAPPING = new HashMap<String, String>(5);
    private static final Set<String> SPECIAL_PROPERTIES;
    private static final Map<String, Integer> ISOLATION_VALUE_MAP;
    private static final Map<Integer, String> ISOLATION_VALUE_CONSTANT_NAME_MAP;
    private static final Map<Integer, String> ISOLATION_VALUE_NICE_NAME_MAP;
    private static final Map<String, String> CONDITIONAL_PROPERTIES;

    @Override
    public Class<ConnectionProvider> getServiceInitiated() {
        return ConnectionProvider.class;
    }

    @Override
    public ConnectionProvider initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        Map injectionData;
        MultiTenancyStrategy strategy = MultiTenancyStrategy.determineMultiTenancyStrategy(configurationValues);
        if (strategy == MultiTenancyStrategy.DATABASE || strategy == MultiTenancyStrategy.SCHEMA) {
            return null;
        }
        StrategySelector strategySelector = registry.getService(StrategySelector.class);
        Object explicitSetting = configurationValues.get("hibernate.connection.provider_class");
        if (explicitSetting != null) {
            if (explicitSetting instanceof ConnectionProvider) {
                return (ConnectionProvider)explicitSetting;
            }
            if (explicitSetting instanceof Class) {
                Class providerClass = (Class)explicitSetting;
                LOG.instantiatingExplicitConnectionProvider(providerClass.getName());
                return this.instantiateExplicitConnectionProvider(providerClass);
            }
            String providerName = StringHelper.nullIfEmpty(explicitSetting.toString());
            if (providerName != null) {
                if (LEGACY_CONNECTION_PROVIDER_MAPPING.containsKey(providerName)) {
                    String actualProviderName = LEGACY_CONNECTION_PROVIDER_MAPPING.get(providerName);
                    DeprecationLogger.DEPRECATION_LOGGER.connectionProviderClassDeprecated(providerName, actualProviderName);
                    providerName = actualProviderName;
                }
                LOG.instantiatingExplicitConnectionProvider(providerName);
                Class<ConnectionProvider> providerClass = strategySelector.selectStrategyImplementor(ConnectionProvider.class, providerName);
                try {
                    return this.instantiateExplicitConnectionProvider(providerClass);
                }
                catch (Exception e) {
                    throw new HibernateException("Could not instantiate connection provider [" + providerName + "]", e);
                }
            }
        }
        if (configurationValues.get("hibernate.connection.datasource") != null) {
            return new DatasourceConnectionProviderImpl();
        }
        ConnectionProvider connectionProvider = null;
        Class<? extends ConnectionProvider> singleRegisteredProvider = this.getSingleRegisteredProvider(strategySelector);
        if (singleRegisteredProvider != null) {
            try {
                connectionProvider = singleRegisteredProvider.newInstance();
            }
            catch (IllegalAccessException | InstantiationException e) {
                throw new HibernateException("Could not instantiate singular-registered ConnectionProvider", e);
            }
        }
        if (connectionProvider == null && ConnectionProviderInitiator.c3p0ConfigDefined(configurationValues)) {
            connectionProvider = this.instantiateC3p0Provider(strategySelector);
        }
        if (connectionProvider == null && ConnectionProviderInitiator.proxoolConfigDefined(configurationValues)) {
            connectionProvider = this.instantiateProxoolProvider(strategySelector);
        }
        if (connectionProvider == null && this.hikariConfigDefined(configurationValues)) {
            connectionProvider = this.instantiateHikariProvider(strategySelector);
        }
        if (connectionProvider == null && this.viburConfigDefined(configurationValues)) {
            connectionProvider = this.instantiateViburProvider(strategySelector);
        }
        if (connectionProvider == null && this.agroalConfigDefined(configurationValues)) {
            connectionProvider = this.instantiateAgroalProvider(strategySelector);
        }
        if (connectionProvider == null && configurationValues.get("hibernate.connection.url") != null) {
            connectionProvider = new DriverManagerConnectionProviderImpl();
        }
        if (connectionProvider == null) {
            LOG.noAppropriateConnectionProvider();
            connectionProvider = new UserSuppliedConnectionProviderImpl();
        }
        if ((injectionData = (Map)configurationValues.get(INJECTION_DATA)) != null && injectionData.size() > 0) {
            final ConnectionProvider theConnectionProvider = connectionProvider;
            new BeanInfoHelper(connectionProvider.getClass()).applyToBeanInfo(connectionProvider, new BeanInfoHelper.BeanInfoDelegate(){

                @Override
                public void processBeanInfo(BeanInfo beanInfo) throws Exception {
                    PropertyDescriptor[] descriptors;
                    for (PropertyDescriptor descriptor : descriptors = beanInfo.getPropertyDescriptors()) {
                        String propertyName = descriptor.getName();
                        if (!injectionData.containsKey(propertyName)) continue;
                        Method method = descriptor.getWriteMethod();
                        method.invoke((Object)theConnectionProvider, injectionData.get(propertyName));
                    }
                }
            });
        }
        return connectionProvider;
    }

    private Class<? extends ConnectionProvider> getSingleRegisteredProvider(StrategySelector strategySelector) {
        Collection<Class<ConnectionProvider>> implementors = strategySelector.getRegisteredStrategyImplementors(ConnectionProvider.class);
        if (implementors != null && implementors.size() == 1) {
            return implementors.iterator().next();
        }
        return null;
    }

    private ConnectionProvider instantiateExplicitConnectionProvider(Class providerClass) {
        try {
            return (ConnectionProvider)providerClass.newInstance();
        }
        catch (Exception e) {
            throw new HibernateException("Could not instantiate connection provider [" + providerClass.getName() + "]", e);
        }
    }

    private static boolean c3p0ConfigDefined(Map configValues) {
        for (Object key : configValues.keySet()) {
            if (!String.class.isInstance(key) || !((String)key).startsWith("hibernate.c3p0")) continue;
            return true;
        }
        return false;
    }

    private ConnectionProvider instantiateC3p0Provider(StrategySelector strategySelector) {
        try {
            return strategySelector.selectStrategyImplementor(ConnectionProvider.class, C3P0_STRATEGY).newInstance();
        }
        catch (Exception e) {
            LOG.c3p0ProviderClassNotFound(C3P0_STRATEGY);
            return null;
        }
    }

    private static boolean proxoolConfigDefined(Map configValues) {
        for (Object key : configValues.keySet()) {
            if (!String.class.isInstance(key) || !((String)key).startsWith("hibernate.proxool")) continue;
            return true;
        }
        return false;
    }

    private ConnectionProvider instantiateProxoolProvider(StrategySelector strategySelector) {
        try {
            return strategySelector.selectStrategyImplementor(ConnectionProvider.class, PROXOOL_STRATEGY).newInstance();
        }
        catch (Exception e) {
            LOG.proxoolProviderClassNotFound(PROXOOL_STRATEGY);
            return null;
        }
    }

    private boolean hikariConfigDefined(Map configValues) {
        for (Object key : configValues.keySet()) {
            if (!String.class.isInstance(key) || !((String)key).startsWith("hibernate.hikari.")) continue;
            return true;
        }
        return false;
    }

    private ConnectionProvider instantiateHikariProvider(StrategySelector strategySelector) {
        try {
            return strategySelector.selectStrategyImplementor(ConnectionProvider.class, HIKARI_STRATEGY).newInstance();
        }
        catch (Exception e) {
            LOG.hikariProviderClassNotFound();
            return null;
        }
    }

    private boolean viburConfigDefined(Map configValues) {
        for (Object key : configValues.keySet()) {
            if (!String.class.isInstance(key) || !((String)key).startsWith("hibernate.vibur.")) continue;
            return true;
        }
        return false;
    }

    private boolean agroalConfigDefined(Map configValues) {
        for (Object key : configValues.keySet()) {
            if (!String.class.isInstance(key) || !((String)key).startsWith("hibernate.agroal.")) continue;
            return true;
        }
        return false;
    }

    private ConnectionProvider instantiateViburProvider(StrategySelector strategySelector) {
        try {
            return strategySelector.selectStrategyImplementor(ConnectionProvider.class, VIBUR_STRATEGY).newInstance();
        }
        catch (Exception e) {
            LOG.viburProviderClassNotFound();
            return null;
        }
    }

    private ConnectionProvider instantiateAgroalProvider(StrategySelector strategySelector) {
        try {
            return strategySelector.selectStrategyImplementor(ConnectionProvider.class, AGROAL_STRATEGY).newInstance();
        }
        catch (Exception e) {
            LOG.agroalProviderClassNotFound();
            return null;
        }
    }

    public static Properties getConnectionProperties(Map<?, ?> properties) {
        Properties result = new Properties();
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            if (!String.class.isInstance(entry.getKey()) || !String.class.isInstance(entry.getValue())) continue;
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (key.startsWith("hibernate.connection")) {
                if (SPECIAL_PROPERTIES.contains(key)) {
                    if (!"hibernate.connection.username".equals(key)) continue;
                    result.setProperty("user", value);
                    continue;
                }
                result.setProperty(key.substring("hibernate.connection".length() + 1), value);
                continue;
            }
            if (!CONDITIONAL_PROPERTIES.containsKey(key)) continue;
            result.setProperty(CONDITIONAL_PROPERTIES.get(key), value);
        }
        return result;
    }

    public static Integer extractIsolation(Map settings) {
        return ConnectionProviderInitiator.interpretIsolation(settings.get("hibernate.connection.isolation"));
    }

    public static Integer interpretIsolation(Object setting) {
        if (setting == null) {
            return null;
        }
        if (Number.class.isInstance(setting)) {
            return ((Number)setting).intValue();
        }
        String settingAsString = setting.toString();
        if (StringHelper.isEmpty(settingAsString)) {
            return null;
        }
        if (ISOLATION_VALUE_MAP.containsKey(settingAsString)) {
            return ISOLATION_VALUE_MAP.get(settingAsString);
        }
        try {
            return Integer.valueOf(settingAsString);
        }
        catch (NumberFormatException numberFormatException) {
            throw new HibernateException("Could not interpret transaction isolation setting [" + setting + "]");
        }
    }

    public static String toIsolationConnectionConstantName(Integer isolation) {
        String name = ISOLATION_VALUE_CONSTANT_NAME_MAP.get(isolation);
        if (name == null) {
            throw new HibernateException("Could not convert isolation value [" + isolation + "] to java.sql.Connection constant name");
        }
        return name;
    }

    public static String toIsolationNiceName(Integer isolation) {
        String name = null;
        if (isolation != null) {
            name = ISOLATION_VALUE_NICE_NAME_MAP.get(isolation);
        }
        if (name == null) {
            name = "<unknown>";
        }
        return name;
    }

    static {
        LEGACY_CONNECTION_PROVIDER_MAPPING.put("org.hibernate.connection.DatasourceConnectionProvider", DatasourceConnectionProviderImpl.class.getName());
        LEGACY_CONNECTION_PROVIDER_MAPPING.put("org.hibernate.connection.DriverManagerConnectionProvider", DriverManagerConnectionProviderImpl.class.getName());
        LEGACY_CONNECTION_PROVIDER_MAPPING.put("org.hibernate.connection.UserSuppliedConnectionProvider", UserSuppliedConnectionProviderImpl.class.getName());
        SPECIAL_PROPERTIES = new HashSet<String>();
        SPECIAL_PROPERTIES.add("hibernate.connection.datasource");
        SPECIAL_PROPERTIES.add("hibernate.connection.url");
        SPECIAL_PROPERTIES.add("hibernate.connection.provider_class");
        SPECIAL_PROPERTIES.add("hibernate.connection.pool_size");
        SPECIAL_PROPERTIES.add("hibernate.connection.isolation");
        SPECIAL_PROPERTIES.add("hibernate.connection.driver_class");
        SPECIAL_PROPERTIES.add("hibernate.connection.username");
        SPECIAL_PROPERTIES.add("hibernate.connection.provider_disables_autocommit");
        ISOLATION_VALUE_MAP = new ConcurrentHashMap<String, Integer>();
        ISOLATION_VALUE_MAP.put("TRANSACTION_NONE", 0);
        ISOLATION_VALUE_MAP.put("NONE", 0);
        ISOLATION_VALUE_MAP.put("TRANSACTION_READ_UNCOMMITTED", 1);
        ISOLATION_VALUE_MAP.put("READ_UNCOMMITTED", 1);
        ISOLATION_VALUE_MAP.put("TRANSACTION_READ_COMMITTED", 2);
        ISOLATION_VALUE_MAP.put("READ_COMMITTED", 2);
        ISOLATION_VALUE_MAP.put("TRANSACTION_REPEATABLE_READ", 4);
        ISOLATION_VALUE_MAP.put("REPEATABLE_READ", 4);
        ISOLATION_VALUE_MAP.put("TRANSACTION_SERIALIZABLE", 8);
        ISOLATION_VALUE_MAP.put("SERIALIZABLE", 8);
        ISOLATION_VALUE_CONSTANT_NAME_MAP = new ConcurrentHashMap<Integer, String>();
        ISOLATION_VALUE_CONSTANT_NAME_MAP.put(0, "TRANSACTION_NONE");
        ISOLATION_VALUE_CONSTANT_NAME_MAP.put(1, "TRANSACTION_READ_UNCOMMITTED");
        ISOLATION_VALUE_CONSTANT_NAME_MAP.put(2, "TRANSACTION_READ_COMMITTED");
        ISOLATION_VALUE_CONSTANT_NAME_MAP.put(4, "TRANSACTION_REPEATABLE_READ");
        ISOLATION_VALUE_CONSTANT_NAME_MAP.put(8, "TRANSACTION_SERIALIZABLE");
        ISOLATION_VALUE_NICE_NAME_MAP = new ConcurrentHashMap<Integer, String>();
        ISOLATION_VALUE_NICE_NAME_MAP.put(0, "NONE");
        ISOLATION_VALUE_NICE_NAME_MAP.put(1, "READ_UNCOMMITTED");
        ISOLATION_VALUE_NICE_NAME_MAP.put(2, "READ_COMMITTED");
        ISOLATION_VALUE_NICE_NAME_MAP.put(4, "REPEATABLE_READ");
        ISOLATION_VALUE_NICE_NAME_MAP.put(8, "SERIALIZABLE");
        CONDITIONAL_PROPERTIES = new HashMap<String, String>();
        CONDITIONAL_PROPERTIES.put("hibernate.synonyms", "includeSynonyms");
    }
}

