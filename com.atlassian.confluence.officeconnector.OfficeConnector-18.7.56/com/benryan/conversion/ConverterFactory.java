/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Service
 */
package com.benryan.conversion;

import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.benryan.components.HtmlCacheManager;
import com.benryan.conversion.Converter;
import com.benryan.conversion.DocConverter;
import com.benryan.conversion.PptConverter;
import com.benryan.conversion.SandboxConversionFeature;
import com.benryan.conversion.XlsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public final class ConverterFactory {
    private final HtmlCacheManager htmlCacheManager;
    private final RenderedContentCleaner renderedContentCleaner;
    private final Sandbox sandbox;
    private final SandboxConversionFeature sandboxConversionFeature;
    private final VelocityHelperService velocityHelperService;

    @Autowired
    public ConverterFactory(HtmlCacheManager htmlCacheManager, @ComponentImport RenderedContentCleaner renderedContentCleaner, @Qualifier(value="officeConnectorConversionSandbox") Sandbox sandbox, SandboxConversionFeature sandboxConversionFeature, @ComponentImport VelocityHelperService velocityHelperService) {
        this.htmlCacheManager = htmlCacheManager;
        this.renderedContentCleaner = renderedContentCleaner;
        this.sandbox = sandbox;
        this.sandboxConversionFeature = sandboxConversionFeature;
        this.velocityHelperService = velocityHelperService;
    }

    public Converter create(String typeName) throws MacroExecutionException {
        String fileType;
        switch (fileType = typeName.toLowerCase()) {
            case "pdf": 
            case "ppt": 
            case "pptx": {
                return new PptConverter(this.velocityHelperService);
            }
            case "doc": 
            case "docx": {
                return new DocConverter(this.htmlCacheManager, this.velocityHelperService, this.sandboxConversionFeature, this.sandbox);
            }
            case "xls": 
            case "xlsx": {
                return new XlsConverter(this.htmlCacheManager, this.velocityHelperService, this.sandboxConversionFeature, this.sandbox, this.renderedContentCleaner);
            }
        }
        throw new MacroExecutionException("The view file macro only supports pdf, doc, xls, and ppt file types.");
    }
}

