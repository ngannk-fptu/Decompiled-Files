/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.CaseFormat
 *  com.google.common.base.Joiner
 *  com.google.common.base.Splitter
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.LinkedHashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.parseinfo.passes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.template.soy.base.SoyBackendKind;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.base.internal.IndentedLinesBuilder;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FieldAccessNode;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.parseinfo.SoyFileInfo;
import com.google.template.soy.sharedpasses.FindIjParamsVisitor;
import com.google.template.soy.sharedpasses.FindIndirectParamsVisitor;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.soytree.defn.HeaderParam;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.types.SoyObjectType;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.aggregate.ListType;
import com.google.template.soy.types.aggregate.MapType;
import com.google.template.soy.types.aggregate.RecordType;
import com.google.template.soy.types.aggregate.UnionType;
import com.google.template.soy.types.proto.SoyProtoType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateParseInfoVisitor
extends AbstractSoyNodeVisitor<ImmutableMap<String, String>> {
    private final String javaPackage;
    private final JavaClassNameSource javaClassNameSource;
    private Map<SoyFileNode, String> soyFileToJavaClassNameMap;
    private TemplateRegistry templateRegistry;
    private final Map<String, String> convertedIdents = Maps.newHashMap();
    private LinkedHashMap<String, String> generatedFiles;
    private IndentedLinesBuilder ilb;

    public GenerateParseInfoVisitor(String javaPackage, String javaClassNameSource) {
        this.javaPackage = javaPackage;
        if (javaClassNameSource.equals("filename")) {
            this.javaClassNameSource = JavaClassNameSource.SOY_FILE_NAME;
        } else if (javaClassNameSource.equals("namespace")) {
            this.javaClassNameSource = JavaClassNameSource.SOY_NAMESPACE_LAST_PART;
        } else if (javaClassNameSource.equals("generic")) {
            this.javaClassNameSource = JavaClassNameSource.GENERIC;
        } else {
            throw new IllegalArgumentException("Invalid value for javaClassNameSource \"" + javaClassNameSource + "\" (valid values are \"filename\", \"namespace\", and \"generic\").");
        }
    }

    @Override
    public ImmutableMap<String, String> exec(SoyNode node) {
        this.generatedFiles = Maps.newLinkedHashMap();
        this.ilb = null;
        this.visit(node);
        return ImmutableMap.copyOf(this.generatedFiles);
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        HashMultimap baseGeneratedClassNameToSoyFilesMap = HashMultimap.create();
        for (SoyFileNode soyFile : node.getChildren()) {
            if (soyFile.getSoyFileKind() != SoyFileKind.SRC) continue;
            baseGeneratedClassNameToSoyFilesMap.put((Object)this.javaClassNameSource.generateBaseClassName(soyFile), (Object)soyFile);
        }
        this.soyFileToJavaClassNameMap = Maps.newHashMap();
        for (String baseClassName : baseGeneratedClassNameToSoyFilesMap.keySet()) {
            Collection soyFiles = baseGeneratedClassNameToSoyFilesMap.get((Object)baseClassName);
            if (soyFiles.size() == 1) {
                for (SoyFileNode soyFile : soyFiles) {
                    this.soyFileToJavaClassNameMap.put(soyFile, baseClassName + "SoyInfo");
                }
                continue;
            }
            int numberSuffix = 1;
            for (SoyFileNode soyFile : soyFiles) {
                this.soyFileToJavaClassNameMap.put(soyFile, baseClassName + numberSuffix + "SoyInfo");
                ++numberSuffix;
            }
        }
        this.templateRegistry = new TemplateRegistry(node);
        for (SoyFileNode soyFile : node.getChildren()) {
            try {
                this.visit(soyFile);
            }
            catch (SoySyntaxException sse) {
                throw sse.associateMetaInfo(null, soyFile.getFilePath(), null);
            }
        }
    }

    @Override
    protected void visitSoyFileNode(SoyFileNode node) {
        if (node.getSoyFileKind() != SoyFileKind.SRC) {
            return;
        }
        if (node.getFilePath() == null) {
            throw SoySyntaxExceptionUtils.createWithNode("In order to generate parse info, all Soy files must have paths (file name is extracted from the path).", node);
        }
        String javaClassName = this.soyFileToJavaClassNameMap.get(node);
        LinkedHashMap publicBasicTemplateMap = Maps.newLinkedHashMap();
        HashSet allParamKeys = Sets.newHashSet();
        LinkedHashMultimap paramKeyToTemplatesMultimap = LinkedHashMultimap.create();
        TreeSet protoTypes = Sets.newTreeSet();
        for (Object template : node.getChildren()) {
            if (!((TemplateNode)template).isPrivate() && template instanceof TemplateBasicNode) {
                publicBasicTemplateMap.put(this.convertToUpperUnderscore(((TemplateNode)template).getPartialTemplateName().substring(1)), template);
            }
            for (TemplateParam param : ((TemplateNode)template).getParams()) {
                allParamKeys.add(param.name());
                paramKeyToTemplatesMultimap.put((Object)param.name(), template);
                if (!(param instanceof HeaderParam)) continue;
                SoyType paramType = ((HeaderParam)param).type();
                GenerateParseInfoVisitor.findProtoTypesRecurse(paramType, protoTypes);
            }
            new FindUsedProtoTypesVisitor(protoTypes).exec((SoyNode)template);
        }
        TreeMap allParamKeysMap = Maps.newTreeMap();
        for (String string : allParamKeys) {
            String upperUnderscoreKey2 = this.convertToUpperUnderscore(string);
            if (allParamKeysMap.containsKey(upperUnderscoreKey2)) {
                throw SoySyntaxExceptionUtils.createWithNode("Cannot generate parse info because two param keys '" + (String)allParamKeysMap.get(upperUnderscoreKey2) + "' and '" + string + "' generate the same upper-underscore name '" + upperUnderscoreKey2 + "'.", node);
            }
            allParamKeysMap.put(upperUnderscoreKey2, string);
        }
        this.ilb = new IndentedLinesBuilder(2);
        this.ilb.appendLine("// This file was automatically generated from ", node.getFileName(), ".");
        this.ilb.appendLine("// Please don't edit this file by hand.");
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("package ", this.javaPackage, ";");
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("import com.google.common.collect.ImmutableList;");
        this.ilb.appendLine("import com.google.common.collect.ImmutableMap;");
        this.ilb.appendLine("import com.google.common.collect.ImmutableSortedSet;");
        this.ilb.appendLine("import com.google.template.soy.parseinfo.SoyFileInfo;");
        this.ilb.appendLine("import com.google.template.soy.parseinfo.SoyTemplateInfo;");
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine(new Object[0]);
        GenerateParseInfoVisitor.appendJavadoc(this.ilb, "Soy parse info for " + node.getFileName() + ".", true, false);
        this.ilb.appendLine("public final class ", javaClassName, " extends SoyFileInfo {");
        this.ilb.increaseIndent();
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("/** This Soy file's namespace. */");
        this.ilb.appendLine("public static final String __NAMESPACE__ = \"", node.getNamespace(), "\";");
        if (!protoTypes.isEmpty()) {
            this.ilb.appendLine(new Object[0]);
            this.ilb.appendLine(new Object[0]);
            this.ilb.appendLine("/** Protocol buffer types used by these templates. */");
            this.ilb.appendLine("@Override public ImmutableList<Object> getProtoTypes() {");
            this.ilb.increaseIndent();
            Iterator defaultInstances = Lists.newArrayList();
            for (String protoTypeName : protoTypes) {
                defaultInstances.add(protoTypeName);
            }
            GenerateParseInfoVisitor.appendListOrSetHelper(this.ilb, "return ImmutableList.<Object>of", (Collection<String>)((Object)defaultInstances));
            this.ilb.appendLineEnd(";");
            this.ilb.decreaseIndent();
            this.ilb.appendLine("}");
        }
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("public static final class TemplateName {");
        this.ilb.increaseIndent();
        this.ilb.appendLine("private TemplateName() {}");
        this.ilb.appendLine(new Object[0]);
        for (Map.Entry entry : publicBasicTemplateMap.entrySet()) {
            StringBuilder javadocSb = new StringBuilder();
            javadocSb.append("The full template name of the ").append(((TemplateNode)entry.getValue()).getPartialTemplateName()).append(" template.");
            GenerateParseInfoVisitor.appendJavadoc(this.ilb, javadocSb.toString(), false, true);
            this.ilb.appendLine("public static final String ", entry.getKey(), " = \"", ((TemplateNode)entry.getValue()).getTemplateName(), "\";");
        }
        this.ilb.decreaseIndent();
        this.ilb.appendLine("}");
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("/**");
        this.ilb.appendLine(" * Param names from all templates in this Soy file.");
        this.ilb.appendLine(" */");
        this.ilb.appendLine("public static final class Param {");
        this.ilb.increaseIndent();
        this.ilb.appendLine("private Param() {}");
        this.ilb.appendLine(new Object[0]);
        for (Map.Entry entry : allParamKeysMap.entrySet()) {
            String upperUnderscoreKey = (String)entry.getKey();
            String key = (String)entry.getValue();
            StringBuilder javadocSb = new StringBuilder();
            javadocSb.append("Listed by ");
            boolean isFirst = true;
            for (TemplateNode template : paramKeyToTemplatesMultimap.get((Object)key)) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    javadocSb.append(", ");
                }
                javadocSb.append(GenerateParseInfoVisitor.buildTemplateNameForJavadoc(node, template));
            }
            javadocSb.append('.');
            GenerateParseInfoVisitor.appendJavadoc(this.ilb, javadocSb.toString(), false, true);
            this.ilb.appendLine("public static final String ", upperUnderscoreKey, " = \"", key, "\";");
        }
        this.ilb.decreaseIndent();
        this.ilb.appendLine("}");
        for (TemplateNode templateNode : publicBasicTemplateMap.values()) {
            try {
                this.visit(templateNode);
            }
            catch (SoySyntaxException sse) {
                throw sse.associateMetaInfo(null, null, templateNode.getTemplateNameForUserMsgs());
            }
        }
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("private ", javaClassName, "() {");
        this.ilb.increaseIndent();
        this.ilb.appendLine("super(");
        this.ilb.increaseIndent(2);
        this.ilb.appendLine("\"", node.getFileName(), "\",");
        this.ilb.appendLine("\"", node.getNamespace(), "\",");
        ArrayList itemSnippets = Lists.newArrayList();
        for (String upperUnderscoreKey : allParamKeysMap.keySet()) {
            itemSnippets.add("Param." + upperUnderscoreKey);
        }
        GenerateParseInfoVisitor.appendImmutableSortedSet(this.ilb, "<String>", itemSnippets);
        this.ilb.appendLineEnd(",");
        itemSnippets = Lists.newArrayList();
        for (String upperUnderscoreTemplateName : publicBasicTemplateMap.keySet()) {
            itemSnippets.add(upperUnderscoreTemplateName);
        }
        GenerateParseInfoVisitor.appendImmutableList(this.ilb, "<SoyTemplateInfo>", itemSnippets);
        this.ilb.appendLineEnd(",");
        SortedMap<String, SoyFileInfo.CssTagsPrefixPresence> sortedMap = new CollectCssNamesVisitor().exec(node);
        ArrayList entrySnippetPairs = Lists.newArrayList();
        for (Map.Entry<String, SoyFileInfo.CssTagsPrefixPresence> entry : sortedMap.entrySet()) {
            entrySnippetPairs.add(Pair.of("\"" + entry.getKey() + "\"", "CssTagsPrefixPresence." + entry.getValue().name()));
        }
        GenerateParseInfoVisitor.appendImmutableMap(this.ilb, "<String, CssTagsPrefixPresence>", entrySnippetPairs);
        this.ilb.appendLineEnd(");");
        this.ilb.decreaseIndent(2);
        this.ilb.decreaseIndent();
        this.ilb.appendLine("}");
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("private static final ", javaClassName, " __INSTANCE__ =");
        this.ilb.increaseIndent(2);
        this.ilb.appendLine("new ", javaClassName, "();");
        this.ilb.decreaseIndent(2);
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("public static ", javaClassName, " getInstance() {");
        this.ilb.increaseIndent();
        this.ilb.appendLine("return __INSTANCE__;");
        this.ilb.decreaseIndent();
        this.ilb.appendLine("}");
        this.ilb.appendLine(new Object[0]);
        this.ilb.decreaseIndent();
        this.ilb.appendLine("}");
        this.generatedFiles.put(javaClassName + ".java", this.ilb.toString());
        this.ilb = null;
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        if (node.isPrivate() || node instanceof TemplateDelegateNode) {
            return;
        }
        LinkedHashMap transitiveParamMap = Maps.newLinkedHashMap();
        List<TemplateParam> params = node.getParams();
        if (params != null) {
            for (TemplateParam templateParam : params) {
                transitiveParamMap.put(templateParam.name(), templateParam);
            }
        }
        FindIndirectParamsVisitor.IndirectParamsInfo indirectParamsInfo = new FindIndirectParamsVisitor(this.templateRegistry).exec(node);
        for (TemplateParam param : indirectParamsInfo.indirectParams.values()) {
            TemplateParam existingParam = (TemplateParam)transitiveParamMap.get(param.name());
            if (existingParam != null) continue;
            transitiveParamMap.put(param.name(), param.cloneEssential());
        }
        FindIjParamsVisitor.IjParamsInfo ijParamsInfo = new FindIjParamsVisitor(this.templateRegistry).exec(node);
        String upperUnderscoreName = this.convertToUpperUnderscore(node.getPartialTemplateName().substring(1));
        String templateInfoClassName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, upperUnderscoreName) + "SoyTemplateInfo";
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine(new Object[0]);
        GenerateParseInfoVisitor.appendJavadoc(this.ilb, node.getSoyDocDesc(), true, false);
        this.ilb.appendLine("public static final class ", templateInfoClassName, " extends SoyTemplateInfo {");
        this.ilb.increaseIndent();
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("/** This template's full name. */");
        this.ilb.appendLine("public static final String __NAME__ = \"", node.getTemplateName(), "\";");
        this.ilb.appendLine("/** This template's partial name. */");
        this.ilb.appendLine("public static final String __PARTIAL_NAME__ = \"", node.getPartialTemplateName(), "\";");
        boolean hasSeenFirstDirectParam = false;
        boolean hasSwitchedToIndirectParams = false;
        for (TemplateParam param : transitiveParamMap.values()) {
            if (param.desc() != null) {
                if (!hasSeenFirstDirectParam) {
                    this.ilb.appendLine(new Object[0]);
                    hasSeenFirstDirectParam = true;
                }
                GenerateParseInfoVisitor.appendJavadoc(this.ilb, param.desc(), false, false);
            } else {
                if (!hasSwitchedToIndirectParams) {
                    this.ilb.appendLine(new Object[0]);
                    this.ilb.appendLine("// Indirect params.");
                    hasSwitchedToIndirectParams = true;
                }
                TreeSet sortedJavadocCalleeNames = Sets.newTreeSet();
                for (TemplateNode transitiveCallee : indirectParamsInfo.paramKeyToCalleesMultimap.get((Object)param.name())) {
                    String javadocCalleeName = GenerateParseInfoVisitor.buildTemplateNameForJavadoc(node.getParent(), transitiveCallee);
                    sortedJavadocCalleeNames.add(javadocCalleeName);
                }
                StringBuilder javadocSb = new StringBuilder();
                javadocSb.append("Listed by ");
                boolean isFirst = true;
                for (String javadocCalleeName : sortedJavadocCalleeNames) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        javadocSb.append(", ");
                    }
                    javadocSb.append(javadocCalleeName);
                }
                javadocSb.append('.');
                GenerateParseInfoVisitor.appendJavadoc(this.ilb, javadocSb.toString(), false, true);
            }
            this.ilb.appendLine("public static final String ", this.convertToUpperUnderscore(param.name()), " = \"", param.name(), "\";");
        }
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("private ", templateInfoClassName, "() {");
        this.ilb.increaseIndent();
        this.ilb.appendLine("super(");
        this.ilb.increaseIndent(2);
        this.ilb.appendLine("\"", node.getTemplateName(), "\",");
        if (transitiveParamMap.size() > 0) {
            ArrayList entrySnippetPairs = Lists.newArrayList();
            for (TemplateParam param : transitiveParamMap.values()) {
                entrySnippetPairs.add(Pair.of("\"" + param.name() + "\"", param.isRequired() ? "ParamRequisiteness.REQUIRED" : "ParamRequisiteness.OPTIONAL"));
            }
            GenerateParseInfoVisitor.appendImmutableMap(this.ilb, "<String, ParamRequisiteness>", entrySnippetPairs);
            this.ilb.appendLineEnd(",");
        } else {
            this.ilb.appendLine("ImmutableMap.<String, ParamRequisiteness>of(),");
        }
        this.appendIjParamSet(this.ilb, ijParamsInfo);
        this.ilb.appendLineEnd(",");
        this.ilb.appendLine(ijParamsInfo.mayHaveIjParamsInExternalCalls, ",");
        this.ilb.appendLineStart(ijParamsInfo.mayHaveIjParamsInExternalDelCalls);
        this.ilb.appendLineEnd(");");
        this.ilb.decreaseIndent(2);
        this.ilb.decreaseIndent();
        this.ilb.appendLine("}");
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("private static final ", templateInfoClassName, " __INSTANCE__ =");
        this.ilb.increaseIndent(2);
        this.ilb.appendLine("new ", templateInfoClassName, "();");
        this.ilb.decreaseIndent(2);
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("public static ", templateInfoClassName, " getInstance() {");
        this.ilb.increaseIndent();
        this.ilb.appendLine("return __INSTANCE__;");
        this.ilb.decreaseIndent();
        this.ilb.appendLine("}");
        this.ilb.decreaseIndent();
        this.ilb.appendLine("}");
        this.ilb.appendLine(new Object[0]);
        this.ilb.appendLine("/** Same as ", templateInfoClassName, ".getInstance(). */");
        this.ilb.appendLine("public static final ", templateInfoClassName, " ", upperUnderscoreName, " =");
        this.ilb.increaseIndent(2);
        this.ilb.appendLine(templateInfoClassName, ".getInstance();");
        this.ilb.decreaseIndent(2);
    }

    private String convertToUpperUnderscore(String ident) {
        String result = this.convertedIdents.get(ident);
        if (result == null) {
            result = BaseUtils.convertToUpperUnderscore(ident);
            this.convertedIdents.put(ident, result);
        }
        return result;
    }

    private static void findProtoTypesRecurse(SoyType type, SortedSet<String> protoTypes) {
        if (type instanceof SoyProtoType) {
            protoTypes.add(((SoyProtoType)((Object)type)).getDescriptorExpression());
        } else {
            switch (type.getKind()) {
                case UNION: {
                    for (SoyType member : ((UnionType)type).getMembers()) {
                        GenerateParseInfoVisitor.findProtoTypesRecurse(member, protoTypes);
                    }
                    break;
                }
                case LIST: {
                    ListType listType = (ListType)type;
                    GenerateParseInfoVisitor.findProtoTypesRecurse(listType.getElementType(), protoTypes);
                    break;
                }
                case MAP: {
                    MapType mapType = (MapType)type;
                    GenerateParseInfoVisitor.findProtoTypesRecurse(mapType.getKeyType(), protoTypes);
                    GenerateParseInfoVisitor.findProtoTypesRecurse(mapType.getValueType(), protoTypes);
                    break;
                }
                case RECORD: {
                    RecordType recordType = (RecordType)type;
                    for (SoyType fieldType : recordType.getMembers().values()) {
                        GenerateParseInfoVisitor.findProtoTypesRecurse(fieldType, protoTypes);
                    }
                    break;
                }
            }
        }
    }

    @VisibleForTesting
    static void appendJavadoc(IndentedLinesBuilder ilb, String doc, boolean forceMultiline, boolean wrapAt100Chars) {
        if (wrapAt100Chars) {
            int wrapLen = 100 - ilb.getCurrIndentLen() - 7;
            ArrayList wrappedLines = Lists.newArrayList();
            for (String line : Splitter.on((char)'\n').split((CharSequence)doc)) {
                while (line.length() > wrapLen) {
                    int spaceIndex = line.lastIndexOf(32, wrapLen);
                    if (spaceIndex >= 0) {
                        wrappedLines.add(line.substring(0, spaceIndex));
                        line = line.substring(spaceIndex + 1);
                        continue;
                    }
                    wrappedLines.add(line.substring(0, wrapLen));
                    line = line.substring(wrapLen);
                }
                wrappedLines.add(line);
            }
            doc = Joiner.on((String)"\n").join((Iterable)wrappedLines);
        }
        if (doc.contains("\n") || forceMultiline) {
            ilb.appendLine("/**");
            for (String line : Splitter.on((char)'\n').split((CharSequence)doc)) {
                ilb.appendLine(" * ", line);
            }
            ilb.appendLine(" */");
        } else {
            ilb.appendLine("/** ", doc, " */");
        }
    }

    private void appendIjParamSet(IndentedLinesBuilder ilb, FindIjParamsVisitor.IjParamsInfo ijParamsInfo) {
        ArrayList itemSnippets = Lists.newArrayList();
        for (String paramKey : ijParamsInfo.ijParamSet) {
            itemSnippets.add("\"" + paramKey + "\"");
        }
        GenerateParseInfoVisitor.appendImmutableSortedSet(ilb, "<String>", itemSnippets);
    }

    private static String buildTemplateNameForJavadoc(SoyFileNode currSoyFile, TemplateNode template) {
        StringBuilder resultSb = new StringBuilder();
        if (template.getParent() == currSoyFile && !(template instanceof TemplateDelegateNode)) {
            resultSb.append(template.getPartialTemplateName());
        } else {
            resultSb.append(template.getTemplateNameForUserMsgs());
        }
        if (template.isPrivate()) {
            resultSb.append(" (private)");
        }
        if (template instanceof TemplateDelegateNode) {
            resultSb.append(" (delegate)");
        }
        return resultSb.toString();
    }

    private static void appendImmutableList(IndentedLinesBuilder ilb, String typeParamSnippet, Collection<String> itemSnippets) {
        GenerateParseInfoVisitor.appendListOrSetHelper(ilb, "ImmutableList." + typeParamSnippet + "of", itemSnippets);
    }

    private static void appendImmutableSortedSet(IndentedLinesBuilder ilb, String typeParamSnippet, Collection<String> itemSnippets) {
        GenerateParseInfoVisitor.appendListOrSetHelper(ilb, "ImmutableSortedSet." + typeParamSnippet + "of", itemSnippets);
    }

    private static void appendListOrSetHelper(IndentedLinesBuilder ilb, String creationFunctionSnippet, Collection<String> itemSnippets) {
        if (itemSnippets.size() == 0) {
            ilb.appendLineStart(creationFunctionSnippet, "()");
        } else {
            ilb.appendLine(creationFunctionSnippet, "(");
            boolean isFirst = true;
            for (String item : itemSnippets) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    ilb.appendLineEnd(",");
                }
                ilb.appendLineStart("    ", item);
            }
            ilb.append(")");
        }
    }

    private static void appendImmutableMap(IndentedLinesBuilder ilb, String typeParamSnippet, Collection<Pair<String, String>> entrySnippetPairs) {
        if (entrySnippetPairs.size() == 0) {
            ilb.appendLineStart("ImmutableMap.", typeParamSnippet, "of()");
        } else {
            ilb.appendLine("ImmutableMap.", typeParamSnippet, "builder()");
            for (Pair<String, String> entrySnippetPair : entrySnippetPairs) {
                ilb.appendLine("    .put(", entrySnippetPair.first, ", ", entrySnippetPair.second, ")");
            }
            ilb.appendLineStart("    .build()");
        }
    }

    private static class FindUsedProtoTypesExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private final SortedSet<String> protoTypes;

        public FindUsedProtoTypesExprVisitor(SortedSet<String> protoTypes) {
            this.protoTypes = protoTypes;
        }

        @Override
        protected void visit(ExprNode node) {
            super.visit(node);
        }

        @Override
        protected void visitExprRootNode(ExprRootNode<?> node) {
            this.visitChildren(node);
            Node expr = node.getChild(0);
            node.setType(expr.getType());
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }

        @Override
        protected void visitFieldAccessNode(FieldAccessNode node) {
            SoyObjectType protoType;
            String importName;
            this.visit(node.getBaseExprChild());
            SoyType baseType = node.getBaseExprChild().getType();
            if (baseType instanceof SoyProtoType && (importName = (protoType = (SoyObjectType)baseType).getFieldImport(node.getFieldName(), SoyBackendKind.TOFU)) != null) {
                this.protoTypes.add(importName);
            }
        }
    }

    private static class FindUsedProtoTypesVisitor
    extends AbstractSoyNodeVisitor<Void> {
        private final SortedSet<String> protoTypes;

        public FindUsedProtoTypesVisitor(SortedSet<String> protoTypes) {
            this.protoTypes = protoTypes;
        }

        @Override
        public Void exec(SoyNode node) {
            this.visit(node);
            return null;
        }

        @Override
        protected void visitSoyNode(SoyNode node) {
            if (node instanceof SoyNode.ExprHolderNode) {
                this.visitExpressions((SoyNode.ExprHolderNode)node);
            }
            if (node instanceof SoyNode.ParentSoyNode) {
                this.visitChildren((SoyNode.ParentSoyNode)node);
            }
        }

        private void visitExpressions(SoyNode.ExprHolderNode node) {
            FindUsedProtoTypesExprVisitor exprVisitor = new FindUsedProtoTypesExprVisitor(this.protoTypes);
            for (ExprUnion exprUnion : node.getAllExprUnions()) {
                if (exprUnion.getExpr() == null) continue;
                exprVisitor.exec(exprUnion.getExpr());
            }
        }
    }

    private static class CollectCssNamesVisitor
    extends AbstractSoyNodeVisitor<SortedMap<String, SoyFileInfo.CssTagsPrefixPresence>> {
        private SortedMap<String, SoyFileInfo.CssTagsPrefixPresence> cssNamesMap = Maps.newTreeMap();

        @Override
        public SortedMap<String, SoyFileInfo.CssTagsPrefixPresence> exec(SoyNode node) {
            this.visit(node);
            return this.cssNamesMap;
        }

        @Override
        protected void visitCssNode(CssNode node) {
            SoyFileInfo.CssTagsPrefixPresence additionalCssTagsPrefixPresence;
            String cssName = node.getSelectorText();
            SoyFileInfo.CssTagsPrefixPresence existingCssTagsPrefixPresence = (SoyFileInfo.CssTagsPrefixPresence)((Object)this.cssNamesMap.get(cssName));
            SoyFileInfo.CssTagsPrefixPresence cssTagsPrefixPresence = additionalCssTagsPrefixPresence = node.getComponentNameExpr() == null ? SoyFileInfo.CssTagsPrefixPresence.NEVER : SoyFileInfo.CssTagsPrefixPresence.ALWAYS;
            if (existingCssTagsPrefixPresence == null) {
                this.cssNamesMap.put(cssName, additionalCssTagsPrefixPresence);
            } else if (existingCssTagsPrefixPresence != additionalCssTagsPrefixPresence) {
                this.cssNamesMap.put(cssName, SoyFileInfo.CssTagsPrefixPresence.SOMETIMES);
            }
        }

        @Override
        protected void visitSoyNode(SoyNode node) {
            if (node instanceof SoyNode.ParentSoyNode) {
                this.visitChildren((SoyNode.ParentSoyNode)node);
            }
        }
    }

    @VisibleForTesting
    static final class JavaClassNameSource
    extends Enum<JavaClassNameSource> {
        public static final /* enum */ JavaClassNameSource SOY_FILE_NAME = new JavaClassNameSource();
        public static final /* enum */ JavaClassNameSource SOY_NAMESPACE_LAST_PART = new JavaClassNameSource();
        public static final /* enum */ JavaClassNameSource GENERIC = new JavaClassNameSource();
        private static final Pattern ALL_UPPER_WORD;
        private static final Pattern ALL_LOWER_WORD;
        private static final Pattern NON_LETTER_DIGIT;
        private static final /* synthetic */ JavaClassNameSource[] $VALUES;

        public static JavaClassNameSource[] values() {
            return (JavaClassNameSource[])$VALUES.clone();
        }

        public static JavaClassNameSource valueOf(String name) {
            return Enum.valueOf(JavaClassNameSource.class, name);
        }

        @VisibleForTesting
        String generateBaseClassName(SoyFileNode soyFile) {
            switch (this) {
                case SOY_FILE_NAME: {
                    String fileName = soyFile.getFileName();
                    if (fileName == null) {
                        throw new IllegalArgumentException("Trying to generate Java class name based on Soy file name, but Soy file name was not provided.");
                    }
                    if (fileName.toLowerCase().endsWith(".soy")) {
                        fileName = fileName.substring(0, fileName.length() - 4);
                    }
                    return JavaClassNameSource.makeUpperCamelCase(fileName);
                }
                case SOY_NAMESPACE_LAST_PART: {
                    String namespace = soyFile.getNamespace();
                    assert (namespace != null);
                    String namespaceLastPart = namespace.substring(namespace.lastIndexOf(46) + 1);
                    return JavaClassNameSource.makeUpperCamelCase(namespaceLastPart);
                }
                case GENERIC: {
                    return "File";
                }
            }
            throw new AssertionError();
        }

        private static String makeUpperCamelCase(String str) {
            str = JavaClassNameSource.makeWordsCapitalized(str, ALL_UPPER_WORD);
            str = JavaClassNameSource.makeWordsCapitalized(str, ALL_LOWER_WORD);
            str = NON_LETTER_DIGIT.matcher(str).replaceAll("");
            return str;
        }

        private static String makeWordsCapitalized(String str, Pattern wordPattern) {
            StringBuffer sb = new StringBuffer();
            Matcher wordMatcher = wordPattern.matcher(str);
            while (wordMatcher.find()) {
                String oldWord = wordMatcher.group();
                StringBuilder newWord = new StringBuilder();
                int n = oldWord.length();
                for (int i = 0; i < n; ++i) {
                    if (i == 0) {
                        newWord.append(Character.toUpperCase(oldWord.charAt(i)));
                        continue;
                    }
                    newWord.append(Character.toLowerCase(oldWord.charAt(i)));
                }
                wordMatcher.appendReplacement(sb, Matcher.quoteReplacement(newWord.toString()));
            }
            wordMatcher.appendTail(sb);
            return sb.toString();
        }

        static {
            $VALUES = new JavaClassNameSource[]{SOY_FILE_NAME, SOY_NAMESPACE_LAST_PART, GENERIC};
            ALL_UPPER_WORD = Pattern.compile("(?<= [^A-Za-z] | ^)  [A-Z]+  (?= [^A-Za-z] | $)", 4);
            ALL_LOWER_WORD = Pattern.compile("(?<= [^A-Za-z] | ^)  [a-z]+  (?= [^a-z] | $)", 4);
            NON_LETTER_DIGIT = Pattern.compile("[^A-Za-z0-9]");
        }
    }
}

