/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.sockjs.frame;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.Nullable;

public interface SockJsMessageCodec {
    public String encode(String ... var1);

    @Nullable
    public String[] decode(String var1) throws IOException;

    @Nullable
    public String[] decodeInputStream(InputStream var1) throws IOException;
}

