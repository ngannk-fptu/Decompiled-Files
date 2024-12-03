/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.EntityNotFoundException
 *  javax.persistence.PersistenceException
 *  javax.persistence.spi.PersistenceUnitTransactionType
 */
package org.hibernate.jpa.boot.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.AttributeConverter;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.archive.scan.internal.StandardScanOptions;
import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
import org.hibernate.boot.cfgxml.spi.LoadedConfig;
import org.hibernate.boot.cfgxml.spi.MappingReference;
import org.hibernate.boot.model.process.spi.ManagedResources;
import org.hibernate.boot.model.process.spi.MetadataBuildingProcess;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.internal.TcclLookupPrecedence;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.bytecode.enhance.spi.DefaultEnhancementContext;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.bytecode.enhance.spi.UnloadedClass;
import org.hibernate.bytecode.enhance.spi.UnloadedField;
import org.hibernate.cfg.AttributeConverterDefinition;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.hibernate.internal.HEMLogging;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.NullnessHelper;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jpa.boot.internal.StandardJpaScanEnvironmentImpl;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.jpa.boot.spi.StrategyRegistrationProviderList;
import org.hibernate.jpa.boot.spi.TypeContributorList;
import org.hibernate.jpa.internal.util.LogHelper;
import org.hibernate.jpa.internal.util.PersistenceUnitTransactionTypeHelper;
import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorBuilderImpl;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorBuilderImpl;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.secure.spi.GrantedPermission;
import org.hibernate.secure.spi.JaccPermissionDeclarations;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceBinding;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.Stoppable;
import org.hibernate.tool.schema.spi.DelayedDropRegistryNotAvailableImpl;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;

