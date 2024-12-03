/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.cdn.mapper;

import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.cdn.mapper.MappingParser;
import com.atlassian.plugin.webresource.cdn.mapper.MappingParserException;
import com.atlassian.plugin.webresource.cdn.mapper.MappingSet;
import com.atlassian.plugin.webresource.cdn.mapper.WebResourceMapper;
import com.atlassian.plugin.webresource.prebake.PrebakeConfig;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebResourceMapper
implements WebResourceMapper {
    private static final Logger log = LoggerFactory.getLogger(DefaultWebResourceMapper.class);
    private final MappingSet mappings;
    private final String ctCdnBaseUrl;
    private final String contextPath;
    private final WebResourceIntegration webResourceIntegration;

    public DefaultWebResourceMapper(@Nonnull WebResourceIntegration webResourceIntegration, @Nonnull MappingParser mappingParser, @Nonnull PrebakeConfig prebakeConfig, @Nonnull String globalStateHash, @Nonnull String ctCdnBaseUrl, @Nonnull String contextPath) throws MappingParserException, IOException {
        Preconditions.checkNotNull((Object)webResourceIntegration, (Object)"webResourceIntegration is null!");
        Preconditions.checkNotNull((Object)mappingParser, (Object)"mappingParser is null!");
        Preconditions.checkNotNull((Object)prebakeConfig, (Object)"prebakeConfig is null!");
        Preconditions.checkNotNull((Object)globalStateHash, (Object)"globalStateHash is null!");
        Preconditions.checkNotNull((Object)ctCdnBaseUrl, (Object)"ctCdnBaseUrl is null!");
        this.webResourceIntegration = webResourceIntegration;
        this.mappings = this.loadMappings(mappingParser, globalStateHash, prebakeConfig);
        this.ctCdnBaseUrl = ctCdnBaseUrl;
        this.contextPath = contextPath;
    }

    @Override
    @Nonnull
    public List<String> map(@Nonnull String resourceUrl) {
        if (!this.webResourceIntegration.isCtCdnMappingEnabled()) {
            return Collections.emptyList();
        }
        List<String> mappedResources = this.mappings.getMappedResources(this.removeContext(resourceUrl));
        if (!mappedResources.isEmpty()) {
            log.debug("Mapped resource {} to {}", (Object)resourceUrl, mappedResources);
        } else {
            log.debug("Cache miss for resource {}", (Object)resourceUrl);
        }
        return mappedResources.stream().map(this::joinBaseUrlSafely).collect(Collectors.toList());
    }

    private String removeContext(String resourceUrl) {
        return this.contextPath == null || this.contextPath.isEmpty() || !resourceUrl.startsWith(this.contextPath) ? resourceUrl : resourceUrl.substring(this.contextPath.length());
    }

    private String joinBaseUrlSafely(String resourcePath) {
        boolean baseUrlHasSlash = this.ctCdnBaseUrl.endsWith("/");
        boolean resourcePathHasSlash = resourcePath.startsWith("/");
        if (baseUrlHasSlash && resourcePathHasSlash) {
            return this.ctCdnBaseUrl.substring(0, this.ctCdnBaseUrl.length() - 1) + resourcePath;
        }
        if (!baseUrlHasSlash && !resourcePathHasSlash) {
            return this.ctCdnBaseUrl + "/" + resourcePath;
        }
        return this.ctCdnBaseUrl + resourcePath;
    }

    @Override
    @Nonnull
    public Optional<String> mapSingle(@Nonnull String resourceUrl) {
        return this.map(resourceUrl).stream().findFirst();
    }

    /*
     * Exception decompiling
     */
    private MappingSet loadMappings(MappingParser mappingParser, String globalStateHash, PrebakeConfig prebakeConfig) throws MappingParserException, IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    @Nonnull
    public MappingSet mappings() {
        return this.mappings;
    }
}

