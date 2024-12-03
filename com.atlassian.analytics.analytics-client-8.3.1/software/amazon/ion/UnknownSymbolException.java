/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import software.amazon.ion.IonException;

public class UnknownSymbolException
extends IonException {
    private static final long serialVersionUID = 1L;
    private final int mySid;

    public UnknownSymbolException(int sid) {
        this.mySid = sid;
    }

    public int getSid() {
        return this.mySid;
    }

    public String getMessage() {
        return "Unknown symbol text for $" + this.mySid;
    }
}

