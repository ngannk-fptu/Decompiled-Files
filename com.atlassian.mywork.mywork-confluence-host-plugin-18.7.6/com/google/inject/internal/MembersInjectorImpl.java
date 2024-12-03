/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.ContextualCallable;
import com.google.inject.internal.EncounterImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.MethodAspect;
import com.google.inject.internal.SingleMemberInjector;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.InjectionPoint;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class MembersInjectorImpl<T>
implements MembersInjector<T> {
    private final TypeLiteral<T> typeLiteral;
    private final InjectorImpl injector;
    private final $ImmutableList<SingleMemberInjector> memberInjectors;
    private final $ImmutableList<MembersInjector<? super T>> userMembersInjectors;
    private final $ImmutableList<InjectionListener<? super T>> injectionListeners;
    private final $ImmutableList<MethodAspect> addedAspects;

    MembersInjectorImpl(InjectorImpl injector, TypeLiteral<T> typeLiteral, EncounterImpl<T> encounter, $ImmutableList<SingleMemberInjector> memberInjectors) {
        this.injector = injector;
        this.typeLiteral = typeLiteral;
        this.memberInjectors = memberInjectors;
        this.userMembersInjectors = encounter.getMembersInjectors();
        this.injectionListeners = encounter.getInjectionListeners();
        this.addedAspects = encounter.getAspects();
    }

    public $ImmutableList<SingleMemberInjector> getMemberInjectors() {
        return this.memberInjectors;
    }

    @Override
    public void injectMembers(T instance) {
        Errors errors = new Errors(this.typeLiteral);
        try {
            this.injectAndNotify(instance, errors, false);
        }
        catch (ErrorsException e) {
            errors.merge(e.getErrors());
        }
        errors.throwProvisionExceptionIfErrorsExist();
    }

    void injectAndNotify(final T instance, final Errors errors, final boolean toolableOnly) throws ErrorsException {
        if (instance == null) {
            return;
        }
        this.injector.callInContext(new ContextualCallable<Void>(){

            @Override
            public Void call(InternalContext context) throws ErrorsException {
                MembersInjectorImpl.this.injectMembers(instance, errors, context, toolableOnly);
                return null;
            }
        });
        if (!toolableOnly) {
            this.notifyListeners(instance, errors);
        }
    }

    void notifyListeners(T instance, Errors errors) throws ErrorsException {
        int numErrorsBefore = errors.size();
        for (InjectionListener injectionListener : this.injectionListeners) {
            try {
                injectionListener.afterInjection(instance);
            }
            catch (RuntimeException e) {
                errors.errorNotifyingInjectionListener(injectionListener, this.typeLiteral, e);
            }
        }
        errors.throwIfNewErrors(numErrorsBefore);
    }

    void injectMembers(T t, Errors errors, InternalContext context, boolean toolableOnly) {
        int i;
        int size = this.memberInjectors.size();
        for (i = 0; i < size; ++i) {
            SingleMemberInjector injector = (SingleMemberInjector)this.memberInjectors.get(i);
            if (toolableOnly && !injector.getInjectionPoint().isToolable()) continue;
            injector.inject(errors, context, t);
        }
        if (!toolableOnly) {
            size = this.userMembersInjectors.size();
            for (i = 0; i < size; ++i) {
                MembersInjector userMembersInjector = (MembersInjector)this.userMembersInjectors.get(i);
                try {
                    userMembersInjector.injectMembers(t);
                    continue;
                }
                catch (RuntimeException e) {
                    errors.errorInUserInjector(userMembersInjector, this.typeLiteral, e);
                }
            }
        }
    }

    public String toString() {
        return "MembersInjector<" + this.typeLiteral + ">";
    }

    public $ImmutableSet<InjectionPoint> getInjectionPoints() {
        $ImmutableSet.Builder<InjectionPoint> builder = $ImmutableSet.builder();
        for (SingleMemberInjector memberInjector : this.memberInjectors) {
            builder.add(memberInjector.getInjectionPoint());
        }
        return builder.build();
    }

    public $ImmutableList<MethodAspect> getAddedAspects() {
        return this.addedAspects;
    }
}

