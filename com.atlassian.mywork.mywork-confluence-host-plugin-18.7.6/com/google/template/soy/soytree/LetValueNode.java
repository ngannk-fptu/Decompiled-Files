/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.LetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.defn.LocalVar;
import java.util.List;

public class LetValueNode
extends LetNode
implements SoyNode.ExprHolderNode {
    private final ExprRootNode<?> valueExpr;

    public LetValueNode(int id, boolean isLocalVarNameUniquified, String commandText) {
        super(id, isLocalVarNameUniquified, commandText);
        LetNode.CommandTextParseResult parseResult = this.parseCommandTextHelper(commandText);
        this.valueExpr = parseResult.valueExpr;
        if (this.valueExpr == null) {
            throw SoySyntaxException.createWithoutMetaInfo("A 'let' tag should be self-ending (with a trailing '/') if and only if it also contains a value (invalid tag is {let " + commandText + " /}).");
        }
        if (parseResult.contentKind != null) {
            throw SoySyntaxException.createWithoutMetaInfo("The 'kind' attribute is not allowed on self-ending 'let' tags that  contain a value (invalid tag is {let " + commandText + " /}).");
        }
        this.setVar(new LocalVar(parseResult.localVarName, this, null));
    }

    protected LetValueNode(LetValueNode orig) {
        super(orig);
        this.valueExpr = orig.valueExpr.clone();
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.LET_VALUE_NODE;
    }

    @Override
    public final String getVarName() {
        return this.var.name();
    }

    public ExprRootNode<?> getValueExpr() {
        return this.valueExpr;
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ImmutableList.of((Object)new ExprUnion(this.valueExpr));
    }

    @Override
    public LetValueNode clone() {
        return new LetValueNode(this);
    }
}

