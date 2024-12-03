/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.io.PrintWriter;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;

public class MessageWriter
implements IMessageHandler {
    protected PrintWriter writer;
    protected boolean abortOnFailure;

    public MessageWriter(PrintWriter writer, boolean abortOnFailure) {
        this.writer = null != writer ? writer : new PrintWriter(System.out);
        this.abortOnFailure = abortOnFailure;
    }

    @Override
    public boolean handleMessage(IMessage message) throws AbortException {
        String result;
        if (null != message && !this.isIgnoring(message.getKind()) && null != (result = this.render(message))) {
            this.writer.println(result);
            this.writer.flush();
            if (this.abortOnFailure && (message.isFailed() || message.isAbort())) {
                throw new AbortException(message);
            }
        }
        return true;
    }

    @Override
    public boolean isIgnoring(IMessage.Kind kind) {
        return false;
    }

    @Override
    public void dontIgnore(IMessage.Kind kind) {
    }

    @Override
    public void ignore(IMessage.Kind kind) {
    }

    protected String render(IMessage message) {
        return message.toString();
    }
}

