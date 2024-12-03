/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  org.aopalliance.intercept.MethodInterceptor
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Errors;
import com.google.inject.internal.Lookups;
import com.google.inject.internal.MethodAspect;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.Message;
import com.google.inject.spi.TypeEncounter;
import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class EncounterImpl<T>
implements TypeEncounter<T> {
    private final Errors errors;
    private final Lookups lookups;
    private List<MembersInjector<? super T>> membersInjectors;
    private List<InjectionListener<? super T>> injectionListeners;
    private List<MethodAspect> aspects;
    private boolean valid = true;

    EncounterImpl(Errors errors, Lookups lookups) {
        this.errors = errors;
        this.lookups = lookups;
    }

    void invalidate() {
        this.valid = false;
    }

    ImmutableList<MethodAspect> getAspects() {
        return this.aspects == null ? ImmutableList.of() : ImmutableList.copyOf(this.aspects);
    }

    @Override
    public void bindInterceptor(Matcher<? super Method> methodMatcher, MethodInterceptor ... interceptors) {
        Preconditions.checkState((boolean)this.valid, (Object)"Encounters may not be used after hear() returns.");
        if (this.aspects == null) {
            this.aspects = Lists.newArrayList();
        }
        this.aspects.add(new MethodAspect(Matchers.any(), methodMatcher, interceptors));
    }

    ImmutableSet<MembersInjector<? super T>> getMembersInjectors() {
        return this.membersInjectors == null ? ImmutableSet.of() : ImmutableSet.copyOf(this.membersInjectors);
    }

    ImmutableSet<InjectionListener<? super T>> getInjectionListeners() {
        return this.injectionListeners == null ? ImmutableSet.of() : ImmutableSet.copyOf(this.injectionListeners);
    }

    @Override
    public void register(MembersInjector<? super T> membersInjector) {
        Preconditions.checkState((boolean)this.valid, (Object)"Encounters may not be used after hear() returns.");
        if (this.membersInjectors == null) {
            this.membersInjectors = Lists.newArrayList();
        }
        this.membersInjectors.add(membersInjector);
    }

    @Override
    public void register(InjectionListener<? super T> injectionListener) {
        Preconditions.checkState((boolean)this.valid, (Object)"Encounters may not be used after hear() returns.");
        if (this.injectionListeners == null) {
            this.injectionListeners = Lists.newArrayList();
        }
        this.injectionListeners.add(injectionListener);
    }

    @Override
    public void addError(String message, Object ... arguments) {
        Preconditions.checkState((boolean)this.valid, (Object)"Encounters may not be used after hear() returns.");
        this.errors.addMessage(message, arguments);
    }

    @Override
    public void addError(Throwable t) {
        Preconditions.checkState((boolean)this.valid, (Object)"Encounters may not be used after hear() returns.");
        this.errors.errorInUserCode(t, "An exception was caught and reported. Message: %s", t.getMessage());
    }

    @Override
    public void addError(Message message) {
        Preconditions.checkState((boolean)this.valid, (Object)"Encounters may not be used after hear() returns.");
        this.errors.addMessage(message);
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        Preconditions.checkState((boolean)this.valid, (Object)"Encounters may not be used after hear() returns.");
        return this.lookups.getProvider(key);
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        return this.getProvider(Key.get(type));
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
        Preconditions.checkState((boolean)this.valid, (Object)"Encounters may not be used after hear() returns.");
        return this.lookups.getMembersInjector(typeLiteral);
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
        return this.getMembersInjector(TypeLiteral.get(type));
    }
}

