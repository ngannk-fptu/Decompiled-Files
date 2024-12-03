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
import com.google.template.soy.basetree.Node;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.internalutils.NodeContentKinds;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.soytree.AbstractCommandNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CommandTextAttributesParser;
import com.google.template.soy.soytree.ExprUnion;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public abstract class CallParamNode
extends AbstractCommandNode {
    private static final Pattern NONATTRIBUTE_COMMAND_TEXT = Pattern.compile("^ (?! key=\") (\\w+) (?: \\s* : \\s* (\\S .*) | (.*) )? $", 36);
    private static final CommandTextAttributesParser ATTRIBUTES_PARSER = new CommandTextAttributesParser("param", new CommandTextAttributesParser.Attribute("key", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, "__NDVBR__"), new CommandTextAttributesParser.Attribute("value", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("kind", NodeContentKinds.getAttributeValues(), null));

    protected CallParamNode(int id, String commandText) {
        super(id, "param", commandText);
    }

    protected CallParamNode(CallParamNode orig) {
        super(orig);
    }

    protected CommandTextParseResult parseCommandTextHelper(String commandText) throws SoySyntaxException {
        ExprRootNode<?> valueExpr;
        Matcher nctMatcher = NONATTRIBUTE_COMMAND_TEXT.matcher(commandText);
        if (nctMatcher.matches()) {
            commandText = "key=\"" + nctMatcher.group(1) + "\"";
            if (nctMatcher.group(3) != null) {
                Preconditions.checkState((nctMatcher.group(2) == null ? 1 : 0) != 0);
                commandText = commandText + " " + nctMatcher.group(3);
            }
        }
        Map<String, String> attributes = ATTRIBUTES_PARSER.parse(commandText);
        String key = attributes.get("key");
        String valueExprText = nctMatcher.matches() && nctMatcher.group(2) != null ? nctMatcher.group(2) : attributes.get("value");
        SanitizedContent.ContentKind contentKind = attributes.get("kind") != null ? NodeContentKinds.forAttributeValue(attributes.get("kind")) : null;
        Node dataRef = ExprParseUtils.parseDataRefElseThrowSoySyntaxException("$" + key, "Invalid key in 'param' command text \"" + commandText + "\".").getChild(0);
        if (!(dataRef instanceof VarRefNode) || ((VarRefNode)dataRef).isInjected()) {
            throw SoySyntaxException.createWithoutMetaInfo("The key in a 'param' tag must be top level, i.e. not contain multiple keys (invalid 'param' command text \"" + commandText + "\").");
        }
        ExprUnion valueExprUnion = valueExprText != null ? ((valueExpr = ExprParseUtils.parseExprElseNull(valueExprText)) != null ? new ExprUnion(valueExpr) : new ExprUnion(valueExprText)) : null;
        return new CommandTextParseResult(key, valueExprUnion, contentKind);
    }

    public abstract String getKey();

    @Override
    public CallNode getParent() {
        return (CallNode)super.getParent();
    }

    protected static class CommandTextParseResult {
        public final String key;
        @Nullable
        public final ExprUnion valueExprUnion;
        public final SanitizedContent.ContentKind contentKind;

        private CommandTextParseResult(String key, ExprUnion valueExprUnion, SanitizedContent.ContentKind contentKind) {
            this.key = key;
            this.valueExprUnion = valueExprUnion;
            this.contentKind = contentKind;
        }
    }
}

