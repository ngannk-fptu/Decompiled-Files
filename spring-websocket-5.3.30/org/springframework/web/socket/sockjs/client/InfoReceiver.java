/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.sockjs.client;

import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

public interface InfoReceiver {
    public String executeInfoRequest(URI var1, @Nullable HttpHeaders var2);
}

