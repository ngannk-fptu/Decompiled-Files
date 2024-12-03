/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Guice
 *  com.google.inject.Injector
 *  com.google.inject.Module
 *  com.google.inject.Provider
 *  com.google.inject.Stage
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.renderer.internal.guice;

import com.atlassian.gadgets.opensocial.internal.guice.OpenSocialModule;
import com.atlassian.gadgets.renderer.internal.guice.AuthModule;
import com.atlassian.gadgets.renderer.internal.guice.SalModule;
import com.atlassian.gadgets.renderer.internal.guice.ShindigModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InjectorProvider
implements Provider<Injector> {
    private final Injector injector;

    @Autowired
    public InjectorProvider(SalModule salModule, ShindigModule shindigModule, AuthModule authModule, OpenSocialModule socialModule) {
        this.injector = Guice.createInjector((Stage)Stage.PRODUCTION, (Module[])new Module[]{salModule, shindigModule, authModule, socialModule});
    }

    public Injector get() {
        return this.injector;
    }
}

