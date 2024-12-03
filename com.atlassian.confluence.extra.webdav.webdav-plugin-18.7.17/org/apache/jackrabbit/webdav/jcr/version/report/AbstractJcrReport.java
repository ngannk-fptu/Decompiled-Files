/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.jcr.version.report;

import javax.jcr.Session;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.version.report.AbstractReport;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;

public abstract class AbstractJcrReport
extends AbstractReport {
    private Session session;
    private ReportInfo reportInfo;

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
        DavSession davSession = resource.getSession();
        if (davSession == null) {
            throw new DavException(400, "The resource must provide a non-null session object in order to create '" + this.getType().getReportName() + "' report.");
        }
        this.session = JcrDavSession.getRepositorySession(resource.getSession());
        if (this.session == null) {
            throw new DavException(500, "Internal error: Unable to access repository session.");
        }
        this.reportInfo = info;
    }

    Session getRepositorySession() {
        return this.session;
    }

    ReportInfo getReportInfo() {
        return this.reportInfo;
    }
}

