/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.version;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavServletRequest;
import org.apache.jackrabbit.webdav.version.LabelInfo;
import org.apache.jackrabbit.webdav.version.MergeInfo;
import org.apache.jackrabbit.webdav.version.OptionsInfo;
import org.apache.jackrabbit.webdav.version.UpdateInfo;
import org.apache.jackrabbit.webdav.version.report.ReportInfo;

public interface DeltaVServletRequest
extends DavServletRequest {
    public String getLabel();

    public LabelInfo getLabelInfo() throws DavException;

    public MergeInfo getMergeInfo() throws DavException;

    public UpdateInfo getUpdateInfo() throws DavException;

    public ReportInfo getReportInfo() throws DavException;

    public OptionsInfo getOptionsInfo() throws DavException;
}

