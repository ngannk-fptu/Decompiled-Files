/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.ha.backend;

import org.apache.catalina.ha.backend.HeartbeatListener;

public interface Sender {
    public void init(HeartbeatListener var1) throws Exception;

    public int send(String var1) throws Exception;
}

