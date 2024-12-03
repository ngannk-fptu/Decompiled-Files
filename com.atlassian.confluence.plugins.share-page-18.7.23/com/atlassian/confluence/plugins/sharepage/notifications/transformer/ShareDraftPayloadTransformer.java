/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 */
package com.atlassian.confluence.plugins.sharepage.notifications.transformer;

import com.atlassian.confluence.plugins.sharepage.api.ShareDraftEvent;
import com.atlassian.confluence.plugins.sharepage.notifications.transformer.AbstractPayloadTransformer;
import com.atlassian.confluence.user.UserAccessor;

public class ShareDraftPayloadTransformer
extends AbstractPayloadTransformer<ShareDraftEvent> {
    public ShareDraftPayloadTransformer(UserAccessor userAccessor) {
        super(userAccessor);
    }
}

