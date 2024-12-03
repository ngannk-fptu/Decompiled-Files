/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.loadtime;

import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;

public class DefaultMessageHandler
implements IMessageHandler {
    boolean isVerbose = false;
    boolean isDebug = false;
    boolean showWeaveInfo = false;
    boolean showWarn = true;

    @Override
    public boolean handleMessage(IMessage message) throws AbortException {
        if (this.isIgnoring(message.getKind())) {
            return false;
        }
        return SYSTEM_ERR.handleMessage(message);
    }

    @Override
    public boolean isIgnoring(IMessage.Kind kind) {
        if (kind.equals(IMessage.WEAVEINFO)) {
            return !this.showWeaveInfo;
        }
        if (kind.isSameOrLessThan(IMessage.INFO)) {
            return !this.isVerbose;
        }
        if (kind.isSameOrLessThan(IMessage.DEBUG)) {
            return !this.isDebug;
        }
        return !this.showWarn;
    }

    @Override
    public void dontIgnore(IMessage.Kind kind) {
        if (kind.equals(IMessage.WEAVEINFO)) {
            this.showWeaveInfo = true;
        } else if (kind.equals(IMessage.DEBUG)) {
            this.isVerbose = true;
        } else if (kind.equals(IMessage.WARNING)) {
            this.showWarn = false;
        }
    }

    @Override
    public void ignore(IMessage.Kind kind) {
        if (kind.equals(IMessage.WEAVEINFO)) {
            this.showWeaveInfo = false;
        } else if (kind.equals(IMessage.DEBUG)) {
            this.isVerbose = false;
        } else if (kind.equals(IMessage.WARNING)) {
            this.showWarn = true;
        }
    }
}

