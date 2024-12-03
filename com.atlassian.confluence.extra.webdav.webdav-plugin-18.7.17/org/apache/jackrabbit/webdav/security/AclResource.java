/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.version.report.Report;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;

public interface AclResource
extends DavResource {
    public static final String METHODS = "ACL, REPORT";

    public void alterAcl(AclProperty var1) throws DavException;

    public Report getReport(ReportInfo var1) throws DavException;
}

