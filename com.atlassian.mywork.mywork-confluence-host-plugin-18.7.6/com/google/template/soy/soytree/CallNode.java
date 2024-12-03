/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.soytree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.soytree.AbstractParentCommandNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

public abstract class CallNode
extends AbstractParentCommandNode<CallParamNode>
implements SoyNode.StandaloneNode,
SoyNode.SplitLevelTopNode<CallParamNode>,
SoyNode.StatementNode,
SoyNode.ExprHolderNode,
SoyNode.MsgPlaceholderInitialNode {
    public static final String FALLBACK_BASE_PLACEHOLDER_NAME = "XXX";
    private final boolean isPassingData;
    private final boolean isPassingAllData;
    @Nullable
    private final ExprRootNode<?> dataExpr;
    @Nullable
    private final String userSuppliedPlaceholderName;
    private ImmutableList<String> escapingDirectiveNames = ImmutableList.of();

    protected CallNode(int id, String commandName, CommandTextInfo commandTextInfo, ImmutableList<String> escapingDirectiveNames) {
        super(id, commandName, commandTextInfo.commandText);
        this.isPassingData = commandTextInfo.isPassingData;
        this.isPassingAllData = commandTextInfo.isPassingData && commandTextInfo.dataExpr == null;
        this.dataExpr = commandTextInfo.dataExpr;
        this.userSuppliedPlaceholderName = commandTextInfo.userSuppliedPlaceholderName;
        this.escapingDirectiveNames = escapingDirectiveNames;
        this.maybeSetSyntaxVersionBound(commandTextInfo.syntaxVersionBound);
    }

    protected static Pair<Boolean, ExprRootNode<?>> parseDataAttributeHelper(String dataAttr, String commandTextForErrorMsgs) {
        ExprRootNode<?> dataExpr;
        boolean isPassingData;
        if (dataAttr == null) {
            isPassingData = false;
            dataExpr = null;
        } else if (dataAttr.equals("all")) {
            isPassingData = true;
            dataExpr = null;
        } else {
            isPassingData = true;
            dataExpr = ExprParseUtils.parseExprElseThrowSoySyntaxException(dataAttr, "Invalid expression in call command text \"" + commandTextForErrorMsgs + "\".");
        }
        return Pair.of(isPassingData, dataExpr);
    }

    protected CallNode(CallNode orig) {
        super(orig);
        this.isPassingData = orig.isPassingData;
        this.isPassingAllData = orig.isPassingAllData;
        this.dataExpr = orig.dataExpr != null ? orig.dataExpr.clone() : null;
        this.userSuppliedPlaceholderName = orig.userSuppliedPlaceholderName;
        this.escapingDirectiveNames = orig.escapingDirectiveNames;
    }

    public boolean isPassingData() {
        return this.isPassingData;
    }

    public boolean isPassingAllData() {
        return this.isPassingAllData;
    }

    @Nullable
    public ExprRootNode<?> getDataExpr() {
        return this.dataExpr;
    }

    @Override
    public String getUserSuppliedPhName() {
        return this.userSuppliedPlaceholderName;
    }

    @Override
    public String getTagString() {
        return this.buildTagStringHelper(this.numChildren() == 0);
    }

    @Override
    public String toSourceString() {
        return this.numChildren() == 0 ? this.getTagString() : super.toSourceString();
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return this.dataExpr != null ? ImmutableList.of((Object)new ExprUnion(this.dataExpr)) : Collections.emptyList();
    }

    @Override
    public String genBasePhName() {
        if (this.userSuppliedPlaceholderName != null) {
            return BaseUtils.convertToUpperUnderscore(this.userSuppliedPlaceholderName);
        }
        return FALLBACK_BASE_PLACEHOLDER_NAME;
    }

    @Override
    public Object genSamenessKey() {
        return this.getId();
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    public void setEscapingDirectiveNames(ImmutableList<String> escapingDirectiveNames) {
        this.escapingDirectiveNames = escapingDirectiveNames;
    }

    public ImmutableList<String> getEscapingDirectiveNames() {
        return this.escapingDirectiveNames;
    }

    @Immutable
    protected static class CommandTextInfo {
        private final String commandText;
        private final boolean isPassingData;
        @Nullable
        private final ExprRootNode<?> dataExpr;
        @Nullable
        private final String userSuppliedPlaceholderName;
        @Nullable
        protected final SyntaxVersionBound syntaxVersionBound;

        public CommandTextInfo(String commandText, boolean isPassingData, @Nullable ExprRootNode<?> dataExpr, @Nullable String userSuppliedPlaceholderName, @Nullable SyntaxVersionBound syntaxVersionBound) {
            Preconditions.checkArgument((isPassingData || dataExpr == null ? 1 : 0) != 0);
            this.commandText = commandText;
            this.isPassingData = isPassingData;
            this.dataExpr = dataExpr;
            this.userSuppliedPlaceholderName = userSuppliedPlaceholderName;
            this.syntaxVersionBound = syntaxVersionBound;
        }
    }
}

