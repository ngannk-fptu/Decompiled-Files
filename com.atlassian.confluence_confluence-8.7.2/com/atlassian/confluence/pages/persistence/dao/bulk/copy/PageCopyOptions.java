/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.copy;

import com.atlassian.confluence.pages.persistence.dao.bulk.DefaultBulkOptions;
import com.atlassian.confluence.pages.persistence.dao.bulk.PageContentTransformer;
import com.atlassian.confluence.pages.persistence.dao.bulk.PageNameConflictResolver;
import com.atlassian.confluence.pages.persistence.dao.bulk.impl.DefaultPageNameConflictResolver;
import com.atlassian.confluence.pages.persistence.dao.bulk.impl.FindAndReplaceNameConflictResolver;
import com.atlassian.confluence.pages.persistence.dao.bulk.impl.KeepAsIsContentTransformer;
import com.atlassian.confluence.pages.persistence.dao.bulk.impl.PrefixNameConflictResolver;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageCopyOptions
extends DefaultBulkOptions
implements Serializable {
    private static final long serialVersionUID = 4351648231357402073L;
    private final boolean shouldCopyAttachments;
    private final boolean shouldCopyPermissions;
    private final boolean shouldCopyContentProperties;
    private final boolean shouldCopyLabels;
    private final boolean shouldSkipLinkUpdates;
    private final String requestId;
    private final transient PageNameConflictResolver pageNameConflictResolver;
    private final transient PageContentTransformer pageContentTransformer;

    private PageCopyOptions(DefaultBulkOptions defaultOptions, boolean shouldCopyAttachments, boolean shouldCopyPermissions, boolean shouldCopyContentProperties, boolean shouldCopyLabels, @Nullable String requestId, PageNameConflictResolver pageNameConflictResolver, PageContentTransformer pageContentTransformer, boolean shouldSkipLinkUpdates) {
        super(defaultOptions);
        this.shouldCopyAttachments = shouldCopyAttachments;
        this.shouldCopyPermissions = shouldCopyPermissions;
        this.shouldCopyContentProperties = shouldCopyContentProperties;
        this.shouldCopyLabels = shouldCopyLabels;
        this.requestId = requestId;
        this.pageNameConflictResolver = pageNameConflictResolver;
        this.pageContentTransformer = pageContentTransformer;
        this.shouldSkipLinkUpdates = shouldSkipLinkUpdates;
    }

    public boolean shouldCopyLabels() {
        return this.shouldCopyLabels;
    }

    public boolean shouldCopyAttachments() {
        return this.shouldCopyAttachments;
    }

    public boolean shouldCopyPermissions() {
        return this.shouldCopyPermissions;
    }

    public boolean shouldCopyContentProperties() {
        return this.shouldCopyContentProperties;
    }

    public @Nullable String getRequestId() {
        return this.requestId;
    }

    public PageNameConflictResolver getPageNameConflictResolver() {
        return this.pageNameConflictResolver;
    }

    public PageContentTransformer getPageContentTransformer() {
        return this.pageContentTransformer;
    }

    public boolean shouldSkipLinkUpdates() {
        return this.shouldSkipLinkUpdates;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    extends DefaultBulkOptions.BaseBuilder<Builder> {
        private boolean shouldCopyAttachments;
        private boolean shouldCopyPermissions;
        private boolean shouldCopyContentProperties;
        private boolean shouldCopyLabels;
        private boolean shouldSkipLinkUpdates;
        private String requestId;
        private PageNameConflictResolver pageNameConflictResolver;
        private PageContentTransformer pageContentTransformer;

        @Override
        protected final Builder builder() {
            return this;
        }

        public Builder withPageCopyOptions(PageCopyOptions pageCopyOptions) {
            return ((Builder)((Builder)((Builder)((Builder)this.withCopyLabel(pageCopyOptions.shouldCopyLabels()).withMaxProcessedEntries(pageCopyOptions.getMaxProcessedEntries())).withBatchSize(pageCopyOptions.getBatchSize())).withUser(pageCopyOptions.getUser())).withProgressMeter(pageCopyOptions.getProgressMeter())).withNameConflictResolver(pageCopyOptions.getPageNameConflictResolver()).withPageContentTranformer(pageCopyOptions.getPageContentTransformer()).withContentProperty(pageCopyOptions.shouldCopyContentProperties()).withCopyLabel(pageCopyOptions.shouldCopyLabels()).withRequestId(pageCopyOptions.getRequestId()).withCopyPermission(pageCopyOptions.shouldCopyPermissions()).withCopyAttachment(pageCopyOptions.shouldCopyAttachments()).withSkipLinkUpdates(pageCopyOptions.shouldSkipLinkUpdates());
        }

        public Builder withCopyLabel(boolean copyLabel) {
            this.shouldCopyLabels = copyLabel;
            return this;
        }

        public Builder withCopyAttachment(boolean copyAttachment) {
            this.shouldCopyAttachments = copyAttachment;
            return this;
        }

        public Builder withCopyPermission(boolean copyPermission) {
            this.shouldCopyPermissions = copyPermission;
            return this;
        }

        public Builder withContentProperty(boolean copyContentProperty) {
            this.shouldCopyContentProperties = copyContentProperty;
            return this;
        }

        public Builder withRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder withNameConflictResolver(PageNameConflictResolver pageNameConflictResolver) {
            this.pageNameConflictResolver = pageNameConflictResolver;
            return this;
        }

        public Builder withReplaceNameConflictResolver(String searchText, String replaceText) {
            Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)searchText) ? 1 : 0) != 0);
            Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)replaceText) ? 1 : 0) != 0);
            this.pageNameConflictResolver = new FindAndReplaceNameConflictResolver(searchText, replaceText);
            return this;
        }

        public Builder withPrefixNameConflictResolver(String prefix) {
            Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)prefix) ? 1 : 0) != 0);
            this.pageNameConflictResolver = new PrefixNameConflictResolver(prefix);
            return this;
        }

        public Builder withPrefixNameConflictResolver(boolean applyForNewName, String prefix, int maxRetry) {
            Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)prefix) ? 1 : 0) != 0);
            this.pageNameConflictResolver = maxRetry <= 0 ? new PrefixNameConflictResolver(applyForNewName, prefix) : new PrefixNameConflictResolver(applyForNewName, prefix, maxRetry);
            return this;
        }

        public Builder withPrefixNameConflictResolver(boolean applyForNewName, String prefix) {
            Preconditions.checkArgument((!StringUtils.isEmpty((CharSequence)prefix) ? 1 : 0) != 0);
            this.pageNameConflictResolver = new PrefixNameConflictResolver(applyForNewName, prefix);
            return this;
        }

        public Builder withPageContentTranformer(PageContentTransformer pageContentTransformer) {
            this.pageContentTransformer = pageContentTransformer;
            return this;
        }

        public Builder withSkipLinkUpdates(boolean shouldSkipLinkUpdates) {
            this.shouldSkipLinkUpdates = shouldSkipLinkUpdates;
            return this;
        }

        public PageCopyOptions build() {
            DefaultBulkOptions defaultBulkOptions = super.buildDefault();
            return new PageCopyOptions(defaultBulkOptions, this.shouldCopyAttachments, this.shouldCopyPermissions, this.shouldCopyContentProperties, this.shouldCopyLabels, this.requestId, new DefaultPageNameConflictResolver(this.pageNameConflictResolver), this.pageContentTransformer == null ? new KeepAsIsContentTransformer() : this.pageContentTransformer, this.shouldSkipLinkUpdates);
        }
    }
}

