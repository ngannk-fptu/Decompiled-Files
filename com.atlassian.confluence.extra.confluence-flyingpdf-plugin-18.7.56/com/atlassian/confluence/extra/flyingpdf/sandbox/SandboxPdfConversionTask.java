/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxCallback
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.sandbox.SandboxTaskContext
 *  org.apache.commons.lang3.SerializationUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.extra.flyingpdf.html.ConfluenceNamespaceHandler;
import com.atlassian.confluence.extra.flyingpdf.impl.AbstractExportUserAgent;
import com.atlassian.confluence.extra.flyingpdf.sandbox.GetResourceCallback;
import com.atlassian.confluence.extra.flyingpdf.sandbox.GetResourceCallbackRequest;
import com.atlassian.confluence.extra.flyingpdf.sandbox.GetResourceCallbackResponse;
import com.atlassian.confluence.extra.flyingpdf.sandbox.MatchResourceCallback;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfConversionRequest;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfConversionResponse;
import com.atlassian.confluence.extra.flyingpdf.util.ImageFileCacheUtils;
import com.atlassian.confluence.extra.flyingpdf.util.ImageInformationURICacheUtil;
import com.atlassian.confluence.extra.flyingpdf.util.ImageTranscoderCacheUtil;
import com.atlassian.confluence.util.sandbox.SandboxCallback;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;
import com.lowagie.text.DocumentException;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.DefaultPDFCreationListener;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class SandboxPdfConversionTask
implements SandboxTask<SandboxPdfConversionRequest, SandboxPdfConversionResponse> {
    public SandboxPdfConversionResponse apply(SandboxTaskContext context, SandboxPdfConversionRequest request) {
        ITextRenderer renderer = SandboxPdfConversionTask.createRenderer(context, request.getBaseUrl(), request.getCdnUrl(), request.getUsername(), request.getFontPath());
        Document document = (Document)SerializationUtils.deserialize((byte[])request.getDocument());
        Result exportResult = SandboxPdfConversionTask.createPdfFile(context, document, renderer, request.getExportFile(), request.getBaseUrl(), request.getBaseUrl() + request.getContextPath() + "/");
        return new SandboxPdfConversionResponse(exportResult.pdf, exportResult.pageCount);
    }

    private static Result createPdfFile(SandboxTaskContext context, Document document, ITextRenderer renderer, String exportFile, String baseUrl, String contextUrl) {
        Result result = new Result(exportFile);
        context.log(Level.INFO, (Object)("Start generating: " + result.pdf.getAbsolutePath()));
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(result.pdf));){
            ImageFileCacheUtils.initializeSandboxTempExportDirectory();
            ImageInformationURICacheUtil.initializeCache();
            ImageTranscoderCacheUtil.initializeCache();
            renderer.setDocument(document, contextUrl);
            renderer.getSharedContext().setNamespaceHandler(new ConfluenceNamespaceHandler(baseUrl));
            renderer.layout();
            renderer.setListener(result);
            renderer.createPDF(outputStream);
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to create the PDF document: " + result.pdf.getAbsolutePath(), ex);
        }
        finally {
            ImageFileCacheUtils.removeTempDirectory();
            ImageInformationURICacheUtil.purgeCache();
            ImageTranscoderCacheUtil.purgeCache();
        }
        context.log(Level.INFO, (Object)String.format("Complete generating %d page(s) in: %s", result.pageCount, result.pdf.getAbsolutePath()));
        return result;
    }

    private static ITextRenderer createRenderer(SandboxTaskContext context, String baseUrl, String cdnUrl, String username, String fontPath) {
        ITextRenderer renderer = new ITextRenderer();
        SandboxCallbackUserAgent userAgent = new SandboxCallbackUserAgent(renderer.getOutputDevice(), baseUrl, cdnUrl, username, context);
        renderer.getSharedContext().setUserAgentCallback(userAgent);
        userAgent.setBaseURL(baseUrl);
        userAgent.setSharedContext(renderer.getSharedContext());
        if (StringUtils.isNotBlank((CharSequence)fontPath)) {
            SandboxPdfConversionTask.configureFonts(renderer.getFontResolver(), fontPath);
        }
        return renderer;
    }

    public SandboxSerializer<SandboxPdfConversionRequest> inputSerializer() {
        return SandboxPdfConversionRequest.serializer();
    }

    public SandboxSerializer<SandboxPdfConversionResponse> outputSerializer() {
        return SandboxPdfConversionResponse.serializer();
    }

    private static void configureFonts(ITextFontResolver fontResolver, String fontPath) {
        try {
            fontResolver.addFont(fontPath, "ConfluenceInstalledFont", "Identity-H", true, null);
        }
        catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SandboxCallbackUserAgent
    extends AbstractExportUserAgent {
        private final String username;
        private final SandboxTaskContext sandboxTaskContext;

        SandboxCallbackUserAgent(ITextOutputDevice device, String baseUrl, String cdnUrl, String username, SandboxTaskContext sandboxTaskContext) {
            super(device, baseUrl, cdnUrl);
            this.username = username;
            this.sandboxTaskContext = sandboxTaskContext;
        }

        @Override
        protected boolean shrinkImageCacheBeforeFetching() {
            return true;
        }

        @Override
        protected InputStream fetchResourceFromConfluence(String relativeUri, String decodedUri) {
            GetResourceCallbackRequest request;
            GetResourceCallbackResponse response;
            if (((Boolean)this.sandboxTaskContext.execute((SandboxCallback)new MatchResourceCallback(), (Object)decodedUri)).booleanValue() && (response = (GetResourceCallbackResponse)this.sandboxTaskContext.execute((SandboxCallback)new GetResourceCallback(), (Object)(request = new GetResourceCallbackRequest(decodedUri, this.username)))).getData().length > 0) {
                return new ByteArrayInputStream(response.getData());
            }
            return null;
        }

        @Override
        protected void log(Level level, String message) {
            this.sandboxTaskContext.log(level, (Object)message);
        }
    }

    private static class Result
    extends DefaultPDFCreationListener {
        public final File pdf;
        public int pageCount = 0;

        public Result(String path) {
            this.pdf = new File(path);
        }

        @Override
        public void preWrite(ITextRenderer iTextRenderer, int pageCount) {
            this.pageCount = pageCount;
        }
    }
}

