/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version.report;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;
import org.apache.jackrabbit.webdav.version.report.ReportType;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

public interface Report
extends XmlSerializable {
    public ReportType getType();

    public boolean isMultiStatusReport();

    public void init(DavResource var1, ReportInfo var2) throws DavException;
}

