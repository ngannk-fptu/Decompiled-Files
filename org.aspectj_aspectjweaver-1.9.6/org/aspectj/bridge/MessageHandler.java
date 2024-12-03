/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.IMessageHolder;

public class MessageHandler
implements IMessageHolder {
    protected final ArrayList<IMessage> messages = new ArrayList();
    protected final ArrayList<IMessage.Kind> ignoring = new ArrayList();
    protected boolean handleMessageResult;
    protected IMessageHandler interceptor;

    public MessageHandler() {
        this(false);
    }

    public MessageHandler(boolean accumulateOnly) {
        this.init(accumulateOnly);
        this.ignore(IMessage.WEAVEINFO);
    }

    public void init() {
        this.init(false);
    }

    public void init(boolean accumulateOnly) {
        this.handleMessageResult = accumulateOnly;
        if (0 < this.messages.size()) {
            this.messages.clear();
        }
        if (0 < this.ignoring.size()) {
            boolean ignoringWeaveMessages = this.isIgnoring(IMessage.WEAVEINFO);
            this.ignoring.clear();
            if (ignoringWeaveMessages) {
                this.ignore(IMessage.WEAVEINFO);
            }
        }
        if (null != this.interceptor) {
            this.interceptor = null;
        }
    }

    @Override
    public void clearMessages() {
        if (0 < this.messages.size()) {
            this.messages.clear();
        }
    }

    @Override
    public boolean handleMessage(IMessage message) {
        if (null != this.interceptor && this.interceptor.handleMessage(message)) {
            return true;
        }
        if (null == message) {
            throw new IllegalArgumentException("null message");
        }
        if (!this.ignoring.contains(message.getKind())) {
            this.messages.add(message);
        }
        return this.handleMessageResult;
    }

    @Override
    public boolean isIgnoring(IMessage.Kind kind) {
        return null != kind && this.ignoring.contains(kind);
    }

    @Override
    public void ignore(IMessage.Kind kind) {
        if (null != kind && !this.ignoring.contains(kind)) {
            this.ignoring.add(kind);
        }
    }

    @Override
    public void dontIgnore(IMessage.Kind kind) {
        if (null != kind) {
            this.ignoring.remove(kind);
        }
    }

    @Override
    public boolean hasAnyMessage(IMessage.Kind kind, boolean orGreater) {
        if (null == kind) {
            return 0 < this.messages.size();
        }
        if (!orGreater) {
            for (IMessage m : this.messages) {
                if (kind != m.getKind()) continue;
                return true;
            }
        } else {
            for (IMessage m : this.messages) {
                if (!kind.isSameOrLessThan(m.getKind())) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public int numMessages(IMessage.Kind kind, boolean orGreater) {
        if (null == kind) {
            return this.messages.size();
        }
        int result = 0;
        if (!orGreater) {
            for (IMessage m : this.messages) {
                if (kind != m.getKind()) continue;
                ++result;
            }
        } else {
            for (IMessage m : this.messages) {
                if (!kind.isSameOrLessThan(m.getKind())) continue;
                ++result;
            }
        }
        return result;
    }

    @Override
    public List<IMessage> getUnmodifiableListView() {
        return Collections.unmodifiableList(this.messages);
    }

    @Override
    public IMessage[] getMessages(IMessage.Kind kind, boolean orGreater) {
        if (null == kind) {
            return this.messages.toArray(IMessage.RA_IMessage);
        }
        ArrayList<IMessage> result = new ArrayList<IMessage>();
        if (!orGreater) {
            for (IMessage m : this.messages) {
                if (kind != m.getKind()) continue;
                result.add(m);
            }
        } else {
            for (IMessage m : this.messages) {
                if (!kind.isSameOrLessThan(m.getKind())) continue;
                result.add(m);
            }
        }
        if (0 == result.size()) {
            return IMessage.RA_IMessage;
        }
        return result.toArray(IMessage.RA_IMessage);
    }

    public IMessage[] getErrors() {
        return this.getMessages(IMessage.ERROR, false);
    }

    public IMessage[] getWarnings() {
        return this.getMessages(IMessage.WARNING, false);
    }

    public void setInterceptor(IMessageHandler interceptor) {
        this.interceptor = interceptor;
    }

    public String toString() {
        if (0 == this.messages.size()) {
            return "MessageHandler: no messages";
        }
        return "MessageHandler: " + this.messages;
    }
}

