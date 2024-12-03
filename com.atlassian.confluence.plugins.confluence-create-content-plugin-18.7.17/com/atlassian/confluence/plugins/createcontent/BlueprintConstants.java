/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContextKeys;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import java.util.UUID;

public final class BlueprintConstants {
    public static final String CREATE_DIALOG_INIT_PARAMS_KEY = "createDialogInitParams";
    @Deprecated
    public static final String BLUEPRINT_PARAM_KEY = BlueprintContextKeys.BLUEPRINT_MODULE_KEY.key();
    @Deprecated
    public static final String SPACE_KEY = BlueprintContextKeys.SPACE_KEY.key();
    @Deprecated
    public static final String TEMPLATE_LABEL = BlueprintContextKeys.TEMPLATE_LABEL.key();
    @Deprecated
    public static final String ANALYTICS_KEY = BlueprintContextKeys.ANALYTICS_KEY.key();
    @Deprecated
    public static final String PAGE_FROM_TEMPLATE_TITLE = BlueprintContextKeys.CREATE_FROM_TEMPLATE_LABEL.key();
    public static final String CREATE_RESULT = "createResult";
    public static final String CREATE_RESULT_VIEW = "view";
    public static final String CREATE_RESULT_EDIT = "edit";
    public static final String CREATE_RESULT_SPACE = "space";
    public static final String INDEX_KEY = "indexKey";
    public static final String INDEX_DISABLED = "com.atlassian.confluence.plugins.confluence-create-content-plugin.blueprint-index-disabled";
    public static final String CREATE_DIALOG_CONTENT_SECTION = "system.create.dialog/content";
    public static final String CREATE_SPACE_DIALOG_CONTENT_SECTION = "system.create.space.dialog/content";
    public static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-create-content-plugin";
    public static final String MODULE_KEY_BLANK_PAGE = "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blank-page";
    public static final String MODULE_KEY_BLOG_POST = "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blog-post";
    public static final String MODULE_KEY_BLANK_SPACE = "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blank-space-blueprint";
    public static final String MODULE_KEY_PERSONAL_SPACE = "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-personal-space-blueprint";
    public static final String MODULE_KEY_BLANK_SPACE_ITEM = "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-blank-space-item";
    public static final String MODULE_KEY_PERSONAL_SPACE_ITEM = "com.atlassian.confluence.plugins.confluence-create-content-plugin:create-personal-space-item";
    @Deprecated
    public static final UUID UUID_BLANK_PAGE = new UUID(0L, 0L);
    @Deprecated
    public static final UUID UUID_BLOG_POST = new UUID(0L, 1L);
    public static final ContentBlueprint BLANK_PAGE_BLUEPRINT = new ContentBlueprint(UUID_BLANK_PAGE);
    public static final ContentBlueprint BLOG_POST_BLUEPRINT = new ContentBlueprint(UUID_BLOG_POST);
}

