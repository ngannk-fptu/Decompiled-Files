/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.c14n;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class CanonicalizationException
extends XMLSecurityException {
    private static final long serialVersionUID = 1L;

    public CanonicalizationException() {
    }

    public CanonicalizationException(Exception ex) {
        super(ex);
    }

    public CanonicalizationException(String msgID) {
        super(msgID);
    }

    public CanonicalizationException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public CanonicalizationException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public CanonicalizationException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public CanonicalizationException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public CanonicalizationException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}

