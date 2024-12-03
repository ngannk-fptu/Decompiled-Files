/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import java.util.Map;
import java.util.Optional;

@Internal
public interface DiffContextProvider {
    @Deprecated
    public Map<String, Object> generateDiffContext(ContentId var1, ContentId var2, Option<UserKey> var3);

    default public Map<String, Object> generateDiffContext(ContentId current, ContentId original, Optional<UserKey> recipient) {
        return this.generateDiffContext(current, original, (Option<UserKey>)Option.option((Object)recipient.orElse(null)));
    }
}

