/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.jackrabbit.webdav;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

public interface DavServletResponse
extends HttpServletResponse {
    public static final int SC_PROCESSING = 102;
    public static final int SC_MULTI_STATUS = 207;
    public static final int SC_UNPROCESSABLE_ENTITY = 422;
    public static final int SC_LOCKED = 423;
    public static final int SC_FAILED_DEPENDENCY = 424;
    public static final int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 507;

    public void sendError(DavException var1) throws IOException;

    public void sendMultiStatus(MultiStatus var1) throws IOException;

    default public void sendMultiStatus(MultiStatus multistatus, List<String> acceptableContentCodings) throws IOException {
        this.sendMultiStatus(multistatus);
    }

    public void sendRefreshLockResponse(ActiveLock[] var1) throws IOException;

    public void sendXmlResponse(XmlSerializable var1, int var2) throws IOException;

    default public void sendXmlResponse(XmlSerializable serializable, int status, List<String> acceptableContentCodings) throws IOException {
        this.sendXmlResponse(serializable, status);
    }
}

