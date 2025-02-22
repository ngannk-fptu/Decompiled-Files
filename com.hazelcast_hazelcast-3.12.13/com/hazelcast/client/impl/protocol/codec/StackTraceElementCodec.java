/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;

public final class StackTraceElementCodec {
    private StackTraceElementCodec() {
    }

    public static StackTraceElement decode(ClientMessage clientMessage) {
        String declaringClass = clientMessage.getStringUtf8();
        String methodName = clientMessage.getStringUtf8();
        boolean fileName_Null = clientMessage.getBoolean();
        String fileName = null;
        if (!fileName_Null) {
            fileName = clientMessage.getStringUtf8();
        }
        int lineNumber = clientMessage.getInt();
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }

    public static void encode(StackTraceElement stackTraceElement, ClientMessage clientMessage) {
        clientMessage.set(stackTraceElement.getClassName());
        clientMessage.set(stackTraceElement.getMethodName());
        String fileName = stackTraceElement.getFileName();
        boolean fileName_Null = fileName == null;
        clientMessage.set(fileName_Null);
        if (!fileName_Null) {
            clientMessage.set(fileName);
        }
        clientMessage.set(stackTraceElement.getLineNumber());
    }

    public static int calculateDataSize(StackTraceElement stackTraceElement) {
        boolean fileName_NotNull;
        int dataSize = 4;
        dataSize += ParameterUtil.calculateDataSize(stackTraceElement.getClassName());
        dataSize += ParameterUtil.calculateDataSize(stackTraceElement.getMethodName());
        ++dataSize;
        String fileName = stackTraceElement.getFileName();
        boolean bl = fileName_NotNull = fileName != null;
        if (fileName_NotNull) {
            dataSize += ParameterUtil.calculateDataSize(fileName);
        }
        return dataSize;
    }
}

