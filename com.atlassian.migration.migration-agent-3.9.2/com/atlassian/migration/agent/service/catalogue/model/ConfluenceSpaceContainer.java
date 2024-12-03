/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import java.util.Collections;
import java.util.Set;
import lombok.Generated;

public class ConfluenceSpaceContainer
extends AbstractContainer {
    private final Set<String> selections;
    private final String sourceId;
    private final String key;
    private final String name;

    public ConfluenceSpaceContainer(String sourceId, String key, String name) {
        super(AbstractContainer.Type.ConfluenceSpace);
        this.sourceId = sourceId;
        this.key = key;
        this.name = name;
        this.selections = Collections.emptySet();
    }

    public ConfluenceSpaceContainer(String sourceId, String key, String name, Set<String> selections) {
        super(AbstractContainer.Type.ConfluenceSpace);
        this.sourceId = sourceId;
        this.key = key;
        this.name = name;
        this.selections = selections;
    }

    @Generated
    public Set<String> getSelections() {
        return this.selections;
    }

    @Generated
    public String getSourceId() {
        return this.sourceId;
    }

    @Generated
    public String getKey() {
        return this.key;
    }

    @Generated
    public String getName() {
        return this.name;
    }
}

