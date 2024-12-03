/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.signature;

import org.apache.xml.security.signature.XMLSignatureException;

public class InvalidDigestValueException
extends XMLSignatureException {
    private static final long serialVersionUID = 1L;

    public InvalidDigestValueException() {
    }

    public InvalidDigestValueException(String msgID) {
        super(msgID);
    }

    public InvalidDigestValueException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public InvalidDigestValueException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public InvalidDigestValueException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public InvalidDigestValueException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public InvalidDigestValueException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}

