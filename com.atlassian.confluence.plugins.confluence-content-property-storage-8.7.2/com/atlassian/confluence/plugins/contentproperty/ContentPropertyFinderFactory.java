/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.content.id.JsonContentPropertyId
 *  com.atlassian.confluence.api.service.content.ContentPropertyService$ContentPropertyFinder
 *  com.atlassian.confluence.api.service.content.ContentPropertyService$ParameterContentPropertyFinder
 *  com.atlassian.confluence.api.service.content.ContentPropertyService$SingleContentPropertyFetcher
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.pagination.PaginationService
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.pages.DraftManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.id.JsonContentPropertyId;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.pagination.PaginationService;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.plugins.contentproperty.ContentPropertyFinderImpl;
import com.atlassian.confluence.plugins.contentproperty.ContentPropertyFinderPermissionCheck;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyFactory;
import com.atlassian.confluence.plugins.contentproperty.TransactionWrappingFinder;
import com.atlassian.confluence.plugins.contentproperty.transaction.ThrowingTransactionCallback;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Iterator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ContentPropertyFinderFactory {
    private final ContentService contentService;
    private final PaginationService paginationService;
    private final CustomContentManager customContentManager;
    private final PermissionManager permissionManager;
    private final JsonPropertyFactory jsonPropertyFactory;
    private final TransactionalHostContextAccessor hostContextAccessor;
    private final DraftManager draftManager;

    public ContentPropertyFinderFactory(@ComponentImport ContentService contentService, @ComponentImport PaginationService paginationService, @ComponentImport CustomContentManager customContentManager, @ComponentImport PermissionManager permissionManager, JsonPropertyFactory jsonPropertyFactory, @ComponentImport TransactionalHostContextAccessor hostContextAccessor, @ComponentImport DraftManager draftManager) {
        this.contentService = contentService;
        this.paginationService = paginationService;
        this.customContentManager = customContentManager;
        this.permissionManager = permissionManager;
        this.jsonPropertyFactory = jsonPropertyFactory;
        this.hostContextAccessor = hostContextAccessor;
        this.draftManager = draftManager;
    }

    public ContentPropertyService.ContentPropertyFinder createContentPropertyFinder(Expansion ... expansions) {
        return this.createContentPropertyFinder(ContentPropertyFinderPermissionCheck.YES, expansions);
    }

    public ContentPropertyService.ContentPropertyFinder createContentPropertyFinder(ContentPropertyFinderPermissionCheck permissionCheck, Expansion ... expansions) {
        ContentPropertyFinderImpl rawFinder = new ContentPropertyFinderImpl(this.contentService, this.paginationService, this.customContentManager, this.permissionManager, this.jsonPropertyFactory, permissionCheck, this.draftManager, expansions);
        return new TransactionWrappingContentFinder(rawFinder, this.hostContextAccessor);
    }

    private static class TransactionWrappingContentFinder
    extends TransactionWrappingFinder<JsonContentProperty>
    implements ContentPropertyService.ContentPropertyFinder {
        private final ContentPropertyService.ContentPropertyFinder delegate;

        public TransactionWrappingContentFinder(ContentPropertyFinderImpl delegate, TransactionalHostContextAccessor hostContextAccessor) {
            super(delegate, delegate, hostContextAccessor);
            this.delegate = delegate;
        }

        public ContentPropertyService.ParameterContentPropertyFinder withKey(String key) {
            this.delegate.withPropertyKey(key);
            return this;
        }

        public ContentPropertyService.ParameterContentPropertyFinder withPropertyKey(String key) {
            this.delegate.withPropertyKey(key);
            return this;
        }

        public ContentPropertyService.ParameterContentPropertyFinder withPropertyKeys(List<String> keys) {
            this.delegate.withPropertyKeys(keys);
            return this;
        }

        public ContentPropertyService.SingleContentPropertyFetcher withId(JsonContentPropertyId contentPropertyId) {
            this.delegate.withId(contentPropertyId);
            return this;
        }

        public ContentPropertyService.ParameterContentPropertyFinder withContentId(ContentId contentId) {
            this.delegate.withContentId(contentId);
            return this;
        }

        public ContentPropertyService.ParameterContentPropertyFinder withContentIds(List<ContentId> contentIds) {
            return this.delegate.withContentIds(contentIds);
        }

        public Iterator<String> fetchPropertyKeys() {
            ThrowingTransactionCallback<Iterator<String>, NotFoundException> callback = new ThrowingTransactionCallback<Iterator<String>, NotFoundException>(){

                @Override
                public Iterator<String> doInTransaction() throws NotFoundException {
                    return delegate.fetchPropertyKeys();
                }
            };
            return this.executeReadOnly(callback);
        }
    }
}

