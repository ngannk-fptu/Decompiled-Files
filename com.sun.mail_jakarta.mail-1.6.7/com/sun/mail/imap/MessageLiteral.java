/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.imap;

import com.sun.mail.iap.Literal;
import com.sun.mail.imap.LengthCounter;
import com.sun.mail.util.CRLFOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.mail.Message;
import javax.mail.MessagingException;

class MessageLiteral
implements Literal {
    private Message msg;
    private int msgSize = -1;
    private byte[] buf;

    public MessageLiteral(Message msg, int maxsize) throws MessagingException, IOException {
        this.msg = msg;
        LengthCounter lc = new LengthCounter(maxsize);
        CRLFOutputStream os = new CRLFOutputStream(lc);
        msg.writeTo(os);
        ((OutputStream)os).flush();
        this.msgSize = lc.getSize();
        this.buf = lc.getBytes();
    }

    @Override
    public int size() {
        return this.msgSize;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        try {
            if (this.buf != null) {
                os.write(this.buf, 0, this.msgSize);
            } else {
                os = new CRLFOutputStream(os);
                this.msg.writeTo(os);
            }
        }
        catch (MessagingException mex) {
            throw new IOException("MessagingException while appending message: " + mex);
        }
    }
}

