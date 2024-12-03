/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.sharedpasses;

import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.sharedpasses.FindIjParamsInExprHelperVisitor;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoytreeUtils;
import java.util.Set;

public class IsUsingIjDataVisitor {
    public boolean exec(SoyFileSetNode soyTree) {
        FindIjParamsInExprHelperVisitor helperVisitor = new FindIjParamsInExprHelperVisitor();
        SoytreeUtils.execOnAllV2ExprsShortcircuitably(soyTree, helperVisitor, new SoytreeUtils.Shortcircuiter<Set<String>>(){

            @Override
            public boolean shouldShortcircuit(AbstractExprNodeVisitor<Set<String>> exprNodeVisitor) {
                return ((FindIjParamsInExprHelperVisitor)exprNodeVisitor).getResult().size() > 0;
            }
        });
        return helperVisitor.getResult().size() > 0;
    }
}

