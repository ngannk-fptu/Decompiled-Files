/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpResponse
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;

public class HttpReport
extends BaseDavRequest {
    private final boolean isDeep;

    public HttpReport(URI uri, ReportInfo reportInfo) throws IOException {
        super(uri);
        DepthHeader dh = new DepthHeader(reportInfo.getDepth());
        this.isDeep = reportInfo.getDepth() > 0;
        super.setHeader(dh.getHeaderName(), dh.getHeaderValue());
        super.setEntity(XmlEntity.create(reportInfo));
    }

    public HttpReport(String uri, ReportInfo reportInfo) throws IOException {
        this(URI.create(uri), reportInfo);
    }

    public String getMethod() {
        return "REPORT";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        if (this.isDeep) {
            return statusCode == 207;
        }
        return statusCode == 200 || statusCode == 207;
    }
}

