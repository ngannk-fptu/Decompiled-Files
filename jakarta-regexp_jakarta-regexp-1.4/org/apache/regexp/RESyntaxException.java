/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

public class RESyntaxException
extends RuntimeException {
    public RESyntaxException(String string) {
        super("Syntax error: " + string);
    }
}

