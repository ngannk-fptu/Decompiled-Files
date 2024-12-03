/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset;

public class Notation {
    public final String name;
    public final String systemIdentifier;
    public final String publicIdentifier;

    public Notation(String _name, String _systemIdentifier, String _publicIdentifier) {
        this.name = _name;
        this.systemIdentifier = _systemIdentifier;
        this.publicIdentifier = _publicIdentifier;
    }
}

