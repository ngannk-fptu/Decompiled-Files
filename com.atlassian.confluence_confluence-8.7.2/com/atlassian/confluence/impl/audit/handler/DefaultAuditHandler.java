/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.ChangedValue
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.audit.handler;

import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import com.atlassian.confluence.impl.audit.handler.AuditHandler;
import com.atlassian.confluence.impl.audit.handler.Handler;
import com.atlassian.confluence.impl.audit.handler.HandlerFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

class DefaultAuditHandler<T>
implements AuditHandler<T> {
    private final Class<T> handledClass;
    private Handler<Object> defaultHandler;
    private final Map<String, Function<? super T, ?>> getters;
    private final Map<String, Handler<Object>> handlers;
    private final Set<String> excludedMethodNames;
    private final Optional<Function<? super T, String>> referenceNameGetter;
    private final AuditHelper auditHelper;

    private DefaultAuditHandler(Class<T> handledClass, Handler<Object> defaultHandler, Map<String, Function<? super T, ?>> getters, Map<String, Handler<Object>> handlers, Set<String> excludedMethodNames, Optional<Function<? super T, String>> referenceNameGetter, AuditHelper helper) {
        this.handledClass = handledClass;
        this.defaultHandler = defaultHandler;
        this.getters = getters;
        this.handlers = handlers;
        this.excludedMethodNames = excludedMethodNames;
        this.referenceNameGetter = referenceNameGetter;
        this.auditHelper = helper;
    }

    private String getI18NString() {
        return this.auditHelper.translate(AuditHelper.buildTextKey(this.getHandledClass().getName()));
    }

    private String getI18NString(String text) {
        return this.auditHelper.translate(AuditHelper.buildTextKey(this.getHandledClass().getName() + "." + text));
    }

    @Override
    public Stream<ChangedValue> handle(Optional<String> name, T ref, AuditAction action) {
        return this.getters.entrySet().stream().flatMap(e -> this.getOneSidedChangedValues(ref, action, (Map.Entry<String, Function<? super T, ?>>)e));
    }

    private Handler<Object> getHandler(String methodName) {
        return this.handlers.getOrDefault(methodName, this.defaultHandler);
    }

    private Stream<ChangedValue> getOneSidedChangedValues(T ref, AuditAction action, Map.Entry<String, Function<? super T, ?>> entry) {
        String value = StringUtils.containsIgnoreCase((CharSequence)entry.getKey(), (CharSequence)"password") ? "********" : entry.getValue().apply(ref);
        if (value == null) {
            return Stream.empty();
        }
        Optional<String> name = Optional.of(this.getI18NString(entry.getKey()));
        return this.getHandler(entry.getKey()).handle(name, (Object)value, (Object)action);
    }

    @Override
    public Stream<ChangedValue> handle(Optional<String> name, T oltT, T newT) {
        return this.getters.entrySet().stream().flatMap(e -> this.getChangedValues(oltT, newT, (Map.Entry<String, Function<? super T, ?>>)e));
    }

    private Stream<ChangedValue> getChangedValues(T oldRef, T newRef, Map.Entry<String, Function<? super T, ?>> entry) {
        Object oldValue = entry.getValue().apply(oldRef);
        Object newValue = entry.getValue().apply(newRef);
        Optional<String> name = Optional.of(this.getI18NString(entry.getKey()));
        Handler<Object> handler = this.getHandler(entry.getKey());
        if (Objects.equals(oldValue, newValue)) {
            return Stream.empty();
        }
        if (oldValue == null) {
            return handler.handle(name, newValue, (Object)AuditAction.ADD);
        }
        if (newValue == null) {
            return handler.handle(name, oldValue, (Object)AuditAction.REMOVE);
        }
        return handler.handle(name, oldValue, newValue);
    }

    @Override
    public Handler<T> reference() {
        return new Handler<T>(){

            @Override
            public Stream<ChangedValue> handle(Optional<String> name, T ref, AuditAction action) {
                return DefaultAuditHandler.this.referenceNameGetter.map(nameGetter -> HandlerFactory.stringHandler().handle(Optional.of(DefaultAuditHandler.this.getI18NString()), (String)nameGetter.apply(ref), (String)((Object)action))).orElseThrow(UnsupportedOperationException::new);
            }

            @Override
            public Stream<ChangedValue> handle(Optional<String> name, T oldT, T newT) {
                if (Objects.equals(oldT, newT)) {
                    return Stream.empty();
                }
                return DefaultAuditHandler.this.referenceNameGetter.map(nameGetter -> HandlerFactory.stringHandler().handle(Optional.of(DefaultAuditHandler.this.getI18NString()), (String)nameGetter.apply(oldT), (String)nameGetter.apply(newT))).orElseThrow(UnsupportedOperationException::new);
            }
        };
    }

    @Override
    public Set<String> getHandledMethodNames() {
        return ImmutableSet.builder().addAll(this.getters.keySet()).build();
    }

    @Override
    public Set<String> getExcludedMethodNames() {
        return this.excludedMethodNames;
    }

    @Override
    public Class<T> getHandledClass() {
        return this.handledClass;
    }

    static <T> Builder<T> builder(Class<T> handledClass, Handler<Object> defaultHandler) {
        return new Builder<T>(handledClass, defaultHandler);
    }

    static class Builder<T> {
        private final Class<T> handledClass;
        private Handler<Object> defaultHandler;
        private ImmutableMap.Builder<String, Function<? super T, ?>> getters = ImmutableMap.builder();
        private ImmutableMap.Builder<String, Handler<Object>> handlers = ImmutableMap.builder();
        private ImmutableSet.Builder<String> excludedMethodNames = ImmutableSet.builder();
        private Optional<Function<? super T, String>> referenceNameGetter = Optional.empty();
        private AuditHelper auditHelper;

        Builder(Class<T> handledClass, Handler<Object> defaultHandler) {
            this.handledClass = handledClass;
            this.defaultHandler = defaultHandler;
        }

        public Builder<T> addGetter(String methodName, Function<? super T, ?> getter) {
            this.getters.put((Object)methodName, getter);
            return this;
        }

        public <V> Builder<T> addGetter(String methodName, Function<? super T, V> getter, Handler<V> handler) {
            this.getters.put((Object)methodName, getter);
            this.handlers.put((Object)methodName, handler);
            return this;
        }

        public Builder<T> excludedMethodName(String methodName) {
            this.excludedMethodNames.add((Object)methodName);
            return this;
        }

        public Builder<T> excludedMethodNames(String ... methodNames) {
            Arrays.stream(methodNames).forEach(arg_0 -> this.excludedMethodNames.add(arg_0));
            return this;
        }

        public Builder<T> referenceNameGetter(Function<? super T, String> referenceNameGetter) {
            this.referenceNameGetter = Optional.of(referenceNameGetter);
            return this;
        }

        public Builder<T> auditHelper(AuditHelper auditHelper) {
            this.auditHelper = auditHelper;
            return this;
        }

        public DefaultAuditHandler<T> build() {
            return new DefaultAuditHandler<T>(this.handledClass, this.defaultHandler, this.getters.build(), (Map<String, Handler<Object>>)this.handlers.build(), (Set<String>)this.excludedMethodNames.build(), this.referenceNameGetter, this.auditHelper);
        }
    }
}

