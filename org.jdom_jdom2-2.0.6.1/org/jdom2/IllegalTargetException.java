/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

public class IllegalTargetException
extends IllegalArgumentException {
    private static final long serialVersionUID = 200L;

    IllegalTargetException(String target, String reason) {
        super("The target \"" + target + "\" is not legal for JDOM/XML Processing Instructions: " + reason + ".");
    }

    public IllegalTargetException(String reason) {
        super(reason);
    }
}

