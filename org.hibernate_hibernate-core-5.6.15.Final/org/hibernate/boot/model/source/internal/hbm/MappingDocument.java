/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Set;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.hbm.spi.EntityInfo;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAuxiliaryDatabaseObjectType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmClassRenameType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchProfileType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIdentifierGeneratorDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTypeDefinitionType;
import org.hibernate.boot.jaxb.hbm.spi.ResultSetMappingBindingDefinition;
import org.hibernate.boot.model.naming.ObjectNameNormalizer;
import org.hibernate.boot.model.source.internal.OverriddenMappingDefaults;
import org.hibernate.boot.model.source.internal.hbm.AuxiliaryDatabaseObjectBinder;
import org.hibernate.boot.model.source.internal.hbm.FetchProfileBinder;
import org.hibernate.boot.model.source.internal.hbm.FilterDefinitionBinder;
import org.hibernate.boot.model.source.internal.hbm.HbmLocalMetadataBuildingContext;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.IdentifierGeneratorDefinitionBinder;
import org.hibernate.boot.model.source.internal.hbm.NamedQueryBinder;
import org.hibernate.boot.model.source.internal.hbm.ResultSetMappingBinder;
import org.hibernate.boot.model.source.internal.hbm.TypeDefinitionBinder;
import org.hibernate.boot.model.source.spi.MetadataSourceProcessor;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MappingDefaults;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.PersistentClass;
import org.jboss.logging.Logger;

