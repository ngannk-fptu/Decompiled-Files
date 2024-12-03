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
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CommandTextAttributesParser;
import com.google.template.soy.soytree.SoyNode;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

public class CallBasicNode
extends CallNode {
    private static final Pattern NONATTRIBUTE_CALLEE_NAME = Pattern.compile("^ (?! name=\" | function=\") [.\\w]+ (?= \\s | $)", 4);
    private static final CommandTextAttributesParser ATTRIBUTES_PARSER = new CommandTextAttributesParser("call", new CommandTextAttributesParser.Attribute("name", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("function", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("data", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null));
    private final String srcCalleeName;
    private String calleeName;

    public CallBasicNode(int id, String commandTextWithoutPhnameAttr, @Nullable String userSuppliedPlaceholderName) throws SoySyntaxException {
        this(id, CallBasicNode.parseCommandTextHelper(commandTextWithoutPhnameAttr, userSuppliedPlaceholderName), (ImmutableList<String>)ImmutableList.of());
    }

    private static CommandTextInfo parseCommandTextHelper(String cmdTextWithoutPhnameAttr, @Nullable String userSuppliedPlaceholderName) {
        String srcCalleeName;
        String functionAttr;
        Map<String, String> attributes;
        String nameAttr;
        String cmdText = cmdTextWithoutPhnameAttr + (userSuppliedPlaceholderName != null ? " phname=\"" + userSuppliedPlaceholderName + "\"" : "");
        String cmdTextForParsing = cmdTextWithoutPhnameAttr;
        SyntaxVersionBound syntaxVersionBound = null;
        ArrayList srcCalleeNames = Lists.newArrayList();
        Matcher ncnMatcher = NONATTRIBUTE_CALLEE_NAME.matcher(cmdTextForParsing);
        if (ncnMatcher.find()) {
            srcCalleeNames.add(ncnMatcher.group());
            cmdTextForParsing = cmdTextForParsing.substring(ncnMatcher.end()).trim();
        }
        if ((nameAttr = (attributes = ATTRIBUTES_PARSER.parse(cmdTextForParsing)).get("name")) != null) {
            srcCalleeNames.add(nameAttr);
            SyntaxVersionBound newSyntaxVersionBound = new SyntaxVersionBound(SyntaxVersion.V2_2, String.format("Callee name should be written directly instead of within attribute 'name' (i.e. use {call %s} instead of {call name=\"%s\"}.", nameAttr, nameAttr));
            syntaxVersionBound = SyntaxVersionBound.selectLower(syntaxVersionBound, newSyntaxVersionBound);
        }
        if ((functionAttr = attributes.get("function")) != null) {
            srcCalleeNames.add(functionAttr);
            SyntaxVersionBound newSyntaxVersionBound = new SyntaxVersionBound(SyntaxVersion.V2_0, "The 'function' attribute in a 'call' tag is a Soy V1 artifact.");
            syntaxVersionBound = SyntaxVersionBound.selectLower(syntaxVersionBound, newSyntaxVersionBound);
        }
        if (srcCalleeNames.size() == 0) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid 'call' command missing callee name: {call " + cmdText + "}.");
        }
        if (srcCalleeNames.size() == 1) {
            srcCalleeName = (String)srcCalleeNames.get(0);
            if (!BaseUtils.isIdentifierWithLeadingDot(srcCalleeName) && !BaseUtils.isDottedIdentifier(srcCalleeName)) {
                throw SoySyntaxException.createWithoutMetaInfo("Invalid callee name \"" + srcCalleeName + "\" for 'call' command.");
            }
        } else {
            throw SoySyntaxException.createWithoutMetaInfo(String.format("Invalid 'call' command with callee name declared multiple times (%s, %s)", srcCalleeNames.get(0), srcCalleeNames.get(1)));
        }
        Pair<Boolean, ExprRootNode<?>> dataAttrInfo = CallBasicNode.parseDataAttributeHelper(attributes.get("data"), cmdText);
        return new CommandTextInfo(cmdText, srcCalleeName, (Boolean)dataAttrInfo.first, (ExprRootNode)dataAttrInfo.second, userSuppliedPlaceholderName, syntaxVersionBound);
    }

    public CallBasicNode(int id, String calleeName, String srcCalleeName, boolean useAttrStyleForCalleeName, boolean useV1FunctionAttrForCalleeName, boolean isPassingData, boolean isPassingAllData, @Nullable ExprRootNode<?> dataExpr, @Nullable String userSuppliedPlaceholderName, @Nullable SyntaxVersionBound syntaxVersionBound, ImmutableList<String> escapingDirectiveNames) {
        this(id, CallBasicNode.buildCommandTextInfoHelper(srcCalleeName, useAttrStyleForCalleeName, useV1FunctionAttrForCalleeName, isPassingData, isPassingAllData, dataExpr, userSuppliedPlaceholderName, syntaxVersionBound), escapingDirectiveNames);
        Preconditions.checkArgument((boolean)BaseUtils.isDottedIdentifier(calleeName));
        this.calleeName = calleeName;
    }

    private static CommandTextInfo buildCommandTextInfoHelper(String srcCalleeName, boolean useAttrStyleForCalleeName, boolean useV1FunctionAttrForCalleeName, boolean isPassingData, boolean isPassingAllData, @Nullable ExprRootNode<?> dataExpr, @Nullable String userSuppliedPlaceholderName, @Nullable SyntaxVersionBound syntaxVersionBound) {
        Preconditions.checkArgument((BaseUtils.isIdentifierWithLeadingDot(srcCalleeName) || BaseUtils.isDottedIdentifier(srcCalleeName) ? 1 : 0) != 0);
        if (isPassingAllData) {
            Preconditions.checkArgument((boolean)isPassingData);
        }
        if (dataExpr != null) {
            Preconditions.checkArgument((isPassingData && !isPassingAllData ? 1 : 0) != 0);
        }
        String commandText = "";
        if (useV1FunctionAttrForCalleeName) {
            Preconditions.checkArgument((syntaxVersionBound != null && syntaxVersionBound.syntaxVersion == SyntaxVersion.V2_0 ? 1 : 0) != 0);
            commandText = commandText + "function=\"" + srcCalleeName + '\"';
        } else {
            commandText = useAttrStyleForCalleeName ? commandText + "name=\"" + srcCalleeName + '\"' : commandText + srcCalleeName;
        }
        if (isPassingAllData) {
            commandText = commandText + " data=\"all\"";
        } else if (isPassingData) {
            assert (dataExpr != null);
            commandText = commandText + " data=\"" + dataExpr.toSourceString() + '\"';
        }
        if (userSuppliedPlaceholderName != null) {
            commandText = commandText + " phname=\"" + userSuppliedPlaceholderName + '\"';
        }
        return new CommandTextInfo(commandText, srcCalleeName, isPassingData, dataExpr, userSuppliedPlaceholderName, syntaxVersionBound);
    }

    private CallBasicNode(int id, CommandTextInfo commandTextInfo, ImmutableList<String> escapingDirectiveNames) {
        super(id, "call", commandTextInfo, escapingDirectiveNames);
        this.srcCalleeName = commandTextInfo.srcCalleeName;
        this.calleeName = null;
    }

    protected CallBasicNode(CallBasicNode orig) {
        super(orig);
        this.srcCalleeName = orig.srcCalleeName;
        this.calleeName = orig.calleeName;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.CALL_BASIC_NODE;
    }

    public String getSrcCalleeName() {
        return this.srcCalleeName;
    }

    public void setCalleeName(String calleeName) {
        Preconditions.checkState((this.calleeName == null ? 1 : 0) != 0);
        Preconditions.checkArgument((boolean)BaseUtils.isDottedIdentifier(calleeName));
        this.calleeName = calleeName;
    }

    public String getCalleeName() {
        return this.calleeName;
    }

    @Override
    public CallBasicNode clone() {
        return new CallBasicNode(this);
    }

    @Immutable
    protected static class CommandTextInfo
    extends CallNode.CommandTextInfo {
        private final String srcCalleeName;

        public CommandTextInfo(String commandText, String srcCalleeName, boolean isPassingData, @Nullable ExprRootNode<?> dataExpr, @Nullable String userSuppliedPlaceholderName, @Nullable SyntaxVersionBound syntaxVersionBound) {
            super(commandText, isPassingData, dataExpr, userSuppliedPlaceholderName, syntaxVersionBound);
            Preconditions.checkArgument((BaseUtils.isIdentifierWithLeadingDot(srcCalleeName) || BaseUtils.isDottedIdentifier(srcCalleeName) ? 1 : 0) != 0);
            this.srcCalleeName = srcCalleeName;
        }
    }
}

