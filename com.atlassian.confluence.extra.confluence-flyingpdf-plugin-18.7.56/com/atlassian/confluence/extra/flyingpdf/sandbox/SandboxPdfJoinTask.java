/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxSerializer
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.confluence.util.sandbox.SandboxTaskContext
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfJoinRequest;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfJoinResponse;
import com.atlassian.confluence.extra.flyingpdf.util.BookmarksPageProcessor;
import com.atlassian.confluence.extra.flyingpdf.util.PageNumbersPageProcessor;
import com.atlassian.confluence.extra.flyingpdf.util.PdfJoiner;
import com.atlassian.confluence.extra.flyingpdf.util.PdfPageProcessor;
import com.atlassian.confluence.extra.flyingpdf.util.PdfPostProcessor;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;
import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class SandboxPdfJoinTask
implements SandboxTask<SandboxPdfJoinRequest, SandboxPdfJoinResponse> {
    public SandboxPdfJoinResponse apply(SandboxTaskContext context, SandboxPdfJoinRequest request) {
        String intermediateFile = request.getOutputFile() + "-partial";
        context.log(Level.INFO, (Object)("Starting PDF merge into: " + intermediateFile));
        int pageCount = -1;
        try {
            pageCount = new PdfJoiner(intermediateFile, request.getExportedSpaceStructure()).join();
            context.log(Level.INFO, (Object)String.format("Merged %d pages into: %s", pageCount, intermediateFile));
            ArrayList<PdfPageProcessor> pageProcessors = new ArrayList<PdfPageProcessor>();
            pageProcessors.add(new BookmarksPageProcessor(request.getSpaceKey(), request.getExportedSpaceStructure().locationByTitleMap(), request.getBaseUrl()));
            if (request.getDecorationPolicy().components().contains((Object)DecorationPolicy.DecorationComponent.PAGE_NUMBERS)) {
                pageProcessors.add(new PageNumbersPageProcessor());
            }
            context.log(Level.INFO, (Object)String.format("Post processing PDF document: %s -> %s", intermediateFile, request.getOutputFile()));
            new PdfPostProcessor(intermediateFile, request.getOutputFile(), pageProcessors).run();
        }
        catch (DocumentException | IOException e) {
            context.log(Level.WARNING, (Object)String.format("Error merging %s: %s", request.getOutputFile(), e));
            throw new RuntimeException(e);
        }
        finally {
            context.log(Level.INFO, (Object)("Deleting: " + intermediateFile));
            new File(intermediateFile).delete();
        }
        context.log(Level.INFO, (Object)String.format("Finished merging: %s", request.getOutputFile()));
        File outputFile = new File(request.getOutputFile());
        return new SandboxPdfJoinResponse(outputFile, pageCount);
    }

    public SandboxSerializer<SandboxPdfJoinRequest> inputSerializer() {
        return SandboxPdfJoinRequest.serializer();
    }

    public SandboxSerializer<SandboxPdfJoinResponse> outputSerializer() {
        return SandboxPdfJoinResponse.serializer();
    }
}

