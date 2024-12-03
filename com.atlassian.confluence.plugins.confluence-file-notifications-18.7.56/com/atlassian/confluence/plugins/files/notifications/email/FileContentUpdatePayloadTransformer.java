/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.plugins.files.notifications.email.FileContentUpdatePayload;
import com.atlassian.confluence.plugins.files.notifications.event.FileContentUpdateEvent;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public class FileContentUpdatePayloadTransformer
extends PayloadTransformerTemplate<FileContentUpdateEvent, FileContentUpdatePayload> {
    protected Maybe<FileContentUpdatePayload> checkedCreate(FileContentUpdateEvent event) {
        if (event.isSuppressNotifications()) {
            return Option.none();
        }
        return Option.some((Object)new FileContentUpdatePayload(event.getType(), event.getContainerContent(), event.getFileContents(), event.getPreviousFileContent(), event.getDescendantContent(), event.getOriginatingUserKey()));
    }
}

