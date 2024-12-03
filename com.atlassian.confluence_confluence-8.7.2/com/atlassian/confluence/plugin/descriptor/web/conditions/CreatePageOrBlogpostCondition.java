/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.google.common.annotations.VisibleForTesting;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CreatePageOrBlogpostCondition
extends BaseConfluenceCondition {
    private static final Logger log = LoggerFactory.getLogger(CreatePageOrBlogpostCondition.class);
    private static final String PAGE_CONTENT_TYPE = ContentType.PAGE.serialise();
    private static final String BLOGPOST_CONTENT_TYPE = ContentType.BLOG_POST.serialise();
    private SpaceManager spaceManager;
    private String contentType;
    private Optional<SpaceType> spaceType;

    @Override
    public void init(Map<String, String> params) {
        this.spaceType = Optional.ofNullable(SpaceType.getSpaceType(params.get("spaceType")));
        this.contentType = this.checkValidContentType(params.get("contentType"));
        super.init(params);
    }

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        String permission = PAGE_CONTENT_TYPE.equalsIgnoreCase(this.contentType) ? "EDITSPACE" : "EDITBLOG";
        log.trace("Checking permission for current user to create '{}' anywhere.", (Object)this.contentType);
        SpacesQuery.Builder spaceQuery = SpacesQuery.newQuery().forUser(context.getCurrentUser()).withSpaceStatus(SpaceStatus.CURRENT).withPermission(permission).unsorted();
        if (this.spaceType.isPresent()) {
            spaceQuery.withSpaceType(this.spaceType.get());
        }
        return !this.spaceManager.getSpaces(spaceQuery.build()).getPage(0, 1).isEmpty();
    }

    @VisibleForTesting
    String checkValidContentType(String contentType) {
        if (!PAGE_CONTENT_TYPE.equalsIgnoreCase(contentType) && !BLOGPOST_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            throw new IllegalArgumentException(String.format("Invalid 'contentType' parameter specified: '%s'. Legal values are: '%s' and '%s'.", contentType, PAGE_CONTENT_TYPE, BLOGPOST_CONTENT_TYPE));
        }
        return contentType;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}

