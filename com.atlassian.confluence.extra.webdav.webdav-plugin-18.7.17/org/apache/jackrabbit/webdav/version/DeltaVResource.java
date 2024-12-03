/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.version.OptionsInfo;
import org.apache.jackrabbit.webdav.version.OptionsResponse;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;

public interface DeltaVResource
extends DavResource {
    public static final String METHODS = "REPORT";
    public static final String METHODS_INCL_MKWORKSPACE = "REPORT, MKWORKSPACE";

    public OptionsResponse getOptionResponse(OptionsInfo var1);

    public Report getReport(ReportInfo var1) throws DavException;

    public void addWorkspace(DavResource var1) throws DavException;

    public DavResource[] getReferenceResources(DavPropertyName var1) throws DavException;
}

