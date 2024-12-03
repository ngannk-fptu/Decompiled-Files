/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

public class IllegalNameException
extends IllegalArgumentException {
    private static final long serialVersionUID = 200L;

    IllegalNameException(String name, String construct, String reason) {
        super("The name \"" + name + "\" is not legal for JDOM/XML " + construct + "s: " + reason + ".");
    }

    IllegalNameException(String name, String construct) {
        super("The name \"" + name + "\" is not legal for JDOM/XML " + construct + "s.");
    }

    public IllegalNameException(String reason) {
        super(reason);
    }
}

