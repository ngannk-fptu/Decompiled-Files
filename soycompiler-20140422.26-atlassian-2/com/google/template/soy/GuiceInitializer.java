/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Guice
 *  com.google.inject.Inject
 *  com.google.inject.Module
 */
package com.google.template.soy;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.SoyModule;
import java.util.logging.Level;
import java.util.logging.Logger;

class GuiceInitializer {
    private static final Logger LOGGER = Logger.getLogger(GuiceInitializer.class.getName());
    private static int initializationCount;
    @Inject
    private static SoyFileSet.SoyFileSetFactory soyFileSetFactory;

    GuiceInitializer() {
    }

    @Inject
    static synchronized void markInitialized() {
        ++initializationCount;
    }

    private static synchronized void initializeIfNecessary() {
        if (initializationCount == 0) {
            Guice.createInjector((Module[])new Module[]{new SoyModule()});
            if (initializationCount == 0) {
                throw new AssertionError((Object)"Injector creation failed to do static injection.");
            }
        }
    }

    static synchronized SoyFileSet.SoyFileSetFactory getHackySoyFileSetFactory() {
        GuiceInitializer.initializeIfNecessary();
        if (initializationCount > 1) {
            String message = "The SoyFileSet.Builder constructor is trying to guess which Injector to use, but multiple Injectors have already installed a new SoyModule(). We will essentially configure Soy at random, so you get an inconsistent set of plugins or Soy types. To fix, inject SoyFileSet.Builder (with SoyModule installed) instead of new'ing it.";
            LOGGER.log(Level.SEVERE, message, new IllegalStateException(message));
        } else {
            String message = "Falling back to statically-injected SoyFileSetFactory; unpredictable behavior is likely. Instead of constructing a SoyFileSet.Builder directly, either inject it using Guice (with SoyModule installed), or call the static SoyFileSet.builder() method.";
            LOGGER.log(Level.WARNING, message);
        }
        return soyFileSetFactory;
    }

    static {
        soyFileSetFactory = null;
    }
}

