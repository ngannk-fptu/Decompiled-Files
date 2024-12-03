/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Effect
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.DefaultMarshallerMetricsAccumulator;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetrics;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulationKey;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulator;
import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarshallerMetricsAccumulatorStack {
    private static final Logger log = LoggerFactory.getLogger(MarshallerMetricsAccumulatorStack.class);
    private static final String CONTEXT_PROPERTY_NAME = MarshallerMetricsAccumulatorStack.class.getSimpleName();

    private static Iterable<MarshallerMetrics> getMetricsSnapshots(ConversionContext context, Predicate<? super MarshallerMetricsAccumulationKey> accumulationKeyFilter) {
        Deque<MarshallerMetricsAccumulator> stack = MarshallerMetricsAccumulatorStack.getStack(context);
        if (stack.isEmpty()) {
            return Collections.emptyList();
        }
        return stack.peek().getMetricsSnapshots(accumulationKeyFilter);
    }

    public static void forEachMetricsSnapshot(ConversionContext context, Predicate<? super MarshallerMetricsAccumulationKey> accumulationKeyFilter, Effect<MarshallerMetrics> effect) {
        for (MarshallerMetrics snapshot : MarshallerMetricsAccumulatorStack.getMetricsSnapshots(context, accumulationKeyFilter)) {
            effect.apply((Object)snapshot);
        }
    }

    @VisibleForTesting
    static @NonNull Deque<MarshallerMetricsAccumulator> getStack(@Nullable ConversionContext context) {
        return context == null ? new LinkedList<MarshallerMetricsAccumulator>() : (Deque)context.getProperty(CONTEXT_PROPERTY_NAME, new LinkedList());
    }

    public static Maybe<MarshallerMetricsAccumulator> getCurrentMetricsAccumulator(ConversionContext context) {
        Deque<MarshallerMetricsAccumulator> stack = MarshallerMetricsAccumulatorStack.getStack(context);
        return Option.option((Object)stack.peek());
    }

    public static void pushNewMetricsAccumulator(@Nullable ConversionContext context) {
        MarshallerMetricsAccumulatorStack.push(context, new DefaultMarshallerMetricsAccumulator());
    }

    @VisibleForTesting
    static void push(@Nullable ConversionContext context, MarshallerMetricsAccumulator accumulator) {
        if (context != null) {
            log.debug("Pushing new metrics collector factory on to the stack");
            Deque<MarshallerMetricsAccumulator> stack = MarshallerMetricsAccumulatorStack.getStack(context);
            stack.push((MarshallerMetricsAccumulator)Preconditions.checkNotNull((Object)accumulator));
            context.setProperty(CONTEXT_PROPERTY_NAME, stack);
        }
    }

    public static void pop(@Nullable ConversionContext context) {
        if (context != null) {
            Deque<MarshallerMetricsAccumulator> stack = MarshallerMetricsAccumulatorStack.getStack(context);
            if (!stack.isEmpty()) {
                stack.pop();
                context.setProperty(CONTEXT_PROPERTY_NAME, stack);
            } else {
                log.warn("No metrics collector factory on the stack, cannot pop. This indicates a bad code path.");
            }
        }
    }
}

