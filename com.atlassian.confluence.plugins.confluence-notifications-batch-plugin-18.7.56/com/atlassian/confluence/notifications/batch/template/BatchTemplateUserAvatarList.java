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
public class BatchTemplateUserAvatarList
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "userAvatarList";
    private final Iterable<UserKey> userKeys;

    public BatchTemplateUserAvatarList(Iterable<UserKey> userKeys) {
        this.userKeys = userKeys;
    }

    public Iterable<UserKey> getUserKeys() {
        return this.userKeys;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }
}

