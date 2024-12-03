/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.process.spi;

import java.util.Set;
import org.hibernate.boot.model.source.spi.MetadataSourceProcessor;

final class NoOpMetadataSourceProcessorImpl
implements MetadataSourceProcessor {
    NoOpMetadataSourceProcessorImpl() {
    }

    @Override
    public void prepare() {
    }

    @Override
    public void processTypeDefinitions() {
    }

    @Override
    public void processQueryRenames() {
    }

    @Override
    public void processNamedQueries() {
    }

    @Override
    public void processAuxiliaryDatabaseObjectDefinitions() {
    }

    @Override
    public void processIdentifierGenerators() {
    }

    @Override
    public void processFilterDefinitions() {
    }

    @Override
    public void processFetchProfiles() {
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
    }

    @Override
    public void finishUp() {
    }
}

