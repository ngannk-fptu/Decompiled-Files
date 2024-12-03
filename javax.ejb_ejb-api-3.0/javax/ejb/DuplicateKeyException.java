/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.CreateException;

public class DuplicateKeyException
extends CreateException {
    public DuplicateKeyException() {
    }

    public DuplicateKeyException(String message) {
        super(message);
    }
}

