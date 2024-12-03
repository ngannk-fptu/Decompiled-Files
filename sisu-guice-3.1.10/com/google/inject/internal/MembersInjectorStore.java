/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.ConfigurationException;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.EncounterImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.FailableCache;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.MembersInjectorImpl;
import com.google.inject.internal.SingleFieldInjector;
import com.google.inject.internal.SingleMemberInjector;
import com.google.inject.internal.SingleMethodInjector;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.TypeListener;
import com.google.inject.spi.TypeListenerBinding;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class MembersInjectorStore {
    private final InjectorImpl injector;
    private final ImmutableList<TypeListenerBinding> typeListenerBindings;
    private final FailableCache<TypeLiteral<?>, MembersInjectorImpl<?>> cache = new FailableCache<TypeLiteral<?>, MembersInjectorImpl<?>>(){

        @Override
        protected MembersInjectorImpl<?> create(TypeLiteral<?> type, Errors errors) throws ErrorsException {
            return MembersInjectorStore.this.createWithListeners(type, errors);
        }
    };

    MembersInjectorStore(InjectorImpl injector, List<TypeListenerBinding> typeListenerBindings) {
        this.injector = injector;
        this.typeListenerBindings = ImmutableList.copyOf(typeListenerBindings);
    }

    public boolean hasTypeListeners() {
        return !this.typeListenerBindings.isEmpty();
    }

    public <T> MembersInjectorImpl<T> get(TypeLiteral<T> key, Errors errors) throws ErrorsException {
        return this.cache.get(key, errors);
    }

    boolean remove(TypeLiteral<?> type) {
        return this.cache.remove(type);
    }

    private <T> MembersInjectorImpl<T> createWithListeners(TypeLiteral<T> type, Errors errors) throws ErrorsException {
        Set injectionPoints;
        int numErrorsBefore = errors.size();
        try {
            injectionPoints = InjectionPoint.forInstanceMethodsAndFields(type);
        }
        catch (ConfigurationException e) {
            errors.merge(e.getErrorMessages());
            injectionPoints = (Set)e.getPartialValue();
        }
        ImmutableList<SingleMemberInjector> injectors = this.getInjectors(injectionPoints, errors);
        errors.throwIfNewErrors(numErrorsBefore);
        EncounterImpl encounter = new EncounterImpl(errors, this.injector.lookups);
        HashSet alreadySeenListeners = Sets.newHashSet();
        for (TypeListenerBinding binding : this.typeListenerBindings) {
            TypeListener typeListener = binding.getListener();
            if (alreadySeenListeners.contains(typeListener) || !binding.getTypeMatcher().matches(type)) continue;
            alreadySeenListeners.add(typeListener);
            try {
                typeListener.hear(type, encounter);
            }
            catch (RuntimeException e) {
                errors.errorNotifyingTypeListener(binding, type, e);
            }
        }
        encounter.invalidate();
        errors.throwIfNewErrors(numErrorsBefore);
        return new MembersInjectorImpl<T>(this.injector, type, encounter, injectors);
    }

    ImmutableList<SingleMemberInjector> getInjectors(Set<InjectionPoint> injectionPoints, Errors errors) {
        ArrayList injectors = Lists.newArrayList();
        for (InjectionPoint injectionPoint : injectionPoints) {
            try {
                Errors errorsForMember = injectionPoint.isOptional() ? new Errors(injectionPoint) : errors.withSource(injectionPoint);
                SingleMemberInjector injector = injectionPoint.getMember() instanceof Field ? new SingleFieldInjector(this.injector, injectionPoint, errorsForMember) : new SingleMethodInjector(this.injector, injectionPoint, errorsForMember);
                injectors.add(injector);
            }
            catch (ErrorsException ignoredForNow) {}
        }
        return ImmutableList.copyOf((Collection)injectors);
    }
}