public class MappingDocument
implements HbmLocalMetadataBuildingContext,
MetadataSourceProcessor {
    private static final Logger log = Logger.getLogger(MappingDocument.class);
    private final JaxbHbmHibernateMapping documentRoot;
    private final Origin origin;
    private final MetadataBuildingContext rootBuildingContext;
    private final MappingDefaults mappingDefaults;
    private final ToolingHintContext toolingHintContext;

    public MappingDocument(JaxbHbmHibernateMapping documentRoot, Origin origin, MetadataBuildingContext rootBuildingContext) {
        this.documentRoot = documentRoot;
        this.origin = origin;
        this.rootBuildingContext = rootBuildingContext;
        this.mappingDefaults = new OverriddenMappingDefaults.Builder(rootBuildingContext.getMappingDefaults()).setImplicitSchemaName(documentRoot.getSchema()).setImplicitCatalogName(documentRoot.getCatalog()).setImplicitPackageName(documentRoot.getPackage()).setImplicitPropertyAccessorName(documentRoot.getDefaultAccess()).setImplicitCascadeStyleName(documentRoot.getDefaultCascade()).setEntitiesImplicitlyLazy(documentRoot.isDefaultLazy()).setAutoImportEnabled(documentRoot.isAutoImport()).setPluralAttributesImplicitlyLazy(documentRoot.isDefaultLazy()).build();
        this.toolingHintContext = Helper.collectToolingHints(null, documentRoot);
    }

    public JaxbHbmHibernateMapping getDocumentRoot() {
        return this.documentRoot;
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }

    @Override
    public String determineEntityName(EntityInfo entityElement) {
        return this.determineEntityName(entityElement.getEntityName(), entityElement.getName());
    }

    private static String qualifyIfNeeded(String name, String implicitPackageName) {
        if (name == null) {
            return null;
        }
        if (name.indexOf(46) < 0 && implicitPackageName != null) {
            return implicitPackageName + '.' + name;
        }
        return name;
    }

    @Override
    public String determineEntityName(String entityName, String clazz) {
        return entityName != null ? entityName : MappingDocument.qualifyIfNeeded(clazz, this.mappingDefaults.getImplicitPackageName());
    }

    @Override
    public String qualifyClassName(String name) {
        return MappingDocument.qualifyIfNeeded(name, this.mappingDefaults.getImplicitPackageName());
    }

    @Override
    public PersistentClass findEntityBinding(String entityName, String clazz) {
        return this.getMetadataCollector().getEntityBinding(this.determineEntityName(entityName, clazz));
    }

    @Override
    public Origin getOrigin() {
        return this.origin;
    }

    @Override
    public BootstrapContext getBootstrapContext() {
        return this.rootBuildingContext.getBootstrapContext();
    }

    @Override
    public MetadataBuildingOptions getBuildingOptions() {
        return this.rootBuildingContext.getBuildingOptions();
    }

    @Override
    public MappingDefaults getMappingDefaults() {
        return this.mappingDefaults;
    }

    @Override
    public InFlightMetadataCollector getMetadataCollector() {
        return this.rootBuildingContext.getMetadataCollector();
    }

    @Override
    public ClassLoaderAccess getClassLoaderAccess() {
        return this.rootBuildingContext.getClassLoaderAccess();
    }

    @Override
    public ObjectNameNormalizer getObjectNameNormalizer() {
        return this.rootBuildingContext.getObjectNameNormalizer();
    }

    @Override
    public void prepare() {
    }

    @Override
    public void processTypeDefinitions() {
        for (JaxbHbmTypeDefinitionType typeDef : this.documentRoot.getTypedef()) {
            TypeDefinitionBinder.processTypeDefinition(this, typeDef);
        }
    }

    @Override
    public void processQueryRenames() {
        for (JaxbHbmClassRenameType renameBinding : this.documentRoot.getImport()) {
            String name = this.qualifyClassName(renameBinding.getClazz());
            String rename = renameBinding.getRename() == null ? StringHelper.unqualify(name) : renameBinding.getRename();
            this.getMetadataCollector().addImport(rename, name);
            log.debugf("Import (query rename): %s -> %s", (Object)rename, (Object)name);
        }
    }

    @Override
    public void processFilterDefinitions() {
        for (JaxbHbmFilterDefinitionType filterDefinitionBinding : this.documentRoot.getFilterDef()) {
            FilterDefinitionBinder.processFilterDefinition(this, filterDefinitionBinding);
        }
    }

    @Override
    public void processFetchProfiles() {
        for (JaxbHbmFetchProfileType fetchProfileBinding : this.documentRoot.getFetchProfile()) {
            FetchProfileBinder.processFetchProfile(this, fetchProfileBinding);
        }
    }

    @Override
    public void processAuxiliaryDatabaseObjectDefinitions() {
        for (JaxbHbmAuxiliaryDatabaseObjectType auxDbObjectBinding : this.documentRoot.getDatabaseObject()) {
            AuxiliaryDatabaseObjectBinder.processAuxiliaryDatabaseObject(this, auxDbObjectBinding);
        }
    }

    @Override
    public void processNamedQueries() {
        for (JaxbHbmNamedQueryType jaxbHbmNamedQueryType : this.documentRoot.getQuery()) {
            NamedQueryBinder.processNamedQuery(this, jaxbHbmNamedQueryType);
        }
        for (JaxbHbmNamedNativeQueryType jaxbHbmNamedNativeQueryType : this.documentRoot.getSqlQuery()) {
            NamedQueryBinder.processNamedNativeQuery(this, jaxbHbmNamedNativeQueryType);
        }
    }

    @Override
    public void processIdentifierGenerators() {
        for (JaxbHbmIdentifierGeneratorDefinitionType identifierGenerator : this.documentRoot.getIdentifierGenerator()) {
            IdentifierGeneratorDefinitionBinder.processIdentifierGeneratorDefinition(this, identifierGenerator);
        }
    }

    @Override
    public void prepareForEntityHierarchyProcessing() {
    }

    @Override
    public void processEntityHierarchies(Set<String> processedEntityNames) {
    }

    @Override
    public void postProcessEntityHierarchies() {
    }

    @Override
    public void processResultSetMappings() {
        for (ResultSetMappingBindingDefinition resultSetMappingBindingDefinition : this.documentRoot.getResultset()) {
            ResultSetMappingDefinition binding = ResultSetMappingBinder.bind(resultSetMappingBindingDefinition, this);
            this.getMetadataCollector().addResultSetMapping(binding);
        }
    }

    @Override
    public void finishUp() {
    }
}

