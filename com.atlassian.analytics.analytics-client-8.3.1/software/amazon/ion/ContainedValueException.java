/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonException;

public class ContainedValueException
extends IonException {
    private static final long serialVersionUID = 1L;

    public ContainedValueException() {
    }

    public ContainedValueException(String message) {
        super(message);
    }
}

