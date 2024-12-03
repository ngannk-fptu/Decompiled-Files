/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSortedSet
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.google.template.soy.tofu.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.data.internalutils.NodeContentKinds;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.internal.InsertMsgsVisitor;
import com.google.template.soy.parseinfo.SoyTemplateInfo;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import com.google.template.soy.shared.internal.ApiCallScopeUtils;
import com.google.template.soy.shared.internal.GuiceSimpleScope;
import com.google.template.soy.shared.restricted.ApiCallScopeBindingAnnotations;
import com.google.template.soy.sharedpasses.FindIjParamsVisitor;
import com.google.template.soy.sharedpasses.MarkLocalVarDataRefsVisitor;
import com.google.template.soy.sharedpasses.RenameCssVisitor;
import com.google.template.soy.sharedpasses.opti.SimplifyVisitor;
import com.google.template.soy.sharedpasses.render.RenderException;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.SoyTofuException;
import com.google.template.soy.tofu.internal.MarkParentNodesNeedingEnvFramesVisitor;
import com.google.template.soy.tofu.internal.NamespacedTofu;
import com.google.template.soy.tofu.internal.TofuRenderVisitor;
import com.google.template.soy.tofu.internal.TofuRenderVisitorFactory;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class BaseTofu
implements SoyTofu {
    private final SoyValueHelper valueHelper;
    private final GuiceSimpleScope apiCallScope;
    private final TofuRenderVisitorFactory tofuRenderVisitorFactory;
    private final SimplifyVisitor simplifyVisitor;
    private final SoyFileSetNode soyTree;
    private final boolean isCaching;
    private final Map<Pair<SoyMsgBundle, SoyCssRenamingMap>, TemplateRegistry> cachedTemplateRegistries;
    private final TemplateRegistry templateRegistryForNoCaching;
    private final ImmutableMap<TemplateNode, FindIjParamsVisitor.IjParamsInfo> templateToIjParamsInfoMap;

    @AssistedInject
    public BaseTofu(SoyValueHelper valueHelper, @ApiCallScopeBindingAnnotations.ApiCall GuiceSimpleScope apiCallScope, TofuRenderVisitorFactory tofuRenderVisitorFactory, SimplifyVisitor simplifyVisitor, @Assisted SoyFileSetNode soyTree, @Assisted boolean isCaching) {
        this.valueHelper = valueHelper;
        this.apiCallScope = apiCallScope;
        this.tofuRenderVisitorFactory = tofuRenderVisitorFactory;
        this.simplifyVisitor = simplifyVisitor;
        this.soyTree = soyTree;
        this.isCaching = isCaching;
        if (isCaching) {
            this.cachedTemplateRegistries = Maps.newHashMap();
            this.addToCache(null, null);
        } else {
            this.cachedTemplateRegistries = null;
        }
        SoyFileSetNode soyTreeForNoCaching = soyTree.clone();
        this.templateRegistryForNoCaching = this.buildTemplateRegistry(soyTreeForNoCaching);
        this.templateToIjParamsInfoMap = new FindIjParamsVisitor(this.templateRegistryForNoCaching).execOnAllTemplates(soyTreeForNoCaching);
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public SoyTofu forNamespace(@Nullable String namespace) {
        return namespace == null ? this : new NamespacedTofu(this, namespace);
    }

    @Override
    public boolean isCaching() {
        return this.isCaching;
    }

    @Override
    public void addToCache(@Nullable SoyMsgBundle msgBundle, @Nullable SoyCssRenamingMap cssRenamingMap) {
        if (!this.isCaching) {
            throw new SoyTofuException("Cannot addToCache() when isCaching is false.");
        }
        this.apiCallScope.enter();
        try {
            ApiCallScopeUtils.seedSharedParams(this.apiCallScope, msgBundle, 0);
            this.getCachedTemplateRegistry(Pair.of(msgBundle, cssRenamingMap), true);
        }
        finally {
            this.apiCallScope.exit();
        }
    }

    @Override
    public SoyTofu.Renderer newRenderer(SoyTemplateInfo templateInfo) {
        return new RendererImpl(this, templateInfo.getName());
    }

    @Override
    public SoyTofu.Renderer newRenderer(String templateName) {
        return new RendererImpl(this, templateName);
    }

    @Override
    public ImmutableSortedSet<String> getUsedIjParamsForTemplate(SoyTemplateInfo templateInfo) {
        return this.getUsedIjParamsForTemplate(templateInfo.getName());
    }

    @Override
    public ImmutableSortedSet<String> getUsedIjParamsForTemplate(String templateName) {
        TemplateBasicNode template = this.templateRegistryForNoCaching.getBasicTemplate(templateName);
        if (template == null) {
            throw new SoyTofuException("Template '" + templateName + "' not found.");
        }
        FindIjParamsVisitor.IjParamsInfo ijParamsInfo = (FindIjParamsVisitor.IjParamsInfo)this.templateToIjParamsInfoMap.get((Object)template);
        return ijParamsInfo.ijParamSet;
    }

    private TemplateRegistry buildTemplateRegistry(SoyFileSetNode soyTree) {
        new MarkParentNodesNeedingEnvFramesVisitor().exec(soyTree);
        new MarkLocalVarDataRefsVisitor().exec(soyTree);
        return new TemplateRegistry(soyTree);
    }

    private TemplateRegistry getCachedTemplateRegistry(Pair<SoyMsgBundle, SoyCssRenamingMap> key, boolean doAddToCache) {
        Preconditions.checkState((boolean)this.apiCallScope.isActive());
        TemplateRegistry templateRegistry = this.cachedTemplateRegistries.get(key);
        if (templateRegistry == null) {
            if (!doAddToCache) {
                return null;
            }
            SoyFileSetNode soyTreeClone = this.soyTree.clone();
            new InsertMsgsVisitor((SoyMsgBundle)key.first, true).exec(soyTreeClone);
            new RenameCssVisitor((SoyCssRenamingMap)key.second).exec(soyTreeClone);
            this.simplifyVisitor.exec(soyTreeClone);
            templateRegistry = this.buildTemplateRegistry(soyTreeClone);
            this.cachedTemplateRegistries.put(key, templateRegistry);
        }
        return templateRegistry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private TemplateNode renderMain(Appendable outputBuf, String templateName, @Nullable SoyRecord data, @Nullable SoyRecord ijData, @Nullable Set<String> activeDelPackageNames, @Nullable SoyMsgBundle msgBundle, @Nullable SoyIdRenamingMap idRenamingMap, @Nullable SoyCssRenamingMap cssRenamingMap, boolean doAddToCache) {
        if (activeDelPackageNames == null) {
            activeDelPackageNames = Collections.emptySet();
        }
        this.apiCallScope.enter();
        try {
            TemplateRegistry cachedTemplateRegistry;
            ApiCallScopeUtils.seedSharedParams(this.apiCallScope, msgBundle, 0);
            TemplateRegistry templateRegistry = cachedTemplateRegistry = this.isCaching ? this.getCachedTemplateRegistry(Pair.of(msgBundle, cssRenamingMap), doAddToCache) : null;
            if (cachedTemplateRegistry != null) {
                TemplateNode templateNode = this.renderMainHelper(cachedTemplateRegistry, outputBuf, templateName, data, ijData, activeDelPackageNames, msgBundle, null, null);
                return templateNode;
            }
            TemplateNode templateNode = this.renderMainHelper(this.templateRegistryForNoCaching, outputBuf, templateName, data, ijData, activeDelPackageNames, msgBundle, idRenamingMap, cssRenamingMap);
            return templateNode;
        }
        finally {
            this.apiCallScope.exit();
        }
    }

    private TemplateNode renderMainHelper(TemplateRegistry templateRegistry, Appendable outputBuf, String templateName, @Nullable SoyRecord data, @Nullable SoyRecord ijData, Set<String> activeDelPackageNames, @Nullable SoyMsgBundle msgBundle, @Nullable SoyIdRenamingMap idRenamingMap, @Nullable SoyCssRenamingMap cssRenamingMap) {
        TemplateBasicNode template = templateRegistry.getBasicTemplate(templateName);
        if (template == null) {
            throw new SoyTofuException("Attempting to render undefined template '" + templateName + "'.");
        }
        if (data == null) {
            data = SoyValueHelper.EMPTY_DICT;
        }
        try {
            TofuRenderVisitor rv = this.tofuRenderVisitorFactory.create(outputBuf, templateRegistry, data, ijData, null, activeDelPackageNames, msgBundle, idRenamingMap, cssRenamingMap);
            rv.exec(template);
        }
        catch (RenderException re) {
            throw new SoyTofuException(re);
        }
        return template;
    }

    @Override
    @Deprecated
    public String render(SoyTemplateInfo templateInfo, @Nullable Map<String, ?> data, @Nullable SoyMsgBundle msgBundle) {
        return new RendererImpl(this, templateInfo.getName()).setData(data).setMsgBundle(msgBundle).render();
    }

    @Override
    @Deprecated
    public String render(SoyTemplateInfo templateInfo, @Nullable SoyRecord data, @Nullable SoyMsgBundle msgBundle) {
        return new RendererImpl(this, templateInfo.getName()).setData(data).setMsgBundle(msgBundle).render();
    }

    @Override
    @Deprecated
    public String render(String templateName, @Nullable Map<String, ?> data, @Nullable SoyMsgBundle msgBundle) {
        return new RendererImpl(this, templateName).setData(data).setMsgBundle(msgBundle).render();
    }

    @Override
    @Deprecated
    public String render(String templateName, @Nullable SoyRecord data, @Nullable SoyMsgBundle msgBundle) {
        return new RendererImpl(this, templateName).setData(data).setMsgBundle(msgBundle).render();
    }

    private static class RendererImpl
    implements SoyTofu.Renderer {
        private final BaseTofu baseTofu;
        private final String templateName;
        private SoyRecord data;
        private SoyRecord ijData;
        private SoyMsgBundle msgBundle;
        private SoyIdRenamingMap idRenamingMap;
        private SoyCssRenamingMap cssRenamingMap;
        private Set<String> activeDelPackageNames;
        private boolean doAddToCache;
        private SanitizedContent.ContentKind expectedContentKind;
        private boolean contentKindExplicitlySet;

        public RendererImpl(BaseTofu baseTofu, String templateName) {
            this.baseTofu = baseTofu;
            this.templateName = templateName;
            this.data = null;
            this.ijData = null;
            this.activeDelPackageNames = null;
            this.msgBundle = null;
            this.cssRenamingMap = null;
            this.idRenamingMap = null;
            this.doAddToCache = true;
            this.expectedContentKind = SanitizedContent.ContentKind.HTML;
            this.contentKindExplicitlySet = false;
        }

        @Override
        public SoyTofu.Renderer setData(Map<String, ?> data) {
            this.data = data == null ? null : this.baseTofu.valueHelper.newEasyDictFromJavaStringMap(data);
            return this;
        }

        @Override
        public SoyTofu.Renderer setData(SoyRecord data) {
            this.data = data;
            return this;
        }

        @Override
        public SoyTofu.Renderer setIjData(Map<String, ?> ijData) {
            this.ijData = ijData == null ? null : this.baseTofu.valueHelper.newEasyDictFromJavaStringMap(ijData);
            return this;
        }

        @Override
        public SoyTofu.Renderer setIjData(SoyRecord ijData) {
            this.ijData = ijData;
            return this;
        }

        @Override
        public SoyTofu.Renderer setActiveDelegatePackageNames(Set<String> activeDelegatePackageNames) {
            this.activeDelPackageNames = activeDelegatePackageNames;
            return this;
        }

        @Override
        public SoyTofu.Renderer setMsgBundle(SoyMsgBundle msgBundle) {
            this.msgBundle = msgBundle;
            return this;
        }

        @Override
        public SoyTofu.Renderer setIdRenamingMap(SoyIdRenamingMap idRenamingMap) {
            this.idRenamingMap = idRenamingMap;
            return this;
        }

        @Override
        public SoyTofu.Renderer setCssRenamingMap(SoyCssRenamingMap cssRenamingMap) {
            this.cssRenamingMap = cssRenamingMap;
            return this;
        }

        @Override
        public SoyTofu.Renderer setDontAddToCache(boolean dontAddToCache) {
            this.doAddToCache = !dontAddToCache;
            return this;
        }

        @Override
        public SoyTofu.Renderer setContentKind(SanitizedContent.ContentKind contentKind) {
            this.expectedContentKind = (SanitizedContent.ContentKind)((Object)Preconditions.checkNotNull((Object)((Object)contentKind)));
            this.contentKindExplicitlySet = true;
            return this;
        }

        @Override
        public String render() {
            StringBuilder sb = new StringBuilder();
            this.render(sb);
            return sb.toString();
        }

        @Override
        public void render(Appendable out) {
            TemplateNode template = this.baseTofu.renderMain(out, this.templateName, this.data, this.ijData, this.activeDelPackageNames, this.msgBundle, this.idRenamingMap, this.cssRenamingMap, this.doAddToCache);
            if (this.contentKindExplicitlySet || template.getContentKind() != null) {
                this.enforceContentKind(template);
            }
        }

        @Override
        public SanitizedContent renderStrict() {
            StringBuilder sb = new StringBuilder();
            TemplateNode template = this.baseTofu.renderMain(sb, this.templateName, this.data, this.ijData, this.activeDelPackageNames, this.msgBundle, this.idRenamingMap, this.cssRenamingMap, this.doAddToCache);
            this.enforceContentKind(template);
            return UnsafeSanitizedContentOrdainer.ordainAsSafe(sb.toString(), this.expectedContentKind);
        }

        private void enforceContentKind(TemplateNode template) {
            if (this.expectedContentKind == SanitizedContent.ContentKind.TEXT) {
                return;
            }
            if (template.getContentKind() == null) {
                throw new SoyTofuException("Expected template to be autoescape=\"strict\" but was autoescape=\"" + template.getAutoescapeMode().getAttributeValue() + "\": " + template.getTemplateName());
            }
            if (this.expectedContentKind != template.getContentKind()) {
                throw new SoyTofuException("Expected template to be kind=\"" + NodeContentKinds.toAttributeValue(this.expectedContentKind) + "\" but was kind=\"" + NodeContentKinds.toAttributeValue(template.getContentKind()) + "\": " + template.getTemplateName());
            }
        }
    }

    public static interface BaseTofuFactory {
        public BaseTofu create(SoyFileSetNode var1, boolean var2);
    }
}

