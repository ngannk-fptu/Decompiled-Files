/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soyparse;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.base.internal.IncrementingIdGenerator;
import com.google.template.soy.base.internal.SoyFileSupplier;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.parsepasses.CheckCallsVisitor;
import com.google.template.soy.parsepasses.CheckDelegatesVisitor;
import com.google.template.soy.parsepasses.CheckOverridesVisitor;
import com.google.template.soy.parsepasses.InferRequiredSyntaxVersionVisitor;
import com.google.template.soy.parsepasses.ReplaceHasDataFunctionVisitor;
import com.google.template.soy.parsepasses.RewriteGenderMsgsVisitor;
import com.google.template.soy.parsepasses.RewriteNullCoalescingOpVisitor;
import com.google.template.soy.parsepasses.RewriteRemainderNodesVisitor;
import com.google.template.soy.parsepasses.SetDefaultForDelcallAllowsEmptyDefaultVisitor;
import com.google.template.soy.parsepasses.SetFullCalleeNamesVisitor;
import com.google.template.soy.parsepasses.VerifyPhnameAttrOnlyOnPlaceholdersVisitor;
import com.google.template.soy.shared.SoyAstCache;
import com.google.template.soy.sharedpasses.CheckCallingParamTypesVisitor;
import com.google.template.soy.sharedpasses.CheckSoyDocVisitor;
import com.google.template.soy.sharedpasses.RemoveHtmlCommentsVisitor;
import com.google.template.soy.sharedpasses.ReportSyntaxVersionErrorsVisitor;
import com.google.template.soy.sharedpasses.ResolveExpressionTypesVisitor;
import com.google.template.soy.sharedpasses.ResolveNamesVisitor;
import com.google.template.soy.soyparse.ParseException;
import com.google.template.soy.soyparse.SoyFileParser;
import com.google.template.soy.soyparse.TokenMgrError;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.types.SoyTypeRegistry;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;

public class SoyFileSetParser {
    private final SoyTypeRegistry typeRegistry;
    private SoyAstCache cache;
    private SyntaxVersion declaredSyntaxVersion;
    private final List<SoyFileSupplier> soyFileSuppliers;
    private boolean doRunInitialParsingPasses;
    private boolean doRunCheckingPasses;
    private boolean doCheckOverrides;

    public SoyFileSetParser(SoyTypeRegistry typeRegistry, @Nullable SoyAstCache astCache, SyntaxVersion declaredSyntaxVersion, SoyFileSupplier ... soyFileSuppliers) {
        this(typeRegistry, astCache, declaredSyntaxVersion, Arrays.asList(soyFileSuppliers));
    }

    public SoyFileSetParser(SoyTypeRegistry typeRegistry, @Nullable SoyAstCache astCache, SyntaxVersion declaredSyntaxVersion, List<SoyFileSupplier> soyFileSuppliers) {
        this.typeRegistry = typeRegistry;
        this.cache = astCache;
        this.declaredSyntaxVersion = declaredSyntaxVersion;
        this.soyFileSuppliers = soyFileSuppliers;
        SoyFileSetParser.verifyUniquePaths(soyFileSuppliers);
        this.doRunInitialParsingPasses = true;
        this.doRunCheckingPasses = true;
        this.doCheckOverrides = true;
    }

    public SoyFileSetParser setDoRunInitialParsingPasses(boolean doRunInitialParsingPasses) {
        this.doRunInitialParsingPasses = doRunInitialParsingPasses;
        if (!doRunInitialParsingPasses) {
            this.doRunCheckingPasses = false;
            this.doCheckOverrides = false;
        }
        return this;
    }

    public SoyFileSetParser setDoRunCheckingPasses(boolean doRunCheckingPasses) {
        this.doRunCheckingPasses = doRunCheckingPasses;
        if (doRunCheckingPasses) {
            Preconditions.checkState((boolean)this.doRunInitialParsingPasses);
        } else {
            this.doCheckOverrides = false;
        }
        return this;
    }

    public SoyFileSetParser setDoCheckOverrides(boolean doCheckOverrides) {
        this.doCheckOverrides = doCheckOverrides;
        if (doCheckOverrides) {
            Preconditions.checkState((boolean)this.doRunCheckingPasses);
        }
        return this;
    }

    public SoyFileSetNode parse() throws SoySyntaxException {
        return (SoyFileSetNode)this.parseWithVersions().first;
    }

