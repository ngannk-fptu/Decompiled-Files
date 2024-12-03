/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.soytree.AbstractParentSoyNode;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.CommandTextAttributesParser;
import com.google.template.soy.soytree.RequirecssUtils;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class SoyFileNode
extends AbstractParentSoyNode<TemplateNode>
implements SoyNode.SplitLevelTopNode<TemplateNode> {
    private static final Pattern NAMESPACE_CMD_TEXT_PATTERN = Pattern.compile("([a-zA-Z_][a-zA-Z_0-9]*(?:[.][a-zA-Z_][a-zA-Z_0-9]*)*) (\\s .*)?", 36);
    private static final Pattern ALIAS_CMD_TEXT_PATTERN = Pattern.compile("([a-zA-Z_][a-zA-Z_0-9]*(?:[.][a-zA-Z_][a-zA-Z_0-9]*)*) (?: \\s+ as \\s+ ([a-zA-Z_][a-zA-Z_0-9]*) )?", 4);
    private static final AutoescapeMode DEFAULT_FILE_WIDE_DEFAULT_AUTOESCAPE_MODE = AutoescapeMode.TRUE;
    private static final CommandTextAttributesParser ATTRIBUTES_PARSER = new CommandTextAttributesParser("namespace", new CommandTextAttributesParser.Attribute("autoescape", AutoescapeMode.getAttributeValues(), DEFAULT_FILE_WIDE_DEFAULT_AUTOESCAPE_MODE.getAttributeValue()), new CommandTextAttributesParser.Attribute("requirecss", CommandTextAttributesParser.Attribute.ALLOW_ALL_VALUES, null));
    public static final Predicate<SoyFileNode> MATCH_SRC_FILENODE = new Predicate<SoyFileNode>(){

        public boolean apply(@Nullable SoyFileNode input) {
            return input != null && input.getSoyFileKind() == SoyFileKind.SRC;
        }
    };
    private final SoyFileKind soyFileKind;
    @Nullable
    private final String delPackageName;
    @Nullable
    private final String namespace;
    private final AutoescapeMode defaultAutoescapeMode;
    private final ImmutableList<String> requiredCssNamespaces;
    private final ImmutableMap<String, String> aliasToNamespaceMap;
    @Nullable
    private String fileName;

    public SoyFileNode(int id, SoyFileKind soyFileKind, @Nullable String delpackageCmdText, @Nullable String namespaceCmdText, @Nullable List<String> aliasCmdTexts) throws SoySyntaxException {
        super(id);
        this.soyFileKind = soyFileKind;
        if (delpackageCmdText != null) {
            this.delPackageName = delpackageCmdText;
            if (!BaseUtils.isDottedIdentifier(this.delPackageName)) {
                throw SoySyntaxException.createWithoutMetaInfo("Invalid delegate package name \"" + this.delPackageName + "\".");
            }
        } else {
            this.delPackageName = null;
        }
        String namespace = null;
        AutoescapeMode defaultAutoescapeMode = DEFAULT_FILE_WIDE_DEFAULT_AUTOESCAPE_MODE;
        ImmutableList<String> requiredCssNamespaces = ImmutableList.of();
        if (namespaceCmdText != null) {
            Matcher nctMatcher = NAMESPACE_CMD_TEXT_PATTERN.matcher(namespaceCmdText);
            if (nctMatcher.matches()) {
                namespace = nctMatcher.group(1);
                String attributeText = nctMatcher.group(2);
                if (attributeText != null) {
                    Map<String, String> attributes = ATTRIBUTES_PARSER.parse(attributeText = attributeText.trim());
                    if (attributes.containsKey("autoescape")) {
                        defaultAutoescapeMode = AutoescapeMode.forAttributeValue(attributes.get("autoescape"));
                    }
                    if (attributes.containsKey("requirecss")) {
                        requiredCssNamespaces = RequirecssUtils.parseRequirecssAttr(attributes.get("requirecss"));
                    }
                }
            } else {
                throw SoySyntaxException.createWithoutMetaInfo("Invalid namespace command text \"" + namespaceCmdText + "\".");
            }
        }
        this.namespace = namespace;
        this.defaultAutoescapeMode = defaultAutoescapeMode;
        this.requiredCssNamespaces = requiredCssNamespaces;
        if (namespace == null) {
            this.maybeSetSyntaxVersionBound(new SyntaxVersionBound(SyntaxVersion.V2_0, "Soy V2 files must have a namespace declaration."));
        } else if (!BaseUtils.isDottedIdentifier(namespace)) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid namespace name \"" + namespace + "\".");
        }
        if (aliasCmdTexts != null) {
            Preconditions.checkNotNull((Object)this.namespace);
            String aliasForFileNamespace = BaseUtils.extractPartAfterLastDot(this.namespace);
            LinkedHashMap tempAliasToNamespaceMap = Maps.newLinkedHashMap();
            for (String aliasCmdText : aliasCmdTexts) {
                String alias;
                Matcher actMatcher = ALIAS_CMD_TEXT_PATTERN.matcher(aliasCmdText);
                Preconditions.checkArgument((boolean)actMatcher.matches());
                String aliasNamespace = actMatcher.group(1);
                Preconditions.checkArgument((boolean)BaseUtils.isDottedIdentifier(aliasNamespace));
                String string = alias = actMatcher.group(2) != null ? actMatcher.group(2) : BaseUtils.extractPartAfterLastDot(aliasNamespace);
                if (alias.equals("as")) {
                    throw SoySyntaxException.createWithoutMetaInfo(String.format("Not allowed to use the string 'as' as a namespace alias (found while aliasing namespace \"%s\").", aliasNamespace));
                }
                if (alias.equals(aliasForFileNamespace) && !aliasNamespace.equals(this.namespace)) {
                    throw SoySyntaxException.createWithoutMetaInfo(String.format("Not allowed to alias the last part of the file's namespace to some other namespace (file's namespace is \"%s\", while aliased namespace is \"%s\").", this.namespace, aliasNamespace));
                }
                if (tempAliasToNamespaceMap.containsKey(alias)) {
                    throw SoySyntaxException.createWithoutMetaInfo(String.format("Found 2 namespaces with the same alias (\"%s\" and \"%s\").", tempAliasToNamespaceMap.get(alias), aliasNamespace));
                }
                tempAliasToNamespaceMap.put(alias, aliasNamespace);
            }
            this.aliasToNamespaceMap = ImmutableMap.copyOf((Map)tempAliasToNamespaceMap);
        } else {
            this.aliasToNamespaceMap = ImmutableMap.of();
        }
    }

    protected SoyFileNode(SoyFileNode orig) {
        super(orig);
        this.soyFileKind = orig.soyFileKind;
        this.delPackageName = orig.delPackageName;
        this.namespace = orig.namespace;
        this.defaultAutoescapeMode = orig.defaultAutoescapeMode;
        this.requiredCssNamespaces = orig.requiredCssNamespaces;
        this.aliasToNamespaceMap = orig.aliasToNamespaceMap;
        this.fileName = orig.fileName;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.SOY_FILE_NODE;
    }

    public SoyFileKind getSoyFileKind() {
        return this.soyFileKind;
    }

    @Nullable
    public String getDelPackageName() {
        return this.delPackageName;
    }

    @Nullable
    public String getNamespace() {
        return this.namespace;
    }

    public AutoescapeMode getDefaultAutoescapeMode() {
        return this.defaultAutoescapeMode;
    }

    public ImmutableList<String> getRequiredCssNamespaces() {
        return this.requiredCssNamespaces;
    }

    public ImmutableMap<String, String> getAliasToNamespaceMap() {
        return this.aliasToNamespaceMap;
    }

    @Override
    public void setSourceLocation(SourceLocation srcLoc) {
        super.setSourceLocation(srcLoc);
    }

    public void setFilePath(String filePath) {
        this.setSourceLocation(new SourceLocation(filePath, 0));
    }

    public String getFilePath() {
        return this.getSourceLocation().getFilePath();
    }

    @Nullable
    public String getFileName() {
        return this.getSourceLocation().getFileName();
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        if (this.delPackageName != null) {
            sb.append("{delpackage ").append(this.delPackageName).append("}\n");
        }
        if (this.namespace != null) {
            sb.append("{namespace ").append(this.namespace).append("}\n");
        }
        if (this.aliasToNamespaceMap.size() > 0) {
            sb.append("\n");
            for (Map.Entry entry : this.aliasToNamespaceMap.entrySet()) {
                String alias = (String)entry.getKey();
                String aliasNamespace = (String)entry.getValue();
                if (aliasNamespace.equals(alias) || aliasNamespace.endsWith("." + alias)) {
                    sb.append("{alias ").append(aliasNamespace).append("}\n");
                    continue;
                }
                sb.append("{alias ").append(aliasNamespace).append(" as ").append(alias).append("}\n");
            }
        }
        for (SoyNode child : this.getChildren()) {
            sb.append("\n");
            sb.append(child.toSourceString());
        }
        return sb.toString();
    }

    @Override
    public SoyFileSetNode getParent() {
        return (SoyFileSetNode)super.getParent();
    }

    @Override
    public SoyFileNode clone() {
        return new SoyFileNode(this);
    }
}

