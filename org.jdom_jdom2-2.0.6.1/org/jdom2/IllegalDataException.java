/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

public class IllegalDataException
extends IllegalArgumentException {
    private static final long serialVersionUID = 200L;

    IllegalDataException(String data, String construct, String reason) {
        super("The data \"" + data + "\" is not legal for a JDOM " + construct + ": " + reason + ".");
    }

    IllegalDataException(String data, String construct) {
        super("The data \"" + data + "\" is not legal for a JDOM " + construct + ".");
    }

    public IllegalDataException(String reason) {
        super(reason);
    }
}

