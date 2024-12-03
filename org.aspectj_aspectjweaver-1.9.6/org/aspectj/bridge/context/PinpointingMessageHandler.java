/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge.context;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.context.CompilationAndWeavingContext;

public class PinpointingMessageHandler
implements IMessageHandler {
    private IMessageHandler delegate;

    public PinpointingMessageHandler(IMessageHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean handleMessage(IMessage message) throws AbortException {
        if (!this.isIgnoring(message.getKind())) {
            MessageIssued ex = new MessageIssued();
            ex.fillInStackTrace();
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            StringBuffer sb = new StringBuffer();
            sb.append(CompilationAndWeavingContext.getCurrentContext());
            sb.append(sw.toString());
            PinpointedMessage pinpointedMessage = new PinpointedMessage(message, sb.toString());
            return this.delegate.handleMessage(pinpointedMessage);
        }
        return this.delegate.handleMessage(message);
    }

    @Override
    public boolean isIgnoring(IMessage.Kind kind) {
        return this.delegate.isIgnoring(kind);
    }

    @Override
    public void dontIgnore(IMessage.Kind kind) {
        this.delegate.dontIgnore(kind);
    }

    @Override
    public void ignore(IMessage.Kind kind) {
        this.delegate.ignore(kind);
    }

    private static class MessageIssued
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private MessageIssued() {
        }

        @Override
        public String getMessage() {
            return "message issued...";
        }
    }

    private static class PinpointedMessage
    implements IMessage {
        private IMessage delegate;
        private String message;

        public PinpointedMessage(IMessage delegate, String pinpoint) {
            this.delegate = delegate;
            this.message = delegate.getMessage() + "\n" + pinpoint;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public IMessage.Kind getKind() {
            return this.delegate.getKind();
        }

        @Override
        public boolean isError() {
            return this.delegate.isError();
        }

        @Override
        public boolean isWarning() {
            return this.delegate.isWarning();
        }

        @Override
        public boolean isDebug() {
            return this.delegate.isDebug();
        }

        @Override
        public boolean isInfo() {
            return this.delegate.isInfo();
        }

        @Override
        public boolean isAbort() {
            return this.delegate.isAbort();
        }

        @Override
        public boolean isTaskTag() {
            return this.delegate.isTaskTag();
        }

        @Override
        public boolean isFailed() {
            return this.delegate.isFailed();
        }

        @Override
        public boolean getDeclared() {
            return this.delegate.getDeclared();
        }

        @Override
        public int getID() {
            return this.delegate.getID();
        }

        @Override
        public int getSourceStart() {
            return this.delegate.getSourceStart();
        }

        @Override
        public int getSourceEnd() {
            return this.delegate.getSourceEnd();
        }

        @Override
        public Throwable getThrown() {
            return this.delegate.getThrown();
        }

        @Override
        public ISourceLocation getSourceLocation() {
            return this.delegate.getSourceLocation();
        }

        @Override
        public String getDetails() {
            return this.delegate.getDetails();
        }

        @Override
        public List<ISourceLocation> getExtraSourceLocations() {
            return this.delegate.getExtraSourceLocations();
        }
    }
}

