/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.util.CharArrayBuffer;

public interface FormattedHeader
extends Header {
    public CharArrayBuffer getBuffer();

    public int getValuePos();
}

