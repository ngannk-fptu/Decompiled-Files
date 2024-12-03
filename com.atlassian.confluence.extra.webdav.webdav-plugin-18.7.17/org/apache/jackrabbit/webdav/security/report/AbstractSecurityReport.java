/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security.report;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractSecurityReport
extends AbstractReport {
    protected MultiStatusResponse[] responses;

    @Override
    public boolean isMultiStatusReport() {
        return true;
    }

    @Override
    public void init(DavResource resource, ReportInfo info) throws DavException {
        if (resource == null || info == null) {
            throw new DavException(400, "Unable to run report: WebDAV Resource and ReportInfo must not be null.");
        }
        if (!this.getType().isRequestedReportType(info)) {
            throw new DavException(400, "Expected report type: '" + this.getType().getReportName() + "', found: '" + info.getReportName() + ";'.");
        }
        if (info.getDepth() > 0) {
            throw new DavException(400, "Invalid Depth header: " + info.getDepth());
        }
    }

    @Override
    public Element toXml(Document document) {
        MultiStatus ms = new MultiStatus();
        if (this.responses != null) {
            for (MultiStatusResponse response : this.responses) {
                ms.addResponse(response);
            }
        }
        return ms.toXml(document);
    }
}

