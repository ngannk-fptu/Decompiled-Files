/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InheritedContentPermissionManager;
import com.atlassian.confluence.impl.search.v2.DefaultContentPermissionCalculator;
import com.atlassian.confluence.impl.search.v2.extractor.ContentPermissionExtractorHelper;
import com.atlassian.confluence.impl.search.v2.lucene.ContentPermissionSearchUtils;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.ContentPermissionCalculator;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.security.ContentPermissionSet;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class ContentPermissionsExtractor
implements Extractor2 {
    private InheritedContentPermissionManager inheritedContentPermissionManager;
    private ContentPermissionCalculator contentPermissionCalculator;

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof ContentEntityObject)) {
            return Collections.emptyList();
        }
        ContentEntityObject content = (ContentEntityObject)searchable;
        ContentEntityObject container = ContentPermissionExtractorHelper.getContainerForPermissions(content);
        if (!(container instanceof AbstractPage)) {
            return Collections.emptyList();
        }
        Collection<ContentPermissionSet> indexablePermissionSets = this.getContentPermissionCalculator().calculate(content);
        if (indexablePermissionSets.isEmpty()) {
            return Collections.emptyList();
        }
        return indexablePermissionSets.stream().map(ContentPermissionSearchUtils::getEncodedPermissionsCollection).map(SearchFieldMappings.CONTENT_PERMISSION_SETS::createField).collect(Collectors.toSet());
    }

    public void setInheritedContentPermissionManager(InheritedContentPermissionManager inheritedContentPermissionManager) {
        this.inheritedContentPermissionManager = inheritedContentPermissionManager;
    }

    private ContentPermissionCalculator getContentPermissionCalculator() {
        if (this.contentPermissionCalculator == null) {
            this.contentPermissionCalculator = new DefaultContentPermissionCalculator(this.inheritedContentPermissionManager);
        }
        return this.contentPermissionCalculator;
    }
}

