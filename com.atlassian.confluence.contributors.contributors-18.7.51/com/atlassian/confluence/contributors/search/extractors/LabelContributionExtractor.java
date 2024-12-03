/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.Labelling
 *  com.atlassian.confluence.labels.Namespace
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Index
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.search.extractors;

import com.atlassian.confluence.contributors.search.extractors.AbstractContributionExtractor;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Date;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

@ExportAsService(value={Extractor2.class})
@Component
public class LabelContributionExtractor
extends AbstractContributionExtractor {
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof AbstractPage)) {
            return null;
        }
        AbstractPage abstractPage = (AbstractPage)searchable;
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Labelling labelling : abstractPage.getLabellings()) {
            Label label;
            if (labelling == null || (label = labelling.getLabel()) == null) continue;
            Date lastModificationDate = label.getLastModificationDate();
            ConfluenceUser owningUser = labelling.getOwningUser();
            if (lastModificationDate == null || owningUser == null || owningUser.getKey() == null) continue;
            String namespace = StringEscapeUtils.escapeHtml4((String)(Namespace.PERSONAL.equals((Object)label.getNamespace()) ? label.toStringWithOwnerPrefix() : label.toStringWithNamespace()));
            long time = lastModificationDate.getTime();
            String userKey = StringEscapeUtils.escapeHtml4((String)owningUser.getKey().getStringValue());
            String encoded = namespace + "<>" + time + "<>" + userKey;
            builder.add((Object)new FieldDescriptor("labelContributions", encoded, FieldDescriptor.Store.YES, FieldDescriptor.Index.NOT_ANALYZED));
        }
        return builder.build();
    }
}

