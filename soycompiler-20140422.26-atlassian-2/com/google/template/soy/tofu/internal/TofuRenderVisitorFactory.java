/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 *  javax.annotation.Nullable
 *  javax.inject.Singleton
 */
package com.google.template.soy.tofu.internal;

import com.google.inject.Inject;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.tofu.internal.TofuEvalVisitorFactory;
import com.google.template.soy.tofu.internal.TofuModule;
import com.google.template.soy.tofu.internal.TofuRenderVisitor;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
class TofuRenderVisitorFactory {
    private final Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap;
    private final TofuEvalVisitorFactory tofuEvalVisitorFactory;

    @Inject
    public TofuRenderVisitorFactory(@TofuModule.Tofu Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap, TofuEvalVisitorFactory tofuEvalVisitorFactory) {
        this.soyJavaDirectivesMap = soyJavaDirectivesMap;
        this.tofuEvalVisitorFactory = tofuEvalVisitorFactory;
    }

    public TofuRenderVisitor create(Appendable outputBuf, TemplateRegistry templateRegistry, SoyRecord data, @Nullable SoyRecord ijData, @Nullable Deque<Map<String, SoyValue>> env, @Nullable Set<String> activeDelPackageNames, @Nullable SoyMsgBundle msgBundle, @Nullable SoyIdRenamingMap xidRenamingMap, @Nullable SoyCssRenamingMap cssRenamingMap) {
        return new TofuRenderVisitor(this.soyJavaDirectivesMap, this.tofuEvalVisitorFactory, outputBuf, templateRegistry, data, ijData, env, activeDelPackageNames, msgBundle, xidRenamingMap, cssRenamingMap);
    }
}

