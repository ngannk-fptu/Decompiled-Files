/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.attachments;

import org.apache.axiom.attachments.IncomingAttachmentInputStream;
import org.apache.axiom.om.OMException;

public abstract class IncomingAttachmentStreams {
    protected boolean _readyToGetNextStream = true;

    public final boolean isReadyToGetNextStream() {
        return this._readyToGetNextStream;
    }

    protected final void setReadyToGetNextStream(boolean ready) {
        this._readyToGetNextStream = ready;
    }

    public abstract IncomingAttachmentInputStream getNextStream() throws OMException;
}

