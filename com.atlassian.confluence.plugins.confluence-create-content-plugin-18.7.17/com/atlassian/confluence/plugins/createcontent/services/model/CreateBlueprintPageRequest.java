/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.createcontent.services.model;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;

public class CreateBlueprintPageRequest {
    private final Space space;
    private final String title;
    private final String viewPermissionsUsers;
    private final Page parentPage;
    private final Map<String, Object> context;
    private final ContentTemplateRef contentTemplateRef;
    private final ConfluenceUser creator;
    private final ContentBlueprint contentBlueprint;

    public CreateBlueprintPageRequest(Space space, String title, String viewPermissionsUsers, Page parentPage, Map<String, Object> context, ContentTemplateRef contentTemplateRef, ConfluenceUser creator, ContentBlueprint contentBlueprint) {
        this.space = space;
        this.title = title;
        this.viewPermissionsUsers = viewPermissionsUsers;
        this.parentPage = parentPage;
        this.context = context;
        this.contentTemplateRef = contentTemplateRef;
        this.creator = creator;
        this.contentBlueprint = contentBlueprint;
    }

    public Page getParentPage() {
        return this.parentPage;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }

    public String getTitle() {
        return this.title;
    }

    public String getViewPermissionsUsers() {
        return this.viewPermissionsUsers;
    }

    public ContentTemplateRef getContentTemplateRef() {
        return this.contentTemplateRef;
    }

    public Space getSpace() {
        return this.space;
    }

    public ConfluenceUser getCreator() {
        return this.creator;
    }

    public ContentBlueprint getContentBlueprint() {
        return this.contentBlueprint;
    }
}

