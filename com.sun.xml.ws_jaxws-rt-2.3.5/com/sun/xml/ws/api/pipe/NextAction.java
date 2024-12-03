/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Tube;

public final class NextAction {
    int kind;
    Tube next;
    Packet packet;
    Throwable throwable;
    Runnable onExitRunnable;
    static final int INVOKE = 0;
    static final int INVOKE_AND_FORGET = 1;
    static final int RETURN = 2;
    static final int THROW = 3;
    static final int SUSPEND = 4;
    static final int THROW_ABORT_RESPONSE = 5;
    static final int ABORT_RESPONSE = 6;
    static final int INVOKE_ASYNC = 7;

    private void set(int k, Tube v, Packet p, Throwable t) {
        this.kind = k;
        this.next = v;
        this.packet = p;
        this.throwable = t;
    }

    public void invoke(Tube next, Packet p) {
        this.set(0, next, p, null);
    }

    public void invokeAndForget(Tube next, Packet p) {
        this.set(1, next, p, null);
    }

    public void returnWith(Packet response) {
        this.set(2, null, response, null);
    }

    public void throwException(Packet response, Throwable t) {
        this.set(2, null, response, t);
    }

    public void throwException(Throwable t) {
        assert (t instanceof RuntimeException || t instanceof Error);
        this.set(3, null, null, t);
    }

    public void throwExceptionAbortResponse(Throwable t) {
        this.set(5, null, null, t);
    }

    public void abortResponse(Packet response) {
        this.set(6, null, response, null);
    }

    public void invokeAsync(Tube next, Packet p) {
        this.set(7, next, p, null);
    }

    public void suspend() {
        this.suspend(null, null);
    }

    public void suspend(Runnable onExitRunnable) {
        this.suspend(null, onExitRunnable);
    }

    public void suspend(Tube next) {
        this.suspend(next, null);
    }

    public void suspend(Tube next, Runnable onExitRunnable) {
        this.set(4, next, null, null);
        this.onExitRunnable = onExitRunnable;
    }

    public Tube getNext() {
        return this.next;
    }

    public void setNext(Tube next) {
        this.next = next;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(super.toString()).append(" [");
        buf.append("kind=").append(this.getKindString()).append(',');
        buf.append("next=").append(this.next).append(',');
        buf.append("packet=").append(this.packet != null ? this.packet.toShortString() : null).append(',');
        buf.append("throwable=").append(this.throwable).append(']');
        return buf.toString();
    }

    public String getKindString() {
        switch (this.kind) {
            case 0: {
                return "INVOKE";
            }
            case 1: {
                return "INVOKE_AND_FORGET";
            }
            case 2: {
                return "RETURN";
            }
            case 3: {
                return "THROW";
            }
            case 4: {
                return "SUSPEND";
            }
            case 5: {
                return "THROW_ABORT_RESPONSE";
            }
            case 6: {
                return "ABORT_RESPONSE";
            }
            case 7: {
                return "INVOKE_ASYNC";
            }
        }
        throw new AssertionError(this.kind);
    }
}

