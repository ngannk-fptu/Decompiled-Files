/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.notifications.batch.soy;

import com.atlassian.confluence.notifications.batch.soy.BatchTemplateFunction;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateMessage;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateUserFullName;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;

public class BatchTemplateMessageMapFunction
extends BatchTemplateFunction<BatchTemplateMessage> {
    private static final Set<Integer> ARGS = ImmutableSet.of((Object)1, (Object)2);

    public BatchTemplateMessage apply(Object ... args) {
        String message = this.checkArgument(args[0], String.class);
        Map messageArgs = this.checkArgument(args[1], Map.class);
        BatchTemplateMessage.Builder builder = new BatchTemplateMessage.Builder(message);
        for (Map.Entry entry : messageArgs.entrySet()) {
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                builder.arg(key, (String)value);
                continue;
            }
            if (value instanceof Number) {
                String strNum = value instanceof Double || value instanceof Float ? String.format("%.3f", ((Number)value).doubleValue()) : Long.toString(((Number)value).longValue());
                builder.arg(key, strNum);
                continue;
            }
            if (value instanceof BatchTemplateElement) {
                builder.arg(key, (BatchTemplateElement)value);
                continue;
            }
            if (!(value instanceof ConfluenceUser) && !(value instanceof UserKey)) continue;
            UserKey userKey = value instanceof UserKey ? (UserKey)value : ((ConfluenceUser)value).getKey();
            builder.arg(key, new BatchTemplateUserFullName(userKey));
        }
        return builder.build();
    }

    public String getName() {
        return "batchMessageMap";
    }

    public Set<Integer> validArgSizes() {
        return ARGS;
    }
}

