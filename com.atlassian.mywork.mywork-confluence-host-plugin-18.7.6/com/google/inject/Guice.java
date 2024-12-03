/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.internal.InternalInjectorCreator;
import java.util.Arrays;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Guice {
    private Guice() {
    }

    public static Injector createInjector(Module ... modules) {
        return Guice.createInjector(Arrays.asList(modules));
    }

    public static Injector createInjector(Iterable<? extends Module> modules) {
        return Guice.createInjector(Stage.DEVELOPMENT, modules);
    }

    public static Injector createInjector(Stage stage, Module ... modules) {
        return Guice.createInjector(stage, Arrays.asList(modules));
    }

    public static Injector createInjector(Stage stage, Iterable<? extends Module> modules) {
        return new InternalInjectorCreator().stage(stage).addModules(modules).build();
    }
}

