/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.sharedpasses.opti;

import com.google.inject.AbstractModule;
import com.google.template.soy.sharedpasses.opti.PreevalVisitorFactory;
import com.google.template.soy.sharedpasses.opti.PrerenderVisitorFactory;
import com.google.template.soy.sharedpasses.opti.SimplifyExprVisitor;
import com.google.template.soy.sharedpasses.opti.SimplifyVisitor;

public class OptiModule
extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(PreevalVisitorFactory.class);
        this.bind(PrerenderVisitorFactory.class);
        this.bind(SimplifyExprVisitor.class);
        this.bind(SimplifyVisitor.class);
    }
}

