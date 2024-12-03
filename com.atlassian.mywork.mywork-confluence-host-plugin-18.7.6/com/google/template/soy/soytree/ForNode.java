/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractBlockCommandNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.defn.LocalVar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForNode
extends AbstractBlockCommandNode
implements SoyNode.StandaloneNode,
SoyNode.StatementNode,
SoyNode.ConditionalBlockNode,
SoyNode.LoopNode,
SoyNode.ExprHolderNode,
SoyNode.LocalVarBlockNode {
    private static final Pattern COMMAND_TEXT_PATTERN = Pattern.compile("( [$] \\w+ ) \\s+ in \\s+ range[(] \\s* (.*) \\s* [)]", 36);
    private final LocalVar var;
    private final ImmutableList<String> rangeArgTexts;
    private final ImmutableList<ExprRootNode<?>> rangeArgs;

    public ForNode(int id, String commandText) throws SoySyntaxException {
        super(id, "for", commandText);
        Matcher matcher = COMMAND_TEXT_PATTERN.matcher(commandText);
        if (!matcher.matches()) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid 'for' command text \"" + commandText + "\".");
        }
        String varName = ExprParseUtils.parseVarNameElseThrowSoySyntaxException(matcher.group(1), "Invalid variable name in 'for' command text \"" + commandText + "\".");
        List<ExprRootNode<?>> tempRangeArgs = ExprParseUtils.parseExprListElseThrowSoySyntaxException(matcher.group(2), "Invalid range specification in 'for' command text \"" + commandText + "\".");
        if (tempRangeArgs.size() > 3) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid range specification in 'for' command text \"" + commandText + "\".");
        }
        this.rangeArgs = ImmutableList.copyOf(tempRangeArgs);
        ArrayList tempRangeArgTexts = Lists.newArrayList();
        for (ExprNode rangeArg : this.rangeArgs) {
            tempRangeArgTexts.add(rangeArg.toSourceString());
        }
        this.rangeArgTexts = ImmutableList.copyOf((Collection)tempRangeArgTexts);
        this.var = new LocalVar(varName, this, null);
    }

    protected ForNode(ForNode orig) {
        super(orig);
        this.var = new LocalVar(orig.var.name(), this, orig.var.type());
        this.rangeArgTexts = orig.rangeArgTexts;
        ArrayList tempRangeArgs = Lists.newArrayListWithCapacity((int)orig.rangeArgs.size());
        for (ExprRootNode origRangeArg : orig.rangeArgs) {
            tempRangeArgs.add(origRangeArg.clone());
        }
        this.rangeArgs = ImmutableList.copyOf((Collection)tempRangeArgs);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.FOR_NODE;
    }

    public final LocalVar getVar() {
        return this.var;
    }

    @Override
    public final String getVarName() {
        return this.var.name();
    }

    public List<String> getRangeArgTexts() {
        return this.rangeArgTexts;
    }

    public List<ExprRootNode<?>> getRangeArgs() {
        return this.rangeArgs;
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ExprUnion.createList(this.rangeArgs);
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public ForNode clone() {
        return new ForNode(this);
    }
}

