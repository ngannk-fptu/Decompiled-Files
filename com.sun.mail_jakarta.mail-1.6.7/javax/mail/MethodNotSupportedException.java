/*
 * Decompiled with CFR 0.152.
 */
package javax.mail;

import javax.mail.MessagingException;

public class MethodNotSupportedException
extends MessagingException {
    private static final long serialVersionUID = -3757386618726131322L;

    public MethodNotSupportedException() {
    }

    public MethodNotSupportedException(String s) {
        super(s);
    }

    public MethodNotSupportedException(String s, Exception e) {
        super(s, e);
    }
}

