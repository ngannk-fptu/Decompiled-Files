/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.createcontent.services.model;

import java.util.Map;

public interface CreateBlueprintPageEntity {
    public long getSpaceId();

    public String getSpaceKey();

    public long getParentPageId();

    public String getContentBlueprintId();

    public String getModuleCompleteKey();

    public Map<String, Object> getContext();

    public String getTitle();

    public String getViewPermissionsUsers();

    public String getContentTemplateId();

    public String getContentTemplateKey();
}

