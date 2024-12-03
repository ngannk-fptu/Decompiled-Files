/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonException;

public class ReadOnlyValueException
extends IonException {
    private static final long serialVersionUID = 1L;

    public ReadOnlyValueException() {
        super("Read-only IonValue cannot be modified");
    }

    public ReadOnlyValueException(Class type) {
        super("Cannot modify read-only instance of " + type);
    }
}

