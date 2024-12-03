/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.impl.nio;

public enum MessageState {
    IDLE,
    HEADERS,
    ACK,
    BODY,
    COMPLETE;

}

