/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.soytree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CommandTextAttributesParser;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

public class CallDelegateNode
extends CallNode {
    private static final Pattern NONATTRIBUTE_CALLEE_NAME = Pattern.compile("^ (?! name=\") [.\\w]+ (?= \\s | $)", 4);
    private static final CommandTextAttributesParser ATTRIBUTES_PARSER = new CommandTextAttributesParser("delcall", new CommandTextAttributesParser.Attribute("name", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("variant", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("data", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("allowemptydefault", CommandTextAttributesParser.Attribute.BOOLEAN_VALUES, null));
    private final String delCalleeName;
    @Nullable
    private final ExprRootNode<?> delCalleeVariantExpr;
    private Boolean allowsEmptyDefault;

    public CallDelegateNode(int id, String commandTextWithoutPhnameAttr, @Nullable String userSuppliedPlaceholderName) throws SoySyntaxException {
        this(id, CallDelegateNode.parseCommandTextHelper(commandTextWithoutPhnameAttr, userSuppliedPlaceholderName), (ImmutableList<String>)ImmutableList.of());
    }

    private static CommandTextInfo parseCommandTextHelper(String commandTextWithoutPhnameAttr, @Nullable String userSuppliedPlaceholderName) {
        ExprRootNode<?> delCalleeVariantExpr;
        Map<String, String> attributes;
        String delCalleeName;
        String commandText = commandTextWithoutPhnameAttr + (userSuppliedPlaceholderName != null ? " phname=\"" + userSuppliedPlaceholderName + "\"" : "");
        Matcher ncnMatcher = NONATTRIBUTE_CALLEE_NAME.matcher(commandTextWithoutPhnameAttr);
        if (ncnMatcher.find()) {
            commandTextWithoutPhnameAttr = ncnMatcher.replaceFirst("name=\"" + ncnMatcher.group() + "\"");
        }
        if ((delCalleeName = (attributes = ATTRIBUTES_PARSER.parse(commandTextWithoutPhnameAttr)).get("name")) == null) {
            throw SoySyntaxException.createWithoutMetaInfo("The 'delcall' command text must contain the callee name (encountered command text \"" + commandTextWithoutPhnameAttr + "\").");
        }
        if (!BaseUtils.isDottedIdentifier(delCalleeName)) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid delegate name \"" + delCalleeName + "\" for 'delcall' command.");
        }
        String variantExprText = attributes.get("variant");
        if (variantExprText == null) {
            delCalleeVariantExpr = null;
        } else {
            String fixedVariantStr;
            delCalleeVariantExpr = ExprParseUtils.parseExprElseThrowSoySyntaxException(variantExprText, String.format("Invalid variant expression \"%s\" in 'delcall'.", variantExprText));
            if (delCalleeVariantExpr.getChild(0) instanceof StringNode && !BaseUtils.isIdentifier(fixedVariantStr = ((StringNode)delCalleeVariantExpr.getChild(0)).getValue())) {
                throw SoySyntaxException.createWithoutMetaInfo("Invalid variant expression \"" + variantExprText + "\" in 'delcall' (variant expression must evaluate to an identifier).");
            }
        }
        Pair<Boolean, ExprRootNode<?>> dataAttrInfo = CallDelegateNode.parseDataAttributeHelper(attributes.get("data"), commandText);
        String allowemptydefaultAttr = attributes.get("allowemptydefault");
        Boolean allowsEmptyDefault = allowemptydefaultAttr == null ? null : Boolean.valueOf(allowemptydefaultAttr.equals("true"));
        return new CommandTextInfo(commandText, delCalleeName, delCalleeVariantExpr, allowsEmptyDefault, (Boolean)dataAttrInfo.first, (ExprRootNode)dataAttrInfo.second, userSuppliedPlaceholderName);
    }

    public CallDelegateNode(int id, String delCalleeName, @Nullable ExprRootNode<?> delCalleeVariantExpr, boolean useAttrStyleForCalleeName, Boolean allowsEmptyDefault, boolean isPassingData, boolean isPassingAllData, @Nullable ExprRootNode<?> dataExpr, @Nullable String userSuppliedPlaceholderName, ImmutableList<String> escapingDirectiveNames) {
        this(id, CallDelegateNode.buildCommandTextInfoHelper(delCalleeName, delCalleeVariantExpr, useAttrStyleForCalleeName, allowsEmptyDefault, isPassingData, isPassingAllData, dataExpr, userSuppliedPlaceholderName), escapingDirectiveNames);
    }

    private static CommandTextInfo buildCommandTextInfoHelper(String delCalleeName, @Nullable ExprRootNode<?> delCalleeVariantExpr, boolean useAttrStyleForCalleeName, Boolean allowsEmptyDefault, boolean isPassingData, boolean isPassingAllData, @Nullable ExprRootNode<?> dataExpr, @Nullable String userSuppliedPlaceholderName) {
        Preconditions.checkArgument((boolean)BaseUtils.isDottedIdentifier(delCalleeName));
        if (isPassingAllData) {
            Preconditions.checkArgument((boolean)isPassingData);
        }
        if (dataExpr != null) {
            Preconditions.checkArgument((isPassingData && !isPassingAllData ? 1 : 0) != 0);
        }
        String commandText = "";
        commandText = useAttrStyleForCalleeName ? commandText + "name=\"" + delCalleeName + '\"' : commandText + delCalleeName;
        if (isPassingAllData) {
            commandText = commandText + " data=\"all\"";
        } else if (isPassingData) {
            assert (dataExpr != null);
            commandText = commandText + " data=\"" + dataExpr.toSourceString() + '\"';
        }
        if (userSuppliedPlaceholderName != null) {
            commandText = commandText + " phname=\"" + userSuppliedPlaceholderName + '\"';
        }
        return new CommandTextInfo(commandText, delCalleeName, delCalleeVariantExpr, allowsEmptyDefault, isPassingData, dataExpr, userSuppliedPlaceholderName);
    }

    private CallDelegateNode(int id, CommandTextInfo commandTextInfo, ImmutableList<String> escapingDirectiveNames) {
        super(id, "delcall", commandTextInfo, escapingDirectiveNames);
        this.delCalleeName = commandTextInfo.delCalleeName;
        this.delCalleeVariantExpr = commandTextInfo.delCalleeVariantExpr;
        this.allowsEmptyDefault = commandTextInfo.allowsEmptyDefault;
    }

    protected CallDelegateNode(CallDelegateNode orig) {
        super(orig);
        this.delCalleeName = orig.delCalleeName;
        this.delCalleeVariantExpr = orig.delCalleeVariantExpr != null ? orig.delCalleeVariantExpr.clone() : null;
        this.allowsEmptyDefault = orig.allowsEmptyDefault;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.CALL_DELEGATE_NODE;
    }

    public String getDelCalleeName() {
        return this.delCalleeName;
    }

    @Nullable
    public ExprRootNode<?> getDelCalleeVariantExpr() {
        return this.delCalleeVariantExpr;
    }

    public void maybeSetAllowsEmptyDefault(boolean defaultValueForAllowsEmptyDefault) {
        if (this.allowsEmptyDefault == null) {
            this.allowsEmptyDefault = defaultValueForAllowsEmptyDefault;
        }
    }

    public boolean allowsEmptyDefault() {
        Preconditions.checkState((this.allowsEmptyDefault != null ? 1 : 0) != 0);
        return this.allowsEmptyDefault;
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        ArrayList allExprUnions = Lists.newArrayListWithCapacity((int)2);
        if (this.delCalleeVariantExpr != null) {
            allExprUnions.add(new ExprUnion(this.delCalleeVariantExpr));
        }
        allExprUnions.addAll(super.getAllExprUnions());
        return Collections.unmodifiableList(allExprUnions);
    }

    @Override
    public CallDelegateNode clone() {
        return new CallDelegateNode(this);
    }

    @Immutable
    private static class CommandTextInfo
    extends CallNode.CommandTextInfo {
        public final String delCalleeName;
        @Nullable
        public final ExprRootNode<?> delCalleeVariantExpr;
        public final Boolean allowsEmptyDefault;

        public CommandTextInfo(String commandText, String delCalleeName, @Nullable ExprRootNode<?> delCalleeVariantExpr, Boolean allowsEmptyDefault, boolean isPassingData, @Nullable ExprRootNode<?> dataExpr, @Nullable String userSuppliedPlaceholderName) {
            super(commandText, isPassingData, dataExpr, userSuppliedPlaceholderName, null);
            Preconditions.checkArgument((boolean)BaseUtils.isDottedIdentifier(delCalleeName));
            this.delCalleeName = delCalleeName;
            this.delCalleeVariantExpr = delCalleeVariantExpr;
            this.allowsEmptyDefault = allowsEmptyDefault;
        }
    }
}

