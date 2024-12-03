/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 */
package org.springframework.web.socket.sockjs.client;

import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.sockjs.client.InfoReceiver;
import org.springframework.web.socket.sockjs.client.Transport;

public interface XhrTransport
extends Transport,
InfoReceiver {
    public boolean isXhrStreamingDisabled();

    public void executeSendRequest(URI var1, HttpHeaders var2, TextMessage var3);
}

