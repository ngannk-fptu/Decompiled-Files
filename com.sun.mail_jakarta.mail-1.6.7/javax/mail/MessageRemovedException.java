/*
 * Decompiled with CFR 0.152.
 */
package javax.mail;

import javax.mail.MessagingException;

public class MessageRemovedException
extends MessagingException {
    private static final long serialVersionUID = 1951292550679528690L;

    public MessageRemovedException() {
    }

    public MessageRemovedException(String s) {
        super(s);
    }

    public MessageRemovedException(String s, Exception e) {
        super(s, e);
    }
}

