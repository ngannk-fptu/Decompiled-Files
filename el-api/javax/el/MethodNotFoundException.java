/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import javax.el.ELException;

public class MethodNotFoundException
extends ELException {
    private static final long serialVersionUID = -3631968116081480328L;

    public MethodNotFoundException() {
    }

    public MethodNotFoundException(String message) {
        super(message);
    }

    public MethodNotFoundException(Throwable cause) {
        super(cause);
    }

    public MethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

