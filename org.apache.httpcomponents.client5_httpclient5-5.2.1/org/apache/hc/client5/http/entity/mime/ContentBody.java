/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.entity.mime;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.hc.client5.http.entity.mime.ContentDescriptor;

public interface ContentBody
extends ContentDescriptor {
    public String getFilename();

    public void writeTo(OutputStream var1) throws IOException;
}

