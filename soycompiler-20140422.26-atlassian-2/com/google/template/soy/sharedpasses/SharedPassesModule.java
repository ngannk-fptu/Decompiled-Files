/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 *  com.google.inject.Module
 */
package com.google.template.soy.sharedpasses;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.template.soy.shared.internal.SharedModule;
import com.google.template.soy.sharedpasses.opti.OptiModule;
import com.google.template.soy.sharedpasses.render.EvalVisitor;
import com.google.template.soy.sharedpasses.render.EvalVisitorFactoryImpl;

public class SharedPassesModule
extends AbstractModule {
    protected void configure() {
        this.install((Module)new SharedModule());
        this.install((Module)new OptiModule());
        this.bind(EvalVisitor.EvalVisitorFactory.class).to(EvalVisitorFactoryImpl.class);
    }

    public boolean equals(Object other) {
        return other != null && ((Object)((Object)this)).getClass().equals(other.getClass());
    }

    public int hashCode() {
        return ((Object)((Object)this)).getClass().hashCode();
    }
}

