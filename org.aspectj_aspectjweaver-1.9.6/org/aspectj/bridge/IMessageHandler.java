/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.io.PrintWriter;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageWriter;

public interface IMessageHandler {
    public static final IMessageHandler SYSTEM_ERR = new MessageWriter(new PrintWriter(System.err, true), true);
    public static final IMessageHandler SYSTEM_OUT = new MessageWriter(new PrintWriter(System.out, true), false);
    public static final IMessageHandler THROW = new IMessageHandler(){

        @Override
        public boolean handleMessage(IMessage message) {
            if (message.getKind().compareTo(IMessage.ERROR) >= 0) {
                throw new AbortException(message);
            }
            return SYSTEM_OUT.handleMessage(message);
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
    };

    public boolean handleMessage(IMessage var1) throws AbortException;

    public boolean isIgnoring(IMessage.Kind var1);

    public void dontIgnore(IMessage.Kind var1);

    public void ignore(IMessage.Kind var1);
}

