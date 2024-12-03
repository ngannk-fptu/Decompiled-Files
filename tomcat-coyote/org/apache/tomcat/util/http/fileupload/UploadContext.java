/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import org.apache.tomcat.util.http.fileupload.RequestContext;

public interface UploadContext
extends RequestContext {
    public long contentLength();
}

