/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.CharSource
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.google.template.soy;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharSource;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.util.Providers;
import com.google.template.soy.GuiceInitializer;
import com.google.template.soy.SoyModule;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.base.internal.SoyFileSupplier;
import com.google.template.soy.base.internal.VolatileSoyFileSupplier;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.data.restricted.PrimitiveData;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.internal.JsSrcMain;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.msgs.internal.ExtractMsgsVisitor;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgBundleImpl;
import com.google.template.soy.parseinfo.passes.GenerateParseInfoVisitor;
import com.google.template.soy.parsepasses.ChangeCallsToPassAllDataVisitor;
import com.google.template.soy.parsepasses.CheckFunctionCallsVisitor;
import com.google.template.soy.parsepasses.HandleCssCommandVisitor;
import com.google.template.soy.parsepasses.PerformAutoescapeVisitor;
import com.google.template.soy.parsepasses.contextautoesc.CheckEscapingSanityVisitor;
import com.google.template.soy.parsepasses.contextautoesc.ContentSecurityPolicyPass;
import com.google.template.soy.parsepasses.contextautoesc.ContextualAutoescaper;
import com.google.template.soy.parsepasses.contextautoesc.DerivedTemplateUtils;
import com.google.template.soy.shared.SoyAstCache;
import com.google.template.soy.shared.SoyGeneralOptions;
import com.google.template.soy.shared.internal.MainEntryPointUtils;
import com.google.template.soy.sharedpasses.AssertNoExternalCallsVisitor;
import com.google.template.soy.sharedpasses.ClearSoyDocStringsVisitor;
import com.google.template.soy.sharedpasses.FindTransitiveDepTemplatesVisitor;
import com.google.template.soy.sharedpasses.SubstituteGlobalsVisitor;
import com.google.template.soy.sharedpasses.opti.SimplifyVisitor;
import com.google.template.soy.soyparse.SoyFileSetParser;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.SoyTofuOptions;
import com.google.template.soy.tofu.internal.BaseTofu;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.SoyTypeProvider;
import com.google.template.soy.types.SoyTypeRegistry;
import com.google.template.soy.types.primitive.UnknownType;
import com.google.template.soy.xliffmsgplugin.XliffMsgPlugin;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SoyFileSet {
    private static final Provider<SoyMsgBundleHandler> DEFAULT_SOY_MSG_BUNDLE_HANDLER_PROVIDER = Providers.of(new SoyMsgBundleHandler(new XliffMsgPlugin()));
    private Provider<SoyMsgBundleHandler> msgBundleHandlerProvider = DEFAULT_SOY_MSG_BUNDLE_HANDLER_PROVIDER;
    private final BaseTofu.BaseTofuFactory baseTofuFactory;
    private final Provider<JsSrcMain> jsSrcMainProvider;
    private final CheckFunctionCallsVisitor.CheckFunctionCallsVisitorFactory checkFunctionCallsVisitorFactory;
    private final PerformAutoescapeVisitor performAutoescapeVisitor;
    private final ContextualAutoescaper contextualAutoescaper;
    private final SimplifyVisitor simplifyVisitor;
    private final SoyTypeRegistry typeRegistry;
    private final List<SoyFileSupplier> soyFileSuppliers;
    private final SoyAstCache cache;
    private final SoyGeneralOptions generalOptions;
    private ImmutableSet<Long> memoizedExtractedMsgIdsForPruning;

    public static Builder builder() {
        Builder builder = new Builder();
        builder.setFactory(Guice.createInjector(new SoyModule()).getInstance(SoyFileSetFactory.class));
        return builder;
    }

    @Inject
    SoyFileSet(BaseTofu.BaseTofuFactory baseTofuFactory, Provider<JsSrcMain> jsSrcMainProvider, CheckFunctionCallsVisitor.CheckFunctionCallsVisitorFactory checkFunctionCallsVisitorFactory, PerformAutoescapeVisitor performAutoescapeVisitor, ContextualAutoescaper contextualAutoescaper, SimplifyVisitor simplifyVisitor, SoyTypeRegistry typeRegistry, @Assisted List<SoyFileSupplier> soyFileSuppliers, @Assisted SoyGeneralOptions generalOptions, @Assisted @Nullable SoyAstCache cache, @Assisted(value="localTypeRegistry") @Nullable SoyTypeRegistry localTypeRegistry) {
        this.baseTofuFactory = baseTofuFactory;
        this.jsSrcMainProvider = jsSrcMainProvider;
        this.checkFunctionCallsVisitorFactory = checkFunctionCallsVisitorFactory;
        this.performAutoescapeVisitor = performAutoescapeVisitor;
        this.contextualAutoescaper = contextualAutoescaper;
        this.simplifyVisitor = simplifyVisitor;
        Preconditions.checkArgument((soyFileSuppliers.size() > 0 ? 1 : 0) != 0, (Object)"Must have non-zero number of input Soy files.");
        this.typeRegistry = localTypeRegistry != null ? localTypeRegistry : typeRegistry;
        this.soyFileSuppliers = soyFileSuppliers;
        this.cache = cache;
        this.generalOptions = generalOptions.clone();
    }

    @Inject(optional=true)
    void setMsgBundleHandlerProvider(Provider<SoyMsgBundleHandler> msgBundleHandlerProvider) {
        this.msgBundleHandlerProvider = msgBundleHandlerProvider;
    }

    @VisibleForTesting
    List<SoyFileSupplier> getSoyFileSuppliersForTesting() {
        return this.soyFileSuppliers;
    }

    @VisibleForTesting
    SoyGeneralOptions getOptionsForTesting() {
        return this.generalOptions;
    }

    ImmutableMap<String, String> generateParseInfo(String javaPackage, String javaClassNameSource) throws SoySyntaxException {
        SyntaxVersion declaredSyntaxVersion = this.generalOptions.getDeclaredSyntaxVersion(SyntaxVersion.V2_0);
        SoyFileSetNode soyTree = new SoyFileSetParser(this.typeRegistry, this.cache, declaredSyntaxVersion, this.soyFileSuppliers).parse();
        return new GenerateParseInfoVisitor(javaPackage, javaClassNameSource).exec(soyTree);
    }

    public SoyMsgBundle extractMsgs() throws SoySyntaxException {
        SyntaxVersion declaredSyntaxVersion = this.generalOptions.getDeclaredSyntaxVersion(SyntaxVersion.V1_0);
        SoyTypeRegistry typeRegistry = this.createDummyTypeRegistry();
        SoyFileSetNode soyTree = new SoyFileSetParser(typeRegistry, this.cache, declaredSyntaxVersion, this.soyFileSuppliers).setDoCheckOverrides(false).parse();
        return new ExtractMsgsVisitor().exec(soyTree);
    }

    public SoyMsgBundle pruneTranslatedMsgs(SoyMsgBundle origTransMsgBundle) throws SoySyntaxException {
        if (this.memoizedExtractedMsgIdsForPruning == null) {
            SyntaxVersion declaredSyntaxVersion = this.generalOptions.getDeclaredSyntaxVersion(SyntaxVersion.V1_0);
            SoyTypeRegistry typeRegistry = this.createDummyTypeRegistry();
            SoyFileSetNode soyTree = new SoyFileSetParser(typeRegistry, this.cache, declaredSyntaxVersion, this.soyFileSuppliers).setDoCheckOverrides(false).parse();
            ArrayList allPublicTemplates = Lists.newArrayList();
            for (SoyFileNode soyFile : soyTree.getChildren()) {
                for (TemplateNode template : soyFile.getChildren()) {
                    if (template.isPrivate()) continue;
                    allPublicTemplates.add(template);
                }
            }
            ImmutableMap<TemplateNode, FindTransitiveDepTemplatesVisitor.TransitiveDepTemplatesInfo> depsInfoMap = new FindTransitiveDepTemplatesVisitor(null).execOnMultipleTemplates(allPublicTemplates);
            FindTransitiveDepTemplatesVisitor.TransitiveDepTemplatesInfo mergedDepsInfo = FindTransitiveDepTemplatesVisitor.TransitiveDepTemplatesInfo.merge(depsInfoMap.values());
            SoyMsgBundle extractedMsgBundle = new ExtractMsgsVisitor().execOnMultipleNodes((Iterable<? extends SoyNode>)mergedDepsInfo.depTemplateSet);
            ImmutableSet.Builder extractedMsgIdsBuilder = ImmutableSet.builder();
            for (SoyMsg extractedMsg : extractedMsgBundle) {
                extractedMsgIdsBuilder.add((Object)extractedMsg.getId());
            }
            this.memoizedExtractedMsgIdsForPruning = extractedMsgIdsBuilder.build();
        }
        ImmutableList.Builder prunedTransMsgsBuilder = ImmutableList.builder();
        for (SoyMsg transMsg : origTransMsgBundle) {
            if (!this.memoizedExtractedMsgIdsForPruning.contains((Object)transMsg.getId())) continue;
            prunedTransMsgsBuilder.add((Object)transMsg);
        }
        return new SoyMsgBundleImpl(origTransMsgBundle.getLocaleString(), (List<SoyMsg>)prunedTransMsgsBuilder.build());
    }

    public SoyTofu compileToTofu() throws SoySyntaxException {
        return this.compileToTofu(new SoyTofuOptions());
    }

    public SoyTofu compileToTofu(SoyTofuOptions tofuOptions) throws SoySyntaxException {
        tofuOptions = tofuOptions.clone();
        SyntaxVersion declaredSyntaxVersion = this.generalOptions.getDeclaredSyntaxVersion(SyntaxVersion.V2_0);
        SoyFileSetNode soyTree = new SoyFileSetParser(this.typeRegistry, this.cache, declaredSyntaxVersion, this.soyFileSuppliers).parse();
        this.runMiddleendPasses(soyTree, declaredSyntaxVersion);
        if (this.generalOptions.allowExternalCalls() == null) {
            // empty if block
        }
        new SubstituteGlobalsVisitor((Map<String, PrimitiveData>)this.generalOptions.getCompileTimeGlobals(), this.typeRegistry, true).exec(soyTree);
        new ClearSoyDocStringsVisitor().exec(soyTree);
        return this.baseTofuFactory.create(soyTree, tofuOptions.useCaching());
    }

    @Deprecated
    public SoyTofu compileToJavaObj() throws SoySyntaxException {
        return this.compileToTofu(new SoyTofuOptions());
    }

    @Deprecated
    public SoyTofu compileToJavaObj(boolean useCaching) throws SoySyntaxException {
        SoyTofuOptions options = new SoyTofuOptions();
        options.setUseCaching(useCaching);
        return this.compileToTofu(options);
    }

    public List<String> compileToJsSrc(SoyJsSrcOptions jsSrcOptions, @Nullable SoyMsgBundle msgBundle) throws SoySyntaxException {
        if (jsSrcOptions.shouldAllowDeprecatedSyntax()) {
            this.generalOptions.setDeclaredSyntaxVersionName("1.0");
        }
        SyntaxVersion declaredSyntaxVersion = this.generalOptions.getDeclaredSyntaxVersion(SyntaxVersion.V2_0);
        SoyFileSetNode soyTree = new SoyFileSetParser(this.typeRegistry, this.cache, declaredSyntaxVersion, this.soyFileSuppliers).parse();
        this.runMiddleendPasses(soyTree, declaredSyntaxVersion);
        return this.jsSrcMainProvider.get().genJsSrc(soyTree, jsSrcOptions, msgBundle);
    }

    void compileToJsSrcFiles(String outputPathFormat, String inputFilePathPrefix, SoyJsSrcOptions jsSrcOptions, List<String> locales, @Nullable String messageFilePathFormat) throws SoySyntaxException, IOException {
        if (jsSrcOptions.shouldAllowDeprecatedSyntax()) {
            this.generalOptions.setDeclaredSyntaxVersionName("1.0");
        }
        SyntaxVersion declaredSyntaxVersion = this.generalOptions.getDeclaredSyntaxVersion(SyntaxVersion.V2_0);
        SoyFileSetNode soyTree = new SoyFileSetParser(this.typeRegistry, this.cache, declaredSyntaxVersion, this.soyFileSuppliers).parse();
        this.runMiddleendPasses(soyTree, declaredSyntaxVersion);
        if (locales.size() == 0) {
            this.jsSrcMainProvider.get().genJsFiles(soyTree, jsSrcOptions, null, null, outputPathFormat, inputFilePathPrefix);
        } else {
            for (String locale : locales) {
                SoyFileSetNode soyTreeClone = soyTree.clone();
                String msgFilePath = MainEntryPointUtils.buildFilePath(messageFilePathFormat, locale, null, inputFilePathPrefix);
                SoyMsgBundle msgBundle = this.msgBundleHandlerProvider.get().createFromFile(new File(msgFilePath));
                if (msgBundle.getLocaleString() == null && !locale.startsWith("en")) {
                    throw new IOException("Error opening or reading message file " + msgFilePath);
                }
                this.jsSrcMainProvider.get().genJsFiles(soyTreeClone, jsSrcOptions, locale, msgBundle, outputPathFormat, inputFilePathPrefix);
            }
        }
    }

    private void runMiddleendPasses(SoyFileSetNode soyTree, SyntaxVersion declaredSyntaxVersion) throws SoySyntaxException {
        this.checkFunctionCallsVisitorFactory.create(declaredSyntaxVersion).exec(soyTree);
        if (this.generalOptions.allowExternalCalls() == Boolean.FALSE) {
            new AssertNoExternalCallsVisitor().exec(soyTree);
        }
        new HandleCssCommandVisitor(this.generalOptions.getCssHandlingScheme()).exec(soyTree);
        if (this.generalOptions.getCompileTimeGlobals() != null || this.typeRegistry != null) {
            new SubstituteGlobalsVisitor((Map<String, PrimitiveData>)this.generalOptions.getCompileTimeGlobals(), this.typeRegistry, false).exec(soyTree);
        }
        this.doContextualEscaping(soyTree);
        this.performAutoescapeVisitor.exec(soyTree);
        if (this.generalOptions.supportContentSecurityPolicy()) {
            ContentSecurityPolicyPass.blessAuthorSpecifiedScripts(this.contextualAutoescaper.getSlicedRawTextNodes());
        }
        new ChangeCallsToPassAllDataVisitor().exec(soyTree);
        this.simplifyVisitor.exec(soyTree);
    }

    private void doContextualEscaping(SoyFileSetNode soyTree) throws SoySyntaxException {
        new CheckEscapingSanityVisitor().exec(soyTree);
        List<TemplateNode> extraTemplates = this.contextualAutoescaper.rewrite(soyTree);
        if (!extraTemplates.isEmpty()) {
            HashMap containingFile = Maps.newHashMap();
            for (SoyFileNode fileNode : soyTree.getChildren()) {
                for (TemplateNode templateNode : fileNode.getChildren()) {
                    String name = templateNode instanceof TemplateDelegateNode ? ((TemplateDelegateNode)templateNode).getDelTemplateName() : templateNode.getTemplateName();
                    containingFile.put(DerivedTemplateUtils.getBaseName(name), fileNode);
                }
            }
            for (TemplateNode extraTemplate : extraTemplates) {
                String name = extraTemplate instanceof TemplateDelegateNode ? ((TemplateDelegateNode)extraTemplate).getDelTemplateName() : extraTemplate.getTemplateName();
                ((SoyFileNode)containingFile.get(DerivedTemplateUtils.getBaseName(name))).addChild(extraTemplate);
            }
        }
    }

    private SoyTypeRegistry createDummyTypeRegistry() {
        return new SoyTypeRegistry((Set<SoyTypeProvider>)ImmutableSet.of((Object)new SoyTypeProvider(){

            @Override
            public SoyType getType(String typeName, SoyTypeRegistry typeRegistry) {
                return UnknownType.getInstance();
            }
        }));
    }

    static interface SoyFileSetFactory {
        public SoyFileSet create(List<SoyFileSupplier> var1, SoyAstCache var2, SoyGeneralOptions var3, @Assisted(value="localTypeRegistry") SoyTypeRegistry var4);
    }

    public static final class Builder {
        private SoyFileSetFactory factory;
        private final ImmutableSet.Builder<SoyFileSupplier> setBuilder = ImmutableSet.builder();
        private SoyAstCache cache = null;
        private SoyGeneralOptions lazyGeneralOptions = null;
        private SoyTypeRegistry localTypeRegistry;

        @Inject
        @Deprecated
        public Builder() {
        }

        @Inject(optional=true)
        void setFactory(SoyFileSetFactory factory) {
            this.factory = factory;
        }

        public void setGeneralOptions(SoyGeneralOptions generalOptions) {
            Preconditions.checkState((this.lazyGeneralOptions == null ? 1 : 0) != 0, (Object)"Call SoyFileSet#setGeneralOptions before any other setters.");
            Preconditions.checkNotNull((Object)generalOptions, (Object)"Non-null argument expected.");
            this.lazyGeneralOptions = generalOptions.clone();
        }

        private SoyGeneralOptions getGeneralOptions() {
            if (this.lazyGeneralOptions == null) {
                this.lazyGeneralOptions = new SoyGeneralOptions();
            }
            return this.lazyGeneralOptions;
        }

        public SoyFileSet build() {
            if (this.factory == null) {
                this.factory = GuiceInitializer.getHackySoyFileSetFactory();
            }
            return this.factory.create((List<SoyFileSupplier>)ImmutableList.copyOf((Collection)this.setBuilder.build()), this.cache, this.getGeneralOptions(), this.localTypeRegistry);
        }

        public Builder addWithKind(CharSource contentSource, SoyFileKind soyFileKind, String filePath) {
            this.setBuilder.add((Object)SoyFileSupplier.Factory.create(contentSource, soyFileKind, filePath));
            return this;
        }

        public Builder add(CharSource contentSource, String filePath) {
            return this.addWithKind(contentSource, SoyFileKind.SRC, filePath);
        }

        public Builder addWithKind(File inputFile, SoyFileKind soyFileKind) {
            this.setBuilder.add((Object)SoyFileSupplier.Factory.create(inputFile, soyFileKind));
            return this;
        }

        public Builder add(File inputFile) {
            return this.addWithKind(inputFile, SoyFileKind.SRC);
        }

        public Builder addVolatileWithKind(File inputFile, SoyFileKind soyFileKind) {
            this.setBuilder.add((Object)new VolatileSoyFileSupplier(inputFile, soyFileKind));
            return this;
        }

        public Builder addVolatile(File inputFile) {
            return this.addVolatileWithKind(inputFile, SoyFileKind.SRC);
        }

        public Builder addWithKind(URL inputFileUrl, SoyFileKind soyFileKind, String filePath) {
            this.setBuilder.add((Object)SoyFileSupplier.Factory.create(inputFileUrl, soyFileKind, filePath));
            return this;
        }

        public Builder add(URL inputFileUrl, String filePath) {
            return this.addWithKind(inputFileUrl, SoyFileKind.SRC, filePath);
        }

        public Builder addWithKind(URL inputFileUrl, SoyFileKind soyFileKind) {
            this.setBuilder.add((Object)SoyFileSupplier.Factory.create(inputFileUrl, soyFileKind));
            return this;
        }

        public Builder add(URL inputFileUrl) {
            return this.addWithKind(inputFileUrl, SoyFileKind.SRC);
        }

        public Builder addWithKind(CharSequence content, SoyFileKind soyFileKind, String filePath) {
            this.setBuilder.add((Object)SoyFileSupplier.Factory.create(content, soyFileKind, filePath));
            return this;
        }

        public Builder add(CharSequence content, String filePath) {
            return this.addWithKind(content, SoyFileKind.SRC, filePath);
        }

        public Builder setSoyAstCache(SoyAstCache cache) {
            this.cache = cache;
            return this;
        }

        public Builder setDeclaredSyntaxVersionName(@Nonnull String versionName) {
            this.getGeneralOptions().setDeclaredSyntaxVersionName(versionName);
            return this;
        }

        public Builder setAllowExternalCalls(boolean allowExternalCalls) {
            this.getGeneralOptions().setAllowExternalCalls(allowExternalCalls);
            return this;
        }

        public Builder setCssHandlingScheme(SoyGeneralOptions.CssHandlingScheme cssHandlingScheme) {
            this.getGeneralOptions().setCssHandlingScheme(cssHandlingScheme);
            return this;
        }

        public Builder setCompileTimeGlobals(Map<String, ?> compileTimeGlobalsMap) {
            this.getGeneralOptions().setCompileTimeGlobals(compileTimeGlobalsMap);
            return this;
        }

        public Builder setCompileTimeGlobals(File compileTimeGlobalsFile) throws IOException {
            this.getGeneralOptions().setCompileTimeGlobals(compileTimeGlobalsFile);
            return this;
        }

        public Builder setCompileTimeGlobals(URL compileTimeGlobalsResource) throws IOException {
            this.getGeneralOptions().setCompileTimeGlobals(compileTimeGlobalsResource);
            return this;
        }

        public Builder setSupportContentSecurityPolicy(boolean supportContentSecurityPolicy) {
            this.getGeneralOptions().setSupportContentSecurityPolicy(supportContentSecurityPolicy);
            return this;
        }

        public Builder setLocalTypeRegistry(SoyTypeRegistry typeRegistry) {
            this.localTypeRegistry = typeRegistry;
            return this;
        }
    }
}

