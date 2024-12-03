/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.scheduling.TaskScheduler
 */
package org.springframework.web.socket.sockjs.transport;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;

public interface SockJsServiceConfig {
    public TaskScheduler getTaskScheduler();

    public int getStreamBytesLimit();

    public long getHeartbeatTime();

    public int getHttpMessageCacheSize();

    public SockJsMessageCodec getMessageCodec();
}

