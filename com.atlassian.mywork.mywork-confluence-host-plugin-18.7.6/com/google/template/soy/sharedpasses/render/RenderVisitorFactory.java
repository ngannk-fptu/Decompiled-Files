/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses.render;

import com.google.inject.Inject;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import com.google.template.soy.shared.internal.SharedModule;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.sharedpasses.render.EvalVisitor;
import com.google.template.soy.sharedpasses.render.RenderVisitor;
import com.google.template.soy.soytree.TemplateRegistry;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
public class RenderVisitorFactory {
    private final Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap;
    private final EvalVisitor.EvalVisitorFactory evalVisitorFactory;

    @Inject
    public RenderVisitorFactory(@SharedModule.Shared Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap, EvalVisitor.EvalVisitorFactory evalVisitorFactory) {
        this.soyJavaDirectivesMap = soyJavaDirectivesMap;
        this.evalVisitorFactory = evalVisitorFactory;
    }

    public RenderVisitor create(Appendable outputBuf, TemplateRegistry templateRegistry, SoyRecord data, @Nullable SoyRecord ijData, @Nullable Deque<Map<String, SoyValue>> env, @Nullable Set<String> activeDelPackageNames, @Nullable SoyMsgBundle msgBundle, @Nullable SoyIdRenamingMap xidRenamingMap, @Nullable SoyCssRenamingMap cssRenamingMap) {
        return new RenderVisitor(this.soyJavaDirectivesMap, this.evalVisitorFactory, outputBuf, templateRegistry, data, ijData, env, activeDelPackageNames, msgBundle, xidRenamingMap, cssRenamingMap);
    }
}

