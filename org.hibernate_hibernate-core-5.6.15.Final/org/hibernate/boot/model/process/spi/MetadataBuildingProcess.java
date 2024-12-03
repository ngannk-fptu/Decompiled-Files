/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.jandex.IndexView
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.process.spi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.boot.AttributeConverterInfo;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.internal.InFlightMetadataCollectorImpl;
import org.hibernate.boot.internal.MetadataBuildingContextRootImpl;
import org.hibernate.boot.jaxb.internal.MappingBinder;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.model.process.internal.ManagedResourcesImpl;
import org.hibernate.boot.model.process.internal.ScanningCoordinator;
import org.hibernate.boot.model.process.spi.ManagedResources;
import org.hibernate.boot.model.process.spi.NoOpMetadataSourceProcessorImpl;
import org.hibernate.boot.model.source.internal.annotations.AnnotationMetadataSourceProcessorImpl;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchyBuilder;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.HbmMetadataSourceProcessorImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.ModelBinder;
import org.hibernate.boot.model.source.spi.MetadataSourceProcessor;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.AdditionalJaxbMappingProducer;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.MetadataContributor;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.MetadataSourceType;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.jboss.jandex.IndexView;
import org.jboss.logging.Logger;

public class MetadataBuildingProcess {
    private static final Logger log = Logger.getLogger(MetadataBuildingProcess.class);

    public static MetadataImplementor build(MetadataSources sources, BootstrapContext bootstrapContext, MetadataBuildingOptions options) {
        return MetadataBuildingProcess.complete(MetadataBuildingProcess.prepare(sources, bootstrapContext), bootstrapContext, options);
    }

    public static ManagedResources prepare(MetadataSources sources, BootstrapContext bootstrapContext) {
        ManagedResourcesImpl managedResources = ManagedResourcesImpl.baseline(sources, bootstrapContext);
        ConfigurationService configService = bootstrapContext.getServiceRegistry().getService(ConfigurationService.class);
        boolean xmlMappingEnabled = configService.getSetting("hibernate.xml_mapping_enabled", StandardConverters.BOOLEAN, Boolean.valueOf(true));
        ScanningCoordinator.INSTANCE.coordinateScan(managedResources, bootstrapContext, xmlMappingEnabled ? sources.getXmlMappingBinderAccess() : null);
        return managedResources;
    }

