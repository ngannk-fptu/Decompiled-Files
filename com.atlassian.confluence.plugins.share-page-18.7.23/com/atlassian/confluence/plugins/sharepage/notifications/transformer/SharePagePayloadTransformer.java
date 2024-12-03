/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 */
package com.atlassian.confluence.plugins.sharepage.notifications.transformer;

import com.atlassian.confluence.plugins.sharepage.api.ShareContentEvent;
import com.atlassian.confluence.plugins.sharepage.notifications.transformer.AbstractPayloadTransformer;
import com.atlassian.confluence.user.UserAccessor;

public class SharePagePayloadTransformer
extends AbstractPayloadTransformer<ShareContentEvent> {
    public SharePagePayloadTransformer(UserAccessor userAccessor) {
        super(userAccessor);
    }
}

