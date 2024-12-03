/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.message;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.util.CharArrayBuffer;

public interface LineFormatter {
    public void formatRequestLine(CharArrayBuffer var1, RequestLine var2);

    public void formatStatusLine(CharArrayBuffer var1, StatusLine var2);

    public void formatHeader(CharArrayBuffer var1, Header var2);
}

