/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.stream.Location;
import javax.xml.stream.events.Comment;

public class CommentEvent
extends AbstractXMLEvent
implements Comment {
    protected String text;

    public CommentEvent(String text) {
        this.text = text;
    }

    public CommentEvent(String text, Location location) {
        super(location);
        this.text = text;
    }

    public CommentEvent(Comment that) {
        super(that);
        this.text = that.getText();
    }

    public String getText() {
        return this.text;
    }

    public int getEventType() {
        return 5;
    }
}

