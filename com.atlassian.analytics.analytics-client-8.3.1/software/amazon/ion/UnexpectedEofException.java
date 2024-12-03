/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonException;

public class UnexpectedEofException
extends IonException {
    private static final long serialVersionUID = 1L;

    public UnexpectedEofException() {
    }

    public UnexpectedEofException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedEofException(String message) {
        super(message);
    }

    public UnexpectedEofException(Throwable cause) {
        super(cause);
    }
}

