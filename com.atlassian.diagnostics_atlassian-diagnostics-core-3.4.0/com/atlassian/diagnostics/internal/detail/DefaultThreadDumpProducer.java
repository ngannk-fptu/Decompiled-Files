/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.detail.ThreadDump
 *  com.atlassian.diagnostics.detail.ThreadDumpProducer
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.diagnostics.internal.detail;

import com.atlassian.diagnostics.detail.ThreadDump;
import com.atlassian.diagnostics.detail.ThreadDumpProducer;
import com.atlassian.diagnostics.internal.detail.SimpleThreadDump;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class DefaultThreadDumpProducer
implements ThreadDumpProducer {
    private static final int MAX_STACK_LENGTH = 128000;
    private static final ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();

    @Nonnull
    public List<ThreadDump> produce(@Nonnull Set<Thread> threads) {
        if (threads.isEmpty()) {
            return Collections.emptyList();
        }
        Map threadsById = threads.stream().collect(Collectors.toMap(Thread::getId, Function.identity()));
        long[] threadIds = threadsById.keySet().stream().mapToLong(Long::longValue).toArray();
        return Arrays.stream(threadMxBean.getThreadInfo(threadIds, 4000)).filter(Objects::nonNull).map(threadInfo -> new SimpleThreadDump((Thread)threadsById.get(threadInfo.getThreadId()), this.toStackTraceString(Arrays.asList(threadInfo.getStackTrace())))).collect(Collectors.toList());
    }

    @Nullable
    public String toStackTraceString(List<StackTraceElement> elements) {
        if (elements.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        elements.stream().map(Object::toString).forEach(elementLine -> {
            if (sb.length() < 128000) {
                sb.append((String)elementLine);
                if (sb.length() < 128000) {
                    sb.append('\n');
                }
            }
        });
        return StringUtils.trimToNull((String)sb.toString());
    }
}

