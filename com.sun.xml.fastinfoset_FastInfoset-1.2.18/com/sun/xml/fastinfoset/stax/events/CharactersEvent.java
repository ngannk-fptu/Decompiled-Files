/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.fastinfoset.stax.events.EventBase;
import com.sun.xml.fastinfoset.stax.events.Util;
import javax.xml.stream.events.Characters;

public class CharactersEvent
extends EventBase
implements Characters {
    private String _text;
    private boolean isCData = false;
    private boolean isSpace = false;
    private boolean isIgnorable = false;
    private boolean needtoCheck = true;

    public CharactersEvent() {
        super(4);
    }

    public CharactersEvent(String data) {
        super(4);
        this._text = data;
    }

    public CharactersEvent(String data, boolean isCData) {
        super(4);
        this._text = data;
        this.isCData = isCData;
    }

    @Override
    public String getData() {
        return this._text;
    }

    public void setData(String data) {
        this._text = data;
    }

    @Override
    public boolean isCData() {
        return this.isCData;
    }

    public String toString() {
        if (this.isCData) {
            return "<![CDATA[" + this._text + "]]>";
        }
        return this._text;
    }

    @Override
    public boolean isIgnorableWhiteSpace() {
        return this.isIgnorable;
    }

    @Override
    public boolean isWhiteSpace() {
        if (this.needtoCheck) {
            this.checkWhiteSpace();
            this.needtoCheck = false;
        }
        return this.isSpace;
    }

    public void setSpace(boolean isSpace) {
        this.isSpace = isSpace;
        this.needtoCheck = false;
    }

    public void setIgnorable(boolean isIgnorable) {
        this.isIgnorable = isIgnorable;
        this.setEventType(6);
    }

    private void checkWhiteSpace() {
        if (!Util.isEmptyString(this._text)) {
            this.isSpace = true;
            for (int i = 0; i < this._text.length(); ++i) {
                if (XMLChar.isSpace(this._text.charAt(i))) continue;
                this.isSpace = false;
                break;
            }
        }
    }
}

