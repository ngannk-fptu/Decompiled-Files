/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  javax.annotation.Nullable
 *  javax.inject.Singleton
 */
package com.google.template.soy.sharedpasses.opti;

import com.google.inject.Inject;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.shared.internal.SharedModule;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.sharedpasses.opti.PreevalVisitorFactory;
import com.google.template.soy.sharedpasses.opti.PrerenderVisitor;
import com.google.template.soy.soytree.TemplateRegistry;
import java.util.Deque;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public class PrerenderVisitorFactory {
    private final Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap;
    private final PreevalVisitorFactory preevalVisitorFactory;

    @Inject
    public PrerenderVisitorFactory(@SharedModule.Shared Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap, PreevalVisitorFactory preevalVisitorFactory) {
        this.soyJavaDirectivesMap = soyJavaDirectivesMap;
        this.preevalVisitorFactory = preevalVisitorFactory;
    }

    public PrerenderVisitor create(Appendable outputBuf, TemplateRegistry templateRegistry, SoyRecord data, @Nullable Deque<Map<String, SoyValue>> env) {
        return new PrerenderVisitor(this.soyJavaDirectivesMap, this.preevalVisitorFactory, outputBuf, templateRegistry, data, env);
    }
}

