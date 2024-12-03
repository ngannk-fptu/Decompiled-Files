/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpConnection
 */
package org.apache.hc.core5.http2.impl.nio;

import java.util.List;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http2.frame.RawFrame;

@Contract(threading=ThreadingBehavior.STATELESS)
@Internal
public interface H2StreamListener {
    public void onHeaderInput(HttpConnection var1, int var2, List<? extends Header> var3);

    public void onHeaderOutput(HttpConnection var1, int var2, List<? extends Header> var3);

    public void onFrameInput(HttpConnection var1, int var2, RawFrame var3);

    public void onFrameOutput(HttpConnection var1, int var2, RawFrame var3);

    public void onInputFlowControl(HttpConnection var1, int var2, int var3, int var4);

    public void onOutputFlowControl(HttpConnection var1, int var2, int var3, int var4);
}

