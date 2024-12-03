/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 */
package com.atlassian.confluence.plugins.sharepage.notifications.transformer;

import com.atlassian.confluence.plugins.sharepage.api.ShareAttachmentEvent;
import com.atlassian.confluence.plugins.sharepage.notifications.transformer.AbstractPayloadTransformer;
import com.atlassian.confluence.user.UserAccessor;

public class ShareAttachmentPayloadTransformer
extends AbstractPayloadTransformer<ShareAttachmentEvent> {
    public ShareAttachmentPayloadTransformer(UserAccessor userAccessor) {
        super(userAccessor);
    }
}

