/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Extension
 */
package org.apache.tomcat.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import javax.websocket.Extension;
import org.apache.tomcat.websocket.MessagePart;
import org.apache.tomcat.websocket.TransformationResult;

public interface Transformation {
    public void setNext(Transformation var1);

    public boolean validateRsvBits(int var1);

    public Extension getExtensionResponse();

    public TransformationResult getMoreData(byte var1, boolean var2, int var3, ByteBuffer var4) throws IOException;

    public boolean validateRsv(int var1, byte var2);

    public List<MessagePart> sendMessagePart(List<MessagePart> var1) throws IOException;

    public void close();
}

