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
import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.CommandTextAttributesParser;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.MsgSubstUnitBaseVarNameUtils;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MsgPluralNode
extends AbstractParentCommandNode<CaseOrDefaultNode>
implements SoyNode.MsgSubstUnitNode,
SoyNode.SplitLevelTopNode<CaseOrDefaultNode>,
SoyNode.ExprHolderNode {
    private static final Pattern COMMAND_TEXT_PATTERN = Pattern.compile("(.+?) ( \\s+ offset= .+ )?", 4);
    private static final CommandTextAttributesParser ATTRIBUTES_PARSER = new CommandTextAttributesParser("plural", new CommandTextAttributesParser.Attribute("offset", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null));
    public static final String FALLBACK_BASE_PLURAL_VAR_NAME = "NUM";
    private final int offset;
    private final ExprRootNode<?> pluralExpr;
    private final String basePluralVarName;

    public MsgPluralNode(int id, String commandText) throws SoySyntaxException {
        block5: {
            super(id, "plural", commandText);
            Matcher matcher = COMMAND_TEXT_PATTERN.matcher(commandText);
            if (!matcher.matches()) {
                throw SoySyntaxException.createWithoutMetaInfo("Invalid 'plural' command text \"" + commandText + "\".");
            }
            this.pluralExpr = ExprParseUtils.parseExprElseThrowSoySyntaxException(matcher.group(1), "Invalid expression in 'plural' command text \"" + commandText + "\".");
            if (matcher.group(2) != null) {
                try {
                    Map<String, String> attributes = ATTRIBUTES_PARSER.parse(matcher.group(2).trim());
                    String offsetAttribute = attributes.get("offset");
                    this.offset = Integer.parseInt(offsetAttribute);
                    if (this.offset < 0) {
                        throw SoySyntaxException.createWithoutMetaInfo("The 'offset' for plural must be a nonnegative integer.");
                    }
                    break block5;
                }
                catch (NumberFormatException nfe) {
                    throw SoySyntaxException.createCausedWithoutMetaInfo("Invalid offset in 'plural' command text \"" + commandText + "\".", nfe);
                }
            }
            this.offset = 0;
        }
        this.basePluralVarName = MsgSubstUnitBaseVarNameUtils.genNaiveBaseNameForExpr(this.pluralExpr, FALLBACK_BASE_PLURAL_VAR_NAME);
    }

    protected MsgPluralNode(MsgPluralNode orig) {
        super(orig);
        this.offset = orig.offset;
        this.pluralExpr = orig.pluralExpr.clone();
        this.basePluralVarName = orig.basePluralVarName;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_PLURAL_NODE;
    }

    public int getOffset() {
        return this.offset;
    }

    public String getExprText() {
        return this.pluralExpr.toSourceString();
    }

    public ExprRootNode<?> getExpr() {
        return this.pluralExpr;
    }

    @Override
    public String getBaseVarName() {
        return this.basePluralVarName;
    }

    @Override
    public boolean shouldUseSameVarNameAs(SoyNode.MsgSubstUnitNode other) {
        return other instanceof MsgPluralNode && this.getCommandText().equals(((MsgPluralNode)other).getCommandText());
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ImmutableList.of((Object)new ExprUnion(this.pluralExpr));
    }

    @Override
    public SoyNode.MsgBlockNode getParent() {
        return (SoyNode.MsgBlockNode)super.getParent();
    }

    @Override
    public MsgPluralNode clone() {
        return new MsgPluralNode(this);
    }
}

