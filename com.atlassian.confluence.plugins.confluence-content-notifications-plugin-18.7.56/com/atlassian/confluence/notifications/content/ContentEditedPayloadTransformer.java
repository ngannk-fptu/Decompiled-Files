/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.notifications.content.ContentEditedPayload;
import com.atlassian.confluence.notifications.content.ContentIdPayloadTransformerTemplate;
import com.atlassian.confluence.notifications.content.SimpleContentEditedPayload;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;

public abstract class ContentEditedPayloadTransformer<SOURCE>
extends ContentIdPayloadTransformerTemplate<SOURCE, ContentEditedPayload> {
    protected Maybe<ContentEditedPayload> checkedCreate(SOURCE source) {
        if (this.isNotificationSuppressed(source)) {
            return Option.none();
        }
        SimpleContentEditedPayload value = new SimpleContentEditedPayload(this.getContentType(source), this.getSourceId(source), this.getOriginalId(source), this.getOriginatingUserKey(source), this.getInlineContext(source));
        return Option.some((Object)value);
    }

    protected abstract boolean isNotificationSuppressed(SOURCE var1);

    protected abstract String getOriginatingUserKey(SOURCE var1);

    protected abstract ContentType getContentType(SOURCE var1);

    protected abstract long getSourceId(SOURCE var1);

    protected abstract long getOriginalId(SOURCE var1);

    protected String getInlineContext(SOURCE source) {
        return null;
    }
}

