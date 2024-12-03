/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.base.Preconditions;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.internalutils.NodeContentKinds;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractCommandNode;
import com.google.template.soy.soytree.CommandTextAttributesParser;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.defn.LocalVar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public abstract class LetNode
extends AbstractCommandNode
implements SoyNode.StandaloneNode,
SoyNode.StatementNode,
SoyNode.LocalVarInlineNode {
    private static final Pattern COMMAND_TEXT_PATTERN = Pattern.compile("( [$] \\w+ ) (?: \\s* : \\s* (\\S .*) | \\s+ (\\S .*) )?", 36);
    private static final CommandTextAttributesParser ATTRIBUTES_PARSER = new CommandTextAttributesParser("let", new CommandTextAttributesParser.Attribute("kind", NodeContentKinds.getAttributeValues(), null));
    private final boolean isVarNameUnique;
    protected LocalVar var;

    protected LetNode(int id, boolean isVarNameUnique, String commandText) {
        super(id, "let", commandText);
        this.isVarNameUnique = isVarNameUnique;
    }

    protected LetNode(LetNode orig) {
        super(orig);
        this.isVarNameUnique = orig.isVarNameUnique;
        this.var = new LocalVar(orig.var.name(), this, orig.var.type());
    }

    protected CommandTextParseResult parseCommandTextHelper(String commandText) throws SoySyntaxException {
        SanitizedContent.ContentKind contentKind;
        Matcher matcher = COMMAND_TEXT_PATTERN.matcher(commandText);
        if (!matcher.matches()) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid 'let' command text \"" + commandText + "\".");
        }
        String localVarName = ExprParseUtils.parseVarNameElseThrowSoySyntaxException(matcher.group(1), "Invalid variable name in 'let' command text \"" + commandText + "\".");
        ExprRootNode<?> valueExpr = matcher.group(2) != null ? ExprParseUtils.parseExprElseThrowSoySyntaxException(matcher.group(2), "Invalid value expression in 'let' command text \"" + commandText + "\".") : null;
        if (matcher.group(3) != null) {
            Preconditions.checkState((matcher.group(2) == null ? 1 : 0) != 0, (Object)"Match groups for value expression and optional attributes should be mutually exclusive");
            Map<String, String> attributes = ATTRIBUTES_PARSER.parse(matcher.group(3));
            contentKind = attributes.get("kind") != null ? NodeContentKinds.forAttributeValue(attributes.get("kind")) : null;
        } else {
            contentKind = null;
        }
        return new CommandTextParseResult(localVarName, valueExpr, contentKind);
    }

    public String getUniqueVarName() {
        return this.isVarNameUnique ? this.getVarName() : this.getVarName() + "__soy" + this.getId();
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    public final LocalVar getVar() {
        return this.var;
    }

    protected final void setVar(LocalVar var) {
        this.var = var;
    }

    protected static class CommandTextParseResult {
        public final String localVarName;
        @Nullable
        public final ExprRootNode<?> valueExpr;
        @Nullable
        public final SanitizedContent.ContentKind contentKind;

        private CommandTextParseResult(String localVarName, @Nullable ExprRootNode<?> valueExpr, @Nullable SanitizedContent.ContentKind contentKind) {
            this.localVarName = localVarName;
            this.valueExpr = valueExpr;
            this.contentKind = contentKind;
        }
    }
}

