/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import javax.ejb.FinderException;

public class ObjectNotFoundException
extends FinderException {
    public ObjectNotFoundException() {
    }

    public ObjectNotFoundException(String message) {
        super(message);
    }
}

