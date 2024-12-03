/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.signature;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class XMLSignatureException
extends XMLSecurityException {
    private static final long serialVersionUID = 1L;

    public XMLSignatureException() {
    }

    public XMLSignatureException(Exception ex) {
        super(ex);
    }

    public XMLSignatureException(String msgID) {
        super(msgID);
    }

    public XMLSignatureException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public XMLSignatureException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public XMLSignatureException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public XMLSignatureException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public XMLSignatureException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}

