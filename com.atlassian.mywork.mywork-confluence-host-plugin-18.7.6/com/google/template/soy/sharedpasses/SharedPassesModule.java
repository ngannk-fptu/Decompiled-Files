/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.sharedpasses;

import com.google.inject.AbstractModule;
import com.google.template.soy.shared.internal.SharedModule;
import com.google.template.soy.sharedpasses.opti.OptiModule;
import com.google.template.soy.sharedpasses.render.EvalVisitor;
import com.google.template.soy.sharedpasses.render.EvalVisitorFactoryImpl;

public class SharedPassesModule
extends AbstractModule {
    @Override
    protected void configure() {
        this.install(new SharedModule());
        this.install(new OptiModule());
        this.bind(EvalVisitor.EvalVisitorFactory.class).to(EvalVisitorFactoryImpl.class);
    }

    public boolean equals(Object other) {
        return other != null && this.getClass().equals(other.getClass());
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}