    public static MetadataImplementor complete(final ManagedResources managedResources, BootstrapContext bootstrapContext, final MetadataBuildingOptions options) {
        Collection<AdditionalJaxbMappingProducer> producers;
        InFlightMetadataCollectorImpl metadataCollector = new InFlightMetadataCollectorImpl(bootstrapContext, options);
        MetadataBuildingProcess.handleTypes(bootstrapContext, options);
        ClassLoaderService classLoaderService = options.getServiceRegistry().getService(ClassLoaderService.class);
        final MetadataBuildingContextRootImpl rootMetadataBuildingContext = new MetadataBuildingContextRootImpl(bootstrapContext, options, metadataCollector);
        for (AttributeConverterInfo converterInfo : managedResources.getAttributeConverterDefinitions()) {
            metadataCollector.addAttributeConverter(converterInfo.toConverterDescriptor(rootMetadataBuildingContext));
        }
        bootstrapContext.getTypeConfiguration().scope(rootMetadataBuildingContext);
        final IndexView jandexView = bootstrapContext.getJandexView();
        MetadataSourceProcessor processor = new MetadataSourceProcessor(){
            private final MetadataSourceProcessor hbmProcessor;
            private final AnnotationMetadataSourceProcessorImpl annotationProcessor;
            {
                this.hbmProcessor = options.isXmlMappingEnabled() ? new HbmMetadataSourceProcessorImpl(managedResources, (MetadataBuildingContext)rootMetadataBuildingContext) : new NoOpMetadataSourceProcessorImpl();
                this.annotationProcessor = new AnnotationMetadataSourceProcessorImpl(managedResources, rootMetadataBuildingContext, jandexView);
            }

            @Override
            public void prepare() {
                this.hbmProcessor.prepare();
                this.annotationProcessor.prepare();
            }

            @Override
            public void processTypeDefinitions() {
                this.hbmProcessor.processTypeDefinitions();
                this.annotationProcessor.processTypeDefinitions();
            }

            @Override
            public void processQueryRenames() {
                this.hbmProcessor.processQueryRenames();
                this.annotationProcessor.processQueryRenames();
            }

            @Override
            public void processNamedQueries() {
                this.hbmProcessor.processNamedQueries();
                this.annotationProcessor.processNamedQueries();
            }

            @Override
            public void processAuxiliaryDatabaseObjectDefinitions() {
                this.hbmProcessor.processAuxiliaryDatabaseObjectDefinitions();
                this.annotationProcessor.processAuxiliaryDatabaseObjectDefinitions();
            }

            @Override
            public void processIdentifierGenerators() {
                this.hbmProcessor.processIdentifierGenerators();
                this.annotationProcessor.processIdentifierGenerators();
            }

            @Override
            public void processFilterDefinitions() {
                this.hbmProcessor.processFilterDefinitions();
                this.annotationProcessor.processFilterDefinitions();
            }

            @Override
            public void processFetchProfiles() {
                this.hbmProcessor.processFetchProfiles();
                this.annotationProcessor.processFetchProfiles();
            }

            @Override
            public void prepareForEntityHierarchyProcessing() {
                for (MetadataSourceType metadataSourceType : options.getSourceProcessOrdering()) {
                    if (metadataSourceType == MetadataSourceType.HBM) {
                        this.hbmProcessor.prepareForEntityHierarchyProcessing();
                    }
                    if (metadataSourceType != MetadataSourceType.CLASS) continue;
                    this.annotationProcessor.prepareForEntityHierarchyProcessing();
                }
            }

            @Override
            public void processEntityHierarchies(Set<String> processedEntityNames) {
                for (MetadataSourceType metadataSourceType : options.getSourceProcessOrdering()) {
                    if (metadataSourceType == MetadataSourceType.HBM) {
                        this.hbmProcessor.processEntityHierarchies(processedEntityNames);
                    }
                    if (metadataSourceType != MetadataSourceType.CLASS) continue;
                    this.annotationProcessor.processEntityHierarchies(processedEntityNames);
                }
            }

            @Override
            public void postProcessEntityHierarchies() {
                for (MetadataSourceType metadataSourceType : options.getSourceProcessOrdering()) {
                    if (metadataSourceType == MetadataSourceType.HBM) {
                        this.hbmProcessor.postProcessEntityHierarchies();
                    }
                    if (metadataSourceType != MetadataSourceType.CLASS) continue;
                    this.annotationProcessor.postProcessEntityHierarchies();
                }
            }

            @Override
            public void processResultSetMappings() {
                this.hbmProcessor.processResultSetMappings();
                this.annotationProcessor.processResultSetMappings();
            }

            @Override
            public void finishUp() {
                this.hbmProcessor.finishUp();
                this.annotationProcessor.finishUp();
            }
        };
        processor.prepare();
        processor.processTypeDefinitions();
        processor.processQueryRenames();
        processor.processAuxiliaryDatabaseObjectDefinitions();
        processor.processIdentifierGenerators();
        processor.processFilterDefinitions();
        processor.processFetchProfiles();
        HashSet<String> processedEntityNames = new HashSet<String>();
        processor.prepareForEntityHierarchyProcessing();
        processor.processEntityHierarchies(processedEntityNames);
        processor.postProcessEntityHierarchies();
        processor.processResultSetMappings();
        processor.processNamedQueries();
        processor.finishUp();
        for (MetadataContributor contributor : classLoaderService.loadJavaServices(MetadataContributor.class)) {
            log.tracef("Calling MetadataContributor : %s", (Object)contributor);
            contributor.contribute(metadataCollector, jandexView);
        }
        metadataCollector.processSecondPasses(rootMetadataBuildingContext);
        if (options.isXmlMappingEnabled() && (producers = classLoaderService.loadJavaServices(AdditionalJaxbMappingProducer.class)) != null) {
            EntityHierarchyBuilder hierarchyBuilder = new EntityHierarchyBuilder();
            MappingBinder mappingBinder = new MappingBinder(classLoaderService, false);
            for (AdditionalJaxbMappingProducer producer : producers) {
                log.tracef("Calling AdditionalJaxbMappingProducer : %s", (Object)producer);
                Collection<MappingDocument> additionalMappings = producer.produceAdditionalMappings(metadataCollector, jandexView, mappingBinder, rootMetadataBuildingContext);
                for (MappingDocument mappingDocument : additionalMappings) {
                    hierarchyBuilder.indexMappingDocument(mappingDocument);
                }
            }
            ModelBinder binder = ModelBinder.prepare(rootMetadataBuildingContext);
            for (EntityHierarchySourceImpl entityHierarchySource : hierarchyBuilder.buildHierarchies()) {
                binder.bindEntityHierarchy(entityHierarchySource);
            }
        }
        return metadataCollector.buildMetadataInstance(rootMetadataBuildingContext);
    }

    private static void handleTypes(final BootstrapContext bootstrapContext, MetadataBuildingOptions options) {
        ClassLoaderService classLoaderService = options.getServiceRegistry().getService(ClassLoaderService.class);
        TypeContributions typeContributions = new TypeContributions(){

            @Override
            public void contributeType(BasicType type) {
                this.getBasicTypeRegistry().register(type);
            }

            @Override
            public void contributeType(BasicType type, String ... keys) {
                this.getBasicTypeRegistry().register(type, keys);
            }

            @Override
            public void contributeType(UserType type, String[] keys) {
                this.getBasicTypeRegistry().register(type, keys);
            }

            @Override
            public void contributeType(CompositeUserType type, String[] keys) {
                this.getBasicTypeRegistry().register(type, keys);
            }

            @Override
            public void contributeJavaTypeDescriptor(JavaTypeDescriptor descriptor) {
                bootstrapContext.getTypeConfiguration().getJavaTypeDescriptorRegistry().addDescriptor(descriptor);
            }

            @Override
            public void contributeSqlTypeDescriptor(SqlTypeDescriptor descriptor) {
                bootstrapContext.getTypeConfiguration().getSqlTypeDescriptorRegistry().addDescriptor(descriptor);
            }

            @Override
            public TypeConfiguration getTypeConfiguration() {
                return bootstrapContext.getTypeConfiguration();
            }

            final BasicTypeRegistry getBasicTypeRegistry() {
                return bootstrapContext.getTypeConfiguration().getBasicTypeRegistry();
            }
        };
        Dialect dialect = options.getServiceRegistry().getService(JdbcServices.class).getDialect();
        dialect.contributeTypes(typeContributions, options.getServiceRegistry());
        for (TypeContributor contributor : classLoaderService.loadJavaServices(TypeContributor.class)) {
            contributor.contribute(typeContributions, options.getServiceRegistry());
        }
        bootstrapContext.getTypeConfiguration().addBasicTypeRegistrationContributions(options.getBasicTypeRegistrations());
    }
}

