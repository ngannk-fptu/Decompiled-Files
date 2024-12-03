/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.JsonSpaceProperty
 *  com.atlassian.confluence.api.service.content.SpacePropertyService$SpacePropertyFinder
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty.spaceproperty;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.service.content.SpacePropertyService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyFactory;
import com.atlassian.confluence.plugins.contentproperty.TransactionWrappingFinder;
import com.atlassian.confluence.plugins.contentproperty.spaceproperty.SpacePropertyFinderImpl;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpacePropertyFinderFactory {
    private final CustomContentManager customContentManager;
    private final PermissionManager permissionManager;
    private final JsonPropertyFactory jsonPropertyFactory;
    private final SpaceService spaceService;
    private final PaginationService paginationService;
    private final TransactionalHostContextAccessor hostContextAccessor;

    @Autowired
    public SpacePropertyFinderFactory(@ComponentImport CustomContentManager customContentManager, @ComponentImport PermissionManager permissionManager, JsonPropertyFactory jsonPropertyFactory, @ComponentImport SpaceService spaceService, @ComponentImport PaginationService paginationService, @ComponentImport TransactionalHostContextAccessor hostContextAccessor) {
        this.customContentManager = customContentManager;
        this.permissionManager = permissionManager;
        this.jsonPropertyFactory = jsonPropertyFactory;
        this.spaceService = spaceService;
        this.paginationService = paginationService;
        this.hostContextAccessor = hostContextAccessor;
    }

    public SpacePropertyService.SpacePropertyFinder createSpacePropertyFinder(Expansion ... expansions) {
        SpacePropertyFinderImpl rawFinder = new SpacePropertyFinderImpl(this.customContentManager, this.permissionManager, this.jsonPropertyFactory, this.spaceService, this.paginationService, new Expansions(expansions));
        return new TransactionWrappingSpaceFinder(rawFinder, this.hostContextAccessor);
    }

    private static class TransactionWrappingSpaceFinder
    extends TransactionWrappingFinder<JsonSpaceProperty>
    implements SpacePropertyService.SpacePropertyFinder {
        private final SpacePropertyFinderImpl delegate;

        public TransactionWrappingSpaceFinder(SpacePropertyFinderImpl delegate, TransactionalHostContextAccessor hostContextAccessor) {
            super(delegate, delegate, hostContextAccessor);
            this.delegate = delegate;
        }

        public SpacePropertyService.SpacePropertyFinder withSpaceKey(String spaceKey) {
            this.delegate.withSpaceKey(spaceKey);
            return this;
        }

        public SpacePropertyService.SpacePropertyFinder withPropertyKey(String key) {
            this.delegate.withPropertyKey(key);
            return this;
        }
    }
}

