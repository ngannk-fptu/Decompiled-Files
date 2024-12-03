/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.model.process.spi.ManagedResources;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchyBuilder;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.ModelBinder;
import org.hibernate.boot.model.source.spi.MetadataSourceProcessor;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.jboss.logging.Logger;

public class HbmMetadataSourceProcessorImpl
implements MetadataSourceProcessor {
    private static final Logger log = Logger.getLogger(HbmMetadataSourceProcessorImpl.class);
    private final MetadataBuildingContext rootBuildingContext;
    private Collection<MappingDocument> mappingDocuments;
    private final ModelBinder modelBinder;
    private List<EntityHierarchySourceImpl> entityHierarchies;

    public HbmMetadataSourceProcessorImpl(ManagedResources managedResources, MetadataBuildingContext rootBuildingContext) {
        this(managedResources.getXmlMappingBindings(), rootBuildingContext);
    }

    public HbmMetadataSourceProcessorImpl(Collection<Binding> xmlBindings, MetadataBuildingContext rootBuildingContext) {
        this.rootBuildingContext = rootBuildingContext;
        EntityHierarchyBuilder hierarchyBuilder = new EntityHierarchyBuilder();
        this.mappingDocuments = new ArrayList<MappingDocument>();
        for (Binding xmlBinding : xmlBindings) {
            if (!JaxbHbmHibernateMapping.class.isInstance(xmlBinding.getRoot())) continue;
            MappingDocument mappingDocument = new MappingDocument((JaxbHbmHibernateMapping)xmlBinding.getRoot(), xmlBinding.getOrigin(), rootBuildingContext);
            this.mappingDocuments.add(mappingDocument);
            hierarchyBuilder.indexMappingDocument(mappingDocument);
        }
        this.entityHierarchies = hierarchyBuilder.buildHierarchies();
        this.modelBinder = ModelBinder.prepare(rootBuildingContext);
    }

    @Override
    public void prepare() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.prepare();
        }
    }

    @Override
    public void processTypeDefinitions() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.processTypeDefinitions();
        }
    }

    @Override
    public void processQueryRenames() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.processQueryRenames();
        }
    }

    @Override
    public void processNamedQueries() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.processNamedQueries();
        }
    }

    @Override
    public void processAuxiliaryDatabaseObjectDefinitions() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.processAuxiliaryDatabaseObjectDefinitions();
        }
    }

    @Override
    public void processFilterDefinitions() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.processFilterDefinitions();
        }
    }

    @Override
    public void processFetchProfiles() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.processFetchProfiles();
        }
    }

    @Override
    public void processIdentifierGenerators() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.processIdentifierGenerators();
        }
    }

    @Override
    public void prepareForEntityHierarchyProcessing() {
    }

    @Override
    public void processEntityHierarchies(Set<String> processedEntityNames) {
        block0: for (EntityHierarchySourceImpl entityHierarchy : this.entityHierarchies) {
            for (String entityName : entityHierarchy.getContainedEntityNames()) {
                if (!processedEntityNames.contains(entityName)) continue;
                log.debugf("Skipping HBM processing of entity hierarchy [%s], as at least one entity [%s] has been processed", (Object)entityHierarchy.getRoot().getEntityNamingSource().getEntityName(), (Object)entityName);
                continue block0;
            }
            this.modelBinder.bindEntityHierarchy(entityHierarchy);
            processedEntityNames.addAll(entityHierarchy.getContainedEntityNames());
        }
    }

    @Override
    public void postProcessEntityHierarchies() {
        this.modelBinder.finishUp(this.rootBuildingContext);
    }

    @Override
    public void processResultSetMappings() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.processResultSetMappings();
        }
    }

    @Override
    public void finishUp() {
        for (MappingDocument mappingDocument : this.mappingDocuments) {
            mappingDocument.finishUp();
        }
    }
}

