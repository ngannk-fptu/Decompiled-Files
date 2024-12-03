/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Maybe
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.content.ContentIdPayload;
import com.atlassian.fugue.Maybe;
import java.util.Optional;

@ExperimentalApi
public interface ContentEditedPayload
extends ContentIdPayload {
    public long getOriginalId();

    @Deprecated
    public Maybe<String> getInlineContext();

    default public Optional<String> optionalInlineContext() {
        return Optional.ofNullable((String)this.getInlineContext().getOrNull());
    }
}

