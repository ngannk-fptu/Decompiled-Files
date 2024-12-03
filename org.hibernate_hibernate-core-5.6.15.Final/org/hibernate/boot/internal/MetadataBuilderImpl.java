/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.ConstraintMode
 *  javax.persistence.SharedCacheMode
 *  org.hibernate.annotations.common.reflection.ReflectionManager
 *  org.jboss.jandex.IndexView
 */
package org.hibernate.boot.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.persistence.AttributeConverter;
import javax.persistence.ConstraintMode;
import javax.persistence.SharedCacheMode;
import org.hibernate.HibernateException;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanOptions;
import org.hibernate.boot.archive.scan.spi.Scanner;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
import org.hibernate.boot.cfgxml.spi.LoadedConfig;
import org.hibernate.boot.cfgxml.spi.MappingReference;
import org.hibernate.boot.internal.BootstrapContextImpl;
import org.hibernate.boot.internal.IdGeneratorInterpreterImpl;
import org.hibernate.boot.model.IdGeneratorStrategyInterpreter;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.model.convert.internal.ClassBasedConverterDescriptor;
import org.hibernate.boot.model.convert.internal.InstanceBasedConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.boot.model.process.spi.MetadataBuildingProcess;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.boot.spi.BasicTypeRegistration;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.JpaOrmXmlPersistenceUnitDefaultAware;
import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.boot.spi.MetadataBuilderInitializer;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.MetadataSourcesContributor;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.AttributeConverterDefinition;
import org.hibernate.cfg.MetadataSourceType;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.jboss.jandex.IndexView;

