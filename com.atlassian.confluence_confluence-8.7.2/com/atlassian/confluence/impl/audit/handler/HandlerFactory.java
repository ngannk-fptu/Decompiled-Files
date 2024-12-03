/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.MapDifference
 *  com.google.common.collect.MapDifference$ValueDifference
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.audit.handler;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import com.atlassian.confluence.impl.audit.handler.Handler;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

class HandlerFactory {
    private static final Handler<String> STRING_HANDLER = new Handler<String>(){
        private static final int MAX_VALUE_LENGTH = 255;

        @Override
        public Stream<ChangedValue> handle(Optional<String> name, String ref, AuditAction action) {
            String value = StringUtils.abbreviate((String)ref, (int)255);
            if (action == AuditAction.ADD) {
                return Stream.of(ChangedValue.fromI18nKeys((String)name.get()).to(value).build());
            }
            if (action == AuditAction.REMOVE) {
                return Stream.of(ChangedValue.fromI18nKeys((String)name.get()).from(value).build());
            }
            throw new IllegalArgumentException(action + " is not supported");
        }

        @Override
        public Stream<ChangedValue> handle(Optional<String> name, String oldT, String newT) {
            return Stream.of(ChangedValue.fromI18nKeys((String)name.get()).from(StringUtils.abbreviate((String)oldT, (int)255)).to(StringUtils.abbreviate((String)newT, (int)255)).build());
        }
    };
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    public HandlerFactory(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    public static <T> Handler<Collection<T>> collectionHandler(final Handler<T> elementHandler) {
        return new Handler<Collection<T>>(){

            @Override
            public Stream<ChangedValue> handle(Optional<String> name, Collection<T> ref, AuditAction action) {
                return ref.stream().flatMap(e -> elementHandler.handle(name, e, action));
            }

            @Override
            public Stream<ChangedValue> handle(Optional<String> name, Collection<T> oldT, Collection<T> newT) {
                ImmutableSet oldSet = ImmutableSet.copyOf(oldT);
                ImmutableSet newSet = ImmutableSet.copyOf(newT);
                return Stream.concat(Sets.difference((Set)oldSet, (Set)newSet).stream().flatMap(e -> elementHandler.handle(name, e, AuditAction.REMOVE)), Sets.difference((Set)newSet, (Set)oldSet).stream().flatMap(e -> elementHandler.handle(name, e, AuditAction.ADD)));
            }
        };
    }

    public static <K, V> Handler<Map<K, V>> mapHandler(final Handler<V> valueHandler) {
        return new Handler<Map<K, V>>(){

            @Override
            public Stream<ChangedValue> handle(Optional<String> name, Map<K, V> ref, AuditAction action) {
                return ref.entrySet().stream().flatMap(e -> valueHandler.handle(Optional.of(e.getKey().toString()), e.getValue(), action));
            }

            @Override
            public Stream<ChangedValue> handle(Optional<String> name, Map<K, V> oldT, Map<K, V> newT) {
                MapDifference difference = Maps.difference(oldT, newT);
                return Stream.of(this.handle(name, difference.entriesOnlyOnLeft(), AuditAction.REMOVE), this.handle(name, difference.entriesOnlyOnRight(), AuditAction.ADD), difference.entriesDiffering().entrySet().stream().flatMap(e -> valueHandler.handle(Optional.of(e.getKey().toString()), ((MapDifference.ValueDifference)e.getValue()).leftValue(), ((MapDifference.ValueDifference)e.getValue()).rightValue()))).flatMap(Function.identity());
            }
        };
    }

    public static Handler<String> stringHandler() {
        return STRING_HANDLER;
    }

    public <T> Handler<T> toStringHandler() {
        return HandlerFactory.toStringHandler(obj -> {
            if (obj instanceof Boolean) {
                return this.getBooleanLabel((Boolean)obj);
            }
            return obj.toString();
        });
    }

    private String getBooleanLabel(Boolean bool) {
        return this.getI18NBean().getText((bool != false ? "yes" : "no") + ".name");
    }

    static <T> Handler<T> toStringHandler(final Function<T, String> converter) {
        return new Handler<T>(){

            @Override
            public Stream<ChangedValue> handle(Optional<String> name, T ref, AuditAction action) {
                return HandlerFactory.stringHandler().handle(name, (String)converter.apply(ref), (String)((Object)action));
            }

            @Override
            public Stream<ChangedValue> handle(Optional<String> name, T oldT, T newT) {
                return HandlerFactory.stringHandler().handle(name, (String)converter.apply(oldT), (String)converter.apply(newT));
            }
        };
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
    }
}

