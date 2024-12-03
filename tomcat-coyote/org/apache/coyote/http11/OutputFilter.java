/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http11;

import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;

public interface OutputFilter
extends HttpOutputBuffer {
    public void setResponse(Response var1);

    public void recycle();

    public void setBuffer(HttpOutputBuffer var1);
}

