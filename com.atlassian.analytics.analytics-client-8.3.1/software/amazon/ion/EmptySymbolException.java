/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonException;

public class EmptySymbolException
extends IonException {
    private static final long serialVersionUID = -7801632953459636349L;

    public EmptySymbolException() {
        super("Symbols must contain at least one character.");
    }
}

