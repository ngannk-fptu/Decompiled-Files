/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.sandbox.SandboxCallback
 *  com.atlassian.confluence.util.sandbox.SandboxTaskContext
 */
package com.atlassian.confluence.impl.util.sandbox;

import com.atlassian.confluence.impl.util.sandbox.SandboxMessage;
import com.atlassian.confluence.impl.util.sandbox.SandboxMessageType;
import com.atlassian.confluence.util.sandbox.SandboxCallback;
import com.atlassian.confluence.util.sandbox.SandboxTaskContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;

public class SandboxServerContext
implements SandboxTaskContext {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final PrintStream errorStream;
    private final Level logLevel;

    public SandboxServerContext(InputStream inputStream, OutputStream outputStream, PrintStream errorStream, Level logLevel) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.errorStream = errorStream;
        this.logLevel = logLevel;
    }

    public <T, R> R execute(SandboxCallback<T, R> callback, T input) {
        this.sendMessage(new SandboxMessage(SandboxMessageType.APPLICATION_REQUEST, SandboxMessage.ApplicationPayload.withUnspecifiedClassloader(callback.getClass().getName(), callback.inputSerializer().serialize(input))));
        SandboxMessage message = this.receiveMessage();
        SandboxMessage.ApplicationPayload payload = (SandboxMessage.ApplicationPayload)message.getPayload();
        return (R)callback.outputSerializer().deserialize(payload.getData());
    }

    public void log(Level level, Object message) {
        if (level.intValue() >= this.logLevel.intValue()) {
            this.errorStream.println(message);
            this.errorStream.flush();
        }
    }

    public void sendMessage(SandboxMessage message) {
        try {
            SandboxMessage.sendMessage(message, this.outputStream);
        }
        catch (IOException e) {
            this.log(Level.SEVERE, "Error sending message to host");
            throw new RuntimeException(e);
        }
    }

    public SandboxMessage receiveMessage() {
        try {
            return SandboxMessage.receiveMessage(this.inputStream);
        }
        catch (IOException e) {
            this.log(Level.SEVERE, "Error reading message from host");
            throw new RuntimeException(e);
        }
    }

    public void sendStartMarker() {
        try {
            SandboxMessage.sendStartMarker(this.outputStream);
        }
        catch (IOException e) {
            this.log(Level.SEVERE, e);
            throw new RuntimeException(e);
        }
    }
}

