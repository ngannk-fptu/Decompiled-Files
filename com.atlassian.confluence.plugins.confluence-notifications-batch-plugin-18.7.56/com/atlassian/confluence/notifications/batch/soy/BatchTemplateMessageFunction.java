/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.soy;

import com.atlassian.confluence.notifications.batch.soy.BatchTemplateFunction;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BatchTemplateMessageFunction
extends BatchTemplateFunction<List<Object>> {
    private static final Set<Integer> ARGS = Collections.singleton(1);

    private static int addNextChunk(String s, int idx, int startChunkIdx, List<String> strings) {
        if (idx != startChunkIdx) {
            strings.add(s.substring(startChunkIdx, idx));
            startChunkIdx = idx;
        }
        return startChunkIdx;
    }

    public List<Object> apply(Object ... args) {
        BatchTemplateMessage obj = this.checkArgument(args[0], BatchTemplateMessage.class);
        Map<String, Object> objArgs = obj.getArgs();
        return this.extractVars(obj.getMessage()).stream().map(s -> s.charAt(0) == '$' ? objArgs.get(s.substring(1)) : s).collect(Collectors.toList());
    }

    private List<String> extractVars(String message) {
        int idx;
        int startChunkIdx = 0;
        char[] chars = message.toCharArray();
        boolean isVar = false;
        ArrayList<String> strings = new ArrayList<String>();
        for (idx = 0; idx < chars.length; ++idx) {
            char c = chars[idx];
            if (c != '$' && (!isVar || Character.isJavaIdentifierPart(c))) continue;
            isVar = c == '$';
            startChunkIdx = BatchTemplateMessageFunction.addNextChunk(message, idx, startChunkIdx, strings);
        }
        BatchTemplateMessageFunction.addNextChunk(message, idx, startChunkIdx, strings);
        return strings;
    }

    public String getName() {
        return "batchMessage";
    }

    public Set<Integer> validArgSizes() {
        return ARGS;
    }
}

