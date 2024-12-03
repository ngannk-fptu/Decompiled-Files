/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 */
package com.google.template.soy.sharedpasses.opti;

import com.google.inject.AbstractModule;
import com.google.template.soy.sharedpasses.opti.PreevalVisitorFactory;
import com.google.template.soy.sharedpasses.opti.PrerenderVisitorFactory;
import com.google.template.soy.sharedpasses.opti.SimplifyExprVisitor;
import com.google.template.soy.sharedpasses.opti.SimplifyVisitor;

public class OptiModule
extends AbstractModule {
    protected void configure() {
        this.bind(PreevalVisitorFactory.class);
        this.bind(PrerenderVisitorFactory.class);
        this.bind(SimplifyExprVisitor.class);
        this.bind(SimplifyVisitor.class);
    }
}

