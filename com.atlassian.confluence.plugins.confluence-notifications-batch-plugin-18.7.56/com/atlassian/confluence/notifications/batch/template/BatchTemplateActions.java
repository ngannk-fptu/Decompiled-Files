/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import java.util.HashMap;
import java.util.Map;

public class BatchTemplateActions
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "actions";
    private final String sectionsId;
    private final Map<String, Object> context;
    private String contentId;

    public BatchTemplateActions(ContentId contentId, String sectionsId) {
        this.contentId = contentId.serialise();
        this.sectionsId = sectionsId;
        this.context = new HashMap<String, Object>();
    }

    public Map<String, Object> getContext() {
        return this.context;
    }

    public String getContentId() {
        return this.contentId;
    }

    public String getSectionsId() {
        return this.sectionsId;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }
}

