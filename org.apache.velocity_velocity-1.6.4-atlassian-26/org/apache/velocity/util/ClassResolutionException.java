/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util;

import org.apache.velocity.util.ClassConstructionException;

public class ClassResolutionException
extends ClassConstructionException {
    public ClassResolutionException(String message) {
        super(message);
    }

    public ClassResolutionException(Throwable cause) {
        super(cause);
    }

    public ClassResolutionException(String message, ClassNotFoundException cause) {
        super(message, cause);
    }
}

