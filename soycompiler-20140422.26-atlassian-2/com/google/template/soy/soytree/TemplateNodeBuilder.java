/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.defn.HeaderParam;
import com.google.template.soy.soytree.defn.SoyDocParam;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.SoyTypeRegistry;
import com.google.template.soy.types.aggregate.UnionType;
import com.google.template.soy.types.parse.ParseException;
import com.google.template.soy.types.parse.TypeParser;
import com.google.template.soy.types.primitive.NullType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public abstract class TemplateNodeBuilder {
    protected final TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo;
    private final SoyTypeRegistry typeRegistry;
    protected Integer id;
    @Nullable
    protected SyntaxVersionBound syntaxVersionBound;
    protected String cmdText;
    private String templateName;
    private String partialTemplateName;
    protected String templateNameForUserMsgs;
    protected Boolean isPrivate;
    private AutoescapeMode autoescapeMode;
    private ImmutableList<String> requiredCssNamespaces;
    private SanitizedContent.ContentKind contentKind;
    protected boolean isSoyDocSet;
    protected String soyDoc;
    protected String soyDocDesc;
    @Nullable
    protected ImmutableList<TemplateParam> params;
    private static final Pattern HEADER_PARAM_DECL_CMD_TEXT_PATTERN = Pattern.compile("^ ([^:\\s]+) \\s* : \\s* (\\S .*) $", 36);
    private static final Pattern NEWLINE = Pattern.compile("\\n|\\r\\n?");
    private static final Pattern SOY_DOC_START = Pattern.compile("^ [/][*][*] [\\ ]* \\r?\\n?", 4);
    private static final Pattern SOY_DOC_END = Pattern.compile("\\r?\\n? [\\ ]* [*][/] $", 4);
    private static final Pattern SOY_DOC_DECL_PATTERN = Pattern.compile("( @param[?]? ) \\s+ ( \\S+ )", 4);
    private static final Pattern SOY_DOC_PARAM_TEXT_PATTERN = Pattern.compile("[a-zA-Z_]\\w*", 4);

    protected TemplateNodeBuilder(TemplateNode.SoyFileHeaderInfo soyFileHeaderInfo, @Nullable SoyTypeRegistry typeRegistry) {
        this.soyFileHeaderInfo = soyFileHeaderInfo;
        this.typeRegistry = typeRegistry;
        this.syntaxVersionBound = null;
        this.isSoyDocSet = false;
    }

    public TemplateNodeBuilder setId(int id) {
        Preconditions.checkState((this.id == null ? 1 : 0) != 0);
        this.id = id;
        return this;
    }

    public abstract TemplateNodeBuilder setCmdText(String var1);

    public String getTemplateNameForUserMsgs() {
        Preconditions.checkState((this.templateNameForUserMsgs != null ? 1 : 0) != 0);
        return this.templateNameForUserMsgs;
    }

    public TemplateNodeBuilder setSoyDoc(String soyDoc) {
        Preconditions.checkState((!this.isSoyDocSet ? 1 : 0) != 0);
        Preconditions.checkState((this.cmdText != null ? 1 : 0) != 0);
        this.isSoyDocSet = true;
        this.soyDoc = soyDoc;
        if (soyDoc != null) {
            Preconditions.checkArgument((soyDoc.startsWith("/**") && soyDoc.endsWith("*/") ? 1 : 0) != 0);
            String cleanedSoyDoc = TemplateNodeBuilder.cleanSoyDocHelper(soyDoc);
            this.soyDocDesc = TemplateNodeBuilder.parseSoyDocDescHelper(cleanedSoyDoc);
            SoyDocDeclsInfo soyDocDeclsInfo = TemplateNodeBuilder.parseSoyDocDeclsHelper(cleanedSoyDoc);
            this.addParams(soyDocDeclsInfo.params);
            if (soyDocDeclsInfo.lowestSyntaxVersionBound != null) {
                SyntaxVersionBound newSyntaxVersionBound = new SyntaxVersionBound(soyDocDeclsInfo.lowestSyntaxVersionBound, "Template SoyDoc has incorrect param declarations where the param name is not a valid identifier: " + soyDocDeclsInfo.incorrectSoyDocParamSrcs);
                this.syntaxVersionBound = SyntaxVersionBound.selectLower(this.syntaxVersionBound, newSyntaxVersionBound);
            }
        } else {
            SyntaxVersionBound newSyntaxVersionBound = new SyntaxVersionBound(SyntaxVersion.V2_0, "Template is missing SoyDoc.");
            this.syntaxVersionBound = SyntaxVersionBound.selectLower(this.syntaxVersionBound, newSyntaxVersionBound);
            this.soyDocDesc = null;
        }
        return this;
    }

    public TemplateNodeBuilder setHeaderDecls(List<DeclInfo> declInfos) {
        ArrayList params = Lists.newArrayList();
        for (DeclInfo declInfo : declInfos) {
            if (declInfo.cmdName.equals("@param") || declInfo.cmdName.equals("@param?")) {
                SoyType type;
                Matcher cmdTextMatcher = HEADER_PARAM_DECL_CMD_TEXT_PATTERN.matcher(declInfo.cmdText);
                if (!cmdTextMatcher.matches()) {
                    throw SoySyntaxException.createWithoutMetaInfo("Invalid @param declaration command text \"" + declInfo.cmdText + "\".");
                }
                String key = cmdTextMatcher.group(1);
                if (!BaseUtils.isIdentifier(key)) {
                    throw SoySyntaxException.createWithoutMetaInfo("Invalid @param key '" + key + "' (must be an identifier).");
                }
                String typeSrc = cmdTextMatcher.group(2);
                boolean isRequired = true;
                try {
                    Preconditions.checkNotNull((Object)this.typeRegistry);
                    type = new TypeParser(typeSrc, this.typeRegistry).parseTypeDeclaration();
                    if (declInfo.cmdName.equals("@param?")) {
                        isRequired = false;
                        type = this.typeRegistry.getOrCreateUnionType(type, NullType.getInstance());
                    } else if (type instanceof UnionType && ((UnionType)type).isNullable()) {
                        isRequired = false;
                    }
                }
                catch (ParseException e) {
                    throw SoySyntaxException.createWithoutMetaInfo(e.getMessage());
                }
                params.add(new HeaderParam(key, typeSrc, type, isRequired, declInfo.soyDoc));
                continue;
            }
            throw new AssertionError();
        }
        this.addParams(params);
        return this;
    }

    private void addParams(Collection<? extends TemplateParam> params) {
        this.params = this.params == null ? ImmutableList.copyOf(params) : ImmutableList.builder().addAll(this.params).addAll(params).build();
        HashSet seenParamKeys = Sets.newHashSet();
        for (TemplateParam param : this.params) {
            if (param.name().equals("ij")) {
                throw SoySyntaxException.createWithoutMetaInfo("Invalid param name 'ij' ('ij' is for injected data ref).");
            }
            if (seenParamKeys.contains(param.name())) {
                throw SoySyntaxException.createWithoutMetaInfo("Duplicate declaration of param '" + param.name() + "'.");
            }
            seenParamKeys.add(param.name());
        }
    }

    public abstract TemplateNode build();

    protected void setAutoescapeInfo(AutoescapeMode autoescapeMode, @Nullable SanitizedContent.ContentKind contentKind) {
        Preconditions.checkArgument((autoescapeMode != null ? 1 : 0) != 0);
        this.autoescapeMode = autoescapeMode;
        if (contentKind == null && autoescapeMode == AutoescapeMode.STRICT) {
            contentKind = SanitizedContent.ContentKind.HTML;
        } else if (contentKind != null && autoescapeMode != AutoescapeMode.STRICT) {
            throw SoySyntaxException.createWithoutMetaInfo("kind=\"...\" attribute is only valid with autoescape=\"strict\".");
        }
        this.contentKind = contentKind;
    }

    protected AutoescapeMode getAutoescapeMode() {
        Preconditions.checkState((this.autoescapeMode != null ? 1 : 0) != 0);
        return this.autoescapeMode;
    }

    @Nullable
    protected SanitizedContent.ContentKind getContentKind() {
        return this.contentKind;
    }

    protected void setRequiredCssNamespaces(ImmutableList<String> requiredCssNamespaces) {
        this.requiredCssNamespaces = (ImmutableList)Preconditions.checkNotNull(requiredCssNamespaces);
    }

    protected ImmutableList<String> getRequiredCssNamespaces() {
        return (ImmutableList)Preconditions.checkNotNull(this.requiredCssNamespaces);
    }

    protected void setTemplateNames(String templateName, @Nullable String partialTemplateName) {
        this.templateName = templateName;
        this.partialTemplateName = partialTemplateName;
        if (partialTemplateName != null) {
            if (!BaseUtils.isIdentifierWithLeadingDot(partialTemplateName)) {
                throw SoySyntaxException.createWithoutMetaInfo("Invalid template name \"" + partialTemplateName + "\".");
            }
        } else if (!BaseUtils.isDottedIdentifier(templateName)) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid template name \"" + templateName + "\".");
        }
    }

    protected String getTemplateName() {
        Preconditions.checkState((this.templateName != null ? 1 : 0) != 0);
        return this.templateName;
    }

    @Nullable
    protected String getPartialTemplateName() {
        return this.partialTemplateName;
    }

    private static String cleanSoyDocHelper(String soyDoc) {
        soyDoc = NEWLINE.matcher(soyDoc).replaceAll("\n");
        soyDoc = soyDoc.replace("@deprecated", "&#64;deprecated");
        soyDoc = SOY_DOC_START.matcher(soyDoc).replaceFirst("");
        soyDoc = SOY_DOC_END.matcher(soyDoc).replaceFirst("");
        ArrayList lines = Lists.newArrayList((Iterable)Splitter.on((Pattern)NEWLINE).split((CharSequence)soyDoc));
        TemplateNodeBuilder.removeCommonStartCharHelper(lines, ' ', true);
        if (TemplateNodeBuilder.removeCommonStartCharHelper(lines, '*', false) == 1) {
            TemplateNodeBuilder.removeCommonStartCharHelper(lines, ' ', true);
        }
        return Joiner.on((char)'\n').join((Iterable)lines);
    }

    private static int removeCommonStartCharHelper(List<String> lines, char charToRemove, boolean shouldRemoveMultiple) {
        int numCharsToRemove = 0;
        boolean isStillCounting = true;
        do {
            boolean areAllLinesEmpty = true;
            for (String line : lines) {
                if (line.length() == 0) continue;
                areAllLinesEmpty = false;
                if (line.length() > numCharsToRemove && line.charAt(numCharsToRemove) == charToRemove) continue;
                isStillCounting = false;
                break;
            }
            if (areAllLinesEmpty) {
                isStillCounting = false;
            }
            if (!isStillCounting) continue;
            ++numCharsToRemove;
        } while (isStillCounting && shouldRemoveMultiple);
        if (numCharsToRemove > 0) {
            for (int i = 0; i < lines.size(); ++i) {
                String line = lines.get(i);
                if (line.length() == 0) continue;
                lines.set(i, line.substring(numCharsToRemove));
            }
        }
        return numCharsToRemove;
    }

    private static String parseSoyDocDescHelper(String cleanedSoyDoc) {
        Matcher paramMatcher = SOY_DOC_DECL_PATTERN.matcher(cleanedSoyDoc);
        int endOfDescPos = paramMatcher.find() ? paramMatcher.start() : cleanedSoyDoc.length();
        String soyDocDesc = cleanedSoyDoc.substring(0, endOfDescPos);
        return CharMatcher.whitespace().trimTrailingFrom((CharSequence)soyDocDesc);
    }

    private static SoyDocDeclsInfo parseSoyDocDeclsHelper(String cleanedSoyDoc) {
        SoyDocDeclsInfo result = new SoyDocDeclsInfo();
        Matcher matcher = SOY_DOC_DECL_PATTERN.matcher(cleanedSoyDoc);
        boolean isFound = matcher.find();
        while (isFound) {
            String declKeyword = matcher.group(1);
            String declText = matcher.group(2);
            int descStart = matcher.end();
            isFound = matcher.find();
            int descEnd = isFound ? matcher.start() : cleanedSoyDoc.length();
            String desc = cleanedSoyDoc.substring(descStart, descEnd).trim();
            if (declKeyword.equals("@param") || declKeyword.equals("@param?")) {
                if (SOY_DOC_PARAM_TEXT_PATTERN.matcher(declText).matches()) {
                    result.params.add(new SoyDocParam(declText, declKeyword.equals("@param"), desc));
                    continue;
                }
                result.incorrectSoyDocParamSrcs.add(declKeyword + " " + declText);
                if (declText.startsWith("{")) {
                    if (result.lowestSyntaxVersionBound != null) continue;
                    result.lowestSyntaxVersionBound = SyntaxVersion.V2_0;
                    continue;
                }
                result.lowestSyntaxVersionBound = SyntaxVersion.V1_0;
                continue;
            }
            throw new AssertionError();
        }
        return result;
    }

    private static class SoyDocDeclsInfo {
        public List<SoyDocParam> params = Lists.newArrayList();
        public List<String> incorrectSoyDocParamSrcs = Lists.newArrayListWithCapacity((int)0);
        public SyntaxVersion lowestSyntaxVersionBound = null;
    }

    public static class DeclInfo {
        public final String cmdName;
        public final String cmdText;
        @Nullable
        public final String soyDoc;

        public DeclInfo(String cmdName, String cmdText, String soyDoc) {
            this.cmdName = cmdName;
            this.cmdText = cmdText;
            this.soyDoc = soyDoc;
        }
    }
}

