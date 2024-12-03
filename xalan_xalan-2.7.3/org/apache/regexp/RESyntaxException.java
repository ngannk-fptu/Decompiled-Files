/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

public class RESyntaxException
extends Exception {
    public RESyntaxException(String string) {
        super("Syntax error: " + string);
    }
}

