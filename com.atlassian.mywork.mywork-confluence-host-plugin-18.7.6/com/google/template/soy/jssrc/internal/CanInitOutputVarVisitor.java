/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.jssrc.internal;

import com.google.inject.Inject;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.internal.IsComputableAsJsExprsVisitor;
import com.google.template.soy.soytree.AbstractReturningSoyNodeVisitor;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.SoyNode;

class CanInitOutputVarVisitor
extends AbstractReturningSoyNodeVisitor<Boolean> {
    private final SoyJsSrcOptions jsSrcOptions;
    private final IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor;

    @Inject
    CanInitOutputVarVisitor(SoyJsSrcOptions jsSrcOptions, IsComputableAsJsExprsVisitor isComputableAsJsExprsVisitor) {
        this.jsSrcOptions = jsSrcOptions;
        this.isComputableAsJsExprsVisitor = isComputableAsJsExprsVisitor;
    }

    @Override
    protected Boolean visitCallNode(CallNode node) {
        return this.jsSrcOptions.getCodeStyle() == SoyJsSrcOptions.CodeStyle.CONCAT;
    }

    @Override
    protected Boolean visitSoyNode(SoyNode node) {
        return (Boolean)this.isComputableAsJsExprsVisitor.exec(node);
    }
}

