/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.inject.internal;

import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Errors;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.LookupProcessor;
import com.google.inject.internal.Lookups;
import com.google.inject.spi.Element;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.ProviderLookup;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DeferredLookups
implements Lookups {
    private final InjectorImpl injector;
    private final List<Element> lookups = Lists.newArrayList();

    DeferredLookups(InjectorImpl injector) {
        this.injector = injector;
    }

    void initialize(Errors errors) {
        this.injector.lookups = this.injector;
        new LookupProcessor(errors).process(this.injector, this.lookups);
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        ProviderLookup<T> lookup = new ProviderLookup<T>(key, key);
        this.lookups.add(lookup);
        return lookup.getProvider();
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> type) {
        MembersInjectorLookup<T> lookup = new MembersInjectorLookup<T>(type, type);
        this.lookups.add(lookup);
        return lookup.getMembersInjector();
    }
}

