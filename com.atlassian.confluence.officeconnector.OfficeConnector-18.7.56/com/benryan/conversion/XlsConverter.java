/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.plugins.conversion.convert.ConversionException
 *  com.atlassian.plugins.conversion.convert.html.HtmlConversionResult
 *  com.atlassian.plugins.conversion.convert.html.spreadsheet.SpreadsheetConverter
 *  com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionType
 */
package com.benryan.conversion;

import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.html.HtmlConversionResult;
import com.atlassian.plugins.conversion.convert.html.spreadsheet.SpreadsheetConverter;
import com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionType;
import com.benryan.components.HtmlCacheManager;
import com.benryan.conversion.DocConverter;
import com.benryan.conversion.SandboxConversionFeature;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class XlsConverter
extends DocConverter {
    private static final String OFFICECONNECTOR_SPREADSHEET_XLSXMAXSIZE = "officeconnector.spreadsheet.xlsxmaxsize";
    private final RenderedContentCleaner renderedContentCleaner;

    public XlsConverter(HtmlCacheManager manager, VelocityHelperService velocityHelperService, SandboxConversionFeature sandboxConversionFeature, Sandbox sandbox, RenderedContentCleaner renderedContentCleaner) {
        super(manager, velocityHelperService, sandboxConversionFeature, sandbox);
        this.renderedContentCleaner = renderedContentCleaner;
    }

    @Override
    protected HtmlConversionResult doConversion(String imgPath, Map<String, Object> args, InputStream inputStream, String imagePath) throws IOException, ConversionException {
        if (this.isSandboxConversionEnabled()) {
            return this.performConversionInSandbox(args, inputStream, imgPath, SandboxHtmlConversionType.EXCEL);
        }
        return SpreadsheetConverter.convertToHtml((InputStream)inputStream, (String)imgPath, args, (RenderedContentCleaner)this.renderedContentCleaner);
    }

    @Override
    protected void validate(Attachment attachment, Map<String, Object> args) throws ConversionException {
        long maxBytesXlsx = Long.getLong(OFFICECONNECTOR_SPREADSHEET_XLSXMAXSIZE, 0x200000L);
        Object type = args.get("type");
        if (type != null && type.equals("xlsx") && attachment.getFileSize() > maxBytesXlsx) {
            throw new ConversionException(String.format("Cannot convert %s, this file exceeds the maximum file size for xlsx files of %dMB.", attachment.getFileName(), maxBytesXlsx >> 20));
        }
    }
}

