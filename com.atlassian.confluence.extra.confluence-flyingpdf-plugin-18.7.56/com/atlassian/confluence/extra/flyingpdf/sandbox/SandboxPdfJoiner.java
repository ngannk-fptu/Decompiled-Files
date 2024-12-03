/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.util.sandbox.SandboxCrashedException
 *  com.atlassian.confluence.util.sandbox.SandboxException
 *  com.atlassian.confluence.util.sandbox.SandboxTimeoutException
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.extra.flyingpdf.analytic.ExportStatus;
import com.atlassian.confluence.extra.flyingpdf.analytic.FailureLocation;
import com.atlassian.confluence.extra.flyingpdf.analytic.SpaceExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.sandbox.PdfExportSandbox;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfJoinRequest;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfJoinResponse;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfJoinTask;
import com.atlassian.confluence.extra.flyingpdf.util.ErrorMessages;
import com.atlassian.confluence.extra.flyingpdf.util.ExportedSpaceStructure;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.util.sandbox.SandboxCrashedException;
import com.atlassian.confluence.util.sandbox.SandboxException;
import com.atlassian.confluence.util.sandbox.SandboxTimeoutException;
import org.springframework.stereotype.Component;

@Component
public class SandboxPdfJoiner {
    private final ErrorMessages errorMessages;
    private final PdfExportSandbox pdfExportSandbox;

    public SandboxPdfJoiner(ErrorMessages errorMessages, PdfExportSandbox pdfExportSandbox) {
        this.errorMessages = errorMessages;
        this.pdfExportSandbox = pdfExportSandbox;
    }

    SandboxPdfJoinResponse join(SpaceExportMetrics spaceExportMetrics, String spaceKey, ExportedSpaceStructure exportedSpaceStructure, String outputFile, String baseUrl, DecorationPolicy decorationPolicy) throws ImportExportException {
        SandboxPdfJoinTask task = new SandboxPdfJoinTask();
        SandboxPdfJoinRequest request = new SandboxPdfJoinRequest(spaceKey, exportedSpaceStructure, outputFile, baseUrl, decorationPolicy);
        try {
            return this.pdfExportSandbox.execute(task, request);
        }
        catch (SandboxTimeoutException e) {
            spaceExportMetrics.getExportResults().setExportStatus(ExportStatus.SANDBOX_TIMEOUT);
            spaceExportMetrics.getExportResults().setFailureLocation(FailureLocation.JOIN);
            throw new ImportExportException(this.errorMessages.joinTimeoutMessage(), (Throwable)e);
        }
        catch (SandboxCrashedException e) {
            spaceExportMetrics.getExportResults().setExportStatus(ExportStatus.SANDBOX_CRASH);
            spaceExportMetrics.getExportResults().setFailureLocation(FailureLocation.JOIN);
            throw new ImportExportException(this.errorMessages.joinErrorMessage(), (Throwable)e);
        }
        catch (SandboxException e) {
            spaceExportMetrics.getExportResults().setExportStatus(ExportStatus.FAIL);
            spaceExportMetrics.getExportResults().setFailureLocation(FailureLocation.JOIN);
            throw new ImportExportException(this.errorMessages.joinErrorMessage(), (Throwable)e);
        }
    }
}

