/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.PongMessage
 */
package org.apache.tomcat.websocket;

import java.nio.ByteBuffer;
import javax.websocket.PongMessage;

public class WsPongMessage
implements PongMessage {
    private final ByteBuffer applicationData;

    public WsPongMessage(ByteBuffer applicationData) {
        byte[] dst = new byte[applicationData.limit()];
        applicationData.get(dst);
        this.applicationData = ByteBuffer.wrap(dst);
    }

    public ByteBuffer getApplicationData() {
        return this.applicationData;
    }
}

