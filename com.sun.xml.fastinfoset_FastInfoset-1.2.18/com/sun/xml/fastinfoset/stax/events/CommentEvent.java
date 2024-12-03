/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.stax.events.EventBase;
import javax.xml.stream.events.Comment;

public class CommentEvent
extends EventBase
implements Comment {
    private String _text;

    public CommentEvent() {
        super(5);
    }

    public CommentEvent(String text) {
        this();
        this._text = text;
    }

    public String toString() {
        return "<!--" + this._text + "-->";
    }

    @Override
    public String getText() {
        return this._text;
    }

    public void setText(String text) {
        this._text = text;
    }
}

