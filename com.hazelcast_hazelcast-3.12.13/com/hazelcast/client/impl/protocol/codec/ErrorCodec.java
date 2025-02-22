/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.StackTraceElementCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value={"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"}, justification="fields may be needed for diagnostic")
public final class ErrorCodec {
    public static final int TYPE = 109;
    public int errorCode;
    public String className;
    public String message;
    public StackTraceElement[] stackTrace;
    public int causeErrorCode;
    public String causeClassName;

    private ErrorCodec(ClientMessage flyweight) {
        this.errorCode = flyweight.getInt();
        this.className = flyweight.getStringUtf8();
        boolean message_isNull = flyweight.getBoolean();
        if (!message_isNull) {
            this.message = flyweight.getStringUtf8();
        }
        int stackTraceCount = flyweight.getInt();
        this.stackTrace = new StackTraceElement[stackTraceCount];
        for (int i = 0; i < stackTraceCount; ++i) {
            this.stackTrace[i] = StackTraceElementCodec.decode(flyweight);
        }
        this.causeErrorCode = flyweight.getInt();
        boolean causeClassName_isNull = flyweight.getBoolean();
        if (!causeClassName_isNull) {
            this.causeClassName = flyweight.getStringUtf8();
        }
    }

    public static ErrorCodec decode(ClientMessage flyweight) {
        return new ErrorCodec(flyweight);
    }

    public static ClientMessage encode(int errorCode, String className, String message, StackTraceElement[] stackTrace, int causeErrorCode, String causeClassName) {
        int requiredDataSize = ErrorCodec.calculateDataSize(errorCode, className, message, stackTrace, causeErrorCode, causeClassName);
        ClientMessage clientMessage = ClientMessage.createForEncode(requiredDataSize);
        clientMessage.setMessageType(109);
        clientMessage.set(errorCode);
        clientMessage.set(className);
        boolean message_isNull = message == null;
        clientMessage.set(message_isNull);
        if (!message_isNull) {
            clientMessage.set(message);
        }
        clientMessage.set(stackTrace.length);
        for (StackTraceElement stackTraceElement : stackTrace) {
            StackTraceElementCodec.encode(stackTraceElement, clientMessage);
        }
        clientMessage.set(causeErrorCode);
        boolean causeClassName_isNull = causeClassName == null;
        clientMessage.set(causeClassName_isNull);
        if (!causeClassName_isNull) {
            clientMessage.set(causeClassName);
        }
        clientMessage.updateFrameLength();
        return clientMessage;
    }

    public static int calculateDataSize(int errorCode, String className, String message, StackTraceElement[] stackTrace, int causeErrorCode, String causeClassName) {
        boolean causeClassName_isNull;
        int dataSize = ClientMessage.HEADER_SIZE + 4;
        dataSize += ParameterUtil.calculateDataSize(className);
        ++dataSize;
        if (message != null) {
            dataSize += ParameterUtil.calculateDataSize(message);
        }
        ++dataSize;
        dataSize += 4;
        boolean bl = causeClassName_isNull = causeClassName == null;
        if (!causeClassName_isNull) {
            dataSize += ParameterUtil.calculateDataSize(causeClassName);
        }
        for (StackTraceElement stackTraceElement : stackTrace) {
            dataSize += StackTraceElementCodec.calculateDataSize(stackTraceElement);
        }
        return dataSize;
    }
}

