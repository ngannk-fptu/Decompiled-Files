/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  io.atlassian.fugue.Pair
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.audit.uri;

import com.atlassian.confluence.api.impl.service.audit.uri.ResourceUriGenerator;
import com.atlassian.confluence.api.impl.service.audit.uri.UriGeneratorHelper;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.internal.content.DraftUtils;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import io.atlassian.fugue.Pair;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageUriGenerator
implements ResourceUriGenerator {
    private static final Logger log = LoggerFactory.getLogger(PageUriGenerator.class);
    private final PageManagerInternal pageManager;
    private final ContentEntityManagerInternal contentEntityManager;

    public PageUriGenerator(PageManagerInternal pageManager, ContentEntityManagerInternal contentEntityManager) {
        this.pageManager = pageManager;
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public Map<String, URI> generate(URI baseUrl, Set<String> identifiers) {
        Set parsedIds = identifiers.stream().filter(NumberUtils::isParsable).map(Long::valueOf).collect(Collectors.toSet());
        return parsedIds.stream().map(contentId -> this.contentEntityManager.getById(ContentId.of((long)contentId))).filter(Objects::nonNull).map(this::getAddressable).filter(pair -> Objects.nonNull(pair) && Objects.nonNull(pair.right())).collect(Collectors.toMap(pair -> String.valueOf(pair.left()), pair -> UriGeneratorHelper.contentUri(baseUrl, (Addressable)pair.right())));
    }

    private @Nullable Pair<Long, Addressable> getAddressable(ContentEntityObject content) {
        long originalId = content.getId();
        try {
            if (DraftUtils.isPageOrBlogPost(content)) {
                return Pair.pair((Object)originalId, (Object)content);
            }
            if (DraftUtils.isDraft(content) && content.isUnpublished()) {
                return DraftUtils.isPersonalDraft(content) ? Pair.pair((Object)originalId, (Object)content) : null;
            }
            if (DraftUtils.isDraft(content) && !content.isUnpublished()) {
                if (DraftUtils.isPersonalDraft(content)) {
                    Draft publishedPersonalDraft = (Draft)content;
                    return Pair.pair((Object)originalId, (Object)this.pageManager.getAbstractPage(publishedPersonalDraft.getPageIdAsLong()));
                }
                AbstractPage publishedSharedDraft = (AbstractPage)content;
                return Pair.pair((Object)originalId, (Object)publishedSharedDraft.getOriginalVersionPage());
            }
        }
        catch (Exception e) {
            log.debug("Error generating link for {}", (Object)content, (Object)e);
        }
        return null;
    }
}

