/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.PolicyConfiguredCleaner
 *  com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.sandbox.SandboxTaskContext
 */
package com.atlassian.plugins.conversion.sandbox.html;

import com.atlassian.confluence.content.render.xhtml.PolicyConfiguredCleaner;
import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.html.HtmlConversionResult;
import com.atlassian.plugins.conversion.convert.html.spreadsheet.SpreadsheetConverter;
import com.atlassian.plugins.conversion.convert.html.word.WordConverter;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
import com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionRequest;
import com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionResponse;
import com.atlassian.plugins.conversion.sandbox.html.SandboxHtmlConversionType;
import java.io.ByteArrayInputStream;

public class SandboxHtmlConversionTask
implements SandboxTask<SandboxHtmlConversionRequest, SandboxHtmlConversionResponse> {
    private static final String ANTISAMI_CONFIG_FILE = "com/atlassian/confluence/content/render/xhtml/antisamy-confluence-rendered-content.xml";

    public SandboxHtmlConversionResponse apply(SandboxTaskContext sandboxTaskContext, SandboxHtmlConversionRequest sandboxHtmlConversionRequest) {
        HtmlConversionResult conversionResult;
        SandboxHtmlConversionType conversionType = sandboxHtmlConversionRequest.getSandboxHtmlConversionType();
        if (conversionType == SandboxHtmlConversionType.EXCEL) {
            try {
                PolicyConfiguredCleaner renderedContentCleaner = new PolicyConfiguredCleaner(ANTISAMI_CONFIG_FILE);
                conversionResult = SpreadsheetConverter.convertToHtml(new ByteArrayInputStream(sandboxHtmlConversionRequest.getInputFile()), sandboxHtmlConversionRequest.getImgPath(), sandboxHtmlConversionRequest.getArgs(), (RenderedContentCleaner)renderedContentCleaner);
            }
            catch (ConversionException e) {
                throw new RuntimeException(e);
            }
        } else {
            conversionResult = WordConverter.convertToHtml(new ByteArrayInputStream(sandboxHtmlConversionRequest.getInputFile()), sandboxHtmlConversionRequest.getImgPath());
        }
        return new SandboxHtmlConversionResponse(SandboxConversionStatus.CONVERTED, conversionResult);
    }

    public SandboxSerializer<SandboxHtmlConversionRequest> inputSerializer() {
        return SandboxHtmlConversionRequest.serializer();
    }

    public SandboxSerializer<SandboxHtmlConversionResponse> outputSerializer() {
        return SandboxHtmlConversionResponse.serializer();
    }
}

