/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.sal.api.user.UserKey;

@Deprecated
public class BatchTemplateUserFullName
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "userFullName";
    private final UserKey userKey;

    public BatchTemplateUserFullName(UserKey userKey) {
        this.userKey = userKey;
    }

    public UserKey getUserKey() {
        return this.userKey;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }
}