public class MetadataBuilderImpl
implements MetadataBuilderImplementor,
TypeContributions {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(MetadataBuilderImpl.class);
    private final MetadataSources sources;
    private final BootstrapContextImpl bootstrapContext;
    private final MetadataBuildingOptionsImpl options;

    public MetadataBuilderImpl(MetadataSources sources) {
        this(sources, MetadataBuilderImpl.getStandardServiceRegistry(sources.getServiceRegistry()));
    }

    private static StandardServiceRegistry getStandardServiceRegistry(ServiceRegistry serviceRegistry) {
        if (serviceRegistry == null) {
            throw new HibernateException("ServiceRegistry passed to MetadataBuilder cannot be null");
        }
        if (StandardServiceRegistry.class.isInstance(serviceRegistry)) {
            return (StandardServiceRegistry)serviceRegistry;
        }
        if (BootstrapServiceRegistry.class.isInstance(serviceRegistry)) {
            log.debugf("ServiceRegistry passed to MetadataBuilder was a BootstrapServiceRegistry; this likely wont end wellif attempt is made to build SessionFactory", new Object[0]);
            return new StandardServiceRegistryBuilder((BootstrapServiceRegistry)serviceRegistry).build();
        }
        throw new HibernateException(String.format("Unexpected type of ServiceRegistry [%s] encountered in attempt to build MetadataBuilder", serviceRegistry.getClass().getName()));
    }

    public MetadataBuilderImpl(MetadataSources sources, StandardServiceRegistry serviceRegistry) {
        this.sources = sources;
        this.options = new MetadataBuildingOptionsImpl(serviceRegistry);
        this.bootstrapContext = new BootstrapContextImpl(serviceRegistry, this.options);
        this.options.setBootstrapContext(this.bootstrapContext);
        for (MetadataSourcesContributor contributor : sources.getServiceRegistry().getService(ClassLoaderService.class).loadJavaServices(MetadataSourcesContributor.class)) {
            contributor.contribute(sources);
        }
        this.applyCfgXmlValues(serviceRegistry.getService(CfgXmlAccessService.class));
        ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
        for (MetadataBuilderInitializer contributor : classLoaderService.loadJavaServices(MetadataBuilderInitializer.class)) {
            contributor.contribute(this, serviceRegistry);
        }
    }

    private void applyCfgXmlValues(CfgXmlAccessService service) {
        LoadedConfig aggregatedConfig = service.getAggregatedConfig();
        if (aggregatedConfig == null) {
            return;
        }
        for (CacheRegionDefinition cacheRegionDefinition : aggregatedConfig.getCacheRegionDefinitions()) {
            this.applyCacheRegionDefinition(cacheRegionDefinition);
        }
    }

    @Override
    public MetadataBuilder applyImplicitSchemaName(String implicitSchemaName) {
        this.options.mappingDefaults.implicitSchemaName = implicitSchemaName;
        return this;
    }

    @Override
    public MetadataBuilder applyImplicitCatalogName(String implicitCatalogName) {
        this.options.mappingDefaults.implicitCatalogName = implicitCatalogName;
        return this;
    }

    @Override
    public MetadataBuilder applyImplicitNamingStrategy(ImplicitNamingStrategy namingStrategy) {
        this.options.implicitNamingStrategy = namingStrategy;
        return this;
    }

    @Override
    public MetadataBuilder applyPhysicalNamingStrategy(PhysicalNamingStrategy namingStrategy) {
        this.options.physicalNamingStrategy = namingStrategy;
        return this;
    }

    @Override
    public MetadataBuilder applySharedCacheMode(SharedCacheMode sharedCacheMode) {
        this.options.sharedCacheMode = sharedCacheMode;
        return this;
    }

    @Override
    public MetadataBuilder applyAccessType(AccessType implicitCacheAccessType) {
        this.options.mappingDefaults.implicitCacheAccessType = implicitCacheAccessType;
        return this;
    }

    @Override
    public MetadataBuilder applyIndexView(IndexView jandexView) {
        this.bootstrapContext.injectJandexView(jandexView);
        return this;
    }

    @Override
    public MetadataBuilder applyScanOptions(ScanOptions scanOptions) {
        this.bootstrapContext.injectScanOptions(scanOptions);
        return this;
    }

    @Override
    public MetadataBuilder applyScanEnvironment(ScanEnvironment scanEnvironment) {
        this.bootstrapContext.injectScanEnvironment(scanEnvironment);
        return this;
    }

    @Override
    public MetadataBuilder applyScanner(Scanner scanner) {
        this.bootstrapContext.injectScanner(scanner);
        return this;
    }

    @Override
    public MetadataBuilder applyArchiveDescriptorFactory(ArchiveDescriptorFactory factory) {
        this.bootstrapContext.injectArchiveDescriptorFactory(factory);
        return this;
    }

    @Override
    public MetadataBuilder enableExplicitDiscriminatorsForJoinedSubclassSupport(boolean supported) {
        this.options.explicitDiscriminatorsForJoinedInheritanceSupported = supported;
        return this;
    }

    @Override
    public MetadataBuilder enableImplicitDiscriminatorsForJoinedSubclassSupport(boolean supported) {
        this.options.implicitDiscriminatorsForJoinedInheritanceSupported = supported;
        return this;
    }

    @Override
    public MetadataBuilder enableImplicitForcingOfDiscriminatorsInSelect(boolean supported) {
        this.options.implicitlyForceDiscriminatorInSelect = supported;
        return this;
    }

    @Override
    public MetadataBuilder enableGlobalNationalizedCharacterDataSupport(boolean enabled) {
        this.options.useNationalizedCharacterData = enabled;
        return this;
    }

    @Override
    public MetadataBuilder applyBasicType(BasicType type) {
        this.options.basicTypeRegistrations.add(new BasicTypeRegistration(type));
        return this;
    }

    @Override
    public MetadataBuilder applyBasicType(BasicType type, String ... keys) {
        this.options.basicTypeRegistrations.add(new BasicTypeRegistration(type, keys));
        return this;
    }

    @Override
    public MetadataBuilder applyBasicType(UserType type, String ... keys) {
        this.options.basicTypeRegistrations.add(new BasicTypeRegistration(type, keys));
        return this;
    }

    @Override
    public MetadataBuilder applyBasicType(CompositeUserType type, String ... keys) {
        this.options.basicTypeRegistrations.add(new BasicTypeRegistration(type, keys));
        return this;
    }

    @Override
    public MetadataBuilder applyTypes(TypeContributor typeContributor) {
        typeContributor.contribute(this, this.options.serviceRegistry);
        return this;
    }

    @Override
    public void contributeType(BasicType type) {
        this.options.basicTypeRegistrations.add(new BasicTypeRegistration(type));
    }

    @Override
    public void contributeType(BasicType type, String ... keys) {
        this.options.basicTypeRegistrations.add(new BasicTypeRegistration(type, keys));
    }

    @Override
    public void contributeType(UserType type, String[] keys) {
        this.options.basicTypeRegistrations.add(new BasicTypeRegistration(type, keys));
    }

    @Override
    public void contributeType(CompositeUserType type, String[] keys) {
        this.options.basicTypeRegistrations.add(new BasicTypeRegistration(type, keys));
    }

    @Override
    public void contributeJavaTypeDescriptor(JavaTypeDescriptor descriptor) {
        this.bootstrapContext.getTypeConfiguration().getJavaTypeDescriptorRegistry().addDescriptor(descriptor);
    }

    @Override
    public void contributeSqlTypeDescriptor(SqlTypeDescriptor descriptor) {
        this.bootstrapContext.getTypeConfiguration().getSqlTypeDescriptorRegistry().addDescriptor(descriptor);
    }

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return this.bootstrapContext.getTypeConfiguration();
    }

    @Override
    public MetadataBuilder applyCacheRegionDefinition(CacheRegionDefinition cacheRegionDefinition) {
        this.bootstrapContext.addCacheRegionDefinition(cacheRegionDefinition);
        return this;
    }

    @Override
    public MetadataBuilder applyTempClassLoader(ClassLoader tempClassLoader) {
        this.bootstrapContext.injectJpaTempClassLoader(tempClassLoader);
        return this;
    }

    @Override
    public MetadataBuilder applySourceProcessOrdering(MetadataSourceType ... sourceTypes) {
        this.options.sourceProcessOrdering.addAll(Arrays.asList(sourceTypes));
        return this;
    }

    public MetadataBuilder allowSpecjSyntax() {
        this.options.specjProprietarySyntaxEnabled = true;
        return this;
    }

    public MetadataBuilder noConstraintByDefault() {
        this.options.noConstraintByDefault = true;
        return this;
    }

    @Override
    public MetadataBuilder applySqlFunction(String functionName, SQLFunction function) {
        this.bootstrapContext.addSqlFunction(functionName, function);
        return this;
    }

    @Override
    public MetadataBuilder applyAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
        this.bootstrapContext.addAuxiliaryDatabaseObject(auxiliaryDatabaseObject);
        return this;
    }

    @Override
    public MetadataBuilder applyAttributeConverter(AttributeConverterDefinition definition) {
        this.bootstrapContext.addAttributeConverterInfo(definition);
        return this;
    }

    @Override
    public MetadataBuilder applyAttributeConverter(final Class<? extends AttributeConverter> attributeConverterClass) {
        this.bootstrapContext.addAttributeConverterInfo(new AttributeConverterInfo(){

            @Override
            public Class<? extends AttributeConverter> getConverterClass() {
                return attributeConverterClass;
            }

            @Override
            public ConverterDescriptor toConverterDescriptor(MetadataBuildingContext context) {
                return new ClassBasedConverterDescriptor(attributeConverterClass, null, context.getBootstrapContext().getClassmateContext());
            }
        });
        return this;
    }

    @Override
    public MetadataBuilder applyAttributeConverter(final Class<? extends AttributeConverter> attributeConverterClass, final boolean autoApply) {
        this.bootstrapContext.addAttributeConverterInfo(new AttributeConverterInfo(){

            @Override
            public Class<? extends AttributeConverter> getConverterClass() {
                return attributeConverterClass;
            }

            @Override
            public ConverterDescriptor toConverterDescriptor(MetadataBuildingContext context) {
                return new ClassBasedConverterDescriptor(attributeConverterClass, autoApply, context.getBootstrapContext().getClassmateContext());
            }
        });
        return this;
    }

    @Override
    public MetadataBuilder applyAttributeConverter(final AttributeConverter attributeConverter) {
        this.bootstrapContext.addAttributeConverterInfo(new AttributeConverterInfo(){

            @Override
            public Class<? extends AttributeConverter> getConverterClass() {
                return attributeConverter.getClass();
            }

            @Override
            public ConverterDescriptor toConverterDescriptor(MetadataBuildingContext context) {
                return new InstanceBasedConverterDescriptor(attributeConverter, null, context.getBootstrapContext().getClassmateContext());
            }
        });
        return this;
    }

    @Override
    public MetadataBuilder applyAttributeConverter(final AttributeConverter attributeConverter, final boolean autoApply) {
        this.bootstrapContext.addAttributeConverterInfo(new AttributeConverterInfo(){

            @Override
            public Class<? extends AttributeConverter> getConverterClass() {
                return attributeConverter.getClass();
            }

            @Override
            public ConverterDescriptor toConverterDescriptor(MetadataBuildingContext context) {
                return new InstanceBasedConverterDescriptor(attributeConverter, (Boolean)autoApply, context.getBootstrapContext().getClassmateContext());
            }
        });
        return this;
    }

    @Override
    public MetadataBuilder enableNewIdentifierGeneratorSupport(boolean enabled) {
        if (enabled) {
            this.options.idGenerationTypeInterpreter.disableLegacyFallback();
        } else {
            this.options.idGenerationTypeInterpreter.enableLegacyFallback();
        }
        return this;
    }

    @Override
    public MetadataBuilder applyIdGenerationTypeInterpreter(IdGeneratorStrategyInterpreter interpreter) {
        this.options.idGenerationTypeInterpreter.addInterpreterDelegate(interpreter);
        return this;
    }

    @Override
    public <T extends MetadataBuilder> T unwrap(Class<T> type) {
        return (T)this;
    }

    @Override
    public MetadataImplementor build() {
        CfgXmlAccessService cfgXmlAccessService = this.options.serviceRegistry.getService(CfgXmlAccessService.class);
        if (cfgXmlAccessService.getAggregatedConfig() != null && cfgXmlAccessService.getAggregatedConfig().getMappingReferences() != null) {
            for (MappingReference mappingReference : cfgXmlAccessService.getAggregatedConfig().getMappingReferences()) {
                mappingReference.apply(this.sources);
            }
        }
        return MetadataBuildingProcess.build(this.sources, this.bootstrapContext, this.options);
    }

    @Override
    public BootstrapContext getBootstrapContext() {
        return this.bootstrapContext;
    }

    @Override
    public MetadataBuildingOptions getMetadataBuildingOptions() {
        return this.options;
    }

    public static class MetadataBuildingOptionsImpl
    implements MetadataBuildingOptions,
    JpaOrmXmlPersistenceUnitDefaultAware {
        private final StandardServiceRegistry serviceRegistry;
        private final MappingDefaultsImpl mappingDefaults;
        private BootstrapContext bootstrapContext;
        private ArrayList<BasicTypeRegistration> basicTypeRegistrations = new ArrayList();
        private ImplicitNamingStrategy implicitNamingStrategy;
        private PhysicalNamingStrategy physicalNamingStrategy;
        private SharedCacheMode sharedCacheMode;
        private AccessType defaultCacheAccessType;
        private MultiTenancyStrategy multiTenancyStrategy;
        private boolean explicitDiscriminatorsForJoinedInheritanceSupported;
        private boolean implicitDiscriminatorsForJoinedInheritanceSupported;
        private boolean implicitlyForceDiscriminatorInSelect;
        private boolean useNationalizedCharacterData;
        private boolean specjProprietarySyntaxEnabled;
        private boolean noConstraintByDefault;
        private ArrayList<MetadataSourceType> sourceProcessOrdering;
        private IdGeneratorInterpreterImpl idGenerationTypeInterpreter = new IdGeneratorInterpreterImpl();
        private String schemaCharset;
        private final boolean xmlMappingEnabled;

        public MetadataBuildingOptionsImpl(StandardServiceRegistry serviceRegistry) {
            this.serviceRegistry = serviceRegistry;
            final StrategySelector strategySelector = serviceRegistry.getService(StrategySelector.class);
            ConfigurationService configService = serviceRegistry.getService(ConfigurationService.class);
            this.mappingDefaults = new MappingDefaultsImpl(serviceRegistry);
            this.multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy(configService.getSettings());
            this.xmlMappingEnabled = configService.getSetting("hibernate.xml_mapping_enabled", StandardConverters.BOOLEAN, Boolean.valueOf(true));
            this.implicitDiscriminatorsForJoinedInheritanceSupported = configService.getSetting("hibernate.discriminator.implicit_for_joined", StandardConverters.BOOLEAN, Boolean.valueOf(false));
            this.explicitDiscriminatorsForJoinedInheritanceSupported = configService.getSetting("hibernate.discriminator.ignore_explicit_for_joined", StandardConverters.BOOLEAN, Boolean.valueOf(false)) == false;
            this.implicitlyForceDiscriminatorInSelect = configService.getSetting("hibernate.discriminator.force_in_select", StandardConverters.BOOLEAN, Boolean.valueOf(false));
            this.sharedCacheMode = configService.getSetting("javax.persistence.sharedCache.mode", new ConfigurationService.Converter<SharedCacheMode>(){

                @Override
                public SharedCacheMode convert(Object value) {
                    if (value == null) {
                        return null;
                    }
                    if (SharedCacheMode.class.isInstance(value)) {
                        return (SharedCacheMode)value;
                    }
                    return SharedCacheMode.valueOf((String)value.toString());
                }
            }, configService.getSetting("jakarta.persistence.sharedCache.mode", new ConfigurationService.Converter<SharedCacheMode>(){

                @Override
                public SharedCacheMode convert(Object value) {
                    if (value == null) {
                        return null;
                    }
                    if (SharedCacheMode.class.isInstance(value)) {
                        return (SharedCacheMode)value;
                    }
                    return SharedCacheMode.valueOf((String)value.toString());
                }
            }, SharedCacheMode.UNSPECIFIED));
            this.defaultCacheAccessType = configService.getSetting("hibernate.cache.default_cache_concurrency_strategy", new ConfigurationService.Converter<AccessType>(){

                @Override
                public AccessType convert(Object value) {
                    if (value == null) {
                        return null;
                    }
                    if (CacheConcurrencyStrategy.class.isInstance(value)) {
                        return ((CacheConcurrencyStrategy)((Object)value)).toAccessType();
                    }
                    if (AccessType.class.isInstance(value)) {
                        return (AccessType)((Object)value);
                    }
                    return AccessType.fromExternalName(value.toString());
                }
            }, serviceRegistry.getService(RegionFactory.class) == null ? null : serviceRegistry.getService(RegionFactory.class).getDefaultAccessType());
            this.specjProprietarySyntaxEnabled = configService.getSetting("hibernate.enable_specj_proprietary_syntax", StandardConverters.BOOLEAN, Boolean.valueOf(false));
            this.noConstraintByDefault = ConstraintMode.NO_CONSTRAINT.name().equalsIgnoreCase(configService.getSetting("hibernate.hbm2ddl.default_constraint_mode", String.class, null));
            this.implicitNamingStrategy = strategySelector.resolveDefaultableStrategy(ImplicitNamingStrategy.class, configService.getSettings().get("hibernate.implicit_naming_strategy"), new Callable<ImplicitNamingStrategy>(){

                @Override
                public ImplicitNamingStrategy call() {
                    return strategySelector.resolveDefaultableStrategy(ImplicitNamingStrategy.class, (Object)"default", ImplicitNamingStrategyJpaCompliantImpl.INSTANCE);
                }
            });
            this.physicalNamingStrategy = strategySelector.resolveDefaultableStrategy(PhysicalNamingStrategy.class, configService.getSettings().get("hibernate.physical_naming_strategy"), PhysicalNamingStrategyStandardImpl.INSTANCE);
            this.sourceProcessOrdering = this.resolveInitialSourceProcessOrdering(configService);
            boolean useNewIdentifierGenerators = configService.getSetting("hibernate.id.new_generator_mappings", StandardConverters.BOOLEAN, Boolean.valueOf(true));
            if (useNewIdentifierGenerators) {
                this.idGenerationTypeInterpreter.disableLegacyFallback();
            } else {
                this.idGenerationTypeInterpreter.enableLegacyFallback();
            }
            this.useNationalizedCharacterData = configService.getSetting("hibernate.use_nationalized_character_data", StandardConverters.BOOLEAN, Boolean.valueOf(false));
            this.schemaCharset = configService.getSetting("hibernate.hbm2ddl.charset_name", String.class, null);
        }

        private ArrayList<MetadataSourceType> resolveInitialSourceProcessOrdering(ConfigurationService configService) {
            ArrayList<MetadataSourceType> initialSelections = new ArrayList<MetadataSourceType>();
            String sourceProcessOrderingSetting = configService.getSetting("hibernate.mapping.precedence", StandardConverters.STRING);
            if (sourceProcessOrderingSetting != null) {
                String[] orderChoices = StringHelper.split(",; ", sourceProcessOrderingSetting, false);
                initialSelections.addAll(CollectionHelper.arrayList(orderChoices.length));
                for (String orderChoice : orderChoices) {
                    initialSelections.add(MetadataSourceType.parsePrecedence(orderChoice));
                }
            }
            if (initialSelections.isEmpty()) {
                initialSelections.add(MetadataSourceType.HBM);
                initialSelections.add(MetadataSourceType.CLASS);
            }
            return initialSelections;
        }

        @Override
        public StandardServiceRegistry getServiceRegistry() {
            return this.serviceRegistry;
        }

        @Override
        public MappingDefaults getMappingDefaults() {
            return this.mappingDefaults;
        }

        @Override
        public List<BasicTypeRegistration> getBasicTypeRegistrations() {
            return this.basicTypeRegistrations;
        }

        @Override
        public ReflectionManager getReflectionManager() {
            return this.bootstrapContext.getReflectionManager();
        }

        @Override
        public IndexView getJandexView() {
            return this.bootstrapContext.getJandexView();
        }

        @Override
        public ScanOptions getScanOptions() {
            return this.bootstrapContext.getScanOptions();
        }

        @Override
        public ScanEnvironment getScanEnvironment() {
            return this.bootstrapContext.getScanEnvironment();
        }

        @Override
        public Object getScanner() {
            return this.bootstrapContext.getScanner();
        }

        @Override
        public ArchiveDescriptorFactory getArchiveDescriptorFactory() {
            return this.bootstrapContext.getArchiveDescriptorFactory();
        }

        @Override
        public ClassLoader getTempClassLoader() {
            return this.bootstrapContext.getJpaTempClassLoader();
        }

        @Override
        public ImplicitNamingStrategy getImplicitNamingStrategy() {
            return this.implicitNamingStrategy;
        }

        @Override
        public PhysicalNamingStrategy getPhysicalNamingStrategy() {
            return this.physicalNamingStrategy;
        }

        @Override
        public SharedCacheMode getSharedCacheMode() {
            return this.sharedCacheMode;
        }

        @Override
        public AccessType getImplicitCacheAccessType() {
            return this.defaultCacheAccessType;
        }

        @Override
        public MultiTenancyStrategy getMultiTenancyStrategy() {
            return this.multiTenancyStrategy;
        }

        @Override
        public IdGeneratorStrategyInterpreter getIdGenerationTypeInterpreter() {
            return this.idGenerationTypeInterpreter;
        }

        @Override
        public List<CacheRegionDefinition> getCacheRegionDefinitions() {
            return new ArrayList<CacheRegionDefinition>(this.bootstrapContext.getCacheRegionDefinitions());
        }

        @Override
        public boolean ignoreExplicitDiscriminatorsForJoinedInheritance() {
            return !this.explicitDiscriminatorsForJoinedInheritanceSupported;
        }

        @Override
        public boolean createImplicitDiscriminatorsForJoinedInheritance() {
            return this.implicitDiscriminatorsForJoinedInheritanceSupported;
        }

        @Override
        public boolean shouldImplicitlyForceDiscriminatorInSelect() {
            return this.implicitlyForceDiscriminatorInSelect;
        }

        @Override
        public boolean useNationalizedCharacterData() {
            return this.useNationalizedCharacterData;
        }

        @Override
        public boolean isSpecjProprietarySyntaxEnabled() {
            return this.specjProprietarySyntaxEnabled;
        }

        @Override
        public boolean isNoConstraintByDefault() {
            return this.noConstraintByDefault;
        }

        @Override
        public List<MetadataSourceType> getSourceProcessOrdering() {
            return this.sourceProcessOrdering;
        }

        @Override
        public Map<String, SQLFunction> getSqlFunctions() {
            return this.bootstrapContext.getSqlFunctions();
        }

        @Override
        public List<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectList() {
            return new ArrayList<AuxiliaryDatabaseObject>(this.bootstrapContext.getAuxiliaryDatabaseObjectList());
        }

        @Override
        public List<AttributeConverterInfo> getAttributeConverters() {
            return new ArrayList<AttributeConverterInfo>(this.bootstrapContext.getAttributeConverters());
        }

        @Override
        public String getSchemaCharset() {
            return this.schemaCharset;
        }

        @Override
        public boolean isXmlMappingEnabled() {
            return this.xmlMappingEnabled;
        }

        @Override
        public void apply(JpaOrmXmlPersistenceUnitDefaultAware.JpaOrmXmlPersistenceUnitDefaults jpaOrmXmlPersistenceUnitDefaults) {
            if (!this.mappingDefaults.shouldImplicitlyQuoteIdentifiers()) {
                this.mappingDefaults.implicitlyQuoteIdentifiers = jpaOrmXmlPersistenceUnitDefaults.shouldImplicitlyQuoteIdentifiers();
            }
            if (this.mappingDefaults.getImplicitCatalogName() == null) {
                this.mappingDefaults.implicitCatalogName = StringHelper.nullIfEmpty(jpaOrmXmlPersistenceUnitDefaults.getDefaultCatalogName());
            }
            if (this.mappingDefaults.getImplicitSchemaName() == null) {
                this.mappingDefaults.implicitSchemaName = StringHelper.nullIfEmpty(jpaOrmXmlPersistenceUnitDefaults.getDefaultSchemaName());
            }
        }

        public void setBootstrapContext(BootstrapContextImpl bootstrapContext) {
            this.bootstrapContext = bootstrapContext;
        }
    }

    public static class MappingDefaultsImpl
    implements MappingDefaults {
        private String implicitSchemaName;
        private String implicitCatalogName;
        private boolean implicitlyQuoteIdentifiers;
        private AccessType implicitCacheAccessType;

        public MappingDefaultsImpl(StandardServiceRegistry serviceRegistry) {
            ConfigurationService configService = serviceRegistry.getService(ConfigurationService.class);
            this.implicitSchemaName = null;
            this.implicitCatalogName = null;
            this.implicitlyQuoteIdentifiers = configService.getSetting("hibernate.globally_quoted_identifiers", StandardConverters.BOOLEAN, Boolean.valueOf(false));
            this.implicitCacheAccessType = configService.getSetting("hibernate.cache.default_cache_concurrency_strategy", new ConfigurationService.Converter<AccessType>(){

                @Override
                public AccessType convert(Object value) {
                    return AccessType.fromExternalName(value.toString());
                }
            });
        }

        @Override
        public String getImplicitSchemaName() {
            return this.implicitSchemaName;
        }

        @Override
        public String getImplicitCatalogName() {
            return this.implicitCatalogName;
        }

        @Override
        public boolean shouldImplicitlyQuoteIdentifiers() {
            return this.implicitlyQuoteIdentifiers;
        }

        @Override
        public String getImplicitIdColumnName() {
            return "id";
        }

        @Override
        public String getImplicitTenantIdColumnName() {
            return "tenant_id";
        }

        @Override
        public String getImplicitDiscriminatorColumnName() {
            return "class";
        }

        @Override
        public String getImplicitPackageName() {
            return null;
        }

        @Override
        public boolean isAutoImportEnabled() {
            return true;
        }

        @Override
        public String getImplicitCascadeStyleName() {
            return "none";
        }

        @Override
        public String getImplicitPropertyAccessorName() {
            return "property";
        }

        @Override
        public boolean areEntitiesImplicitlyLazy() {
            return false;
        }

        @Override
        public boolean areCollectionsImplicitlyLazy() {
            return true;
        }

        @Override
        public AccessType getImplicitCacheAccessType() {
            return this.implicitCacheAccessType;
        }
    }
}

