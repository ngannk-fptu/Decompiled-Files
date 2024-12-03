/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.google.template.soy.soytree;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.internalutils.NodeContentKinds;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.CommandTextAttributesParser;
import com.google.template.soy.soytree.RequirecssUtils;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateNodeBuilder;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.types.SoyTypeRegistry;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateDelegateNodeBuilder
extends TemplateNodeBuilder {
    private static final Pattern COMMAND_TEXT_PATTERN = Pattern.compile("([.\\w]+) ( \\s .* | $ )", 36);
    private static final CommandTextAttributesParser ATTRIBUTES_PARSER = new CommandTextAttributesParser("deltemplate", new CommandTextAttributesParser.Attribute("variant", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("autoescape", AutoescapeMode.getAttributeValues(), null), new CommandTextAttributesParser.Attribute("kind", NodeContentKinds.getAttributeValues(), null), new CommandTextAttributesParser.Attribute("requirecss", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null));
    private String delTemplateName;
    private String delTemplateVariant = null;
    private ExprRootNode<?> delTemplateVariantExpr = null;
    private TemplateDelegateNode.DelTemplateKey delTemplateKey;
    private int delPriority;

    public TemplateDelegateNodeBuilder(TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo) {
        super(soyFileHeaderInfo, null);
    }

    public TemplateDelegateNodeBuilder(TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo, SoyTypeRegistry typeRegistry) {
        super(soyFileHeaderInfo, typeRegistry);
    }

    @Override
    public TemplateDelegateNodeBuilder setId(int id) {
        return (TemplateDelegateNodeBuilder)super.setId(id);
    }

    @Override
    public TemplateDelegateNodeBuilder setCmdText(String cmdText) {
        Preconditions.checkState((this.cmdText == null ? 1 : 0) != 0);
        this.cmdText = cmdText;
        Matcher matcher = COMMAND_TEXT_PATTERN.matcher(cmdText);
        if (!matcher.matches()) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid 'deltemplate' command text \"" + cmdText + "\".");
        }
        this.delTemplateName = matcher.group(1);
        if (!BaseUtils.isDottedIdentifier(this.delTemplateName)) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid delegate template name \"" + this.delTemplateName + "\".");
        }
        Map<String, String> attributes = ATTRIBUTES_PARSER.parse(matcher.group(2).trim());
        String variantExprText = attributes.get("variant");
        if (variantExprText == null) {
            this.delTemplateVariant = "";
        } else {
            ExprRootNode<?> variantExpr = ExprParseUtils.parseExprElseThrowSoySyntaxException(variantExprText, String.format("Invalid variant expression \"%s\" in 'deltemplate'.", variantExprText));
            Node child = variantExpr.getChild(0);
            if (child instanceof StringNode) {
                this.delTemplateVariant = ((StringNode)child).getValue();
                TemplateDelegateNode.verifyVariantName(this.delTemplateVariant);
            } else if (child instanceof GlobalNode) {
                this.delTemplateVariantExpr = variantExpr;
                this.templateNameForUserMsgs = this.delTemplateName + ":" + ((GlobalNode)child).getName();
            } else {
                throw SoySyntaxException.createWithoutMetaInfo("Invalid variant expression \"" + variantExprText + "\" in 'deltemplate' (must be a string literal that contains an identifier or an integer global).");
            }
        }
        if (this.delTemplateVariant != null) {
            this.delTemplateKey = new TemplateDelegateNode.DelTemplateKey(this.delTemplateName, this.delTemplateVariant);
            this.templateNameForUserMsgs = this.delTemplateKey.toString();
        }
        this.delPriority = this.soyFileHeaderInfo.defaultDelPriority;
        if (this.delPriority < 0 || this.delPriority > 1) {
            throw SoySyntaxException.createWithoutMetaInfo(String.format("Invalid delegate template priority %s (valid range is 0 to %s).", this.delPriority, 1));
        }
        String autoescapeModeStr = attributes.get("autoescape");
        AutoescapeMode autoescapeMode = autoescapeModeStr != null ? AutoescapeMode.forAttributeValue(autoescapeModeStr) : this.soyFileHeaderInfo.defaultAutoescapeMode;
        SanitizedContent.ContentKind contentKind = attributes.get("kind") != null ? NodeContentKinds.forAttributeValue(attributes.get("kind")) : null;
        this.setAutoescapeInfo(autoescapeMode, contentKind);
        this.setRequiredCssNamespaces(RequirecssUtils.parseRequirecssAttr(attributes.get("requirecss")));
        this.genInternalTemplateNameHelper();
        return this;
    }

    public TemplateDelegateNodeBuilder setCmdTextInfo(String delTemplateName, String delTemplateVariant, int delPriority, AutoescapeMode autoescapeMode, SanitizedContent.ContentKind contentKind, ImmutableList<String> requiredCssNamespaces) {
        Preconditions.checkState((this.cmdText == null ? 1 : 0) != 0);
        Preconditions.checkArgument((boolean)BaseUtils.isDottedIdentifier(delTemplateName));
        Preconditions.checkArgument((delTemplateVariant.length() == 0 || BaseUtils.isIdentifier(delTemplateVariant) ? 1 : 0) != 0);
        Preconditions.checkArgument((0 <= delPriority && delPriority <= 1 ? 1 : 0) != 0);
        Preconditions.checkArgument((contentKind != null == (autoescapeMode == AutoescapeMode.STRICT) ? 1 : 0) != 0);
        this.delTemplateName = delTemplateName;
        this.delTemplateVariant = delTemplateVariant;
        this.delTemplateKey = new TemplateDelegateNode.DelTemplateKey(delTemplateName, delTemplateVariant);
        this.templateNameForUserMsgs = this.delTemplateKey.toString();
        this.delPriority = delPriority;
        this.setAutoescapeInfo(autoescapeMode, contentKind);
        this.setRequiredCssNamespaces(requiredCssNamespaces);
        String cmdText = delTemplateName + (delTemplateVariant.length() == 0 ? "" : " variant=\"" + delTemplateVariant + "\"") + " autoescape=\"" + autoescapeMode.getAttributeValue() + "\"";
        if (contentKind != null) {
            cmdText = cmdText + " kind=\"" + NodeContentKinds.toAttributeValue(contentKind) + '\"';
        }
        if (!requiredCssNamespaces.isEmpty()) {
            cmdText = cmdText + " requirecss=\"" + Joiner.on((String)", ").join(requiredCssNamespaces) + "\"";
        }
        this.cmdText = cmdText;
        this.genInternalTemplateNameHelper();
        return this;
    }

    private void genInternalTemplateNameHelper() {
        Preconditions.checkState((this.id != null ? 1 : 0) != 0);
        String delPackageAndDelTemplateStr = (this.soyFileHeaderInfo.delPackageName == null ? "" : this.soyFileHeaderInfo.delPackageName) + "~" + this.delTemplateName + "~" + this.delTemplateVariant;
        String collisionPreventionStr = BaseUtils.computePartialSha1AsHexString(delPackageAndDelTemplateStr, 32);
        String generatedPartialTemplateName = ".__deltemplate_s" + this.id + "_" + collisionPreventionStr;
        String generatedTemplateName = this.soyFileHeaderInfo.namespace + generatedPartialTemplateName;
        this.setTemplateNames(generatedTemplateName, generatedPartialTemplateName);
    }

    @Override
    public TemplateDelegateNodeBuilder setSoyDoc(String soyDoc) {
        if (soyDoc == null) {
            throw SoySyntaxException.createWithoutMetaInfo(this.delTemplateName != null ? "Encountered delegate template " + this.delTemplateName + " without SoyDoc." : "Encountered delegate template without SoyDoc.");
        }
        return (TemplateDelegateNodeBuilder)super.setSoyDoc(soyDoc);
    }

    @Override
    public TemplateDelegateNodeBuilder setHeaderDecls(List<TemplateNodeBuilder.DeclInfo> declInfos) {
        return (TemplateDelegateNodeBuilder)super.setHeaderDecls(declInfos);
    }

    @Override
    public TemplateDelegateNode build() {
        Preconditions.checkState((this.id != null && this.isSoyDocSet && this.cmdText != null ? 1 : 0) != 0);
        return new TemplateDelegateNode(this.id, this.syntaxVersionBound, this.cmdText, this.soyFileHeaderInfo, this.delTemplateName, this.delTemplateVariant, this.delTemplateVariantExpr, this.delTemplateKey, this.delPriority, this.getTemplateName(), this.getPartialTemplateName(), this.templateNameForUserMsgs, this.getAutoescapeMode(), this.getContentKind(), this.getRequiredCssNamespaces(), this.soyDoc, this.soyDocDesc, (ImmutableList<TemplateParam>)this.params);
    }
}

