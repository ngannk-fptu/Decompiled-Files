/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractParentCommandNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.defn.LocalVar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForeachNode
extends AbstractParentCommandNode<SoyNode>
implements SoyNode.StandaloneNode,
SoyNode.SplitLevelTopNode<SoyNode>,
SoyNode.StatementNode,
SoyNode.ExprHolderNode {
    private static final Pattern COMMAND_TEXT_PATTERN = Pattern.compile("( [$] \\w+ ) \\s+ in \\s+ (\\S .*)", 36);
    private final LocalVar var;
    private final ExprRootNode<?> expr;

    public ForeachNode(int id, String commandText) throws SoySyntaxException {
        super(id, "foreach", commandText);
        Matcher matcher = COMMAND_TEXT_PATTERN.matcher(commandText);
        if (!matcher.matches()) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid 'foreach' command text \"" + commandText + "\".");
        }
        String varName = ExprParseUtils.parseVarNameElseThrowSoySyntaxException(matcher.group(1), "Invalid variable name in 'foreach' command text \"" + commandText + "\".");
        this.expr = ExprParseUtils.parseExprElseThrowSoySyntaxException(matcher.group(2), "Invalid expression in 'foreach' command text \"" + commandText + "\".");
        this.var = new LocalVar(varName, this, null);
    }

    protected ForeachNode(ForeachNode orig) {
        super(orig);
        this.var = new LocalVar(orig.var.name(), this, orig.var.type());
        this.expr = orig.expr.clone();
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.FOREACH_NODE;
    }

    public final LocalVar getVar() {
        return this.var;
    }

    public final String getVarName() {
        return this.var.name();
    }

    public String getExprText() {
        return this.expr.toSourceString();
    }

    public ExprRootNode<?> getExpr() {
        return this.expr;
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ImmutableList.of((Object)new ExprUnion(this.expr));
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public ForeachNode clone() {
        return new ForeachNode(this);
    }
}

