/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.VersionHistorySummary
 *  com.atlassian.confluence.core.persistence.ContentEntityObjectDao
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Index
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.search.extractors;

import com.atlassian.confluence.contributors.search.extractors.AbstractContributionExtractor;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={Extractor2.class})
@Component
public class AuthorContributionExtractor
extends AbstractContributionExtractor {
    private static final Logger logger = LoggerFactory.getLogger(AuthorContributionExtractor.class);
    private final ContentEntityObjectDao contentEntityObjectDao;

    @Autowired
    public AuthorContributionExtractor(@ComponentImport ContentEntityObjectDao contentEntityObjectDao) {
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof AbstractPage)) {
            return null;
        }
        AbstractPage abstractPage = (AbstractPage)searchable;
        List versionHistories = this.contentEntityObjectDao.getVersionHistorySummary(abstractPage.getId());
        ImmutableList.Builder builder = ImmutableList.builder();
        for (VersionHistorySummary versionHistory : versionHistories) {
            if (versionHistory.getLastModificationDate() == null) {
                logger.warn("Page#{} version#{} has a null last modification date and will be dropped from the content edit contribution calculation.", (Object)abstractPage.getId(), (Object)versionHistory.getVersion());
                continue;
            }
            ConfluenceUser lastModifier = versionHistory.getLastModifier();
            String key = StringEscapeUtils.escapeHtml4(lastModifier == null ? null : lastModifier.getKey().getStringValue());
            long time = versionHistory.getLastModificationDate().getTime();
            String encoded = key + "<>" + time;
            builder.add((Object)new FieldDescriptor("authorContributions", encoded, FieldDescriptor.Store.YES, FieldDescriptor.Index.NOT_ANALYZED));
        }
        return builder.build();
    }
}

