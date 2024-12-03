/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.tribes.Member
 */
package org.apache.catalina.ha.tcp;

import org.apache.catalina.tribes.Member;

public class SendMessageData {
    private Object message;
    private Member destination;
    private Exception exception;

    public SendMessageData(Object message, Member destination, Exception exception) {
        this.message = message;
        this.destination = destination;
        this.exception = exception;
    }

    public Member getDestination() {
        return this.destination;
    }

    public Exception getException() {
        return this.exception;
    }

    public Object getMessage() {
        return this.message;
    }
}