    private static void verifyUniquePaths(Iterable<SoyFileSupplier> soyFileSuppliers) {
        HashSet paths = Sets.newHashSet();
        for (SoyFileSupplier supplier : soyFileSuppliers) {
            Preconditions.checkArgument((!paths.contains(supplier.getFilePath()) ? 1 : 0) != 0, (Object)("Two file suppliers have the same path: " + supplier.getFilePath()));
            paths.add(supplier.getFilePath());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Pair<SoyFileSetNode, List<SoyFileSupplier.Version>> parseWithVersions() throws SoySyntaxException {
        Preconditions.checkState((this.cache == null || this.doRunInitialParsingPasses && this.doRunCheckingPasses ? 1 : 0) != 0, (Object)"AST caching is only allowed when all parsing and checking passes are enabled, to avoid caching inconsistent versions");
        IdGenerator nodeIdGen = this.cache != null ? this.cache.getNodeIdGenerator() : new IncrementingIdGenerator();
        SoyFileSetNode soyTree = new SoyFileSetNode(nodeIdGen.genId(), nodeIdGen);
        ImmutableList.Builder versions = ImmutableList.builder();
        for (SoyFileSupplier soyFileSupplier : this.soyFileSuppliers) {
            Pair<SoyFileNode, SoyFileSupplier.Version> fileAndVersion;
            Pair<SoyFileNode, SoyFileSupplier.Version> pair = fileAndVersion = this.cache != null ? this.cache.get(soyFileSupplier) : null;
            if (fileAndVersion == null) {
                IdGenerator idGenerator = nodeIdGen;
                synchronized (idGenerator) {
                    fileAndVersion = SoyFileSetParser.parseSoyFileHelper(soyFileSupplier, nodeIdGen, this.typeRegistry);
                    if (this.doRunInitialParsingPasses) {
                        this.runSingleFileParsingPasses((SoyFileNode)fileAndVersion.first, nodeIdGen);
                    }
                }
                if (this.doRunCheckingPasses) {
                    this.runSingleFileCheckingPasses((SoyFileNode)fileAndVersion.first);
                }
                if (this.cache != null) {
                    this.cache.put(soyFileSupplier, (SoyFileSupplier.Version)fileAndVersion.second, (SoyFileNode)fileAndVersion.first);
                }
            }
            soyTree.addChild((SoyNode)fileAndVersion.first);
            versions.add(fileAndVersion.second);
        }
        if (this.doRunCheckingPasses) {
            this.runWholeFileSetCheckingPasses(soyTree);
        }
        return Pair.of(soyTree, versions.build());
    }

    private static Pair<SoyFileNode, SoyFileSupplier.Version> parseSoyFileHelper(SoyFileSupplier soyFileSupplier, IdGenerator nodeIdGen, SoyTypeRegistry typeRegistry) throws SoySyntaxException {
        SoyFileSupplier.Version version;
        Reader soyFileReader;
        String filePath = soyFileSupplier.getFilePath();
        try {
            Pair<Reader, SoyFileSupplier.Version> readerAndVersion = soyFileSupplier.open();
            soyFileReader = (Reader)readerAndVersion.first;
            version = (SoyFileSupplier.Version)readerAndVersion.second;
        }
        catch (IOException ioe) {
            throw SoySyntaxException.createWithoutMetaInfo("Error opening Soy file " + filePath + ": " + ioe);
        }
        try {
            SoyFileNode soyFile = new SoyFileParser(typeRegistry, nodeIdGen, soyFileReader, soyFileSupplier.getSoyFileKind(), filePath).parseSoyFile();
            if (soyFileSupplier.hasChangedSince(version)) {
                throw SoySyntaxException.createWithoutMetaInfo("Version skew in Soy file " + filePath);
            }
            Pair<SoyFileNode, SoyFileSupplier.Version> pair = Pair.of(soyFile, version);
            return pair;
        }
        catch (TokenMgrError tme) {
            throw SoySyntaxException.createCausedWithMetaInfo(null, tme, null, soyFileSupplier.getFilePath(), null);
        }
        catch (ParseException pe) {
            throw SoySyntaxException.createCausedWithMetaInfo(null, pe, null, soyFileSupplier.getFilePath(), null);
        }
        catch (SoySyntaxException sse) {
            throw sse.associateMetaInfo(null, soyFileSupplier.getFilePath(), null);
        }
        finally {
            try {
                soyFileReader.close();
            }
            catch (IOException ioe) {
                throw SoySyntaxException.createWithoutMetaInfo("Error closing Soy file " + soyFileSupplier.getFilePath() + ": " + ioe);
            }
        }
    }

    private void runSingleFileParsingPasses(SoyFileNode fileNode, IdGenerator nodeIdGen) {
        new RewriteGenderMsgsVisitor(nodeIdGen).exec(fileNode);
        new RewriteRemainderNodesVisitor().exec(fileNode);
        new ReplaceHasDataFunctionVisitor(this.declaredSyntaxVersion).exec(fileNode);
        new RewriteNullCoalescingOpVisitor().exec(fileNode);
        new SetFullCalleeNamesVisitor().exec(fileNode);
        new SetDefaultForDelcallAllowsEmptyDefaultVisitor(this.declaredSyntaxVersion).exec(fileNode);
        if (this.declaredSyntaxVersion == SyntaxVersion.V1_0) {
            new RemoveHtmlCommentsVisitor(nodeIdGen).exec(fileNode);
        }
        new ResolveNamesVisitor(this.declaredSyntaxVersion).exec(fileNode);
        new ResolveExpressionTypesVisitor(this.typeRegistry, this.declaredSyntaxVersion).exec(fileNode);
    }

    private void runSingleFileCheckingPasses(SoyFileNode fileNode) {
        new VerifyPhnameAttrOnlyOnPlaceholdersVisitor().exec(fileNode);
        new ReportSyntaxVersionErrorsVisitor(this.declaredSyntaxVersion, true).exec(fileNode);
        SyntaxVersion inferredSyntaxVersion = new InferRequiredSyntaxVersionVisitor().exec(fileNode);
        if (inferredSyntaxVersion.num > this.declaredSyntaxVersion.num) {
            new ReportSyntaxVersionErrorsVisitor(inferredSyntaxVersion, false).exec(fileNode);
        }
    }

    private void runWholeFileSetCheckingPasses(SoyFileSetNode soyTree) {
        new CheckSoyDocVisitor(this.declaredSyntaxVersion).exec(soyTree);
        if (this.doCheckOverrides) {
            new CheckOverridesVisitor().exec(soyTree);
        }
        new CheckDelegatesVisitor().exec(soyTree);
        new CheckCallsVisitor().exec(soyTree);
        new CheckCallingParamTypesVisitor().exec(soyTree);
    }
}

