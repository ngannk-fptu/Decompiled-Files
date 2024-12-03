/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Set;

public interface MetadataSourceProcessor {
    public void prepare();

    public void processTypeDefinitions();

    public void processQueryRenames();

    public void processNamedQueries();

    public void processAuxiliaryDatabaseObjectDefinitions();

    public void processIdentifierGenerators();

    public void processFilterDefinitions();

    public void processFetchProfiles();

    public void prepareForEntityHierarchyProcessing();

    public void processEntityHierarchies(Set<String> var1);

    public void postProcessEntityHierarchies();

    public void processResultSetMappings();

    public void finishUp();
}

