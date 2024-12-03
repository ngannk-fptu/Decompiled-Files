/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.ConfigurationException;
import com.google.inject.Stage;
import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.ContextualCallable;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.Initializer;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.InternalContext;
import com.google.inject.internal.SingleMemberInjector;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Lists;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.StaticInjectionRequest;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InjectionRequestProcessor
extends AbstractProcessor {
    private final List<StaticInjection> staticInjections = $Lists.newArrayList();
    private final Initializer initializer;

    InjectionRequestProcessor(Errors errors, Initializer initializer) {
        super(errors);
        this.initializer = initializer;
    }

    @Override
    public Boolean visit(StaticInjectionRequest request) {
        this.staticInjections.add(new StaticInjection(this.injector, request));
        return true;
    }

    @Override
    public Boolean visit(InjectionRequest<?> request) {
        Set injectionPoints;
        try {
            injectionPoints = request.getInjectionPoints();
        }
        catch (ConfigurationException e) {
            this.errors.merge(e.getErrorMessages());
            injectionPoints = (Set)e.getPartialValue();
        }
        this.initializer.requestInjection(this.injector, request.getInstance(), request.getSource(), injectionPoints);
        return true;
    }

    void validate() {
        for (StaticInjection staticInjection : this.staticInjections) {
            staticInjection.validate();
        }
    }

    void injectMembers() {
        for (StaticInjection staticInjection : this.staticInjections) {
            staticInjection.injectMembers();
        }
    }

    private class StaticInjection {
        final InjectorImpl injector;
        final Object source;
        final StaticInjectionRequest request;
        $ImmutableList<SingleMemberInjector> memberInjectors;

        public StaticInjection(InjectorImpl injector, StaticInjectionRequest request) {
            this.injector = injector;
            this.source = request.getSource();
            this.request = request;
        }

        void validate() {
            Set injectionPoints;
            Errors errorsForMember = InjectionRequestProcessor.this.errors.withSource(this.source);
            try {
                injectionPoints = this.request.getInjectionPoints();
            }
            catch (ConfigurationException e) {
                InjectionRequestProcessor.this.errors.merge(e.getErrorMessages());
                injectionPoints = (Set)e.getPartialValue();
            }
            this.memberInjectors = this.injector.membersInjectorStore.getInjectors(injectionPoints, errorsForMember);
        }

        void injectMembers() {
            try {
                this.injector.callInContext(new ContextualCallable<Void>(){

                    @Override
                    public Void call(InternalContext context) {
                        for (SingleMemberInjector memberInjector : StaticInjection.this.memberInjectors) {
                            if (StaticInjection.this.injector.options.stage == Stage.TOOL && !memberInjector.getInjectionPoint().isToolable()) continue;
                            memberInjector.inject(InjectionRequestProcessor.this.errors, context, null);
                        }
                        return null;
                    }
                });
            }
            catch (ErrorsException e) {
                throw new AssertionError();
            }
        }
    }
}

