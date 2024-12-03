/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.signature;

import org.apache.xml.security.signature.XMLSignatureException;

public class ReferenceNotInitializedException
extends XMLSignatureException {
    private static final long serialVersionUID = 1L;

    public ReferenceNotInitializedException() {
    }

    public ReferenceNotInitializedException(Exception ex) {
        super(ex);
    }

    public ReferenceNotInitializedException(String msgID) {
        super(msgID);
    }

    public ReferenceNotInitializedException(String msgID, Object[] exArgs) {
        super(msgID, exArgs);
    }

    public ReferenceNotInitializedException(Exception originalException, String msgID) {
        super(originalException, msgID);
    }

    @Deprecated
    public ReferenceNotInitializedException(String msgID, Exception originalException) {
        this(originalException, msgID);
    }

    public ReferenceNotInitializedException(Exception originalException, String msgID, Object[] exArgs) {
        super(originalException, msgID, exArgs);
    }

    @Deprecated
    public ReferenceNotInitializedException(String msgID, Object[] exArgs, Exception originalException) {
        this(originalException, msgID, exArgs);
    }
}

