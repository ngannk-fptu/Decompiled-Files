/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Function
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Iterables
 */
package com.atlassian.streams.common.renderer;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class CompoundStatementRenderer<T>
implements Function<Iterable<T>, Option<Html>> {
    private final I18nResolver i18nResolver;
    private final java.util.function.Function<T, Option<Html>> render;

    @Deprecated
    public CompoundStatementRenderer(I18nResolver i18nResolver, Function<T, Option<Html>> render) {
        this.i18nResolver = i18nResolver;
        this.render = render;
    }

    public CompoundStatementRenderer(I18nResolver i18nResolver, java.util.function.Function<T, Option<Html>> render) {
        this.i18nResolver = i18nResolver;
        this.render = render;
    }

    public Option<Html> apply(Iterable<T> xs) {
        Stream<T> xsStream = StreamSupport.stream(xs.spliterator(), false);
        Iterable rendered = xsStream.map(this.render).filter(Option::isDefined).map(Option::get).collect(Collectors.toList());
        if (Iterables.isEmpty((Iterable)rendered)) {
            return Option.none();
        }
        int numRendered = Iterables.size((Iterable)rendered);
        if (numRendered == 1) {
            return Option.some((Object)Iterables.get((Iterable)rendered, (int)0));
        }
        Iterator partitions = Iterables.partition((Iterable)rendered, (int)(numRendered - 1)).iterator();
        Iterable allButLast = (Iterable)partitions.next();
        Iterable last = (Iterable)partitions.next();
        return Option.some((Object)new Html(Joiner.on((String)", ").join(allButLast) + " " + this.i18nResolver.getText("streams.and") + " " + Iterables.get((Iterable)last, (int)0)));
    }
}

