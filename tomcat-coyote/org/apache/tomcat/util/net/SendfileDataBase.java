/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import org.apache.tomcat.util.net.SendfileKeepAliveState;

public abstract class SendfileDataBase {
    public SendfileKeepAliveState keepAliveState = SendfileKeepAliveState.NONE;
    public final String fileName;
    public long pos;
    public long length;

    public SendfileDataBase(String filename, long pos, long length) {
        this.fileName = filename;
        this.pos = pos;
        this.length = length;
    }
}

