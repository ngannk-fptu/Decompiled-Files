/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import java.util.regex.Pattern;

@ExperimentalApi
@Deprecated
public class MessageIdUtil {
    private static final Pattern MESSAGE_ID_PATTERN = Pattern.compile("\\[(.*)]\\[(\\d+)]");

    public static String encodeMessageId(String completeKey, Content content) {
        return String.format("%s", content.getId().asLong());
    }

    @Deprecated
    public static Option<Pair<String, String>> extractFrom(String messageId) {
        if (messageId == null) {
            return Option.none();
        }
        return Option.some((Object)Pair.pair((Object)messageId, (Object)messageId));
    }
}