public class EntityManagerFactoryBuilderImpl
implements EntityManagerFactoryBuilder {
    private static final EntityManagerMessageLogger LOG = HEMLogging.messageLogger(EntityManagerFactoryBuilderImpl.class);
    public static final String INTEGRATOR_PROVIDER = "hibernate.integrator_provider";
    public static final String STRATEGY_REGISTRATION_PROVIDERS = "hibernate.strategy_registration_provider";
    public static final String TYPE_CONTRIBUTORS = "hibernate.type_contributors";
    public static final String METADATA_BUILDER_CONTRIBUTOR = "hibernate.metadata_builder_contributor";
    public static final String JANDEX_INDEX = "hibernate.jandex_index";
    private final PersistenceUnitDescriptor persistenceUnit;
    private final Map configurationValues;
    private final StandardServiceRegistry standardServiceRegistry;
    private final ManagedResources managedResources;
    private final MetadataBuilderImplementor metamodelBuilder;
    private static final String IS_JTA_TXN_COORD = "local.setting.IS_JTA_TXN_COORD";
    private Object validatorFactory;
    private Object cdiBeanManager;
    private DataSource dataSource;
    private MetadataImplementor metadata;

    public EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings) {
        this(persistenceUnit, integrationSettings, null, null);
    }

    public EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings, ClassLoader providedClassLoader) {
        this(persistenceUnit, integrationSettings, providedClassLoader, null);
    }

    public EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings, ClassLoaderService providedClassLoaderService) {
        this(persistenceUnit, integrationSettings, null, providedClassLoaderService);
    }

    private EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings, ClassLoader providedClassLoader, ClassLoaderService providedClassLoaderService) {
        LogHelper.logPersistenceUnitInformation(persistenceUnit);
        this.persistenceUnit = persistenceUnit;
        if (integrationSettings == null) {
            integrationSettings = new HashMap();
        }
        HashMap<Object, Object> mergedIntegrationSettings = null;
        Properties properties = persistenceUnit.getProperties();
        if (properties != null) {
            mergedIntegrationSettings = new HashMap<Object, Object>(properties);
            mergedIntegrationSettings.putAll(integrationSettings);
        }
        BootstrapServiceRegistry bsr = this.buildBootstrapServiceRegistry(mergedIntegrationSettings != null ? mergedIntegrationSettings : integrationSettings, providedClassLoader, providedClassLoaderService);
        try {
            StandardServiceRegistryBuilder ssrBuilder = this.getStandardServiceRegistryBuilder(bsr);
            MergedSettings mergedSettings = this.mergeSettings(persistenceUnit, integrationSettings, ssrBuilder);
            if ("true".equals(mergedSettings.configurationValues.get("hibernate.transaction.flush_before_completion"))) {
                LOG.definingFlushBeforeCompletionIgnoredInHem("hibernate.transaction.flush_before_completion");
                mergedSettings.configurationValues.put("hibernate.transaction.flush_before_completion", "false");
            }
            this.configurationValues = mergedSettings.getConfigurationValues();
            ssrBuilder.applySettings(this.configurationValues);
            this.standardServiceRegistry = ssrBuilder.build();
            this.configureIdentifierGenerators(this.standardServiceRegistry);
            MetadataSources metadataSources = new MetadataSources(bsr);
            this.metamodelBuilder = (MetadataBuilderImplementor)metadataSources.getMetadataBuilder(this.standardServiceRegistry);
            List<AttributeConverterDefinition> attributeConverterDefinitions = this.applyMappingResources(metadataSources);
            this.applyMetamodelBuilderSettings(mergedSettings, attributeConverterDefinitions);
            this.applyMetadataBuilderContributor();
            CfgXmlAccessService cfgXmlAccessService = this.standardServiceRegistry.getService(CfgXmlAccessService.class);
            if (cfgXmlAccessService.getAggregatedConfig() != null && cfgXmlAccessService.getAggregatedConfig().getMappingReferences() != null) {
                for (MappingReference mappingReference : cfgXmlAccessService.getAggregatedConfig().getMappingReferences()) {
                    mappingReference.apply(metadataSources);
                }
            }
            this.managedResources = MetadataBuildingProcess.prepare(metadataSources, this.metamodelBuilder.getBootstrapContext());
            Object validatorFactory = this.configurationValues.get("javax.persistence.validation.factory");
            if (validatorFactory == null) {
                this.withValidatorFactory(this.configurationValues.get("jakarta.persistence.validation.factory"));
            } else {
                this.withValidatorFactory(validatorFactory);
            }
            boolean dirtyTrackingEnabled = this.readBooleanConfigurationValue("hibernate.enhancer.enableDirtyTracking");
            boolean lazyInitializationEnabled = this.readBooleanConfigurationValue("hibernate.enhancer.enableLazyInitialization");
            boolean associationManagementEnabled = this.readBooleanConfigurationValue("hibernate.enhancer.enableAssociationManagement");
            if (dirtyTrackingEnabled || lazyInitializationEnabled || associationManagementEnabled) {
                EnhancementContext enhancementContext = this.getEnhancementContext(dirtyTrackingEnabled, lazyInitializationEnabled, associationManagementEnabled);
                persistenceUnit.pushClassTransformer(enhancementContext);
            }
            this.metamodelBuilder.applyTempClassLoader(null);
        }
        catch (Throwable t) {
            bsr.close();
            this.cleanup();
            throw t;
        }
    }

    protected StandardServiceRegistryBuilder getStandardServiceRegistryBuilder(BootstrapServiceRegistry bsr) {
        return StandardServiceRegistryBuilder.forJpa(bsr);
    }

    private void applyMetadataBuilderContributor() {
        Object metadataBuilderContributorSetting = this.configurationValues.get(METADATA_BUILDER_CONTRIBUTOR);
        if (metadataBuilderContributorSetting == null) {
            return;
        }
        MetadataBuilderContributor metadataBuilderContributor = this.loadSettingInstance(METADATA_BUILDER_CONTRIBUTOR, metadataBuilderContributorSetting, MetadataBuilderContributor.class);
        if (metadataBuilderContributor != null) {
            metadataBuilderContributor.contribute(this.metamodelBuilder);
        }
    }

    public Map getConfigurationValues() {
        return Collections.unmodifiableMap(this.configurationValues);
    }

    private boolean readBooleanConfigurationValue(String propertyName) {
        Object propertyValue = this.configurationValues.remove(propertyName);
        return propertyValue != null && Boolean.parseBoolean(propertyValue.toString());
    }

    protected EnhancementContext getEnhancementContext(final boolean dirtyTrackingEnabled, final boolean lazyInitializationEnabled, final boolean associationManagementEnabled) {
        return new DefaultEnhancementContext(){

            @Override
            public boolean isEntityClass(UnloadedClass classDescriptor) {
                return EntityManagerFactoryBuilderImpl.this.managedResources.getAnnotatedClassNames().contains(classDescriptor.getName()) && super.isEntityClass(classDescriptor);
            }

            @Override
            public boolean isCompositeClass(UnloadedClass classDescriptor) {
                return EntityManagerFactoryBuilderImpl.this.managedResources.getAnnotatedClassNames().contains(classDescriptor.getName()) && super.isCompositeClass(classDescriptor);
            }

            @Override
            public boolean doBiDirectionalAssociationManagement(UnloadedField field) {
                return associationManagementEnabled;
            }

            @Override
            public boolean doDirtyCheckingInline(UnloadedClass classDescriptor) {
                return dirtyTrackingEnabled;
            }

            @Override
            public boolean hasLazyLoadableAttributes(UnloadedClass classDescriptor) {
                return lazyInitializationEnabled;
            }

            @Override
            public boolean isLazyLoadable(UnloadedField field) {
                return lazyInitializationEnabled;
            }

            @Override
            public boolean doExtendedEnhancement(UnloadedClass classDescriptor) {
                return false;
            }
        };
    }

    private BootstrapServiceRegistry buildBootstrapServiceRegistry(Map integrationSettings, ClassLoader providedClassLoader, ClassLoaderService providedClassLoaderService) {
        BootstrapServiceRegistryBuilder bsrBuilder = new BootstrapServiceRegistryBuilder();
        this.applyIntegrationProvider(integrationSettings, bsrBuilder);
        StrategyRegistrationProviderList strategyRegistrationProviderList = (StrategyRegistrationProviderList)integrationSettings.get(STRATEGY_REGISTRATION_PROVIDERS);
        if (strategyRegistrationProviderList != null) {
            for (StrategyRegistrationProvider strategyRegistrationProvider : strategyRegistrationProviderList.getStrategyRegistrationProviders()) {
                bsrBuilder.applyStrategySelectors(strategyRegistrationProvider);
            }
        }
        if (providedClassLoaderService != null) {
            bsrBuilder.applyClassLoaderService(providedClassLoaderService);
        } else {
            String tcclLookupPrecedence;
            Properties puProperties;
            Object classLoadersSetting;
            ClassLoader appClassLoader;
            if (this.persistenceUnit.getClassLoader() != null) {
                bsrBuilder.applyClassLoader(this.persistenceUnit.getClassLoader());
            }
            if (providedClassLoader != null) {
                bsrBuilder.applyClassLoader(providedClassLoader);
            }
            if ((appClassLoader = (ClassLoader)integrationSettings.get("hibernate.classLoader.application")) != null) {
                LOG.debugf("Found use of deprecated `%s` setting; use `%s` instead.", "hibernate.classLoader.application", "hibernate.classLoaders");
            }
            if ((classLoadersSetting = integrationSettings.get("hibernate.classLoaders")) != null) {
                if (Collection.class.isInstance(classLoadersSetting)) {
                    for (ClassLoader classLoader : (Collection)classLoadersSetting) {
                        bsrBuilder.applyClassLoader(classLoader);
                    }
                } else if (classLoadersSetting.getClass().isArray()) {
                    for (ClassLoader classLoader : (ClassLoader[])classLoadersSetting) {
                        bsrBuilder.applyClassLoader(classLoader);
                    }
                } else if (ClassLoader.class.isInstance(classLoadersSetting)) {
                    bsrBuilder.applyClassLoader((ClassLoader)classLoadersSetting);
                }
            }
            if ((puProperties = this.persistenceUnit.getProperties()) != null && (tcclLookupPrecedence = puProperties.getProperty("hibernate.classLoader.tccl_lookup_precedence")) != null) {
                bsrBuilder.applyTcclLookupPrecedence(TcclLookupPrecedence.valueOf(tcclLookupPrecedence.toUpperCase(Locale.ROOT)));
            }
        }
        return bsrBuilder.build();
    }

    private void applyIntegrationProvider(Map integrationSettings, BootstrapServiceRegistryBuilder bsrBuilder) {
        Object integrationSetting = integrationSettings.get(INTEGRATOR_PROVIDER);
        if (integrationSetting == null) {
            return;
        }
        IntegratorProvider integratorProvider = this.loadSettingInstance(INTEGRATOR_PROVIDER, integrationSetting, IntegratorProvider.class);
        if (integratorProvider != null) {
            for (Integrator integrator : integratorProvider.getIntegrators()) {
                bsrBuilder.applyIntegrator(integrator);
            }
        }
    }

    private MergedSettings mergeSettings(PersistenceUnitDescriptor persistenceUnit, Map<?, ?> integrationSettings, StandardServiceRegistryBuilder ssrBuilder) {
        MergedSettings mergedSettings = new MergedSettings();
        mergedSettings.processPersistenceUnitDescriptorProperties(persistenceUnit);
        String cfgXmlResourceName = (String)NullnessHelper.coalesceSuppliedValues(() -> (String)mergedSettings.configurationValues.remove("hibernate.cfg_xml_file"), () -> {
            String oldSetting = (String)mergedSettings.configurationValues.remove("hibernate.ejb.cfgfile");
            if (oldSetting != null) {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.cfgfile", "hibernate.cfg_xml_file");
            }
            return oldSetting;
        }, () -> (String)integrationSettings.get("hibernate.cfg_xml_file"), () -> {
            String oldSetting = (String)integrationSettings.get("hibernate.ejb.cfgfile");
            if (oldSetting != null) {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.cfgfile", "hibernate.cfg_xml_file");
            }
            return oldSetting;
        });
        if (StringHelper.isNotEmpty(cfgXmlResourceName)) {
            this.processHibernateConfigXmlResources(ssrBuilder, mergedSettings, cfgXmlResourceName);
        }
        this.normalizeSettings(persistenceUnit, integrationSettings, mergedSettings);
        String jaccContextId = (String)mergedSettings.configurationValues.get("hibernate.jacc_context_id");
        Iterator itr = mergedSettings.configurationValues.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = itr.next();
            if (entry.getValue() == null) {
                itr.remove();
                break;
            }
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) continue;
            String keyString = (String)entry.getKey();
            String valueString = (String)entry.getValue();
            if (keyString.startsWith("hibernate.jacc")) {
                if ("hibernate.jacc_context_id".equals(keyString) || "hibernate.jacc.enabled".equals(keyString)) continue;
                if (jaccContextId == null) {
                    LOG.debugf("Found JACC permission grant [%s] in properties, but no JACC context id was specified; ignoring", keyString);
                    continue;
                }
                mergedSettings.getJaccPermissions(jaccContextId).addPermissionDeclaration(this.parseJaccConfigEntry(keyString, valueString));
                continue;
            }
            if (keyString.startsWith("hibernate.classcache")) {
                mergedSettings.addCacheRegionDefinition(this.parseCacheRegionDefinitionEntry(keyString.substring("hibernate.classcache".length() + 1), valueString, CacheRegionDefinition.CacheRegionType.ENTITY));
                continue;
            }
            if (keyString.startsWith("hibernate.ejb.classcache")) {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.classcache", "hibernate.classcache");
                mergedSettings.addCacheRegionDefinition(this.parseCacheRegionDefinitionEntry(keyString.substring("hibernate.ejb.classcache".length() + 1), valueString, CacheRegionDefinition.CacheRegionType.ENTITY));
                continue;
            }
            if (keyString.startsWith("hibernate.collectioncache")) {
                mergedSettings.addCacheRegionDefinition(this.parseCacheRegionDefinitionEntry(keyString.substring("hibernate.collectioncache".length() + 1), (String)entry.getValue(), CacheRegionDefinition.CacheRegionType.COLLECTION));
                continue;
            }
            if (!keyString.startsWith("hibernate.ejb.collectioncache")) continue;
            DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.collectioncache", "hibernate.collectioncache");
            mergedSettings.addCacheRegionDefinition(this.parseCacheRegionDefinitionEntry(keyString.substring("hibernate.ejb.collectioncache".length() + 1), (String)entry.getValue(), CacheRegionDefinition.CacheRegionType.COLLECTION));
        }
        return mergedSettings;
    }

    private void normalizeSettings(PersistenceUnitDescriptor persistenceUnit, Map<?, ?> integrationSettings, MergedSettings mergedSettings) {
        HashMap integrationSettingsCopy = new HashMap(integrationSettings);
        this.normalizeConnectionAccessUserAndPass(integrationSettingsCopy, mergedSettings);
        this.normalizeTransactionCoordinator(persistenceUnit, integrationSettingsCopy, mergedSettings);
        this.normalizeDataAccess(integrationSettingsCopy, mergedSettings, persistenceUnit);
        Object intgValidationMode = integrationSettingsCopy.remove("javax.persistence.validation.mode");
        Object jakartaIntgValidationMode = integrationSettingsCopy.remove("jakarta.persistence.validation.mode");
        if (intgValidationMode != null) {
            mergedSettings.configurationValues.put("javax.persistence.validation.mode", intgValidationMode);
        } else if (jakartaIntgValidationMode != null) {
            mergedSettings.configurationValues.put("jakarta.persistence.validation.mode", jakartaIntgValidationMode);
        } else if (persistenceUnit.getValidationMode() != null) {
            mergedSettings.configurationValues.put("javax.persistence.validation.mode", persistenceUnit.getValidationMode());
            mergedSettings.configurationValues.put("jakarta.persistence.validation.mode", persistenceUnit.getValidationMode());
        }
        Object intgCacheMode = integrationSettingsCopy.remove("javax.persistence.sharedCache.mode");
        Object jakartaIntgCacheMode = integrationSettingsCopy.remove("jakarta.persistence.sharedCache.mode");
        if (intgCacheMode != null) {
            mergedSettings.configurationValues.put("javax.persistence.sharedCache.mode", intgCacheMode);
        } else if (jakartaIntgCacheMode != null) {
            mergedSettings.configurationValues.put("jakarta.persistence.sharedCache.mode", jakartaIntgCacheMode);
        } else if (persistenceUnit.getSharedCacheMode() != null) {
            mergedSettings.configurationValues.put("javax.persistence.sharedCache.mode", persistenceUnit.getSharedCacheMode());
            mergedSettings.configurationValues.put("jakarta.persistence.sharedCache.mode", persistenceUnit.getSharedCacheMode());
        }
        for (Map.Entry<?, ?> entry : integrationSettingsCopy.entrySet()) {
            if (entry.getKey() == null) continue;
            if (entry.getValue() == null) {
                mergedSettings.configurationValues.remove(entry.getKey());
                continue;
            }
            mergedSettings.configurationValues.put(entry.getKey(), entry.getValue());
        }
    }

    private void normalizeConnectionAccessUserAndPass(HashMap<?, ?> integrationSettingsCopy, MergedSettings mergedSettings) {
        Object effectiveUser = NullnessHelper.coalesceSuppliedValues(() -> integrationSettingsCopy.remove("hibernate.connection.username"), () -> integrationSettingsCopy.remove("javax.persistence.jdbc.user"), () -> integrationSettingsCopy.remove("jakarta.persistence.jdbc.user"), () -> this.extractPuProperty(this.persistenceUnit, "hibernate.connection.username"), () -> this.extractPuProperty(this.persistenceUnit, "javax.persistence.jdbc.user"), () -> this.extractPuProperty(this.persistenceUnit, "jakarta.persistence.jdbc.user"));
        Object effectivePass = NullnessHelper.coalesceSuppliedValues(() -> integrationSettingsCopy.remove("hibernate.connection.password"), () -> integrationSettingsCopy.remove("javax.persistence.jdbc.password"), () -> integrationSettingsCopy.remove("jakarta.persistence.jdbc.password"), () -> this.extractPuProperty(this.persistenceUnit, "hibernate.connection.password"), () -> this.extractPuProperty(this.persistenceUnit, "javax.persistence.jdbc.password"), () -> this.extractPuProperty(this.persistenceUnit, "jakarta.persistence.jdbc.password"));
        if (effectiveUser != null || effectivePass != null) {
            this.applyUserAndPass(effectiveUser, effectivePass, mergedSettings);
        }
    }

    private <T> T extractPuProperty(PersistenceUnitDescriptor persistenceUnit, String propertyName) {
        return (T)(persistenceUnit.getProperties() == null ? null : persistenceUnit.getProperties().get(propertyName));
    }

    private void applyUserAndPass(Object effectiveUser, Object effectivePass, MergedSettings mergedSettings) {
        if (effectiveUser != null) {
            mergedSettings.configurationValues.put("hibernate.connection.username", effectiveUser);
            mergedSettings.configurationValues.put("javax.persistence.jdbc.user", effectiveUser);
            mergedSettings.configurationValues.put("jakarta.persistence.jdbc.user", effectiveUser);
        }
        if (effectivePass != null) {
            mergedSettings.configurationValues.put("hibernate.connection.password", effectivePass);
            mergedSettings.configurationValues.put("javax.persistence.jdbc.password", effectivePass);
            mergedSettings.configurationValues.put("jakarta.persistence.jdbc.password", effectivePass);
        }
    }

    private void normalizeTransactionCoordinator(PersistenceUnitDescriptor persistenceUnit, HashMap<?, ?> integrationSettingsCopy, MergedSettings mergedSettings) {
        Boolean definiteJtaCoordinator;
        boolean hasTxStrategy;
        PersistenceUnitTransactionType txnType = null;
        Object intgTxnType = integrationSettingsCopy.remove("javax.persistence.transactionType");
        if (intgTxnType == null) {
            intgTxnType = integrationSettingsCopy.remove("jakarta.persistence.transactionType");
        }
        if (intgTxnType != null) {
            txnType = PersistenceUnitTransactionTypeHelper.interpretTransactionType(intgTxnType);
        } else if (persistenceUnit.getTransactionType() != null) {
            txnType = persistenceUnit.getTransactionType();
        } else {
            Object puPropTxnType = mergedSettings.configurationValues.get("javax.persistence.transactionType");
            if (puPropTxnType == null) {
                puPropTxnType = mergedSettings.configurationValues.get("jakarta.persistence.transactionType");
            }
            if (puPropTxnType != null) {
                txnType = PersistenceUnitTransactionTypeHelper.interpretTransactionType(puPropTxnType);
            }
        }
        if (txnType == null) {
            LOG.debug("PersistenceUnitTransactionType not specified - falling back to RESOURCE_LOCAL");
            txnType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
        }
        if (hasTxStrategy = mergedSettings.configurationValues.containsKey("hibernate.transaction.coordinator_class")) {
            LOG.overridingTransactionStrategyDangerous("hibernate.transaction.coordinator_class");
            Object strategy = mergedSettings.configurationValues.get("hibernate.transaction.coordinator_class");
            definiteJtaCoordinator = strategy instanceof TransactionCoordinatorBuilder ? Boolean.valueOf(((TransactionCoordinatorBuilder)strategy).isJta()) : Boolean.valueOf(false);
        } else if (txnType == PersistenceUnitTransactionType.JTA) {
            mergedSettings.configurationValues.put("hibernate.transaction.coordinator_class", JtaTransactionCoordinatorBuilderImpl.class);
            definiteJtaCoordinator = true;
        } else if (txnType == PersistenceUnitTransactionType.RESOURCE_LOCAL) {
            mergedSettings.configurationValues.put("hibernate.transaction.coordinator_class", JdbcResourceLocalTransactionCoordinatorBuilderImpl.class);
            definiteJtaCoordinator = false;
        } else {
            throw new IllegalStateException("Could not determine TransactionCoordinator strategy to use");
        }
        mergedSettings.configurationValues.put(IS_JTA_TXN_COORD, definiteJtaCoordinator);
    }

    private void normalizeDataAccess(HashMap<?, ?> integrationSettingsCopy, MergedSettings mergedSettings, PersistenceUnitDescriptor persistenceUnit) {
        Object url;
        Object integrationJdbcUrl;
        Object dataSourceRef;
        if (this.dataSource != null) {
            this.applyDataSource(this.dataSource, null, integrationSettingsCopy, mergedSettings);
            return;
        }
        if (integrationSettingsCopy.containsKey("hibernate.connection.datasource") && (dataSourceRef = integrationSettingsCopy.remove("hibernate.connection.datasource")) != null) {
            this.applyDataSource(dataSourceRef, null, integrationSettingsCopy, mergedSettings);
            return;
        }
        if (integrationSettingsCopy.containsKey("javax.persistence.jtaDataSource") && (dataSourceRef = integrationSettingsCopy.remove("javax.persistence.jtaDataSource")) != null) {
            this.applyDataSource(dataSourceRef, true, integrationSettingsCopy, mergedSettings);
            return;
        }
        if (integrationSettingsCopy.containsKey("jakarta.persistence.jtaDataSource") && (dataSourceRef = integrationSettingsCopy.remove("jakarta.persistence.jtaDataSource")) != null) {
            this.applyDataSource(dataSourceRef, true, integrationSettingsCopy, mergedSettings);
            return;
        }
        if (integrationSettingsCopy.containsKey("javax.persistence.nonJtaDataSource")) {
            dataSourceRef = integrationSettingsCopy.remove("javax.persistence.nonJtaDataSource");
            this.applyDataSource(dataSourceRef, false, integrationSettingsCopy, mergedSettings);
            return;
        }
        if (integrationSettingsCopy.containsKey("jakarta.persistence.nonJtaDataSource")) {
            dataSourceRef = integrationSettingsCopy.remove("jakarta.persistence.nonJtaDataSource");
            this.applyDataSource(dataSourceRef, false, integrationSettingsCopy, mergedSettings);
            return;
        }
        if (integrationSettingsCopy.containsKey("hibernate.connection.url") && (integrationJdbcUrl = integrationSettingsCopy.get("hibernate.connection.url")) != null) {
            this.applyJdbcSettings(integrationJdbcUrl, (String)NullnessHelper.coalesceSuppliedValues(() -> ConfigurationHelper.getString("hibernate.connection.driver_class", integrationSettingsCopy), () -> ConfigurationHelper.getString("javax.persistence.jdbc.driver", integrationSettingsCopy), () -> ConfigurationHelper.getString("jakarta.persistence.jdbc.driver", integrationSettingsCopy), () -> ConfigurationHelper.getString("hibernate.connection.driver_class", mergedSettings.configurationValues), () -> ConfigurationHelper.getString("javax.persistence.jdbc.driver", mergedSettings.configurationValues), () -> ConfigurationHelper.getString("jakarta.persistence.jdbc.driver", mergedSettings.configurationValues)), integrationSettingsCopy, mergedSettings);
            return;
        }
        if (integrationSettingsCopy.containsKey("javax.persistence.jdbc.url") && (integrationJdbcUrl = integrationSettingsCopy.get("javax.persistence.jdbc.url")) != null) {
            this.applyJdbcSettings(integrationJdbcUrl, (String)NullnessHelper.coalesceSuppliedValues(() -> ConfigurationHelper.getString("javax.persistence.jdbc.driver", integrationSettingsCopy), () -> ConfigurationHelper.getString("javax.persistence.jdbc.driver", mergedSettings.configurationValues)), integrationSettingsCopy, mergedSettings);
            return;
        }
        if (integrationSettingsCopy.containsKey("jakarta.persistence.jdbc.url") && (integrationJdbcUrl = integrationSettingsCopy.get("jakarta.persistence.jdbc.url")) != null) {
            this.applyJdbcSettings(integrationJdbcUrl, (String)NullnessHelper.coalesceSuppliedValues(() -> ConfigurationHelper.getString("jakarta.persistence.jdbc.driver", integrationSettingsCopy), () -> ConfigurationHelper.getString("jakarta.persistence.jdbc.driver", mergedSettings.configurationValues)), integrationSettingsCopy, mergedSettings);
            return;
        }
        if (persistenceUnit.getJtaDataSource() != null) {
            this.applyDataSource(persistenceUnit.getJtaDataSource(), true, integrationSettingsCopy, mergedSettings);
            return;
        }
        if (persistenceUnit.getNonJtaDataSource() != null) {
            this.applyDataSource(persistenceUnit.getNonJtaDataSource(), false, integrationSettingsCopy, mergedSettings);
            return;
        }
        if (mergedSettings.configurationValues.containsKey("hibernate.connection.url") && (url = mergedSettings.configurationValues.get("hibernate.connection.url")) != null && (!(url instanceof String) || StringHelper.isNotEmpty((String)url))) {
            this.applyJdbcSettings(url, ConfigurationHelper.getString("hibernate.connection.driver_class", mergedSettings.configurationValues), integrationSettingsCopy, mergedSettings);
            return;
        }
        if (mergedSettings.configurationValues.containsKey("javax.persistence.jdbc.url") && (url = mergedSettings.configurationValues.get("javax.persistence.jdbc.url")) != null && (!(url instanceof String) || StringHelper.isNotEmpty((String)url))) {
            this.applyJdbcSettings(url, ConfigurationHelper.getString("javax.persistence.jdbc.driver", mergedSettings.configurationValues), integrationSettingsCopy, mergedSettings);
            return;
        }
        if (mergedSettings.configurationValues.containsKey("jakarta.persistence.jdbc.url") && (url = mergedSettings.configurationValues.get("jakarta.persistence.jdbc.url")) != null && (!(url instanceof String) || StringHelper.isNotEmpty((String)url))) {
            this.applyJdbcSettings(url, ConfigurationHelper.getString("jakarta.persistence.jdbc.driver", mergedSettings.configurationValues), integrationSettingsCopy, mergedSettings);
            return;
        }
    }

    private void applyDataSource(Object dataSourceRef, Boolean useJtaDataSource, HashMap<?, ?> integrationSettingsCopy, MergedSettings mergedSettings) {
        String jakartaInverseEmfKey;
        String inverseEmfKey;
        String jakartaEmfKey;
        String emfKey;
        boolean isJta;
        boolean isJtaTransactionCoordinator = (Boolean)mergedSettings.configurationValues.remove(IS_JTA_TXN_COORD);
        boolean bl = isJta = useJtaDataSource == null ? isJtaTransactionCoordinator : useJtaDataSource;
        if (isJta) {
            emfKey = "javax.persistence.jtaDataSource";
            jakartaEmfKey = "jakarta.persistence.jtaDataSource";
            inverseEmfKey = "javax.persistence.nonJtaDataSource";
            jakartaInverseEmfKey = "jakarta.persistence.nonJtaDataSource";
        } else {
            emfKey = "javax.persistence.nonJtaDataSource";
            jakartaEmfKey = "jakarta.persistence.nonJtaDataSource";
            inverseEmfKey = "javax.persistence.jtaDataSource";
            jakartaInverseEmfKey = "jakarta.persistence.jtaDataSource";
        }
        mergedSettings.configurationValues.put(emfKey, dataSourceRef);
        mergedSettings.configurationValues.put(jakartaEmfKey, dataSourceRef);
        this.cleanUpConfigKeys(integrationSettingsCopy, mergedSettings, inverseEmfKey, jakartaInverseEmfKey, "javax.persistence.jdbc.driver", "jakarta.persistence.jdbc.driver", "hibernate.connection.driver_class", "javax.persistence.jdbc.url", "jakarta.persistence.jdbc.url", "hibernate.connection.url");
        this.cleanUpConfigKeys(integrationSettingsCopy, "hibernate.connection.datasource", "javax.persistence.jtaDataSource", "jakarta.persistence.jtaDataSource", "javax.persistence.nonJtaDataSource", "jakarta.persistence.nonJtaDataSource");
        mergedSettings.configurationValues.put("hibernate.connection.datasource", dataSourceRef);
    }

    private void cleanUpConfigKeys(HashMap<?, ?> integrationSettingsCopy, MergedSettings mergedSettings, String ... keys) {
        for (String key : keys) {
            Object removedMergedSetting;
            Object removedIntgSetting = integrationSettingsCopy.remove(key);
            if (removedIntgSetting != null) {
                LOG.debugf("Removed integration override setting [%s] due to normalization", key);
            }
            if ((removedMergedSetting = mergedSettings.configurationValues.remove(key)) == null) continue;
            LOG.debugf("Removed merged setting [%s] due to normalization", key);
        }
    }

    private void cleanUpConfigKeys(Map<?, ?> settings, String ... keys) {
        for (String key : keys) {
            settings.remove(key);
        }
    }

    private void applyJdbcSettings(Object url, String driver, HashMap<?, ?> integrationSettingsCopy, MergedSettings mergedSettings) {
        mergedSettings.configurationValues.put("hibernate.connection.url", url);
        mergedSettings.configurationValues.put("javax.persistence.jdbc.url", url);
        mergedSettings.configurationValues.put("jakarta.persistence.jdbc.url", url);
        if (driver != null) {
            mergedSettings.configurationValues.put("hibernate.connection.driver_class", driver);
            mergedSettings.configurationValues.put("javax.persistence.jdbc.driver", driver);
            mergedSettings.configurationValues.put("jakarta.persistence.jdbc.driver", driver);
        } else {
            mergedSettings.configurationValues.remove("hibernate.connection.driver_class");
            mergedSettings.configurationValues.remove("javax.persistence.jdbc.driver");
            mergedSettings.configurationValues.remove("jakarta.persistence.jdbc.driver");
        }
        this.cleanUpConfigKeys(integrationSettingsCopy, "hibernate.connection.driver_class", "javax.persistence.jdbc.driver", "jakarta.persistence.jdbc.driver", "hibernate.connection.url", "javax.persistence.jdbc.url", "jakarta.persistence.jdbc.url", "hibernate.connection.username", "javax.persistence.jdbc.user", "jakarta.persistence.jdbc.user", "hibernate.connection.password", "javax.persistence.jdbc.password", "jakarta.persistence.jdbc.password");
        this.cleanUpConfigKeys(integrationSettingsCopy, mergedSettings, "hibernate.connection.datasource", "javax.persistence.jtaDataSource", "jakarta.persistence.jtaDataSource", "javax.persistence.nonJtaDataSource", "jakarta.persistence.nonJtaDataSource");
    }

    private void processHibernateConfigXmlResources(StandardServiceRegistryBuilder ssrBuilder, MergedSettings mergedSettings, String cfgXmlResourceName) {
        LoadedConfig loadedConfig = ssrBuilder.getConfigLoader().loadConfigXmlResource(cfgXmlResourceName);
        mergedSettings.processHibernateConfigXmlResources(loadedConfig);
        ssrBuilder.getAggregatedCfgXml().merge(loadedConfig);
    }

    private GrantedPermission parseJaccConfigEntry(String keyString, String valueString) {
        try {
            int roleStart = "hibernate.jacc".length() + 1;
            String role = keyString.substring(roleStart, keyString.indexOf(46, roleStart));
            int classStart = roleStart + role.length() + 1;
            String clazz = keyString.substring(classStart, keyString.length());
            return new GrantedPermission(role, clazz, valueString);
        }
        catch (IndexOutOfBoundsException e) {
            throw this.persistenceException("Illegal usage of hibernate.jacc: " + keyString);
        }
    }

    private CacheRegionDefinition parseCacheRegionDefinitionEntry(String role, String value, CacheRegionDefinition.CacheRegionType cacheType) {
        StringTokenizer params = new StringTokenizer(value, ";, ");
        if (!params.hasMoreTokens()) {
            StringBuilder error = new StringBuilder("Illegal usage of ");
            if (cacheType == CacheRegionDefinition.CacheRegionType.ENTITY) {
                error.append("hibernate.classcache").append(": ").append("hibernate.classcache");
            } else {
                error.append("hibernate.collectioncache").append(": ").append("hibernate.collectioncache");
            }
            error.append('.').append(role).append(' ').append(value).append(".  Was expecting configuration (usage[,region[,lazy]]), but found none");
            throw this.persistenceException(error.toString());
        }
        String usage = params.nextToken();
        String region = null;
        if (params.hasMoreTokens()) {
            region = params.nextToken();
        }
        boolean lazyProperty = true;
        if (cacheType == CacheRegionDefinition.CacheRegionType.ENTITY) {
            if (params.hasMoreTokens()) {
                lazyProperty = "all".equalsIgnoreCase(params.nextToken());
            }
        } else {
            lazyProperty = false;
        }
        return new CacheRegionDefinition(cacheType, role, usage, region, lazyProperty);
    }

    private void configureIdentifierGenerators(StandardServiceRegistry ssr) {
        StrategySelector strategySelector = ssr.getService(StrategySelector.class);
        Object idGeneratorStrategyProviderSetting = NullnessHelper.coalesceSuppliedValues(() -> this.configurationValues.remove("hibernate.identifier_generator_strategy_provider"), () -> {
            Object oldSetting = this.configurationValues.remove("hibernate.ejb.identifier_generator_strategy_provider");
            if (oldSetting != null) {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.identifier_generator_strategy_provider", "hibernate.identifier_generator_strategy_provider");
            }
            return oldSetting;
        });
        if (idGeneratorStrategyProviderSetting != null) {
            IdentifierGeneratorStrategyProvider idGeneratorStrategyProvider = strategySelector.resolveStrategy(IdentifierGeneratorStrategyProvider.class, idGeneratorStrategyProviderSetting);
            MutableIdentifierGeneratorFactory identifierGeneratorFactory = ssr.getService(MutableIdentifierGeneratorFactory.class);
            if (identifierGeneratorFactory == null) {
                throw this.persistenceException("Application requested custom identifier generator strategies, but the MutableIdentifierGeneratorFactory could not be found");
            }
            for (Map.Entry<String, Class<?>> entry : idGeneratorStrategyProvider.getStrategies().entrySet()) {
                identifierGeneratorFactory.register(entry.getKey(), entry.getValue());
            }
        }
    }

    private List<AttributeConverterDefinition> applyMappingResources(MetadataSources metadataSources) {
        List explicitOrmXmlList;
        String explicitHbmXmlFiles;
        List explicitAnnotatedClasses;
        ArrayList<AttributeConverterDefinition> attributeConverterDefinitions = null;
        List annotatedClasses = (List)this.configurationValues.remove("hibernate.loaded_classes");
        List oldAnnotatedClasses = (List)this.configurationValues.remove("hibernate.ejb.loaded.classes");
        if (oldAnnotatedClasses != null && !oldAnnotatedClasses.isEmpty()) {
            DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.loaded.classes", "hibernate.loaded_classes");
        }
        if ((explicitAnnotatedClasses = NullnessHelper.coalesce(annotatedClasses, oldAnnotatedClasses)) != null) {
            for (Class cls : explicitAnnotatedClasses) {
                if (AttributeConverter.class.isAssignableFrom(cls)) {
                    if (attributeConverterDefinitions == null) {
                        attributeConverterDefinitions = new ArrayList<AttributeConverterDefinition>();
                    }
                    attributeConverterDefinitions.add(AttributeConverterDefinition.from(cls));
                    continue;
                }
                metadataSources.addAnnotatedClass(cls);
            }
        }
        String hbmXmlFiles = (String)this.configurationValues.remove("hibernate.hbm_xml_files");
        String oldHbmXmlFiles = (String)this.configurationValues.remove("hibernate.hbmxml.files");
        if (oldHbmXmlFiles != null) {
            DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.hbmxml.files", "hibernate.hbm_xml_files");
        }
        if ((explicitHbmXmlFiles = NullnessHelper.coalesce(hbmXmlFiles, oldHbmXmlFiles)) != null) {
            for (String hbmXml : StringHelper.split(", ", explicitHbmXmlFiles)) {
                metadataSources.addResource(hbmXml);
            }
        }
        List ormXmlFiles = (List)this.configurationValues.remove("hibernate.orm_xml_files");
        List oldOrmXmlFiles = (List)this.configurationValues.remove("hibernate.ejb.xml_files");
        if (oldOrmXmlFiles != null) {
            DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.xml_files", "hibernate.orm_xml_files");
        }
        if ((explicitOrmXmlList = NullnessHelper.coalesce(ormXmlFiles, oldOrmXmlFiles)) != null) {
            explicitOrmXmlList.forEach(metadataSources::addResource);
        }
        return attributeConverterDefinitions;
    }

    private void applyMetamodelBuilderSettings(MergedSettings mergedSettings, List<AttributeConverterDefinition> attributeConverterDefinitions) {
        TypeContributorList typeContributorList;
        this.metamodelBuilder.getBootstrapContext().markAsJpaBootstrap();
        if (this.persistenceUnit.getTempClassLoader() != null) {
            this.metamodelBuilder.applyTempClassLoader(this.persistenceUnit.getTempClassLoader());
        }
        this.metamodelBuilder.applyScanEnvironment(new StandardJpaScanEnvironmentImpl(this.persistenceUnit));
        this.metamodelBuilder.applyScanOptions(new StandardScanOptions((String)this.configurationValues.get("hibernate.archive.autodetection"), this.persistenceUnit.isExcludeUnlistedClasses()));
        if (mergedSettings.cacheRegionDefinitions != null) {
            mergedSettings.cacheRegionDefinitions.forEach(this.metamodelBuilder::applyCacheRegionDefinition);
        }
        if ((typeContributorList = (TypeContributorList)this.configurationValues.remove(TYPE_CONTRIBUTORS)) != null) {
            typeContributorList.getTypeContributors().forEach(this.metamodelBuilder::applyTypes);
        }
        if (attributeConverterDefinitions != null) {
            attributeConverterDefinitions.forEach(this.metamodelBuilder::applyAttributeConverter);
        }
    }

    public MetadataImplementor getMetadata() {
        return this.metadata;
    }

    @Override
    public EntityManagerFactoryBuilder withValidatorFactory(Object validatorFactory) {
        this.validatorFactory = validatorFactory;
        if (validatorFactory != null) {
            BeanValidationIntegrator.validateFactory(validatorFactory);
        }
        return this;
    }

    @Override
    public EntityManagerFactoryBuilder withDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public void cancel() {
        this.cleanup();
    }

    private void cleanup() {
        if (this.standardServiceRegistry instanceof ServiceRegistryImplementor && this.standardServiceRegistry instanceof ServiceBinding.ServiceLifecycleOwner) {
            ServiceRegistryImplementor serviceRegistry = (ServiceRegistryImplementor)((Object)this.standardServiceRegistry);
            ServiceBinding.ServiceLifecycleOwner lifecycleOwner = (ServiceBinding.ServiceLifecycleOwner)((Object)serviceRegistry);
            ServiceBinding<ConnectionProvider> binding = serviceRegistry.locateServiceBinding(ConnectionProvider.class);
            if (binding != null && binding.getService() instanceof Stoppable) {
                lifecycleOwner.stopService(binding);
                binding.setService(null);
            }
        }
    }

    protected MetadataImplementor metadata() {
        if (this.metadata == null) {
            this.metadata = MetadataBuildingProcess.complete(this.managedResources, this.metamodelBuilder.getBootstrapContext(), this.metamodelBuilder.getMetadataBuildingOptions());
        }
        return this.metadata;
    }

    @Override
    public void generateSchema() {
        try {
            SessionFactoryBuilder sfBuilder = this.metadata().getSessionFactoryBuilder();
            this.populateSfBuilder(sfBuilder, this.standardServiceRegistry);
            SchemaManagementToolCoordinator.process(this.metadata, this.standardServiceRegistry, this.configurationValues, DelayedDropRegistryNotAvailableImpl.INSTANCE);
        }
        catch (Exception e) {
            throw this.persistenceException("Error performing schema management", e);
        }
        finally {
            this.cancel();
        }
    }

    @Override
    public EntityManagerFactory build() {
        boolean success = false;
        try {
            SessionFactoryBuilder sfBuilder = this.metadata().getSessionFactoryBuilder();
            this.populateSfBuilder(sfBuilder, this.standardServiceRegistry);
            try {
                SessionFactory emf = sfBuilder.build();
                success = true;
                SessionFactory sessionFactory = emf;
                return sessionFactory;
            }
            catch (Exception e) {
                throw this.persistenceException("Unable to build Hibernate SessionFactory", e);
            }
        }
        finally {
            if (!success) {
                this.cleanup();
            }
        }
    }

    protected void populateSfBuilder(SessionFactoryBuilder sfBuilder, StandardServiceRegistry ssr) {
        Object sessionFactoryObserverSetting;
        boolean allowRefreshDetachedEntity;
        StrategySelector strategySelector = ssr.getService(StrategySelector.class);
        boolean jtaTransactionAccessEnabled = this.readBooleanConfigurationValue("hibernate.jta.allowTransactionAccess");
        if (!jtaTransactionAccessEnabled) {
            ((SessionFactoryBuilderImplementor)sfBuilder).disableJtaTransactionAccess();
        }
        if (!(allowRefreshDetachedEntity = this.readBooleanConfigurationValue("hibernate.allow_refresh_detached_entity"))) {
            ((SessionFactoryBuilderImplementor)sfBuilder).disableRefreshDetachedEntity();
        }
        if ((sessionFactoryObserverSetting = NullnessHelper.coalesceSuppliedValues(() -> this.configurationValues.remove("hibernate.session_factory_observer"), () -> {
            Object oldSetting = this.configurationValues.remove("hibernate.ejb.session_factory_observer");
            if (oldSetting != null) {
                DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.session_factory_observer", "hibernate.session_factory_observer");
            }
            return oldSetting;
        })) != null) {
            SessionFactoryObserver suppliedSessionFactoryObserver = strategySelector.resolveStrategy(SessionFactoryObserver.class, sessionFactoryObserverSetting);
            sfBuilder.addSessionFactoryObservers(suppliedSessionFactoryObserver);
        }
        sfBuilder.addSessionFactoryObservers(ServiceRegistryCloser.INSTANCE);
        sfBuilder.applyEntityNotFoundDelegate(JpaEntityNotFoundDelegate.INSTANCE);
        if (this.validatorFactory != null) {
            sfBuilder.applyValidatorFactory(this.validatorFactory);
        }
        if (this.cdiBeanManager != null) {
            sfBuilder.applyBeanManager(this.cdiBeanManager);
        }
    }

    private PersistenceException persistenceException(String message) {
        return this.persistenceException(message, null);
    }

    protected PersistenceException persistenceException(String message, Exception cause) {
        return new PersistenceException(this.getExceptionHeader() + message, (Throwable)cause);
    }

    private String getExceptionHeader() {
        return "[PersistenceUnit: " + this.persistenceUnit.getName() + "] ";
    }

    private <T> T loadSettingInstance(String settingName, Object settingValue, Class<T> clazz) {
        Object instance = null;
        Class<Object> instanceClass = null;
        if (clazz.isAssignableFrom(settingValue.getClass())) {
            instance = settingValue;
        } else if (settingValue instanceof Class) {
            instanceClass = (Class<?>)settingValue;
        } else if (settingValue instanceof String) {
            String settingStringValue = (String)settingValue;
            if (this.standardServiceRegistry != null) {
                ClassLoaderService classLoaderService = this.standardServiceRegistry.getService(ClassLoaderService.class);
                instanceClass = classLoaderService.classForName(settingStringValue);
            } else {
                try {
                    instanceClass = Class.forName(settingStringValue);
                }
                catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Can't load class: " + settingStringValue, e);
                }
            }
        } else {
            throw new IllegalArgumentException("The provided " + settingName + " setting value [" + settingValue + "] is not supported!");
        }
        if (instanceClass != null) {
            try {
                instance = instanceClass.newInstance();
            }
            catch (IllegalAccessException | InstantiationException e) {
                throw new IllegalArgumentException("The " + clazz.getSimpleName() + " class [" + instanceClass + "] could not be instantiated!", e);
            }
        }
        return (T)instance;
    }

    protected StandardServiceRegistry getStandardServiceRegistry() {
        return this.standardServiceRegistry;
    }

    private static class MergedSettings {
        private final Map configurationValues = new ConcurrentHashMap(16, 0.75f, 1);
        private Map<String, JaccPermissionDeclarations> jaccPermissionsByContextId;
        private List<CacheRegionDefinition> cacheRegionDefinitions;

        private MergedSettings() {
            this.configurationValues.putAll(Environment.getProperties());
        }

        public void processPersistenceUnitDescriptorProperties(PersistenceUnitDescriptor persistenceUnit) {
            if (persistenceUnit.getProperties() != null) {
                this.configurationValues.putAll(persistenceUnit.getProperties());
            }
            this.configurationValues.put("hibernate.persistenceUnitName", persistenceUnit.getName());
            this.configurationValues.put("hibernate.ejb.persistenceUnitName", persistenceUnit.getName());
        }

        public void processHibernateConfigXmlResources(LoadedConfig loadedConfig) {
            String sfName;
            if (!this.configurationValues.containsKey("hibernate.session_factory_name") && (sfName = loadedConfig.getSessionFactoryName()) != null) {
                this.configurationValues.put("hibernate.session_factory_name", sfName);
            }
            this.configurationValues.putAll(loadedConfig.getConfigurationValues());
        }

        public Map getConfigurationValues() {
            return this.configurationValues;
        }

        private JaccPermissionDeclarations getJaccPermissions(String jaccContextId) {
            JaccPermissionDeclarations jaccPermissions;
            if (this.jaccPermissionsByContextId == null) {
                this.jaccPermissionsByContextId = new HashMap<String, JaccPermissionDeclarations>();
            }
            if ((jaccPermissions = this.jaccPermissionsByContextId.get(jaccContextId)) == null) {
                jaccPermissions = new JaccPermissionDeclarations(jaccContextId);
                this.jaccPermissionsByContextId.put(jaccContextId, jaccPermissions);
            }
            return jaccPermissions;
        }

        private void addCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition) {
            if (this.cacheRegionDefinitions == null) {
                this.cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
            }
            this.cacheRegionDefinitions.add(cacheRegionDefinition);
        }
    }

    private static class ServiceRegistryCloser
    implements SessionFactoryObserver {
        public static final ServiceRegistryCloser INSTANCE = new ServiceRegistryCloser();

        private ServiceRegistryCloser() {
        }

        @Override
        public void sessionFactoryCreated(SessionFactory sessionFactory) {
        }

        @Override
        public void sessionFactoryClosed(SessionFactory sessionFactory) {
            SessionFactoryImplementor sfi = (SessionFactoryImplementor)sessionFactory;
            sfi.getServiceRegistry().destroy();
            ServiceRegistry basicRegistry = sfi.getServiceRegistry().getParentServiceRegistry();
            ((ServiceRegistryImplementor)basicRegistry).destroy();
        }
    }

    private static class JpaEntityNotFoundDelegate
    implements EntityNotFoundDelegate,
    Serializable {
        public static final JpaEntityNotFoundDelegate INSTANCE = new JpaEntityNotFoundDelegate();

        private JpaEntityNotFoundDelegate() {
        }

        @Override
        public void handleEntityNotFound(String entityName, Serializable id) {
            throw new EntityNotFoundException("Unable to find " + entityName + " with id " + id);
        }
    }
}

