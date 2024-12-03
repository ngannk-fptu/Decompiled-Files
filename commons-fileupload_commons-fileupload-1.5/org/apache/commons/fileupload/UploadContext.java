/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import org.apache.commons.fileupload.RequestContext;

public interface UploadContext
extends RequestContext {
    public long contentLength();
}

