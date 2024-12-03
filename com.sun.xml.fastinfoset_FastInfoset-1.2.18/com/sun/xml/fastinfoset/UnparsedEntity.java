/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset;

import com.sun.xml.fastinfoset.Notation;

public class UnparsedEntity
extends Notation {
    public final String notationName;

    public UnparsedEntity(String _name, String _systemIdentifier, String _publicIdentifier, String _notationName) {
        super(_name, _systemIdentifier, _publicIdentifier);
        this.notationName = _notationName;
    }
}

