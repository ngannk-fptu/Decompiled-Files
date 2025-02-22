/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.imap;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.IMAPMessage;
import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import com.sun.mail.imap.protocol.ENVELOPE;
import com.sun.mail.imap.protocol.IMAPProtocol;
import javax.mail.Flags;
import javax.mail.FolderClosedException;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;

public class IMAPNestedMessage
extends IMAPMessage {
    private IMAPMessage msg;

    IMAPNestedMessage(IMAPMessage m, BODYSTRUCTURE b, ENVELOPE e, String sid) {
        super(m._getSession());
        this.msg = m;
        this.bs = b;
        this.envelope = e;
        this.sectionId = sid;
        this.setPeek(m.getPeek());
    }

    @Override
    protected IMAPProtocol getProtocol() throws ProtocolException, FolderClosedException {
        return this.msg.getProtocol();
    }

    @Override
    protected boolean isREV1() throws FolderClosedException {
        return this.msg.isREV1();
    }

    @Override
    protected Object getMessageCacheLock() {
        return this.msg.getMessageCacheLock();
    }

    @Override
    protected int getSequenceNumber() {
        return this.msg.getSequenceNumber();
    }

    @Override
    protected void checkExpunged() throws MessageRemovedException {
        this.msg.checkExpunged();
    }

    @Override
    public boolean isExpunged() {
        return this.msg.isExpunged();
    }

    @Override
    protected int getFetchBlockSize() {
        return this.msg.getFetchBlockSize();
    }

    @Override
    protected boolean ignoreBodyStructureSize() {
        return this.msg.ignoreBodyStructureSize();
    }

    @Override
    public int getSize() throws MessagingException {
        return this.bs.size;
    }

    @Override
    public synchronized void setFlags(Flags flag, boolean set) throws MessagingException {
        throw new MethodNotSupportedException("Cannot set flags on this nested message");
    }
}

