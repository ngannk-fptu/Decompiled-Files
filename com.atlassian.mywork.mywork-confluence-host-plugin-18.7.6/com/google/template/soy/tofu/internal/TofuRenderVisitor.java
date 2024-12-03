/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.tofu.internal;

import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.sharedpasses.render.RenderVisitor;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.tofu.internal.TofuEvalVisitorFactory;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

class TofuRenderVisitor
extends RenderVisitor {
    protected TofuRenderVisitor(Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap, TofuEvalVisitorFactory tofuEvalVisitorFactory, Appendable outputBuf, @Nullable TemplateRegistry templateRegistry, SoyRecord data, @Nullable SoyRecord ijData, @Nullable Deque<Map<String, SoyValue>> env, @Nullable Set<String> activeDelPackageNames, @Nullable SoyMsgBundle msgBundle, @Nullable SoyIdRenamingMap xidRenamingMap, @Nullable SoyCssRenamingMap cssRenamingMap) {
        super(soyJavaDirectivesMap, tofuEvalVisitorFactory, outputBuf, templateRegistry, data, ijData, env, activeDelPackageNames, msgBundle, xidRenamingMap, cssRenamingMap);
    }

    @Override
    protected TofuRenderVisitor createHelperInstance(Appendable outputBuf, SoyRecord data) {
        return new TofuRenderVisitor((Map<String, SoyJavaPrintDirective>)this.soyJavaDirectivesMap, (TofuEvalVisitorFactory)this.evalVisitorFactory, outputBuf, this.templateRegistry, data, this.ijData, null, (Set<String>)this.activeDelPackageNames, this.msgBundle, this.xidRenamingMap, this.cssRenamingMap);
    }
}

