/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.internalutils.NodeContentKinds;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.CommandTextAttributesParser;
import com.google.template.soy.soytree.RequirecssUtils;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateNodeBuilder;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.types.SoyTypeRegistry;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class TemplateBasicNodeBuilder
extends TemplateNodeBuilder {
    private static final Pattern NONATTRIBUTE_TEMPLATE_NAME = Pattern.compile("^ (?! name=\") [.\\w]+ (?= \\s | $)", 4);
    private static final CommandTextAttributesParser ATTRIBUTES_PARSER = new CommandTextAttributesParser("template", new CommandTextAttributesParser.Attribute("name", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null), new CommandTextAttributesParser.Attribute("private", CommandTextAttributesParser.Attribute.BOOLEAN_VALUES, "false"), new CommandTextAttributesParser.Attribute("override", CommandTextAttributesParser.Attribute.BOOLEAN_VALUES, null), new CommandTextAttributesParser.Attribute("autoescape", AutoescapeMode.getAttributeValues(), null), new CommandTextAttributesParser.Attribute("kind", NodeContentKinds.getAttributeValues(), null), new CommandTextAttributesParser.Attribute("requirecss", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null));
    private Boolean isOverride;

    public TemplateBasicNodeBuilder(TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo) {
        super(soyFileHeaderInfo, null);
    }

    public TemplateBasicNodeBuilder(TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo, SoyTypeRegistry typeRegistry) {
        super(soyFileHeaderInfo, typeRegistry);
    }

    @Override
    public TemplateBasicNodeBuilder setId(int id) {
        return (TemplateBasicNodeBuilder)super.setId(id);
    }

    @Override
    public TemplateBasicNodeBuilder setCmdText(String cmdText) {
        SyntaxVersionBound newSyntaxVersionBound;
        Preconditions.checkState((this.cmdText == null ? 1 : 0) != 0);
        this.cmdText = cmdText;
        String commandTextForParsing = cmdText;
        String nameAttr = null;
        Matcher ntnMatcher = NONATTRIBUTE_TEMPLATE_NAME.matcher(commandTextForParsing);
        if (ntnMatcher.find()) {
            nameAttr = ntnMatcher.group();
            commandTextForParsing = commandTextForParsing.substring(ntnMatcher.end()).trim();
        }
        Map<String, String> attributes = ATTRIBUTES_PARSER.parse(commandTextForParsing);
        if (nameAttr == null) {
            nameAttr = attributes.get("name");
            if (nameAttr == null) {
                throw SoySyntaxException.createWithoutMetaInfo("Invalid 'template' command missing template name: {template " + cmdText + "}.");
            }
            newSyntaxVersionBound = new SyntaxVersionBound(SyntaxVersion.V2_2, String.format("Template name should be written directly instead of within attribute 'name' (i.e. use {template %s} instead of {template name=\"%s\"}.", nameAttr, nameAttr));
            this.syntaxVersionBound = SyntaxVersionBound.selectLower(this.syntaxVersionBound, newSyntaxVersionBound);
        } else if (attributes.get("name") != null) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid 'template' command with template name declared multiple times (" + nameAttr + ", " + attributes.get("name") + ").");
        }
        if (BaseUtils.isIdentifierWithLeadingDot(nameAttr)) {
            if (this.soyFileHeaderInfo.namespace == null) {
                throw SoySyntaxException.createWithoutMetaInfo("Missing namespace in Soy file containing 'template' with namespace-relative name ({template " + cmdText + "}).");
            }
            this.setTemplateNames(this.soyFileHeaderInfo.namespace + nameAttr, nameAttr);
        } else if (BaseUtils.isDottedIdentifier(nameAttr)) {
            newSyntaxVersionBound = new SyntaxVersionBound(SyntaxVersion.V2_0, "Soy V2 template names must be relative to the namespace, i.e. a dot followed by an identifier.");
            this.syntaxVersionBound = SyntaxVersionBound.selectLower(this.syntaxVersionBound, newSyntaxVersionBound);
            this.setTemplateNames(nameAttr, null);
        } else {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid template name \"" + nameAttr + "\".");
        }
        this.templateNameForUserMsgs = this.getTemplateName();
        String overrideAttr = attributes.get("override");
        if (overrideAttr == null) {
            this.isOverride = false;
        } else {
            SyntaxVersionBound newSyntaxVersionBound2 = new SyntaxVersionBound(SyntaxVersion.V2_0, "The 'override' attribute in a 'template' tag is a Soy V1 artifact.");
            this.syntaxVersionBound = SyntaxVersionBound.selectLower(this.syntaxVersionBound, newSyntaxVersionBound2);
            this.isOverride = overrideAttr.equals("true");
        }
        this.isPrivate = attributes.get("private").equals("true");
        String autoescapeModeStr = attributes.get("autoescape");
        AutoescapeMode autoescapeMode = autoescapeModeStr != null ? AutoescapeMode.forAttributeValue(autoescapeModeStr) : this.soyFileHeaderInfo.defaultAutoescapeMode;
        SanitizedContent.ContentKind contentKind = attributes.get("kind") != null ? NodeContentKinds.forAttributeValue(attributes.get("kind")) : null;
        this.setAutoescapeInfo(autoescapeMode, contentKind);
        this.setRequiredCssNamespaces(RequirecssUtils.parseRequirecssAttr(attributes.get("requirecss")));
        return this;
    }

    public TemplateBasicNodeBuilder setCmdTextInfo(String templateName, @Nullable String partialTemplateName, boolean useAttrStyleForName, boolean isOverride, boolean isPrivate, AutoescapeMode autoescapeMode, SanitizedContent.ContentKind contentKind, ImmutableList<String> requiredCssNamespaces) {
        String templateNameInCommandText;
        Preconditions.checkState((this.cmdText == null ? 1 : 0) != 0);
        Preconditions.checkArgument((boolean)BaseUtils.isDottedIdentifier(templateName));
        Preconditions.checkArgument((partialTemplateName == null || BaseUtils.isIdentifierWithLeadingDot(partialTemplateName) ? 1 : 0) != 0);
        Preconditions.checkArgument((contentKind != null == (autoescapeMode == AutoescapeMode.STRICT) ? 1 : 0) != 0);
        this.setTemplateNames(templateName, partialTemplateName);
        this.templateNameForUserMsgs = templateName;
        this.isOverride = isOverride;
        this.isPrivate = isPrivate;
        this.setAutoescapeInfo(autoescapeMode, contentKind);
        this.setRequiredCssNamespaces(requiredCssNamespaces);
        StringBuilder cmdTextBuilder = new StringBuilder();
        String string = templateNameInCommandText = partialTemplateName != null ? partialTemplateName : templateName;
        if (useAttrStyleForName) {
            cmdTextBuilder.append("name=\"").append(templateNameInCommandText).append('\"');
        } else {
            cmdTextBuilder.append(templateNameInCommandText);
        }
        cmdTextBuilder.append(" autoescape=\"").append(autoescapeMode.getAttributeValue()).append('\"');
        if (contentKind != null) {
            cmdTextBuilder.append(" kind=\"" + NodeContentKinds.toAttributeValue(contentKind) + '\"');
        }
        if (isOverride) {
            cmdTextBuilder.append(" override=\"true\"");
        }
        if (isPrivate) {
            cmdTextBuilder.append(" private=\"true\"");
        }
        if (!requiredCssNamespaces.isEmpty()) {
            cmdTextBuilder.append(" requirecss=\"" + Joiner.on((String)", ").join(requiredCssNamespaces) + "\"");
        }
        this.cmdText = cmdTextBuilder.toString();
        return this;
    }

    @Override
    public TemplateBasicNodeBuilder setSoyDoc(String soyDoc) {
        return (TemplateBasicNodeBuilder)super.setSoyDoc(soyDoc);
    }

    @Override
    public TemplateBasicNodeBuilder setHeaderDecls(List<TemplateNodeBuilder.DeclInfo> declInfos) {
        return (TemplateBasicNodeBuilder)super.setHeaderDecls(declInfos);
    }

    @Override
    public TemplateBasicNode build() {
        Preconditions.checkState((this.id != null && this.isSoyDocSet && this.cmdText != null ? 1 : 0) != 0);
        return new TemplateBasicNode((int)this.id, this.syntaxVersionBound, this.cmdText, this.soyFileHeaderInfo, this.getTemplateName(), this.getPartialTemplateName(), this.templateNameForUserMsgs, this.isOverride, (boolean)this.isPrivate, this.getAutoescapeMode(), this.getContentKind(), this.getRequiredCssNamespaces(), this.soyDoc, this.soyDocDesc, (ImmutableList<TemplateParam>)this.params);
    }
}

