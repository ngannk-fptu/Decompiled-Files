/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintCleaner;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentBlueprintAo;
import com.atlassian.confluence.plugins.createcontent.api.services.ContentBlueprintService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultContentBlueprintCleaner
implements ContentBlueprintCleaner {
    private static final Logger log = LoggerFactory.getLogger(DefaultContentBlueprintCleaner.class);
    private final TransactionTemplate transactionTemplate;
    private final ContentBlueprintService contentBlueprintService;
    private final ActiveObjects activeObjects;
    private final SpaceManager spaceManager;

    @Autowired
    public DefaultContentBlueprintCleaner(@ComponentImport TransactionTemplate transactionTemplate, ContentBlueprintService contentBlueprintService, @ComponentImport ActiveObjects activeObjects, @ComponentImport SpaceManager spaceManager) {
        this.transactionTemplate = transactionTemplate;
        this.contentBlueprintService = contentBlueprintService;
        this.activeObjects = activeObjects;
        this.spaceManager = spaceManager;
    }

    @Override
    public int cleanUp() {
        try {
            Set<String> spaceKeys = this.getAllRemovedSpaceKeys();
            return (Integer)this.transactionTemplate.execute(() -> {
                spaceKeys.forEach(this.contentBlueprintService::deleteContentBlueprintsForSpace);
                return spaceKeys.size();
            });
        }
        catch (Exception e) {
            log.error("Could not clean up content blueprints for removed space: {}", (Throwable)e);
            return -1;
        }
    }

    @VisibleForTesting
    Set<String> getAllRemovedSpaceKeys() {
        Query query = Query.select();
        query.setWhereClause("SPACE_KEY IS NOT NULL");
        ContentBlueprintAo[] aos = (ContentBlueprintAo[])this.activeObjects.find(ContentBlueprintAo.class, query);
        Set<String> spaceKeys = Arrays.stream(aos).map(ContentBlueprintAo::getSpaceKey).collect(Collectors.toSet());
        SpacesQuery spacesQuery = SpacesQuery.newQuery().withSpaceKeys(spaceKeys).build();
        Set existingSpaceKeys = this.spaceManager.getAllSpaces(spacesQuery).stream().map(Space::getKey).collect(Collectors.toSet());
        spaceKeys.removeAll(existingSpaceKeys);
        return spaceKeys;
    }
}

