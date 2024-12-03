/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.soytree.ExprUnion;
import java.util.List;
import javax.annotation.Nullable;

public interface SoyNode
extends Node {
    public Kind getKind();

    public void setId(int var1);

    public int getId();

    public void setSourceLocation(SourceLocation var1);

    public SourceLocation getSourceLocation();

    public ParentSoyNode<?> getParent();

    @Override
    public SoyNode clone();

    public static interface MsgPlaceholderInitialNode
    extends StandaloneNode {
        public String getUserSuppliedPhName();

        public String genBasePhName();

        public Object genSamenessKey();
    }

    public static interface MsgBlockNode
    extends BlockNode {
    }

    public static interface MsgSubstUnitNode
    extends StandaloneNode {
        @Override
        public MsgBlockNode getParent();

        public String getBaseVarName();

        public boolean shouldUseSameVarNameAs(MsgSubstUnitNode var1);
    }

    public static interface ExprHolderNode
    extends SoyNode {
        public List<ExprUnion> getAllExprUnions();
    }

    public static interface LocalVarInlineNode
    extends LocalVarNode,
    StandaloneNode {
    }

    public static interface LocalVarBlockNode
    extends LocalVarNode,
    BlockNode {
    }

    public static interface LocalVarNode
    extends SoyNode {
        public String getVarName();
    }

    public static interface LoopNode
    extends BlockNode {
    }

    public static interface ConditionalBlockNode
    extends BlockNode {
    }

    public static interface StatementNode
    extends StandaloneNode {
    }

    public static interface RenderUnitNode
    extends BlockCommandNode {
        @Nullable
        public SanitizedContent.ContentKind getContentKind();
    }

    public static interface BlockCommandNode
    extends CommandNode,
    BlockNode {
    }

    public static interface CommandNode
    extends SoyNode {
        public String getCommandName();

        public String getCommandText();

        public String getTagString();
    }

    public static interface BlockNode
    extends ParentSoyNode<StandaloneNode> {
    }

    public static interface StandaloneNode
    extends SoyNode {
        @Override
        public BlockNode getParent();
    }

    public static interface SplitLevelTopNode<N extends SoyNode>
    extends ParentSoyNode<N> {
    }

    public static interface ParentSoyNode<N extends SoyNode>
    extends SoyNode,
    ParentNode<N> {
        public void setNeedsEnvFrameDuringInterp(Boolean var1);

        public Boolean needsEnvFrameDuringInterp();
    }

    public static enum Kind {
        SOY_FILE_SET_NODE,
        SOY_FILE_NODE,
        TEMPLATE_BASIC_NODE,
        TEMPLATE_DELEGATE_NODE,
        RAW_TEXT_NODE,
        GOOG_MSG_DEF_NODE,
        GOOG_MSG_REF_NODE,
        MSG_FALLBACK_GROUP_NODE,
        MSG_NODE,
        MSG_PLURAL_NODE,
        MSG_PLURAL_CASE_NODE,
        MSG_PLURAL_DEFAULT_NODE,
        MSG_PLURAL_REMAINDER_NODE,
        MSG_SELECT_NODE,
        MSG_SELECT_CASE_NODE,
        MSG_SELECT_DEFAULT_NODE,
        MSG_PLACEHOLDER_NODE,
        MSG_HTML_TAG_NODE,
        PRINT_NODE,
        PRINT_DIRECTIVE_NODE,
        XID_NODE,
        CSS_NODE,
        LET_VALUE_NODE,
        LET_CONTENT_NODE,
        IF_NODE,
        IF_COND_NODE,
        IF_ELSE_NODE,
        SWITCH_NODE,
        SWITCH_CASE_NODE,
        SWITCH_DEFAULT_NODE,
        FOREACH_NODE,
        FOREACH_NONEMPTY_NODE,
        FOREACH_IFEMPTY_NODE,
        FOR_NODE,
        CALL_BASIC_NODE,
        CALL_DELEGATE_NODE,
        CALL_PARAM_VALUE_NODE,
        CALL_PARAM_CONTENT_NODE,
        LOG_NODE,
        DEBUGGER_NODE;

    }
}

