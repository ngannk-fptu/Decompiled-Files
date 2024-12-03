/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  com.google.common.io.Files
 *  com.google.inject.Inject
 *  com.google.inject.Key
 *  com.google.inject.Provider
 *  javax.annotation.Nullable
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.internal.i18n.SoyBidiUtils;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.internal.GenJsCodeVisitor;
import com.google.template.soy.jssrc.internal.MoveGoogMsgDefNodesEarlierVisitor;
import com.google.template.soy.jssrc.internal.OptimizeBidiCodeGenVisitor;
import com.google.template.soy.jssrc.internal.ReplaceMsgsWithGoogMsgsVisitor;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.internal.InsertMsgsVisitor;
import com.google.template.soy.shared.internal.ApiCallScopeUtils;
import com.google.template.soy.shared.internal.GuiceSimpleScope;
import com.google.template.soy.shared.internal.MainEntryPointUtils;
import com.google.template.soy.shared.restricted.ApiCallScopeBindingAnnotations;
import com.google.template.soy.sharedpasses.IsUsingIjDataVisitor;
import com.google.template.soy.sharedpasses.opti.SimplifyVisitor;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class JsSrcMain {
    private final GuiceSimpleScope apiCallScope;
    private final SimplifyVisitor simplifyVisitor;
    private final Provider<OptimizeBidiCodeGenVisitor> optimizeBidiCodeGenVisitorProvider;
    private final Provider<GenJsCodeVisitor> genJsCodeVisitorProvider;

    @Inject
    public JsSrcMain(@ApiCallScopeBindingAnnotations.ApiCall GuiceSimpleScope apiCallScope, SimplifyVisitor simplifyVisitor, Provider<OptimizeBidiCodeGenVisitor> optimizeBidiCodeGenVisitorProvider, Provider<GenJsCodeVisitor> genJsCodeVisitorProvider) {
        this.apiCallScope = apiCallScope;
        this.simplifyVisitor = simplifyVisitor;
        this.optimizeBidiCodeGenVisitorProvider = optimizeBidiCodeGenVisitorProvider;
        this.genJsCodeVisitorProvider = genJsCodeVisitorProvider;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> genJsSrc(SoyFileSetNode soyTree, SoyJsSrcOptions jsSrcOptions, @Nullable SoyMsgBundle msgBundle) throws SoySyntaxException {
        boolean isUsingIjData = jsSrcOptions.isUsingIjData() || new IsUsingIjDataVisitor().exec(soyTree);
        Preconditions.checkState((!jsSrcOptions.getUseGoogIsRtlForBidiGlobalDir() || jsSrcOptions.shouldProvideRequireSoyNamespaces() || jsSrcOptions.shouldProvideRequireJsFunctions() ? 1 : 0) != 0, (Object)"Do not specify useGoogIsRtlForBidiGlobalDir without either shouldProvideRequireSoyNamespaces or shouldProvideRequireJsFunctions.");
        this.apiCallScope.enter();
        try {
            this.apiCallScope.seed(SoyJsSrcOptions.class, jsSrcOptions);
            this.apiCallScope.seed(Key.get(Boolean.class, ApiCallScopeBindingAnnotations.IsUsingIjData.class), Boolean.valueOf(isUsingIjData));
            BidiGlobalDir bidiGlobalDir = SoyBidiUtils.decodeBidiGlobalDirFromOptions(jsSrcOptions.getBidiGlobalDir(), jsSrcOptions.getUseGoogIsRtlForBidiGlobalDir());
            ApiCallScopeUtils.seedSharedParams(this.apiCallScope, msgBundle, bidiGlobalDir);
            if (jsSrcOptions.shouldGenerateGoogMsgDefs()) {
                new ReplaceMsgsWithGoogMsgsVisitor().exec(soyTree);
                new MoveGoogMsgDefNodesEarlierVisitor().exec(soyTree);
                Preconditions.checkState((bidiGlobalDir != null ? 1 : 0) != 0, (Object)"If enabling shouldGenerateGoogMsgDefs, must also set bidi global directionality.");
            } else {
                Preconditions.checkState((bidiGlobalDir == null || bidiGlobalDir.isStaticValue() ? 1 : 0) != 0, (Object)"If using bidiGlobalIsRtlCodeSnippet, must also enable shouldGenerateGoogMsgDefs.");
                try {
                    new InsertMsgsVisitor(msgBundle, false).exec(soyTree);
                }
                catch (InsertMsgsVisitor.EncounteredPlrselMsgException e) {
                    throw SoySyntaxExceptionUtils.createWithNode("JS code generation currently only supports plural/select messages when shouldGenerateGoogMsgDefs is true.", e.msgNode);
                }
            }
            ((OptimizeBidiCodeGenVisitor)this.optimizeBidiCodeGenVisitorProvider.get()).exec(soyTree);
            this.simplifyVisitor.exec(soyTree);
            List<String> list = ((GenJsCodeVisitor)this.genJsCodeVisitorProvider.get()).exec(soyTree);
            return list;
        }
        finally {
            this.apiCallScope.exit();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void genJsFiles(SoyFileSetNode soyTree, SoyJsSrcOptions jsSrcOptions, @Nullable String locale, @Nullable SoyMsgBundle msgBundle, String outputPathFormat, String inputPathsPrefix) throws SoySyntaxException, IOException {
        List<String> jsFileContents = this.genJsSrc(soyTree, jsSrcOptions, msgBundle);
        ImmutableList srcsToCompile = ImmutableList.copyOf((Iterable)Iterables.filter(soyTree.getChildren(), SoyFileNode.MATCH_SRC_FILENODE));
        if (srcsToCompile.size() != jsFileContents.size()) {
            throw new AssertionError((Object)String.format("Expected to generate %d code chunk(s), got %d", srcsToCompile.size(), jsFileContents.size()));
        }
        Multimap<String, Integer> outputs = this.mapOutputsToSrcs(locale, outputPathFormat, inputPathsPrefix, (ImmutableList<SoyFileNode>)srcsToCompile);
        for (String outputFilePath : outputs.keySet()) {
            try (BufferedWriter out = Files.newWriter((File)new File(outputFilePath), (Charset)Charsets.UTF_8);){
                boolean isFirst = true;
                Iterator iterator = outputs.get((Object)outputFilePath).iterator();
                while (iterator.hasNext()) {
                    int inputFileIndex = (Integer)iterator.next();
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        out.write("\n;\n");
                    }
                    out.write(jsFileContents.get(inputFileIndex));
                }
            }
        }
    }

    private Multimap<String, Integer> mapOutputsToSrcs(String locale, String outputPathFormat, String inputPathsPrefix, ImmutableList<SoyFileNode> fileNodes) {
        ArrayListMultimap outputs = ArrayListMultimap.create();
        for (int i = 0; i < fileNodes.size(); ++i) {
            SoyFileNode inputFile = (SoyFileNode)fileNodes.get(i);
            String inputFilePath = inputFile.getFilePath();
            String outputFilePath = MainEntryPointUtils.buildFilePath(outputPathFormat, locale, inputFilePath, inputPathsPrefix);
            BaseUtils.ensureDirsExistInPath(outputFilePath);
            outputs.put((Object)outputFilePath, (Object)i);
        }
        return outputs;
    }
}

