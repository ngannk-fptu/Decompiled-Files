/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

public class UnregisteredOutputFormatException
extends Exception {
    public UnregisteredOutputFormatException(String message) {
        this(message, null);
    }

    public UnregisteredOutputFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}

