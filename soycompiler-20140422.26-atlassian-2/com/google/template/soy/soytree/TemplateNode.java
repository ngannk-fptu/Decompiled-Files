/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.soytree.AbstractBlockCommandNode;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.defn.HeaderParam;
import com.google.template.soy.soytree.defn.TemplateParam;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

public abstract class TemplateNode
extends AbstractBlockCommandNode
implements SoyNode.RenderUnitNode {
    public static final int MAX_PRIORITY = 1;
    private final SoyFileHeaderInfo soyFileHeaderInfo;
    private final String templateName;
    @Nullable
    private final String partialTemplateName;
    private final String templateNameForUserMsgs;
    private final boolean isPrivate;
    private final AutoescapeMode autoescapeMode;
    @Nullable
    private final SanitizedContent.ContentKind contentKind;
    private final ImmutableList<String> requiredCssNamespaces;
    private String soyDoc;
    private String soyDocDesc;
    @Nullable
    private ImmutableList<TemplateParam> params;

    TemplateNode(int id, @Nullable SyntaxVersionBound syntaxVersionBound, String cmdName, String cmdText, SoyFileHeaderInfo soyFileHeaderInfo, String templateName, @Nullable String partialTemplateName, String templateNameForUserMsgs, boolean isPrivate, AutoescapeMode autoescapeMode, SanitizedContent.ContentKind contentKind, ImmutableList<String> requiredCssNamespaces, String soyDoc, String soyDocDesc, @Nullable ImmutableList<TemplateParam> params) {
        super(id, cmdName, cmdText);
        this.maybeSetSyntaxVersionBound(syntaxVersionBound);
        this.soyFileHeaderInfo = soyFileHeaderInfo;
        this.templateName = templateName;
        this.partialTemplateName = partialTemplateName;
        this.templateNameForUserMsgs = templateNameForUserMsgs;
        this.isPrivate = isPrivate;
        this.autoescapeMode = autoescapeMode;
        this.contentKind = contentKind;
        this.requiredCssNamespaces = requiredCssNamespaces;
        this.soyDoc = soyDoc;
        this.soyDocDesc = soyDocDesc;
        this.params = params;
    }

    protected TemplateNode(TemplateNode orig) {
        super(orig);
        this.soyFileHeaderInfo = orig.soyFileHeaderInfo;
        this.templateName = orig.templateName;
        this.partialTemplateName = orig.partialTemplateName;
        this.templateNameForUserMsgs = orig.templateNameForUserMsgs;
        this.isPrivate = orig.isPrivate;
        this.autoescapeMode = orig.autoescapeMode;
        this.contentKind = orig.contentKind;
        this.requiredCssNamespaces = orig.requiredCssNamespaces;
        this.soyDoc = orig.soyDoc;
        this.soyDocDesc = orig.soyDocDesc;
        this.params = orig.params;
    }

    public SoyFileHeaderInfo getSoyFileHeaderInfo() {
        return this.soyFileHeaderInfo;
    }

    public String getDelPackageName() {
        return this.soyFileHeaderInfo.delPackageName;
    }

    public String getTemplateNameForUserMsgs() {
        return this.templateNameForUserMsgs;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    @Nullable
    public String getPartialTemplateName() {
        return this.partialTemplateName;
    }

    public boolean isPrivate() {
        return this.isPrivate;
    }

    public AutoescapeMode getAutoescapeMode() {
        return this.autoescapeMode;
    }

    @Override
    @Nullable
    public SanitizedContent.ContentKind getContentKind() {
        return this.contentKind;
    }

    public ImmutableList<String> getRequiredCssNamespaces() {
        return this.requiredCssNamespaces;
    }

    public void clearSoyDocStrings() {
        this.soyDoc = null;
        this.soyDocDesc = null;
        assert (this.params != null);
        ArrayList newParams = Lists.newArrayListWithCapacity((int)this.params.size());
        for (TemplateParam origParam : this.params) {
            newParams.add(origParam.cloneEssential());
        }
        this.params = ImmutableList.copyOf((Collection)newParams);
    }

    public String getSoyDoc() {
        return this.soyDoc;
    }

    public String getSoyDocDesc() {
        return this.soyDocDesc;
    }

    @Nullable
    public List<TemplateParam> getParams() {
        return this.params;
    }

    @Override
    public SoyFileNode getParent() {
        return (SoyFileNode)super.getParent();
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        if (this.soyDoc != null) {
            sb.append(this.soyDoc).append("\n");
        }
        sb.append(this.getTagString()).append("\n");
        if (this.params != null) {
            for (TemplateParam param : this.params) {
                if (param.declLoc() != TemplateParam.DeclLoc.HEADER) continue;
                HeaderParam headerParam = (HeaderParam)param;
                sb.append("  {@param ").append(headerParam.name()).append(": ").append(headerParam.typeSrc()).append("}");
                if (headerParam.desc() != null) {
                    sb.append("  /** ").append(headerParam.desc()).append(" */");
                }
                sb.append("\n");
            }
        }
        StringBuilder bodySb = new StringBuilder();
        this.appendSourceStringForChildren(bodySb);
        int bodyLen = bodySb.length();
        if (bodyLen != 0) {
            if (bodyLen != 1 && bodySb.charAt(bodyLen - 1) == ' ') {
                bodySb.replace(bodyLen - 1, bodyLen, "{sp}");
            }
            if (bodySb.charAt(0) == ' ') {
                bodySb.replace(0, 1, "{sp}");
            }
        }
        sb.append((CharSequence)bodySb);
        sb.append("\n");
        sb.append("{/").append(this.getCommandName()).append("}\n");
        return sb.toString();
    }

    public StackTraceElement createStackTraceElement(SourceLocation srcLocation) {
        if (this.partialTemplateName == null) {
            return new StackTraceElement("(UnknownSoyNamespace)", this.templateName, srcLocation.getFileName(), srcLocation.getLineNumber());
        }
        return new StackTraceElement(this.soyFileHeaderInfo.namespace, this.partialTemplateName.substring(1), srcLocation.getFileName(), srcLocation.getLineNumber());
    }

    @Immutable
    public static class SoyFileHeaderInfo {
        @Nullable
        public final String delPackageName;
        public final int defaultDelPriority;
        @Nullable
        public final String namespace;
        public final AutoescapeMode defaultAutoescapeMode;

        public SoyFileHeaderInfo(SoyFileNode soyFileNode) {
            this(soyFileNode.getDelPackageName(), soyFileNode.getNamespace(), soyFileNode.getDefaultAutoescapeMode());
        }

        public SoyFileHeaderInfo(String namespace) {
            this(null, namespace, AutoescapeMode.TRUE);
        }

        public SoyFileHeaderInfo(@Nullable String delPackageName, String namespace, AutoescapeMode defaultAutoescapeMode) {
            this.delPackageName = delPackageName;
            this.defaultDelPriority = delPackageName == null ? 0 : 1;
            this.namespace = namespace;
            this.defaultAutoescapeMode = defaultAutoescapeMode;
        }
    }
}

