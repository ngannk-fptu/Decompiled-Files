/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import software.amazon.ion.SymbolToken;
import software.amazon.ion.UnknownSymbolException;
import software.amazon.ion.util.IonTextUtils;

final class SymbolTokenImpl
implements SymbolToken {
    private final String myText;
    private final int mySid;

    SymbolTokenImpl(String text, int sid) {
        assert (text != null || sid > 0) : "Neither text nor sid is defined";
        this.myText = text;
        this.mySid = sid;
    }

    SymbolTokenImpl(int sid) {
        assert (sid > 0) : "sid is undefined";
        this.myText = null;
        this.mySid = sid;
    }

    public String getText() {
        return this.myText;
    }

    public String assumeText() {
        if (this.myText == null) {
            throw new UnknownSymbolException(this.mySid);
        }
        return this.myText;
    }

    public int getSid() {
        return this.mySid;
    }

    public String toString() {
        String text = this.myText == null ? null : IonTextUtils.printString(this.myText);
        return "SymbolToken::{text:" + text + ",id:" + this.mySid + "}";
    }
}

